# DragonTweaks V1 — Salvage Review
*Reviewed: 2026-06-06*
*Sources: lessons-learned doc, advisor prompt engineering spec v0.2, diagnostic branching spec v0.1, V1 current state doc (session 28)*

---

## How to Use This Document

This document captures concepts, rules, data, and design decisions from the V1 "The Assistant Mod" project that are worth reusing in DragonTweaksV2. It is organized by theme. **Nothing here is implementation truth for V2** — all Java API paths, class names, and event names from V1 must be re-verified against V2 function signatures (in `docs/api/neoforge/` and `docs/api/minecolonies/`) before use.

---

## 1. Behavioral Rules — Fully Transferable

These are architecture-agnostic and apply directly to V2 advisor design.

### The Mandatory Sequence
**Observe → Diagnose → Recommend.** Never skip Observe. If data is unavailable, say so explicitly rather than guessing. Confident wrong answers are more damaging than honest uncertainty.

### Player-Confirmed Facts Are Ground Truth
Once the player confirms a fact, it is locked. Never re-investigate or propose hypotheses that contradict player-confirmed information. If API data contradicts a player-confirmed fact, flag the discrepancy explicitly rather than silently re-investigating.

### Never Speculate
If a data field is unavailable or unverified, acknowledge uncertainty rather than filling in a plausible answer. "The cause is unclear" is more useful than confident wrong analysis.

### Pattern Recognition Before Individual Diagnosis
When all happiness factors are simultaneously red — especially at colony founding — prioritize systemic root causes before individual factor analysis. Uniform red across unrelated factors is a strong signal of one upstream condition (e.g. doDaylightCycle=false), not multiple independent problems.

### Check Environmental Preconditions First
Before diagnosing citizen complaints, verify relevant gamerule states. If `doDaylightCycle` is false, flag it prominently before surfacing any happiness-related recommendations — happiness updates are not processing and complaint data is unreliable.

### Full Dependency Chain Before Any Recommendation
"Build X" is only valid advice if the full dependency chain can be satisfied. Before surfacing any build recommendation, validate: worker availability, bed availability, research prerequisites (both University level AND building level prereqs), and material supply.

---

## 2. Diagnostic Priority Order

For per-citizen diagnosis, evaluate factors in this order. Stop at the first red flag — that is the diagnosis for that citizen. Do not surface lower-priority complaints until higher-priority issues are resolved.

| Priority | Factor | Notes |
|---|---|---|
| 1 | Security | Active raid or insufficient guard coverage. Everything else stops. |
| 2 | Food | Fast cascade into productivity collapse and morale death spiral. |
| 3 | Health | Injured or sick. Compounds quickly if food also low. |
| 4 | Housing | No home building assigned. Degrades every night. |
| 5 | Sleep | Has a bed but cannot reach it. Usually pathing or distance. |
| 6 | Unemployment | No job assigned. Productivity loss but not immediately dangerous. |
| 7 | Commute | Bed too far from work. Only meaningful once citizen has both home and job. |
| 8 | Idle at job | Has a job but nothing to do. Treat as symptom first — check upstream. |
| 9 | Social | Low citizen count or isolation. Slow burn, rarely a crisis. |
| 10 | School | Children only, no school access. Long-term skill impact. |
| 11 | Mystical | No Mystical Site nearby. Late-game quality of life. |

**Example:** A citizen flagged idle at job who also has no housing assigned should never receive advice about their workload. They need a house. The idle flag is noise until housing is resolved.

### Commute Note
Commute is advisor-derived — there is no native MineColonies commute happiness factor. Calculate XZ Euclidean distance between work building and home building positions. Threshold: 80 blocks (advisor warning). MineColonies itself warns at 100 blocks — 80 is intentionally conservative.

---

## 3. Systemic Pattern Detection

Runs before per-citizen analysis. First match wins. If a systemic pattern is detected, suppress per-citizen LLM output and address the pattern instead.

| Priority | Pattern | Trigger |
|---|---|---|
| 1 | Newly founded, all red | Colony day < 3 AND all citizens have ≥1 red factor |
| 2 | All citizens red simultaneously | All citizens have ≥1 red factor (any colony age) |
| 3 | Housing/sleep/commute cluster | Housing red AND sleep red AND commute flagged, affecting ≥2 citizens |

