# Advisor Validation Checklist — Design Spec

**Date:** 2026-06-17
**Mod:** DragonTweaksV2 (NeoForge 1.21.1)
**Branch:** advisor
**Status:** Design approved; not yet implemented.

---

## Problem Statement

A prior session began logging in-game validation results against a flat numeric ID scheme (`V-00`, `V-01`, `V-02`, …) in `feed-me.md`. Five items (`V-00`, `V-01`, `V-02`, `V-04`, `V-05`) were logged with enough one-line context to reverse-engineer what they tested. The scheme implied a plan running through `V-29`, but no document ever defined what `V-06` through `V-29` were supposed to test. That knowledge is permanently lost — it was never written down, only referenced by number.

Root cause: a flat numeric ID carries no inherent meaning. Once the session that assigned the numbers ended, the only way to recover their meaning was the assigning session's own memory, which doesn't persist.

This spec replaces that scheme with a living, evidence-backed checklist where the ID itself is self-describing and the full definition is written down at creation time, not inferred after the fact.

---

## Goals

- Replace the flat `V-*` sequence with subsystem-grouped IDs that carry meaning even out of context.
- Seed the checklist with every behavior the existing `test-audit-trail.md` / `feed-me.md` history already and explicitly flags as requiring live (in-game) confirmation — no speculative or invented items.
- Remap the 5 already-passed legacy `V-*` results into the new structure so that history isn't discarded.
- Make `feed-me.md` reference this checklist instead of re-listing validation items each session.
- Define a standing rule for adding new items, so the original failure mode can't recur.

## Non-Goals

- Recovering the definitions of `V-06` through `V-29` — confirmed unrecoverable; out of scope.
- Adding checklist items for behavior that is already fully covered by unit/simulation tests with no explicit live-only caveat in the audit trail. (Approach C: evidence-bounded seeding, not exhaustive guessing.)
- Implementing any of the features referenced by the checklist items. This spec only formalizes how validation status is tracked.

---

## Document

**New file:** `docs/advisor-validation-checklist.md` — the single living checklist doc.

### Group legend

| Prefix | Group | Concern |
|---|---|---|
| `IR` | Init & Readiness | Startup-sequencing: capability probe, init gate, timeout/availability behavior |
| `TC` | Tool-Calling Invocation | The four tools (`get_inventory`, `get_environment`, `get_status`, `scan_area`) called correctly against a real `ServerPlayer` |
| `PV` | Persona & Voice | Subjective LLM-output checks: persona consistency, banned phrases, format rules, no tool-mechanism leakage |
| `SH` | Session & History Recovery | `/dt.purge`, corrupted-session recovery, history inclusion/exclusion |
| `SM` | Status Monitor / Effects | Live `MobEffectEvent` notification and circuit breaker |
| `EL` | Entity Lifecycle & Rendering | `AdvisorEntity` spawn/despawn/render |

The legend lives at the top of the doc so an ID is interpretable without cross-referencing anything else.

### Per-item format

```
### TC-02 — get_environment called instead of hallucinating
**Status:** PASS (2026-06-15)
**Covers:** location/time/weather/biome/elevation questions trigger a real get_environment call rather than a training-knowledge guess.
**Evidence:** test-audit-trail.md 2026-06-15 entry; root-caused by the "vast underground cavern" hallucination on surface grass.
```

**Status vocabulary:** `PASS` / `FAIL` / `PENDING` (not yet attempted) / `BLOCKED` (not testable under current conditions, with a one-line reason).

Updating a status is an in-place edit to that item's block — append the new date, don't duplicate the item.

---

## Seed Content (Approach C — evidence-bounded)

Every item below is either a remap of an already-logged `V-*` result, or traces to an explicit phrase in `test-audit-trail.md` / `feed-me.md` such as "in-game confirmation required," "no unit test possible," "requires live game/server," or "Tier 3 live verification required." No item was added on guesswork.

### IR — Init & Readiness

| ID | Item | Status | Evidence |
|---|---|---|---|
| IR-01 | Capability probe runs and logs result on player login | PASS (2026-06-15; remapped from `V-00`) | feed-me.md `V-00` |
| IR-02 | Chat rejected gracefully while `openRouter.isEnabled()` is false (init/priming window) | PASS (2026-06-15; remapped from `V-01`) | feed-me.md `V-01`; `AdvisorChatHandler.java:89-90` |
| IR-03 | After one timed-out response, OpenRouter stays enabled for subsequent queries | PENDING | test-audit-trail.md 2026-06-14, "stay-enabled behavior requires live in-game confirmation" |
| IR-04 | 5s/10s "still thinking" messages fire under a slow response | BLOCKED — model responds <2s, threshold never reached | feed-me.md `V-03` |

### TC — Tool-Calling Invocation

