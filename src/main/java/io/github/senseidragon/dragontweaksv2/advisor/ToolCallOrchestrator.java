package io.github.senseidragon.dragontweaksv2.advisor;

import com.google.gson.JsonObject;
import io.github.senseidragon.dragontweaksv2.advisor.model.OpenRouterResponse;
import io.github.senseidragon.dragontweaksv2.advisor.model.ToolCall;
import io.github.senseidragon.dragontweaksv2.advisor.model.ToolResult;
import io.github.senseidragon.dragontweaksv2.openrouter.OpenRouterService;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ToolCallOrchestrator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToolCallOrchestrator.class);
    static final long TOTAL_TIMEOUT_MS = 60_000;
    static final long TOOL_TIMEOUT_MS  = 10_000;

    /** Raised completion budget for queries known upfront to need more headroom: no
     *  deterministic category matched (free-form reasoning) or multiple tool results
     *  are being synthesized in one turn. Sized once, proactively — no retry on failure. */
    static final int COMPLEX_MAX_TOKENS = 1500;

    static final String PERSONA_BIO =
        "You are a seasoned adventurer who has spent years living in and surviving this land. " +
        "You speak plainly, from experience, the way someone talks while working — not the way someone lectures. " +
        "You talk in plain sentences, the way you'd say it out loud — never bullet points, dashes, or headers; " +
        "nobody describes what they saw to a friend with a list. " +
        "You answer exactly what you're asked, nothing more; you don't pad an answer with extra observations nobody asked for, " +
        "and you don't tack on a closing remark when you're done, you just stop. " +
        "You never speak on your surroundings, your gear, or your condition unless you've actually checked them first — " +
        "you're careful that way, the same as any adventurer who's survived this long. " +
        "If you're asked how something works and you've never learned it firsthand, you say plainly that you don't know — " +
        "you'd rather admit you don't know than guess and sound a fool. " +
        "You've never set foot outside this land and have nothing to say about places, things, or ideas beyond it. " +
        "What the tools show you is what you know. You don't revise your read of the area because someone pushes back — " +
        "if the scan came up empty, that's what you saw. You'd rather say you saw nothing than invent something you didn't.\n\n";

    private final OpenRouterService openRouter;
    private final List<AdvisorTool> tools;
    private final boolean modelRetainsContext;
    final Map<String, AdvisorTool> registry;

    public ToolCallOrchestrator(OpenRouterService openRouter,
                                 List<AdvisorTool> tools,
                                 boolean modelRetainsContext) {
        this.openRouter = openRouter;
        this.tools = tools != null ? tools : List.of();
        this.modelRetainsContext = modelRetainsContext;
        this.registry = new HashMap<>();
        if (tools != null) tools.forEach(t -> registry.put(t.name(), t));
    }

    /**
     * Handles a player query end-to-end.
     * The session must NOT already contain the current player message — this method adds it.
     */
    public CompletableFuture<Void> handleQuery(
            String playerMessage,
            ServerPlayer player,
            AdvisorSession session,
            Consumer<String> responseCallback) {
        return handleQuery(
            playerMessage, player, session, responseCallback,
            r -> player.getServer().execute(r),
            () -> isOnline(player));
    }

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
                Optional<Category> category = classify(playerMessage);

                List<ChatMessage> history = shouldIncludeHistory(playerMessage)
                    ? session.getMessages() : List.of();

                List<JsonObject> defs = toolDefinitions();

                // No matched category means free-form reasoning with no deterministic
                // grounding tool — give it more headroom upfront.
                OpenRouterResponse rt1 = (category.isEmpty()
                        ? openRouter.sendWithTools(systemPrompt, history, playerMessage, defs, COMPLEX_MAX_TOKENS)
                        : openRouter.sendWithTools(systemPrompt, history, playerMessage, defs))
                    .get(TOTAL_TIMEOUT_MS, TimeUnit.MILLISECONDS);

                if (rt1.hasToolCalls()) {
                    executeToolsAndDeliver(rt1.toolCalls(), playerMessage, systemPrompt, history, defs,
                        player, session, responseCallback, executor, isOnline);
                    return;
                }

                if (category.isPresent() && !category.get().tools().isEmpty()) {
                    // Known category with tool(s), but round 1 made no tool calls — don't ask
                    // again and trust a freeform retry. Ground deterministically instead.
                    List<ToolCall> forcedCalls = category.get().tools().stream()
                        .map(toolName -> new ToolCall("forced-" + toolName, toolName, new JsonObject()))
                        .collect(Collectors.toList());
                    executeToolsAndDeliver(forcedCalls, playerMessage, systemPrompt, history, defs,
                        player, session, responseCallback, executor, isOnline);
                    return;
                }

                if (category.isPresent()) {
                    // Chitchat: no tool to ground with — round 1's text is the answer.
                    deliverTextOnly(rt1.textContent(), playerMessage, session, responseCallback, isOnline);
                    return;
                }

                // No category matched — genuinely ambiguous, and there's no known tool to
                // inject. Force a second attempt before delivering anything to the player.
                String groundingPrompt = systemPrompt +
                    "\n\nYour previous answer made no tool call, but this question may need checked " +
                    "information you don't actually have. If a tool can answer it, call it now. If " +
                    "not, say plainly you don't know rather than guessing.";
                OpenRouterResponse rt2 = openRouter
                    .sendWithTools(groundingPrompt, history, playerMessage, defs, COMPLEX_MAX_TOKENS)
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

        // Synthesizing multiple tool results in one turn needs more headroom up front.
        boolean complex = results.size() > 1;

        String finalText = sendToolResults(systemPrompt, history, playerMessage, calls, results, defs, complex)
            .get(TOTAL_TIMEOUT_MS, TimeUnit.MILLISECONDS);

        if (finalText == null || finalText.isBlank()) {
            // The model occasionally returns an empty completion when synthesizing
            // tool results into text. One retry before falling back to the "ask again" message.
            finalText = sendToolResults(systemPrompt, history, playerMessage, calls, results, defs, complex)
                .get(TOTAL_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        }

        deliverTextOnly(finalText, playerMessage, session, responseCallback, isOnline);
    }

    private CompletableFuture<String> sendToolResults(String systemPrompt, List<ChatMessage> history,
            String playerMessage, List<ToolCall> calls, List<ToolResult> results, List<JsonObject> defs,
            boolean complex) {
        return complex
            ? openRouter.sendWithToolResults(systemPrompt, history, playerMessage, calls, results, defs,
                COMPLEX_MAX_TOKENS)
            : openRouter.sendWithToolResults(systemPrompt, history, playerMessage, calls, results, defs);
    }

    // package-private for testing
    record Category(String name, List<String> signals, List<String> tools, boolean includeHistory) {}

    private static final List<Category> CATEGORIES = List.of(
        // Must come before "location" and "scan" — "what kind of logs" matches "nearby" too
        new Category("identify", List.of("what kind", "what type of", "which kind", "which type", "identify"),
            List.of("identify_nearby"), true),
        new Category("environment", List.of("what time", "weather", "biome"),
            List.of("get_environment"), true),
        new Category("inventory", List.of("inventory", "holding", "wearing", "what do i have"),
            List.of("get_inventory"), false),
        new Category("status", List.of("health", "effect", "how am i feeling"),
            List.of("get_status"), true),
        new Category("scan", List.of("creature", "threat", "scan"),
            List.of("scan_area"), false),
        // Must come before "location" -- "where is the nearest village" matches both signals,
        // and only this category's tool can actually answer a village-finding question.
        new Category("village", List.of("village"),
            List.of("find_nearest_village"), false),
        new Category("location", List.of("where", "nearby", "around me", "see"),
            List.of("get_environment", "scan_area"), false),
        new Category("chitchat", List.of("hello", "hi", "hey", "thanks", "thank you", "bye", "goodbye", "lol"),
            List.of(), true)
    );

    // package-private for testing — first matching category wins, in table order above
    Optional<Category> classify(String playerMessage) {
        String lower = playerMessage.toLowerCase(Locale.ROOT);
        return CATEGORIES.stream()
            .filter(c -> c.signals().stream().anyMatch(s -> containsWord(lower, s)))
            .findFirst();
    }

    private static boolean containsWord(String lowerText, String phrase) {
        if (phrase.contains(" ")) return lowerText.contains(phrase);
        return java.util.regex.Pattern.compile("\\b" + java.util.regex.Pattern.quote(phrase) + "\\b")
            .matcher(lowerText).find();
    }

    // package-private for testing
    boolean shouldIncludeHistory(String playerMessage) {
        String lower = playerMessage.toLowerCase(Locale.ROOT);
        if (lower.contains("you said") || lower.contains("earlier") ||
            lower.contains("what about") || lower.contains("tell me more")) {
            return true;
        }
        return classify(playerMessage).map(Category::includeHistory).orElse(true);
    }

    List<JsonObject> toolDefinitions() {
        return tools.stream().map(AdvisorTool::definition).collect(Collectors.toList());
    }

    private List<ToolResult> executeTools(List<ToolCall> calls, ServerPlayer player,
                                           Consumer<Runnable> executor) {
        List<CompletableFuture<ToolResult>> futures = new ArrayList<>();

        for (ToolCall call : calls) {
            CompletableFuture<ToolResult> future = new CompletableFuture<>();
            executor.accept(() -> {
                AdvisorTool tool = registry.get(call.name());
                if (tool == null) {
                    LOGGER.warn("[ToolCallOrchestrator] Unrecognized tool: {}", call.name());
                    future.complete(new ToolResult(call.id(), "[Unknown tool: " + call.name() + "]"));
                    return;
                }
                try {
                    String result = tool.execute(call.args(), player);
                    for (String line : result.split("\n", -1)) {
                        LOGGER.info("[DT_TOOL] [{}] {}", call.name(), line);
                    }
                    future.complete(new ToolResult(call.id(), result));
                } catch (Exception e) {
                    LOGGER.debug("[ToolCallOrchestrator] Tool '{}' failed: {}", call.name(), e.getMessage());
                    future.complete(new ToolResult(call.id(), "[Tool error: " + call.name() + " unavailable]"));
                }
            });
            futures.add(future);
        }

        List<ToolResult> results = new ArrayList<>();
        for (int i = 0; i < calls.size(); i++) {
            try {
                results.add(futures.get(i).get(TOOL_TIMEOUT_MS, TimeUnit.MILLISECONDS));
            } catch (Exception e) {
                results.add(new ToolResult(calls.get(i).id(), "[Tool error: timeout]"));
            }
        }
        return results;
    }

    private static boolean isOnline(ServerPlayer player) {
        return player != null && player.getServer() != null &&
            player.getServer().getPlayerList().getPlayer(player.getUUID()) != null;
    }

    String buildSystemPrompt(String loreBlock) {
        StringBuilder sb = new StringBuilder();
        sb.append(PERSONA_BIO);
        if (!loreBlock.isEmpty()) {
            sb.append(loreBlock);
        }
        return sb.toString();
    }
}
