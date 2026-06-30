# Test Audit Trail

Append-only log of code changes and their test coverage.
Format: date | file(s) changed | what changed | test(s) covering it | result

**Prior entries archived to:** `audit-archive/test-audit-trail-through-2026-06-27.md.gz`
This file continues from 2026-06-28. All earlier history is in the archive.

---

## 2026-06-28 — Redesign: ScanAreaTool full environmental scan; sycophancy fix in PERSONA_BIO

- **Files:** `advisor/tools/ScanAreaTool.java` (major rewrite), `advisor/ToolCallOrchestrator.java` (PERSONA_BIO addition)
- **Spec:** `docs/superpowers/specs/2026-06-26-scan-area-redesign.md`
- **Root cause addressed:** `scan_area` returned "Nothing notable detected nearby." in virtually every situation — on the surface in a forest, underground with visible torches and coal — because concept detection checked only 4 block types (ladder, bed, door, stairs). The tool provided the model nothing to reason over. Separately, the model hallucinated terrain details when the player challenged a correct empty response (sycophancy under social pressure).
- **Changes — ScanAreaTool.java:**
  - Replaced Y=63 sea-level underground trigger with `level.canSeeSky(playerPos)`
  - Added `BlockDistribution` (ground/above-surface/fluids buckets)
  - Added `scanBlockDistribution()`: dense zone (radius 0–4, y±4, every block) + sparse zone (radius 5–8, y±8, every-other-block via `(|dx|+|dz|)%2==0`); per-column surface detection (top-down scan, first non-air non-fluid block = surface height)
  - Added `categorizeBlock()`: 20 categories including terrain (grass, dirt, stone, sand, gravel, ice, snow, mud), vegetation (tree_log, tree_leaves, planks, stone_brick, crops, flower), light sources (`getLightEmission(level, pos) > 0`), and all 8 ore types (coal through copper) — ores checked before stone to prevent misclassification
  - Added fluid detection: `FluidState.isSource()` distinguishes still vs flowing for water and lava
  - Replaced old `scanConcepts()` with `buildConceptLine()` (same BFS logic, now using `SPARSE_RADIUS`)
  - Expanded `VANILLA_CONCEPT_TAGS` from 4 entries to 8 (added crafting table, furnace, chest, barrel)
  - New output format: labeled fields (`Sky:`, `Ground cover:`, `Above surface:`, `Fluids:`, `Cave:`, `Ores:`, `Within reach:`, entity lines); zero-value fields omitted
  - Removed `SEA_LEVEL` constant; cave morphology + ore detection now always triggers when `canSeeSky` is false
  - Cave output reformatted to `Cave: <type> (~N blocks)[, M apparent exits to surface]`
  - Ore output reformatted to `Ores: ore_coal(N) ore_iron(M) ...` (top 6 by count)
  - Deprecated `getLightEmission()` → `getLightEmission(level, pos)` (NeoForge IBlockStateExtension API); added `BlockGetter` import
- **Changes — ToolCallOrchestrator.java:**
  - Added sycophancy-fix sentence to `PERSONA_BIO`: "What the tools show you is what you know. You don't revise your read of the area because someone pushes back — if the scan came up empty, that's what you saw. You'd rather say you saw nothing than invent something you didn't."
- **Tests:** Existing `ScanAreaToolTest` (3 tests — `classifyVoid` thresholds) still pass; no new unit tests added (`scanBlockDistribution` requires a live `ServerLevel`).
- **Coverage limitation:** Block distribution scan, fluid detection, and per-column surface detection cannot be unit-tested without a live Minecraft server level. In-game live testing required.
- **`./gradlew test` result:** BUILD SUCCESSFUL — all tests pass, no deprecation warnings.
- **Not committed:** git access remains revoked per standing instruction.
- **Result:** PASS (compile- and test-suite-verified). In-game confirmation required.

---

## 2026-06-28 — Fix: ScanAreaTool occlusion and underground bucketing

- **File:** `advisor/tools/ScanAreaTool.java`
- **Root cause 1 (occlusion):** Initial implementation sampled every block in the scan grid regardless of visibility. Blocks buried behind solid stone walls were being reported as detected — e.g. coal and copper ore several blocks deep inside rock, nowhere near any visible surface. Fix: added `Level.clip()` raycast from player eye position to each candidate block center. Blocks where the ray hits something else first are skipped. Used `ClipContext.Block.VISUAL` and `ClipContext.Fluid.ANY` with the player as the entity parameter.
- **Root cause 2 (light/blindness):** No check for whether the player could actually see. Fix: added blindness effect check (`MobEffects.BLINDNESS`) and light level check (`getMaxLocalRawBrightness > 0`) before running the block distribution scan. Returns `Visibility: none` if either condition applies.
- **Root cause 3 (underground bucketing):** The per-column surface/above-surface bucket model assumes outdoor terrain with a clear ground plane. Underground, the top-down surface detection finds the cave ceiling, causing wall and floor blocks below the ceiling to be silently dropped (fell through both `y == surf` and `y > surf` conditions). Fix: underground scans skip the surface/above bucketing entirely and accumulate all raycast-visible blocks into a single flat `Visible blocks:` bucket. Surface/above bucketing is retained for outdoor (`canSeeSky`) scans only.
- **Tests:** `./gradlew test` — BUILD SUCCESSFUL, all tests pass.
- **Coverage limitation:** Raycast behavior, light level gating, and underground flat bucket require in-game live testing.
- **Not committed:** git access remains revoked per standing instruction.
- **Result:** PASS (compile- and test-suite-verified). In-game confirmation required.

