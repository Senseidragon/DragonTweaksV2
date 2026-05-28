---
tags:
  - feature
  - planned
  - memory
  - npc
  - persistence
status: planned
---

# Conversation History

Per-NPC conversation history store. Allows NPCs to remember prior exchanges with the player across sessions.

## Key Details

**Status:** Not yet implemented.

### Planned Scope
- Each NPC (identified by colony ID + citizen ID) maintains a rolling history of recent exchanges
- History is passed as context to [[Dialogue-System]] on each new interaction
- Persisted to disk (NBT or JSON) so it survives server restarts

### Storage Design (Planned)
```
Key: <colony_id>:<citizen_id>
Value: list of {role, content, timestamp} entries (capped at N most recent)
```
| Field | Type | Notes |
|-------|------|-------|
| `role` | String | `"player"` or `"npc"` |
| `content` | String | Message text |
| `timestamp` | Long | Game tick or Unix epoch |

History list is capped (e.g. last 20 entries) to bound prompt token cost.

### Integration with Memory System
Conversation history is runtime state, not knowledge. It does NOT flow through the MemSearch candidate pipeline (see [[Memory-System]]). It is stored separately in world save data.

## Relationships
- [[Dialogue-System]] — injects history as context for each inference call
- [[NPC-Roles]] — history is scoped per NPC role instance
- [[Memory-System]] — distinct from knowledge memory; runtime state only
