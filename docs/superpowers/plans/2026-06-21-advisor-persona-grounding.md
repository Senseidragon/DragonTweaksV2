# Advisor Persona & Grounding Redesign — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the advisor's prose rule-list system prompt with a single persona bio, move mechanically-checkable rules to code, close the structural gap that lets an ungrounded answer reach the player, and replace two fixed-example test files with a generative pass-rate harness.

**Architecture:** Three production files change (`ToolCallOrchestrator`, `AdvisorChatHandler`, `OpenRouterService`); two fixed-example test files are deleted and replaced by one generative test class; one dead test file is deleted; `README.md` gets a superseded-annotation.

**Tech Stack:** Java 21, NeoForge 21.1.230, JUnit 5, Mockito, Gradle/NeoGradle. Live-API tests gated by `run/client/.env` presence (existing `assumeTrue` pattern) — no new test infrastructure dependencies.

## Global Constraints

- Spec of record: `docs/superpowers/specs/2026-06-20-advisor-persona-grounding-design.md` ("Design approved; not yet implemented").
- Do not commit without Dragon's explicit per-session authorization (CLAUDE.md). Every "Commit" step below assumes that authorization is already in force for the execution session; if it is not, stage with `git add` and stop — do not run `git commit`.
- Pre-flight checklist required before touching any Java source file (CLAUDE.md) — this plan touches `ToolCallOrchestrator.java`, `AdvisorChatHandler.java`, `OpenRouterService.java`, and several test files. None of this touches tick handlers, event handlers, networking sockets directly, file I/O, or pathfinding — it touches LLM-call orchestration code that is already fully async (`CompletableFuture`, dedicated executors). No new blocking calls are introduced anywhere in this plan; every `.get(...)` call added is on a background-executor future, mirroring the exact pattern already used by the surrounding code.
- `./gradlew test` must pass and a `test-audit-trail.md` entry must be appended before any task is reported complete (CLAUDE.md Code Change Gate). `test-audit-trail.md` is append-only.
- Tool JSON schemas (`AdvisorTool.definition()`) are unchanged by this plan — only system-prompt prose and orchestration logic change.
- `docs/minecraft-lore/`, `LoreIndex`, `AdvisorStatusMonitor`, entity lifecycle, and session/history storage are out of scope (per spec Non-Goals) and are not touched by any task below.
- `ChatCommandHandler.java` (the source class) and `model_config.json`'s `"flavor"`/`"advisory"` role split are explicitly out of scope (per spec Non-Goals) — Task 6 deletes only the test file, never the source class.

---

## Task 1: Consolidate the persona bio into one source of truth

**Files:**
- Modify: `src/main/java/io/github/senseidragon/dragontweaksv2/advisor/ToolCallOrchestrator.java:181-213` (replace `buildSystemPrompt` body; add `PERSONA_BIO` constant)
- Modify: `src/main/java/io/github/senseidragon/dragontweaksv2/advisor/AdvisorChatHandler.java:26-32` (repoint `SYSTEM_PROMPT` at the shared constant)
- Modify: `src/main/java/io/github/senseidragon/dragontweaksv2/advisor/AdvisorChatHandler.java:173-181` (remove inline duplicate prompt string in the fallback branch)
- Test: `src/test/java/io/github/senseidragon/dragontweaksv2/advisor/ToolCallOrchestratorTest.java`

**Interfaces:**
- Produces: `ToolCallOrchestrator.PERSONA_BIO` (package-private `static final String`, package `io.github.senseidragon.dragontweaksv2.advisor`) — the single persona bio text, consumed by `AdvisorChatHandler.SYSTEM_PROMPT`.
- Consumes: nothing new from other tasks.

