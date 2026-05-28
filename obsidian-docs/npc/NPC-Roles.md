---
tags:
  - feature
  - planned
  - npc
  - minecolonies
status: planned
---

# NPC Roles

Custom citizen job types for MineColonies workers with dragon-themed behaviour and dialogue.

## Key Details

**Status:** Not yet implemented. Depends on [[MineColonies-API]] integration being active.

### Planned Role Types
- Dragon Keeper — manages dragon-adjacent structures and research
- Lore Keeper — delivers lore dialogue triggered by [[ObservationTicker]]
- (additional roles TBD)

### Technical Approach
MineColonies exposes citizen job types via its API. Each custom role will:
1. Register a job entry with MineColonies' job registry
2. Implement an AI goal tree for worker behaviour
3. Wire into [[Dialogue-System]] for player-facing interaction

### Unlock Mechanism
Custom roles may be gated behind [[Research-Tree]] nodes in the University.

## Relationships
- [[Dialogue-System]] — dialogue trees triggered by NPC interactions
- [[ObservationTicker]] — periodic events that drive NPC speech
- [[MineColonies-API]] — required integration layer
- [[Research-Tree]] — research may unlock role availability
