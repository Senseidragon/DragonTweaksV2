$memoryMd = "C:\Users\sense\.claude\projects\C--Users-sense-Desktop-DragonTweaksV2\memory\MEMORY.md"

$append = @'
- [Wikilinks protocol for memory source fields](feedback_wikilinks_protocol.md) — [[local/file]], [[none]], [[url-to-specific-file]], [[other-entry.md]]; NeoForge domain not yet converted
'@

Add-Content $memoryMd $append -Encoding UTF8
Write-Host "MEMORY.md updated."