**Key design decision:** Systemic patterns suppress per-citizen LLM output only — not the panel citizen list. The panel is a reference tool the player uses independently. Hiding the citizen list removes useful data even when systemic output fires.

---

## 4. MineColonies Mechanics — Verified Data Worth Reusing

*All Java API paths listed here are from V1. Re-verify against V2 function signatures in `docs/api/` before implementing.*

### Food Quality System
MineColonies implements a two-layer food quality system:

**Tier table:**
| Effective food value | Tier |
|---|---|
| ≤ 4 | Tier 1 |
| ≤ 6 | Tier 2 |
| > 6 | Tier 3 |

Vanilla food receives a 0.25 saturation nerf; MineColonies food uses full 1.0.

**Building-level food gating:** Citizens in huts below level 3 eat any food. Citizens in huts level 3+: food must have nutrition ≥ `buildingLevel + 1`. This is the mechanism behind food happiness degrading as workplaces are upgraded.

**Two distinct root causes the advisor must distinguish:**
1. Cook knows high-quality recipes but warehouse isn't stocked with the right ingredients.
2. Cook doesn't have high-quality recipes taught to it at all.

**Saturation drain scales with workplace level:** Saturation decreases by `0.2 × worker hut level` each night. A food supply that worked at colony founding will silently fail as buildings are upgraded.

### Housing Level Caps Citizen Skill

A citizen's maximum skill level is determined by their **home level**, not work hut level. This is a silent productivity ceiling, not a happiness penalty.

| Home Level | Max Skill Level |
|---|---|
| 0 | 10 |
| 1 | 20 |
| 2 | 30 |
| 3 | 40 |
| 4 | 50 |
| 5 | 99 |

Surface this as a **recommendation**, not a crisis. Identify the specific worker(s), note the home level vs skill cap relationship, suggest upgrading residences as a long-term growth investment.

### Research — Two Independent Gates

Research has two separate gating conditions, both must be satisfied:
1. **University level requirement** — what level the University must be to offer this research column
2. **Building level requirement** — what other buildings must exist at what total level

**Verified research chains (as of V1 session):**

| Research | University Level | Building Prereq | Prior Research | Cost |
|---|---|---|---|---|
| Woodwork | 1 | Forester's Hut(s) ≥ level 3 total | None | 64x Any Planks |
| Stringwork | 2 | None beyond Woodwork | Woodwork | 16x String |
| Hitting Iron | 1 | Mine(s) ≥ level 3 total | None | 1x Anvil |

**Surface the longest blocking dependency first.** For the Fletcher, the blocker is upgrading the Forester to level 3 — not building the Fletcher itself.

### Guard Tower Patrol Radius

| Tower Level | Patrol Radius |
|---|---|
| 1 | 80 blocks |
| 2 | 110 blocks |
| 3 | 140 blocks |
| 4 | 170 blocks |
| 5 | 200 blocks |

### Commute Thresholds
- **80 blocks** — advisor warning (conservative, pre-complaint)
- **100 blocks** — MineColonies native complaint threshold

### Misc Thresholds
- Raid trigger: 7 citizens

---

## 5. Advisor State Machine

Four states. State must be persisted in SavedData, not entity NBT (entity NBT does not survive despawn).

| State | Entry Trigger | Entity | Capability |
|---|---|---|---|
| DORMANT | Default | None | None |
| PRE_COLONY | Build tool enters hotbar (one-time per player) | Floating book, player-attached | Sensory only (terrain, biome, weather) |
| COLONY_NO_CITIZEN | ColonyCreatedModEvent | Floating book, colony-attached | Colony structure data, no citizen data |
| COLONY_WITH_CITIZEN | Player assigns citizen to Advisor role | Floating book-and-quill, colony-attached | Full diagnostic capability |

**Visual signal:** Book → Book-and-Quill is the passive signal that full capability is unlocked. No popup required.

**Shadow entity follows the player, NOT the citizen.** The citizen is the name and personality anchor only. The citizen continues normal MineColonies work completely unmodified.

**Per-player state:** On multiplayer, each player has fully independent advisor state.

### Degraded States
- COLONY_WITH_CITIZEN → COLONY_NO_CITIZEN: on CitizenDiedModEvent or CitizenJobChangedModEvent for the assigned citizen
- Any colony state → PRE_COLONY: on ColonyDeletedModEvent

