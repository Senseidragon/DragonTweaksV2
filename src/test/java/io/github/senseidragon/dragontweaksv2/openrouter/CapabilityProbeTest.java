package io.github.senseidragon.dragontweaksv2.openrouter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link OpenRouterService#probeContextRetention()}.
 *
 * The probe makes two sequential HTTP calls. We stub the HttpClient so that
 * both calls return 200 OK. The second response is what we vary:
 * - If it mentions "apple" → probe must return true
 * - If it does not → probe must return false
 *
 * The first call's response is ignored by the probe (it only reads call 2),
 * so both stubs can return the same body without affecting correctness.
 */
class CapabilityProbeTest {

    @TempDir
    Path tempDir;

    /**
     * Build a service whose HttpClient always returns the given body with status 200.
     * Call 1 and call 2 both return the same stubbed body; the probe ignores call 1's body.
     */
    @SuppressWarnings("unchecked")
    private OpenRouterService buildServiceWithStubbedHttp(String responseBody) throws Exception {
        HttpResponse<String> fakeResponse = mock(HttpResponse.class);
        when(fakeResponse.statusCode()).thenReturn(200);
        when(fakeResponse.body()).thenReturn(responseBody);

        HttpClient fakeClient = mock(HttpClient.class);
        when(fakeClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(fakeResponse);

        OpenRouterService service = new OpenRouterService(tempDir, fakeClient);
        service.setModelIdsForTest("flavor-model", "advisory-model", "test-key");
        return service;
    }

    @Test
    void probeReturnsTrueWhenModelReferencesApple() throws Exception {
        String responseJson = """
            {
              "choices": [{
                "message": {
                  "role": "assistant",
                  "content": "You are holding an apple in your left hand."
                },
                "finish_reason": "stop"
              }]
            }
            """;
        OpenRouterService service = buildServiceWithStubbedHttp(responseJson);
        assertTrue(service.probeContextRetention(), "Probe should return true when response contains 'apple'");
    }

    @Test
    void probeReturnsFalseWhenModelDoesNotReferenceApple() throws Exception {
        String responseJson = """
            {
              "choices": [{
                "message": {
                  "role": "assistant",
                  "content": "I cannot determine what you are holding without more context."
                },
                "finish_reason": "stop"
              }]
            }
            """;
        OpenRouterService service = buildServiceWithStubbedHttp(responseJson);
        assertFalse(service.probeContextRetention(), "Probe should return false when response does not contain 'apple'");
    }
}
