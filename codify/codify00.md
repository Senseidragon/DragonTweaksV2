# Codify — Consolidated Session Snapshot

## Metadata
- Consolidated: Mon Jun 29 07:45 UTC 2026
- Supersedes: archive/codify00.md through archive/codify07.md (eight prior snapshots)
- Project root: C:\Users\sense\Desktop\DragonTweaksV2
- Branch: `advisor-persona-grounding`

---

## Current Project Status

All work is uncommitted. Git access is revoked — no commands of any kind until Dragon explicitly confirms ALL live testing has passed. Merge of this branch remains blocked for the same reason.

`./gradlew test` was last run and passed (96 tests, 0 failures) in the session captured by archive/codify06.md. The current session (archive/codify07.md) applied one new fix and was interrupted before a test run.

---

## Outstanding Work — Priority Order

### Immediate (current session, before next client launch)
1. **Remove `[ScanArea-DBG]` logging block** from `samplePos` in `ScanAreaTool.java` (lines ~344–351). This temporary debug block was left in by `/codify` interrupt and must be removed before live testing.
2. **Run `./gradlew test --rerun`** to verify the snow-pierce fix does not break existing tests.
3. **Append `test-audit-trail.md` entry** for the snow-pierce fix.

### Live Tests Pending (unit-verified only, not in-game confirmed)
- **Surface scan / tree trunk detection** — primary. Snow-pierce fix applied this session: `if (bs.getBlock() == Blocks.SNOW) continue;` added to surfaceY detection before the tree-category check. Expect "Above surface: tree_log(N)" to appear next to a snowy-biome spruce tree.
- **Player fluid state line** — `isInWater()` / `isUnderWater()` / `isInLava()` line after Sky in scan output.
- **MineColonies food lore** — ask advisor about colony food; verify lore injection fires (LoreIndex entry `needs/food` added).
- **Surface scan outdoors** — normal terrain and high mountain peak (valley trees below player).
- **Sycophancy fix** — PERSONA_BIO line "What the tools show you is what you know…" not yet live-tested.
- **Build-tool spawn gate** — `AdvisorEntityManager.onPlayerLogin` now gates spawn on `hasBuildTool(player)`.

### Deferred Design Proposals
- **#5 — "see" keyword overlap:** The bare `"see"` signal in the `location` category (`ToolCallOrchestrator.java`) causes chitchat containing "see" to force-inject `get_environment + scan_area`. Real, confirmed bug. Fix design not yet written.
- **#6 — Persona 4th-wall slip fix:** Target ambiguous within a 3-item bundle (lore docs, validation checklist PV-03/04/05, persona 4th-wall). Not started.
- **Section E — Response-phrasing scope:** "no ladder in this room" vs. "no ladder nearby" — per-spec deferred.
- **wiki-ref scaling:** Markdoc convention resolved and applied to one file (`needs/food.mdoc`). 61 remaining files identified as good subagent candidate, not yet dispatched.
- **Finding #4 — chain-of-thought leak:** Unreproduced; raw pre-strip logging in `OpenRouterService.parseOpenRouterResponse` is in place for capture. Keep-monitor-only.
- **Two truncation failures** observed during birch-forest test session — not investigated.
- **Hallucinated terrain detail** in `get_environment` responses (podzol, ferns not from tool data) — noted, not addressed.
- **MineColonies dev-memory defect:** `.memsearch/memory/domains/minecolonies/approved/` has naming-resolution defects (cross-referenced names rendered as bare IDs). Confirmed in 2 of ~60 approved files. Dev-facing only, deprioritized per Dragon's explicit correction.

---

## Implemented and Confirmed (do not re-derive)

### ScanAreaTool.java — Full Redesign

**Sky and underground detection:**
- `underground = getBrightness(LightLayer.SKY, origin) <= 0` (v2 — confirmed live; prior heightmap approaches failed)
- BFS sky-exit: same `LightLayer.SKY` check inside `floodFill()` for per-block sky exclusion
- If not underground: BFS through connected air — if any cell has sky > 0, report "sky visible" (cave entrance case)

**Block distribution scan:**
- Dense zone: radius 0–4, dy `(-denseBelow)..+DENSE_Y_RANGE`, every block
- Sparse zone: radius 5–8, dy `(-sparseBelow)..+SPARSE_Y_RANGE`, every-other-column `(|dx|+|dz|)%2==0`
- Surface mode: `denseBelow = sparseBelow = SURFACE_BELOW_RANGE = 20`
- Underground mode: `denseBelow = DENSE_Y_RANGE = 4`, `sparseBelow = SPARSE_Y_RANGE = 8`

**Occlusion:** `Level.clip()` raycast from player eye to block center; blocks where hitPos ≠ pos are skipped.