### Sky Visibility Constraint (Shadow Entities)
Shadow entities may only occupy positions with unobstructed sky access. Prevents entering buildings, going underground, descending staircases. Natural overhangs and jungle canopy permitted. This constraint was designed but not yet confirmed working in V1.

---

## 6. Response Delivery Pattern

**Short responses** (below threshold): delivered to public chat as `[CitizenName]: [text]`

**Long responses** (at or above threshold): public whisper message from template pool + full response delivered privately to triggering player only.

**Config values:**
- `ADVISOR_WHISPER_THRESHOLD`: int, default 120 characters
- `ADVISOR_FORCE_PRIVATE`: boolean, default false — server operator override to force all responses private (public whisper message still fires for visual cue)

**Whisper template pools (by state):**

COLONY_WITH_CITIZEN:
- "[CitizenName] whispers something to [PlayerName]."
- "[CitizenName] leans over and murmurs to [PlayerName]."
- "[CitizenName] speaks quietly with [PlayerName]."

COLONY_NO_CITIZEN:
- "The advisor whispers something to [PlayerName]."

PRE_COLONY:
- "Your advisor murmurs something to you."
- "The book rustles quietly near [PlayerName]."

---

## 7. Throttle / Proactive Output

**Per-citizen daily throttle:** Advisor delivers unprompted diagnosis at most once per in-game day per citizen. Key: `{colonyId}:{citizenId}:{colonyDay}`. Persists across sessions via SavedData.

**Root cause suppression:** If a citizen's root cause has not changed, suppress repeat observations for a configurable number of colony days (default 2). Key format: `{colonyId}:{citizenId}:rc{rootCauseOrdinal}`. Config: `ADVISOR_ROOTCAUSE_SUPPRESS_DAYS`.

**Systemic pattern throttle:** Once per pattern type per in-game day. Key: `{patternType}:{colonyDay}`.

**Universal LLM call rule:** LLM calls of any kind only fire when at least one player is within detection range of the entity. No player nearby = no LLM calls.

---

## 8. Hybrid Root Cause Model

Rule-based logic determines the root cause. LLM narrates it in character.

- Pure LLM root cause generation is unpredictable and may hallucinate causes that don't exist in the colony.
- Pure rule-based output is dry and breaks immersion.
- Hybrid gives deterministic accuracy with characterful delivery.

**Root cause rules (factor → hypothesis):**

| Factor | Condition | Hypothesis |
|---|---|---|
| `slepttonight` | commute > threshold | Bed too far from work building |
| `slepttonight` | housing also red | No home building assigned |
| `slepttonight` | neither | Unknown — flag for player inspection |
| `housing` | no home assigned | No Residence available or assigned |
| `food` | no Cook/Restaurant exists | No food production building |
| `food` | building exists | Supply chain issue |
| `unemployment` | no job | No matching work building available |
| `idleatjob` | has job | Pathfinding or supply blockage at work building |
| `security` | no Guard Tower in range | No guard coverage at citizen location |
| `health` | — | Injury or disease — no automated fix |
| commute | distance > threshold | Home building too far from work building |

---

## 9. Role Concepts Not Yet in V2

These roles were designed and partially implemented in V1. The concepts are reusable; the implementation is not.

### Scout Role
- Terrain scan (32×8×32 radius), underground scan (depth 10), sound detection, explosion detection
- **Speaks from instinct, not game mechanics.** Uses atmospheric, vague language: "I sense something beneath the earth", "something chittering in the dark"
- Intentional misidentification: 5–10% chance of incorrect identification by design
- Sound confidence hierarchy: Zombies/Skeletons/Spiders high → Witches low → Endermen effectively none
- Never reports passive mobs. Never says Y-level, light level, or mob spawn rate
- Detection limited to vanilla Minecraft and MineColonies mobs only — mod-added mobs ignored
- Output token cap: 300

### Ranch Hand Role
- **The only role with direct world-state side effects.** Applies leads, moves animals, attaches to fence posts. All other roles are advisory only.
- Sighting memory: short TTL list with entries {animal type, approximate XZ coords, game tick timestamp}
- Dual eviction: TTL (~12,000 ticks / ~10 real minutes) + cap (8–10 entries)
- Only catches animals for which a facility exists in the colony (no cowherder hut → no catching cows)
- Routes to nearest appropriate facility, not always the same one
- One animal at a time — no chain-leading
- Wandering is guided: biases toward areas with previous sightings and known facility locations
- Operates beyond colony bounds at a configurable buffer distance
- Output token cap: 150

