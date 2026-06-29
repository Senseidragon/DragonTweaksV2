"""
MCP PoC server — sandbox-only, bearer-auth, three tools.
Exposes /mcp as a JSON-RPC-over-HTTP endpoint compatible with the MCP protocol.

Known limitation: no transport-level request-size limiting is implemented.
Callers could send arbitrarily large HTTP bodies. Application-level content
limits (max_read_bytes / max_write_bytes) are enforced inside tool logic only.
"""

import json
import os
import pathlib
import ssl
import sys
from http.server import BaseHTTPRequestHandler, HTTPServer
from typing import Any

# ---------------------------------------------------------------------------
# Config loading
# ---------------------------------------------------------------------------

def load_config(path: str = "config.json") -> dict:
    with open(path, "r", encoding="utf-8") as f:
        raw = f.read()
    # Detect duplicate keys by using a custom object_pairs_hook
    seen: dict[str, int] = {}
    def check_dupes(pairs):
        d = {}
        for k, v in pairs:
            if k in seen:
                raise ValueError(f"Duplicate key in config: {k!r}")
            seen[k] = 1
            d[k] = v
        return d
    return json.loads(raw, object_pairs_hook=check_dupes)


CONFIG: dict = {}
SANDBOX_ROOT: pathlib.Path = pathlib.Path(".")

def init_config(path: str = "config.json"):
    global CONFIG, SANDBOX_ROOT
    CONFIG = load_config(path)
    SANDBOX_ROOT = pathlib.Path(CONFIG["sandbox_root"]).resolve()
    if not SANDBOX_ROOT.exists():
        SANDBOX_ROOT.mkdir(parents=True, exist_ok=True)

# ---------------------------------------------------------------------------
# Validation helpers
# ---------------------------------------------------------------------------

WILDCARD_CHARS = set("*?[]{}")


def _reject_wildcards(path: str) -> None:
    if any(c in path for c in WILDCARD_CHARS):
        raise ToolError(-32602, "Wildcard or pattern characters are not allowed in paths")


def _reject_absolute(path: str) -> None:
    # UNC paths checked first (// is also absolute under PurePosixPath)
    if path.startswith("\\\\") or path.startswith("//"):
        raise ToolError(-32602, "UNC paths are not allowed")
    # Windows drive letters: contains ':'
    if ":" in path:
        raise ToolError(-32602, "Drive-letter paths are not allowed")
    p = pathlib.PurePosixPath(path)
    if p.is_absolute():
        raise ToolError(-32602, "Absolute paths are not allowed")


def _reject_traversal(path: str) -> None:
    parts = pathlib.PurePosixPath(path).parts
    for part in parts:
        if part == "..":
            raise ToolError(-32602, "Parent traversal (..) is not allowed")


def _resolve_sandbox(rel: str) -> pathlib.Path:
    resolved = (SANDBOX_ROOT / rel).resolve()
    try:
        resolved.relative_to(SANDBOX_ROOT)
    except ValueError:
        raise ToolError(-32602, "Path escapes sandbox_root")
    return resolved


def _check_extension(path: str) -> None:
    allowed = CONFIG.get("allowed_extensions")
    if not allowed:
        return
    ext = pathlib.Path(path).suffix.lower()
    if ext not in [e.lower() for e in allowed]:
        raise ToolError(-32602, f"Extension {ext!r} is not in allowed_extensions")


def validate_path(path: str) -> pathlib.Path:
    _reject_wildcards(path)
    _reject_absolute(path)
    _reject_traversal(path)
    return _resolve_sandbox(path)


def validate_id(id_val: Any) -> None:
    if isinstance(id_val, bool):
        raise MCPError(-32600, "id must be a string or integer, not boolean")
    if id_val is None:
        raise MCPError(-32600, "id must be a string or integer, not null")
    if isinstance(id_val, list) or isinstance(id_val, dict):
        raise MCPError(-32600, "id must be a string or integer, not array/object")
    if isinstance(id_val, float):
        if not id_val.is_integer():
            raise MCPError(-32600, "id must be a whole number, not fractional")
    if isinstance(id_val, str) and len(id_val) > 256:
        raise MCPError(-32600, "id string is too long (max 256 chars)")


