# Advisor Validation Checklist

Living checklist for in-game-only validation of the advisor system. See `docs/superpowers/specs/2026-06-17-advisor-validation-checklist-design.md` for the design rationale and the Standing Rule for adding new items.

## Group Legend

| Prefix | Group | Concern |
|---|---|---|
| `IR` | Init & Readiness | Startup-sequencing: capability probe, init gate, timeout/availability behavior |
| `TC` | Tool-Calling Invocation | The four tools (`get_inventory`, `get_environment`, `get_status`, `scan_area`) called correctly against a real `ServerPlayer` |
| `PV` | Persona & Voice | Subjective LLM-output checks: persona consistency, banned phrases, format rules, no tool-mechanism leakage |
| `SH` | Session & History Recovery | `/dt.purge`, corrupted-session recovery, history inclusion/exclusion |
| `SM` | Status Monitor / Effects | Live `MobEffectEvent` notification and circuit breaker |
| `EL` | Entity Lifecycle & Rendering | `AdvisorEntity` spawn/despawn/render |

**Status vocabulary:** `PASS` / `FAIL` / `PENDING` (not yet attempted) / `BLOCKED` (not testable under current conditions, with a one-line reason).

Updating a status is an in-place edit to that item's block — append the new date, don't duplicate the item.

---

## IR — Init & Readiness

### IR-01 — Capability probe runs and logs result on player login
**Status:** PASS (2026-06-15; remapped from `V-00`)
**Covers:** On player login, the context-retention capability probe executes and its result is logged.
**Evidence:** feed-me.md `V-00`.

### IR-02 — Chat rejected gracefully during init/priming window
**Status:** PASS (2026-06-15; remapped from `V-01`)
**Covers:** While `openRouter.isEnabled()` is false (service not yet ready), chat messages are rejected rather than hanging or crashing.
**Evidence:** feed-me.md `V-01`; `AdvisorChatHandler.java:89-90`.

### IR-03 — OpenRouter stays enabled after one timed-out response
**Status:** PENDING
**Covers:** After a single 60s timeout, OpenRouter must remain enabled for subsequent player queries (no permanent self-disable).
**Evidence:** test-audit-trail.md 2026-06-14, "stay-enabled behavior requires live in-game confirmation".

### IR-04 — 5s/10s "still thinking" messages fire under a slow response
**Status:** BLOCKED — model currently responds in under 2 seconds, so the 5s/10s thresholds are never reached under normal conditions. Needs an artificially slow model or a load scenario to exercise.
**Covers:** The handler's intermediate "still thinking" messages appear at the 5s and 10s marks when a response is slow.
**Evidence:** feed-me.md `V-03`.

---

## TC — Tool-Calling Invocation

### TC-01 — get_inventory called, correct item count returned
**Status:** PASS (2026-06-15; remapped from `V-05`)
**Covers:** Asking about inventory triggers `get_inventory`, and the reported item count matches the player's actual inventory.
**Evidence:** feed-me.md `V-05`.

### TC-02 — get_environment called instead of hallucinating
**Status:** PASS (2026-06-15)
**Covers:** Location/time/weather/biome/elevation questions trigger a real `get_environment` call rather than a training-knowledge guess.
**Evidence:** test-audit-trail.md 2026-06-14/15 entries; root-caused by the "vast underground cavern" hallucination on surface grass.

### TC-03 — get_status called for effect questions
**Status:** PENDING
**Covers:** Asking about active effects triggers `get_status`, returning the correct detrimental effects and remaining duration.
**Evidence:** test-audit-trail.md 2026-06-14, "tool invocation behavior requires in-game confirmation".

### TC-04 — scan_area entity categories classify correctly
**Status:** PENDING — known gap: piglin/bee aggro detection unverified.
**Covers:** `scan_area`'s passives/neutrals/hostiles/aggro parameters correctly classify nearby entities, including aggro-on-player detection.
**Evidence:** test-audit-trail.md 2026-06-14; feed-me.md deferred note on neutral mob aggro edge cases.

