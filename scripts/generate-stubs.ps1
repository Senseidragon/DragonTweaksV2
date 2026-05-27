# generate-stubs.ps1
# Extracts NeoForge sources jar into docs/stubs/ and regenerates docs/STUB_INDEX.md
# Run this script whenever NeoForge version changes or stubs need to be rebuilt

$ErrorActionPreference = "Stop"

$sourcesJar = "C:\Users\sense\.gradle\caches\modules-2\files-2.1\net.neoforged\neoforge\21.1.230\8b0dba969a089e3c98f601b0a00e978a65936a6b\neoforge-21.1.230-sources.jar"
$stubsDir = "C:\Users\sense\Desktop\DragonTweaksV2\docs\stubs"
$indexFile = "C:\Users\sense\Desktop\DragonTweaksV2\docs\STUB_INDEX.md"

Write-Host "[1/2] Extracting NeoForge 21.1.230 sources into docs/stubs/..."
if (Test-Path $stubsDir) { Remove-Item $stubsDir -Recurse -Force }
Expand-Archive -Path $sourcesJar -DestinationPath $stubsDir
Write-Host "Extracted $(Get-ChildItem $stubsDir -Recurse -File | Measure-Object | Select-Object -ExpandProperty Count) files."

Write-Host "[2/2] STUB_INDEX.md must be regenerated manually by Claude Code after extraction."
Write-Host "Run: claude 'Regenerate docs/STUB_INDEX.md by scanning docs/stubs/'"
Write-Host "Done."
