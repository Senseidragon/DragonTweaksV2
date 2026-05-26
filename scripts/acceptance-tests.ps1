#Requires -Version 5.1
<#
.SYNOPSIS
    Retrieval acceptance tests for the memsearch-indexed framework rules.
    Verifies that 11 required chunks are retrievable via keyword search.
#>

$ErrorActionPreference = "Stop"

$collection = "ms_elegant_stonebraker_5c8067_4e381995"
$milvusUri  = "http://localhost:19530"
$scriptDir  = Split-Path -Parent $MyInvocation.MyCommand.Path
$repoRoot   = Split-Path -Parent $scriptDir
$memoryDir  = Join-Path $repoRoot ".memsearch\memory"

Write-Host "=== Retrieval Acceptance Tests ==="
Write-Host "Collection : $collection"
Write-Host "Memory dir : $memoryDir"
Write-Host ""

# Index memory so any newly added seed chunks are visible
Write-Host "[Preflight] Indexing memory directory..."
memsearch index $memoryDir --force -c $collection --milvus-uri $milvusUri
Write-Host ""

# Each test: issue a keyword query and assert all expected strings appear
# (case-insensitive) somewhere in the top-5 search results.
$tests = @(
    [ordered]@{
        Id      = 1
        Name    = "defective-query-log"
        Query   = "defective query log format entry timestamp"
        Expects = @("defective query log", "log format")
    }
    [ordered]@{
        Id      = 2
        Name    = "fact-deduplication"
        Query   = "deduplication before saving fact memory entry"
        Expects = @("deduplication", "duplicate")
    }
    [ordered]@{
        Id      = 3
        Name    = "git-maturity-model"
        Query   = "git maturity level lockout read-only"
        Expects = @("git maturity", "Level 0")
    }
    [ordered]@{
        Id      = 4
        Name    = "memory-first-retrieval"
        Query   = "memory first retrieval before external reasoning"
        Expects = @("memory", "retrieval")
    }
    [ordered]@{
        Id      = 5
        Name    = "query-quality-rules"
        Query   = "durable value reusable fact query quality"
        Expects = @("durable value", "query")
    }
    [ordered]@{
        Id      = 6
        Name    = "role-boundaries"
        Query   = "Claude Code role executor repository boundaries"
        Expects = @("Claude Code", "executor")
    }
    [ordered]@{
        Id      = 7
        Name    = "rule-pack-structure"
        Query   = "portable framework rule pack project specific"
        Expects = @("rule pack", "portable")
    }
    [ordered]@{
        Id      = 8
        Name    = "safe-shell-policy"
        Query   = "shell access authorization permitted commands"
        Expects = @("shell", "permitted")
    }
    [ordered]@{
        Id      = 9
        Name    = "save-after-query-protocol"
        Query   = "save after query extract durable value external reasoning"
        Expects = @("save after", "durable value")
    }
    [ordered]@{
        Id      = 10
        Name    = "threading-no-blocking-main-thread"
        Query   = "blocking main thread server thread Minecraft"
        Expects = @("main thread", "blocking")
    }
    [ordered]@{
        Id      = 11
        Name    = "event-bus-routing"
        Query   = "event bus routing mod bus game bus NeoForge"
        Expects = @("event bus", "mod bus")
    }
)

$passed = 0
$failed = 0
$results = [System.Collections.Generic.List[string]]::new()

foreach ($test in $tests) {
    $output = memsearch search $test.Query --top-k 5 -c $collection --milvus-uri $milvusUri 2>&1 |
              Out-String

    $allFound = $true
    $missing  = [System.Collections.Generic.List[string]]::new()

    foreach ($expect in $test.Expects) {
        if ($output -notmatch [regex]::Escape($expect)) {
            $allFound = $false
            $missing.Add($expect)
        }
    }

    $idPad = $test.Id.ToString().PadLeft(2)
    if ($allFound) {
        $passed++
        $results.Add("PASS  [$idPad] $($test.Name)")
    } else {
        $failed++
        $results.Add("FAIL  [$idPad] $($test.Name)  (missing: $($missing -join ', '))")
    }
}

Write-Host "--- Results ---"
foreach ($line in $results) { Write-Host $line }
Write-Host ""
Write-Host "Total: $passed passed, $failed failed out of $($tests.Count) tests"

if ($failed -gt 0) { exit 1 }
