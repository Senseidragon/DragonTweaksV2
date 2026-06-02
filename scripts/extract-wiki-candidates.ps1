#Requires -Version 7
<#
.SYNOPSIS
    Batch-extracts MineColonies wiki pages from docs/wiki-ref/ into memsearch
    memory candidates under .memsearch/memory/domains/minecolonies/candidates/extracted/.

.DESCRIPTION
    For each .mdoc file:
      - Parses YAML frontmatter (name, description, workers, type)
      - Strips Markdoc component tags, preserving readable prose
      - Converts internal markdown links [text](/wiki/x/slug) -> [[slug.mdoc]]
      - Writes a pipeline-compatible candidate file with wikilink source field

.PARAMETER DryRun
    Preview candidate filenames and fact summaries without writing anything.

.PARAMETER Overwrite
    Re-generate candidates that already exist in the output directory.
#>
[CmdletBinding()]
param(
    [switch]$DryRun,
    [switch]$Overwrite
)

$wikiRefDir  = "C:\Users\sense\Desktop\DragonTweaksV2\docs\wiki-ref"
$outputDir   = "C:\Users\sense\Desktop\DragonTweaksV2\.memsearch\memory\domains\minecolonies\candidates\extracted"
$datestamp   = Get-Date -Format "yyyy-MM-dd"

$subdirOrder = @("needs", "systems", "buildings", "items")

function ConvertTo-PlainText {
    param([string]$raw, [string]$buildingName, [string]$mdocFile)

    # Replace {% building /%} (self-referential) with the building name
    $text = $raw -replace '\{%\s+building\s+/%\}', $buildingName
    $text = $text -replace '\{%\s+building\s*/\s*%\}', $buildingName

    # Replace {% building name="x" ... /%} with the building name
    $text = $text -replace '\{%\s+building\s+name="([^"]+)"[^%]*/%\}', '$1'

    # Replace {% worker name="x" ... /%} with "x"
    $text = $text -replace '\{%\s+worker\s+name="([^"]+)"[^%]*/%\}', '$1'

    # Replace {% worker /%} (self-referential) — use filename as fallback
    $workerFallback = [System.IO.Path]::GetFileNameWithoutExtension($mdocFile)
    $text = $text -replace '\{%\s+worker\s+/%\}', $workerFallback

    # Replace {% item name="x" /%} with "x"
    $text = $text -replace '\{%\s+item(?:_page)?\s+name="([^"]+)"[^%]*/%\}', '$1'

    # Replace {% research_link name="x" /%} with "x"
    $text = $text -replace '\{%\s+research_link\s+name="([^"]+)"[^%]*/%\}', '$1'

    # Replace {% social_link id="x" /%} with "x"
    $text = $text -replace '\{%\s+social_link\s+id="([^"]+)"[^%]*/%\}', '$1'

    # Strip remaining block-level Markdoc tags — keep inner text
    $text = $text -replace '\{%\s+\w[^%]*%\}', ''
    $text = $text -replace '\{%\s*/\w+\s*%\}', ''

    # Convert internal markdown links [text](/wiki/x/slug) -> [[slug.mdoc]]
    $text = $text -replace '\[([^\]]+)\]\(/wiki/[^/]+/([^)]+)\)', '[[${2}.mdoc]]'

    # Strip image references
    $text = $text -replace '!\[[^\]]*\]\([^)]*\)', ''

    # Collapse excessive blank lines
    $text = $text -replace '(\r?\n){3,}', "`n`n"

    return $text.Trim()
}

function Get-FrontmatterField {
    param([string[]]$lines, [string]$field)
    foreach ($line in $lines) {
        if ($line -match "^${field}:\s*(.+)$") {
            return $Matches[1].Trim().Trim('"')
        }
    }
    return $null
}

function Get-FrontmatterList {
    param([string[]]$lines, [string]$field)
    $inList = $false
    $items  = @()
    foreach ($line in $lines) {
        if ($line -match "^${field}:") { $inList = $true; continue }
        if ($inList) {
            if ($line -match '^\s+-\s+(.+)') { $items += $Matches[1].Trim() }
            elseif ($line -match '^\w') { break }
        }
    }
    return $items
}

