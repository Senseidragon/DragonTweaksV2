#Requires -Version 7
[CmdletBinding()]
param(
    [Parameter(Mandatory)][string]$InputFile,
    [string]$OutputDir  = "C:\Users\sense\Desktop\DragonTweaksV2\.memsearch\memory\domains\minecolonies\candidates\extracted",
    [string]$Domain     = "MineColonies",
    [string]$Title      = "",
    [string]$Triggers   = "",
    [switch]$Apply
)

$inputPath = Resolve-Path $InputFile -ErrorAction Stop
$content   = Get-Content $inputPath -Raw -Encoding UTF8

# Derive title from filename if not provided
if (-not $Title) {
    $Title = [System.IO.Path]::GetFileNameWithoutExtension($inputPath)
}

# Extract source URL from content if present (line starting with "Source: http")
$sourceUrl = $null
foreach ($line in ($content -split "`n")) {
    if ($line -match '(?i)^Source:\s+(https?://\S+)') {
        $sourceUrl = $Matches[1].Trim()
        break
    }
}
$sourceField = if ($sourceUrl) { "**Source:** [[$sourceUrl]]" } else { "**Source:** [[none]]" }

# Strip the raw source line from body content so it isn't duplicated
$content = ($content -split "`n" | Where-Object { $_ -notmatch '(?i)^Source:\s+https?://' }) -join "`n"

# Derive output filename from input filename + datestamp
$datestamp   = Get-Date -Format "yyyy-MM-dd"
$baseName    = [System.IO.Path]::GetFileNameWithoutExtension($inputPath)
$outFileName = "$baseName-$datestamp.md"
$outPath     = Join-Path $OutputDir $outFileName

$candidate = @"
Domain: $Domain
Change type: ADD
Title: $Title
Type: fact
Intent triggers: $Triggers
$sourceField
**Provenance:** Claude/Context7-derived — not verified against source files.
Proposed fact/rule:

$content
"@

$mode = if ($Apply) { "APPLY" } else { "DRY-RUN" }
Write-Host ""
Write-Host "=== write-candidate-from-file  [$mode] ===" -ForegroundColor Cyan
Write-Host "  Input : $inputPath"
Write-Host "  Output: $outPath"
Write-Host "  Source: $sourceField"
Write-Host ""

if ($Apply) {
    New-Item -ItemType Directory -Force -Path $OutputDir | Out-Null
    Set-Content -Path $outPath -Value $candidate -Encoding UTF8
    Write-Host "Candidate written." -ForegroundColor Green
} else {
    Write-Host "--- Preview ---"
    Write-Host $candidate
    Write-Host "Run with -Apply to write." -ForegroundColor Yellow
}
