# Codify Session Snapshot

## Metadata
- Created at: Mon Jun 29 07:45 UTC 2026
- Project root: C:\Users\sense\Desktop\DragonTweaksV2
- Command invoked: /codify
- Additional user arguments: none

## Current Project Status

Branch: `advisor-persona-grounding`. No git commands authorized. No test run this session (not authorized yet). The session is a live-testing loop on `ScanAreaTool.java` — the environmental scan tool that feeds context to the advisor LLM.

**Confirmed fixed and live-verified (prior sessions):**
- Sky detection: reports "sky visible" at cave entrance via BFS through connected air cells
- Below-floor probe: no longer returns exterior grass/logs; sky-light filter eliminates surface-connected cells
- Lighting line: "sky" credited as light source outdoors (not just block-based sources)

**Active bug — fix applied this session, NOT yet live-tested:**
- Surface scan silently drops tree trunk blocks in snowy biomes. Root cause confirmed: thin snow layers (`Blocks.SNOW`) accumulate on top of tree canopy in snowy biomes. The surfaceY detection scan hits a snow layer at the canopy top (e.g., y=181) and sets surfaceY to that value — all trunk blocks below it have `y < surfaceY` → silently dropped (no bucket). Fix: pierce through `Blocks.SNOW` (thin layer only, not `snow_block`) in the surfaceY detection loop, so the scan continues past the snow-capped canopy to find the actual terrain surface below the tree.

**Debug logging still in code:** Temporary `[ScanArea-DBG]` block in `samplePos` was NOT yet removed when Dragon interrupted with `/codify`.

## Current Task List

| Status | Task |
|--------|------|
| IN PROGRESS | Remove `[ScanArea-DBG]` debug logging from `samplePos` in `ScanAreaTool.java` |
| PENDING | Dragon authorizes client launch for live test of tree-trunk fix |
| PENDING | Confirm "Above surface: tree_log(N)" appears in scan output next to a snowy-biome tree |
| PENDING | Run `./gradlew test --rerun` and confirm all tests pass |
| PENDING | Append audit trail entry for surface scan snow-pierce fix |
| DEFERRED | Live test: high mountain peak scan (valley trees below player) |
| DEFERRED | Live test: sycophancy fix in PERSONA_BIO |
| DEFERRED | Live test: MineColonies food lore entry |
| DEFERRED | Live test: build-tool spawn gate on AdvisorEntityManager |
| DEFERRED | Live test: outdoors surface scan in normal (non-snowy) terrain |
| DEFERRED | Design proposal: "see" keyword overlap in `location` category |
| DEFERRED | Design proposal: persona 4th-wall slip fix |
| DEFERRED | Raise AdvisorEntityManager unconditional-spawn-vs-privacy-gate tension |

## Session Topics Developed

**Root cause identification — tree trunks silently dropped in surface bucketing:**
- Debug logging (added in prior session) confirmed trunk blocks at x=180, y=175–179, z=-168 DO pass raycast (`hitType=BLOCK`, `hitPos==pos`)
- `BlockDistribution.addAbove` confirmed correct — no filtering, just `above.merge(cat, 1, Integer::sum)`
- Bucketing code path traced: `surf != null && y == surf` → ground; `surf == null || y > surf` → above; else → SILENT DROP
- Root cause: snowy biome places thin snow layers on top of all solid blocks including tree canopy tops. Surface detection finds snow layer at canopy top → sets `surfaceY = canopy_top + 1` → trunk at y < surfaceY → silently dropped
- Fix: add `if (bs.getBlock() == net.minecraft.world.level.block.Blocks.SNOW) continue;` BEFORE the category-based pierce check in the surfaceY detection loop. Thin snow layer is pierced; full `snow_block` (actual terrain) stops the scan normally.

**Test coverage gap (Dragon feedback):**
- Dragon noted the existing test suite clearly does not catch the scan tool's ability to detect a tree. This was raised as a design flaw: surface scan testing is inadequate.
- Unit-testing `samplePos` / `scanBlockDistribution` requires a live `ServerLevel` (NeoForge constraint). Cannot be unit-tested directly without mocking the server-level API.
- Issue is open — no resolution reached this session.

## Files Discussed Or Modified

