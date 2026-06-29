"""
Local validation tests for the MCP PoC server.
Runs entirely in-process — no network access required.
"""

import json
import os
import pathlib
import sys
import tempfile

# Point imports at the server module in the same directory
sys.path.insert(0, str(pathlib.Path(__file__).parent))

import server as srv

PASS = "\033[32mPASS\033[0m"
FAIL = "\033[31mFAIL\033[0m"

results: list[tuple[str, bool, str]] = []


def check(name: str, condition: bool, detail: str = "") -> None:
    results.append((name, condition, detail))
    status = PASS if condition else FAIL
    print(f"  [{status}] {name}" + (f" — {detail}" if detail else ""))


def expect_tool_error(fn, params: dict, fragment: str) -> None:
    try:
        fn(params)
        check(f"expected ToolError containing '{fragment}'", False, "no exception raised")
    except srv.ToolError as e:
        check(f"expected ToolError containing '{fragment}'", fragment.lower() in e.message.lower(), e.message)
    except Exception as e:
        check(f"expected ToolError containing '{fragment}'", False, repr(e))


def run_all():
    with tempfile.TemporaryDirectory() as tmpdir:
        srv.CONFIG = {
            "host": "127.0.0.1",
            "port": 8000,
            "sandbox_root": tmpdir,
            "bearer_token": "test-token",
            "max_read_bytes": 64,
            "max_write_bytes": 64,
            "allowed_extensions": [".txt", ".md"],
        }
        srv.SANDBOX_ROOT = pathlib.Path(tmpdir).resolve()

        sandbox = srv.SANDBOX_ROOT

        print("\n--- Path validation ---")

        # Absolute path rejection
        expect_tool_error(srv.tool_file_read, {"path": "/etc/passwd"}, "Absolute")
        expect_tool_error(srv.tool_file_write, {"path": "/tmp/evil.txt", "content": "x"}, "Absolute")
        expect_tool_error(srv.tool_list_files, {"path": "/tmp"}, "Absolute")

        # Drive-letter paths
        expect_tool_error(srv.tool_file_read, {"path": "C:/Windows/system.ini"}, "Drive-letter")
        expect_tool_error(srv.tool_file_write, {"path": "C:/evil.txt", "content": "x"}, "Drive-letter")

        # UNC paths
        expect_tool_error(srv.tool_file_read, {"path": "//server/share/file.txt"}, "UNC")

        # Parent traversal
        expect_tool_error(srv.tool_file_read, {"path": "../outside.txt"}, "traversal")
        expect_tool_error(srv.tool_file_write, {"path": "../outside.txt", "content": "x"}, "traversal")
        expect_tool_error(srv.tool_list_files, {"path": "../"}, "traversal")

        print("\n--- Wildcard rejection ---")

        for wc in ["*.txt", "file?.txt", "file[0].txt", "file{a,b}.txt"]:
            expect_tool_error(srv.tool_file_read, {"path": wc}, "Wildcard")
            expect_tool_error(srv.tool_file_write, {"path": wc, "content": "x"}, "Wildcard")

        print("\n--- Directory read rejection ---")

        subdir = sandbox / "subdir"
        subdir.mkdir()
        expect_tool_error(srv.tool_file_read, {"path": "subdir"}, "not a regular file")

        print("\n--- Missing file read rejection ---")

        expect_tool_error(srv.tool_file_read, {"path": "nonexistent.txt"}, "not found")

        print("\n--- Disallowed extension rejection ---")

        expect_tool_error(srv.tool_file_write, {"path": "evil.exe", "content": "x"}, "not in allowed_extensions")
        expect_tool_error(srv.tool_file_write, {"path": "data.bin", "content": "x"}, "not in allowed_extensions")

        print("\n--- Max write size rejection ---")

        big = "x" * 65  # > 64 bytes
        expect_tool_error(srv.tool_file_write, {"path": "big.txt", "content": big}, "max_write_bytes")

        print("\n--- Write blocked when parent does not exist ---")

        expect_tool_error(
            srv.tool_file_write,
            {"path": "nosuchdir/file.txt", "content": "hello"},
            "Parent directory does not exist",
        )

        print("\n--- Write overwrite behavior ---")

        target = "overwrite_test.txt"
        result1 = srv.tool_file_write({"path": target, "content": "version1"})
        check("initial write succeeds", result1["success"] is True)
        result2 = srv.tool_file_write({"path": target, "content": "version2"})
        check("overwrite write succeeds", result2["success"] is True)
        read_back = srv.tool_file_read({"path": target})
        check("overwrite content is version2", read_back["content"] == "version2", repr(read_back["content"]))

        print("\n--- Max read size rejection ---")

        big_file = sandbox / "bigfile.txt"
        big_file.write_text("y" * 65, encoding="utf-8")
        expect_tool_error(srv.tool_file_read, {"path": "bigfile.txt"}, "max_read_bytes")

        print("\n--- list_files ---")

        result = srv.tool_list_files({})
        names = {e["name"] for e in result["entries"]}
        check("list_files includes subdir", "subdir" in names)
        check("list_files includes overwrite_test.txt", "overwrite_test.txt" in names)
        check("list_files includes bigfile.txt", "bigfile.txt" in names)

        print("\n--- Authorization check (HTTP layer) ---")

        # Simulate auth check directly against CONFIG
        def fake_auth(header_value: str) -> bool:
            expected = srv.CONFIG.get("bearer_token", "")
            if not header_value.startswith("Bearer "):
                return False
            return header_value[len("Bearer "):] == expected

        check("correct token accepted", fake_auth("Bearer test-token") is True)
        check("wrong token rejected", fake_auth("Bearer wrong-token") is False)
        check("missing Bearer prefix rejected", fake_auth("test-token") is False)
        check("empty header rejected", fake_auth("") is False)

        print("\n--- Duplicate JSON key rejection ---")

        raw_dupes = b'{"jsonrpc":"2.0","id":1,"method":"tools/list","id":2}'
        try:
            srv.parse_body(raw_dupes)
            check("duplicate JSON key raises MCPError", False, "no exception")
        except srv.MCPError as e:
            check("duplicate JSON key raises MCPError", "Duplicate" in e.message or "duplicate" in e.message, e.message)

        print("\n--- JSON-RPC id validation ---")

        # null id
        try:
            srv.validate_id(None)
            check("null id raises MCPError", False)
        except srv.MCPError:
            check("null id raises MCPError", True)

        # boolean id
        try:
            srv.validate_id(True)
            check("boolean id raises MCPError", False)
        except srv.MCPError:
            check("boolean id raises MCPError", True)

        # object id
        try:
            srv.validate_id({"x": 1})
            check("object id raises MCPError", False)
        except srv.MCPError:
            check("object id raises MCPError", True)

        # fractional id
        try:
            srv.validate_id(1.5)
            check("fractional float id raises MCPError", False)
        except srv.MCPError:
            check("fractional float id raises MCPError", True)

        # valid string id
        try:
            srv.validate_id("abc")
            check("valid string id accepted", True)
        except srv.MCPError as e:
            check("valid string id accepted", False, e.message)

        # valid integer id
        try:
            srv.validate_id(42)
            check("valid integer id accepted", True)
        except srv.MCPError as e:
            check("valid integer id accepted", False, e.message)

        print("\n--- tools/list dispatch ---")

        srv.init_config.__module__  # ensure module loaded
        # Inject a fake body directly
        body = {"jsonrpc": "2.0", "id": 1, "method": "tools/list"}
        resp = srv.dispatch(body)
        tool_names = {t["name"] for t in resp["result"]["tools"]}
        check("tools/list returns file_read", "file_read" in tool_names)
        check("tools/list returns file_write", "file_write" in tool_names)
        check("tools/list returns list_files", "list_files" in tool_names)
        check("tools/list returns exactly 3 tools", len(tool_names) == 3, str(tool_names))

        print("\n--- tools/call unknown tool ---")

        body2 = {"jsonrpc": "2.0", "id": 2, "method": "tools/call", "params": {"name": "delete_everything", "arguments": {}}}
        resp2 = srv.dispatch(body2)
        check("unknown tool returns error", "error" in resp2, str(resp2))

        print("\n--- Unknown method ---")

        body3 = {"jsonrpc": "2.0", "id": 3, "method": "evil/method"}
        resp3 = srv.dispatch(body3)
        check("unknown method returns Method not found error", "error" in resp3)

        print("\n--- TLS config validation (no sockets opened) ---")

        # use_tls false: cert_file/key_file not required
        try:
            srv._validate_tls_config({"use_tls": False})
            check("use_tls false accepts missing cert_file/key_file", True)
        except ValueError as e:
            check("use_tls false accepts missing cert_file/key_file", False, str(e))

        # use_tls false with empty strings: also fine
        try:
            srv._validate_tls_config({"use_tls": False, "cert_file": "", "key_file": ""})
            check("use_tls false with empty cert/key accepted", True)
        except ValueError as e:
            check("use_tls false with empty cert/key accepted", False, str(e))

        # use_tls true with empty cert_file
        try:
            srv._validate_tls_config({"use_tls": True, "cert_file": "", "key_file": "key.pem"})
            check("use_tls true rejects empty cert_file", False, "no error raised")
        except ValueError as e:
            check("use_tls true rejects empty cert_file", "cert_file" in str(e), str(e))

        # use_tls true with empty key_file
        try:
            srv._validate_tls_config({"use_tls": True, "cert_file": "cert.pem", "key_file": ""})
            check("use_tls true rejects empty key_file", False, "no error raised")
        except ValueError as e:
            check("use_tls true rejects empty key_file", "key_file" in str(e), str(e))

        # use_tls true with non-existent cert_file
        try:
            srv._validate_tls_config({"use_tls": True, "cert_file": "/nonexistent/cert.pem", "key_file": "/nonexistent/key.pem"})
            check("use_tls true rejects missing cert_file", False, "no error raised")
        except ValueError as e:
            check("use_tls true rejects missing cert_file", "cert_file" in str(e) or "not found" in str(e), str(e))

        # use_tls true with existent cert but non-existent key
        with tempfile.NamedTemporaryFile(suffix=".pem", delete=False) as tf:
            fake_cert = tf.name
        try:
            try:
                srv._validate_tls_config({"use_tls": True, "cert_file": fake_cert, "key_file": "/nonexistent/key.pem"})
                check("use_tls true rejects missing key_file", False, "no error raised")
            except ValueError as e:
                check("use_tls true rejects missing key_file", "key_file" in str(e) or "not found" in str(e), str(e))
        finally:
            os.unlink(fake_cert)

    # Summary
    total = len(results)
    passed = sum(1 for _, ok, _ in results if ok)
    print(f"\n=== Results: {passed}/{total} passed ===")
    if passed < total:
        sys.exit(1)


if __name__ == "__main__":
    run_all()
