<#
.SYNOPSIS
    Standardize the **Source:** field in minecolonies domain memory nodes to wikilink format.
    Plain file paths become [[path]]. Descriptive/non-path values become [[none]].
    Already-wrapped [[...]] values are left untouched.

.DESCRIPTION
    Dry-run by default. Pass -Apply to write changes.
    Idempotent: re-running after -Apply shows all previously-changed files as CLEAN.

    Scope   : .memsearch\memory\domains\minecolonies\approved\ only (deprecated skipped)

.PARAMETER Apply
    Write changes to disk. Without this flag the script is read-only.

.EXAMPLE
    .\fix-memory-source-fields.ps1          # dry-run
    .\fix-memory-source-fields.ps1 -Apply   # apply changes
#>
param(
    [switch]$Apply
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$RepoRoot   = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
$DomainPath = Join-Path $RepoRoot '.memsearch\memory\domains\minecolonies\approved'

if (-not (Test-Path $DomainPath -PathType Container)) {
    Write-Error "Domain directory not found: $DomainPath"
    exit 1
}

$Utf8NoBom = New-Object System.Text.UTF8Encoding $false

$nChanged     = 0
$nWouldChange = 0
$nClean       = 0
$nNoSource    = 0

# Matches **Source:** lines whose value is NOT already a wikilink [[...]]
$SourcePattern = [regex]'(?m)^(\*\*Source:\*\*\s+)(?!\[\[)(.+)$'

function Get-WikilinkValue([string]$raw) {
    $trimmed = $raw.Trim()
    # A linkable path: no spaces, contains at least one path separator or dot
    if ($trimmed -match '^[\w./\\-]+$') {
        return "[[$trimmed]]"
    }
    return '[[none]]'
}

$files = Get-ChildItem -Path $DomainPath -Recurse -Filter '*.md' -File

foreach ($f in $files) {
    $rel     = $f.FullName.Substring($RepoRoot.Length).TrimStart('\', '/')
    $content = [System.IO.File]::ReadAllText($f.FullName, [System.Text.Encoding]::UTF8)

    if (-not ($content -match '\*\*Source:\*\*')) {
        Write-Host "NO SOURCE       $rel" -ForegroundColor Red
        $nNoSource++
        continue
    }

    if ($SourcePattern.IsMatch($content)) {
        $match    = $SourcePattern.Match($content)
        $rawValue = $match.Groups[2].Value.Trim()
        $linked   = Get-WikilinkValue $rawValue

        if ($Apply) {
            $updated = $SourcePattern.Replace($content, {
                param($m)
                "$($m.Groups[1].Value)$(Get-WikilinkValue $m.Groups[2].Value.Trim())"
            })
            [System.IO.File]::WriteAllText($f.FullName, $updated, $Utf8NoBom)
            Write-Host "CHANGED         $rel" -ForegroundColor Green
            Write-Host "  was : $rawValue"
            Write-Host "  now : $linked"
            $nChanged++
        } else {
            Write-Host "WOULD CHANGE    $rel" -ForegroundColor Yellow
            Write-Host "  was : $rawValue"
            Write-Host "  will: $linked"
            $nWouldChange++
        }
    } else {
        Write-Host "CLEAN           $rel" -ForegroundColor Cyan
        $nClean++
    }
}

Write-Host ''
Write-Host '=== Summary ===' -ForegroundColor White
if ($Apply) {
    Write-Host "  Changed:         $nChanged"
} else {
    Write-Host "  Would change:    $nWouldChange"
}
Write-Host "  Clean:           $nClean"
Write-Host "  No source field: $nNoSource"

if (-not $Apply -and $nWouldChange -gt 0) {
    Write-Host ''
    Write-Host "Run with -Apply to apply $nWouldChange change(s)." -ForegroundColor Yellow
}
