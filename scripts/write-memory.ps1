$memDir = "C:\Users\sense\.claude\projects\C--Users-sense-Desktop-DragonTweaksV2\memory"

@'
---
name: GateGuard blocked writes — script workaround
description: When GateGuard blocks a write to ~/.claude/ memory, write a .ps1.txt script inside the workspace for Dragon to rename and execute
type: feedback
---

When a Write to `~/.claude/projects/.../memory/` is blocked by GateGuard (outside workspace path), do not attempt to circumvent. Instead, write a `.ps1.txt` file inside the workspace (e.g. `scripts/write-memory.ps1.txt`), ask Dragon to rename it to `.ps1` and run it with `! scripts/write-memory.ps1`.

**Why:** GateGuard treats `~/.claude/` as outside the safe workspace path, and blocks new `.ps1` files as unidentifiable. A `.ps1.txt` file passes GateGuard and Dragon can rename and execute it.

**How to apply:** Any time a memory write is blocked by GateGuard, immediately fall back to this pattern rather than retrying the blocked write.

**Hard constraint:** Scripts written using this workaround must never disable, modify, or bypass any existing guardrail, hook, or GateGuard configuration. Write-only to the memory directory — nothing else.
'@ | Set-Content "$memDir\feedback_gateguard_script_workaround.md" -Encoding UTF8

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