function Parse-MdocFrontmatter {
    param([string]$content)
    $fm   = @{}
    $body = $content

    if ($content -match '(?s)^---\r?\n(.+?)\r?\n---\r?\n?(.*)$') {
        $fmRaw   = $Matches[1]
        $body    = $Matches[2].Trim()
        $fmLines = $fmRaw -split "`n"

        foreach ($key in @('type','id','name','plural','description','title','rotation')) {
            $val = Get-FrontmatterField $fmLines $key
            if ($val) { $fm[$key] = $val }
        }
        $workers = Get-FrontmatterList $fmLines 'workers'
        if ($workers.Count -gt 0) { $fm['workers'] = $workers -join ', ' }
    }
    return $fm, $body
}

function Build-CandidateFact {
    param([hashtable]$fm, [string]$subdir)

    $desc    = $fm['description']
    $name    = if ($fm['name']) { $fm['name'] } elseif ($fm['id']) { $fm['id'] } elseif ($fm['title']) { $fm['title'] } else { '(unknown)' }
    $workers = $fm['workers']

    if ($desc) { return $desc }

    switch ($subdir) {
        'buildings' {
            $base = "The $name building"
            if ($workers) { return "$base is staffed by: $workers." }
            return "$base is a MineColonies colony structure."
        }
        'needs'    { return "Citizen need: $name — affects colonist behaviour and happiness." }
        'systems'  { return "Colony system: $name — governs a core aspect of colony operation." }
        'items'    { return "MineColonies item: $name." }
        default    { return "MineColonies wiki entry: $name." }
    }
}

function Build-CandidateTitle {
    param([hashtable]$fm, [string]$subdir)

    $name    = if ($fm['name']) { $fm['name'] } elseif ($fm['id']) { $fm['id'] } elseif ($fm['title']) { $fm['title'] } else { '(unknown)' }
    $workers = $fm['workers']

    switch ($subdir) {
        'buildings' {
            if ($workers) { return "MineColonies building — $name (workers: $workers)" }
            return "MineColonies building — $name"
        }
        'needs'    { return "MineColonies citizen need — $name" }
        'systems'  { return "MineColonies system — $name" }
        'items'    { return "MineColonies item — $name" }
        default    { return "MineColonies wiki — $name" }
    }
}

# ── Main ─────────────────────────────────────────────────────────────────────

New-Item -ItemType Directory -Force -Path $outputDir | Out-Null

$written  = 0
$skipped  = 0
$previews = [System.Collections.Generic.List[string]]::new()

foreach ($subdir in $subdirOrder) {
    $srcDir = Join-Path $wikiRefDir $subdir
    if (-not (Test-Path $srcDir)) { continue }

    $files = Get-ChildItem -Path $srcDir -Filter "*.mdoc" | Sort-Object Name
    foreach ($file in $files) {
        $baseName   = $file.BaseName
        $outName    = "wiki-${subdir}-${baseName}-${datestamp}.md"
        $outPath    = Join-Path $outputDir $outName
        $sourceLink = "[[docs/wiki-ref/$subdir/$($file.Name)]]"

        if (-not $Overwrite -and (Test-Path $outPath)) {
            $skipped++
            continue
        }

        $raw       = Get-Content $file.FullName -Raw -Encoding UTF8
        $fm, $body = Parse-MdocFrontmatter $raw
        $plainBody = ConvertTo-PlainText $body ($fm['name'] ?? $baseName) $file.Name
        $fact      = Build-CandidateFact $fm $subdir
        $title     = Build-CandidateTitle $fm $subdir

        $candidate = @"
---
title: $title
domain: minecolonies
fact: $fact
confidence: 0.80
usefulness: medium
authority: wiki-derived
---

$plainBody

**Source:** $sourceLink
"@

        if ($DryRun) {
            $previews.Add("  [$subdir] $baseName")
            $previews.Add("    title: $title")
            $previews.Add("    fact:  $fact")
            $previews.Add("")
        } else {
            Set-Content -Path $outPath -Value $candidate -Encoding UTF8
            $written++
        }
    }
}

Write-Host ""
if ($DryRun) {
    Write-Host "=== extract-wiki-candidates  [DRY-RUN] ===" -ForegroundColor Cyan
    $previews | ForEach-Object { Write-Host $_ }
    Write-Host "Run without -DryRun to write candidates." -ForegroundColor Yellow
} else {
    Write-Host "=== extract-wiki-candidates  [DONE] ===" -ForegroundColor Green
    Write-Host "  Written : $written"
    Write-Host "  Skipped : $skipped (already exist; use -Overwrite to regenerate)"
    Write-Host "  Output  : $outputDir"
}
