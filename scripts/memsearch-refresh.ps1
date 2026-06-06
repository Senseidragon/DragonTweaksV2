$ErrorActionPreference = "Stop"
$env:PYTHONUTF8 = "1"

$milvusDir   = "C:\Users\sense\.memsearch\milvus-docker"
$repoRoot    = "C:\Users\sense\Desktop\DragonTweaksV2"
$memoryDir   = "$repoRoot\.memsearch\memory"
$collection  = "ms_dragontweaksv2_4403422f"
$milvusUri   = "http://localhost:19530"

Write-Host "[1/4] Starting Docker Milvus stack..."
Set-Location $milvusDir
docker compose up -d

Write-Host "[2/4] Reindexing project memory into Claude Code plugin collection ($collection)..."
Set-Location $repoRoot

# INDEXING INVARIANT - READ BEFORE MODIFYING THIS PATH LIST
#
# Only approved/ subdirectories and explicitly approved seed/daily files may
# appear in the memsearch index command below. This list IS the enforcement
# boundary.
#
# PERMITTED paths:
#   - Named seed/daily files (e.g. seed-framework-rules.md, 2026-05-25.md)
#   - <domain>/approved/ and projects/<project>/approved/ directories
#
# PROHIBITED paths - never add these, even temporarily:
#   - .memsearch/memory/      (indexes everything, including unsafe subtrees)
#   - .memsearch/             (same problem, wider scope)
#   - Any path containing: deprecated/  candidates/  rejected/  raw/
#
# WHY: Index: false in a memory file is policy metadata only. Memsearch does
# NOT honor it at index time. Path selection here is the only technical
# barrier preventing deprecated, candidate, and rejected entries from
# polluting the live collection and being recalled as authoritative facts.
#
# If pending-reindex.txt names a path outside the approved list, STOP and
# report to Dragon. Do not add it here without explicit authorization.
memsearch index `
    "$memoryDir\seed-framework-rules.md" `
    "$memoryDir\2026-05-25.md" `
    "$memoryDir\framework\approved" `
    "$memoryDir\domains\neoforge\approved" `
    "$memoryDir\domains\minecolonies\approved" `
    "$memoryDir\domains\minecraft\approved" `
    "$memoryDir\projects\dragontweaksv2\approved" `
    --force -c $collection

Write-Host "[3/4] Flushing collection so rows are visible to search..."
python -c "from pymilvus import MilvusClient; c=MilvusClient(uri='$milvusUri'); c.flush('$collection'); stats=c.get_collection_stats('$collection'); print('Row count:', stats)"

Write-Host "[4/4] Smoke-test search against plugin collection..."
memsearch search "Docker Milvus" --top-k 5 -c $collection

Write-Host "Done. MemSearch is ready for Claude Code recall."
