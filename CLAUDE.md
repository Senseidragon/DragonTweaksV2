# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Memory System

Before any external reasoning, query project memory first:

```bash
memsearch search "<query>" --top-k 5 -c ms_dragontweaksv2_4403422f
```

Framework operating procedures are in docs/framework/. Load the relevant doc before validation, candidate evaluation, shell commands, or git operations — use MemorySystemDocIndex.md to route to the correct file.

Before creating any file, check whether it already exists. If it does, report the conflict to Dragon and show the diff between the existing content and the intended new content. Do not overwrite without explicit authorization.

At the start of each session:

1. **Reindex check** — if `.memsearch/candidates/pending-reindex.txt` exists, read it, run `memsearch index <dir> --force -c ms_dragontweaksv2_4403422f` for each listed directory, then delete the file. Do this before anything else and before checking candidate folders.

2. **Candidate queue** — check the following folders for any files. If any are present, immediately read `docs/framework/fact-deduplication.md` and `docs/active/memory-system-architecture.md` and run validation against existing approved memory without waiting for instruction. Apply all candidates with confidence >= 0.85 automatically — promote to approved, tombstone superseded entries, and reindex. Route candidates below 0.85 to a human review patch and report to Dragon. Do not proceed with any other task until the candidate queue is clear.

- `.memsearch/memory/framework/candidates/extracted/`
- `.memsearch/memory/framework/candidates/tentative-approved/`
- `.memsearch/memory/domains/neoforge/candidates/extracted/`
- `.memsearch/memory/domains/neoforge/candidates/tentative-approved/`
- `.memsearch/memory/domains/minecolonies/candidates/extracted/`
- `.memsearch/memory/domains/minecolonies/candidates/tentative-approved/`
- `.memsearch/memory/projects/dragontweaksv2/candidates/`

After any external reasoning, web fetch, or tool call that returns new information, capture the raw result as a candidate entry in the appropriate domain or framework candidates/extracted/ folder before proceeding. Do not filter or summarize — write the raw result. Validation handles quality control.

## ECC Plugin

ECC (Everything Claude Code) is installed globally at `~/.claude/plugins/`. It provides hooks, skills, and agents that are active in every session. GateGuard is an ECC hook that fires before file edits and bash commands, requiring facts to be stated before proceeding. Do not disable GateGuard via `ECC_GATEGUARD=off` or `ECC_DISABLED_HOOKS` — it complements the safe-shell policy and no-silent-overwrite rule. If GateGuard blocks a legitimate operation, satisfy its fact requirements and retry.

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

## Obsidian Sync

The Obsidian vault at `obsidian-docs/` is the human-readable counterpart to the Claude memory system. When writing a `type: project` memory entry, also create or update the corresponding Obsidian doc and add a link to it in `obsidian-docs/DragonTweaks-v2.md` (the MOC). Feedback and reference memory types do not need Obsidian counterparts.

## Stub Library

NeoForge 21.1.230 source stubs are in `docs/stubs/`. Do not bulk-load stubs. Use `docs/STUB_INDEX.md` to find the relevant package, then load only the specific file(s) needed for the current task.
