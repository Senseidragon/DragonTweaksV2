# Patch 02 -- scripts/acceptance-tests.ps1

## Status
NOT blocked by deny rules. Dragon may apply directly or review first.

## Problem
Line 23 runs:
    memsearch index $memoryDir --force -c $collection --milvus-uri $milvusUri

$memoryDir resolves to .memsearch\memory (the entire tree). This indexes
candidates/, deprecated/, rejected/, and raw/ into the live collection on
every test run, re-polluting it after any cleanup.

## Why not duplicate the path list here
Two copies of the approved-path list create a drifting source of truth.
When a new domain is added, both files need updating -- the missed one
silently re-pollutes or fails tests. Delegating to memsearch-refresh.ps1
keeps one enforcement boundary.

## Change summary
- Remove $memoryDir variable (line 14) -- no longer used
- Remove "Memory dir" display line (line 18)
- Replace preflight index block (lines 21-24) with a call to memsearch-refresh.ps1

## Full replacement for lines 1-25

```powershell
#Requires -Version 5.1
<#
.SYNOPSIS
    Retrieval acceptance tests for the memsearch-indexed framework rules.
    Verifies that 11 required chunks are retrievable via keyword search.
#>

$ErrorActionPreference = "Stop"

$collection = "ms_dragontweaksv2_4403422f"
$milvusUri  = "http://localhost:19530"
$scriptDir  = Split-Path -Parent $MyInvocation.MyCommand.Path
$repoRoot   = Split-Path -Parent $scriptDir

Write-Host "=== Retrieval Acceptance Tests ==="
Write-Host "Collection : $collection"
Write-Host ""

# Preflight: index only approved memory using the guarded refresh script.
# Do NOT index the whole .memsearch\memory tree -- that includes candidates/,
# deprecated/, rejected/, and raw/ subtrees and pollutes the collection.
Write-Host "[Preflight] Indexing approved memory via memsearch-refresh.ps1..."
powershell.exe -NonInteractive -File (Join-Path $scriptDir "memsearch-refresh.ps1")
Write-Host ""
```

Lines 26 onward (the $tests array and test runner loop) are unchanged.
