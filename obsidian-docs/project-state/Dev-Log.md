# Dev Log

## 2026-06-30 — advisor-persona-grounding (current)

- Rewrote `identify_nearby`: dropped TARGETS predicate table; general visible-block scan with substring filter; spawner enrichment via NBT
- `BlockUtil.java` extracted as shared `friendlyName()` utility
- `ToolCallOrchestrator`: removed `includeHistory` from Category record; signal-only history detection; excluded `identify_nearby` from force-inject; chitchat fallthrough fix; PERSONA_BIO anchor sentence
- `AdvisorChatHandler`: `--` dev comment filter — messages starting with `--` skip all advisor processing
- `docs/minecolonies-lore/needs/food.md`: rewritten as flowing prose, "tier" language removed
- `.gitattributes`: `* text=auto eol=lf` added to normalize line endings on Windows
- Live tests passed: fluid state line (test 1), MineColonies food lore (test 2), build-tool spawn gate (test 4), `--` filter

## 2026-06 — advisor system built (a8463ba)

- Two-model pipeline (advisory + flavor) via OpenRouter
- `scan_area` tool with COLLIDER raycast occlusion and fluid state reporting
- `get_environment`, `get_inventory`, `get_status` tools (force-injected)
- `LoreIndex` keyword-matched lore injection from `docs/`
- PERSONA_BIO persona grounding
- AdvisorSavedData per-player session memory
- `dt.purge` player command

## Earlier

- Sound patches: goat horn, screaming goat
- MineColonies compileOnly dependency wired
- Compliance testing architecture

← [[Project-State]]
