# Onboarding Guide: DragonTweaks V2

> **Start here first:** `START-HERE.md` at the project root routes a new session to the current continuity log, verification history, and architecture docs. This file covers static tech-stack/build/convention facts only — it is not a session log and is not kept in sync with day-to-day project state.

## Overview

DragonTweaksV2 is a NeoForge mod for Minecraft 1.21.1, authored by SenseiDragon. The mod is in active development with dual-domain memory (NeoForge + MineColonies), MineColonies integration wired as a `compileOnly` dependency, and an immersion-first advisor system (tool-calling, persona grounding, lore injection) implemented and under live-testing. The codebase follows standard NeoForge patterns with comprehensive API documentation and gameplay context extracted from the MineColonies Wiki.

## Tech Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| Language | Java | 21 |
| Mod loader | NeoForge | 21.1.230 |
| Minecraft | Minecraft | 1.21.1 |
| Build tool | Gradle + NeoGradle | 7.1.36 |
| Mappings | Parchment | 2024.11.17 |
| IDE | IntelliJ IDEA (detected) | — |

## Key Source Files

Core mod structure (3 primary Java files):

| File | Purpose |
|------|---------|
| `DragonTweaksV2.java` | `@Mod` entry point. Wires mod bus listeners, registers config, subscribes to game bus events. |
| `DragonTweaksV2Client.java` | Client-only code. Must never load on a dedicated server. Registers config GUI. |
| `Config.java` | `ModConfigSpec` wrapper. Add config values here. Reacts to `ModConfigEvent` on reload. |

Additional integration: MineColonies API wired as `compileOnly` dependency (v1.1.1299). Source stubs in `docs/stubs/minecolonies/`.

## Architecture

Standard NeoForge two-bus pattern:

```
Mod Event Bus                    Game/Forge Event Bus
─────────────────                ────────────────────
FMLCommonSetupEvent              ServerStartingEvent
ModConfigEvent                   Block/Item registration
Client setup                     Gameplay events
```

**Critical rule:** Mod bus vs. game bus mistakes fail silently. Check which bus an event belongs to before subscribing.

## Build & Run Commands

```bash
./gradlew build              # Compile and package JAR
./gradlew clean              # Clean build artifacts
./gradlew runClient          # Launch Minecraft client with mod
./gradlew runServer          # Launch dedicated server with mod
./gradlew runData            # Run data generators
./gradlew runGameTestServer  # Run game tests
./gradlew --refresh-dependencies  # Force re-download deps
```

`./gradlew test` runs the project's unit/integration test suite (advisor, openrouter, etc.). Every code change to the mod requires a green run plus an appended entry in `test-audit-trail.md` (project root, append-only) — see CLAUDE.md's Code Change Gate.

## Adding Features: Where Things Go

| I want to... | Do this... |
|--------------|-----------|
| Add a config option | `Config.java` — declare a `ModConfigSpec.*Value` field |
| Register a block/item/entity | Create a `DeferredRegister`, call `registerEventBus()` before it |
| Handle a gameplay event | Subscribe on `NeoForge.EVENT_BUS` |
| Handle a mod lifecycle event | Subscribe on `modEventBus` in the constructor |
| Add client-only code | `DragonTweaksV2Client.java` only — never load on server |
| Add generated resources | Run `./gradlew runData` → lands in `src/generated/resources/` |

## Memory & Knowledge System

This project runs a MemSearch vector memory system backed by Milvus with 1110+ approved entries across multiple domains. Before any external reasoning, query it first:

```bash
memsearch search "<query>" --top-k 5 -c ms_dragontweaksv2_4403422f
```

**Domain Organization:**
- **NeoForge domain** (`.memsearch/memory/domains/neoforge/approved/`) — Mod loader, event system, capabilities, registration APIs
- **MineColonies domain** (`.memsearch/memory/domains/minecolonies/approved/`) — API layer (IColony, ICitizenData, IJob, IBuilding, IWorkOrder, etc.) + 65 wiki-derived gameplay/immersion entries covering buildings, research, citizen systems
- **Framework domain** — Operating procedures in `docs/framework/`

To find NeoForge source stubs without bulk-loading, see `.memsearch/memory/domains/neoforge/approved/` for API documentation or query the system directly.

## Conventions

- **Commit style:** `type(scope): description` (feat, fix, refactor, docs, diag, chore)
- **Tests required** — `./gradlew test` before any change is reported complete; game tests via `runGameTestServer` are separate and used as needed
- **Mixins and Access Transformers** are commented out in `neoforge.mods.toml` — re-enable before use
- **Mod ID:** `dragontweaksv2` — must match `@Mod` annotation exactly