| ID | Item | Status | Evidence |
|---|---|---|---|
| TC-01 | `get_inventory` called, correct item count returned | PASS (2026-06-15; remapped from `V-05`) | feed-me.md `V-05` |
| TC-02 | `get_environment` called instead of hallucinating (location/time/weather/biome/elevation) | PASS (2026-06-15) | test-audit-trail.md 2026-06-14/15 |
| TC-03 | `get_status` called for effect questions; correct effects + duration | PENDING | test-audit-trail.md 2026-06-14, "tool invocation behavior requires in-game confirmation" |
| TC-04 | `scan_area` entity categories (passives/neutrals/hostiles/aggro) classify correctly | PENDING — known gap: piglin/bee aggro detection unverified | test-audit-trail.md 2026-06-14; feed-me.md deferred note |
| TC-05 | Hunger state line reflects real player food level | PENDING | test-audit-trail.md 2026-06-14, "Tier 3 live verification required" |
| TC-06 | All four tools execute end-to-end against a real `ServerPlayer` | PENDING | test-audit-trail.md 2026-06-14 Task 18 |

### PV — Persona & Voice

| ID | Item | Status | Evidence |
|---|---|---|---|
| PV-01 | "hello" gets an in-persona reply within reasonable RTT | PASS (2026-06-15; remapped from `V-02`) | feed-me.md `V-02` |
| PV-02 | Greetings stay ≤ 4 words | PASS (2026-06-15; remapped from `V-04`) | feed-me.md `V-04` |
| PV-03 | Persona stays "seasoned adventurer"; no tutorial/dictionary voice; no banned closings | PENDING | test-audit-trail.md 2026-06-16, "(in-game confirmation required)" |
| PV-04 | No tool/mechanism references in responses ("the scan", "data", "results") | PENDING | same entry |
| PV-05 | No invented terrain/details alongside real tool data (gap-filling ban) | PENDING | test-audit-trail.md 2026-06-16 `/dt.purge` entry |

### SH — Session & History Recovery

| ID | Item | Status | Evidence |
|---|---|---|---|
| SH-01 | `/dt.purge` clears a player's own session; confirmation message sent | PENDING | test-audit-trail.md 2026-06-16, "no unit test possible" |
| SH-02 | A corrupted/hallucinating session recovers cleanly after purge | PENDING | same entry |

### SM — Status Monitor / Effects

| ID | Item | Status | Evidence |
|---|---|---|---|
| SM-01 | Real detrimental `MobEffectEvent` triggers exactly one proactive notification (no double-fire) | PENDING | test-audit-trail.md 2026-06-14 Task 18; `notifiedEffects` DEFERRED note |
| SM-02 | Circuit breaker suppresses further notifications after threshold exceeded in window | PENDING | same |

### EL — Entity Lifecycle & Rendering

| ID | Item | Status | Evidence |
|---|---|---|---|
| EL-01 | `AdvisorEntity` spawns on login, despawns on logout | PENDING | test-audit-trail.md 2026-06-14 Task 18 |
| EL-02 | `AdvisorEntity` renders with no crash (correct event bus) | PASS (prior session fix confirmed stable since) | test-audit-trail.md 2026-06-14, two renderer-fix entries |

**Total: 21 items across 6 groups.** 7 are PASS (5 remapped legacy + TC-02 + EL-02), 1 is BLOCKED, 13 are PENDING.

---

## Explicitly Excluded From This Seed

Behavior that is already covered by a passing unit/simulation test with no explicit live-only caveat in the audit trail was deliberately left out, per Approach C:

- Double-timeout fix, executor-starvation fix — covered by dedicated unit tests (`AdvisorChatHandlerTest`, `OpenRouterServiceTest`) with no live-confirmation caveat in their audit entries.
- `<|...|>` token stripping — covered by a unit test on the stripping function; no explicit live-only flag.
- "Answer only what is asked" / "ground truth" prompt additions — no explicit live-only flag (though see PV-03–PV-05, which cover the same prompt-hardening work where a flag does exist).

Feature work not yet implemented is also excluded, since there's nothing to validate yet:

- Cave/ore data in `scan_area` (`detectOres` exists but out of scope)
- `get_status` conditional inclusion in the tool list
- Block data in `scan_area`

These stay tracked in `feed-me.md`'s Deferred section until implemented, at which point they get new checklist items following the Standing Rule below.

---

## Integration With feed-me.md / test-audit-trail.md

- `feed-me.md` stops re-listing individual `V-*`/checklist items in its Successes/In Progress tables. It instead links to `docs/advisor-validation-checklist.md` and notes which IDs changed status during that session.
- `test-audit-trail.md` is unaffected — it remains the append-only record of code changes and their test coverage. The checklist doc's "Evidence" field points back into it by date.
- The legacy `V-00`–`V-05` lines in `feed-me.md` are left as historical record (not deleted — `feed-me.md` is a session log, not append-only, but there's no reason to scrub true history); a note is added pointing to the remapped IDs.

---

## Standing Rule For New Items (prevents recurrence)

Whenever a code change introduces behavior that can only be confirmed in a live game (i.e., the audit-trail entry for that change would otherwise contain a phrase like "requires live confirmation" or "no unit test possible"):

1. Add the item to `docs/advisor-validation-checklist.md` under its group **in the same session as the code change**, status `PENDING`, with an `Evidence` pointer to that audit-trail entry.
2. If no existing group fits, add a new group to the legend with a 2-letter prefix, in the same edit.
3. Never reference a checklist ID in `feed-me.md` or anywhere else before its definition exists in the checklist doc.

---

## Open Questions

None — all structural decisions (ID format, document location, legacy remapping, seed scope, init-gate placement) were resolved during brainstorming.
