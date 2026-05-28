# Onboarding Guide: DragonTweaks V2

## Overview

DragonTweaksV2 is a NeoForge mod for Minecraft 1.21.1, authored by SenseiDragon. It is currently at example/scaffold level — the mod loads, registers config, and logs lifecycle events, but no gameplay features are implemented yet. The codebase is set up as a clean foundation ready for feature development.

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

There are currently only 3 Java files — the entire mod is scaffold:

| File | Purpose |
|------|---------|
| `DragonTweaksV2.java` | `@Mod` entry point. Wires mod bus listeners, registers config, subscribes to game bus events. |
| `DragonTweaksV2Client.java` | Client-only code. Must never load on a dedicated server. |
| `Config.java` | `ModConfigSpec` wrapper. Add config values here. Reacts to `ModConfigEvent` on reload. |

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

No test framework is configured — `./gradlew test` finds nothing.

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

This project runs a MemSearch vector memory system backed by Milvus. Before any external reasoning, query it first:

```bash
memsearch search "<query>" --top-k 5 -c ms_dragontweaksv2_4403422f
```

NeoForge API knowledge lives in `.memsearch/memory/domains/neoforge/approved/`. Operating procedures are in `docs/framework/`. Use `docs/STUB_INDEX.md` to find NeoForge source stubs without bulk-loading them.

## Conventions

- **Commit style:** `type: description` (feat, fix, refactor, docs, chore)
- **No tests yet** — game tests via `runGameTestServer` when needed
- **Mixins and Access Transformers** are commented out in `neoforge.mods.toml` — re-enable before use
- **Mod ID:** `dragontweaksv2` — must match `@Mod` annotation exactly
