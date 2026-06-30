package io.github.senseidragon.dragontweaksv2.advisor;

import com.google.gson.JsonObject;
import io.github.senseidragon.dragontweaksv2.advisor.model.OpenRouterResponse;
import io.github.senseidragon.dragontweaksv2.advisor.model.ToolCall;
import io.github.senseidragon.dragontweaksv2.advisor.model.ToolResult;
import io.github.senseidragon.dragontweaksv2.openrouter.OpenRouterService;
import net.minecraft.server.level.ServerPlayer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToolCallOrchestratorTest {

    @Mock OpenRouterService openRouter;

    // ── history decision tests — no player needed ─────────────────────────────

    @Test
    void followUpSignalIncludesHistory() {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(null, List.of(), false);
        assertTrue(orc.shouldIncludeHistory("you said I should avoid spiders earlier"));
        assertTrue(orc.shouldIncludeHistory("tell me more about that"));
        assertTrue(orc.shouldIncludeHistory("what about the cave?"));
    }

    @Test
    void pureStateSuppressesHistory() {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(null, List.of(), false);
        assertFalse(orc.shouldIncludeHistory("what do i have"));
        assertFalse(orc.shouldIncludeHistory("scan the area"));
        assertFalse(orc.shouldIncludeHistory("what's around me"));
    }

    @Test
    void defaultExcludesHistory() {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(null, List.of(), false);
        assertFalse(orc.shouldIncludeHistory("how do I make a sword?"));
        assertFalse(orc.shouldIncludeHistory("good morning"));
    }

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
    void personaBioInstructsAgainstListFormatting() {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(null, List.of(), false);
        String prompt = orc.buildSystemPrompt("");
        assertTrue(prompt.contains("never bullet points, dashes, or headers"),
            "Expected persona bio to reject list-style formatting");
    }

    @Test
    void systemPromptConstantMatchesOrchestratorPersonaBio() {
        assertEquals(ToolCallOrchestrator.PERSONA_BIO, AdvisorChatHandler.SYSTEM_PROMPT);
    }

    // ── classification table tests — no player needed ─────────────────────────

    @Test
    void classifiesEnvironmentSignalToSingleTool() {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(null, List.of(), false);
        var category = orc.classify("what's the weather like").orElseThrow();
        assertEquals("environment", category.name());
        assertEquals(List.of("get_environment"), category.tools());
    }

    @Test
    void classifiesLocationSignalToBothTools() {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(null, List.of(), false);
        var category = orc.classify("where am i").orElseThrow();
        assertEquals("location", category.name());
        assertEquals(List.of("get_environment", "scan_area"), category.tools());
    }

    @Test
    void classifiesScanSignalAheadOfLocationWhenBothPresent() {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(null, List.of(), false);
        // "creature" (scan) and "nearby" (location) both appear — scan wins, earlier in table order.
        assertEquals("scan", orc.classify("is there a creature nearby").orElseThrow().name());
    }

    @Test
    void classifiesVillageSignalToLocatorTool() {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(null, List.of(), false);
        var category = orc.classify("which way to the nearest village").orElseThrow();
        assertEquals("village", category.name());
        assertEquals(List.of("find_nearest_village"), category.tools());
    }

    @Test
    void classifiesVillageSignalAheadOfLocationWhenBothPresent() {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(null, List.of(), false);
        // "where" (location) and "village" both appear — only find_nearest_village can actually
        // answer this, so "village" is placed earlier than "location" in the table to win here.
        assertEquals("village", orc.classify("where is the nearest village").orElseThrow().name());
    }

    @Test
    void classifiesChitchatToNoTool() {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(null, List.of(), false);
        var category = orc.classify("hello there").orElseThrow();
        assertEquals("chitchat", category.name());
        assertTrue(category.tools().isEmpty());
    }

    @Test
    void unmatchedQueryHasNoCategory() {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(null, List.of(), false);
        assertTrue(orc.classify("how do I make a sword?").isEmpty());
    }

    // ── deterministic injection — handleQuery path tests ───────────────────────

    @Test
    void locationQueryWithNoToolCallForcesDeterministicInjection() throws Exception {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(openRouter, List.of(), false);
        when(openRouter.sendWithTools(any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(new OpenRouterResponse("It's a cavern.", List.of())));

        List<List<ToolCall>> capturedCalls = new ArrayList<>();
        doAnswer(inv -> {
            capturedCalls.add(inv.getArgument(3));
            return CompletableFuture.completedFuture("You're on the surface in a forest.");
        }).when(openRouter).sendWithToolResults(any(), any(), any(), any(), any(), any(), eq(ToolCallOrchestrator.COMPLEX_MAX_TOKENS));

        AdvisorSession session = new AdvisorSession(20);
        List<String> delivered = new ArrayList<>();
        orc.handleQuery("where am i", null, session, delivered::add, Runnable::run, () -> true)
            .get(5, TimeUnit.SECONDS);

        verify(openRouter, times(1)).sendWithTools(any(), any(), any(), any());
        // Two forced tools (get_environment + scan_area) synthesized in one turn — uses the raised, complex budget.
        verify(openRouter, times(1)).sendWithToolResults(any(), any(), any(), any(), any(), any(), eq(ToolCallOrchestrator.COMPLEX_MAX_TOKENS));
        assertEquals(List.of("You're on the surface in a forest."), delivered);
        assertEquals(1, capturedCalls.size());
        assertEquals(List.of("get_environment", "scan_area"),
            capturedCalls.get(0).stream().map(ToolCall::name).toList());
    }

    @Test
    void ambiguousQueryWithNoToolCallRetriesOnceThenExecutesOfferedTool() throws Exception {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(openRouter, List.of(), false);
        ToolCall call = new ToolCall("id1", "get_inventory", new JsonObject());
        // No category matched ("how do I make a sword?") — both attempts go through the
        // complex-budget overload since the model must reason freely with no grounding tool.
        when(openRouter.sendWithTools(any(), any(), any(), any(), eq(ToolCallOrchestrator.COMPLEX_MAX_TOKENS)))
            .thenReturn(CompletableFuture.completedFuture(new OpenRouterResponse("I'd use an anvil.", List.of())))
            .thenReturn(CompletableFuture.completedFuture(new OpenRouterResponse(null, List.of(call))));
        when(openRouter.sendWithToolResults(any(), any(), any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture("Check your inventory for an anvil."));

        AdvisorSession session = new AdvisorSession(20);
        List<String> delivered = new ArrayList<>();
        orc.handleQuery("how do I make a sword?", null, session, delivered::add, Runnable::run, () -> true)
            .get(5, TimeUnit.SECONDS);

        verify(openRouter, times(2)).sendWithTools(any(), any(), any(), any(), eq(ToolCallOrchestrator.COMPLEX_MAX_TOKENS));
        assertEquals(List.of("Check your inventory for an anvil."), delivered);
    }

    @Test
    void ambiguousQueryWithNoToolCallOnEitherAttemptDeliversSecondAttemptText() throws Exception {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(openRouter, List.of(), false);
        when(openRouter.sendWithTools(any(), any(), any(), any(), eq(ToolCallOrchestrator.COMPLEX_MAX_TOKENS)))
            .thenReturn(CompletableFuture.completedFuture(new OpenRouterResponse("I'd use an anvil.", List.of())))
            .thenReturn(CompletableFuture.completedFuture(new OpenRouterResponse("I have no way to check that.", List.of())));

        AdvisorSession session = new AdvisorSession(20);
        List<String> delivered = new ArrayList<>();
        orc.handleQuery("how do I make a sword?", null, session, delivered::add, Runnable::run, () -> true)
            .get(5, TimeUnit.SECONDS);

        verify(openRouter, times(2)).sendWithTools(any(), any(), any(), any(), eq(ToolCallOrchestrator.COMPLEX_MAX_TOKENS));
        assertEquals(List.of("I have no way to check that."), delivered);
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

    // ── handleQuery path tests — use injected executor + isOnline ────────────
    // Pass null for ServerPlayer; fake tools never dereference it.

    @Test
    void textOnlyResponsePathDeliveredToPlayer() throws Exception {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(openRouter, List.of(), false);
        when(openRouter.sendWithTools(any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(new OpenRouterResponse("Hello!", List.of())));

        AdvisorSession session = new AdvisorSession(20);
        List<String> delivered = new ArrayList<>();
        orc.handleQuery("hi", null, session, delivered::add, Runnable::run, () -> true)
            .get(5, TimeUnit.SECONDS);

        assertEquals(List.of("Hello!"), delivered);
        assertEquals("user",    session.getMessages().get(0).role());
        assertEquals("advisor", session.getMessages().get(1).role());
    }

    @Test
    void toolCallPathExecutesToolsAndDeliversFinalResponse() throws Exception {
        AdvisorTool fakeTool = new AdvisorTool() {
            public String name() { return "get_inventory"; }
            public JsonObject definition() { return new JsonObject(); }
            public String execute(JsonObject args, ServerPlayer p) { return "Bread x5"; }
        };

        ToolCallOrchestrator orc = new ToolCallOrchestrator(openRouter, List.of(fakeTool), false);

        ToolCall call = new ToolCall("id1", "get_inventory", new JsonObject());
        when(openRouter.sendWithTools(any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(new OpenRouterResponse(null, List.of(call))));
        when(openRouter.sendWithToolResults(any(), any(), any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture("You have Bread x5."));

        AdvisorSession session = new AdvisorSession(20);
        List<String> delivered = new ArrayList<>();
        // Runnable::run executes tool callbacks synchronously; () -> true = always online
        orc.handleQuery("what do I have?", null, session, delivered::add, Runnable::run, () -> true)
            .get(5, TimeUnit.SECONDS);

        assertEquals(List.of("You have Bread x5."), delivered);
    }

    @Test
    void unrecognizedToolReturnsErrorString() throws Exception {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(openRouter, List.of(), false);

        ToolCall call = new ToolCall("id1", "unknown_tool", new JsonObject());
        // "do something" matches no category, so round 1 goes through the complex-budget overload.
        when(openRouter.sendWithTools(any(), any(), any(), any(), eq(ToolCallOrchestrator.COMPLEX_MAX_TOKENS)))
            .thenReturn(CompletableFuture.completedFuture(new OpenRouterResponse(null, List.of(call))));

        List<ToolResult> capturedResults = new ArrayList<>();
        doAnswer(inv -> {
            capturedResults.addAll(inv.getArgument(4));
            return CompletableFuture.completedFuture("Sorry.");
        }).when(openRouter).sendWithToolResults(any(), any(), any(), any(), any(), any());

        AdvisorSession session = new AdvisorSession(20);
        List<String> delivered = new ArrayList<>();
        orc.handleQuery("do something", null, session, delivered::add, Runnable::run, () -> true)
            .get(5, TimeUnit.SECONDS);

        assertTrue(capturedResults.stream()
            .anyMatch(r -> r.content().startsWith("[Unknown tool:")),
            "Expected unknown tool error in results");
    }

    @Test
    void disconnectBetweenRoundTripsDiscardsResponse() throws Exception {
        AdvisorTool fakeTool = new AdvisorTool() {
            public String name() { return "get_inventory"; }
            public JsonObject definition() { return new JsonObject(); }
            public String execute(JsonObject args, ServerPlayer p) { return "Bread x5"; }
        };

        ToolCallOrchestrator orc = new ToolCallOrchestrator(openRouter, List.of(fakeTool), false);

        ToolCall call = new ToolCall("id1", "get_inventory", new JsonObject());
        when(openRouter.sendWithTools(any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(new OpenRouterResponse(null, List.of(call))));

        AdvisorSession session = new AdvisorSession(20);
        List<String> delivered = new ArrayList<>();
        // () -> false = offline between round trips
        orc.handleQuery("what do I have?", null, session, delivered::add, Runnable::run, () -> false)
            .get(5, TimeUnit.SECONDS);

        assertTrue(delivered.isEmpty(), "Should not deliver to offline player");
        verify(openRouter, never()).sendWithToolResults(any(), any(), any(), any(), any(), any());
    }
}
