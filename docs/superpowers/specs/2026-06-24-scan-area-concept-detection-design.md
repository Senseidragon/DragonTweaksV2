# Scan Area Concept Detection & Mod-Integration Registry — Design Spec

**Date:** 2026-06-24
**Mod:** DragonTweaksV2 (NeoForge 1.21.1)
**Branch:** advisor-persona-grounding
**Status:** Design confirmed; not yet implemented.

**Relates to:** Finding #5, first recorded in `codify/codify00.md` (live-testing bug found after `docs/superpowers/specs/2026-06-21-advisor-classification-grounding-design.md` shipped). This spec designs the grounding-data fix for that finding. It does not modify the classification table or `handleQuery` flow from the 06-21 spec — it extends what `ScanAreaTool` is actually capable of reporting once it's invoked.

---

## Problem Statement

Finding #5: the classification redesign's round-1-miss deterministic tool injection (06-21 spec, Section C, Step 4) trusts forced tool results as grounding even when those tools cannot structurally answer the question. Concrete trigger: "is there a ladder nearby?" matched the `location` category, force-injecting `get_environment` + `scan_area`. The advisor answered "No ladder in sight" with full confidence — contradicted by a player-provided screenshot showing a ladder directly ahead.

Root cause, confirmed by reading `ScanAreaTool.java` (not guessed at): the tool has exactly three data paths —

1. `scanEntities` — `LivingEntity` census within a bounding box, bucketed passive/neutral/hostile/aggro. Entities only.
2. `scanUnderground` — flood-fills **air** blocks below the player to find cave/void shapes (`floodFill`), classified by volume.
3. `detectOreType` — runs only on the surface of detected voids, checking 8 hardcoded substrings (`coal_ore`, `iron_ore`, `gold_ore`, `diamond_ore`, `emerald_ore`, `redstone_ore`, `lapis_ore`, `copper_ore`).

A ladder is solid (never visited by the air flood-fill), not an entity, and not one of the 8 ore strings. There is no code path that could ever detect it. This is a missing capability, not a coverage gap to hedge around.

## Goals

- Replace `detectOreType`'s 8 hardcoded substrings with vanilla ore `BlockTags`, which already merge stone/deepslate variants and pick up any mod ore registered into the same tags.
- Add genuine block-level "concept" detection (ladder, bed, door, stairs, etc.) so `scan_area` can answer "is there X nearby" instead of being structurally blind to it.
- Bound detection to physically reachable space (BFS through air, stopped by solid blocks) rather than a naive radius cube, so enclosed rooms don't get credited with "seeing" through walls, and open areas aren't artificially clipped.
- Make adding awareness of a specific mod's blocks (e.g. ProjectE's transmutation table) a deliberate, bounded, low-cost integration step — not a generic classifier, and not an obligation triggered by every mod a player happens to run.
- Keep the concept matcher uniformly tag-based (`TagKey<Block>` only, never a raw block ID) so the scan-time check has exactly one shape regardless of whether the tag is vanilla, mod-native, or one DragonTweaksV2 defines itself to group a target mod's block.

## Non-Goals

- No semantic/ML classifier. The concept table remains a curated, deliberate keyword/tag list — consistent with the 06-21 spec's existing posture.
- No generic cross-mod auto-detection (e.g. "any tag containing 'machine'"). Unsupported mods remain silently unrecognized by design. New mod awareness is added only via deliberate, named integration, mirroring the existing MineColonies precedent (`project_minecolonies.md`).
- No public/third-party plugin API. Integrations live inside DragonTweaksV2's own source, authored by Dragon, gated by `ModList.isLoaded` checks where relevant.
- No fix for Finding #4 (chain-of-thought leak) — unrelated bug.
- No implementation in this session. This spec captures a confirmed design for a later implementation phase.

---

## Section A — Ore Detection via BlockTags

Replace `detectOreType(BlockState)`'s 8 substring checks with checks against vanilla ore `BlockTags` (coal/iron/gold/diamond/emerald/redstone/lapis/copper ores). The display-name mapping stays a small static table of the same 8 entries, keyed by tag instead of substring. Net effect: correctness improves (any mod ore registered into the same vanilla tags becomes visible for free) and the hand-maintained substring list disappears.

**Open item:** exact tag identifiers must be confirmed against this repo's pinned NeoForge 21.1.230 / Parchment 2024.11.17 mappings before implementation — not assumed from general knowledge.

## Section B — Reachable-Space Bounding

