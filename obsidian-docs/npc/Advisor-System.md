---
tags:
  - feature
  - design
  - npc
  - ai
status: in-design
---

# Advisor System

The central feature of DragonTweaksV2. A floating book entity that serves as an immersion-first companion — knowledgeable, role-weighted, and conversationally alive.

## Entity & Lifecycle

- **Pre-colony:** floating Book entity — acts as scout/wilderness companion
- **Colony founded:** upgrades to Book-and-Quill — transitions into colony advisor role
- **Colony destroyed:** reverts to scout

## Archetypes

All advisors share conversational capability but weight memory and proactive commentary toward their role.

| Archetype | Role weight |
|-----------|-------------|
| Scout | Exploration, hostiles, terrain, weather |
| Colony Advisor | Housing, colonist welfare, resource shortfalls |
| Farm Hand | Crop cycles, soil, seasons |
| Planner | Build order, supply chains |
| Military Commander | Threat tracking, patrol, defense posture |
| Cranky Fisherman | Personal details, weather gripes, local gossip |

Example behavior: a colony advisor with no housing for colonists will *tell you* — it won't wait to be asked.

## Sensory Model

The advisor perceives (and speaks in immersive terms, never game coordinates):

- Biome, weather, time of day
- Sky visibility — knows when the player is underground; knows Y-level internally but uses terrain descriptors ("deep underground", "near the surface"), never raw coordinates
- Nearby passives — approximated counts ("a small herd"), never exact numbers
- Hostiles in detection range; underground hostiles that make noise trigger hints ("I sense the presence of zombies somewhere around here")
- Villagers, illagers, structures — approximate distance + direction
- Colony state

## Memory

Per-player conversational memory, ~20 turns, persists across reloads. Role-weighted: military commander prioritizes tactical info, fisherman prioritizes personal details.

## AI / Cost Model

BYOK (Bring Your Own Key) — operator or player supplies API key.

- **Ambient chatter:** cheap model (~$0.16–0.20/M weighted tokens)
- **High-stakes colony synthesis:** smarter model on demand
- **Target:** 2-player server ≈ $3 over several weeks; 10-player server under the cost of a Starbucks coffee

See [[OpenRouter-Integration]] for the inference backend.

## Immersion Constraint

Built initially for Dragon and his mom to play together on a private server.

> A cranky fisherman who gives the shortest possible answer isn't cranky — he's broken.

Efficiency shortcuts that gut personality are explicitly wrong here, even when they appear to save tokens or compute. Immersion quality is non-negotiable.

## V1 Lessons

V1 was derailed by an AI assistant applying "efficiency first" shortcuts that hollowed out the immersion design. V2 rebuilds from scratch with immersion as the primary constraint.

## Relationships

- [[NPC-Roles]] — advisor is one of several NPC archetypes
- [[Dialogue-System]] — dialogue trees and conversational memory
- [[ObservationTicker]] — sensory events that feed advisor awareness
- [[OpenRouter-Integration]] — AI inference backend
- [[Conversation-History]] — per-player memory persistence
- [[MineColonies-API]] — colony state awareness