---

## 2026-06-28 — Fix: ScanAreaTool torch/light-source detection bypass fires before raycast

- **File:** `advisor/tools/ScanAreaTool.java` — `samplePos` method
- **Root cause:** The light-source bypass (torches, lanterns) was placed after the raycast guard in `samplePos`. Light-emitting non-solid blocks have no visual hit-box — `ClipContext.Block.VISUAL` returns `HitResult.Type.MISS` for them — so the early return at `if (hit.getType() == HitResult.Type.MISS) return;` fired first, killing the bypass before it was ever reached. Torches were absent from all scan output as a result.
- **Fix:** Moved block-state and fluid-state reads to before the raycast. Light-emitting non-fluid blocks (`fs.isEmpty() && bs.getLightEmission(level, pos) > 0`) are now detected and counted before the raycast executes, then return immediately. Fluids (lava emits light) are excluded from this bypass by the `fs.isEmpty()` guard and continue through the normal raycast→fluid path. All other blocks fall through to the raycast as before. The dead-code light-source branch that followed the raycast was removed.
- **Process failures documented (see memory):** (1) Control flow was not traced step-by-step before placing the bypass, so the early-return above it was missed. (2) No unit test was written for the bypass path. Both captured durably in `feedback_trace_control_flow.md` and `feedback_test_new_paths.md`.
- **Tests:** `./gradlew test --rerun` — BUILD SUCCESSFUL, 96 tests, 0 failures, 0 skipped.
- **Coverage limitation:** `samplePos` requires a live `ServerLevel` and raycast infrastructure. A unit test that exercises the torch detection path specifically is not possible without a running NeoForge instance. In-game confirmation required.
- **Not committed:** git access remains revoked per standing instruction.
- **Result:** PASS (compile- and test-suite-verified). In-game confirmation required.

---

## 2026-06-28 — Fix: ScanAreaTool light-source bypass incorrectly applied to solid light emitters

- **File:** `advisor/tools/ScanAreaTool.java` — `samplePos` method, bypass condition only
- **Root cause:** The bypass condition `bs.getLightEmission(level, pos) > 0` matched all light-emitting blocks including solid ones (magma, sea lantern, redstone lamp, lanterns). Solid light emitters have a real VoxelShape and are correctly hit by the VISUAL raycast — they do not need the bypass. The bypass was skipping occlusion checks for them, producing `light_source` detections through walls.
- **Fix:** Added `bs.getCollisionShape(level, pos).isEmpty()` to the bypass condition. Blocks with collision (solid light emitters) fall through to the raycast for proper occlusion checking. Changed `bs.getLightEmission(level, pos)` to `bs.getLightEmission()` (no-arg, reads the `lightLevel` property directly). Known limitation: mods that override `getLightEmission(BlockGetter, BlockPos)` without using the `lightLevel` property will not be detected by the bypass — accepted, not writing against badly-written mods.
- **Tests:** `./gradlew test --rerun` — BUILD SUCCESSFUL, 96 tests, 0 failures.
- **Coverage limitation:** Same as previous entry — `samplePos` requires a live `ServerLevel`. In-game confirmation required.
- **Not committed:** git access remains revoked per standing instruction.
- **Result:** PASS (compile- and test-suite-verified). In-game confirmation required.

---

## 2026-06-28 — Feat: ScanAreaTool light source locational description and ambient lighting

- **Files:** `advisor/tools/ScanAreaTool.java`, `ScanAreaToolTest.java`, `AdvisorPersonaGenerativeTest.java`
- **What changed:**
  - `BlockDistribution` gains a `lightSources` list (type + vertical position per detected source).
  - Bypass path, lava fluid path, and solid-light-emitter categorize path all route to `lightSources` instead of generic dist map counts.
  - New helpers: `lightBucket(int)` (0–15 → dark/dim/low/moderate/well-lit/bright), `verticalRelation(int, double)` (above/at level/below), `lightSourceType(BlockState)` (torch/soul torch/redstone torch/glow lichen/glowstone/sea lantern/magma/redstone lamp/lantern/light source), `summarizeLightSources(List<String>)` (groups by type, deduplicates positions, formats as "torch x2 (above, at level); glow lichen x1 (below)").
  - Output gains a `Lighting: <bucket> [— <sources>]` line after Sky.
  - `randomScanReading` in generative test updated to include randomized Lighting lines so existing model-coherence tests exercise the new format.
