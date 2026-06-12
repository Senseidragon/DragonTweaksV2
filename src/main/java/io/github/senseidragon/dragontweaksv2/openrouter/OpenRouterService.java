package io.github.senseidragon.dragontweaksv2.openrouter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.senseidragon.dragontweaksv2.advisor.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class OpenRouterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenRouterService.class);
    private static final String BASE_URL = "https://openrouter.ai/api/v1";
    private static final Gson GSON = new Gson();

    private static volatile OpenRouterService instance;

    private final Path workingDir;
    private final ExecutorService executor;
    private final HttpClient httpClient;

    private final AtomicBoolean initStarted = new AtomicBoolean(false);
    private volatile boolean enabled = false;
    private volatile String failureReason = null;
    private volatile String apiKey;
    private volatile String flavorModelId;
    private volatile String advisoryModelId;

    OpenRouterService(Path workingDir) {
        this.workingDir = workingDir;
        this.executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "openrouter-worker");
            t.setDaemon(true);
            return t;
        });
        this.httpClient = HttpClient.newBuilder().executor(executor).build();
    }

    public static OpenRouterService getInstance() {
        if (instance == null) {
            instance = new OpenRouterService(Path.of(System.getProperty("user.dir")));
        }
        return instance;
    }

    public boolean tryBeginInit() {
        return initStarted.compareAndSet(false, true);
    }

    public CompletableFuture<Void> initAsync(Consumer<String> onFailure) {
        LOGGER.debug("OpenRouter init started");
        return CompletableFuture.runAsync(() -> {
            try {
                loadConfig();
                enabled = true;
                LOGGER.debug("OpenRouter ready. flavor={}, advisory={}", flavorModelId, advisoryModelId);
                primeModel(flavorModelId);
                primeModel(advisoryModelId);
            } catch (Exception ex) {
                String reason = ex.getMessage() != null ? ex.getMessage() : "unexpected error";
                LOGGER.debug("OpenRouter init failed: {}", reason);
                failureReason = reason;
                onFailure.accept(reason);
            }
        }, executor);
    }

    private void loadConfig() throws Exception {
        Map<String, String> env;
        try {
            env = EnvLoader.load(workingDir.resolve(".env"));
        } catch (NoSuchFileException e) {
            throw new IllegalStateException(".env file not found.");
        }
        apiKey = env.get("OPENROUTER_API_KEY");
        if (apiKey == null || apiKey.isBlank())
            throw new IllegalStateException("OPENROUTER_API_KEY not set.");

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
    }

    private void primeModel(String modelId) {
        String body = String.format(
            "{\"model\":\"%s\",\"messages\":[{\"role\":\"user\",\"content\":\"ping\"}],\"max_tokens\":1}",
            modelId
        );
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/chat/completions"))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(r -> LOGGER.debug("Model primed: {} (status {})", modelId, r.statusCode()))
            .exceptionally(ex -> {
                LOGGER.warn("Prime failed for {}: {}", modelId, ex.getMessage());
                return null;
            });
    }

    public CompletableFuture<String> query(String role, String prompt) {
        if (!enabled)
            return CompletableFuture.failedFuture(new IllegalStateException("OpenRouter service is not enabled."));
        String modelId = role.equals("advisory") ? advisoryModelId : flavorModelId;
        String body = String.format(
            "{\"model\":\"%s\",\"messages\":[{\"role\":\"user\",\"content\":\"%s\"}],\"temperature\":1.2,\"reasoning\":{\"max_tokens\":400}}",
            modelId, prompt.replace("\"", "\\\"")
        );
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/chat/completions"))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(response -> {
                if (response.statusCode() < 200 || response.statusCode() >= 300)
                    throw new RuntimeException("HTTP " + response.statusCode());
                JsonObject json = GSON.fromJson(response.body(), JsonObject.class);
                String content = json.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
                return content.replaceAll("[^\\x00-\\x7F]", "");
            });
    }

    public CompletableFuture<String> queryAsync(String systemPrompt, List<ChatMessage> history) {
        if (!enabled)
            return CompletableFuture.failedFuture(new IllegalStateException("OpenRouter service is not enabled."));
        JsonObject body = new JsonObject();
        body.addProperty("model", advisoryModelId);
        JsonArray messages = new JsonArray();
        JsonObject sysMsg = new JsonObject();
        sysMsg.addProperty("role", "system");
        sysMsg.addProperty("content", systemPrompt);
        messages.add(sysMsg);
        for (ChatMessage msg : history) {
            JsonObject m = new JsonObject();
            m.addProperty("role", "advisor".equals(msg.role()) ? "assistant" : msg.role());
            m.addProperty("content", msg.content());
            messages.add(m);
        }
        body.add("messages", messages);
        body.addProperty("max_tokens", 500);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/chat/completions"))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(body)))
            .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(response -> {
                if (response.statusCode() < 200 || response.statusCode() >= 300)
                    throw new RuntimeException("HTTP " + response.statusCode());
                JsonObject json = GSON.fromJson(response.body(), JsonObject.class);
                return json.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString()
                    .replaceAll("[^\\x00-\\x7F]", "");
            });
    }

    public void disable() {
        enabled = false;
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