# ---------------------------------------------------------------------------
# Error types
# ---------------------------------------------------------------------------

class MCPError(Exception):
    def __init__(self, code: int, message: str):
        self.code = code
        self.message = message


class ToolError(MCPError):
    pass


# ---------------------------------------------------------------------------
# Tools
# ---------------------------------------------------------------------------

def tool_file_read(params: dict) -> dict:
    path_str = params.get("path")
    if path_str is None:
        raise ToolError(-32602, "Missing required argument: path")
    if not isinstance(path_str, str):
        raise ToolError(-32602, "path must be a string")

    resolved = validate_path(path_str)

    if not resolved.exists():
        raise ToolError(-32602, f"File not found: {path_str!r}")
    if not resolved.is_file():
        raise ToolError(-32602, f"Path is not a regular file: {path_str!r}")

    size = resolved.stat().st_size
    max_bytes = CONFIG.get("max_read_bytes", 524288)
    if size > max_bytes:
        raise ToolError(-32602, f"File size {size} exceeds max_read_bytes {max_bytes}")

    try:
        content = resolved.read_text(encoding="utf-8")
    except (UnicodeDecodeError, PermissionError) as e:
        raise ToolError(-32602, f"Cannot read file as text: {e}")

    return {"content": content, "size_bytes": size, "path": path_str}


def tool_file_write(params: dict) -> dict:
    path_str = params.get("path")
    content = params.get("content")
    if path_str is None:
        raise ToolError(-32602, "Missing required argument: path")
    if content is None:
        raise ToolError(-32602, "Missing required argument: content")
    if not isinstance(path_str, str):
        raise ToolError(-32602, "path must be a string")
    if not isinstance(content, str):
        raise ToolError(-32602, "content must be a string")

    _check_extension(path_str)
    resolved = validate_path(path_str)

    max_bytes = CONFIG.get("max_write_bytes", 524288)
    encoded = content.encode("utf-8")
    if len(encoded) > max_bytes:
        raise ToolError(-32602, f"Content size {len(encoded)} exceeds max_write_bytes {max_bytes}")

    if not resolved.parent.exists():
        raise ToolError(-32602, "Parent directory does not exist; will not create nested directories")

    resolved.write_text(content, encoding="utf-8")
    bytes_written = resolved.stat().st_size

    return {
        "success": True,
        "path": path_str,
        "bytes_written": bytes_written,
    }


def tool_list_files(params: dict) -> dict:
    path_str = params.get("path")

    if path_str is None or path_str == "":
        target = SANDBOX_ROOT
        rel_base = pathlib.Path(".")
    else:
        if not isinstance(path_str, str):
            raise ToolError(-32602, "path must be a string")
        target = validate_path(path_str)
        rel_base = pathlib.Path(path_str)

    if not target.exists():
        raise ToolError(-32602, f"Path not found: {path_str!r}")
    if not target.is_dir():
        raise ToolError(-32602, f"Path is not a directory: {path_str!r}")

    entries = []
    for child in sorted(target.iterdir()):
        rel = rel_base / child.name
        entry: dict[str, Any] = {
            "name": child.name,
            "relative_path": rel.as_posix(),
            "type": "file" if child.is_file() else "directory",
        }
        if child.is_file():
            entry["size_bytes"] = child.stat().st_size
        entries.append(entry)

    return {"entries": entries, "count": len(entries)}


# ---------------------------------------------------------------------------
# Tool registry
# ---------------------------------------------------------------------------

