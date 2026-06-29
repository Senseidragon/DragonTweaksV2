# ScanAreaTool Redesign — Full Environmental Scan

**Date:** 2026-06-26  
**Mod:** DragonTweaksV2 (NeoForge 1.21.1)  
**Branch:** advisor-persona-grounding  
**Status:** Design confirmed; not yet implemented.

**Supersedes:** `docs/superpowers/specs/2026-06-24-scan-area-concept-detection-design.md` (Sections A–D of that spec are already implemented; this spec replaces their output model with a richer one and fixes the sky-access underground trigger)

**Root cause of current failure (observed 2026-06-26 live testing):**  
`scan_area` returns "Nothing notable detected nearby." in virtually every situation — outdoors in a forest, underground in a cave with torches and coal veins — because its concept detection only checks 4 block types (ladder, bed, door, stairs). The tool gives the model nothing to work with. The model then either parrots "nothing notable" (correct but useless) or hallucinates terrain from biome inference when challenged (sycophancy — separate bug).

---

## Design Principle: Three Separate Layers

The previous design conflated data collection, output formatting, and model interpretation into a single sparse string. This spec separates them explicitly.

**Layer 1 — Data collection** (`ScanAreaTool.java`):  
Algorithm. BFS, per-column surface detection, block distribution sampling, fluid detection, sky-access check, cave morphology, ore detection. Produces structured in-memory data. No formatting.

**Layer 2 — Tool output format** (the string `scan_area` returns to the model):  
Formats Layer 1's structured data into labeled, readable fields the model can reason over. Rich enough that the model needs to infer or invent nothing. Tunable independently of the algorithm.

**Layer 3 — Model interpretation instructions** (`PERSONA_BIO` / system prompt):  
Tells the model how to turn the tool output into natural prose. Tunable independently of the tool. Addressed in Section E.

---

## Section A — Underground vs Surface Detection (replaces sea-level Y=63 filter)

**Current:** `scanUnderground` skips any void seed at or above `Y=63`.  
**Problem:** Sea-level heuristic fires in the wrong situations (above-ground caves, valley terrain) and misses the right ones (enclosed structures above sea level).

**New trigger:** `level.canSeeSky(playerPos)` at the player's actual block position.

- `canSeeSky` returns `true` → player is on an open surface → run the surface block distribution scan (Section B). Do not run cave morphology detection.
- `canSeeSky` returns `false` → player is underground or inside a structure → run both the block distribution scan AND the cave morphology + ore detection from the existing implementation (with its sky-exclusion and player-centered radius bound already in place from the 2026-06-24 implementation).

This replaces the Y=63 check in `scanUnderground`'s seed loop. Cave morphology and ore detection are now **always triggered when the player cannot see the sky**, not only when a void seed happens to be below sea level.

---

## Section B — Block Distribution Scan (new core capability)

**Always runs**, regardless of Section A's surface/underground result. Provides the raw environmental picture the model needs to describe what the player sees.

### Scan Geometry

Two concentric zones centered on the player's block position:

**Dense inner zone:**
- Horizontal: all (x, z) within radius 4 (cylindrical, `sqrt(dx²+dz²) ≤ 4`)
- Vertical: player.y − 4 to player.y + 4
- Sample: every block position
- Approximate non-air block count: ~380

**Sparse outer zone:**
- Horizontal: radius > 4 and ≤ 8
- Vertical: player.y − 8 to player.y + 8
- Sample: every other block — sparse grid, e.g. `(|dx| + |dz|) % 2 == 0` or similar alternating pattern
- Skips air blocks; non-air count substantially reduced vs exhaustive

Both zones are **bounded by solid surfaces** — the iteration does not pierce walls, but the sampling grid still generates candidate positions. Blocks that fall inside a solid mass (e.g. deep underground, inside a hill) are sampled normally; the per-column surface detection (below) determines their layer assignment, not their inclusion in the scan.

### Per-Column Surface Detection

"Surface" is **not a fixed y offset**. The terrain is not flat.

For each (x, z) column within the scan radius:
- Scan from the top of the y range downward within that column
- The first non-air, non-fluid block found is the **surface block** for that column
- If no such block exists in the y range, the column has no surface in range (e.g. the player is at the edge of a cliff and the bottom is out of range)

This correctly handles slopes, cliffs, hills, craters, dunes: a column 4 blocks to the north at the top of a hill contributes a surface 3 blocks above the player; a column 4 blocks to the south at the bottom of a cliff contributes a surface 4 blocks below. The ground layer follows actual terrain topology.