Generalize the existing `floodFill` BFS (today used only by `scanUnderground`, seeded from a detected void) to also run seeded from the player's own position. Air-connected space, stopped by solid blocks, capped at a radius (proposed default: 8 blocks) and a max-volume safety cap mirroring `MAX_FLOOD_VOLUME`. This single mechanism encodes enclosure for free: indoors, the BFS stops at walls; outdoors, it expands to the radius cap. Only blocks bordering this reachable region are checked against the concept table in Section C (and could also bound Section A's ore-surface check, which is already structured similarly today).

**Note:** this is a CPU-bound, synchronous, server-thread operation — no I/O/network, so it does not trip the main-thread-blocking rule — but the radius/volume caps still matter for per-tick CPU budget, a related but distinct concern.

## Section C — Concept Detection Table

A registry: concept name → `TagKey<Block>`, populated once at mod setup. Vanilla concepts are registered unconditionally — illustrative, not final: `ladder`/climbable → `BlockTags.CLIMBABLE` (note: bundles ladders with vines/scaffolding, a known precision limit, not fixed by this spec), `bed` → `BlockTags.BEDS`, `door` → `BlockTags.DOORS`, `stairs` → `BlockTags.STAIRS`.

A block is reported under a concept only if its actual tag membership resolves to that concept — this is what prevents false positives from decorative, non-functional builds (e.g. wool blocks arranged to look like a bed are never reported as a "bed," since they don't carry `BlockTags.BEDS`).

Scan-time check: for each registered concept, does any block bordering the reachable space (Section B) carry that tag? If the registry holds only the vanilla entries, this is not a special case — the same iteration runs over a shorter list. There is no "if no integrations registered" branch anywhere in this design; an integration-free run and a multi-integration run hit identical code.

**Open item:** final vanilla concept list is illustrative above, not exhaustively decided — to be finalized at implementation time.

## Section D — Mod Integration Registration

Concepts from optional mods register the same way as vanilla concepts, just conditionally:

- Gate: `ModList.get().isLoaded("<modid>")` at mod setup time, for the Java-side registration call.
- An integration's only responsibility: ensure a `TagKey<Block>` exists covering the target block(s), then register `{concept name, tag}` into the same Section C table. Raw block IDs never reach the registry/matcher.
- If the target mod doesn't expose a usable tag of its own, DragonTweaksV2 defines and ships its own tag (datapack JSON under `data/dragontweaksv2/tags/block/`, or NeoForge's `BlockTagsProvider` data-gen via the existing `runData` task) listing that block as a tag member. An unsatisfied tag reference (referencing a block from a mod that isn't installed) is harmless in NeoForge — it simply matches nothing — so the tag *data* does not need its own loaded-check; the gate matters only for the Java registration call itself.
- Worked example: ProjectE's transmutation table (single specific block, not a broad category — confirms the matcher needs to handle single-block tags, not just multi-block vanilla categories like `STAIRS`). Exact ProjectE registry name, and whether it already exposes a usable tag, must be confirmed against ProjectE's actual data before implementation — not assumed in this spec.
- Explicitly not a generic plugin API or auto-discovery mechanism. Each integration is a small, specific, Dragon-authored addition inside this mod's own source, matching the existing MineColonies precedent rather than introducing a third-party-extensible framework.

## Section E — Response-Phrasing Scope (flagged, not solved here)

Even with Sections A–D implemented, the advisor's stated claim must match what was actually searched — e.g. "no ladder in this room" rather than an unqualified "no ladder nearby" when detection was bounded by an enclosed reachable space. This is a persona/prompt-shaping concern layered on top of the data fix; it is flagged as a dependency for the implementation phase, not designed in this spec.

---

## Constraints

- Do not commit without explicit authorization from Dragon.
- Pre-flight checklist required before any Java source edit.
- `./gradlew test` required before any change is reported complete; `test-audit-trail.md` entry appended per change (append-only).
- Nothing blocks the Minecraft main/server/render thread.
- Tag identifiers (vanilla ore tags, ProjectE's block/tag identity) must be verified against this repo's pinned mappings and the target mod's actual data before implementation — not assumed.

## Open Questions

- Exact vanilla concept list beyond the illustrative examples in Section C.
- Exact NeoForge 21.1.230 / Parchment 2024.11.17 ore tag names (Section A).
- ProjectE's transmutation table registry name and existing tag (if any) (Section D).
- Final reachable-space radius/volume-cap defaults — 8 blocks proposed in Section B, not finalized.
- Response-phrasing scope fix (Section E) — flagged, not designed.

This spec addresses Finding #5's grounding-data layer only. It does not itself constitute a fix until implemented, tested, and live-validated per standing project gates.
