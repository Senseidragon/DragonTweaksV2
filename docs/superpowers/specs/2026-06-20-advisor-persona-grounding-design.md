# Advisor Persona-Driven Prompt & Grounding Redesign — Design Spec

**Date:** 2026-06-20
**Mod:** DragonTweaksV2 (NeoForge 1.21.1)
**Branch:** advisor
**Status:** Design approved; not yet implemented.

**Supersedes (partially):** `docs/superpowers/specs/2026-06-13-tool-calling-design.md` — specifically its "System Prompt — Tool Guidance" section and the text-only branch of the round-trip threading model. Everything else in that spec (tools, `LoreIndex`, `AdvisorStatusMonitor`, entity lifecycle, edge cases, dispatch mechanics) is unaffected and remains authoritative.

---

## Problem Statement

The advisor's system prompt grew, session by session, by appending a new prose rule every time a failure was observed: "don't say 'scan'," "don't say 'data'," "answer only what's asked," "no gap-filling," "no innate world knowledge." `feed-me.md`'s Failures table shows at least seven of these patched in one session alone. Two exceptions moved enforcement to code instead of prose: reasoning-token stripping and a 3-sentence truncation cap. Every other rule stayed prose-only, with no way to verify compliance except manual chat review — which is exactly why `PV-03`, `PV-04`, and `PV-05` in `docs/advisor-validation-checklist.md` have sat at `PENDING` since 2026-06-16.

Two distinct upstream issues were found:

1. **Mismatched enforcement strategy.** Rules that are mechanically checkable (literal banned phrases) were left as prose asking the model to comply, when code can just check the output. Rules that are *not* mechanically checkable (voice, "don't lecture," ground-truth-only) were also left as prose, asking the model to follow a negative instruction — which models are generally worse at than at consistently *being* something. The fix in both directions is to match the enforcement mechanism to what's actually checkable: code where a property is a literal/structural check, persona framing where it's a character trait, and stop treating every problem as "add another prompt sentence."
2. **A checkable-looking fix that doesn't enforce the right thing.** The 3-sentence truncation cap is deterministic and easy to verify, but it truncates at a fixed count regardless of where the important content falls in the response — it can sever a warning mid-thought ("whatever you do, don't —"). Being checkable doesn't mean a mechanism enforces the *correct* property; sentence count was never the actual goal, concision was.
3. **A structural gap, not a wording gap.** The cavern hallucination (`feed-me.md`, "Failures" row 1) happened because round 1 of `ToolCallOrchestrator` is allowed to answer a world-state question directly, with no tool call, straight from training knowledge. No amount of prompt wording fixes a path that's structurally allowed to skip grounding — only changing the round-trip contract does.

---

## Goals

- Replace prose rules that are mechanically checkable with code-level checks.
- Replace prose rules that describe a character trait with persona framing, so the model is *being* something rather than obeying a negative instruction list.
- Close the structural path that let round 1 deliver an ungrounded, un-vetted answer for world-state queries.
- Stop enforcing brevity by truncation; make it a persona trait instead.
- Replace fixed-example tests with a generative harness so persona/voice properties — currently unverifiable except by manual play — get an automated, repeatable check, unblocking `PV-03`–`PV-05`.
- Remove the test and doc content that's gone stale now that `#a`/`#f` persona-prefix routing is confirmed abandoned.

## Non-Goals

- Renaming or fundamentally re-conceiving the advisor as a different character — continuity with the existing "seasoned adventurer" label is preserved (see Section 2).
- A code-level semantic classifier that proves a response is fully grounded — out of scope. The heuristic gate in Section 4 is accepted as imperfect; it shrinks the blast radius of the original bug, it doesn't claim to eliminate it.
- Auditing or removing `ChatCommandHandler.java` (the source class) or consolidating `model_config.json`'s `"flavor"`/`"advisory"` model roles. Both were raised during design and are plausible follow-up cleanup, but neither blocks this redesign and neither has been confirmed dead without reading source under separate authorization. Deliberately excluded, not forgotten.
- Any change to `LoreIndex`, the four tools' definitions/execution, `AdvisorStatusMonitor`, or entity lifecycle.

---

## Section 1 — Scope

Unchanged: `LoreIndex`, `InventoryTool`, `EnvironmentTool`, `StatusTool`, `ScanAreaTool`, `AdvisorStatusMonitor`, entity lifecycle, session/history storage (`AdvisorSession`, `AdvisorSavedData`). This is plumbing, not rule-list or persona content, and none of it is implicated in the problems above.

Changed: the static content of `AdvisorChatHandler.SYSTEM_PROMPT`, the round-trip contract in `ToolCallOrchestrator`, and the test suite (`AdvisorPromptIntegrationTest`, `EnvironmentToolSimulationTest`, `ChatCommandHandlerTest`).

---

## Section 2 — The Persona Document

The static portion of the system prompt becomes a single persona bio — identity, voice, and habits — rather than a persona one-liner followed by a separate list of "don't" rules. Dynamic context (time/weather/biome/hunger/lore) still gets injected per-query exactly as today; only the static prose changes. Tool *definitions* stay structured JSON schemas passed via the API's tools parameter, unchanged — only the prose *guidance* about when to use them moves into the bio as habit.

