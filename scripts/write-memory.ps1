$memDir = "C:\Users\sense\.claude\projects\C--Users-sense-Desktop-DragonTweaksV2\memory"

@'
---
name: Wikilinks protocol for memory source fields
description: How to format source references in memory entries and candidates — local files, no source, and external URLs
type: feedback
---

Use `[[...]]` wikilink syntax for all source references in memory entries and candidates.

- Local file: `[[docs/api/minecolonies/colony/ICitizenData.java]]`
- No traceable source: `[[none]]`
- External URL (must point to a specific file, not a repo root): `[[https://github.com/neoforged/documentation/blob/main/docs/items/datacomponents.md]]`
- Cross-reference to another memory entry: `[[filename.md]]`

**Why:** Consistent wikilink format makes sources clickable in Obsidian and machine-parseable for validation. URLs that point only to a repo root are not acceptable — the link must resolve to actual data.

**How to apply:** Apply whenever writing a candidate entry or approved memory file. NeoForge domain not yet converted — apply when that conversion happens. MineColonies domain already follows this protocol.
'@ | Set-Content "$memDir\feedback_wikilinks_protocol.md" -Encoding UTF8

Write-Host "Memory files written."
