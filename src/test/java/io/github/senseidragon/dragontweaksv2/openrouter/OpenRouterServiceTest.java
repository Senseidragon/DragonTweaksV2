package io.github.senseidragon.dragontweaksv2.openrouter;

import com.google.gson.JsonObject;
import io.github.senseidragon.dragontweaksv2.advisor.ChatMessage;
import io.github.senseidragon.dragontweaksv2.advisor.model.OpenRouterResponse;
import io.github.senseidragon.dragontweaksv2.advisor.model.ToolCall;
import io.github.senseidragon.dragontweaksv2.advisor.model.ToolResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OpenRouterServiceTest {
    @TempDir Path tempDir;

    private static final String MINIMAL_CONFIG = """
        {
          "roles": {
            "flavor": {
              "candidates": [{"model_id": "flavor-model", "role_weighted_cost_per_1m": 0.05}]
            },
            "advisory": {
              "candidates": [{"model_id": "advisory-model", "role_weighted_cost_per_1m": 0.07}]
            }
          }
        }
        """;

    @BeforeEach
    void setUp() throws Exception {
        Files.writeString(tempDir.resolve(".env"), "OPENROUTER_API_KEY=test-key\n");
        Files.writeString(tempDir.resolve("model_config.json"), MINIMAL_CONFIG);
    }

    @Test
    void isDisabledByDefault() {
        OpenRouterService service = new OpenRouterService(tempDir);
        assertFalse(service.isEnabled());
    }

    @Test
    void disablesWhenEnvFileMissing() throws Exception {
        Files.delete(tempDir.resolve(".env"));
        OpenRouterService service = new OpenRouterService(tempDir);
        List<String> failures = new ArrayList<>();
        service.initAsync(failures::add).get(5, TimeUnit.SECONDS);
        assertFalse(service.isEnabled());
        assertTrue(failures.get(0).contains(".env file not found"));
    }

    @Test
    void disablesWhenApiKeyMissing() throws Exception {
        Files.writeString(tempDir.resolve(".env"), "OTHER=value\n");
        OpenRouterService service = new OpenRouterService(tempDir);
        List<String> failures = new ArrayList<>();
        service.initAsync(failures::add).get(5, TimeUnit.SECONDS);
        assertFalse(service.isEnabled());
        assertTrue(failures.get(0).contains("OPENROUTER_API_KEY not set"));
    }

    @Test
    void disablesWhenModelConfigMissing() throws Exception {
        Files.delete(tempDir.resolve("model_config.json"));
        OpenRouterService service = new OpenRouterService(tempDir);
        List<String> failures = new ArrayList<>();
        service.initAsync(failures::add).get(5, TimeUnit.SECONDS);
        assertFalse(service.isEnabled());
        assertTrue(failures.get(0).contains("model_config.json unreadable"));
    }

    @Test
    void queryReturnsFailedFutureWhenNotEnabled() {
        OpenRouterService service = new OpenRouterService(tempDir);
        var future = service.query("advisory", "hello");
        assertTrue(future.isCompletedExceptionally());
    }

    @Test
    void queryAsyncFailsWhenNotEnabled() {
        OpenRouterService service = new OpenRouterService(tempDir);
        var future = service.queryAsync("prompt", List.of());
        assertTrue(future.isCompletedExceptionally());
    }

    @Test
    void queryAsyncFailsWhenNotEnabledWithHistory() {
        OpenRouterService service = new OpenRouterService(tempDir);
        var history = List.of(new ChatMessage("user", "hello"), new ChatMessage("advisor", "hi"));
        assertTrue(service.queryAsync("prompt", history).isCompletedExceptionally());
    }

    @Test
    void disableSetIsEnabledFalse() {
        OpenRouterService service = new OpenRouterService(tempDir);
        service.disable();
        assertFalse(service.isEnabled());
    }

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

    // -----------------------------------------------------------------------
    // Advisor token budget — default cap and per-call override
    // -----------------------------------------------------------------------

    @Test
    void buildRequestBodyDefaultsToRaisedAdvisorBudget() {
        OpenRouterService service = new OpenRouterService(tempDir);
        service.setModelIdsForTest("flavor-model", "advisory-model", "test-key");

        JsonObject body = service.buildRequestBody("system", List.of(), "hello");

        assertEquals(1000, body.get("max_tokens").getAsInt(),
            "Reasoning model's hidden chain-of-thought shares this budget with the visible answer; " +
                "175 was proven too low to reliably leave room for both.");
    }

    @Test
    void buildRequestBodyHonorsExplicitMaxTokensOverride() {
        OpenRouterService service = new OpenRouterService(tempDir);
        service.setModelIdsForTest("flavor-model", "advisory-model", "test-key");

        JsonObject body = service.buildRequestBody("system", List.of(), "hello", 1500);

        assertEquals(1500, body.get("max_tokens").getAsInt());
    }

    @Test
    void sendWithToolsExplicitMaxTokensOverloadStillParsesResponse() throws Exception {
        OpenRouterService service = buildServiceWithStubbedHttp(textResponseJson("Here is my answer."));

        OpenRouterResponse result = service.sendWithTools("system", List.of(), "hello", List.of(), 1500)
            .get(5, TimeUnit.SECONDS);

        assertEquals("Here is my answer.", result.textContent());
    }

    @Test
    void sendWithToolResultsExplicitMaxTokensOverloadStillParsesResponse() throws Exception {
        OpenRouterService service = buildServiceWithStubbedHttp(textResponseJson("Here is my answer."));
        ToolCall call = new ToolCall("id1", "get_inventory", new JsonObject());
        ToolResult toolResult = new ToolResult("id1", "Bread x5");

        String result = service.sendWithToolResults("system", List.of(), "hello",
                List.of(call), List.of(toolResult), List.of(), 1500)
            .get(5, TimeUnit.SECONDS);

        assertEquals("Here is my answer.", result);
    }

    // -----------------------------------------------------------------------
    // sendWithTools — HTTP stubbing tests
    // -----------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private OpenRouterService buildServiceWithStubbedHttp(String stubbedResponseBody) throws Exception {
        HttpResponse<String> fakeResponse = mock(HttpResponse.class);
        when(fakeResponse.statusCode()).thenReturn(200);
        when(fakeResponse.body()).thenReturn(stubbedResponseBody);

        HttpClient fakeClient = mock(HttpClient.class);
        when(fakeClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(fakeResponse);
        when(fakeClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(CompletableFuture.completedFuture(fakeResponse));

        OpenRouterService service = new OpenRouterService(tempDir, fakeClient);
        service.setModelIdsForTest("flavor-model", "advisory-model", "test-key");
        return service;
    }

    @Test
    void sendWithToolsParsesPureTextResponse() throws Exception {
        String responseJson = """
            {
              "choices": [{
                "message": {
                  "role": "assistant",
                  "content": "Here is my answer."
                },
                "finish_reason": "stop"
              }]
            }
            """;
        OpenRouterService service = buildServiceWithStubbedHttp(responseJson);

        OpenRouterResponse result = service.sendWithTools("system", List.of(), "hello", List.of())
            .get(5, TimeUnit.SECONDS);

        assertFalse(result.hasToolCalls(), "Expected no tool calls in a pure-text response");
        assertEquals("Here is my answer.", result.textContent());
    }

    @Test
    void sendWithToolsParsesToolCallResponse() throws Exception {
        String responseJson = """
            {
              "choices": [{
                "message": {
                  "role": "assistant",
                  "content": null,
                  "tool_calls": [{
                    "id": "call_abc123",
                    "type": "function",
                    "function": {
                      "name": "get_inventory",
                      "arguments": "{\\"slot\\": 0}"
                    }
                  }]
                },
                "finish_reason": "tool_calls"
              }]
            }
            """;
        OpenRouterService service = buildServiceWithStubbedHttp(responseJson);

        OpenRouterResponse result = service.sendWithTools("system", List.of(), "hello", List.of())
            .get(5, TimeUnit.SECONDS);

        assertTrue(result.hasToolCalls(), "Expected tool calls in the response");
        assertEquals("get_inventory", result.toolCalls().get(0).name());
    }

    // -----------------------------------------------------------------------
    // Denylist repair loop — findBannedPhraseHits / repairBannedPhrasesIfNeeded
    // -----------------------------------------------------------------------

    private static String textResponseJson(String content) {
        return """
            {
              "choices": [{
                "message": { "role": "assistant", "content": "%s" },
                "finish_reason": "stop"
              }]
            }
            """.formatted(content.replace("\"", "\\\""));
    }

    @SuppressWarnings("unchecked")
    private OpenRouterService buildServiceWithStubbedHttpSequence(Object... responses) throws Exception {
        HttpClient fakeClient = mock(HttpClient.class);
        List<CompletableFuture<HttpResponse<String>>> futures = new ArrayList<>();
        for (Object response : responses) {
            if (response instanceof Exception ex) {
                CompletableFuture<HttpResponse<String>> failed = new CompletableFuture<>();
                failed.completeExceptionally(ex);
                futures.add(failed);
            } else {
                HttpResponse<String> fakeResponse = mock(HttpResponse.class);
                when(fakeResponse.statusCode()).thenReturn(200);
                when(fakeResponse.body()).thenReturn((String) response);
                futures.add(CompletableFuture.completedFuture(fakeResponse));
            }
        }
        CompletableFuture<HttpResponse<String>>[] rest =
            futures.subList(1, futures.size()).toArray(new CompletableFuture[0]);
        when(fakeClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(futures.get(0), rest);

        OpenRouterService service = new OpenRouterService(tempDir, fakeClient);
        service.setModelIdsForTest("flavor-model", "advisory-model", "test-key");
        return service;
    }

    @Test
    void findBannedPhraseHitsDetectsBannedWords() {
        assertEquals(List.of("scan"), OpenRouterService.findBannedPhraseHits("I did a scan of the area."));
        assertEquals(List.of("data", "results"),
            OpenRouterService.findBannedPhraseHits("Here is the data and results."));
    }

    @Test
    void findBannedPhraseHitsReturnsEmptyForCleanText() {
        assertTrue(OpenRouterService.findBannedPhraseHits("Stay alert out there.").isEmpty());
    }

    @Test
    void repairBannedPhrasesIfNeededLeavesCleanTextUnchangedWithNoHttpCalls() throws Exception {
        HttpClient fakeClient = mock(HttpClient.class);
        OpenRouterService service = new OpenRouterService(tempDir, fakeClient);
        service.setModelIdsForTest("flavor-model", "advisory-model", "test-key");

        String result = service.repairBannedPhrasesIfNeeded("Stay alert out there.").get(5, TimeUnit.SECONDS);

        assertEquals("Stay alert out there.", result);
        verify(fakeClient, never()).sendAsync(any(), any());
    }

    @Test
    void repairBannedPhrasesIfNeededRepairsOffendingSentenceOnly() throws Exception {
        OpenRouterService service = buildServiceWithStubbedHttpSequence(
            textResponseJson("I looked over the area."));

        String result = service.repairBannedPhrasesIfNeeded("I did a scan of the area. Nothing to report.")
            .get(5, TimeUnit.SECONDS);

        assertEquals("I looked over the area. Nothing to report.", result);
    }

    @Test
    void repairBannedPhrasesIfNeededFallsBackToMechanicalStripWhenRephraseStillDirty() throws Exception {
        String dirtySentence = "I did a scan of the area.";
        OpenRouterService service = buildServiceWithStubbedHttpSequence(
            textResponseJson("I performed another scan."));

        String result = service.repairBannedPhrasesIfNeeded(dirtySentence + " Nothing to report.")
            .get(5, TimeUnit.SECONDS);

        String expectedFallback = OpenRouterService.stripBannedPhrases(dirtySentence);
        assertEquals(expectedFallback + " Nothing to report.", result);
        assertTrue(OpenRouterService.findBannedPhraseHits(result).isEmpty());
    }

    @Test
    void repairBannedPhrasesIfNeededFallsBackToMechanicalStripOnRepairError() throws Exception {
        String dirtySentence = "I did a scan of the area.";
        OpenRouterService service = buildServiceWithStubbedHttpSequence(
            new RuntimeException("network down"));

        String result = service.repairBannedPhrasesIfNeeded(dirtySentence + " Nothing to report.")
            .get(5, TimeUnit.SECONDS);

        String expectedFallback = OpenRouterService.stripBannedPhrases(dirtySentence);
        assertEquals(expectedFallback + " Nothing to report.", result);
    }

    @Test
    void repairBannedPhrasesIfNeededRepairsMultipleOffendingSentencesIndependently() throws Exception {
        OpenRouterService service = buildServiceWithStubbedHttpSequence(
            textResponseJson("I looked over the area."),
            textResponseJson("Here is what I found."));

        String result = service.repairBannedPhrasesIfNeeded(
                "I did a scan of the area. Here is the data.")
            .get(5, TimeUnit.SECONDS);

        assertEquals("I looked over the area. Here is what I found.", result);
    }

    // -----------------------------------------------------------------------
    // Denylist repair loop — full pipeline (sendWithTools / sendWithToolResults)
    // -----------------------------------------------------------------------

    @Test
    void sendWithToolsAppliesRepairToOffendingSentenceOnly() throws Exception {
        OpenRouterService service = buildServiceWithStubbedHttpSequence(
            textResponseJson("I did a scan of the area. Nothing to report."),
            textResponseJson("I looked over the area."));

        OpenRouterResponse result = service.sendWithTools("system", List.of(), "hello", List.of())
            .get(5, TimeUnit.SECONDS);

        assertFalse(result.hasToolCalls());
        assertEquals("I looked over the area. Nothing to report.", result.textContent());
    }

    @Test
    void sendWithToolsSkipsRepairWhenToolCallsPresent() throws Exception {
        String toolCallResponse = """
            {
              "choices": [{
                "message": {
                  "role": "assistant",
                  "content": null,
                  "tool_calls": [{
                    "id": "call_abc123",
                    "type": "function",
                    "function": { "name": "get_inventory", "arguments": "{}" }
                  }]
                },
                "finish_reason": "tool_calls"
              }]
            }
            """;
        HttpResponse<String> fakeResponse = mock(HttpResponse.class);
        when(fakeResponse.statusCode()).thenReturn(200);
        when(fakeResponse.body()).thenReturn(toolCallResponse);

        HttpClient fakeClient = mock(HttpClient.class);
        when(fakeClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(CompletableFuture.completedFuture(fakeResponse));

        OpenRouterService service = new OpenRouterService(tempDir, fakeClient);
        service.setModelIdsForTest("flavor-model", "advisory-model", "test-key");

        OpenRouterResponse result = service.sendWithTools("system", List.of(), "hello", List.of())
            .get(5, TimeUnit.SECONDS);

        assertTrue(result.hasToolCalls());
        verify(fakeClient, times(1)).sendAsync(any(), any());
    }

    @Test
    void sendWithToolResultsAppliesRepairToFinalText() throws Exception {
        OpenRouterService service = buildServiceWithStubbedHttpSequence(
            textResponseJson("I did a scan of the area. Nothing to report."),
            textResponseJson("I looked over the area."));

        ToolCall call = new ToolCall("id1", "get_inventory", new JsonObject());
        ToolResult toolResult = new ToolResult("id1", "Bread x5");

        String result = service.sendWithToolResults("system", List.of(), "hello",
                List.of(call), List.of(toolResult), List.of())
            .get(5, TimeUnit.SECONDS);

        assertEquals("I looked over the area. Nothing to report.", result);
    }
}
