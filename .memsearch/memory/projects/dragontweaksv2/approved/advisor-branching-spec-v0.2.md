**Title:** Advisor diagnostic loop branching spec v0.2 — implementable design
**Type:** fact
**Intent triggers:** advisor, diagnostic loop, AdvisorDiagnosticLoop, runCycleAsync, pre-scan, citizen selection, throttle, suppression, root cause, AdvisorThrottleData, RaidStartedEvent, buildCitizenPrompt, CitizenRecord, ColonyDiagnosticReport, LLM call, per-citizen, branching logic
**Source/evidence:** Advisor Branching Spec v0.2, design session 2026-05-17, provided by Dragon
**Status:** Not yet implemented. Line numbers (85–101 in runCycleAsync) may have drifted. Verify B1/B2 against stubs before implementing.

---

## Document Scope

Specifies implementable branching logic for `AdvisorDiagnosticLoop.runCycleAsync()`. Does NOT specify LLM prompt wording (see `docs/advisor_prompt_engineering_spec_v0_2.md`).

**Files touched by this spec:**
- `AdvisorDiagnosticLoop.runCycleAsync()` — lines 85–101 replaced (verify line numbers before implementing)
- `AdvisorThrottleData` — storage model changes (Section 8)
- `DragonTweaks.java` — one new invalidation trigger (RaidStartedEvent)
- `ColonyDiagnosticReportGenerator` — unchanged

---

## Pre-Scan Pass (Section 1)

Before any LLM call, collect all citizens from `report.getCitizenRecords()`, filter to flagged only, sort, truncate to max 5 → **candidate list**.

**Sort order:**
1. Red citizens first (any factor below `ADVISOR_HAPPINESS_THRESHOLD_RED`, or `commuteFlagged == true`)
2. Yellow citizens second (any factor below `ADVISOR_HAPPINESS_THRESHOLD_YELLOW`, none red, commute not flagged)
3. Alphabetical by name within each tier

**Source fields:**
- `citizenRecord.isCommuteFlagged()`
- `Config.ADVISOR_HAPPINESS_THRESHOLD_RED.get()` / `Config.ADVISOR_HAPPINESS_THRESHOLD_YELLOW.get()`

---

## Output Selection (Section 2)

Take **top 2** from candidate list. No special-casing by tier.

| State | Output |
|---|---|
| 2+ reds | Top 2 reds |
| 1 red, 1+ yellows | Top red + top yellow |
| 0 reds, 2+ yellows | Top 2 yellows |
| 0 reds, 1 yellow | That yellow only |
| Empty | No per-citizen LLM calls this cycle |

Each selected citizen → one LLM call via `buildCitizenPrompt(report, citizenRecord)`.

---

## Throttle and Suppression (Section 4)

### 4.1 Per-Citizen Daily Throttle
Key: `"{colonyId}:{citizenId}:{colonyDay}"`
If already fired today → skip. Next candidate is NOT promoted.

### 4.2 Root Cause Suppression
Prevents a citizen with a chronic unchanged issue from consuming a daily slot indefinitely.

**Suppression key:** `"{colonyId}:{citizenId}:rc{rootCauseOrdinal}"`

**Check order (before daily throttle):**
1. Build suppression key from citizen's current `RootCause` ordinal
2. Look up `AdvisorThrottleData.getSuppressedSinceDay(key)`
3. If `colonyDay - suppressedSinceDay < ADVISOR_ROOTCAUSE_SUPPRESS_DAYS` → skip (no throttle slot consumed)
4. If expired (`>= ADVISOR_ROOTCAUSE_SUPPRESS_DAYS`) → clear suppression, allow through
5. If no entry → allow through; after firing, record `suppressedSinceDay = colonyDay`

**Config:** `ADVISOR_ROOTCAUSE_SUPPRESS_DAYS`, default **2**, in `dragontweaks-common.toml`

**Root cause change:** Different ordinal = different key = fires immediately.

---

## RaidStartedEvent Invalidation Trigger (Section 5)

Add `RaidStartedEvent` as a cache invalidation + `markDirty()` trigger in `DragonTweaks.java`, alongside existing four event handlers.

Pattern: `ColonyDiagnosticCache.invalidate(colonyId)` + `AdvisorDiagnosticLoop.markDirty(colonyId)` together.

**B1 — VERIFY package path of `RaidStartedEvent` against `docs/stubs/` before implementing.**

---

## runCycleAsync() Flow (Section 6)

Replace lines 85–101 (verify line numbers first):

```
1. [Existing] Check isSystemicPatternDetected() → fire systemic LLM call if true
2. [New] Pre-scan pass → candidate list (max 5)
3. [New] Output selection → top 2
4. [New] Per-citizen loop (max 2 iterations):
   For each citizen:
     a. Build suppression key: "{colonyId}:{citizenId}:rc{rootCauseOrdinal}"
     b. Check suppression (async thread — read-only)
        → suppressed + in window: skip entirely
        → expired or absent: continue
     c. Build daily throttle key: "{colonyId}:{citizenId}:{colonyDay}"
     d. server.execute() block (main thread):
         · hasFiredToday(dailyKey) → skip if true
         · buildCitizenPrompt(report, citizenRecord) → LLMClient.observe()
         · markFiredToday(dailyKey)
         · recordSuppression(suppressionKey, colonyDay)
```

Steps 4a–4b: async thread. Step 4d: main thread via `server.execute()`. Each citizen gets its own `server.execute()` block — do not batch.

---

## Signature Change (Section 7)

**New:** `private String buildCitizenPrompt(ColonyDiagnosticReport report, CitizenRecord citizen)`

`report.getTargetCitizen()` no longer called from the loop.

---

## AdvisorThrottleData Storage Model Change (Section 8)

**Replace** `Set<String> firedKeys` with:
```java
Map<String, Integer> firedKeys;      // key → colonyDay fired (daily throttle)
Map<String, Integer> suppressedKeys; // suppressionKey → colonyDay first fired
```

Both maps persist to NBT on overworld SavedData. Use distinct NBT tag names (`"firedKeys"`, `"suppressedKeys"`).

**Updated API:**

| Method | Description |
|---|---|
| `hasFiredToday(String key, int colonyDay)` | key in firedKeys AND stored day == colonyDay |
| `markFiredToday(String key, int colonyDay)` | firedKeys.put(key, colonyDay) |
| `getSuppressedSinceDay(String key)` | suppressedKeys.getOrDefault(key, -1) |
| `recordSuppression(String key, int colonyDay)` | suppressedKeys.put(key, colonyDay) |
| `clearSuppression(String key)` | suppressedKeys.remove(key) |

Old `hasFired(String)` and `markFired(String)` are replaced — update all call sites.

**Stale entry cleanup:** Prune `firedKeys` entries older than `colonyDay - 2` on load or periodically.

---

## Open Questions (Must Resolve Before Implementing)

| ID | Question | Blocks |
|---|---|---|
| B1 | Verify `RaidStartedEvent` package path against `docs/stubs/` | Section 5 |
| B2 | Verify `CitizenRecord` field names for red/yellow flags, commute flag, root cause against current `ColonyDiagnosticReport` implementation | Sections 1 and 4.2 |

Both resolvable by reading stubs — no design session required.
