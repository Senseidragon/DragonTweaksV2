# Advisor Classification Unification & Denylist-Repair Redesign — Design Spec

**Date:** 2026-06-21
**Mod:** DragonTweaksV2 (NeoForge 1.21.1)
**Branch:** advisor-persona-grounding
**Status:** Design confirmed; not yet implemented.

**Supersedes (partially):** `docs/superpowers/specs/2026-06-20-advisor-persona-grounding-design.md` — specifically:
- Section 3's disposition of "banned literal phrases" (mechanical strip only → strip remains the safety floor, with an optional sentence-level repair pass layered on top).
- Section 4's heuristic gate (`WORLD_STATE_SIGNALS`/`CHITCHAT_SIGNALS` flat boolean, plus `shouldIncludeHistory`'s two separate inline keyword lists) → replaced by one unified classification table.

Everything else in the 06-20 spec — the persona bio, the reasoning-token strip, the truncation-cap removal, the generative test harness, the cleanup items — is unaffected and remains authoritative and implemented.

---

## Problem Statement

Post-implementation review of the 06-20 redesign surfaced three issues, found by reading the actual shipped code (`ToolCallOrchestrator.java`, `OpenRouterService.java`, and the four tool classes) rather than guessed at:

1. **Four independent, disagreeing keyword lists.** `WORLD_STATE_SIGNALS`, `CHITCHAT_SIGNALS` (in `isWorldStateRelevant`), and two more inline lists in `shouldIncludeHistory` were each authored separately and already disagree: `shouldIncludeHistory` excludes history for inventory/scan phrasings ("what do i have", "scan") but has no equivalent exclusion for environment/status phrasings, with no principled reason for the asymmetry — it's an artifact of four lists evolving independently, not a deliberate design choice.
2. **`get_status` doesn't return HP.** `WORLD_STATE_SIGNALS` includes `"health"`, implying `get_status` grounds health questions, but `StatusTool.execute()` only reports `MobEffectCategory.HARMFUL` detrimental effects — current/max HP isn't in its output at all.
3. **`where` is ambiguous between two tools.** `get_environment` (biome/elevation) and `scan_area` (nearby entities/terrain) both plausibly answer different senses of "where am i" / "what's around me," and the flat signal list can't express "call both and let the answer draw from whichever is relevant."

Separately, the mechanical denylist strip shipped in the 06-20 spec (Task 2: `OpenRouterService.stripBannedPhrases`) is a safety floor, not a quality mechanism — excising a banned phrase mid-sentence can leave a grammatically broken fragment. Hit-rate logging was added this session (`stripBannedPhrases` now logs before/after when it actually changes text) to measure how often this happens before committing to a repair mechanism's added complexity.

## Goals

- Replace the four ad-hoc keyword lists (`WORLD_STATE_SIGNALS`, `CHITCHAT_SIGNALS`, `shouldIncludeHistory`'s two lists) with one unified classification table: category → signal keywords → tool(s) to ground with → history-inclusion flag.
- Extend `get_status` to report current/max HP, closing the `health`-signal-to-tool mismatch.
- Make the round-1-miss path deterministic for any query that matches a known category: execute that category's tool(s) directly rather than asking the model a second time and trusting its freeform retry text.
- Add an optional sentence-level repair pass for denylist hits, without weakening the existing mechanical-strip safety floor (it still runs as the fallback on repair failure/timeout).

## Non-Goals

- No change to `LoreIndex`, tool JSON schemas (`AdvisorTool.definition()`), `AdvisorStatusMonitor`, or entity lifecycle.
- No semantic/ML classifier — this is still a keyword-table heuristic, consolidated rather than replaced with something fundamentally different. Residual misclassification risk (06-20 spec's Section 4 "Residual risk") still applies and is still accepted.
- No token-cost optimization. Measured this session against `model_config.json`'s advisory-role model (`openai/gpt-oss-120b`): simple query ≈ $0.000026, average ≈ $0.00007, worst case (triple lore match + grounding retry + tool call + one repair call) ≈ $0.00067. At this model tier, cost is not a constraint on any decision below.

---

## Section A — `get_status` HP Extension

`StatusTool.execute()` gains a leading line reporting current/max HP before the existing detrimental-effects text. Example outputs:

- `"Health: 20/20. No active detrimental effects."`
- `"Health: 6/20. Active effects: Poison (12s remaining)."`

`playerHasDetrimentalEffects(ServerPlayer)` is unaffected — it stays scoped to detrimental effects only, since that's a distinct check used elsewhere (not just `get_status`'s return text).

---

## Section B — Unified Classification Table

Replaces `WORLD_STATE_SIGNALS`, `CHITCHAT_SIGNALS`, and `shouldIncludeHistory`'s two inline lists with one table. Each row: category name, signal keywords, tool(s) to force-ground with, default history-inclusion flag.

| Category | Signal keywords | Tool(s) | History |
|---|---|---|---|
| `environment` | "what time", "weather", "biome" | `get_environment` | true |
| `inventory` | "inventory", "holding", "wearing", "what do i have" | `get_inventory` | false |
| `status` | "health", "effect", "how am i feeling" | `get_status` | true |
| `scan` | "creature", "threat" | `scan_area` | false |
| `location` | "where", "nearby", "around me", "see" | `get_environment`, `scan_area` | false |
| `chitchat` | "hello", "hi", "hey", "thanks", "thank you", "bye", "goodbye", "lol" | (none) | true |

Notes:
- `location` resolves the `where`-is-ambiguous problem by grounding with **both** tools rather than picking one arbitrarily — `handleQuery` already supports executing a list of tool calls in one round, so this is a data change (two tools in one category), not a new code path.
- A message can only match one category (first match wins, in table order above) — this preserves the same "no double classification" property the old flat lists had.
- **Continuity override**, independent of category: if the message contains "you said", "earlier", "what about", or "tell me more", history is forced `true` regardless of the matched category's default (including overriding `inventory`/`scan`/`location`'s normal `false`). This is `shouldIncludeHistory`'s existing override, kept as a standalone check layered on top of the table rather than folded into it — it's a property of *any* query, not a category itself.
- **No category match** (message doesn't hit any row): treated as ambiguous. History defaults `true` (matches today's default). No tool is force-injected on a round-1 miss, since there's no known tool for an unmatched category — this case keeps the existing round-2 nudge-and-retry behavior (see Step 4 below) as the fallback, because deterministic injection has nothing to inject.

