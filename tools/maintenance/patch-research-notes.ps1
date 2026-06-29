$approved = '.memsearch/memory/domains/minecolonies/approved'
foreach ($file in @('research-combat-tree.md','research-civilian-tree.md','research-technology-tree.md')) {
    $p = Join-Path $approved $file
    (Get-Content $p -Raw) -replace 'See research-global-requirements\.md\.','See [[research-global-requirements.md]].' | Set-Content $p -NoNewline
    Write-Host "Patched: $file"
}
