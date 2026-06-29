# MCP PoC Server

## Purpose

This is a minimal proof-of-concept remote MCP server. Its goal is to test
whether Claude AI can connect to a locally hosted MCP server exposed through
a public IP and port forwarding.

**What MCP problem it proves:** Can Claude AI's custom connector feature
reach and authenticate with a self-hosted, raw-IP MCP endpoint? This PoC
provides the simplest possible server to answer that question before building
anything more complex.

---

## Connectivity Model

```
Claude AI
   |
   | HTTPS POST https://151.213.207.11/mcp
   v
Router / Firewall (your network)
   |
   | Port forwards 443 → local server port (default 8000)
   v
server.py  (listens on 0.0.0.0:8000 by default)
```

**TLS note:** The public endpoint uses port 443. Whether Claude AI enforces
HTTPS/TLS, accepts a self-signed certificate, or rejects a raw-IP endpoint
entirely is **unknown until tested**. You may need a reverse proxy (e.g.,
nginx with a self-signed cert) between the public port and this server.

---

## Requirements

- Python 3.9 or later
- No external pip packages required (standard library only)

---

## Setup

### 1. Install dependencies

No pip install needed. Verify Python version:

```bash
python --version
```

### 2. Copy and edit config

```bash
cp config.example.json config.json
```

Edit `config.json`:

| Field | Description |
|---|---|
| `host` | Interface to listen on. `0.0.0.0` = all interfaces. |
| `port` | Local port (e.g. `8000`). Your router must forward public 443 to this. |
| `sandbox_root` | Directory where all file operations are confined. Must exist. |
| `bearer_token` | Secret string the caller must supply. See below. |
| `max_read_bytes` | Maximum file size the server will read (bytes). |
| `max_write_bytes` | Maximum content size the server will accept for writes (bytes). |
| `allowed_extensions` | List of permitted file extensions. Empty array = allow all. |
| `use_tls` | `false` = plain HTTP (default). `true` = HTTPS using cert/key below. |
| `cert_file` | Path to PEM certificate file. Required when `use_tls` is `true`. |
| `key_file` | Path to PEM private key file. Required when `use_tls` is `true`. |

### 3. Set sandbox_root

Create a directory that the server can safely read and write:

```bash
mkdir sandbox
```

Set `sandbox_root` in `config.json` to an absolute path or a relative path
from the directory where you run the server (e.g., `"./sandbox"`).

### 4. Set bearer_token

A bearer token is a shared secret password sent in the HTTP Authorization
header. Pick a long, random string and set it in `config.json`.

Example Authorization header the caller must send:

```
Authorization: Bearer your-secret-token-here
```

Requests without this exact header are rejected with HTTP 401 before any
tool list or tool call is processed.

---

## Running the server

```bash
python server.py config.json
```

The server prints startup info to stderr and listens on the configured host
and port.

---

## Exposed Tools

Exactly three tools are exposed. No others.

### `file_read(path)`

Read a sandbox-relative file and return its text content.

- `path` — required, string, sandbox-relative, no wildcards

### `file_write(path, content)`

Write or overwrite a file in the sandbox.

- `path` — required, string, sandbox-relative, must use an allowed extension
- `content` — required, string, must be within `max_write_bytes`

Returns `{ success, path, bytes_written }`.

### `list_files(path?)`

List immediate children of a sandbox directory.

- `path` — optional, string, sandbox-relative. Omit to list sandbox root.

Returns an array of `{ name, relative_path, type, size_bytes? }`.

---

## Validation Rules

All paths are validated before use:

- Absolute paths rejected (`/etc/passwd`)
- Drive-letter paths rejected (`C:\...`, `C:/...`)
- UNC paths rejected (`\\server\share`, `//server/share`)
- Parent traversal rejected (`../outside`)
- Wildcard characters rejected: `*  ?  [  ]  {  }`
- Final resolved path must remain inside `sandbox_root`
- Extension must be in `allowed_extensions` (if set) for writes
- File size must be within `max_read_bytes` for reads
- Content size must be within `max_write_bytes` for writes
- `file_write` will not create parent directories; parent must exist

---

## Claude AI Custom Connector Test Checklist

1. Start the MCP server: `python server.py config.json`
2. Confirm `sandbox_root` exists and is writable.
3. Confirm your router/firewall forwards public port 443 to local server port.
4. Register connector URL in Claude AI as `https://151.213.207.11/mcp`.
5. Configure the connector to send `Authorization: Bearer <your-token>`.
6. Test `list_files` — should return an empty or populated list.
7. Test `file_write` with a sandbox-relative path like `test.txt`.
8. Test `file_read` on the file you just wrote.

---

---

## Optional Direct HTTPS PoC Mode

The server can optionally terminate TLS directly using a local certificate and
key. This is a PoC testing mode only — **not a production configuration**.

> **WARNING:**
> - Claude AI may reject connections to a raw IP (`151.213.207.11`) even over HTTPS.
> - Claude AI may reject a self-signed certificate.
> - This test only proves what actually happens when attempted.
> - A CA-trusted certificate and a proper domain name may still be required.

### Required config fields for HTTPS mode

```json
{
  "use_tls": true,
  "cert_file": "/absolute/path/to/cert.pem",
  "key_file": "/absolute/path/to/key.pem"
}
```