### Block Layer Assignment

Each sampled non-air block is assigned to one of two output buckets:

**Ground bucket** (`surface`): the surface block itself for each (x, z) column — the topmost solid block in that column's y range. Captures what the player is walking on and the terrain types immediately surrounding them.

**Above-surface bucket** (`above`): all non-air, non-fluid blocks above the surface block in a given column, within the y range. Captures trees, structures, torches on walls, crops, plants, overhanging terrain.

Fluid blocks (water, lava) are categorized separately — see Section C — and appear in their own output field, not in the surface or above-surface counts.

### Block Categorization

Blocks are mapped to semantic category strings for the output. Use `BlockTags` and `net.neoforged.neoforge.common.Tags.Blocks` where available; fall back to direct `Blocks.*` checks where no suitable tag exists.

**Minimum required categories** (expand as implementation confirms tag availability against pinned NeoForge 21.1.230 / Parchment 2024.11.17):

| Category string | Blocks / tags |
|---|---|
| `grass` | `Blocks.GRASS_BLOCK`, `Blocks.PODZOL`, `Blocks.MYCELIUM` |
| `dirt` | `BlockTags.DIRT` |
| `stone` | `BlockTags.STONE_ORE_REPLACEABLES`, `Blocks.COBBLESTONE`, `Blocks.STONE` |
| `sand` | `BlockTags.SAND` |
| `gravel` | `Blocks.GRAVEL` |
| `ice` | `BlockTags.ICE` |
| `snow` | `BlockTags.SNOW` (includes `SNOW_BLOCKS`) |
| `mud` | `Blocks.MUD`, `Blocks.MUDDY_MANGROVE_ROOTS` |
| `tree_log` | `BlockTags.LOGS` |
| `tree_leaves` | `BlockTags.LEAVES` |
| `planks` | `BlockTags.PLANKS` |
| `stone_brick` | `BlockTags.STONE_BRICKS` |
| `light_source` | check `state.getLightEmission() > 0` (catches torches, lanterns, glowstone, sea lanterns, etc.) |
| `crops` | `BlockTags.CROPS` |
| `flower` | `BlockTags.FLOWERS` |
| `ore_coal` | `Tags.Blocks.ORES_COAL` |
| `ore_iron` | `Tags.Blocks.ORES_IRON` |
| `ore_gold` | `Tags.Blocks.ORES_GOLD` |
| `ore_diamond` | `Tags.Blocks.ORES_DIAMOND` |
| `ore_emerald` | `Tags.Blocks.ORES_EMERALD` |
| `ore_redstone` | `Tags.Blocks.ORES_REDSTONE` |
| `ore_lapis` | `Tags.Blocks.ORES_LAPIS` |
| `ore_copper` | `Tags.Blocks.ORES_COPPER` |

Blocks that match no category are silently skipped — they contribute nothing to the distribution. The list above is a minimum; additional categories may be added at implementation time if useful tags are found for them.

**Implementation note:** check the ore tags before the generic stone tags, so a coal ore block is counted as `ore_coal`, not double-counted as `stone`.

---

## Section C — Fluid Detection

Fluids are detected separately from the block distribution, because fluid blocks often occupy the same position as a block (waterlogged), and their character (still vs flowing) carries different meaning.

For each sampled position in both zones:
- Retrieve `level.getFluidState(pos)`
- If non-empty:
  - `Fluids.WATER` or `Fluids.FLOWING_WATER`, `FluidState.isSource() == true` → count toward `still_water`
  - `Fluids.WATER` or `Fluids.FLOWING_WATER`, `FluidState.isSource() == false` → count toward `flowing_water`
  - `Fluids.LAVA` or `Fluids.FLOWING_LAVA`, `isSource() == true` → count toward `still_lava`
  - `Fluids.LAVA` or `Fluids.FLOWING_LAVA`, `isSource() == false` → count toward `flowing_lava`

Fluid counts are emitted as a separate `fluids:` field in the output (Section D), not merged into the surface/above-surface distributions.

---

## Section D — Tool Output Format (Layer 2)

The string returned by `scan_area` to the model. Structured, labeled, counts-based. The model reads this and synthesizes prose — it does not receive a pre-written description.

**Example — surface forest, no cave:**
```
Sky: open
Ground cover: grass(38) dirt(14) stone(3)
Above surface: oak_log(21) birch_log(9) leaves(52) light_source(2)
Fluids: none
Entities: none
```

