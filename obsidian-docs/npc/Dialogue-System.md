---
tags:
  - feature
  - planned
  - npc
  - dialogue
  - ai-inference
status: planned
---

# Dialogue System

Player-facing dialogue for MineColonies NPCs. Driven by [[ObservationTicker]] events and optionally backed by AI inference.

## Key Details

**Status:** Not yet implemented.

### Planned Modes
| Mode | Backend | Notes |
|------|---------|-------|
| Static | Hardcoded/JSON trees | No inference cost, deterministic |
| Cloud AI | [[OpenRouter-Integration]] | Dynamic responses via cloud LLM |

### Trigger Points
- Player right-clicks an NPC with an active role (see [[NPC-Roles]])
- [[ObservationTicker]] fires a periodic observation event
- Colony milestone events (building completed, raid survived, etc.)

### Conversation Persistence
Per-NPC conversation history stored via [[Conversation-History]] so NPCs remember prior exchanges.

### Response Format
Responses should be short (1–3 sentences) to fit Minecraft's chat UI without truncation.

## Relationships
- [[NPC-Roles]] — dialogue is attached to specific role types
- [[ObservationTicker]] — ticker triggers unprompted NPC speech
- [[OpenRouter-Integration]] — cloud inference backend
- [[Conversation-History]] — persistence layer for NPC memory
