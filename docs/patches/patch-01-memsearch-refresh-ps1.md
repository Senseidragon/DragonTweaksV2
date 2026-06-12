# Patch 01 -- scripts/memsearch-refresh.ps1

## Status
BLOCKED -- Write(scripts/memsearch-refresh.ps1) is in the deny list.
Dragon must apply this change manually.

## Problem
The minecraft domain approved/ path is missing from the guarded index path list.
memsearch-refresh.ps1 is the enforcement boundary; if it does not list minecraft,
running it after a collection rebuild will not restore minecraft domain memory.

## Change
Add one line to the memsearch index command block (lines 39-46).

### Before (lines 39-46)
```powershell
memsearch index `
    "$memoryDir\seed-framework-rules.md" `
    "$memoryDir\2026-05-25.md" `
    "$memoryDir\framework\approved" `
    "$memoryDir\domains\neoforge\approved" `
    "$memoryDir\domains\minecolonies\approved" `
    "$memoryDir\projects\dragontweaksv2\approved" `
    --force -c $collection
```

### After (add minecraft line before projects line)
```powershell
memsearch index `
    "$memoryDir\seed-framework-rules.md" `
    "$memoryDir\2026-05-25.md" `
    "$memoryDir\framework\approved" `
    "$memoryDir\domains\neoforge\approved" `
    "$memoryDir\domains\minecolonies\approved" `
    "$memoryDir\domains\minecraft\approved" `
    "$memoryDir\projects\dragontweaksv2\approved" `
    --force -c $collection
```

## Notes
- This is the ONLY change required to memsearch-refresh.ps1.
- The existing INDEXING INVARIANT comment block (lines 17-37) already prohibits
  candidates/, deprecated/, rejected/, raw/ -- no changes needed there.
- After applying, run .\scripts\memsearch-refresh.ps1 to trigger a clean reindex.