**Example — underground cave with water, torches, coal:**
```
Sky: enclosed
Ground cover: stone(44) gravel(8)
Above surface: stone(112) light_source(5)
Fluids: flowing_water(14)
Ores: coal_ore(6)
Cave: Small cave (~380 blocks), apparent exit to surface (1 opening)
Entities: none
```

**Example — surface, by a frozen river:**
```
Sky: open
Ground cover: grass(22) ice(18) snow(11) stone(6)
Above surface: spruce_log(14) leaves(31) light_source(0)
Fluids: still_water(4)
Entities: none
```

**Format rules:**
- Each field on its own line, label followed by colon
- Omit fields with zero / empty values entirely (no "Ores: none" clutter)
- Counts in parentheses after category name, space-separated within a field
- `Sky:` is always present (`open` or `enclosed`)
- `Entities:` is always present (may be `none`)
- `Cave:` only present when sky is enclosed and cave detection ran
- `Ores:` only present when ores were detected

**Backward compatibility:** the existing `PERSONA_BIO` rule "never reference tools, scans, or internal mechanisms" still applies. The model must not quote or reference these field names in its response — it synthesizes from them.

---

## Section E — Model Interpretation Instructions (Layer 3)

Additions to `PERSONA_BIO` or the system prompt to guide the model in using the new tool output. These address two problems observed in 2026-06-26 live testing:

**E.1 — Standing by tool-grounded answers under challenge (sycophancy fix):**  
The model currently hallucinates terrain details ("grass and trees") when a player challenges a correct empty-scan result. Proposed addition to `PERSONA_BIO`:

> "What the tools show you is what you know. You don't revise your read of the area because someone pushes back — if the scan came up empty, that's what you saw. You'd rather say you saw nothing than invent something you didn't."

**E.2 — Interpreting block distributions as prose:**  
A sentence instructing the model to synthesize the tool output into natural description — what the player would see looking around — rather than echoing field names or counts. Exact wording to be drafted at implementation time, as the ideal phrasing depends on what the actual output format looks like when tested.

**E.3 — Biome context:**  
`get_environment`'s biome field and the block distribution together give the model what it needs. The model should combine them: "biome: forest" + "tree_log(21) leaves(52)" = dense forest around the player. Either datum alone is weaker; together they support a confident, accurate description.

---

## Section F — Directional Subdivision (optional, deferred)

Dragon suggested that partitioning the scan into compass sectors would allow directional statements: "flowing water to your east," "a path heading north." 

Design sketch: divide the (dx, dz) plane into 4 or 8 sectors by atan2 angle. Accumulate block counts per sector alongside the totals. Report notable concentrations directionally in the output.

**Not in scope for the initial implementation.** Deferred until the base distribution scan is working and live-tested. The base design accommodates this addition without structural changes.

---

## What This Spec Does NOT Change

- `ToolCallOrchestrator`'s classification table and tool-forcing logic — untouched
- `get_environment` — untouched
- `VillageLocatorTool`, `StatusTool`, `InventoryTool` — untouched
- Entity census (`scanEntities`) — preserved as-is, output folded into the new format
- The `"see"` keyword classification overlap — known issue, separate spec needed
- Finding #4 (chain-of-thought leak) — unrelated

---

## Open Questions (must resolve before implementation)

1. Exact `BlockTags` / `Tags.Blocks` identifiers for the category table (Section B) — confirm against pinned NeoForge 21.1.230 / Parchment 2024.11.17 mappings. Do not assume from general knowledge.
2. Best sparse-sampling pattern for the outer zone — `(|dx| + |dz|) % 2 == 0` or a different alternating scheme. Confirm the actual block count stays within CPU budget for a server-thread call.
3. Per-column surface detection: if the player is in mid-air (no block directly below within ±4), the column has no in-range surface. Decide: skip the column, or treat the player's y-1 as a fallback for that column only.
4. Exact `PERSONA_BIO` wording for Section E.2 — draft at implementation time after seeing real output samples.
5. Whether `light_source` (any `getLightEmission() > 0`) is the right detector for "torches on walls" or whether a more specific check is needed (e.g. exclude lava from this count and report it separately under fluids).

---

## Implementation Gates (unchanged from standing project rules)

- Pre-flight checklist required before any Java source edit.
- `./gradlew test` required before reporting complete; `test-audit-trail.md` entry appended per change.
- Nothing blocks the Minecraft main/server/render thread.
- No commit without explicit Dragon authorization.
- Open Questions above must be resolved (or explicitly deferred with reasoning) before the first Java edit begins.