Set `port` to `443` to receive forwarded traffic directly, or use a separate
local port and configure your router to forward public `443` to that port.

> **Binding to port 443 directly may require Administrator/root privileges.**
> On Windows, run the terminal as Administrator. On Linux/macOS, use `sudo` or
> grant the capability with `setcap cap_net_bind_service`.

### Generating a self-signed certificate (PoC only)

**OpenSSL (Linux / macOS / Git Bash on Windows):**

```bash
openssl req -x509 -newkey rsa:2048 -keyout key.pem -out cert.pem \
  -days 30 -nodes \
  -subj "/CN=151.213.207.11" \
  -addext "subjectAltName=IP:151.213.207.11"
```

**PowerShell (Windows):**

```powershell
# Creates a self-signed cert in the local certificate store, then exports it.
$cert = New-SelfSignedCertificate `
  -DnsName "151.213.207.11" `
  -CertStoreLocation "Cert:\LocalMachine\My" `
  -NotAfter (Get-Date).AddDays(30)

$pwd = ConvertTo-SecureString -String "changeme" -Force -AsPlainText
Export-PfxCertificate -Cert $cert -FilePath mcp-poc.pfx -Password $pwd

# Convert PFX to PEM (requires OpenSSL):
openssl pkcs12 -in mcp-poc.pfx -nocerts -nodes -out key.pem -passin pass:changeme
openssl pkcs12 -in mcp-poc.pfx -nokeys -out cert.pem -passin pass:changeme
```

### Example config snippet for HTTPS on a local port

```json
{
  "host": "0.0.0.0",
  "port": 8000,
  "use_tls": true,
  "cert_file": "./cert.pem",
  "key_file": "./key.pem",
  "sandbox_root": "./sandbox",
  "bearer_token": "REPLACE_WITH_STRONG_SECRET_TOKEN",
  "max_read_bytes": 524288,
  "max_write_bytes": 524288,
  "allowed_extensions": [".txt", ".md", ".json", ".csv", ".log"]
}
```

### Running the server in HTTPS mode

```bash
cd tools/mcp-poc
python server.py config.json
```

Startup output will confirm HTTPS mode:
```
MCP PoC server starting on 0.0.0.0:8000 (HTTPS)
Sandbox root: /path/to/sandbox
Endpoint: https://0.0.0.0:8000/mcp
TLS enabled — certificate loaded.
```

### Network path for Claude AI

```
Claude AI
   |
   | HTTPS POST https://151.213.207.11/mcp
   v
Router / Firewall (your network)
   |
   | Port forwards 443 → local server port (e.g. 8000)
   v
server.py with use_tls: true  (listens on configured host:port)
```

If you set `"port": 443` and bind with sufficient privileges, no port
forwarding is needed and the local server handles 443 directly.

### TLS test with curl (local verification before public forwarding)

```bash
# -k skips certificate verification (needed for self-signed certs)
curl -sk -X POST https://<HOST>:<PORT>/mcp \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{"jsonrpc":"2.0","id":1,"method":"tools/list"}'
```

> TLS socket wrapping is not tested by `validation_tests.py` (requires a live
> socket and valid cert files). Certificate loading must be verified manually
> by starting the server and issuing the curl command above.

---

## Security Assumptions

- The bearer token is a shared secret. Rotate it if exposed.
- The sandbox confines all reads and writes. The server never accesses anything outside `sandbox_root`.
- In plain HTTP mode (default), all traffic is unencrypted. Use `use_tls: true` or a reverse proxy to enable TLS before exposing publicly.
- There is no IP allowlist. Any caller with the correct token can call tools.

---

## Known Limitations

1. **TLS is optional**: Plain HTTP is the default. Direct HTTPS is available via `use_tls: true` with local cert/key files. A reverse proxy is still an option but no longer required for PoC testing.
2. **Self-signed cert risk**: Claude AI may reject a self-signed certificate. Whether it will connect to a raw-IP HTTPS endpoint at all is unknown until tested.
3. **No transport-level request-size limit**: HTTP body size is not capped at the transport layer. `max_read_bytes` and `max_write_bytes` limit application content after parsing.
4. **No rate limiting**: There is no request rate limit.
5. **No IP allowlist**: Any IP can attempt authentication.
6. **Raw-IP SNI**: Some HTTPS clients reject connections to bare IP addresses (no hostname/SNI). Claude AI connector behavior against `151.213.207.11` is unknown until tested.
7. **Single-threaded**: `http.server.HTTPServer` is single-threaded. It handles one request at a time.
8. **No binary file support**: `file_read` returns UTF-8 text only.
9. **No delete tool**: Intentionally omitted.
10. **TLS socket wrap not covered by automated tests**: `validation_tests.py` tests config field validation only. Live TLS socket behavior requires manual verification with a real cert.

---

## WARNING

> This is a proof-of-concept. Do not expose this server long-term without:
> - Terminating TLS with a valid certificate at your reverse proxy
> - Rotating the bearer token regularly
> - Restricting inbound connections to Claude AI's IP ranges only
> - Implementing transport-level request-size limits in your reverse proxy
> - Reviewing and hardening firewall rules