**Surface bucketing (outdoor only):**
- `surfaceY` map: per-column top-down scan, pierces `tree_leaves`, `tree_log`, and `Blocks.SNOW` (thin snow layer — **this session's fix**, confirmed unit-test needed)
- `y == surfaceY` → `dist.ground`; `y > surfaceY` → `dist.above`; `y < surfaceY` → silent drop (intentional)

**Underground:** flat `dist.visible` bucket; no ground/above bucketing.

**Below-floor probe:**
- Downward probe from `origin` to `origin.getY() - 20`
- Skips cells with sky > 0 (exterior surface cells filtered out)
- Per probe air cell, runs `samplePos` with dy range `-4..0` (downward only)
- Blindness/light gates skipped ("sensing, not seeing")

**Light sources:**
- No-collision light emitters (torch, soul torch, glow lichen, etc.) bypass raycast if `bs.getCollisionShape(level, pos).isEmpty()`
- `getLightEmission()` (no-arg) used — intentional; not writing against badly-behaved mods
- `lightSources` list per BlockDistribution; `lightSourceType`, `lightBucket`, `verticalRelation`, `summarizeLightSources`
- Output: `Lighting: <bucket> [— <sources>]`; sky prepended as a source when not enclosed

**categorizeBlock:** 20 categories (terrain types, vegetation, ores via `Tags.Blocks.ORES_*`, light sources) + fallback to `state.getBlock().getName().getString()` — no block is silently null-returned.

**classifyVoid:** `Large cave` is the open-ended top bucket (≥1000). "Massive cavern" removed.

**Cavern BFS:** `CAVERN_SCAN_RADIUS=20`, sea-level pre-filter at `SEA_LEVEL=63`, per-block sky exclusion (not whole-region invalidation), additive-only "apparent exit" note.

**Concept detection:** 8 concepts (ladder, bed, door, stairs, crafting table, furnace, chest, barrel) via `TagKey<Block>`. ProjectE transmutation table registered conditionally via `ModList.isLoaded("projecte")`.

**Player fluid state:** `isInWater()` / `isUnderWater()` / `isInLava()` line after Sky in output. `isWet()` rejected (fires on rain/snow).

**Tool description:** leads with visual/terrain/lighting content; explicit "Use this tool when asked about surroundings, what is nearby, or what can be seen." — confirmed routing fix in-game.

### ToolCallOrchestrator.java

- Classification table: village / environment / inventory / status / scan / location / chitchat
- Round-1 miss with known category → force-inject that category's tools deterministically
- PERSONA_BIO: 7 sentences — seasoned adventurer, plain speech, no lists/dashes/headers, no padding, honesty/no-guessing, world-boundary, anti-sycophancy ("What the tools show you is what you know…")
- Per-line "[DT_TOOL] [tool-name] line" logging in `executeTools` (replaces single-line truncation)

### AdvisorEntityManager.java

- `onPlayerLogin`: gated on `AdvisorChatHandler.hasBuildTool(player)` before spawning `AdvisorEntity`
- First-join `BUILD_TOOL_HINT` in `DragonTweaksV2.java` is independent (still fires unconditionally)
- Known limitation: no mid-session acquisition listener (player who crafts the build tool after login doesn't get an entity until next login — accepted as out-of-scope; entity is currently dormant scaffolding)

### LoreIndex / syncLoreFromDocs

- `syncLoreFromDocs` generalized to multi-root (`docs/minecraft-lore`, `docs/minecolonies-lore`)
- 92 entries loaded at runtime (was 91); new entry: `needs/food` (MineColonies food lore)
- `docs/minecolonies-lore/needs/food.md` created; source/scrape-date fields marked unknown

### Test Suite

- 96 tests, 0 failures (last confirmed run: archive/codify06.md session)
- `ScanAreaToolTest`: classifyVoid buckets, lightBucket, lightSources formatting
- `ToolCallOrchestratorTest`: personaBioInstructsAgainstListFormatting
- `AdvisorPersonaGenerativeTest`: parallelized (15.81s vs 76.19s prior)

---

## Carry-Forward Technical Notes

**Snow-pierce root cause (this session):** In snowy biomes, thin snow layers (`Blocks.SNOW`) accumulate on top of tree canopy. The surfaceY scan was hitting the snow layer at the canopy top and stopping there — setting surfaceY to canopy top + 1, causing all trunk blocks (y < surfaceY) to be silently dropped. Fix: pierce `Blocks.SNOW` (not `snow_block`) before the tree-category check. Note: thin snow on the actual ground surface will now appear in `dist.above` rather than `dist.ground` for those columns; the actual terrain substrate becomes the "ground cover" for snowy columns.

**Multi-line tool-result logging:** Log4j only prefixes the first line of a multi-line message. Use `[DT_TOOL]`-tagged per-line logging (already implemented in `executeTools`) for grep-reliable live-log inspection. Raw log reading via `Read` with explicit line ranges is required to see full tool output.

**Markdoc convention (wiki-ref):** Resolved on `needs/food.mdoc`. Key rules: self-referencing building tags resolve to their display name (from frontmatter `name:`); cross-referenced building tags require lookup in the respective `.mdoc` file's `name:` frontmatter; worker names have no registry — infer Titlecase and flag as inferred; unresolvable item/research-link tags become content gaps, never fabricated; dynamic content tags (e.g. `{% food_list /%}`) are flagged as gaps. Full 10-rule table is in archive/codify04.md Session Topics Developed #29–30.

**Permission boundary:** A settings-level deny rule on `scripts/` cannot be overridden by conversational authorization alone. Dragon must adjust `.claude/settings.local.json` directly.

---

## Standing Process Constraints

(All captured in memory — listed here as a reminder index, not authoritative source)
- No git commands at all (see `feedback_git_access_revoked.md`)
- No PowerShell unless Bash genuinely cannot do it (see `feedback_no_powershell.md`)
- No compound shell commands: `&&` and `;` prohibited; `|` permitted (see `feedback_no_compound_shell_commands.md`)
- No client launch without explicit "go" from Dragon
- Clear run/client/logs/ before each client launch
- Pre-flight checklist required before any Java edit
- `./gradlew test --rerun` must pass + audit trail entry before any change is complete
- No source scanning without explicit Dragon authorization
- Check in immediately when Dragon interjects mid-task

---

## Next Recommended Action

Remove `[ScanArea-DBG]` debug block from `samplePos`, run `./gradlew test --rerun`, write audit trail entry, then wait for Dragon's "go" to launch client for the tree-trunk surface scan live test.