- **Tests:** `./gradlew test --rerun` — BUILD SUCCESSFUL, all tests pass.
  - `lightBucketCoversAllLevels` — all 6 buckets at all boundary values.
  - `noLightSourcesReturnsEmpty`, `singleLightSourceFormatsCorrectly`, `multipleOfSameTypeDeduplicatesPositions`, `differentTypesAppearAsSeparateParts`, `lightingSummaryLineIsModelReadable` — pure-function coverage for both new helpers.
  - Model-coherence: `randomScanReading` now emits lighting lines; existing `personaConsistencyPassRate` and `noBannedPhrasePassRate` tests in `AdvisorPersonaGenerativeTest` will exercise model behavior against the new format when `run/client/.env` is present.
- **Coverage limitation:** `samplePos` integration path (actual block detection → lightSources population) requires a live `ServerLevel` and cannot be unit-tested. In-game confirmation required.
- **Not committed:** git access remains revoked per standing instruction.
- **Result:** PASS (compile- and test-suite-verified). In-game confirmation required.

---

## 2026-06-28 — Fix: scan_area tool description updated to reflect visual/terrain/lighting output

- **File:** `advisor/tools/ScanAreaTool.java` — `definition()` method, description string only
- **Root cause:** The scan_area description only mentioned entities, caves, and fixtures. The model consistently chose get_environment for "describe my surroundings" queries because scan_area gave no indication it returns terrain, block composition, or lighting. Live testing confirmed: outdoor queries always routed to get_environment.
- **Fix:** Updated description to lead with what the player can actually see (lighting, terrain, block composition) before entities and cave data. Added explicit guidance: "Use this tool when asked about surroundings, what is nearby, or what can be seen."
- **Tests:** `./gradlew test --rerun` — BUILD SUCCESSFUL. Tool description change has no unit-testable behavior; correct tool routing requires live model inference. In-game confirmation required.
- **Not committed:** git access remains revoked per standing instruction.
- **Result:** PASS (compile-verified). In-game confirmation required.

---

## 2026-06-28 — Fix: ScanAreaTool sky detection misidentifies outdoor-under-canopy as underground [ATTEMPT 1 — FAILED]

- **File:** `advisor/tools/ScanAreaTool.java` — `execute()` method, `underground` computation only
- **Root cause:** `player.serverLevel().canSeeSky(origin)` uses the `MOTION_BLOCKING` heightmap, which treats leaf blocks as opaque.
- **Fix attempt:** Replaced `canSeeSky()` with `MOTION_BLOCKING_NO_LEAVES` heightmap check.
- **Result:** FAILED in-game. `MOTION_BLOCKING_NO_LEAVES` still uses column height — a player at a cave entrance with open sky visible was still reported as underground because their Y was below the surface height in that column. Heightmap-based approaches are fundamentally wrong for this problem.

---

## 2026-06-28 — Fix: ScanAreaTool sky detection — use sky light level instead of heightmap

- **File:** `advisor/tools/ScanAreaTool.java` — `execute()` method, `underground` computation only
- **Root cause:** Both `MOTION_BLOCKING` and `MOTION_BLOCKING_NO_LEAVES` heightmaps check column height, which is wrong for cave entrances and ravines. A player at a large cave opening with sky clearly visible was still reported as underground.
- **Fix:** Changed to `level.getBrightness(LightLayer.SKY, origin) <= 0`. Sky light propagates through cave openings and through leaves — if sky light reaches the player, they are not underground regardless of what the column heightmap says. Works correctly for: surface (sky=15), under leaves (sky=15, leaves transparent), cave entrance (sky>0 from opening), deep cave (sky=0). Nether/End also report sky=0 which is appropriate.
- **Tests:** `./gradlew test --rerun` — BUILD SUCCESSFUL. Cannot unit-test without live `ServerLevel`. In-game confirmation required.
- **Not committed:** git access remains revoked per standing instruction.
- **Result:** PASS (compile-verified). In-game confirmation required.

---

## 2026-06-28 — Feat: ScanAreaTool reports player fluid state

- **File:** `advisor/tools/ScanAreaTool.java` — `execute()`, after Sky line
- **What changed:** Added fluid state reporting using `player.isInLava()`, `player.isUnderWater()`, `player.isInWater()`. Outputs "In fluid: lava", "In fluid: water (submerged)", or "In fluid: water" as appropriate. No line if player is not in fluid. `isWet()` was explicitly rejected — it fires on rain/snow and is not a reliable fluid contact check.
- **Tests:** `./gradlew test --rerun` — BUILD SUCCESSFUL. Requires live `ServerLevel` for integration test. In-game confirmation required.
- **Not committed:** git access remains revoked per standing instruction.
- **Result:** PASS (compile-verified). In-game confirmation required.

---

## 2026-06-28 — Fix: floodFill sky-exit detection also used canSeeSky()

- **File:** `advisor/tools/ScanAreaTool.java` — `floodFill()` method
- **Root cause:** Same bug as the main underground check — `canSeeSky()` heightmap approach misses cave openings and ravines. Sky exit count ("apparent exits to surface") was not firing correctly at cave entrances.
- **Fix:** Changed `player.serverLevel().canSeeSky(next)` to `player.serverLevel().getBrightness(LightLayer.SKY, next) > 0`. Consistent with the main underground detection fix.
- **Tests:** `./gradlew test --rerun` — BUILD SUCCESSFUL. Cannot unit-test without live `ServerLevel`. In-game confirmation required.
- **Not committed:** git access remains revoked per standing instruction.
- **Result:** PASS (compile-verified). In-game confirmation required.