### Planner Role
- Validates full dependency chain before any build recommendation
- Research prerequisites are first-class dependencies (University level AND building level prereqs)
- Surfaces longest blocking dependency first
- Worker availability, bed availability, material supply all checked before recommendation
- Output token cap: 500

---

## 10. Persona Notes from V1 Role Config

These are supplementary to the V2 persona definitions already saved in memory.

| Role | V1 Persona Note |
|---|---|
| Advisor | Analytical and direct. Prioritizes actionable recommendations. Avoids flavor embellishment. Never invents facts about colony state — all observations must be grounded in provided diagnostic data. |
| Planner | Methodical and structured. Output follows dependency chain order. Flags blockers clearly. Does not speculate beyond available dependency and colony data. |
| Scout | Speaks from instinct and observation, not game mechanics. Occasional misidentification is intentional. |
| Ranch Hand | Practical and brief. Focuses on animal management tasks. Does not speculate or editorialize. Reports what it finds and what it did. |

---

## 11. Config Values Worth Reusing

| Key | Type | Default | Notes |
|---|---|---|---|
| `ADVISOR_COMMUTE_THRESHOLD` | int | 80 | Blocks — advisor warning before MineColonies complains at 100 |
| `ADVISOR_HAPPINESS_THRESHOLD_RED` | double | 0.5 | ⚠️ INVALID — V1 assumed a 0–1 per-factor scale; actual overall happiness scale is 0–10 (MAX_HAPPINESS=10) and starts near -1 at colony founding. Thresholds must be calibrated in-game before use. |
| `ADVISOR_HAPPINESS_THRESHOLD_YELLOW` | double | 0.9 | ⚠️ INVALID — same scale issue as RED. Do not apply V1 defaults to V2. |
| `ADVISOR_ROOTCAUSE_SUPPRESS_DAYS` | int | 2 | Colony days to suppress repeat observations for unchanged root cause |
| `ADVISOR_WHISPER_THRESHOLD` | int | 120 | Characters — above this triggers whisper pattern |
| `ADVISOR_FORCE_PRIVATE` | boolean | false | Server operator override — forces all responses private |
| `ADVISOR_ENTITY_OFFSET` | double | 1.8 | Blocks offset from player |
| `ADVISOR_HOTBAR_CHECK_TICKS` | int | 40 | Hotbar poll interval (PRE_COLONY only) |
| `ADVISOR_BOUNDARY_DETECTION_RANGE` | int | 40 | Blocks before snap to Town Hall |
| `FLAVOR_NPC_GREETING_CHANCE` | double | 0.07 | Roll on player entering detection range |
| `FLAVOR_NPC_GREETING_COOLDOWN_TICKS` | int | 12000 | Per-NPC, per-player cooldown (~10 real minutes) |

---

## 12. Hard Architectural Rules — Still Valid for V2

These carry over from V1 and align with V2's existing patterns:

1. Nothing may ever block the main Minecraft game thread.
2. All network calls (including OpenRouter HTTP requests) must be async on a separate thread.
3. Responses from async operations must be queued back to the main thread before any game interaction.
4. Zero interference with MineColonies internals. All integration via public API and event system only. Citizens are never directly modified.
5. All MineColonies API dependencies must be verified against function signatures in `docs/api/` before any dependent code is written.

---

## 13. What to Discard — V1 Only

| Item | Reason |
|---|---|
| V1 class names (ColonyDiagnosticReport, LLMClient, ObservationTicker, PlannerDependencyRegistry, etc.) | Do not exist in V2 |
| V1 event names (BuildingConstructionModEvent, CitizenJobChangedModEvent, etc.) | Need re-verification against V2 function signatures in `docs/api/` |
| V1 Java API paths (ICitizenFoodHandler, CitizenFoodStats, getMinFoodQualityRequirement, etc.) | Need re-verification against V2 function signatures in `docs/api/` |
| V1 model config (google/gemma-4-26b-a4b-it, hardcoded) | Replaced by dynamic model_config.json in V2 |
| V1 role config JSON schema (scraper_tier, capabilities) | Format only — concepts salvaged above |
| Shadow entity sky-visibility constraint | Designed in V1, not confirmed working — needs fresh implementation and testing |
| planner_dependencies.json content | Building registry names may differ in V2; re-verify before use |

---

*End of salvage review.*
