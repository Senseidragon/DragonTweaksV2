# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

DragonTweaksV2 is a NeoForge mod for Minecraft 1.21.1, authored by SenseiDragon. Mod ID: `dragontweaksv2`. Package root: `io.github.senseidragon.dragontweaksv2`.

## Build Commands

```bash
./gradlew build              # Compile and package the mod JAR
./gradlew clean              # Clean build artifacts
./gradlew runClient          # Launch Minecraft client with the mod loaded
./gradlew runServer          # Launch dedicated server with the mod loaded
./gradlew runData            # Run data generators (resources)
./gradlew runGameTestServer  # Run game tests
./gradlew --refresh-dependencies  # Force re-download dependencies
```

No test framework is currently set up; `./gradlew test` will find nothing.

## Key Versions

| Component | Version |
|-----------|---------|
| Minecraft | 1.21.1 |
| NeoForge  | 21.1.230 |
| Java      | 21 |
| NeoGradle | 7.1.36 |
| Parchment mappings | 2024.11.17 |

All versions are pinned in `gradle.properties` and referenced via template expansion in `neoforge.mods.toml`.

## Architecture

The mod follows standard NeoForge structure with a hard split between common and client-side code:

- **`DragonTweaksV2.java`** — Main entry point (`@Mod`). Registers the config spec with NeoForge and subscribes to server-lifecycle events on the mod event bus.
- **`DragonTweaksV2Client.java`** — Client-only code (`@EventBusSubscriber(Dist.CLIENT)`). Registers the config screen factory (GUI) and handles client setup. This class must never be loaded on a dedicated server.
- **`Config.java`** — Wraps NeoForge's `ModConfigSpec` builder. Config values are declared as `ForgeConfigSpec.ConfigValue<T>` fields. The class listens for `ModConfigEvent` to react to reloads.

Generated resources (from `runData`) land in `src/generated/resources/` and are included in the source set automatically.

## NeoForge Patterns

- Event subscribers go on the **mod bus** (`FMLCommonSetupEvent`, `ModConfigEvent`, etc.) or the **game/Forge bus** (`ServerStartingEvent`, block/item registration, etc.). Mixing them up silently fails.
- Deferred registers (`DeferredRegister`) must be created before `registerEventBus()` is called.
- Mixins and Access Transformers are currently **commented out** in `neoforge.mods.toml`; re-enable the relevant lines before using them.

## Docs

- `docs/versions.md` — Pinned version baseline; update it when bumping any dependency.
- `docs/framework/` — Project-ops reference docs (git maturity model, query quality rules, safe-shell policy, etc.). Not Minecraft-specific; treat as standing operating procedures for this project.