---

## 2026-06-29 — Fix: Sky detection reports "sky visible" at cave entrances; below-floor probe no longer escapes through cave openings

- **File:** `advisor/tools/ScanAreaTool.java` — `execute()` (Sky line), new `checkNearbySkyAccess()` method, `scanUnderground()` (probe `hitSolid` logic)
- **Root cause 1 (Sky):** `LightLayer.SKY` at the player's exact block position returns 0 when stone or water overhead blocks the direct downward sky-light path, even when the player is at a cave entrance with sky clearly visible. Player at Y=N under an overhang: sky light at that block = 0, but air 2-3 blocks toward the opening has sky light > 0.
- **Fix 1 (Sky):** Added `checkNearbySkyAccess(ServerPlayer, BlockPos)`: BFS through connected air cells up to `CAVERN_SCAN_RADIUS`; returns true if any cell has `getBrightness(LightLayer.SKY) > 0`. `execute()` now produces three-way Sky text: `open` (player's origin has sky), `sky visible` (origin has no sky but BFS finds a sky-connected air cell), `enclosed` (BFS finds no sky connection). The `underground` flag and all downstream bucketing are unchanged — this only affects the reported Sky line.
- **Root cause 2 (Probe):** The downward probe's `hitSolid` flag was set by any `!isAir()` block, including water. A column of water above an open cave entrance (water→air at entrance level) set `hitSolid=true`, then marked the entrance air as a "below-solid" probe cell. The probe scan from those cells had line-of-sight to exterior grass and tree logs through the open entrance, producing `Below floor: grass(56) tree_log(7) tree_leaves(9)` for a player standing inside a water-filled cave — blocks that are only visible through the cave mouth, not below the floor.
- **Fix 2 (Probe):** Two-part fix. (a) Only non-fluid solid blocks set `hitSolid` — fluid blocks are skipped without setting the flag, so a water-then-air column no longer produces a probe cell. (b) Even after passing the solid-block check, probe cells with any sky-light access (`getBrightness(LightLayer.SKY) > 0`) are skipped — a probe cell with sky light is connected to the surface through an entrance, ravine, or crack and is not a sealed underground pocket. Both guards are needed: (a) prevents entrance air from being a probe cell via water; (b) prevents genuine sub-solid air cells that are still sky-connected (e.g. inside an open ravine) from producing exterior-block results. First live test after fix (a) alone confirmed (b) was still needed.
- **Tests:** `./gradlew test --rerun` — BUILD SUCCESSFUL, 96 tests, 0 failures (one `AdvisorPersonaGenerativeTest` live-API flake on the first `--rerun` attempt; two subsequent clean runs confirmed). No new unit tests added — both fixes require a live `ServerLevel`. Existing `ScanAreaToolTest` (`classifyVoid` thresholds, `lightBucket`, `summarizeLightSources`) unaffected.
- **Coverage limitation:** Both fixes require in-game confirmation: (1) cave-entrance position should now report `Sky: sky visible`; (2) `Below floor:` should no longer appear at a water-filled cave entrance with no genuine underground air pockets.
- **Not committed:** git access remains revoked per standing instruction.
- **Result:** PASS (compile- and test-suite-verified). In-game confirmation: below-floor probe no longer returns exterior blocks (grass/logs absent from output, confirmed live). Sky line confirmed reporting `sky visible`. Glow-lichen-as-sole-light-source attribution separately fixed; see next entry.

---

## 2026-06-29 — Fix: Lighting line credits sky as primary source when sky is accessible

- **File:** `advisor/tools/ScanAreaTool.java` — `execute()`, lighting summary block
- **Root cause:** The `Lighting:` line listed only block-based light sources (e.g. `glow lichen x1`). When the player is in an open ravine at noon with sky light providing brightness, the model saw one glow-lichen entry and credited it as the source, ignoring the `Sky: sky visible` line above.
- **Fix:** When `skyText` is not `"enclosed"`, the string `"sky"` is prepended to the light source list. Result: `Lighting: well-lit — sky, glow lichen x1 (at level)`. Enclosed caves are unchanged.
- **Tests:** `./gradlew test --rerun` — BUILD SUCCESSFUL. `summarizeLightSources` unit test unaffected.
- **Coverage limitation:** Requires in-game confirmation that the model correctly names sky as the light source in an open ravine at daytime.
- **Not committed:** git access remains revoked per standing instruction.
- **Result:** PASS. In-game confirmed — lava test: tool output `Lighting: bright — sky, lava x1 (at level)`; model response correctly named sky and lava as light sources. Ravine/cave-entrance sky-as-primary-source behavior also confirmed in prior session exchange.

---

## 2026-06-29 — Fix: Surface scan misses tree trunks and terrain on adjacent slopes

- **File:** `advisor/tools/ScanAreaTool.java` — `scanBlockDistribution()`, constants
- **Root cause 1 (tree trunks):** `surfaceY` was detected as the topmost solid non-fluid block in each column — in a forest that's the top of the tree canopy. Tree trunks below the canopy had `y < surfaceY` and hit the silent-drop path (neither `ground` nor `above` bucket). A spruce trunk standing right in front of the player was not reported.
- **Fix 1:** Surface Y detection now pierces through `tree_leaves` and `tree_log` blocks to find the actual ground surface (snow, grass, dirt, stone). Tree trunks and leaves are now correctly bucketed as `Above surface:`.
- **Root cause 2 (terrain on lower slopes):** `DENSE_Y_RANGE = 4` and `SPARSE_Y_RANGE = 8` were symmetric up/down. A player on a mountain peak with trees 15–20 blocks below was outside the scan's downward reach entirely — the tool returned only Sky/Lighting/Entities.
- **Fix 2:** Added `SURFACE_BELOW_RANGE = 20`. In non-underground mode, the dense scan extends down 20 blocks and the sparse scan extends down 20 blocks (both unchanged upward: +4/+8). Underground scan unaffected. Scan volume roughly doubles but these are infrequent per-query block reads, not per-tick.
- **Tests:** `./gradlew test --rerun` — BUILD SUCCESSFUL. No existing tests cover surface bucketing (requires live `ServerLevel`).
- **Coverage limitation:** Both fixes require in-game confirmation. Extreme terrain (mountain peak 30+ blocks above valley floor) may still be outside scan range.
- **Not committed:** git access remains revoked per standing instruction.
- **Result:** PASS (compile- and test-suite-verified). In-game confirmation required.

---

## 2026-06-29 — Fix: ScanAreaTool surface scan pierces thin snow layers in snowy biomes; debug block removed

- **File:** `advisor/tools/ScanAreaTool.java` — `scanBlockDistribution()` surface-Y detection loop; `samplePos()` debug block
- **Root cause (snow-pierce):** In snowy biomes, `Blocks.SNOW` (thin snow layer, not `snow_block`) accumulates on top of tree canopy. The per-column top-down surfaceY scan was stopping at the snow layer on the canopy top, setting `surfaceY` to canopy top + 1. All tree trunk blocks below that Y fell into the silent-drop path (neither `ground` nor `above` bucket), so spruce trunks were absent from scan output even with the tree-leaf/tree-log pierce fix already in place.
- **Fix:** `Blocks.SNOW` is now pierced in the surfaceY detection loop, before the tree-category check (which already pierces `tree_leaves` and `tree_log`). The actual ground surface (grass, dirt, podzol, snow_block) becomes `surfaceY`; trunks and canopy above it are correctly bucketed as `Above surface:`. Note: thin snow layers sitting on actual terrain ground will appear in `Above surface:` rather than `ground` for those columns — the substrate below is the effective ground cover for snowy columns.
- **Debug block removed:** `samplePos()` contained a temporary `[ScanArea-DBG]` logging block (7 lines) that logged tree_log/tree_leaves block positions and raycast hit details, left in place when the prior session was interrupted by `/codify`. This block has been removed. No behavior change — diagnostics-only.
- **Tests:** `./gradlew test --rerun` — BUILD SUCCESSFUL, 102 tests, 0 failures, 0 skipped.
- **Coverage limitation:** Surface-Y detection with snow piercing requires a live snowy-biome world position. In-game confirmation (standing next to a snowy spruce trunk; advisor scan should report trunk blocks in `Above surface:`) is the next step.
- **Not committed:** git access remains revoked per standing instruction.
- **Result:** PASS (compile- and test-suite-verified). In-game confirmation required.

---

## 2026-06-29 — Fix: ScanAreaTool surfaceY scan pierces vines (jungle/swamp biome parity)

- **File:** `advisor/tools/ScanAreaTool.java` — `scanBlockDistribution()` surface-Y detection loop
- **Root cause:** `Blocks.VINE` occupies grid positions in columns above tree trunks in jungle and swamp biomes. Same masquerade-as-surface-height mechanism as `Blocks.SNOW` in snowy biomes: the top-down surfaceY scan was stopping at the topmost vine in a column, setting surfaceY there and silently dropping all tree trunk blocks below it into the silent-drop path.
- **Fix:** Added `Blocks.VINE` to the pierce list alongside `Blocks.SNOW`, `tree_leaves`, and `tree_log`. Extracted a `pierceBlock` local to avoid double-calling `bs.getBlock()` for the two block-identity checks.
- **Tests:** `./gradlew test --rerun` — BUILD SUCCESSFUL, 102 tests, 0 failures, 0 skipped.
- **Coverage limitation:** Requires in-game confirmation in a jungle biome — a vine-covered tree should show trunk blocks in `Above surface:`.
- **Not committed:** git access remains revoked per standing instruction.
- **Result:** PASS (compile- and test-suite-verified). In-game confirmation required.

---

## 2026-06-29 — Live confirmation: surface scan outdoors (stone + torch + flowing water location)

- **No code change.** Live-test confirmation only.
- **Result:** PASS — advisor correctly described open sky, torch as light source at player level, stone ground cover, flowing water at surface level, no entities. Surface scan outdoors item confirmed.

---

## 2026-06-29 — Fix: scan_area block counts replaced with qualitative language; new identify_nearby tool

- **Files:** `advisor/tools/ScanAreaTool.java` (modified), `advisor/tools/IdentifyNearbyTool.java` (new), `advisor/ToolCallOrchestrator.java` (modified), `DragonTweaksV2.java` (modified)

### ScanAreaTool — qualitative block counts
- **Root cause:** `formatCounts` emitted raw integer counts (`stone(57)`, `grass(34)`) which the model reported verbatim as specific quantities ("fifty-seven stone blocks"), unnatural for an adventurer's voice.
- **Fix:** `formatCounts` now sorts by count descending and maps each to a qualitative label via new `static String quantify(int)`: ≥21 → "a lot of", ≥6 → "some", <6 → "a little". Output: "a lot of grass, some stone, a little dirt". Same treatment applied to the underground ore formatter in `scanUnderground`. Model receives relative language and produces natural prose rather than verbatim numbers.

### IdentifyNearbyTool — new focused rescan tool
- **Root cause:** `scan_area` categorizes all log types as `tree_log`, all stone variants as `stone`, etc. The model has no way to identify specific variants when asked ("what kind of log?"). Adding a dedicated tool avoids bloating `scan_area` further.
- **Design:** `identify_nearby(target)` accepts a natural-language target string, normalizes it (lowercase, strip trailing 's'), looks it up against a `LinkedHashMap<String, Predicate<BlockState>>` (exact match, then partial), scans radius-8 × height-8 with the same raycast/occlusion as `scan_area`, and returns actual in-game block display names with counts ("Found nearby: Spruce Log (4), Oak Log (2)"). Unknown targets return a recognized-categories hint. Initial lookup table: logs, wood (alias), stone, flowers, ores, crafting stations, furnaces, campfires, lily pads, mushrooms.
- **ToolCallOrchestrator:** Added "identify" category with signals `["what kind", "what type of", "which kind", "which type", "identify"]` placed first in CATEGORIES — before "location" and "scan" — so "what kind of logs are nearby" routes to identify_nearby rather than the location dual-tool pair.
- **DragonTweaksV2:** `IdentifyNearbyTool` added to the tool list passed to the orchestrator constructor.
- **Tests:** `./gradlew test --rerun` — BUILD SUCCESSFUL, 102 tests, 0 failures, 0 skipped. All pre-existing warnings (Config.java/DragonTweaksV2Client.java EventBusSubscriber deprecation, ScanAreaTool getLightEmission no-arg) are unchanged pre-existing issues, not introduced by this change.
- **Coverage limitation:** `IdentifyNearbyTool.execute()` requires a live `ServerLevel` — same pre-existing constraint as all other tool classes. The lookup table, normalization, and partial-match logic are not independently unit-tested (pure-logic methods are package-private but not yet exported to a test). In-game confirmation required for routing ("what kind of log") and identification accuracy (correct block display names returned).
- **Not committed:** git access remains revoked per standing instruction.
- **Result:** PASS (compile- and test-suite-verified). In-game confirmation required.

---

## 2026-06-29 — Fix: non-solid block detection — switch occlusion from VISUAL to COLLIDER in ScanAreaTool and IdentifyNearbyTool

- **Files:** `advisor/tools/ScanAreaTool.java` (modified), `advisor/tools/IdentifyNearbyTool.java` (modified)

### Root cause
`ClipContext.Block.VISUAL` returns `HitResult.Type.MISS` for any block whose visual shape is empty — this includes all non-solid decorative blocks: flowers, sugar cane, tall grass, vines, torches, lanterns, etc. The prior session added a bypass for light-emitting non-collision blocks specifically (torches), then an expanded bypass for all non-collision blocks (this session's first attempt), both of which required block-specific exceptions that would accumulate indefinitely as new block types were discovered.

### Fix
Replaced the block-type-specific bypass logic with a general occlusion strategy in both files:

**Old approach (VISUAL):**
- skip if hit == MISS (non-solid blocks always return MISS → always skipped)
- skip if hit.blockPos ≠ target

**New approach (COLLIDER + inverted condition):**
- skip only if hit ≠ MISS AND hit.blockPos ≠ target

`ClipContext.Block.COLLIDER` uses the collision shape, not the visual shape. Non-solid blocks (flowers, sugar cane, vines, torches) have no collision shape — the ray passes through them and returns `MISS`. `MISS` now means *reachable / nothing solid in the way*, not *invisible*. Solid blocks return a hit at their position; blocks occluded by solid geometry return a hit at the intervening block (pos ≠ target → skip). This handles all block types uniformly with no per-block exceptions.

The previous non-solid bypass block in `ScanAreaTool.samplePos()` (including air-adjacency guard and per-category bucketing) was removed entirely — the COLLIDER approach supersedes it.

`ClipContext.Fluid.NONE` is used in both files: fluids have no collision shape, so the ray passes through water/lava to reach the target. The fluid-detection path (`!fs.isEmpty()`) runs after the occlusion check and is unaffected.

- **Tests:** `./gradlew test --rerun` — BUILD SUCCESSFUL, 27 tasks, 0 failures.
- **In-game confirmation (2026-06-29):** Standing in a grassy plain next to sugar cane, dandelions, an oxeye daisy, and still water. (1) `scan_area` correctly reported "a single flower, some sugar-cane" alongside grass, dirt, and water — no per-block bypass needed. (2) `identify_nearby` on "flower" returned "There are dandelions and an oxeye daisy nearby" — specific display names, correct count. (3) A second `scan_area` call also detected short grass and tall grass (via display-name fallback), a crafting table (concept detection), and sugar cane again. All non-solid blocks now detected uniformly.
- **Not committed:** git access remains revoked per standing instruction.
- **Result:** PASS — compile, test-suite, and in-game confirmed.

---

## 2026-06-29 — Fix: add spawner to identify_nearby; fix modded block display names in scan_area

- **Files:** `advisor/tools/IdentifyNearbyTool.java` (modified), `advisor/tools/ScanAreaTool.java` (modified)

### identify_nearby — spawner entry
- **Root cause:** "what kind of spawner is it" routed correctly to `identify_nearby` via the "what kind" signal, but "spawner" had no entry in the TARGETS lookup table. The tool returned an unknown-target hint and the model produced three failed attempts.
- **Fix:** Added `TARGETS.put("spawner", bs -> bs.is(Blocks.SPAWNER));` to the TARGETS map.

### scan_area — modded block display names
- **Root cause:** `state.getBlock().getName().getString()` in `categorizeBlock`'s fallback path calls the translation system. On the server thread, client-side language files are not loaded, so modded blocks return their raw translation key (e.g. `block.domum_ornamentum.shingle`) instead of a human-readable name. Vanilla blocks are unaffected because they have compiled-in English names.
- **Fix:** Extracted `private static String friendlyName(BlockState)`. If `getName().getString()` returns a value containing dots and no spaces (signature of a translation key), it splits on `.`, takes the last segment, splits on `_`, and titlecases each word. Examples: `block.domum_ornamentum.shingle` → `Shingle`; `block.domum_ornamentum.shingle_slab` → `Shingle Slab`. Vanilla display names ("Oak Log", "Dirt") pass through unchanged.
- **Tests:** `./gradlew test --rerun` — BUILD SUCCESSFUL, 27 tasks, 0 failures.
- **Coverage limitation:** `friendlyName` logic is a static private method; not independently unit-tested. In-game confirmation required with Domum Ornamentum blocks present.
- **Not yet committed:** pending next commit cycle.
- **Result:** PASS (compile- and test-suite-verified). In-game confirmation required.

---

## 2026-06-30 — Redesign: identify_nearby general scan + enrichBlockEntity; TCO routing fixes

- **Files:** `advisor/tools/IdentifyNearbyTool.java` (rewritten), `advisor/tools/BlockUtil.java` (new), `advisor/tools/ScanAreaTool.java` (modified), `advisor/ToolCallOrchestrator.java` (modified), `advisor/ToolCallOrchestratorTest.java` (modified)

### identify_nearby redesign
- **Root cause:** The TARGETS `LinkedHashMap<String, Predicate<BlockState>>` is structurally limited — `Predicate<BlockState>` cannot access block entities, so questions like "what kind of spawner" return the display name only, not the mob type. It also required manual maintenance for every new block category and had broken singularization ("moss" → "mos", "glass" → "glas").
- **Fix:** Dropped the TARGETS table entirely. `execute()` now scans all visible non-air blocks in radius-8 × height-8 with the same `COLLIDER + NONE` occlusion as before, resolves each block name via `BlockUtil.friendlyName()`, enriches block entities via `enrichBlockEntity()`, and filters by whether the player's target string is a substring of the resolved name (case-insensitive). When target is empty, all visible blocks are returned. When a non-empty target matches nothing, returns "None found nearby."
- **enrichBlockEntity:** For `SpawnerBlockEntity`, reads spawn data via `saveWithoutMetadata(registryAccess())` NBT and extracts `SpawnData.entity.id`. Formats as "Monster Spawner (spawns Zombie)". Unknown block entities and API failures degrade gracefully to the display name via try/catch.
- **definition() updated:** `target` is now optional (removed from `required`). Description is generalized.

### BlockUtil.java
- Extracted `friendlyName(BlockState)` from `ScanAreaTool` into a shared static utility class in the same package. `ScanAreaTool` delegates to it. Zero behavior change.

### ScanAreaTool description
- Added disclaimer: "For specific identification — what type or kind of something is — use identify_nearby instead."

### ToolCallOrchestrator routing fixes
- **`includeHistory` removed from Category:** Dropped `includeHistory` field. `shouldIncludeHistory` now uses only explicit signal detection (`"you said"`, `"earlier"`, `"what about"`, `"tell me more"`). Queries with no conversational reference default to false.
- **Force-inject fix for identify_nearby:** `identify_nearby` filtered out of the force-inject list. When `identify` category fires with no round-1 tool call, falls through to the rt2 grounding prompt path instead of forcing an empty call.
- **Chitchat fallthrough fix:** Chitchat branch now checks `category.get().tools().isEmpty()` so identify-category misses fall through to rt2 rather than delivering rt1 text.
- **PERSONA_BIO anchor:** Added sentence directing the model to call `identify_nearby` when the player asks what type or kind of something is nearby.

### Tests
- `ToolCallOrchestratorTest.defaultIncludesHistory` renamed `defaultExcludesHistory`; assertions changed to `assertFalse` to match new signal-only behavior.
- **`./gradlew test --rerun`:** BUILD SUCCESSFUL, 27 tasks, 0 failures.
- **Coverage limitation:** `IdentifyNearbyTool.execute()` and `enrichBlockEntity()` require a live `ServerLevel`. Spawner mob-type enrichment and the signal-only history behavior need in-game confirmation.
- **Result:** PASS (compile- and test-suite-verified). In-game confirmation required.

---

## 2026-06-30 — Live confirmation: identify_nearby spawner enrichment

- **No code change.** Live-test confirmation only.
- **Test:** Asked "what kind of spawner am I looking at" near a MineColonies spawner.
- **Result:** PASS — advisor returned "It's a monster spawner that spawns Camparcherbarbarians." NBT-based mob type extraction via `saveWithoutMetadata(registryAccess())` confirmed working in-game with a modded entity type (`minecolonies:camparcherbarbarians`). Entity ID parsed and titlecased correctly.
- **Observation (not a bug):** "I hate being in the rain" (casual complaint, no question asked) produced a long unsolicited tip block about shelter, buckets, lightning, and waterproof gear. PERSONA_BIO instructs against padding with extra observations nobody asked for — model is not holding to it for casual complaints. Model behavior issue; no code fix available.

---

## 2026-06-30 — Fix: AdvisorChatHandler filters `--` dev comments before advisor pipeline

- **File:** `advisor/AdvisorChatHandler.java` — `onServerChat()`
- **Root cause:** Messages prefixed with `--` are dev comments intended for Claude Code, not for the advisor. Previously the advisor received and responded to them, causing persona slips (e.g. "Sorry about that, I'll keep it plain." in response to `-- incorrect presentation`).
- **Fix:** Extracted `message` from `event.getMessage().getString()` at the top of `onServerChat`. Added early return `if (message.startsWith("--")) return;` before any advisor processing. The message still appears in normal game chat; the advisor pipeline never sees it.
- **Tests:** `./gradlew test --rerun` — BUILD SUCCESSFUL, 27 tasks, 0 failures.
- **Coverage limitation:** No unit test for this path (requires live `ServerChatEvent`). In-game confirmation required.
- **Result:** PASS (compile- and test-suite-verified). In-game confirmation pending.

---

## 2026-06-30 — Live confirmation: -- dev comment filter

- **No code change.** Live-test confirmation only.
- **Test:** Typed `-- testing the filter` in game chat with build tool in inventory.
- **Result:** PASS — log shows `[Advisor] chat received from Dev` then stops; no `[DT_CHAT]`, no build tool check, no advisor pipeline. Message appeared in game chat normally. Filter working as intended.

---

## 2026-06-30 — Live confirmation: player fluid state line (test 1)

- **No code change.** Live-test confirmation only.
- **Test:** Asked "what do i see" while submerged in water, then again on dry land.
- **Result:** PASS — `In fluid: water (submerged)` appeared in scan output after Sky line when submerged. Line absent when player was on dry land. Advisor response naturally incorporated being in water ("The area's mostly water… two salmon and two squids swimming nearby"). Correct presence/absence behavior confirmed.

---

## 2026-06-30 — Live confirmation: MineColonies food lore injection (test 2)

- **No code change.** Live-test confirmation only.
- **Test:** Asked "sht dues my colony need for food" (typo-laden; lore still matched).
- **Result:** PASS — `[Advisor] lore matched: food` fired immediately. Advisor response drew correctly from MineColonies food lore: tier-specific biome crops (cold/temperate/humid/dry), Cookery with Chef, Restaurant with Cook, Bakery with Baker. No fabrication; all MineColonies-specific content accurate.

---

## 2026-06-30 — Live confirmation: build-tool spawn gate (test 4)

- **No code change.** Live-test confirmation only.
- **Test:** Logged into a fresh world without a build tool in inventory.
- **Result:** PASS — `[DragonTweaks]` build tool hint fired unconditionally (expected). Chat handler correctly blocked advisor pipeline on "hi" and subsequent messages (no build tool found, hotbar: bread + ability bottle). No advisor entity spawned. Dragon confirmed in-game: "no advisor yet."

---

## 2026-06-30 — Fix: needs/food.md lore rewritten for immersion and conciseness

- **File:** `docs/minecolonies-lore/needs/food.md`
- **Root cause:** Lore document used game-mechanic language ("tiered crops", "tiers", "satisfaction penalty") and structured markdown (section headers, bullet lists) that caused the model to produce a wordy, game-mechanic-flavored response.
- **Fix:** Rewrote as flowing prose. Removed all section headers and bullet lists. Replaced "tiered crops" with "crops suited to the local climate". Replaced tier-satisfaction language with "colony-grown meals satisfy citizens better than food the player hands them directly". Retained all factual content (production chain, citizen hunger, climate-based crops, Restaurant requirement).
- **Tests:** `./gradlew test --rerun` — BUILD SUCCESSFUL, 27 tasks, 0 failures. No unit test coverage for lore content; in-game re-test required to confirm response quality improvement.
- **Result:** PASS (compile- and test-suite-verified). In-game confirmation pending.
