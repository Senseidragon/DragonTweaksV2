---
tags:
  - feature
  - deferred
  - minecolonies
  - integration
status: deferred
---

# MineColonies API

Integration layer between DragonTweaksV2 and the MineColonies mod API.

## Key Details

**Status:** Deferred — pending MineColonies domain memory pack (as of 2026-05-24).

### Deferred Reason
MineColonies domain memory pack not yet built. Integration begins once:
1. MineColonies domain pack is validated in [[Memory-System]]
2. API surface is mapped (citizen jobs, colony events, research registry, schematic hooks)

### Planned Integration Points
| Area | MineColonies Hook | DragonTweaks Use |
|------|-------------------|-----------------|
| Citizen jobs | `IJobRegistry` | Register custom [[NPC-Roles]] |
| Colony events | `ColonyInGameEvent` | Trigger [[ObservationTicker]] observations |
| Research | `IGlobalResearchTree` | Add custom [[Research-Tree]] nodes |
| Schematics | Blueprint API | Distribute [[Blueprint-Packs]] |

### Dependency Declaration
When ready, add to `neoforge.mods.toml`:
```toml
[[dependencies.dragontweaksv2]]
    modId = "minecolonies"
    type = "required"
    versionRange = "[1.21.1-x.x,)"
    ordering = "AFTER"
    side = "BOTH"
```

## Relationships
- [[NPC-Roles]] — depends on citizen job registry
- [[Research-Tree]] — depends on research tree registry
- [[Blueprint-Packs]] — depends on schematic/blueprint API
- [[Memory-System]] — MineColonies domain pack must exist before integration begins
