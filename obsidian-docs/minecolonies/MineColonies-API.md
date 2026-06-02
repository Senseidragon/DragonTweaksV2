---
tags:
  - feature
  - active
  - minecolonies
  - integration
  - domain-memory
status: active
last-updated: 2026-06-02
---

# MineColonies API

Integration layer between DragonTweaksV2 and the MineColonies mod API.

## Key Details

**Status:** Active — Domain memory established with 1110+ approved entries (as of 2026-06-02).

### Domain Memory Content

**API Layer** (reference implementations and patterns):
- `IColony` — Colony state, structure access, citizen management
- `ICitizenData` — Citizen attributes, skills, home assignment, satiation
- `IJob` — Job base class, work assignments, job state
- `IBuilding` — Building type registry, schematic management, work orders
- `IWorkOrder` — Work order lifecycle, priority, progress tracking
- `IColonyManager` — Colony discovery, creation, persistence
- `IMinecoloniesAPI` — Mod registry entry point
- Research trees — Combat, civilian, and technology research branches
- Job registration patterns — Custom citizen job types

**Gameplay & Immersion** (65 wiki-derived entries):
- **Buildings** (51 entries) — Residential, production, defense, culture systems
- **Systems** (9 entries) — Citizen behavior, work allocation, research progression
- **Needs** (4 entries) — Happiness, satiation, housing requirements
- **Items** (1 entry) — Dragon-relevant crafting materials

Total: ~90 entries covering both API surface and gameplay context.

### Integration Points (Active)
| Area | MineColonies Hook | DragonTweaks Use |
|------|-------------------|-----------------|
| Citizen jobs | `IJobRegistry` | Register custom [[NPC-Roles]] (advisor roles, scout) |
| Colony events | `ColonyInGameEvent` | Trigger [[ObservationTicker]] observations and dialogue |
| Research | `IGlobalResearchTree` | Add custom [[Research-Tree]] nodes for advisory mechanics |
| Schematics | Blueprint API | Distribute [[Blueprint-Packs]] (dragon-themed structures) |

### Dependency Status
**Declared in `build.gradle`:**
```gradle
compileOnly "com.minecolonies:minecolonies-api:1.1.1299-ALPHA"
```

**In `neoforge.mods.toml`:** Optional dependency declared (compileOnly allows optional integration)
```toml
[[dependencies.dragontweaksv2]]
    modId = "minecolonies"
    type = "OPTIONAL"
    versionRange = "[1.21.1-1.0.0,)"
    ordering = "AFTER"
    side = "BOTH"
```

## Relationships
- [[NPC-Roles]] — depends on citizen job registry; advisor archetypes map to MC job structure
- [[Research-Tree]] — depends on research tree registry; custom nodes integrate with university progression
- [[Blueprint-Packs]] — depends on schematic/blueprint API; dragon-themed schematics
- [[Advisor-System]] — uses MC colony state for immersion context (citizen happiness, colony progress)
- [[Memory-System]] — Domain memory established; query with `memsearch search "minecolonies <query>" --top-k 5 -c ms_dragontweaksv2_4403422f`
- [[Dev-Log]] — 2026-06-02 session: 65 wiki-derived gameplay entries promoted to approved
