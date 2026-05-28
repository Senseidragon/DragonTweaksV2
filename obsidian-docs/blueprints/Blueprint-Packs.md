---
tags:
  - feature
  - planned
  - blueprints
  - minecolonies
status: planned
---

# Blueprint Packs

Dragon-themed building schematic packs for MineColonies, distributed inside the mod JAR.

## Key Details

**Status:** Not yet implemented. Requires [[MineColonies-API]] integration.

### Planned Scope
- Dragon-themed variants for standard MineColonies buildings (barracks, tavern, university, etc.)
- Packed into `src/main/resources/assets/dragontweaksv2/schematics/`
- Some styles gated behind [[Research-Tree]] nodes

### Blueprint Format
MineColonies uses `.blueprint` files (Structurize format). Workflow:
1. Build the structure in-game using Structurize's scan tool
2. Export as `.blueprint`
3. Place in mod resources under the correct building name key
4. Register via MineColonies schematic pack API

### Style Tiers (Planned)
| Style | Buildings Covered | Research Gate |
|-------|-------------------|---------------|
| Dragon Stone | Barracks, Guard Tower | None (default) |
| Dragon Hoard | Treasury, Warehouse | Draconic Architecture I |
| Dragon Lair | University, Library | Draconic Architecture II |

## Relationships
- [[MineColonies-API]] — schematic registration hook required
- [[Research-Tree]] — higher tier styles locked behind research nodes