This is the same persona/prompt entry point `AdvisorPromptIntegrationTest` currently builds prompts from (Task 5 replaces that file, but Task 1 must land first since later tasks' tests assert against the new bio text).

- [ ] **Step 1: Write the failing tests**

Add to `src/test/java/io/github/senseidragon/dragontweaksv2/advisor/ToolCallOrchestratorTest.java` (inside the existing class, after the `// ── history decision tests` block):

```java
    // ── persona bio tests ──────────────────────────────────────────────────────

    @Test
    void buildSystemPromptUsesPersonaBioNotOldProseRules() {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(null, List.of(), false);
        String prompt = orc.buildSystemPrompt("");
        assertTrue(prompt.contains("seasoned adventurer"), "Expected persona bio identity");
        assertTrue(prompt.contains("you just stop"), "Expected persona bio brevity trait");
        assertFalse(prompt.contains("Never say"), "Old banned-word prose rule should be gone");
        assertFalse(prompt.contains("CRITICAL:"), "Old meta-instruction framing should be gone");
    }

    @Test
    void systemPromptConstantMatchesOrchestratorPersonaBio() {
        assertEquals(ToolCallOrchestrator.PERSONA_BIO, AdvisorChatHandler.SYSTEM_PROMPT);
    }
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew test --tests "io.github.senseidragon.dragontweaksv2.advisor.ToolCallOrchestratorTest"`
Expected: FAIL — compile error, `ToolCallOrchestrator.PERSONA_BIO` does not exist yet.

- [ ] **Step 3: Add the persona bio constant and rewrite `buildSystemPrompt`**

In `ToolCallOrchestrator.java`, replace lines 181-213 (the entire current `buildSystemPrompt` method) with:

```java
    static final String PERSONA_BIO =
        "You are a seasoned adventurer who has spent years living in and surviving this land. " +
        "You speak plainly, from experience, the way someone talks while working — not the way someone lectures. " +
        "You answer exactly what you're asked, nothing more; you don't pad an answer with extra observations nobody asked for, " +
        "and you don't tack on a closing remark when you're done, you just stop. " +
        "You never speak on your surroundings, your gear, or your condition unless you've actually checked them first — " +
        "you're careful that way, the same as any adventurer who's survived this long. " +
        "You've never set foot outside this land and have nothing to say about places, things, or ideas beyond it.\n\n";

    String buildSystemPrompt(String loreBlock) {
        StringBuilder sb = new StringBuilder();
        sb.append(PERSONA_BIO);
        if (!loreBlock.isEmpty()) {
            sb.append(loreBlock);
        }
        return sb.toString();
    }
```

Place the `PERSONA_BIO` field near the top of the class, alongside the other `static final` fields (after `TOOL_TIMEOUT_MS`).

- [ ] **Step 4: Repoint `AdvisorChatHandler.SYSTEM_PROMPT` at the shared constant**

In `AdvisorChatHandler.java`, replace lines 26-32:

```java
    // Kept for tests and external callers that build prompts directly
    public static final String SYSTEM_PROMPT =
        "You are a friendly mentor and guide: helpful, warm, and concise. " +
        "Always speak in natural, conversational sentences — never use lists or sentence fragments. " +
        "Greetings and farewells: one brief reply, 4 words or fewer. " +
        "Questions and requests: answer in one or two natural sentences, then stop. " +
        "Speak only from the context below; if something is missing, say so briefly.\n\n";
```

with:

```java
    // Single source of truth for the advisor's persona is ToolCallOrchestrator.PERSONA_BIO.
    // Kept public for tests and external callers that build prompts directly.
    public static final String SYSTEM_PROMPT = ToolCallOrchestrator.PERSONA_BIO;
```

- [ ] **Step 5: Remove the inline duplicate prompt in the fallback branch**

In `AdvisorChatHandler.java`, inside `handleChat`, replace lines 173-181:

```java
        } else {
            // Fallback: direct queryAsync path (pre-orchestrator or during init)
            String lore = LoreIndex.inject(chatText);
            String systemPrompt = "You are a friendly mentor and guide: helpful, warm, and concise. " +
                "Always speak in natural, conversational sentences — never use lists or sentence fragments. " +
                "Greetings and farewells: one brief reply, 4 words or fewer. " +
                "Questions and requests: answer in one or two natural sentences, then stop. " +
                "Speak only from the context below; if something is missing, say so briefly.\n\n" + lore + context;
            session.addMessage("user", chatText);
```

with:

```java
        } else {
            // Fallback: direct queryAsync path (pre-orchestrator or during init)
            String lore = LoreIndex.inject(chatText);
            String systemPrompt = SYSTEM_PROMPT + lore + context;
            session.addMessage("user", chatText);
```

- [ ] **Step 6: Run tests to verify they pass**

Run: `./gradlew test --tests "io.github.senseidragon.dragontweaksv2.advisor.ToolCallOrchestratorTest" --tests "io.github.senseidragon.dragontweaksv2.advisor.AdvisorChatHandlerTest"`
Expected: PASS — all tests in both classes green.

- [ ] **Step 7: Append `test-audit-trail.md` entry**

Record: date, "Consolidated persona bio into ToolCallOrchestrator.PERSONA_BIO; AdvisorChatHandler.SYSTEM_PROMPT now references it instead of an independent, divergent prompt", tests covered (`buildSystemPromptUsesPersonaBioNotOldProseRules`, `systemPromptConstantMatchesOrchestratorPersonaBio`), pass/fail result, and the note "compile-verified; in-game persona-voice confirmation deferred to Task 5's generative harness."

- [ ] **Step 8: Commit**

```bash
git add src/main/java/io/github/senseidragon/dragontweaksv2/advisor/ToolCallOrchestrator.java src/main/java/io/github/senseidragon/dragontweaksv2/advisor/AdvisorChatHandler.java src/test/java/io/github/senseidragon/dragontweaksv2/advisor/ToolCallOrchestratorTest.java test-audit-trail.md
git commit -m "feat(advisor): consolidate persona bio into ToolCallOrchestrator.PERSONA_BIO"
```

---

## Task 2: Move banned-phrase enforcement from prose to a post-generation denylist

**Files:**
- Modify: `src/main/java/io/github/senseidragon/dragontweaksv2/openrouter/OpenRouterService.java:1-31` (add `import java.util.regex.Pattern;`)
- Modify: `src/main/java/io/github/senseidragon/dragontweaksv2/openrouter/OpenRouterService.java:300-326` (`parseOpenRouterResponse` — apply the new strip)
- Modify: `src/main/java/io/github/senseidragon/dragontweaksv2/openrouter/OpenRouterService.java` (add `BANNED_PHRASES` + `stripBannedPhrases` near `truncateToSentences`, before Task 3 removes the latter)
- Test: `src/test/java/io/github/senseidragon/dragontweaksv2/openrouter/OpenRouterServiceTest.java`

**Interfaces:**
- Produces: `OpenRouterService.stripBannedPhrases(String)` (package-private `static String`) — strips literal banned phrases, case-insensitively, word-bounded.
- Consumes: nothing from other tasks. Independent of Task 1 (different file).

- [ ] **Step 1: Write the failing tests**

Add to `OpenRouterServiceTest.java`, directly above the `// truncateToSentences` comment block:

```java
    // stripBannedPhrases

    @Test
    void stripsBannedMechanicWordFromResponse() {
        assertEquals("I checked the area.", OpenRouterService.stripBannedPhrases("I checked the scan area."));
    }

    @Test
    void stripsBannedClosingPhrase() {
        String result = OpenRouterService.stripBannedPhrases("Stay alert. That's all.");
        assertEquals("Stay alert.", result);
    }

    @Test
    void leavesCleanResponseUnchanged() {
        assertEquals("Stay alert out there.", OpenRouterService.stripBannedPhrases("Stay alert out there."));
    }

    @Test
    void stripIsCaseInsensitive() {
        assertEquals("I checked the area.", OpenRouterService.stripBannedPhrases("I checked the SCAN area."));
    }

    @Test
    void stripDoesNotMangleWordsContainingBannedSubstring() {
        // "scanner" contains "scan" but is a different word — word-boundary match must not touch it.
        assertEquals("The scanner hums.", OpenRouterService.stripBannedPhrases("The scanner hums."));
    }
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew test --tests "io.github.senseidragon.dragontweaksv2.openrouter.OpenRouterServiceTest"`
Expected: FAIL — compile error, `OpenRouterService.stripBannedPhrases` does not exist yet.

- [ ] **Step 3: Implement `stripBannedPhrases`**

Add `import java.util.regex.Pattern;` to the import block at the top of `OpenRouterService.java`.

Add the following near `truncateToSentences` (which Task 3 removes next — placing them adjacently keeps the diff small):

```java
    private static final List<String> BANNED_PHRASES =
        List.of("scan", "data", "results", "that's all", "hope that helps");

    static String stripBannedPhrases(String text) {
        if (text == null || text.isBlank()) return text == null ? "" : text.trim();
        String result = text;
        for (String phrase : BANNED_PHRASES) {
            result = result.replaceAll("(?i)\\b" + Pattern.quote(phrase) + "\\b[.,!]?\\s*", " ");
        }
        return result.replaceAll("\\s{2,}", " ").trim();
    }
```

- [ ] **Step 4: Wire the strip into `parseOpenRouterResponse`**

In `parseOpenRouterResponse` (around line 321-324), replace:

```java
        JsonElement contentEl = message.get("content");
        String text = (contentEl != null && !contentEl.isJsonNull())
            ? contentEl.getAsString().replaceAll("<\\|[^|]*\\|>", "").trim()
            : "";
        return new OpenRouterResponse(text, List.of());
```

with:

```java
        JsonElement contentEl = message.get("content");
        String text = (contentEl != null && !contentEl.isJsonNull())
            ? stripBannedPhrases(contentEl.getAsString().replaceAll("<\\|[^|]*\\|>", "").trim())
            : "";
        return new OpenRouterResponse(text, List.of());
```

This is the single choke point both `sendWithTools` and `sendWithToolResults` flow through, so the denylist applies to every text response the player can receive, in both round 1 and round 2.

- [ ] **Step 5: Run tests to verify they pass**

Run: `./gradlew test --tests "io.github.senseidragon.dragontweaksv2.openrouter.OpenRouterServiceTest"`
Expected: PASS — all tests green, including the 5 new ones.

- [ ] **Step 6: Append `test-audit-trail.md` entry**

Record: date, "Added stripBannedPhrases post-generation denylist (scan/data/results/that's all/hope that helps), wired into parseOpenRouterResponse alongside the existing reasoning-token strip", tests covered (the 5 listed above), pass/fail result.

- [ ] **Step 7: Commit**

```bash
git add src/main/java/io/github/senseidragon/dragontweaksv2/openrouter/OpenRouterService.java src/test/java/io/github/senseidragon/dragontweaksv2/openrouter/OpenRouterServiceTest.java test-audit-trail.md
git commit -m "feat(openrouter): strip banned mechanic/closing phrases from advisor responses"
```

---

## Task 3: Remove the dead sentence-truncation cap

**Files:**
- Modify: `src/main/java/io/github/senseidragon/dragontweaksv2/openrouter/OpenRouterService.java:454-467` (delete `truncateToSentences`)
- Modify: `src/test/java/io/github/senseidragon/dragontweaksv2/openrouter/OpenRouterServiceTest.java:111-156` (delete its 7 tests)

**Interfaces:**
- Produces: nothing (pure removal).
- Consumes: nothing.

**Note on the spec's premise:** the spec (Section 3) describes this as removing "the truncation cap, currently invoked in the response-delivery path." That's not quite what the code shows: `truncateToSentences` exists but is **never called** anywhere in production code today — it's already dead, exercised only by its own unit tests. `AdvisorPromptIntegrationTest`'s `assertSentences(r, 3)` checks were asserting on the model's *prompt-instructed* brevity, not on any code-level truncation. This doesn't change the disposition (drop it — brevity is persona-driven per Task 1), it just means this task is pure dead-code deletion rather than removing a live call site.

- [ ] **Step 1: Delete the dead method**

Remove lines 454-467 from `OpenRouterService.java`:

```java
    static String truncateToSentences(String text, int max) {
        if (text == null || text.isBlank()) return text == null ? "" : text.trim();
        text = text.trim();
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '.' || c == '!' || c == '?') {
                while (i + 1 < text.length() && "!?.".indexOf(text.charAt(i + 1)) >= 0) i++;
                count++;
                if (count == max) return text.substring(0, i + 1).trim();
            }
        }
        return text;
    }
```

- [ ] **Step 2: Delete its tests**

Remove lines 111-156 from `OpenRouterServiceTest.java` — the `// truncateToSentences` comment and all 7 `truncate_*` test methods, up to (not including) the `// sendWithTools — HTTP stubbing tests` section comment.

- [ ] **Step 3: Run the full OpenRouterServiceTest class to confirm nothing else references the removed method**

Run: `./gradlew test --tests "io.github.senseidragon.dragontweaksv2.openrouter.OpenRouterServiceTest"`
Expected: PASS — compiles clean (no remaining references) and all remaining tests green.

- [ ] **Step 4: Append `test-audit-trail.md` entry**

Record: date, "Removed dead truncateToSentences method and its 7 unit tests — was never wired into any response-delivery path; brevity is now persona-driven per Task 1", tests covered (none added; 7 removed, full class re-run to confirm clean compile), pass/fail result.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/io/github/senseidragon/dragontweaksv2/openrouter/OpenRouterService.java src/test/java/io/github/senseidragon/dragontweaksv2/openrouter/OpenRouterServiceTest.java test-audit-trail.md
git commit -m "refactor(openrouter): remove dead truncateToSentences sentence-cap"
```

---

## Task 4: Close the round-1 grounding shortcut

**Files:**
- Modify: `src/main/java/io/github/senseidragon/dragontweaksv2/advisor/ToolCallOrchestrator.java:56-122` (restructure `handleQuery`; add `isWorldStateRelevant`, `deliverTextOnly`, `executeToolsAndDeliver`)
- Test: `src/test/java/io/github/senseidragon/dragontweaksv2/advisor/ToolCallOrchestratorTest.java`

**Interfaces:**
- Produces: `ToolCallOrchestrator.isWorldStateRelevant(String)` (package-private `boolean`, mirrors `shouldIncludeHistory`'s visibility/testability pattern).
- Consumes: `PERSONA_BIO` is unaffected by this task (the grounding-retry nudge text is appended to the already-built `systemPrompt`, not baked into the persona bio itself).

- [ ] **Step 1: Write the failing tests**

Add to `ToolCallOrchestratorTest.java`, after the existing history-decision tests:

```java
    // ── world-state relevance tests — no player needed ────────────────────────

    @Test
    void worldStateSignalRequiresGrounding() {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(null, List.of(), false);
        assertTrue(orc.isWorldStateRelevant("where am i"));
        assertTrue(orc.isWorldStateRelevant("what's the weather like"));
        assertTrue(orc.isWorldStateRelevant("what creatures are nearby"));
    }

    @Test
    void chitchatSignalSkipsGrounding() {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(null, List.of(), false);
        assertFalse(orc.isWorldStateRelevant("hello there"));
        assertFalse(orc.isWorldStateRelevant("hey, thanks for the help"));
    }

    @Test
    void ambiguousQueryDefaultsToGrounding() {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(null, List.of(), false);
        assertTrue(orc.isWorldStateRelevant("how do I make a sword?"));
    }

    // ── round-1 shortcut closure — handleQuery path tests ──────────────────────

    @Test
    void worldStateQueryWithNoToolCallForcesSecondAttempt() throws Exception {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(openRouter, List.of(), false);
        when(openRouter.sendWithTools(any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(new OpenRouterResponse("It's a cavern.", List.of())))
            .thenReturn(CompletableFuture.completedFuture(new OpenRouterResponse("I have no way to check that.", List.of())));

        AdvisorSession session = new AdvisorSession(20);
        List<String> delivered = new ArrayList<>();
        orc.handleQuery("where am i", null, session, delivered::add, Runnable::run, () -> true)
            .get(5, TimeUnit.SECONDS);

        verify(openRouter, times(2)).sendWithTools(any(), any(), any(), any());
        assertEquals(List.of("I have no way to check that."), delivered);
    }

    @Test
    void worldStateQuerySelfCorrectsOnSecondAttempt() throws Exception {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(openRouter, List.of(), false);
        ToolCall call = new ToolCall("id1", "get_environment", new JsonObject());
        when(openRouter.sendWithTools(any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(new OpenRouterResponse("It's a cavern.", List.of())))
            .thenReturn(CompletableFuture.completedFuture(new OpenRouterResponse(null, List.of(call))));
        when(openRouter.sendWithToolResults(any(), any(), any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture("You're on the surface in a forest."));

        AdvisorSession session = new AdvisorSession(20);
        List<String> delivered = new ArrayList<>();
        orc.handleQuery("where am i", null, session, delivered::add, Runnable::run, () -> true)
            .get(5, TimeUnit.SECONDS);

        assertEquals(List.of("You're on the surface in a forest."), delivered);
    }

    @Test
    void chitchatWithNoToolCallStillUsesSingleRoundTripShortcut() throws Exception {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(openRouter, List.of(), false);
        when(openRouter.sendWithTools(any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(new OpenRouterResponse("Hey there.", List.of())));

        AdvisorSession session = new AdvisorSession(20);
        List<String> delivered = new ArrayList<>();
        orc.handleQuery("hello there", null, session, delivered::add, Runnable::run, () -> true)
            .get(5, TimeUnit.SECONDS);

        verify(openRouter, times(1)).sendWithTools(any(), any(), any(), any());
        assertEquals(List.of("Hey there."), delivered);
    }
```

- [ ] **Step 2: Run tests to verify the new ones fail**

Run: `./gradlew test --tests "io.github.senseidragon.dragontweaksv2.advisor.ToolCallOrchestratorTest"`
Expected: FAIL — compile error, `isWorldStateRelevant` does not exist yet; `worldStateQueryWithNoToolCallForcesSecondAttempt` and `worldStateQuerySelfCorrectsOnSecondAttempt` would also behave incorrectly under today's logic even once compiling (round 1's ungrounded text would be delivered directly).

- [ ] **Step 3: Add `isWorldStateRelevant` and restructure `handleQuery`**

In `ToolCallOrchestrator.java`, add near `shouldIncludeHistory`:

```java
    private static final List<String> WORLD_STATE_SIGNALS = List.of(
        "where", "what time", "weather", "biome", "inventory", "holding", "wearing",
        "health", "effect", "nearby", "around me", "see", "creature", "threat"
    );

    private static final List<String> CHITCHAT_SIGNALS = List.of(
        "hello", "hi", "hey", "thanks", "thank you", "bye", "goodbye", "lol"
    );

    // package-private for testing
    boolean isWorldStateRelevant(String playerMessage) {
        String lower = playerMessage.toLowerCase(Locale.ROOT);
        if (WORLD_STATE_SIGNALS.stream().anyMatch(s -> containsWord(lower, s))) return true;
        if (CHITCHAT_SIGNALS.stream().anyMatch(s -> containsWord(lower, s))) return false;
        return true; // default: ground it when ambiguous
    }

    private static boolean containsWord(String lowerText, String phrase) {
        if (phrase.contains(" ")) return lowerText.contains(phrase);
        return java.util.regex.Pattern.compile("\\b" + java.util.regex.Pattern.quote(phrase) + "\\b")
            .matcher(lowerText).find();
    }
```

Then replace the entire package-private `handleQuery` overload (lines 56-122) with:

```java
    // package-private: allows testing without ServerPlayer (pass null for player, supply executor and isOnline)
    CompletableFuture<Void> handleQuery(
            String playerMessage,
            ServerPlayer player,
            AdvisorSession session,
            Consumer<String> responseCallback,
            Consumer<Runnable> executor,
            BooleanSupplier isOnline) {

        return CompletableFuture.runAsync(() -> {
            try {
                String loreBlock = LoreIndex.inject(playerMessage);
                String systemPrompt = buildSystemPrompt(loreBlock);

                List<ChatMessage> history = shouldIncludeHistory(playerMessage)
                    ? session.getMessages() : List.of();

                List<JsonObject> defs = toolDefinitions();

                OpenRouterResponse rt1 = openRouter
                    .sendWithTools(systemPrompt, history, playerMessage, defs)
                    .get(TOTAL_TIMEOUT_MS, TimeUnit.MILLISECONDS);

                if (rt1.hasToolCalls()) {
                    executeToolsAndDeliver(rt1.toolCalls(), playerMessage, systemPrompt, history, defs,
                        player, session, responseCallback, executor, isOnline);
                    return;
                }

                if (!isWorldStateRelevant(playerMessage)) {
                    deliverTextOnly(rt1.textContent(), playerMessage, session, responseCallback, isOnline);
                    return;
                }

                // World-state-relevant query, but round 1 made no tool calls — don't trust it
                // un-vetted. Force a second attempt before delivering anything to the player.
                String groundingPrompt = systemPrompt +
                    "\n\nYour previous answer did not call a tool, but this question may require " +
                    "checked information. Call the appropriate tool now if it's relevant, or state " +
                    "plainly that you have no way to check this.";
                OpenRouterResponse rt2 = openRouter
                    .sendWithTools(groundingPrompt, history, playerMessage, defs)
                    .get(TOTAL_TIMEOUT_MS, TimeUnit.MILLISECONDS);

                if (rt2.hasToolCalls()) {
                    executeToolsAndDeliver(rt2.toolCalls(), playerMessage, systemPrompt, history, defs,
                        player, session, responseCallback, executor, isOnline);
                    return;
                }

                // Model still didn't call a tool — deliver its retry text rather than rt1's
                // original, un-nudged guess. This is the best available outcome; don't loop further.
                String fallbackText = (rt2.textContent() != null && !rt2.textContent().isBlank())
                    ? rt2.textContent() : rt1.textContent();
                deliverTextOnly(fallbackText, playerMessage, session, responseCallback, isOnline);

            } catch (java.util.concurrent.TimeoutException e) {
                String name = player != null ? player.getName().getString() : "unknown";
                LOGGER.warn("[ToolCallOrchestrator] Timeout for player {}", name);
                if (isOnline.getAsBoolean()) responseCallback.accept("I got a bit turned around — ask me again.");
            } catch (Exception e) {
                LOGGER.error("[ToolCallOrchestrator] Unexpected error", e);
            }
        });
    }

    private void deliverTextOnly(String textContent, String playerMessage, AdvisorSession session,
                                  Consumer<String> responseCallback, BooleanSupplier isOnline) {
        String text = textContent != null ? textContent : "";
        if (text.isBlank()) {
            LOGGER.warn("[ToolCallOrchestrator] Model returned blank response for query: {}", playerMessage);
            if (isOnline.getAsBoolean()) responseCallback.accept("Ask me again — I didn't quite get that.");
            return;
        }
        session.addMessage("user", playerMessage);
        session.addMessage("advisor", text);
        responseCallback.accept(text);
    }

    private void executeToolsAndDeliver(List<ToolCall> calls, String playerMessage, String systemPrompt,
                                         List<ChatMessage> history, List<JsonObject> defs,
                                         ServerPlayer player, AdvisorSession session,
                                         Consumer<String> responseCallback, Consumer<Runnable> executor,
                                         BooleanSupplier isOnline) throws Exception {
        List<ToolResult> results = executeTools(calls, player, executor);

        if (!isOnline.getAsBoolean()) {
            session.addMessage("user", playerMessage);
            session.addMessage("advisor", "[disconnected]");
            return;
        }

        String finalText = openRouter
            .sendWithToolResults(systemPrompt, history, playerMessage, calls, results, defs)
            .get(TOTAL_TIMEOUT_MS, TimeUnit.MILLISECONDS);

        deliverTextOnly(finalText, playerMessage, session, responseCallback, isOnline);
    }
```

`executeTools`, `toolDefinitions`, `shouldIncludeHistory`, and `isOnline` are unchanged — only the package-private `handleQuery` overload's body changes, plus the two new private helpers it calls. The public single-arg `handleQuery` overload (lines 44-53) is untouched; it delegates to this one.

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew test --tests "io.github.senseidragon.dragontweaksv2.advisor.ToolCallOrchestratorTest"`
Expected: PASS — all tests green, including the existing `textOnlyResponsePathDeliveredToPlayer` (question `"hi"` classifies as chitchat under `CHITCHAT_SIGNALS`, so behavior is unchanged) and the 6 new tests.

- [ ] **Step 5: Run the full advisor test package to confirm no regressions**

Run: `./gradlew test --tests "io.github.senseidragon.dragontweaksv2.advisor.*"`
Expected: PASS. (`EnvironmentToolSimulationTest` and `AdvisorPromptIntegrationTest` still exist at this point — Task 5 replaces them next — so this run also covers them; both should still pass since the chitchat shortcut path and tool-call path they exercise are unchanged in shape, only the no-tool-call/world-state path gained the retry.)

- [ ] **Step 6: Append `test-audit-trail.md` entry**

Record: date, "Closed the round-1 grounding shortcut: ToolCallOrchestrator.handleQuery now forces a second sendWithTools attempt before delivering an ungrounded answer to a world-state-relevant query; pure chitchat keeps the single-round-trip shortcut", tests covered (the 6 listed above plus full advisor-package regression run), pass/fail result, and the note "heuristic keyword coverage is illustrative per spec; tightening based on observed misses is an accepted iterative follow-up, not a blocker."

- [ ] **Step 7: Commit**

```bash
git add src/main/java/io/github/senseidragon/dragontweaksv2/advisor/ToolCallOrchestrator.java src/test/java/io/github/senseidragon/dragontweaksv2/advisor/ToolCallOrchestratorTest.java test-audit-trail.md
git commit -m "feat(advisor): force grounding retry before delivering ungrounded world-state answers"
```

---

## Task 5: Replace fixed-example tests with a generative persona/grounding harness

**Files:**
- Create: `src/test/java/io/github/senseidragon/dragontweaksv2/advisor/AdvisorPersonaGenerativeTest.java`
- Delete: `src/test/java/io/github/senseidragon/dragontweaksv2/advisor/AdvisorPromptIntegrationTest.java`
- Delete: `src/test/java/io/github/senseidragon/dragontweaksv2/advisor/EnvironmentToolSimulationTest.java`

**Interfaces:**
- Consumes: `ToolCallOrchestrator` (public constructor + `handleQuery`), `AdvisorTool` interface, `EnvironmentTool`/`InventoryTool`/`StatusTool`/`ScanAreaTool` (only their `definition()`, for realistic JSON schemas), `OpenRouterService` (real instance, gated by `run/client/.env`), `OpenRouterService.stripBannedPhrases`'s effect is exercised indirectly (every response already passes through it before this test sees it — no direct call needed).
- Produces: nothing consumed by other tasks — this is the terminal test artifact for Section 5 of the spec, and the harness `judgePersonaConsistency` is what gives `PV-03`–`PV-05` in `docs/advisor-validation-checklist.md` a path off `PENDING` (flipping their actual status is deferred until the harness has run against real gameplay, per that checklist's own Standing Rule — not part of this task).

This harness is a live-API integration test, skipped automatically without `run/client/.env` — same pattern as the two files it replaces. It runs a small, cost-bounded number of trials per query intent, with randomized tool-result contexts and varied phrasing per trial, and asserts a pass-rate threshold per property rather than a binary pass/fail per trial (since LLM output is probabilistic).

- [ ] **Step 1: Write the harness**

Create `src/test/java/io/github/senseidragon/dragontweaksv2/advisor/AdvisorPersonaGenerativeTest.java`:

```java
package io.github.senseidragon.dragontweaksv2.advisor;

import com.google.gson.JsonObject;
import io.github.senseidragon.dragontweaksv2.advisor.tools.EnvironmentTool;
import io.github.senseidragon.dragontweaksv2.advisor.tools.InventoryTool;
import io.github.senseidragon.dragontweaksv2.advisor.tools.ScanAreaTool;
import io.github.senseidragon.dragontweaksv2.advisor.tools.StatusTool;
import io.github.senseidragon.dragontweaksv2.openrouter.OpenRouterService;
import net.minecraft.server.level.ServerPlayer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Generative replacement for AdvisorPromptIntegrationTest and EnvironmentToolSimulationTest.
 * Runs randomized-context, paraphrased-intent trials against the real ToolCallOrchestrator
 * and asserts a pass-rate threshold per property instead of one fixed example per case.
 * Skipped automatically if run/client/.env is absent.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdvisorPersonaGenerativeTest {

    private static final int TRIALS_PER_INTENT = 2;
    private static final double PASS_RATE_THRESHOLD = 0.8;

    private static final List<String> BANNED_PHRASES =
        List.of("scan", "data", "results", "that's all", "hope that helps");

    private static final List<String> HALLUCINATION_POOL =
        List.of("biscuit", "flask", "coyote", "lantern", "rope", "knife", "rusted key");

    private record Intent(String name, List<String> paraphrases, String expectedTool) {}

    private static final List<Intent> INTENTS = List.of(
        new Intent("environment",
            List.of("what time is it", "what's the weather like", "what biome is this"),
            "get_environment"),
        new Intent("inventory",
            List.of("what's in my inventory", "what am i carrying", "what do i have on me"),
            "get_inventory"),
        new Intent("status",
            List.of("how am i feeling", "what's affecting me right now", "do i have any effects on me"),
            "get_status"),
        new Intent("nearby",
            List.of("what's around me", "what creatures are nearby", "what do you see around us"),
            "scan_area"),
        new Intent("chitchat",
            List.of("hello there", "hey, thanks for the help", "good to see you"),
            null)
    );

    private record TrialResult(String intent, String question, String response,
                                boolean correctToolCall, boolean noBannedPhrase,
                                boolean noHallucination, boolean personaConsistent) {}

    private OpenRouterService service;
    private final List<TrialResult> results = new ArrayList<>();

    @BeforeAll
    void setUpAndRunTrials() throws Exception {
        Path envPath = Path.of("run/client/.env");
        assumeTrue(Files.exists(envPath), "Skipping — run/client/.env not found");
        service = new OpenRouterService(Path.of("run/client"));
        service.initAsync(reason -> fail("OpenRouter init failed: " + reason))
               .get(15, TimeUnit.SECONDS);
        assumeTrue(service.isEnabled(), "Skipping — OpenRouter failed to enable");

        Random random = new Random();
        for (Intent intent : INTENTS) {
            for (int i = 0; i < TRIALS_PER_INTENT; i++) {
                String question = intent.paraphrases().get(random.nextInt(intent.paraphrases().size()));
                results.add(runTrial(intent, question, random));
            }
        }
    }

    @AfterAll
    void tearDown() {
        if (service != null) service.shutdown();
    }

    private TrialResult runTrial(Intent intent, String question, Random random) throws Exception {
        TrackingTool envTool = new TrackingTool("get_environment", new EnvironmentTool().definition(),
            randomEnvironmentReading(random));
        TrackingTool invTool = new TrackingTool("get_inventory", new InventoryTool().definition(),
            randomInventoryReading(random));
        TrackingTool statusTool = new TrackingTool("get_status", new StatusTool().definition(),
            randomStatusReading(random));
        TrackingTool scanTool = new TrackingTool("scan_area", new ScanAreaTool().definition(),
            randomScanReading(random));

        ToolCallOrchestrator orchestrator = new ToolCallOrchestrator(
            service, List.of(envTool, invTool, statusTool, scanTool), false);

        AtomicReference<String> response = new AtomicReference<>();
        AdvisorSession session = new AdvisorSession(20);
        orchestrator.handleQuery(question, null, session, response::set, Runnable::run, () -> true)
            .get(30, TimeUnit.SECONDS);

        String reply = response.get() != null ? response.get() : "";

        boolean correctToolCall = intent.expectedTool() == null
            ? Stream.of(envTool, invTool, statusTool, scanTool).noneMatch(TrackingTool::wasCalled)
            : toolNamed(intent.expectedTool(), envTool, invTool, statusTool, scanTool).wasCalled();

        String lowerReply = reply.toLowerCase(Locale.ROOT);
        boolean noBannedPhrase = BANNED_PHRASES.stream().noneMatch(lowerReply::contains);
        boolean noHallucination = HALLUCINATION_POOL.stream().noneMatch(lowerReply::contains);
        boolean personaConsistent = judgePersonaConsistency(question, reply);

        System.out.println("[" + intent.name() + "] Q: " + question + " -> A: " + reply);
        return new TrialResult(intent.name(), question, reply, correctToolCall, noBannedPhrase,
            noHallucination, personaConsistent);
    }

    private TrackingTool toolNamed(String name, TrackingTool... tools) {
        for (TrackingTool t : tools) if (t.name().equals(name)) return t;
        throw new IllegalArgumentException("No tracking tool named " + name);
    }

    private boolean judgePersonaConsistency(String question, String response) {
        if (response.isBlank()) return false;
        String judgePrompt =
            "You are grading a roleplay response for character consistency. " +
            "The character is a seasoned adventurer: plain-spoken, speaks from experience, never lectures, " +
            "answers only what was asked, never tacks on a closing line, and never reveals it checked tools, " +
            "scans, or data. " +
            "Question asked: \"" + question + "\"\n" +
            "Character's response: \"" + response + "\"\n" +
            "Does the response stay in character on ALL of these traits? Reply with exactly one word: PASS or FAIL.";
        try {
            String verdict = service.query("advisory", judgePrompt).get(30, TimeUnit.SECONDS);
            return verdict != null && verdict.toUpperCase(Locale.ROOT).contains("PASS");
        } catch (Exception e) {
            return false;
        }
    }

    // ── pass-rate assertions ──────────────────────────────────────────────────

    @Test
    void correctToolCallPassRate() {
        assertPassRate(TrialResult::correctToolCall, "correct tool call");
    }

    @Test
    void noBannedPhrasePassRate() {
        assertPassRate(TrialResult::noBannedPhrase, "no banned phrase");
    }

    @Test
    void noHallucinationPassRate() {
        assertPassRate(TrialResult::noHallucination, "no hallucination");
    }

    @Test
    void personaConsistencyPassRate() {
        assertPassRate(TrialResult::personaConsistent, "persona consistency");
    }

    private void assertPassRate(Predicate<TrialResult> property, String label) {
        assumeTrue(!results.isEmpty(), "No trials ran — skipping");
        long passed = results.stream().filter(property).count();
        double rate = (double) passed / results.size();
        assertTrue(rate >= PASS_RATE_THRESHOLD,
            String.format("%s pass rate %.0f%% below threshold %.0f%% (%d/%d). Failures: %s",
                label, rate * 100, PASS_RATE_THRESHOLD * 100, passed, results.size(),
                results.stream().filter(property.negate())
                    .map(r -> "[" + r.intent() + "] \"" + r.question() + "\" -> \"" + r.response() + "\"")
                    .collect(Collectors.joining("; "))));
    }

    // ── randomized tool-result generators ────────────────────────────────────

    private static String randomEnvironmentReading(Random r) {
        List<String> times = List.of("morning", "midday", "dusk", "night", "midnight");
        List<String> weathers = List.of("clear", "raining", "thunderstorm");
        List<String> biomes = List.of("plains", "forest", "swamp", "desert", "taiga");
        return "Time: " + pick(times, r) + ". Day: " + (1 + r.nextInt(50)) + ". Weather: " + pick(weathers, r) +
               ". Biome: " + pick(biomes, r) + ". Elevation: " + (r.nextInt(80) - 20) + " blocks above sea level.";
    }

    private static String randomInventoryReading(Random r) {
        List<String> pool = List.of("Bread", "Iron Sword", "Torch", "Arrow", "Apple", "Coal", "Stick", "Bucket");
        List<String> items = new ArrayList<>(pool);
        Collections.shuffle(items, r);
        int count = 1 + r.nextInt(4);
        return items.subList(0, count).stream().map(i -> i + " x1").collect(Collectors.joining("\n"));
    }

    private static String randomStatusReading(Random r) {
        if (r.nextBoolean()) return "No active detrimental effects.";
        List<String> effects = List.of("Poison", "Slowness", "Weakness", "Hunger");
        return "Active effects: " + pick(effects, r) + " (" + (5 + r.nextInt(30)) + "s remaining).";
    }

    private static String randomScanReading(Random r) {
        if (r.nextBoolean()) return "Nothing notable detected nearby.";
        List<String> creatures = List.of("Passive: 2x Sheep", "Hostile: 1x Zombie", "Neutral: 1x Wolf");
        return pick(creatures, r);
    }

    private static <T> T pick(List<T> options, Random r) {
        return options.get(r.nextInt(options.size()));
    }

    // ── tracking tool ─────────────────────────────────────────────────────────

    private static class TrackingTool implements AdvisorTool {
        private final String name;
        private final JsonObject def;
        private final String returnValue;
        private boolean called = false;

        TrackingTool(String name, JsonObject def, String returnValue) {
            this.name = name;
            this.def = def;
            this.returnValue = returnValue;
        }

        @Override public String name() { return name; }
        @Override public JsonObject definition() { return def; }
        @Override public String execute(JsonObject args, ServerPlayer player) {
            called = true;
            return returnValue;
        }
        public boolean wasCalled() { return called; }
    }
}
```

- [ ] **Step 2: Run the new harness on its own**

Run: `./gradlew test --tests "io.github.senseidragon.dragontweaksv2.advisor.AdvisorPersonaGenerativeTest"`
Expected: SKIP (4 tests skipped) if `run/client/.env` is absent, or PASS if present and the live model clears every pass-rate threshold. If it fails, read the printed failure message (it lists every failing trial's question/response) before changing thresholds or prompts — a failure here is signal about Tasks 1, 2, or 4, not noise to silence.

- [ ] **Step 3: Delete the two superseded fixed-example test files**

```bash
rm src/test/java/io/github/senseidragon/dragontweaksv2/advisor/AdvisorPromptIntegrationTest.java
rm src/test/java/io/github/senseidragon/dragontweaksv2/advisor/EnvironmentToolSimulationTest.java
```

- [ ] **Step 4: Run the full advisor test package**

Run: `./gradlew test --tests "io.github.senseidragon.dragontweaksv2.advisor.*"`
Expected: PASS (or SKIP for the live-API classes without `.env`) — no leftover references to the deleted classes anywhere else in the suite (neither deleted file was a dependency of any other test; confirm with a clean compile).

- [ ] **Step 5: Append `test-audit-trail.md` entry**

Record: date, "Replaced AdvisorPromptIntegrationTest (12 fixed examples) and EnvironmentToolSimulationTest (4 fixed examples) with AdvisorPersonaGenerativeTest — randomized contexts x paraphrased intents x 2 trials each, pass-rate threshold (80%) per property across tool-call correctness, banned-phrase denylist, hallucination, and LLM-judged persona consistency", tests covered (the 4 pass-rate assertions), pass/fail result, and explicitly state whether `run/client/.env` was present for this run (live confirmation) or absent (compile-only verification, live confirmation deferred).

- [ ] **Step 6: Commit**

```bash
git add src/test/java/io/github/senseidragon/dragontweaksv2/advisor/AdvisorPersonaGenerativeTest.java test-audit-trail.md
git rm src/test/java/io/github/senseidragon/dragontweaksv2/advisor/AdvisorPromptIntegrationTest.java src/test/java/io/github/senseidragon/dragontweaksv2/advisor/EnvironmentToolSimulationTest.java
git commit -m "test(advisor): replace fixed-example prompt tests with a generative pass-rate harness"
```

---

## Task 6: Cleanup — delete dead routing test, annotate stale README section

**Files:**
- Delete: `src/test/java/io/github/senseidragon/dragontweaksv2/openrouter/ChatCommandHandlerTest.java`
- Modify: `README.md` (annotate the `## Session Status — 2026-06-10` heading)

**Interfaces:** none — purely additive annotation and a deletion of an already-dead test.

`ChatCommandHandler.java` (the source class) is untouched — confirmed it's still registered on the event bus in `DragonTweaksV2.java:77`, but its `parseCommand` static method (the only thing the deleted test exercised) has no caller anywhere in production code; chat routing goes entirely through `AdvisorChatHandler.onServerChat`. Per spec Non-Goals, auditing/removing the source class itself is explicitly out of scope for this plan.

- [ ] **Step 1: Delete the dead routing test**

```bash
rm src/test/java/io/github/senseidragon/dragontweaksv2/openrouter/ChatCommandHandlerTest.java
```

- [ ] **Step 2: Run the openrouter test package to confirm a clean compile**

Run: `./gradlew test --tests "io.github.senseidragon.dragontweaksv2.openrouter.*"`
Expected: PASS — no remaining references to the deleted class.

- [ ] **Step 3: Annotate the stale README section**

In `README.md`, replace:

```markdown
## Session Status — 2026-06-10
```

with:

```markdown
## Session Status — 2026-06-10

> **Superseded.** This entire session log describes an abandoned design (Indiana Jones persona, `#a`/`#f` command-prefix routing, static woodland-mansion lore with no dynamic injection). The advisor was rebuilt around dynamic `LoreIndex` lookup and a persona-driven system prompt — see `docs/superpowers/specs/2026-06-20-advisor-persona-grounding-design.md`. Kept below for historical reference.
```

- [ ] **Step 4: Append `test-audit-trail.md` entry**

Record: date, "Deleted ChatCommandHandlerTest.java (tested only dead #a/#f prefix parsing, no production caller); annotated README.md's stale 2026-06-10 Session Status section as superseded, pointing to the 2026-06-20 spec", tests covered (full openrouter package re-run, no new tests — this is a deletion + doc change), pass/fail result, and explicitly note "ChatCommandHandler.java source class intentionally untouched — out of scope per spec Non-Goals."

- [ ] **Step 5: Commit**

```bash
git add README.md test-audit-trail.md
git rm src/test/java/io/github/senseidragon/dragontweaksv2/openrouter/ChatCommandHandlerTest.java
git commit -m "chore: remove dead #a/#f routing test, annotate superseded README section"
```

---

## Self-Review

**Spec coverage:**
- Section 1 (scope) — honored: only `SYSTEM_PROMPT`/`buildSystemPrompt`, `ToolCallOrchestrator`'s round-trip contract, and the test suite change (Tasks 1, 2, 3, 4, 5). `LoreIndex`, the four tools' definitions, `AdvisorStatusMonitor`, entity lifecycle untouched.
- Section 2 (persona document) — Task 1.
- Section 3 (rule classification) — Task 2 (code-deterministic denylist) + Task 3 (drop truncation).
- Section 4 (round-1 shortcut) — Task 4.
- Section 5 (generative harness) — Task 5.
- Section 6 (cleanup) — Task 6.
- Constraints (pre-flight, Code Change Gate, append-only audit trail, no source scanning beyond what's authorized, main-thread non-blocking) — addressed in Global Constraints and respected by every task (no new blocking calls; all `.get()` calls run on the existing background executors, identical in shape to the code they replace).

**Placeholder scan:** no TBD/TODO; every step shows complete code, not a description of code. The one spec/code discrepancy found (`truncateToSentences` was already dead, not live) is called out explicitly in Task 3 rather than papered over.

**Type consistency:** `isWorldStateRelevant(String): boolean` (Task 4) is used identically in its own tests and inside `handleQuery`. `PERSONA_BIO` (Task 1) is `ToolCallOrchestrator.PERSONA_BIO` everywhere it's referenced, including from `AdvisorChatHandler`. `stripBannedPhrases(String): String` (Task 2) matches between its tests and its call site in `parseOpenRouterResponse`. `TrackingTool` (Task 5) duplicates the pattern already proven in the deleted `EnvironmentToolSimulationTest` rather than inventing a new shape.

**Scope check:** single cohesive redesign, six tasks, each independently testable and revertible. No decomposition into separate plans needed.
