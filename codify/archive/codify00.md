# Codify Session Snapshot

## Metadata
- Created at: Wed Jun 24 04:45:02 EDT 2026
- Project root: C:\Users\sense\Desktop\DragonTweaksV2
- Command invoked: manual consolidation (not `/codify`) — see Carry-Forward Context
- Additional user arguments: none

## Current Project Status

- Branch `advisor-persona-grounding`, created off `advisor` (merge-base `5bd8baa`). 8 commits as of 2026-06-22 (`86b637f` … `847826b`), none pushed or merged. No commits exist beyond `847826b` as of this consolidation.
- **Git access for the assistant is revoked — standing, not session-scoped.** Dragon revoked it explicitly on 2026-06-22 after a process misstep (mistakenly pivoting to `git log` history search instead of using a known, already-correct Minecraft API approach). Do not run any git command, including read-only ones, until Dragon explicitly restores access.
- A `VillageLocatorTool` (`find_nearest_village`, using vanilla's own `ChunkGenerator.findNearestMapStructure` + `StructureTags.VILLAGE` — the same API `/locate village` uses) was implemented, wired into `ToolCallOrchestrator`'s classification table, registered in `DragonTweaksV2.java`, covered by 2 new tests, and live-verified working correctly (including surviving a typo in the player's query and updating correctly as the player moved). It remains **uncommitted** because git access is off.
- The classification-table redesign is implemented and committed, replacing the old flat `WORLD_STATE_SIGNALS` / `CHITCHAT_SIGNALS` / `shouldIncludeHistory` keyword lists: categories are `village`, `environment`, `inventory`, `status`, `scan`, `location`, `chitchat`, each carrying required tool(s) and a history-inclusion flag, plus an independent continuity-override check (`you said` / `earlier` / `what about` / `tell me more` forces history=true regardless of category). On a round-1 miss for a known category, the orchestrator force-injects that category's tool(s) deterministically rather than trusting a second free-text model attempt.
- `get_status` was extended to report current/max HP (closing a gap where a `health` signal existed but the tool didn't return HP).
- Denylist-repair settled on dropping the unrepairable sentence entirely rather than mechanically stripping individual words (word-stripping produced broken grammar in production). `stripBannedPhrases` and its tests were deleted as dead code.
- Brevity is 100% persona-driven; `truncateToSentences` was deleted entirely (a hard cut could sever a response mid-warning). `max_tokens` remains only as an anti-runaway safety valve, not a shaping tool.
- Persona base identity is "seasoned adventurer, speaks from experience" — a continuity choice, not a new character. No proper name beyond "the Veteran," used once, in-character, when a player asked.
- The build-tool gate (`structurize:sceptergold` possession required before the advisor responds) is intentional and privacy-motivated (Dragon: "people not interested in using the mod should not have an invisible stalker following them everywhere"). A login-hint chat message — using the real crafting recipe extracted from the shipped Structurize jar, not a guess — tells new players how to unlock it instead of silently rejecting their first chat.
- `dt.purge` is a player-facing-only session-recovery command; the LLM must never be given a way to call or trigger it. (Captured durably as memory `feedback_dt_purge_player_only.md`.)
- `AdvisorEntityManager.onPlayerLogin` spawns `AdvisorEntity` for every player unconditionally, regardless of build-tool possession — in tension with the build-tool gate's own stated privacy rationale. Found while implementing the login hint; never explicitly raised to Dragon as its own decision point. Still open.
- Two confirmed live-testing bugs remain unresolved — see Current Task List and Carry-Forward Context.

## Current Task List

- Hold branch-finishing (merge/push/PR for `advisor-persona-grounding`) until live testing is clean — explicit Dragon directive ("no merges until live testing has actually been completed"), still in force.
- **Finding #5 (higher priority — a confirmed-false answer already reached a player):** the deterministic tool-injection synthesis trusts forced tool results even when those tools don't cover the actual question. Example: "is there a ladder nearby?" matched the `location` category, force-injecting `get_environment` + `scan_area` — neither reports block-level detail — yet the response confidently said "No ladder in sight," contradicted by a player-provided screenshot showing a ladder directly ahead. Not diagnosed beyond this observation; not fixed.
- **Finding #4 (lower priority — unreproduced):** one response leaked third-person chain-of-thought text ("the user asked… the assistant responded… no further actions needed") concatenated with echoes of the two prior turns and missing spaces between sentences. Not caught by the `<|...|>` strip, the banned-phrase denylist, or the blank-response fallback. Raw pre-strip response-content logging was added to `OpenRouterService.parseOpenRouterResponse` to help capture a reproduction; it has not recurred across the testing done so far (~12+ varied live exchanges).
- Commit the uncommitted village-locator work (tool, classification wiring, tests) once Dragon restores git access.
- Raise the `AdvisorEntityManager` unconditional-spawn-vs-build-tool-gate tension to Dragon as an explicit decision point.
- Long-standing, low-priority, never actioned: backfill `docs/minecraft-lore/effects/*.md` for the 4 resources-only lore entries to reach docs/resources parity; `docs/advisor-validation-checklist.md` items `PV-03`/`PV-04`/`PV-05` (and other `PENDING` items) remain unresolved; a minor persona 4th-wall slip ("you're the player-character in this world") was noted but not fixed.

## Session Topics Developed

This file is a **consolidation**, not a single session's record. It replaces `codify/codify00.md` through `codify07.md` (2026-06-19 through 2026-06-22 EDT), which are preserved at `codify/archive/` rather than deleted.

What was cut as obsolete or superseded:
- The multi-session design journey behind the now-implemented classification-table redesign — the original 3-way rule-classification split, the discarded "retry with a terse correction" denylist proposal, and multiple unconfirmed HP-extension / category-table proposal iterations — all superseded by what is actually implemented and committed (see Current Project Status above).
- The `feed-me.md`-discovery and `README.md`-staleness investigative narrative — both already resolved (`README.md` annotated; `feed-me.md` superseded by `codify/` per `START-HERE.md`).
- Boilerplate restating standing CLAUDE.md constraints, repeated nearly verbatim across most of the original 8 files.
- Stale per-session task-tracker IDs that no longer map to anything current.

What was kept: settled design decisions with their rationale (still true, still load-bearing), the two genuinely unresolved bugs in full diagnostic detail, and the handful of long-standing low-priority deferred items that persisted unchanged across many snapshots.

## Files Discussed Or Modified

| File | Status |
|---|---|
| `codify/codify00.md`–`codify07.md` | moved to `codify/archive/` (preserved, not deleted) |
| `codify/codify00.md` (this file) | created — replaces the moved originals as the new baseline |

No project source, test, or doc file was touched by this consolidation; it is scoped entirely to the `codify/` directory.

## Violations, Corrections, And User Directives

Carried forward because they remain actively governing, not because they recurred during this consolidation:
- No compound shell commands (`cmd1 && cmd2 && cmd3` chained for effect) — corrected once in the archived history, standing since.
- Git access revoked, standing, not session-scoped, until Dragon explicitly restores it.
- "Nothing is sacred other than the do-not-block-main-thread rule" — proactively flag structural roadblocks rather than patching around them.
- Don't bandage reactively — prefer structural fixes over reactive denylist-style patches (shaped the Finding #4/#5 approach so far; should continue to shape Finding #5's eventual fix).
- Before claiming a documentation/memory write happened, verify the file write actually occurred — don't infer it from having said it would. (Violated twice in the archived history: the Finding #4 audit-trail entry, and the `dt.purge` memory directive. Both have since been corrected.)

## Decisions Made

Only decisions that remain current; superseded intermediate decisions were dropped (see Session Topics Developed).

1. Persona-driven brevity, no truncation; `max_tokens` is an anti-runaway valve only.
2. Denylist-hit responses drop the unrepairable sentence rather than mechanically stripping words.
3. Final classification-table shape: `village`/`environment`/`inventory`/`status`/`scan`/`location`/`chitchat` categories, each with required tool(s) + history-inclusion flag, plus an independent continuity-override check; deterministic tool force-injection (or deterministic refusal) replaces trusting a second free-text model attempt.
4. `get_status` reports HP.
5. Build-tool gate stays; a login-hint message (real recipe, extracted from the shipped jar) replaces silent rejection.
6. `dt.purge` is permanently player-only; never exposed to the LLM.
7. Village-locator tool built using vanilla's own structure-locating API, returning direction + 50-block-rounded distance, no coordinates.
8. Branch-finishing stays blocked until live testing is clean or Dragon explicitly accepts a known limitation.

## Deferred / Not Yet Implemented

- Finding #5 (confirmed-false grounded answer) — most urgent open item.
- Finding #4 (chain-of-thought leak) — diagnostic logging in place, unreproduced.
- Committing the village-locator work — blocked on git access restoration.
- Raising the `AdvisorEntityManager` unconditional-spawn-vs-privacy-gate tension explicitly to Dragon.
- `docs/minecraft-lore/effects/*.md` backfill; `docs/advisor-validation-checklist.md` `PV-03`/`PV-04`/`PV-05`; the minor persona 4th-wall slip — all long-standing, low-priority, unrequested.

## Carry-Forward Context

This file replaces `codify/codify00.md`–`codify07.md` (2026-06-19 through 2026-06-22 EDT) as the session-continuity baseline, per Dragon's explicit request to consolidate the growing codify sequence before it became an unbounded per-session context cost — the SessionStart hook at `scripts/hooks/show-start-here.py` globs `codify/codify*.md` non-recursively and injects every match as auto-loaded context on every session start. The 8 originals are archived, not deleted, at `codify/archive/codify00.md`–`codify07.md`, consistent with this project's own precedent of annotating superseded-but-true history rather than erasing it (see how `README.md`'s stale section was handled).

This consolidation intentionally compresses or omits the session-by-session narrative of *how* the current design was reached (proposal iterations, rejected alternatives, the specific exchanges that produced each decision) in favor of *what* is currently true and *why* it's still true. If a future session needs the original blow-by-blow reasoning behind a settled decision — why persona framing was chosen over negative-constraint prompting, or the exact sequence of live-test findings that produced the current denylist-repair design — it exists in `codify/archive/`: `codify01.md`–`codify03.md` cover the persona-grounding design brainstorm, and `codify06.md`–`codify07.md` cover the implementation and live-testing sessions that found Findings #4 and #5.

Going forward, `/codify` will write its next snapshot as `codify01.md` in this same `codify/` directory — the command determines its next index by scanning `codify/codify<N>.md`, which now starts fresh from this file.

Standing constraints remain in force: no git commands of any kind until Dragon explicitly restores access; no compound shell commands; `./gradlew test` plus an append-only `test-audit-trail.md` entry required before any code change is reported complete; nothing blocks the Minecraft main/client/render thread.

## Next Recommended Action

Diagnose Finding #5 (the confirmed-false "no ladder in sight" answer) first, since it has a known trigger (a matched category whose forced tools don't cover the actual question) while Finding #4 has none yet — once Dragon is ready to resume technical work, and once git access is restored for committing the village-locator work already sitting in the working tree.