TOOLS = {
    "file_read": {
        "name": "file_read",
        "description": "Read a file from the sandbox.",
        "inputSchema": {
            "type": "object",
            "properties": {
                "path": {"type": "string", "description": "Sandbox-relative path to read"}
            },
            "required": ["path"]
        },
        "fn": tool_file_read,
    },
    "file_write": {
        "name": "file_write",
        "description": "Write or overwrite a file in the sandbox.",
        "inputSchema": {
            "type": "object",
            "properties": {
                "path": {"type": "string", "description": "Sandbox-relative path to write"},
                "content": {"type": "string", "description": "Text content to write"}
            },
            "required": ["path", "content"]
        },
        "fn": tool_file_write,
    },
    "list_files": {
        "name": "list_files",
        "description": "List immediate children of a sandbox directory.",
        "inputSchema": {
            "type": "object",
            "properties": {
                "path": {"type": "string", "description": "Sandbox-relative directory path (omit for root)"}
            },
            "required": []
        },
        "fn": tool_list_files,
    },
}


# ---------------------------------------------------------------------------
# JSON-RPC / MCP dispatch
# ---------------------------------------------------------------------------

def make_response(id_val: Any, result: Any) -> dict:
    return {"jsonrpc": "2.0", "id": id_val, "result": result}


def make_error(id_val: Any, code: int, message: str) -> dict:
    return {"jsonrpc": "2.0", "id": id_val, "error": {"code": code, "message": message}}


def parse_body(raw: bytes) -> dict:
    try:
        text = raw.decode("utf-8")
    except UnicodeDecodeError:
        raise MCPError(-32700, "Request body is not valid UTF-8")

    seen_keys: list[str] = []
    def check_dupes(pairs):
        d = {}
        for k, v in pairs:
            if k in seen_keys:
                raise MCPError(-32700, f"Duplicate JSON key: {k!r}")
            seen_keys.append(k)
            d[k] = v
        return d

    try:
        return json.loads(text, object_pairs_hook=check_dupes)
    except MCPError:
        raise
    except json.JSONDecodeError as e:
        raise MCPError(-32700, f"Parse error: {e}")


def dispatch(body: dict) -> dict:
    id_val = body.get("id", None)

    # Validate id
    if "id" in body:
        try:
            validate_id(id_val)
        except MCPError as e:
            return make_error(None, e.code, e.message)

    # Validate jsonrpc field
    if body.get("jsonrpc") != "2.0":
        return make_error(id_val, -32600, "jsonrpc field must be exactly '2.0'")

    method = body.get("method")
    params = body.get("params", {})

    if not isinstance(params, dict):
        return make_error(id_val, -32602, "params must be an object")

    if method == "tools/list":
        tool_list = [
            {"name": t["name"], "description": t["description"], "inputSchema": t["inputSchema"]}
            for t in TOOLS.values()
        ]
        return make_response(id_val, {"tools": tool_list})

    if method == "tools/call":
        tool_name = params.get("name")
        tool_args = params.get("arguments", {})

        if not isinstance(tool_name, str) or not tool_name:
            return make_error(id_val, -32602, "params.name must be a non-empty string")
        if not isinstance(tool_args, dict):
            return make_error(id_val, -32602, "params.arguments must be an object")

        tool = TOOLS.get(tool_name)
        if tool is None:
            return make_error(id_val, -32602, f"Unknown tool: {tool_name!r}")

        try:
            result = tool["fn"](tool_args)
        except ToolError as e:
            return make_error(id_val, e.code, e.message)

        return make_response(id_val, {"content": [{"type": "text", "text": json.dumps(result)}]})

    if method in ("initialize", "ping"):
        # Minimal lifecycle support
        return make_response(id_val, {})

    return make_error(id_val, -32601, f"Method not found: {method!r}. Use tools/list or tools/call.")


# ---------------------------------------------------------------------------
# HTTP handler
# ---------------------------------------------------------------------------

