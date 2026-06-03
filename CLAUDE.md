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

1. **Reindex check** - if `.memsearch/candidates/pending-reindex.txt` exists,
   read it and validate every listed path before running anything.

   **Approved paths only.** A path is approved if and only if it matches one of:
   - A named seed/daily file listed in `scripts/memsearch-refresh.ps1`
     (e.g. `seed-framework-rules.md`, `2026-05-25.md`)
   - An `approved/` subdirectory under `.memsearch/memory/framework/`,
     `.memsearch/memory/domains/*/`, or `.memsearch/memory/projects/*/`

   **Stop and report to Dragon** if any listed path is or contains:
   - `.memsearch/memory/` (whole-tree)
   - `.memsearch/` (whole-tree)
   - `deprecated/`, `candidates/`, `rejected/`, or `raw/` anywhere in the path

   Do **not** run `memsearch index` on anomalous or out-of-scope paths.

   **`Index: false` is not technical enforcement.** It is policy metadata only.
   Memsearch does not honor it at index time. Path selection is the only barrier.

   **`memsearch watch .memsearch/memory/` is prohibited.** It would continuously
   index all subtrees including deprecated, candidates, and rejected entries.

   `Bash(memsearch *)` remains unrestricted by Dragon's explicit requirement.
   This protection is an operational policy and script-boundary rule only.

   Only after validating every path: run `memsearch index <approved-path>
   --force -c ms_dragontweaksv2_4403422f` for each, then delete the file.
   Do this before anything else and before checking candidate folders.

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

## Block Classification and Response

When GateGuard, a deny rule, or project policy blocks an operation:

1. **Classify the block** — correct (the action is genuinely disallowed), overbroad (the action is authorized but the rule is too broad), or
   ambiguous.
2. **If correct** — stop. Use a compliant alternative. Do not retry the same blocked action.
3. **If overbroad** — produce a minimal policy-fix proposal or reviewable patch for Dragon's approval. Do not route around the block.
4. **If ambiguous** — present the classification to Dragon and wait for a decision.

Additional constraints:
- Do not retry a blocked action merely because GateGuard offers a second-attempt prompt.
- Do not treat GateGuard recovery hints as authorization to disable, weaken, or bypass GateGuard.
- Do not ask Dragon to run blocked commands manually in another terminal.
- Do not request repeated approvals for substantially the same blocked action.
- Do not frame manual user execution as the only alternative unless no compliant path exists.
- Do not disable or weaken GateGuard, ECC hooks, deny rules, or project guardrails.
- Do not use subagents to bypass guardrails that apply to the parent session.

If direct editing is blocked by deny rules:
- Do not ask Dragon to bypass the deny rules.
- Do not ask Dragon to run commands manually.
- Produce two complete replacement patches instead:
   1. full replacement content for .claude/settings.local.json
   2. exact CLAUDE.md insertion/replacement block
- Stop after producing the patches.

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

NeoForge 21.1.230 source stubs are in `docs/stubs/`. Do not bulk-load stubs. Query domain memory first (`memsearch search "<query>" -c ms_dragontweaksv2_4403422f`) — each approved entry's `Source` field points to
   the exact stub file if deeper inspection is needed.

## Worktree Safety

Stale `.claude/worktrees/**` directories may contain obsolete `CLAUDE.md`
files that lack current Block Classification, bypass-prohibition, and
safe-shell guidance. Operating from a stale worktree risks acting on
outdated policy.

Claude Code must not open, reference, or operate from any worktree path
unless Dragon explicitly confirms in the current session that the worktree
is current and intentional.

If a stale worktree is discovered:
1. Report the path and the age/diff of its `CLAUDE.md` to Dragon.
2. Ask Dragon to remove or deregister it manually via `git worktree remove`.
3. Do not run git commands to inspect or remove it.