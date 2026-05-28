---
tags:
  - feature
  - planned
  - research
  - minecolonies
status: planned
---

# Research Tree

Custom research nodes added to MineColonies' University research tree.

## Key Details

**Status:** Not yet implemented. Requires [[MineColonies-API]] integration.

### Planned Research Branch
- Top-level node: "Draconic Studies" (unlocked at University level 3)
- Child nodes gate access to [[NPC-Roles]] and [[Blueprint-Packs]] building styles

### Technical Approach
MineColonies uses a data-driven research system via JSON. Custom entries go in:
```
src/main/resources/data/dragontweaksv2/researches/
```

Example node structure:
```json
{
  "branch": "dragontweaksv2:draconic",
  "parentResearch": "minecolonies:core/something",
  "researchLevel": 1,
  "effects": [{ "dragontweaksv2:dragon_keeper_unlock": 1 }],
  "requirements": [{ "item": "minecraft:dragon_egg", "quantity": 1 }]
}
```

### Planned Nodes
| Node | Unlocks | Cost |
|------|---------|------|
| Draconic Studies I | Dragon Keeper role | Dragon Egg |
| Draconic Studies II | Lore Keeper role | Nether Star |
| Draconic Architecture | Dragon-themed blueprints | Ancient Debris x4 |

## Relationships
- [[MineColonies-API]] — research registry hook required
- [[NPC-Roles]] — research nodes unlock citizen roles
- [[Blueprint-Packs]] — research nodes unlock building styles
