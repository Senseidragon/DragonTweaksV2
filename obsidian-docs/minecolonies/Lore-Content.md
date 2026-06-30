# MineColonies Lore Content

Lore documents live in `docs/minecolonies-lore/`. They are keyword-indexed by `LoreIndex` and injected into advisory model prompts when a player message matches.

## Current Coverage

**One file exists:** `needs/food.md`

MineColonies is a large mod. Topics with no lore coverage yet include:
- Worker buildings (Lumberjack, Miner, Builder, Blacksmith, Farmer, etc.)
- Research trees and university progression
- Citizen happiness and morale mechanics
- Military (Guard Tower, Barracks, Knights, Rangers)
- Supply mechanics (Warehouse, Courier, Postbox)
- Colony founding and town hall progression
- Raids and barbarian attacks

## Format Conventions (minecolonies-lore only)

These conventions apply to files in `docs/minecolonies-lore/` only — they do **not** apply to `docs/minecraft-lore/`, which uses the advisor-artifact schema with headers and bullets.

- Plain prose — no section headers, no bullet lists
- No "tier" language, no "saturation bar", no "satisfaction penalty", no game-mechanic framing
- Write as if describing the colony world from the inside, not as a game manual

## Recent Changes

`needs/food.md` — rewritten as flowing prose (session `advisor-persona-grounding`, 2026-06-30). Removed: tiered crop language, section headers, bullet lists. Retained: full production chain (Farmer → Cookery/Chef → Restaurant/Cook → Bakery/Baker), citizen hunger vs. player hunger, climate-based crops, colony-grown food superiority.

**Status: not yet re-tested in-game after rewrite.**

← [[MineColonies]]