Base identity is the existing "seasoned adventurer" label (`feed-me.md` row 14), fleshed into a full bio rather than discarded — confirmed preference, with latitude to adapt specific details during implementation if they clash with achieving the underlying goals below. No proper name is introduced; the project has never assigned one and consistently refers to the character as "the advisor."

Draft bio (implementation may adjust wording, not intent):

> You are a seasoned adventurer who has spent years living in and surviving this land. You speak plainly, from experience, the way someone talks while working — not the way someone lectures. You answer exactly what you're asked, nothing more; you don't pad an answer with extra observations nobody asked for, and you don't tack on a closing remark when you're done, you just stop. You never speak on your surroundings, your gear, or your condition unless you've actually checked them first — you're careful that way, the same as any adventurer who's survived this long. You've never set foot outside this land and have nothing to say about places, things, or ideas beyond it.

Each sentence maps directly to a row in Section 3's classification table: plain/experience-based voice, answer-only-what's-asked, no closings, check-before-speaking (covers all four tools as habit rather than instruction), and the world-knowledge boundary as in-fiction isolation rather than a meta-rule about the AI.

---

## Section 3 — Rule Classification

Every existing prompt rule and prior fix (`feed-me.md` Failures table, `README.md` items 15/17/19) falls into one of two buckets — a third bucket, the truncation cap, is being removed rather than reclassified.

| Bucket | Rule | Disposition |
|---|---|---|
| **Code-deterministic** | `<\|...\|>` reasoning-token leakage | Unchanged — stays a post-processing strip. |
| | Banned literal phrases ("the scan," "data," "results," "That's all," "Hope that helps") | **Changed.** Moves from prose ("never say X") to a post-generation denylist check, same pattern as the token-strip. A literal string match doesn't need the model's cooperation to be reliable. |
| **Persona trait** | Ground-truth-only / no gap-filling | Becomes a habit: never speaks on what hasn't been checked. |
| | "Answer only what's asked" | Becomes a habit: doesn't lecture, doesn't pad. |
| | Voice / no tutorial-dictionary register | The persona bio itself. |
| | No modern/outside knowledge | In-fiction boundary (isolation), not a meta-instruction to the AI. |
| | Tool-use guidance (when to call which tool) | Reframed as the "checks before speaking" habit; the structural backstop for the cases this doesn't catch is Section 4. |
| **Removed, not reclassified** | 3-sentence truncation cap (`OpenRouterService.truncateToSentences`, currently invoked in the response-delivery path) | **Dropped entirely.** It's deterministic and checkable, but sentence-count isn't the property that matters — it can sever a response exactly where the important clause was about to land. Brevity becomes 100% persona-driven (the bio's "speaks plainly... while working" framing), with no code chopping the output afterward. The existing `max_tokens` generation ceiling stays as a pure anti-runaway safety valve (already set generously, per `README.md`, specifically so it's rarely hit) — that guards against a pathological case (the model looping), not against a normal, complete answer, which is a different risk than routine truncation. |

Net effect: the prompt shrinks to persona bio + dynamic context, with no leftover "don't say X / don't do Y" checklist — anything mechanically checkable moved to code, everything else moved into character, and the one mechanism that was checkable-but-wrong was removed rather than kept for the sake of having *something* deterministic.

---

## Section 4 — Closing the Round-1 Shortcut

### Today

Per the 2026-06-13 spec's threading model, round 1 either returns tool calls, or returns a final text response directly with no tool calls, which goes straight to the player. The cavern hallucination happened on the second path: asked "where am i," the model answered from training knowledge instead of calling `get_environment`. Nothing in the round-trip contract prevented this — round 1's freeform text was treated as trustworthy by default.

### Change

Round 1 is no longer trusted to deliver a final answer unsupervised whenever the query plausibly depends on world state. The orchestrator classifies each incoming query with a cheap heuristic gate — the same kind of pattern already used by `ToolCallOrchestrator`'s existing history-inclusion heuristic (follow-up-signal keywords → include history; pure state-query patterns → suppress; default → include) — but answering a different question: does this query plausibly reference world state, or is it pure social/conversational content with nothing in it to ground?

- World-state signal keywords (illustrative, not exhaustive — tuned during implementation): "where," "what time," "weather," "biome," "inventory," "holding," "wearing," "health," "effect(s)," "nearby," "around me," "see," "creature(s)," "threat(s)."
- Pure-chitchat signal keywords: greeting/farewell/acknowledgment patterns ("hello," "hey," "thanks," "bye," "lol") with none of the above present.

- **Heuristic flags world-state-relevant:** round 1's output is never delivered to the player even if it made no tool calls. The orchestrator always proceeds to round 2: if round 1 made tool calls, round 2 narrates from the results (unchanged from today); if round 1 made no tool calls, round 2 still runs, given the same baseline context already injected and explicitly told no further data is available, and must answer only from that context. This is the path that prevents an ungrounded round-1 guess from ever reaching the player un-vetted.
- **Heuristic flags pure chitchat:** today's single-round-trip shortcut is kept — round 1's direct text answer is delivered as-is. Greetings were never the danger case; only world-state claims hallucinate, and forcing every "hello" through two round trips would double latency and API cost (a real concern on BYOK) for no safety benefit.
- **Default, when ambiguous:** treat as world-state-relevant (force round 2). This mirrors the existing heuristic's own caution-leaning default, applied in the direction that matters here — when in doubt, ground it.

