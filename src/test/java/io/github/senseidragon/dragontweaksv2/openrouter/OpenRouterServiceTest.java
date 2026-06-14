package io.github.senseidragon.dragontweaksv2.openrouter;

import io.github.senseidragon.dragontweaksv2.advisor.ChatMessage;
import io.github.senseidragon.dragontweaksv2.advisor.model.OpenRouterResponse;
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
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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

    // truncateToSentences

    @Test
    void truncate_threeOrFewerSentences_returnedUnchanged() {
        String s = "One. Two. Three.";
        assertEquals(s, OpenRouterService.truncateToSentences(s, 3));
    }

    @Test
    void truncate_moreThanThreeSentences_cutsAfterThird() {
        String input = "One. Two. Three. Four. Five.";
        assertEquals("One. Two. Three.", OpenRouterService.truncateToSentences(input, 3));
    }

    @Test
    void truncate_longModelResponse_cutsAfterThird() {
        String input = "The sun has slipped below the rim of the world. Shadows stretch long and black. " +
                       "The air has cooled just enough. It's the crack between day and night. Late twilight.";
        String result = OpenRouterService.truncateToSentences(input, 3);
        assertEquals("The sun has slipped below the rim of the world. Shadows stretch long and black. " +
                     "The air has cooled just enough.", result);
    }

    @Test
    void truncate_exclamationAndQuestion_countAsSentences() {
        String input = "Watch out! Why is it so dark? Something moves. And another thing.";
        assertEquals("Watch out! Why is it so dark? Something moves.", OpenRouterService.truncateToSentences(input, 3));
    }

    @Test
    void truncate_fewerThanMaxSentences_returnedAsIs() {
        String input = "One sentence only.";
        assertEquals(input, OpenRouterService.truncateToSentences(input, 3));
    }

    @Test
    void truncate_emptyString_returnsEmpty() {
        assertEquals("", OpenRouterService.truncateToSentences("", 3));
    }

    @Test
    void truncate_shortFragments_countAsSentences() {
        String input = "Night. Cold. Dark. Something else entirely said here.";
        assertEquals("Night. Cold. Dark.", OpenRouterService.truncateToSentences(input, 3));
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
}
