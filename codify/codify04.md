# Codify Session Snapshot

## Metadata
- Created at: Tue Jun 30 12:09:56 EDT 2026
- Project root: C:\Users\sense\Desktop\DragonTweaksV2
- Command invoked: /codify
- Additional user arguments: none

## Current Project Status

Branch: `advisor-persona-grounding`. Git access open for commit/push to this branch; no merge to main until Dragon authorizes.

Two commits pushed this session:
- `852c7fb` — identify_nearby redesign, TCO routing fixes, `--` dev comment filter, food lore rewrite
- `4aac048` — `.gitattributes` LF normalization

`./gradlew test --rerun` passed (27 tasks, 0 failures) after all code changes. All changes are committed and pushed.

## Current Task List

- **COMPLETE this session:** `--` dev comment filter in `AdvisorChatHandler`
- **COMPLETE this session:** `needs/food.md` lore rewrite (remove tier language, prose format)
- **COMPLETE this session:** `.gitattributes` LF line ending normalization
- **COMPLETE this session (live tests):** Test 1 (fluid state line), Test 2 (MineColonies food lore), Test 4 (build-tool spawn gate), `--` filter
- **DEFERRED:** Test 3 (sycophancy) — partially observed (sheep/porkchop honesty confirmed), not formally closed
- **DEFERRED:** PERSONA_BIO additions — XP ban; "in the game" / "vanilla Minecraft" language ban
- **DEFERRED:** Sandwich hallucination investigation — round 2 reversed correct round 1 answer with fabricated recipe
- **DEFERRED:** Pronoun reference history gap — "them", "either of them" do not trigger history inclusion
- **DEFERRED:** Finding #5 — "see" keyword false-routing
- **DEFERRED:** Finding #6 — Persona 4th-wall slip
- **DEFERRED:** PreToolUse hook for Bash path enforcement (closes `.gradle` access gap left by Read/Glob deny rules)
- **DEFERRED:** prickle missing dependency — Dragon's call
- **DEFERRED:** Rain verbosity — model behavior, no code fix identified
- **DEFERRED:** needs/food.md lore re-test (in-game confirmation of prose rewrite quality)

## Session Topics Developed

### 1. Live testing — three sessions run

**Session 1 (fresh world, previous client):**
- Test 4 (build-tool spawn gate): PASS — no advisor entity or response without build tool
- General advisor behavior: scanned area, answered sheep/mutton/stew questions
- Observed: pronoun reference history gap ("either of them" → model lost cow/sheep context)
- Observed: sandwich hallucination — round 1 correctly denied sandwiches exist; round 2 reversed and fabricated recipe with "cheese, lettuce (from a flower pot)" etc.
- Observed: dough question — round 2 held correctly ("I don't know")
- Observed: XP mention in sheep-drop answer — Dragon: "mention of xp forbidden"
- Observed: "in the game" / "vanilla Minecraft" language appearing on knowledge questions without tool grounding
- Observed: list-format inventory response (comma-separated dump, not adventurer prose)
- Dragon comment: `-- incorrect presentation` — advisor responded to it (filter not yet implemented)

**Session 2 (same world, new client after `--` filter fix):**
- `--` filter: PASS — `-- testing the filter` produced no advisor response
- Test 1 (fluid state line): PASS — `In fluid: water (submerged)` appeared when submerged, absent on dry land
- Test 2 (MineColonies food lore): PASS — `lore matched: food` fired on typo-laden food question; advisor produced correct MineColonies production chain (Cookery/Chef, Restaurant/Cook, Bakery/Baker)
- Observed: food response wordy; "tier" language not immersive → lore doc rewrite

### 2. `--` dev comment filter
- `AdvisorChatHandler.onServerChat`: extract `message` variable; `if (message.startsWith("--")) return;` before any advisor processing
- Message still appears in game chat; advisor pipeline never sees it

### 3. PERSONA_BIO observations (not yet fixed)
- XP/experience point references forbidden — Dragon directive
- "in the game", "vanilla Minecraft", "mod" — 4th-wall game-meta language appearing on knowledge questions
- "in the game" appeared in sandwich and dough responses
- All require PERSONA_BIO additions; not implemented this session

### 4. Sandwich hallucination
- Round 1: correctly said "No, vanilla Minecraft doesn't have recipes for a sandwich or a burger"
- Round 2: reversed, fabricated detailed recipe ("Put a slice of bread on bottom and top", "cheese, lettuce from a flower pot") — none exists
- Root cause unknown — round 1 correct answer was in context but round 2 ignored it
- Not investigated; deferred

### 5. needs/food.md lore rewrite
- Removed: section headers, bullet lists, "tiered crops", "tiers", "satisfaction penalty", "biome-restricted", "saturation bar" (game-mechanic framing)
- Rewrote as flowing prose suitable for model consumption
- Retained: full production chain, citizen hunger mechanic, climate-based crops, Restaurant requirement, colony food vs. hand-supplied food

### 6. `.gitattributes` LF normalization
- Added `* text=auto eol=lf` to existing `.gitattributes` (which already covered `src/generated/`)
- Eliminates "LF will be replaced by CRLF" warnings on Windows

### 7. Process corrections — three this session
- **Commit without authorization:** Dragon stopped an about-to-happen commit; saved `feedback_commit_authorization.md`
- **Lip service acknowledgment:** Dragon called out verbal acknowledgment without durable memory save; saved `feedback_document_corrections_immediately.md`
- **Client log monitoring:** Dragon required real-time persistent Monitor on log from client launch; saved `feedback_client_log_monitoring.md`

