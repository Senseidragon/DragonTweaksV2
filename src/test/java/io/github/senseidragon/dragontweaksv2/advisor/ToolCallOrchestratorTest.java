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
    void defaultIncludesHistory() {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(null, List.of(), false);
        assertTrue(orc.shouldIncludeHistory("how do I make a sword?"));
        assertTrue(orc.shouldIncludeHistory("good morning"));
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
    void systemPromptConstantMatchesOrchestratorPersonaBio() {
        assertEquals(ToolCallOrchestrator.PERSONA_BIO, AdvisorChatHandler.SYSTEM_PROMPT);
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
        when(openRouter.sendWithTools(any(), any(), any(), any()))
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
