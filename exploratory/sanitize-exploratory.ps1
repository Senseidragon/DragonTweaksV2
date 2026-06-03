#Requires -Version 7
# Adapted from scripts/sanitize-extracted-candidates.ps1
# Processes .md files in exploratory/ and writes sanitized output to exploratory/sanitized/
[CmdletBinding()]
param(
    [switch]$Apply
)

$extractedDir  = "C:\Users\sense\Desktop\DragonTweaksV2\exploratory"
$sanitizedDir  = "C:\Users\sense\Desktop\DragonTweaksV2\exploratory\sanitized"
$rejectedDir   = "C:\Users\sense\Desktop\DragonTweaksV2\exploratory\sanitized\rejected"

# Patterns
# Compliant:   **Source:** [[anything]]
$compliantRx   = [regex]'^\*\*Source:\*\*\s+\[\[.+\]\]\s*$'
# Fixable label (Source/evidence: [[...]])
$labelFixRx    = [regex]'^Source/evidence:\s+(\[\[.+\]\])\s*$'
# Bare none variants
$bareNoneRx    = [regex]'^(?:\*\*Source:\*\*|Source(?:/evidence)?):?\s+none\s*$'

$report = [System.Collections.Generic.List[PSCustomObject]]::new()

# Only process .md files directly in exploratory/ (not subdirs)
$files = Get-ChildItem -Path $extractedDir -Filter "*.md" -File -ErrorAction Stop

foreach ($file in $files) {
    $lines      = Get-Content -Path $file.FullName -Encoding UTF8
    $sourceLine = $null
    $sourceIdx  = -1

    for ($i = 0; $i -lt $lines.Count; $i++) {
        $l = $lines[$i].Trim()
        if ($l -match '(?i)^\*?\*?Source') {
            $sourceLine = $l
            $sourceIdx  = $i
            break
        }
    }

    # --- Evaluate ---

    if ($null -eq $sourceLine) {
        $entry = [PSCustomObject]@{
            File      = $file.Name
            Action    = "NOT_FIXABLE"
            Reason    = "No Source field found"
            OldSource = "(missing)"
            NewSource = ""
        }
        if ($Apply) {
            New-Item -ItemType Directory -Force -Path $rejectedDir | Out-Null
            Copy-Item -Path $file.FullName -Destination (Join-Path $rejectedDir $file.Name) -Force
        }
        $report.Add($entry)
        continue
    }

    if ($compliantRx.IsMatch($sourceLine)) {
        $entry = [PSCustomObject]@{
            File      = $file.Name
            Action    = "CLEAN"
            Reason    = "Source field already compliant"
            OldSource = $sourceLine
            NewSource = ""
        }
        if ($Apply) {
            New-Item -ItemType Directory -Force -Path $sanitizedDir | Out-Null
            Copy-Item -Path $file.FullName -Destination (Join-Path $sanitizedDir $file.Name) -Force
        }
        $report.Add($entry)
        continue
    }

    if ($bareNoneRx.IsMatch($sourceLine)) {
        $newLine = "**Source:** [[none]]"
        $entry = [PSCustomObject]@{
            File      = $file.Name
            Action    = "FIX"
            Reason    = "Bare 'none' value normalised to wikilink"
            OldSource = $sourceLine
            NewSource = $newLine
        }
        if ($Apply) {
            $lines[$sourceIdx] = $newLine
            New-Item -ItemType Directory -Force -Path $sanitizedDir | Out-Null
            Set-Content -Path (Join-Path $sanitizedDir $file.Name) -Value $lines -Encoding UTF8
        }
        $report.Add($entry)
        continue
    }

    $labelMatch = $labelFixRx.Match($sourceLine)
    if ($labelMatch.Success) {
        $wikilinks = $labelMatch.Groups[1].Value
        $newLine   = "**Source:** $wikilinks"
        $entry = [PSCustomObject]@{
            File      = $file.Name
            Action    = "FIX"
            Reason    = "Label 'Source/evidence:' replaced with '**Source:**'"
            OldSource = $sourceLine
            NewSource = $newLine
        }
        if ($Apply) {
            $lines[$sourceIdx] = $newLine
            New-Item -ItemType Directory -Force -Path $sanitizedDir | Out-Null
            Set-Content -Path (Join-Path $sanitizedDir $file.Name) -Value $lines -Encoding UTF8
        }
        $report.Add($entry)
        continue
    }

    # Anything else: plain prose, ambiguous, unrecognised format
    $entry = [PSCustomObject]@{
        File      = $file.Name
        Action    = "NOT_FIXABLE"
        Reason    = "Source value is plain prose, ambiguous, or unsupported format"
        OldSource = $sourceLine
        NewSource = ""
    }
    if ($Apply) {
        New-Item -ItemType Directory -Force -Path $rejectedDir | Out-Null
        Copy-Item -Path $file.FullName -Destination (Join-Path $rejectedDir $file.Name) -Force
    }
    $report.Add($entry)
}

# --- Report ---

$mode = if ($Apply) { "APPLY" } else { "DRY-RUN" }
Write-Host ""
Write-Host "=== sanitize-exploratory  [$mode] ===" -ForegroundColor Cyan
Write-Host ""

$report | Format-Table -AutoSize -Property File, Action, Reason, OldSource, NewSource

$clean      = ($report | Where-Object Action -eq "CLEAN").Count
$fixed      = ($report | Where-Object Action -eq "FIX").Count
$notFixable = ($report | Where-Object Action -eq "NOT_FIXABLE").Count

Write-Host "Summary: CLEAN=$clean  FIX=$fixed  NOT_FIXABLE=$notFixable  (total=$($report.Count))"
if (-not $Apply) {
    Write-Host ""
    Write-Host "Run with -Apply to copy CLEAN/FIX files to exploratory/sanitized/ and NOT_FIXABLE to exploratory/sanitized/rejected/." -ForegroundColor Yellow
}
