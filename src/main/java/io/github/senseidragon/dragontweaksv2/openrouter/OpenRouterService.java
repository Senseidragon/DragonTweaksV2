package io.github.senseidragon.dragontweaksv2.openrouter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class OpenRouterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenRouterService.class);
    private static final String BASE_URL = "https://openrouter.ai/api/v1";
    private static final Gson GSON = new Gson();

    private static volatile OpenRouterService instance;

    private final HttpClient httpClient;
    private final Path workingDir;
    private final ExecutorService executor;

    private volatile boolean enabled = false;
    private volatile String failureReason = null;
    private String apiKey;
    private String flavorModelId;
    private String advisoryModelId;

    OpenRouterService(HttpClient httpClient, Path workingDir) {
        this.httpClient = httpClient;
        this.workingDir = workingDir;
        this.executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "openrouter-init");
            t.setDaemon(true);
            return t;
        });
    }

    public static OpenRouterService getInstance() {
        if (instance == null) {
            instance = new OpenRouterService(
                HttpClient.newHttpClient(),
                Path.of(System.getProperty("user.dir"))
            );
        }
        return instance;
    }

    public CompletableFuture<Void> initAsync(Consumer<String> onFailure) {
        LOGGER.debug("OpenRouter init started");
        return CompletableFuture.runAsync(() -> {
            try {
                init();
            } catch (Exception ex) {
                String reason = ex.getMessage() != null ? ex.getMessage() : "unexpected error";
                LOGGER.debug("OpenRouter init failed: {}", reason);
                failureReason = reason;
                onFailure.accept(reason);
            }
        }, executor);
    }

    private void init() throws Exception {
        Map<String, String> env;
        try {
            env = EnvLoader.load(workingDir.resolve(".env"));
        } catch (NoSuchFileException e) {
            throw new IllegalStateException(".env file not found.");
        }
        LOGGER.debug("API key present: {}", env.containsKey("OPENROUTER_API_KEY"));
        apiKey = env.get("OPENROUTER_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENROUTER_API_KEY not set.");
        }

        String configJson;
        try {
            configJson = Files.readString(workingDir.resolve("model_config.json"));
        } catch (NoSuchFileException e) {
            throw new IllegalStateException("model_config.json unreadable.");
        }
        JsonObject config = GSON.fromJson(configJson, JsonObject.class);
        flavorModelId = ModelSelector.selectCheapest(config, "flavor");
        advisoryModelId = ModelSelector.selectCheapest(config, "advisory");
        LOGGER.debug("Selected models — flavor: {}, advisory: {}", flavorModelId, advisoryModelId);

        HttpRequest keyRequest = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/auth/key"))
            .header("Authorization", "Bearer " + apiKey)
            .GET()
            .build();
        HttpResponse<String> keyResp = httpClient.send(keyRequest, HttpResponse.BodyHandlers.ofString());
        if (keyResp.statusCode() < 200 || keyResp.statusCode() >= 300) {
            throw new IllegalStateException("API key rejected by OpenRouter.");
        }
        LOGGER.debug("API key validated successfully");

        primeModel(flavorModelId);
        primeModel(advisoryModelId);

        enabled = true;
        LOGGER.debug("OpenRouter ready. flavor={}, advisory={}", flavorModelId, advisoryModelId);
    }

    private void primeModel(String modelId) throws Exception {
        String body = String.format(
            "{\"model\":\"%s\",\"messages\":[{\"role\":\"user\",\"content\":\"ping\"}]}",
            modelId
        );
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/chat/completions"))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new IllegalStateException("model '" + modelId + "' did not respond.");
        }
        LOGGER.debug("Model primed: {}", modelId);
    }

    public boolean isEnabled() { return enabled; }
    public String getFailureReason() { return failureReason; }
    public String getFlavorModelId() { return flavorModelId; }
    public String getAdvisoryModelId() { return advisoryModelId; }

    public void shutdown() {
        executor.shutdown();
        instance = null;
    }
}
