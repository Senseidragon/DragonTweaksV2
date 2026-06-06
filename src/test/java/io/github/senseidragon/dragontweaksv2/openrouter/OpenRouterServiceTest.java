package io.github.senseidragon.dragontweaksv2.openrouter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OpenRouterServiceTest {
    @TempDir Path tempDir;
    @Mock HttpClient mockClient;
    @Mock HttpResponse<String> okResponse;
    @Mock HttpResponse<String> failResponse;

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
        when(okResponse.statusCode()).thenReturn(200);
        when(failResponse.statusCode()).thenReturn(401);
    }

    @Test
    void isDisabledByDefault() {
        OpenRouterService service = new OpenRouterService(mockClient, tempDir);
        assertFalse(service.isEnabled());
    }

    @Test
    void enablesOnFullSuccess() throws Exception {
        doReturn(okResponse).when(mockClient).send(any(HttpRequest.class), any());
        OpenRouterService service = new OpenRouterService(mockClient, tempDir);
        List<String> failures = new ArrayList<>();
        service.initAsync(failures::add).get(5, TimeUnit.SECONDS);
        assertTrue(service.isEnabled());
        assertTrue(failures.isEmpty());
        assertEquals("flavor-model", service.getFlavorModelId());
        assertEquals("advisory-model", service.getAdvisoryModelId());
    }

    @Test
    void disablesWhenEnvFileMissing() throws Exception {
        Files.delete(tempDir.resolve(".env"));
        OpenRouterService service = new OpenRouterService(mockClient, tempDir);
        List<String> failures = new ArrayList<>();
        service.initAsync(failures::add).get(5, TimeUnit.SECONDS);
        assertFalse(service.isEnabled());
        assertTrue(failures.get(0).contains(".env file not found"));
    }

    @Test
    void disablesWhenApiKeyMissing() throws Exception {
        Files.writeString(tempDir.resolve(".env"), "OTHER=value\n");
        OpenRouterService service = new OpenRouterService(mockClient, tempDir);
        List<String> failures = new ArrayList<>();
        service.initAsync(failures::add).get(5, TimeUnit.SECONDS);
        assertFalse(service.isEnabled());
        assertTrue(failures.get(0).contains("OPENROUTER_API_KEY not set"));
    }

    @Test
    void disablesWhenModelConfigMissing() throws Exception {
        Files.delete(tempDir.resolve("model_config.json"));
        OpenRouterService service = new OpenRouterService(mockClient, tempDir);
        List<String> failures = new ArrayList<>();
        service.initAsync(failures::add).get(5, TimeUnit.SECONDS);
        assertFalse(service.isEnabled());
        assertTrue(failures.get(0).contains("model_config.json unreadable"));
    }

    @Test
    void disablesWhenKeyValidationFails() throws Exception {
        doReturn(failResponse).when(mockClient).send(any(HttpRequest.class), any());
        OpenRouterService service = new OpenRouterService(mockClient, tempDir);
        List<String> failures = new ArrayList<>();
        service.initAsync(failures::add).get(5, TimeUnit.SECONDS);
        assertFalse(service.isEnabled());
        assertTrue(failures.get(0).contains("API key rejected"));
    }

    @Test
    void disablesWhenFlavorModelPrimeFails() throws Exception {
        doReturn(okResponse).doReturn(failResponse)
            .when(mockClient).send(any(HttpRequest.class), any());
        OpenRouterService service = new OpenRouterService(mockClient, tempDir);
        List<String> failures = new ArrayList<>();
        service.initAsync(failures::add).get(5, TimeUnit.SECONDS);
        assertFalse(service.isEnabled());
        assertTrue(failures.get(0).contains("did not respond"));
    }

    @Test
    void disablesWhenAdvisoryModelPrimeFails() throws Exception {
        doReturn(okResponse).doReturn(okResponse).doReturn(failResponse)
            .when(mockClient).send(any(HttpRequest.class), any());
        OpenRouterService service = new OpenRouterService(mockClient, tempDir);
        List<String> failures = new ArrayList<>();
        service.initAsync(failures::add).get(5, TimeUnit.SECONDS);
        assertFalse(service.isEnabled());
        assertTrue(failures.get(0).contains("did not respond"));
    }

    @Test
    void queryReturnsNonBlankResponse() throws Exception {
        String fakeReply = "{\"choices\":[{\"message\":{\"content\":\"I would avoid it.\"}}]}";
        when(okResponse.body()).thenReturn(fakeReply);
        doReturn(okResponse).when(mockClient).send(any(HttpRequest.class), any());

        OpenRouterService service = new OpenRouterService(mockClient, tempDir);
        service.initAsync(failures -> {}).get(5, TimeUnit.SECONDS);

        String result = service.query("advisory", "how should I respond if I encounter a lone pillager?");
        assertFalse(result.isBlank());
        assertEquals("I would avoid it.", result);
    }

    @Test
    void queryThrowsWhenNotEnabled() {
        OpenRouterService service = new OpenRouterService(mockClient, tempDir);
        assertThrows(IllegalStateException.class, () ->
            service.query("advisory", "hello"));
    }

    @Test
    void apiKeyNeverAppearsInFailureReason() throws Exception {
        doReturn(failResponse).when(mockClient).send(any(HttpRequest.class), any());
        OpenRouterService service = new OpenRouterService(mockClient, tempDir);
        List<String> failures = new ArrayList<>();
        service.initAsync(failures::add).get(5, TimeUnit.SECONDS);
        failures.forEach(reason ->
            assertFalse(reason.contains("test-key"), "API key must not appear in failure reason"));
    }
}
