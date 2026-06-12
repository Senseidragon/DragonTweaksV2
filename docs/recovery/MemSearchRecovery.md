# MemSearch Recovery Notes

## Current Working Setup

MemSearch is operational on Windows using Docker-hosted Milvus Server.

The working setup uses Docker Compose with three containers:

- milvus-etcd
- milvus-minio
- milvus-standalone

The single-container Milvus approach failed and should not be used for this setup.

## Docker Compose File Location

```text
C:\Users\sense\.memsearch\milvus-docker\docker-compose.yml
```

## Restart Milvus After Reboot

Run:

```bash
cd ~/.memsearch/milvus-docker
docker compose up -d
```

## Verify MemSearch Configuration

Run:

```bash
memsearch config list --resolved
```

Expected important values:

```text
milvus.uri = "http://localhost:19530"
embedding.provider = "onnx"
```

## Verify Indexed Chunk Count

Run:

```bash
memsearch stats
```

## Claude Code Plugin Collection

Claude Code's MemSearch plugin uses a project-specific collection distinct from the CLI default (`memsearch_chunks`):

```text
ms_dragontweaksv2_4403422f
```

All reindex and search commands must target this collection explicitly using `-c`. The CLI default collection is never searched by the Claude Code plugin.

## Reindex Project Memory

Run the guarded refresh script. It starts Milvus, indexes only approved memory
paths, flushes the collection, and runs a smoke-test search:

```powershell
.\scripts\memsearch-refresh.ps1
```

WARNING: Do NOT run memsearch index against .memsearch/memory/ directly.
That path covers the entire memory tree including candidates/, deprecated/,
rejected/, and raw/ subtrees and will pollute the collection with unvalidated
memory.

Prohibited paths -- never pass to memsearch index:
  - .memsearch/memory/           (entire tree -- indexes everything)
  - any path containing candidates/
  - any path containing deprecated/
  - any path containing rejected/
  - any path containing raw/

If you need to verify row count after the refresh script runs, flush manually:

```bash
python -c "from pymilvus import MilvusClient; c=MilvusClient(uri='http://localhost:19530'); c.flush('ms_dragontweaksv2_4403422f'); print(c.get_collection_stats('ms_dragontweaksv2_4403422f'))"
```

Expected output: `{'row_count': N}` where N > 0.

## Test Search

Run:

```bash
memsearch search "test memory recall" --top-k 5 -c ms_dragontweaksv2_4403422f
```

## Notes

If `memsearch stats` reports `Total indexed chunks: 0`, this is expected — `stats` queries the CLI default collection (`memsearch_chunks`), not the Claude Code plugin collection. Use the pymilvus flush+stats command above to verify the plugin collection directly.

memsearch does not call `flush()` after upsert. Data written by `memsearch index` sits in Milvus growing segments and is invisible to search until explicitly flushed.

## Daily Refresh Shortcut

```powershell
.\scripts\memsearch-refresh.ps1
```

This script starts Docker Milvus, reindexes project memory into the Claude Code plugin collection, flushes Milvus, and runs a smoke-test search.