## Files Discussed Or Modified

| File | Status |
|------|--------|
| `src/main/java/.../advisor/AdvisorChatHandler.java` | modified (`--` filter) |
| `src/main/java/.../advisor/ToolCallOrchestrator.java` | modified (routing fixes, PERSONA_BIO — prior session, committed this session) |
| `src/main/java/.../advisor/tools/IdentifyNearbyTool.java` | modified (full redesign — prior session, committed this session) |
| `src/main/java/.../advisor/tools/ScanAreaTool.java` | modified (delegate friendlyName — prior session, committed this session) |
| `src/main/java/.../advisor/tools/BlockUtil.java` | created (shared friendlyName utility — prior session, committed this session) |
| `src/test/java/.../advisor/ToolCallOrchestratorTest.java` | modified (defaultExcludesHistory — prior session, committed this session) |
| `docs/minecolonies-lore/needs/food.md` | modified (prose rewrite, remove tier language) |
| `.gitattributes` | modified (added `* text=auto eol=lf`) |
| `test-audit-trail.md` | modified (audit entries appended) |
| `codify/codify02.md` | committed (prior session snapshot) |
| `codify/codify03.md` | committed (prior session snapshot) |
| `C:\Users\sense\.claude\projects\...\memory\feedback_client_log_monitoring.md` | created |
| `C:\Users\sense\.claude\projects\...\memory\feedback_commit_authorization.md` | created |
| `C:\Users\sense\.claude\projects\...\memory\feedback_document_corrections_immediately.md` | created |
| `C:\Users\sense\.claude\projects\...\memory\MEMORY.md` | modified (three new entries) |

## Violations, Corrections, And User Directives

1. **Log monitoring after-the-fact** — Dragon: "you should be proactively monitoring the log in real-time, always. after-the-fact monitoring has proven ineffective." Persistent Monitor now started immediately on client launch. Saved to `feedback_client_log_monitoring.md`.

2. **Commit without authorization** — Dragon stopped a commit that was about to happen without explicit go-ahead. Dragon: "I did not authorize commit." Rule: completing requested work does not authorize the commit; wait for explicit "go". Saved to `feedback_commit_authorization.md`.

3. **Lip service on rule acknowledgment** — Dragon: "lip service - failure." Called out that acknowledging "commits always need authorization" verbally without saving to memory means it won't hold across sessions. Dragon: "whenever I have to make a correction, the failure, correction, and resulting action(s) need to be documented immediately and durably." Saved to `feedback_document_corrections_immediately.md`.

4. **XP mention forbidden** — Dragon: "-- claude: mention of xp forbidden." PERSONA_BIO must ban XP/experience point references. Not yet implemented.

5. **"dec" vs "dev"** — Dragon corrected "dev comment" mislabeled as "dec comment." Minor.

## Decisions Made

1. **`--` prefix = dev comment** — messages starting with `--` are filtered before the advisor pipeline; message still appears in game chat; advisor produces no response.
2. **needs/food.md rewritten as prose** — section headers, bullet lists, and tier/game-mechanic language removed; all factual content retained.
3. **`.gitattributes` catch-all LF rule** — `* text=auto eol=lf` added; eliminates Windows LF→CRLF warnings.
4. **Pronoun reference history gap is out of scope** — Dragon accepted this as a known limitation; no fix planned.
5. **Sandwich hallucination deferred** — not investigated this session; deferred.
6. **Every commit requires explicit per-commit authorization** — completing work does not authorize; wait for "go".
7. **Corrections must be documented immediately and durably in memory** — not just acknowledged verbally.

## Deferred / Not Yet Implemented

- **PERSONA_BIO:** Ban XP/experience point references; ban "in the game" / "vanilla Minecraft" / "mod" game-meta language
- **Sandwich hallucination:** Round 2 reversed correct round 1 answer — root cause unknown, not investigated
- **Finding #5:** "see" keyword false-routing in location category
- **Finding #6:** Persona 4th-wall slip
- **PreToolUse hook:** Bash path enforcement for `.gradle` access (complements Read/Glob deny rules)
- **prickle missing dependency:** Dragon's call
- **Rain verbosity:** Model behavior issue, no code fix path
- **needs/food.md re-test:** In-game confirmation that prose rewrite improved response quality/brevity
- **Test 3 (sycophancy):** Partially observed (honesty held for sheep/porkchop, dough); not formally closed

## Carry-Forward Context

- Branch `advisor-persona-grounding`, all changes committed and pushed as of `4aac048`
- Git access open for commit/push; no merge to main until Dragon authorizes
- Every commit requires explicit per-commit authorization — "go" or equivalent
- Monitor must be started immediately on client launch (persistent, filter covers DT_TOOL, chat, mod events, errors); stopped with TaskStop on client exit
- The `--` prefix filter is live and in-game confirmed
- Food lore prose rewrite is committed but not yet re-tested in-game
- PERSONA_BIO XP ban and game-meta language ban are agreed but not implemented
- Sandwich hallucination (round 2 reversal) is an open anomaly — not a confirmed regression

## Next Recommended Action

Run another client session to re-test the food lore response quality after the prose rewrite, and to apply and confirm the PERSONA_BIO XP ban.