### TC-05 — Hunger state reflects real player food level
**Status:** PENDING
**Covers:** The injected hunger-state line matches the player's actual food level in a live game.
**Evidence:** test-audit-trail.md 2026-06-14, "Tier 3 live verification required (uses player.getFoodData())".

### TC-06 — All four tools execute end-to-end against a real ServerPlayer
**Status:** PENDING
**Covers:** `get_inventory`, `get_environment`, `get_status`, and `scan_area` all execute correctly end-to-end (argument parsing, server-thread execution, result formatting) against a real `ServerPlayer`, not a mock.
**Evidence:** test-audit-trail.md 2026-06-14 Task 18.

---

## PV — Persona & Voice

### PV-01 — Basic greeting round trip
**Status:** PASS (2026-06-15; remapped from `V-02`)
**Covers:** "hello" gets an in-persona reply within a reasonable round-trip time.
**Evidence:** feed-me.md `V-02`.

### PV-02 — Greetings stay ≤ 4 words
**Status:** PASS (2026-06-15; remapped from `V-04`)
**Covers:** Greeting/farewell responses obey the ≤ 4 word format rule.
**Evidence:** feed-me.md `V-04`.

### PV-03 — Persona stays "seasoned adventurer"; no tutorial voice; no banned closings
**Status:** PENDING
**Covers:** Responses stay in the "seasoned adventurer" persona — plain-spoken, not tutorial/dictionary voice — and never use banned closings like "That's all" or "Hope that helps".
**Evidence:** test-audit-trail.md 2026-06-16, "(in-game confirmation required)".

### PV-04 — No tool/mechanism references in responses
**Status:** PENDING
**Covers:** Responses never say "the scan", "data", "results", or otherwise reference the tool-calling mechanism.
**Evidence:** test-audit-trail.md 2026-06-16 (same entry as PV-03).

### PV-05 — No invented terrain/details alongside real tool data
**Status:** PENDING
**Covers:** When a tool call returns real data, the response doesn't pad it with invented terrain or details the tool didn't provide (gap-filling ban holds under real queries).
**Evidence:** test-audit-trail.md 2026-06-16 `/dt.purge` entry.

---

## SH — Session & History Recovery

### SH-01 — /dt.purge clears a player's own session
**Status:** PENDING
**Covers:** Running `/dt.purge` clears the invoking player's session and sends a confirmation message.
**Evidence:** test-audit-trail.md 2026-06-16, "requires a live Minecraft server; no unit test possible".

### SH-02 — Corrupted session recovers cleanly after purge
**Status:** PENDING
**Covers:** A session that was hallucinating/defending invented details returns to normal, grounded answers after `/dt.purge`.
**Evidence:** test-audit-trail.md 2026-06-16 (same entry as SH-01).

---

## SM — Status Monitor / Effects

### SM-01 — Real MobEffectEvent triggers exactly one notification
**Status:** PENDING
**Covers:** A real detrimental `MobEffectEvent` triggers exactly one proactive advisor notification — no double-fire for the same effect application.
**Evidence:** test-audit-trail.md 2026-06-14 Task 18; `notifiedEffects` DEFERRED note.

### SM-02 — Circuit breaker suppresses notification spam
**Status:** PENDING
**Covers:** After repeated effects exceed the threshold within the rolling window, further notifications are suppressed.
**Evidence:** test-audit-trail.md 2026-06-14 Task 18 / AdvisorStatusMonitor entries.

---

## EL — Entity Lifecycle & Rendering

### EL-01 — AdvisorEntity spawns on login, despawns on logout
**Status:** PENDING
**Covers:** `AdvisorEntityManager` spawns an `AdvisorEntity` on `PlayerLoggedInEvent` and discards it on `PlayerLoggedOutEvent`, with no world remnant.
**Evidence:** test-audit-trail.md 2026-06-14 Task 18.

### EL-02 — AdvisorEntity renders with no crash
**Status:** PASS (prior session fix confirmed stable since)
**Covers:** `AdvisorEntity` does not crash the renderer; `NoopRenderer` is registered on the correct (MOD) event bus.
**Evidence:** test-audit-trail.md 2026-06-14, two renderer-fix entries.