| File | Status |
|------|--------|
| `src/main/java/.../advisor/tools/ScanAreaTool.java` | modified |
| `src/main/java/.../advisor/ToolCallOrchestrator.java` | inspected (from prior session, loaded in context) |

**Change applied to `ScanAreaTool.java` this session:**
In the surfaceY detection loop (surface mode, `!underground` branch), added before the category-based pierce check:
```java
// Thin snow layers (Blocks.SNOW) accumulate on canopy tops in snowy
// biomes and would otherwise masquerade as the terrain surface.
// Full snow blocks are genuine terrain and stop the scan.
if (bs.getBlock() == net.minecraft.world.level.block.Blocks.SNOW) continue;
```

**Debug logging NOT yet removed:** `[ScanArea-DBG]` block in `samplePos` (lines ~344–351) is still present.

## Violations, Corrections, And User Directives

- Dragon: "your test for trees clearly sucks ass. If you can't confirm that the scan tool can detect a damn tree right in front of it, you are testing wrong." — surface scan test coverage is inadequate; tests must actually exercise tree detection, not just pass a suite that never hits the new code path.
- Prior-session standing directives (still active):
  - No git commands at all (git access revoked while bugs remain on branch)
  - No PowerShell — use Bash only
  - No compound shell commands (`&&`, `;` prohibited; `|` permitted)
  - Do not launch the client without explicit "go" from Dragon
  - Clear logs folder before each client launch
  - Do not claim verification complete if `./gradlew test` has not been run

## Decisions Made

- Root cause of tree-trunk drop: thin snow layer on canopy top sets `surfaceY` too high → all trunk blocks `y < surfaceY` → silent drop.
- Fix strategy: pierce `Blocks.SNOW` (thin layer) in surfaceY detection; do NOT pierce `Blocks.SNOW_BLOCK` (actual terrain). This is precise and avoids misclassifying true snow terrain.
- Debug logging to be removed before final test run (not removed yet due to Dragon's /codify interrupt).

## Deferred / Not Yet Implemented

- Test coverage for `samplePos` / `scanBlockDistribution`: NeoForge constraint prevents direct unit testing; no resolution. Needs a design decision on how to test surface bucketing without a live server level.
- All live tests beyond tree-fix confirmation (see task list above).
- Persona grounding fixes, MineColonies lore, spawn gate, all deferred pending tree-fix confirmation.

## Carry-Forward Context

**Key invariants:**
- `underground = (getBrightness(LightLayer.SKY, origin) <= 0)` determines which bucket path blocks go to
- `surfaceY` map: keyed by `columnKey(x, z)` → `(long)x << 32 | (z & 0xFFFFFFFFL)`, value is the Y of the topmost non-air, non-fluid, non-vegetation, non-thin-snow block
- Surface bucketing: `y == surfaceY` → `dist.ground`; `y > surfaceY` → `dist.above`; `y < surfaceY` → SILENT DROP (intentional but dangerous)
- Scan constants: `DENSE_RADIUS=4`, `SPARSE_RADIUS=8`, `DENSE_Y_RANGE=4`, `SPARSE_Y_RANGE=8`, `SURFACE_BELOW_RANGE=20`
- In surface mode: `denseBelow = SURFACE_BELOW_RANGE`, `sparseBelow = SURFACE_BELOW_RANGE`

**State of code:**
- Snow-pierce fix: APPLIED
- Debug logging `[ScanArea-DBG]`: STILL IN CODE, must be removed before live test
- Tests: not run this session

**Mental model of remaining risk:**
- The snow-pierce fix is correct for snowy biomes, but may cause thin snow on the actual ground surface (at terrain level, not canopy) to go to `dist.above("snow")` instead of `dist.ground("snow")`. The terrain surface for a snow-covered column will now be the block UNDER the snow layer (dirt, stone, etc.). This is arguably more accurate but changes what "Ground cover" reports in snowy biomes: shows terrain substrate, not snow layer. If Dragon wants snow layer as ground cover, the fix strategy needs revisiting.

## Next Recommended Action

Remove the `[ScanArea-DBG]` debug logging block from `samplePos` in `ScanAreaTool.java`, then wait for Dragon to authorize the client launch for live confirmation of the tree-trunk fix.
