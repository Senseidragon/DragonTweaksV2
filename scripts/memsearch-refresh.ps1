$ErrorActionPreference = "Stop"

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
memsearch index $memoryDir --force -c $collection

Write-Host "[3/4] Flushing collection so rows are visible to search..."
python -c "from pymilvus import MilvusClient; c=MilvusClient(uri='$milvusUri'); c.flush('$collection'); stats=c.get_collection_stats('$collection'); print('Row count:', stats)"

Write-Host "[4/4] Smoke-test search against plugin collection..."
memsearch search "Docker Milvus" --top-k 5 -c $collection

Write-Host "Done. MemSearch is ready for Claude Code recall."
