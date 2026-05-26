#Requires -Version 5.1
param(
    [Parameter(Mandatory=$true)]
    [string]$Task
)

$ErrorActionPreference = "Stop"

$collection  = "ms_dragontweaksv2_4403422f"
$milvusUri   = "http://localhost:19530"

Write-Host ""
Write-Host "=== Task Start ==="
Write-Host "Task: $Task"
Write-Host ""

Write-Host "[1/2] Running memory preflight for: $Task"
memsearch search $Task --top-k 5 -c $collection --uri $milvusUri

Write-Host ""
Write-Host "[2/2] Memory preflight complete."
Write-Host "=== Ready: $Task ==="
Write-Host ""
