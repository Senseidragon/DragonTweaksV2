# Patch 03 -- docs/recovery/MemSearchRecovery.md

## Status
NOT blocked by deny rules. Dragon may apply directly or review first.

## Problem
Line 68 documents the wide-path command:
    memsearch index "C:/Users/sense/Desktop/DragonTweaksV2/.memsearch/memory/" --force -c ms_dragontweaksv2_4403422f

Following this re-pollutes the collection with candidates/, deprecated/,
rejected/, and raw/ content. Written before the guarded refresh script existed.

## Change: Replace the "Reindex Project Memory" section (lines 63-77)

### Before
```
## Reindex Project Memory

Run:

    memsearch index "C:/Users/sense/Desktop/DragonTweaksV2/.memsearch/memory/" --force -c ms_dragontweaksv2_4403422f

After indexing, flush immediately -- memsearch does not flush automatically,
so rows stay invisible to stats and search until flushed:

    python -c "from pymilvus import MilvusClient; c=MilvusClient(uri='http://localhost:19530'); c.flush('ms_dragontweaksv2_4403422f'); print(c.get_collection_stats('ms_dragontweaksv2_4403422f'))"

Expected output: `{'row_count': N}` where N > 0.
```

### After
```
## Reindex Project Memory

Run the guarded refresh script. It starts Milvus, indexes only approved memory
paths, flushes the collection, and runs a smoke-test search:

    .\scripts\memsearch-refresh.ps1

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

    python -c "from pymilvus import MilvusClient; c=MilvusClient(uri='http://localhost:19530'); c.flush('ms_dragontweaksv2_4403422f'); print(c.get_collection_stats('ms_dragontweaksv2_4403422f'))"

Expected output: `{'row_count': N}` where N > 0.
```

## No other changes required
Lines 80-100 (Test Search, Notes, Daily Refresh Shortcut) already reference
.\scripts\memsearch-refresh.ps1 correctly.