---

## Section C — `handleQuery` Restructure (5 Steps)

Replaces today's round-1/round-2 contract (`ToolCallOrchestrator.handleQuery`, current lines ~73–127) with:

1. **Classify.** Match `playerMessage` against the table in Section B → category (or none), its tool(s), its history flag. Apply the continuity override independently (may flip history `true`).
2. **Build context.** Lore injection, system prompt, history (per Step 1's flag), tool definitions — unchanged from today.
3. **Round 1** (`sendWithTools`). If it returns tool calls, execute + deliver via `sendWithToolResults` (round 2) — unchanged from today.
4. **Round-1 miss, category known.** If round 1 returned no tool calls **and** Step 1 matched a category with tool(s): do not ask the model again. Execute that category's tool(s) directly (server-side, deterministic) and deliver via `sendWithToolResults` using those results. This replaces today's "ask again with a nudge, trust whatever round 2 says" path — it removes the model's discretion from the one case that was actually risky, and is cheaper (one fewer LLM round trip) than today's retry.
5. **Round-1 miss, no category (chitchat or unmatched).** If the matched category has no tool (chitchat) or no category matched at all: chitchat delivers round 1's text as-is (today's single-round-trip shortcut, unchanged). Unmatched/ambiguous keeps today's round-2 nudge-and-retry as a fallback, since Step 4's deterministic injection has no tool to act on.

Net effect: `WORLD_STATE_SIGNALS`, `CHITCHAT_SIGNALS`, and both of `shouldIncludeHistory`'s inline lists are deleted. `isWorldStateRelevant` and `shouldIncludeHistory` are replaced by one `classify(String): Classification` (or equivalent) that returns category, tool(s), and history flag together, plus the standalone continuity-override check.

---

## Section D — Denylist Repair Loop

`OpenRouterService.stripBannedPhrases` (06-20 spec, Task 2) remains the unconditional safety floor — it always runs, and its hit-rate logging (added this session) stays in place regardless of repair success. The repair loop is a quality layer on top, not a replacement.

- **Trigger.** After parsing a response, check whether stripping would change the text (a hit occurred).
- **Isolation granularity.** Sentence-level (confirmed this session) — split the pre-strip text into sentences; only sentences containing a banned phrase are sent for repair. Clean sentences are left untouched.
- **Repair call.** `repairSentence(sentence, hitPhrases)`: no system prompt, no tools, no history — a single user-style instruction: *"Rephrase the following sentence to remove these words while preserving its meaning and voice. Output only the rephrased sentence, nothing else. Words to remove: {phrases}. Sentence: \"{sentence}\""*. `max_tokens=60`, `temperature=0.3`.
- **Timeout.** `REPAIR_TIMEOUT_MS = 5000` (confirmed this session, reduced from the previously-floated reuse of `TOOL_TIMEOUT_MS`'s 10s).
- **Re-verification.** Run the same banned-phrase check against the rephrased sentence.
  - Clean → splice the rephrased sentence back into the response in place of the original.
  - Still dirty, or the call errors/times out → fall back to mechanically stripping **only that sentence** via the existing `stripBannedPhrases` regex (not the whole response).
- **Multiple hits.** Independent sentences are repaired in parallel (`CompletableFuture.allOf`), each with its own independent re-verify/fallback — one sentence's repair failure doesn't block another's.
- **Pipeline change required.** `sendWithTools` and `sendWithToolResults` move from `.thenApplyAsync(...)` to `.thenComposeAsync(...)` at the point where the response is currently parsed, so the new async repair step can be chained. `parseOpenRouterResponse` stops calling `stripBannedPhrases` directly; the chain becomes parse (sync, no strip) → `repairBannedPhrasesIfNeeded(text)` (async, `CompletableFuture<String>`) → final text.

---

## Constraints

- Do not commit without explicit authorization from Dragon.
- Pre-flight checklist required before any Java source edit.
- `./gradlew test` required before any change is reported complete; `test-audit-trail.md` entry appended per change.
- `test-audit-trail.md` is append-only.
- Nothing blocks the Minecraft main/server/render thread — the repair loop's network call runs on the existing async executor path, identical in shape to every other OpenRouter call.

## Open Questions

None — classification table contents, the `location` dual-tool resolution, HP output format, repair-loop granularity, timeout, and cost posture were all resolved during design review this session.
