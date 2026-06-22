package io.github.senseidragon.dragontweaksv2.openrouter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.senseidragon.dragontweaksv2.advisor.ChatMessage;
import io.github.senseidragon.dragontweaksv2.advisor.model.OpenRouterResponse;
import io.github.senseidragon.dragontweaksv2.advisor.model.ToolCall;
import io.github.senseidragon.dragontweaksv2.advisor.model.ToolResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OpenRouterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenRouterService.class);
    private static final String BASE_URL = "https://openrouter.ai/api/v1";
    private static final Gson GSON = new Gson();

    /** Advisor dialogue completion budget. Must comfortably cover this reasoning model's
     *  hidden chain-of-thought (observed 140-200 tokens) plus a full visible answer. */
    static final int ADVISOR_MAX_TOKENS = 1000;

    private static volatile OpenRouterService instance;

    private final Path workingDir;
    private final ExecutorService initExecutor;
    private final ExecutorService queryExecutor;
    private final HttpClient httpClient;

    private final AtomicBoolean initStarted = new AtomicBoolean(false);
    private volatile boolean enabled = false;
    private volatile String failureReason = null;
    private volatile String apiKey;
    private volatile String flavorModelId;
    private volatile String advisoryModelId;
    private volatile boolean modelRetainsContext = false;

    public OpenRouterService(Path workingDir) {
        this.workingDir = workingDir;
        this.initExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "openrouter-init");
            t.setDaemon(true);
            return t;
        });
        this.queryExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "openrouter-query");
            t.setDaemon(true);
            return t;
        });
        this.httpClient = HttpClient.newBuilder().executor(initExecutor).build();
    }

    /** Package-private constructor for tests that need to inject a custom {@link HttpClient}. */
    OpenRouterService(Path workingDir, HttpClient httpClient) {
        this.workingDir = workingDir;
        this.initExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "openrouter-init");
            t.setDaemon(true);
            return t;
        });
        this.queryExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "openrouter-query");
            t.setDaemon(true);
            return t;
        });
        this.httpClient = httpClient;
    }

    /** Package-private setter for tests — sets model IDs and API key without loading config files. */
    void setModelIdsForTest(String flavorModelId, String advisoryModelId, String apiKey) {
        this.flavorModelId = flavorModelId;
        this.advisoryModelId = advisoryModelId;
        this.apiKey = apiKey;
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
        return CompletableFuture.runAsync(() -> {
            try {
                loadConfig();
                enabled = true;
                LOGGER.info("OpenRouter ready. flavor={}, advisory={}", flavorModelId, advisoryModelId);
                primeModel(flavorModelId);
                primeModel(advisoryModelId);
            } catch (Exception ex) {
                String reason = ex.getMessage() != null ? ex.getMessage() : "unexpected error";
                LOGGER.warn("OpenRouter init failed: {}", reason);
                failureReason = reason;
                onFailure.accept(reason);
            }
        }, initExecutor);
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
            .exceptionally(ex -> {
                LOGGER.warn("Model prime failed for {}: {}", modelId, ex.getMessage());
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
            .thenApplyAsync(response -> {
                if (response.statusCode() < 200 || response.statusCode() >= 300)
                    throw new RuntimeException("HTTP " + response.statusCode());
                JsonObject json = GSON.fromJson(response.body(), JsonObject.class);
                String content = json.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
                return content.replaceAll("[^\\x00-\\x7F]", "");
            }, queryExecutor);
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
        body.addProperty("max_tokens", ADVISOR_MAX_TOKENS);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/chat/completions"))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(body)))
            .build();
        final long start = System.currentTimeMillis();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApplyAsync(response -> {
                long elapsed = System.currentTimeMillis() - start;
                if (response.statusCode() < 200 || response.statusCode() >= 300)
                    throw new RuntimeException("HTTP " + response.statusCode() + ": " + response.body());
                JsonObject json = GSON.fromJson(response.body(), JsonObject.class);
                var contentEl = json.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content");
                if (contentEl == null || contentEl.isJsonNull()) {
                    LOGGER.warn("[Advisor] null content from {} (finish_reason={}). body: {}",
                        advisoryModelId,
                        json.getAsJsonArray("choices").get(0).getAsJsonObject().get("finish_reason"),
                        response.body());
                    throw new RuntimeException("null content");
                }
                String content = contentEl.getAsString()
                    .replaceAll("[^\\x00-\\x7F]", "");
                LOGGER.info("[Advisor] response {}ms model={}", elapsed, advisoryModelId);
                return content;
            }, queryExecutor);
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    /** Builds a base request body with system prompt + history + a final user turn, using the default advisor budget. */
    JsonObject buildRequestBody(String systemPrompt, List<ChatMessage> history, String userMessage) {
        return buildRequestBody(systemPrompt, history, userMessage, ADVISOR_MAX_TOKENS);
    }

    /** Builds a base request body with an explicit completion token budget. */
    JsonObject buildRequestBody(String systemPrompt, List<ChatMessage> history, String userMessage, int maxTokens) {
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
        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", userMessage);
        messages.add(userMsg);
        body.add("messages", messages);
        body.addProperty("max_tokens", maxTokens);
        return body;
    }

    /** Sends a synchronous HTTP POST to the completions endpoint and returns the raw response body. */
    String sendHttpRequest(JsonObject body) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/chat/completions"))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(body)))
            .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300)
                throw new RuntimeException("HTTP " + response.statusCode() + ": " + response.body());
            return response.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("HTTP request interrupted", e);
        } catch (java.io.IOException e) {
            throw new RuntimeException("HTTP request failed", e);
        }
    }

    /** Async variant used by query methods — does not block any thread. */
    private CompletableFuture<String> sendAsync(JsonObject body) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/chat/completions"))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(body)))
            .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(response -> {
                if (response.statusCode() < 200 || response.statusCode() >= 300)
                    throw new RuntimeException("HTTP " + response.statusCode() + ": " + response.body());
                return response.body();
            });
    }

    /** Parses a raw OpenRouter completion response into an {@link OpenRouterResponse}. */
    private OpenRouterResponse parseOpenRouterResponse(String rawJson) {
        JsonObject root = JsonParser.parseString(rawJson).getAsJsonObject();
        JsonObject choice = root.getAsJsonArray("choices").get(0).getAsJsonObject();
        JsonObject message = choice.getAsJsonObject("message");

        JsonElement toolCallsEl = message.get("tool_calls");
        if (toolCallsEl != null && !toolCallsEl.isJsonNull()) {
            List<ToolCall> calls = new ArrayList<>();
            for (JsonElement el : toolCallsEl.getAsJsonArray()) {
                JsonObject tc = el.getAsJsonObject();
                String id = tc.get("id").getAsString();
                JsonObject fn = tc.getAsJsonObject("function");
                String name = fn.get("name").getAsString();
                JsonObject args = JsonParser.parseString(fn.get("arguments").getAsString()).getAsJsonObject();
                calls.add(new ToolCall(id, name, args));
            }
            return new OpenRouterResponse(null, calls);
        }

        JsonElement finishReasonEl = choice.get("finish_reason");
        String finishReason = (finishReasonEl != null && !finishReasonEl.isJsonNull())
            ? finishReasonEl.getAsString() : "unknown";
        if ("length".equals(finishReason)) {
            LOGGER.warn("[Advisor] response truncated by max_tokens. model={}, finish_reason={}, usage={}",
                advisoryModelId, finishReason, root.has("usage") ? root.get("usage") : "absent");
        }

        JsonElement contentEl = message.get("content");
        String text = (contentEl != null && !contentEl.isJsonNull())
            ? contentEl.getAsString().replaceAll("<\\|[^|]*\\|>", "").trim()
            : "";
        return new OpenRouterResponse(text, List.of());
    }

    // -----------------------------------------------------------------------
    // Tool-calling API
    // -----------------------------------------------------------------------

    /**
     * Sends a request with optional tool definitions. If the model responds with a
     * tool-call, the returned {@link OpenRouterResponse} will have {@code hasToolCalls() == true}.
     * Must not be called on the Minecraft main/server/render thread.
     */
    public CompletableFuture<OpenRouterResponse> sendWithTools(
            String systemPrompt,
            List<ChatMessage> history,
            String userMessage,
            List<JsonObject> toolDefinitions) {
        return sendWithTools(systemPrompt, history, userMessage, toolDefinitions, ADVISOR_MAX_TOKENS);
    }

    /**
     * Same as {@link #sendWithTools(String, List, String, List)} but with an explicit
     * completion token budget, for callers that know upfront a query needs more headroom
     * (e.g. no deterministic tool category matched, so the model must reason freely).
     */
    public CompletableFuture<OpenRouterResponse> sendWithTools(
            String systemPrompt,
            List<ChatMessage> history,
            String userMessage,
            List<JsonObject> toolDefinitions,
            int maxTokens) {

        JsonObject body = buildRequestBody(systemPrompt, history, userMessage, maxTokens);
        if (!toolDefinitions.isEmpty()) {
            JsonArray toolsArray = new JsonArray();
            toolDefinitions.forEach(toolsArray::add);
            body.add("tools", toolsArray);
            body.addProperty("tool_choice", "auto");
        }
        return sendAsync(body).thenComposeAsync(raw -> {
            OpenRouterResponse parsed = parseOpenRouterResponse(raw);
            if (parsed.hasToolCalls()) return CompletableFuture.completedFuture(parsed);
            return repairBannedPhrasesIfNeeded(parsed.textContent())
                .thenApply(repaired -> new OpenRouterResponse(repaired, List.of()));
        }, queryExecutor);
    }

    /**
     * Sends round-trip 2: appends the assistant's tool-call message and the tool
     * result messages to the conversation, then returns the model's final text response.
     * Must not be called on the Minecraft main/server/render thread.
     */
    public CompletableFuture<String> sendWithToolResults(
            String systemPrompt,
            List<ChatMessage> history,
            String userMessage,
            List<ToolCall> priorToolCalls,
            List<ToolResult> results,
            List<JsonObject> toolDefinitions) {
        return sendWithToolResults(systemPrompt, history, userMessage, priorToolCalls, results, toolDefinitions,
            ADVISOR_MAX_TOKENS);
    }

    /**
     * Same as {@link #sendWithToolResults(String, List, String, List, List, List)} but with an
     * explicit completion token budget, for callers that know upfront a query needs more
     * headroom (e.g. synthesizing multiple tool results at once).
     */
    public CompletableFuture<String> sendWithToolResults(
            String systemPrompt,
            List<ChatMessage> history,
            String userMessage,
            List<ToolCall> priorToolCalls,
            List<ToolResult> results,
            List<JsonObject> toolDefinitions,
            int maxTokens) {

        JsonObject body = buildRequestBody(systemPrompt, history, userMessage, maxTokens);
        JsonArray messages = body.getAsJsonArray("messages");

        // Append assistant's tool-call message
        JsonArray callsArray = new JsonArray();
        for (ToolCall tc : priorToolCalls) {
            JsonObject fn = new JsonObject();
            fn.addProperty("name", tc.name());
            fn.addProperty("arguments", tc.args().toString());
            JsonObject call = new JsonObject();
            call.addProperty("id", tc.id());
            call.addProperty("type", "function");
            call.add("function", fn);
            callsArray.add(call);
        }
        JsonObject assistantMsg = new JsonObject();
        assistantMsg.addProperty("role", "assistant");
        assistantMsg.add("content", JsonNull.INSTANCE);
        assistantMsg.add("tool_calls", callsArray);
        messages.add(assistantMsg);

        // Append tool result messages
        for (ToolResult result : results) {
            JsonObject toolMsg = new JsonObject();
            toolMsg.addProperty("role", "tool");
            toolMsg.addProperty("tool_call_id", result.toolCallId());
            toolMsg.addProperty("content", result.content());
            messages.add(toolMsg);
        }

        if (!toolDefinitions.isEmpty()) {
            JsonArray toolsArray = new JsonArray();
            toolDefinitions.forEach(toolsArray::add);
            body.add("tools", toolsArray);
        }

        return sendAsync(body).thenComposeAsync(
            raw -> repairBannedPhrasesIfNeeded(parseOpenRouterResponse(raw).textContent()), queryExecutor);
    }

    // -----------------------------------------------------------------------
    // Capability probe
    // -----------------------------------------------------------------------

    /**
     * Probes whether the model retains context across two independent turns with no
     * shared history. Two sequential synchronous HTTP calls are made (no system prompt,
     * no history, no tools). Must only be called off the Minecraft main/server/render thread.
     *
     * @return {@code true} if the model's reply to call 2 contains "apple" (case-insensitive)
     */
    public boolean probeContextRetention() {
        try {
            // Call 1 — tell the model we have an apple
            JsonObject body1 = new JsonObject();
            body1.addProperty("model", advisoryModelId);
            JsonArray msgs1 = new JsonArray();
            JsonObject u1 = new JsonObject();
            u1.addProperty("role", "user");
            u1.addProperty("content", "I have an apple in my left hand.");
            msgs1.add(u1);
            body1.add("messages", msgs1);
            sendHttpRequest(body1); // response ignored

            // Call 2 — ask what we are holding (no history passed)
            JsonObject body2 = new JsonObject();
            body2.addProperty("model", advisoryModelId);
            JsonArray msgs2 = new JsonArray();
            JsonObject u2 = new JsonObject();
            u2.addProperty("role", "user");
            u2.addProperty("content", "What am I holding?");
            msgs2.add(u2);
            body2.add("messages", msgs2);
            String response = sendHttpRequest(body2);

            JsonObject root = JsonParser.parseString(response).getAsJsonObject();
            String content = root.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString().toLowerCase();

            return content.contains("apple");
        } catch (Exception e) {
            LOGGER.warn("[OpenRouterService] Capability probe failed, defaulting to false", e);
            return false;
        }
    }

    private static final List<String> BANNED_PHRASES =
        List.of("scan", "data", "results", "that's all", "hope that helps");
    private static final long REPAIR_TIMEOUT_MS = 5_000;
    private static final Pattern SENTENCE_SPLIT = Pattern.compile("(?<=[.!?])\\s+");

    static String stripBannedPhrases(String text) {
        if (text == null || text.isBlank()) return text == null ? "" : text.trim();
        String result = text;
        for (String phrase : BANNED_PHRASES) {
            result = result.replaceAll("(?i)\\b" + Pattern.quote(phrase) + "\\b[.,!]?\\s*", " ");
        }
        String stripped = result.replaceAll("\\s{2,}", " ").trim();
        if (!stripped.equals(text.trim())) {
            LOGGER.info("[Advisor] stripBannedPhrases changed response text. before=\"{}\" after=\"{}\"", text, stripped);
        }
        return stripped;
    }

    /** Returns the banned phrases (if any) literally present in {@code text}, word-boundary matched. */
    static List<String> findBannedPhraseHits(String text) {
        if (text == null || text.isBlank()) return List.of();
        List<String> hits = new ArrayList<>();
        for (String phrase : BANNED_PHRASES) {
            if (Pattern.compile("(?i)\\b" + Pattern.quote(phrase) + "\\b").matcher(text).find()) {
                hits.add(phrase);
            }
        }
        return hits;
    }

    /**
     * Asks the model to rephrase a single sentence to remove specific banned words, with no
     * system prompt, tools, or history — an isolated, minimal completion. Must not be called
     * on the Minecraft main/server/render thread.
     */
    CompletableFuture<String> repairSentence(String sentence, List<String> hitPhrases) {
        String prompt = "Rephrase the following sentence to remove these words while preserving its meaning and voice. " +
            "Output only the rephrased sentence, nothing else. Words to remove: " +
            String.join(", ", hitPhrases) + ". Sentence: \"" + sentence + "\"";

        JsonObject body = new JsonObject();
        body.addProperty("model", advisoryModelId);
        JsonArray messages = new JsonArray();
        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", prompt);
        messages.add(userMsg);
        body.add("messages", messages);
        body.addProperty("max_tokens", 60);
        body.addProperty("temperature", 0.3);

        return sendAsync(body).thenApplyAsync(raw -> {
            JsonObject root = JsonParser.parseString(raw).getAsJsonObject();
            JsonElement contentEl = root.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content");
            if (contentEl == null || contentEl.isJsonNull()) return "";
            return contentEl.getAsString().replaceAll("<\\|[^|]*\\|>", "").trim();
        }, queryExecutor);
    }

    /**
     * Repairs banned phrases sentence-by-sentence via {@link #repairSentence}, falling back to a
     * mechanical strip (limited to the offending sentence) on timeout, error, or a still-dirty
     * rephrase. Clean text is returned unchanged with no extra calls. Must not be called on the
     * Minecraft main/server/render thread.
     */
    CompletableFuture<String> repairBannedPhrasesIfNeeded(String text) {
        if (text == null || text.isBlank() || findBannedPhraseHits(text).isEmpty()) {
            return CompletableFuture.completedFuture(text == null ? "" : text);
        }

        String[] sentences = SENTENCE_SPLIT.split(text.trim());
        List<CompletableFuture<String>> pieces = new ArrayList<>();
        for (String sentence : sentences) {
            List<String> hits = findBannedPhraseHits(sentence);
            if (hits.isEmpty()) {
                pieces.add(CompletableFuture.completedFuture(sentence));
                continue;
            }
            pieces.add(repairSentence(sentence, hits)
                .orTimeout(REPAIR_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .thenApply(rephrased -> findBannedPhraseHits(rephrased).isEmpty() ? rephrased : stripBannedPhrases(sentence))
                .exceptionally(ex -> stripBannedPhrases(sentence)));
        }

        return CompletableFuture.allOf(pieces.toArray(new CompletableFuture[0]))
            .thenApply(v -> pieces.stream().map(CompletableFuture::join).collect(Collectors.joining(" ")));
    }

    public void disable() {
        enabled = false;
    }

    public boolean isEnabled() { return enabled; }
    public String getFailureReason() { return failureReason; }
    public String getFlavorModelId() { return flavorModelId; }
    public String getAdvisoryModelId() { return advisoryModelId; }
    public boolean isModelRetainsContext() { return modelRetainsContext; }

    public void shutdown() {
        initExecutor.shutdown();
        queryExecutor.shutdown();
        instance = null;
    }
}
