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

    static final String PERSONA_BIO =
        "You are a seasoned adventurer who has spent years living in and surviving this land. " +
        "You speak plainly, from experience, the way someone talks while working — not the way someone lectures. " +
        "You answer exactly what you're asked, nothing more; you don't pad an answer with extra observations nobody asked for, " +
        "and you don't tack on a closing remark when you're done, you just stop. " +
        "You never speak on your surroundings, your gear, or your condition unless you've actually checked them first — " +
        "you're careful that way, the same as any adventurer who's survived this long. " +
        "You've never set foot outside this land and have nothing to say about places, things, or ideas beyond it.\n\n";

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

                List<ChatMessage> history = shouldIncludeHistory(playerMessage)
                    ? session.getMessages() : List.of();

                List<JsonObject> defs = toolDefinitions();

                OpenRouterResponse rt1 = openRouter
                    .sendWithTools(systemPrompt, history, playerMessage, defs)
                    .get(TOTAL_TIMEOUT_MS, TimeUnit.MILLISECONDS);

                if (!rt1.hasToolCalls()) {
                    String text = rt1.textContent() != null ? rt1.textContent() : "";
                    if (text.isBlank()) {
                        LOGGER.warn("[ToolCallOrchestrator] Model returned no tool calls and no content");
                        if (isOnline.getAsBoolean()) responseCallback.accept("Ask me again — I didn't quite get that.");
                        return;
                    }
                    session.addMessage("user", playerMessage);
                    session.addMessage("advisor", text);
                    responseCallback.accept(text);
                    return;
                }

                List<ToolResult> results = executeTools(rt1.toolCalls(), player, executor);

                if (!isOnline.getAsBoolean()) {
                    session.addMessage("user", playerMessage);
                    session.addMessage("advisor", "[disconnected]");
                    return;
                }

                String finalText = openRouter
                    .sendWithToolResults(systemPrompt, history, playerMessage,
                                         rt1.toolCalls(), results, defs)
                    .get(TOTAL_TIMEOUT_MS, TimeUnit.MILLISECONDS);

                String text = finalText != null ? finalText : "";
                if (text.isBlank()) {
                    LOGGER.warn("[ToolCallOrchestrator] Model returned empty text after tool results");
                    if (isOnline.getAsBoolean()) responseCallback.accept("Ask me again — I didn't quite get that.");
                    return;
                }
                session.addMessage("user", playerMessage);
                session.addMessage("advisor", text);
                responseCallback.accept(text);

            } catch (java.util.concurrent.TimeoutException e) {
                String name = player != null ? player.getName().getString() : "unknown";
                LOGGER.warn("[ToolCallOrchestrator] Timeout for player {}", name);
                if (isOnline.getAsBoolean()) responseCallback.accept("I got a bit turned around — ask me again.");
            } catch (Exception e) {
                LOGGER.error("[ToolCallOrchestrator] Unexpected error", e);
            }
        });
    }

    // package-private for testing
    boolean shouldIncludeHistory(String playerMessage) {
        String lower = playerMessage.toLowerCase(Locale.ROOT);
        if (lower.contains("you said") || lower.contains("earlier") ||
            lower.contains("what about") || lower.contains("tell me more")) {
            return true;
        }
        if (lower.contains("what do i have") || lower.contains("scan") ||
            lower.contains("what's around") || lower.contains("what is around")) {
            return false;
        }
        return true;
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
                    future.complete(new ToolResult(call.id(), tool.execute(call.args(), player)));
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
