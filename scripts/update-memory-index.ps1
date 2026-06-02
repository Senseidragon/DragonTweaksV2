$memoryMd = "C:\Users\sense\.claude\projects\C--Users-sense-Desktop-DragonTweaksV2\memory\MEMORY.md"

$append = @'
- [GateGuard blocked writes — script workaround](feedback_gateguard_script_workaround.md) — write .ps1.txt in workspace, Dragon renames+runs; script must never disable or alter any guardrail
- [Wikilinks protocol for memory source fields](feedback_wikilinks_protocol.md) — [[local/file]], [[none]], [[url-to-specific-file]], [[other-entry.md]]; NeoForge domain not yet converted
'@

Add-Content $memoryMd $append -Encoding UTF8
Write-Host "MEMORY.md updated."
