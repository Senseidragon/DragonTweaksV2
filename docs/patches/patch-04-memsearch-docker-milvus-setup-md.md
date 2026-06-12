# Patch 04 -- .memsearch/memory/projects/dragontweaksv2/approved/memsearch-docker-milvus-setup.md

## Status
BLOCKED -- Write(.memsearch/**/approved/**) is in the deny list.
Dragon must apply this change manually.

## Problem
Lines 41-44 document the wide-path reindex command under "Reindex (correct collection)":
    memsearch index "C:/Users/sense/Desktop/DragonTweaksV2/.memsearch/memory/" --force -c ms_dragontweaksv2_4403422f

This is recalled as an authoritative operational fact and will be followed by
Claude Code in recovery scenarios, re-polluting the collection with candidates/,
deprecated/, rejected/, and raw/ content.

## Change: Replace the "Reindex (correct collection)" section (lines 40-44)

### Before
```
## Reindex (correct collection)

```
memsearch index "C:/Users/sense/Desktop/DragonTweaksV2/.memsearch/memory/" --force -c ms_dragontweaksv2_4403422f
```
```

### After
```
## Reindex (correct collection)

Run the guarded refresh script. Do NOT index .memsearch/memory/ directly:

```
.\scripts\memsearch-refresh.ps1
```

WARNING: Never run memsearch index against .memsearch/memory/ or any path
containing candidates/, deprecated/, rejected/, or raw/. Those subtrees
contain unvalidated memory and will pollute the collection.
```

## No other changes required
Restart, Manual flush, Verification search, TRAP sections, and Stale path
warning are all correct and unchanged.