### Residual risk

This is a heuristic, not a guarantee. A query the heuristic misclassifies as pure chitchat when it actually needed grounding could still slip through ungrounded. The blast radius is much smaller than today's open-ended shortcut (which applied to every query with no classification at all), but it isn't zero. Tightening the heuristic's keyword coverage based on observed misses is expected to be an iterative, low-risk follow-up — not blocking this design.

---

## Section 5 — Testing: From Fixed Examples to a Generative Harness

### Today

- `AdvisorPromptIntegrationTest` — 12 fixed test methods, each a single hardcoded query against a small set of hardcoded contexts, several asserting `assertSentences(r, 3)` — an assertion against the truncation cap being removed in Section 3, and so already invalid once that cap is gone.
- `EnvironmentToolSimulationTest` — 4 fixed test methods, one fixed phrasing per intent ("where am i," "what biome is this?," "what do you see around us," "what was I doing?").

Both replay the same exact wording every run and only check tool-invocation/hallucination properties already covered structurally; neither checks persona/voice at all (that's exactly the gap `PV-03`–`PV-05` represent).

### Change

Both are replaced by one generative harness: randomized contexts (extending the existing `Random`-based hotbar generation already in `t04`/`t08`) crossed with multiple paraphrasings per query intent (not one fixed phrasing each), run for N trials per property. Two kinds of checks per trial, matching Section 3's split:

- **Code-deterministic** (generalizes what's already covered today): correct tool called for the query's category, or correctly *not* called for chitchat under the new heuristic gate (Section 4); the banned-phrase denylist (Section 3); no literal hallucinated terms absent from context (generalizing the existing `HALLUCINATION_TERMS` check).
- **Persona-consistency** (not mechanically checkable, so it needs a judge): an LLM-judge or rubric pass scoring voice/brevity/no-lecturing against the persona bio. This only runs in the test harness, never at runtime per player query, so the extra API cost is bounded and acceptable even on BYOK.

Pass/fail moves from binary to a **pass-rate threshold** per property (e.g. "≥90% of trials"), since LLM output is probabilistic — a single noisy trial failing shouldn't redden the whole suite, but a high failure rate should. Trial count per property stays small and configurable (cost-bounded, not exhaustive). The harness still skips entirely without `run/client/.env`, same as today (`assumeTrue`).

This is also what gives `PV-03`, `PV-04`, and `PV-05` in `docs/advisor-validation-checklist.md` a path off `PENDING` — they've been stuck there since 2026-06-16 specifically because nothing automated could check them. The judge pass is that check. Updating those items' actual status is implementation-time work once the harness produces real evidence — this spec only establishes the mechanism, consistent with the validation checklist's own Standing Rule.

---

## Section 6 — Cleanup

Two items, both confirmed stale, both in scope:

1. **`ChatCommandHandlerTest.java`** — deleted. It tests `#a`/`#f` prefix parsing, which is confirmed abandoned; nothing in this design or the current advisor flow relies on command-prefix routing.
2. **`README.md`'s Indiana Jones / `#a`/`#f` session-status content** — superseded by Section 2. Following the project's own precedent for handling superseded-but-true history (how `feed-me.md`'s legacy `V-*` results were kept rather than deleted, with a forward-pointer note added — see `docs/superpowers/specs/2026-06-17-advisor-validation-checklist-design.md`), a short note is added at the top of that section marking it superseded and pointing at this spec, rather than deleting project history.

`ChatCommandHandler.java` (the source class) and `model_config.json`'s `"flavor"`/`"advisory"` role split are explicitly excluded — see Non-Goals.

---

## Edge Cases and Error Handling

Unchanged from `docs/superpowers/specs/2026-06-13-tool-calling-design.md`'s "Edge Cases and Error Handling" section (client disconnect between round trips, tool execution failure, unrecognized tool name, malformed model response, round-trip-2 timeout, LLM unavailable) — none of those paths are touched by this redesign. The one addition is the residual-risk note in Section 4 (heuristic misclassification), which is a behavior risk, not an error-handling path — there's no exception to catch, just an imperfect classification that's a known, accepted trade-off.

---

## Constraints

- Do not commit without explicit authorization from Dragon.
- Pre-flight checklist required before any Java source edit.
- `./gradlew test` required before any change is reported complete; `test-audit-trail.md` entry appended per change.
- `test-audit-trail.md` is append-only.
- Nothing blocks the Minecraft main/server/render thread.
- No source scanning (Java/JSON/config) without explicit authorization — applies to the two Non-Goal follow-up items if they're picked up later.

---

## Open Questions

None — the heuristic-gate scope, persona base identity, truncation removal, and cleanup scope were all resolved during brainstorming.