class MCPHandler(BaseHTTPRequestHandler):
    def log_message(self, format, *args):
        print(f"[MCP] {self.address_string()} {format % args}", file=sys.stderr)

    def _send_json(self, status: int, body: dict) -> None:
        data = json.dumps(body).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(data)))
        self.end_headers()
        self.wfile.write(data)

    def _check_auth(self) -> bool:
        expected = CONFIG.get("bearer_token", "")
        auth = self.headers.get("Authorization", "")
        if not auth.startswith("Bearer "):
            return False
        token = auth[len("Bearer "):]
        return token == expected

    def do_POST(self):
        if self.path != "/mcp":
            self.send_response(404)
            self.end_headers()
            return

        if not self._check_auth():
            self._send_json(401, {"error": "Unauthorized"})
            return

        length = int(self.headers.get("Content-Length", 0))
        raw = self.rfile.read(length)

        try:
            body = parse_body(raw)
        except MCPError as e:
            self._send_json(400, make_error(None, e.code, e.message))
            return

        response = dispatch(body)
        self._send_json(200, response)

    def do_GET(self):
        if self.path != "/mcp":
            self.send_response(404)
            self.end_headers()
            return
        # Return MCP server info without requiring auth (safe — no tools exposed)
        self._send_json(200, {
            "name": "DragonTweaksV2 MCP PoC",
            "version": "0.1.0",
            "protocol": "mcp",
            "endpoint": "/mcp",
        })


# ---------------------------------------------------------------------------
# TLS config validation (extracted so tests can call it without a live socket)
# ---------------------------------------------------------------------------

def _validate_tls_config(config: dict) -> None:
    """Raise ValueError if use_tls is true but cert/key config is incomplete or files missing."""
    if not config.get("use_tls", False):
        return
    cert_file = config.get("cert_file", "")
    key_file = config.get("key_file", "")
    if not cert_file:
        raise ValueError("use_tls is true but cert_file is empty or missing")
    if not key_file:
        raise ValueError("use_tls is true but key_file is empty or missing")
    if not pathlib.Path(cert_file).exists():
        raise ValueError(f"cert_file not found: {cert_file}")
    if not pathlib.Path(key_file).exists():
        raise ValueError(f"key_file not found: {key_file}")


# ---------------------------------------------------------------------------
# Entry point
# ---------------------------------------------------------------------------

def main():
    config_path = sys.argv[1] if len(sys.argv) > 1 else "config.json"
    try:
        init_config(config_path)
    except FileNotFoundError:
        print(f"ERROR: Config file not found: {config_path}", file=sys.stderr)
        print("Copy config.example.json to config.json and fill in your values.", file=sys.stderr)
        sys.exit(1)
    except ValueError as e:
        print(f"ERROR: Config error: {e}", file=sys.stderr)
        sys.exit(1)

    host = CONFIG.get("host", "0.0.0.0")
    port = CONFIG.get("port", 8000)

    try:
        _validate_tls_config(CONFIG)
    except ValueError as e:
        print(f"ERROR: TLS config error: {e}", file=sys.stderr)
        sys.exit(1)

    use_tls = CONFIG.get("use_tls", False)
    scheme = "https" if use_tls else "http"

    print(f"MCP PoC server starting on {host}:{port} ({'HTTPS' if use_tls else 'HTTP'})", file=sys.stderr)
    print(f"Sandbox root: {SANDBOX_ROOT}", file=sys.stderr)
    print(f"Endpoint: {scheme}://{host}:{port}/mcp", file=sys.stderr)

    server = HTTPServer((host, port), MCPHandler)

    if use_tls:
        ctx = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)
        ctx.load_cert_chain(
            certfile=CONFIG["cert_file"],
            keyfile=CONFIG["key_file"],
        )
        server.socket = ctx.wrap_socket(server.socket, server_side=True)
        print("TLS enabled — certificate loaded.", file=sys.stderr)

    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("Server stopped.", file=sys.stderr)


if __name__ == "__main__":
    main()
