---
tags: [moc, root, dragontweaksv2]
type: map-of-content
---

# DragonTweaks V2 — Map of Content

**Mod ID:** `dragontweaksv2` | **Author:** SenseiDragon | **Platform:** NeoForge 21.1.230 / Minecraft 1.21.1 / Java 21
**Status:** Scaffold — loads, logs, config registered; no gameplay features implemented yet.

## Architecture
- [[DragonTweaksV2-Main]] — `@Mod` entry point, event bus wiring, server lifecycle
- [[DragonTweaksV2Client]] — Client-only code, config screen (`@Dist.CLIENT`)
- [[Config]] — `ModConfigSpec` wrapper, config reload events
- [[NeoForge-Patterns]] — Two-bus pattern, `DeferredRegister`, Mixin/AT toggle

## NPC Roles & Dialogue
- [[Advisor-System]] — Core design: immersion-first companion, archetypes, sensory model, BYOK cost model
- [[NPC-Roles]] — Planned: custom citizen job types
- [[Dialogue-System]] — Planned: dialogue trees for NPC interactions
- [[ObservationTicker]] — Planned: periodic observation events driving NPC behaviour

## Memory & Persistence
- [[Memory-System]] — MemSearch vector DB, approval pipeline, domain/framework/project separation
- [[Conversation-History]] — Planned: per-NPC conversation history persistence

## AI Inference
- [[OpenRouter-Integration]] — Planned: cloud LLM inference via OpenRouter (sole backend; supersedes Ollama)
- [[Compliance-Testing]] — Model probe suite; scores instruction-following before models enter rotation

## MineColonies Integration
- [[MineColonies-API]] — Deferred: MineColonies mod API integration layer
- [[Research-Tree]] — Planned: custom research nodes in University tree

## Blueprints
- [[Blueprint-Packs]] — Planned: dragon-themed building schematic packs

## Resources & Assets
- [[Sound-Patches]] — Planned: vanilla sound event patches via resource pack

## Dev Log
- [[Dev-Log]] — Chronological session timeline
