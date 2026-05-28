---
tags:
  - feature
  - planned
  - npc
  - events
  - neoforge
status: planned
---

# ObservationTicker

Periodic server-side tick that prompts NPCs to generate an observation, feeding into [[Dialogue-System]].

## Key Details

**Status:** Not yet implemented.

### Planned Behaviour
- Fires on a configurable interval (e.g. every N game ticks or real-time seconds)
- Selects a nearby NPC with an active role (see [[NPC-Roles]])
- Passes world context (time of day, recent events, player proximity) to [[Dialogue-System]]
- Dialogue System produces a short response delivered in chat or overhead text

### NeoForge Hook
Will subscribe to a tick event on the **Forge/game bus** (see [[DragonTweaksV2-Main]] for bus wiring). Tick interval and enable/disable flag exposed via [[Config]].

## Relationships
- [[NPC-Roles]] — only NPCs with an active role produce observations
- [[Dialogue-System]] — ticker output is routed here for response generation
- [[DragonTweaksV2-Main]] — Forge bus subscription registered in constructor
