package io.github.senseidragon.dragontweaksv2.advisor;

import io.github.senseidragon.dragontweaksv2.openrouter.OpenRouterService;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AdvisorChatHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AdvisorChatHandler.class);
    private static final ResourceLocation BUILD_TOOL = ResourceLocation.fromNamespaceAndPath("structurize", "sceptergold");
    // Single source of truth for the advisor's persona is ToolCallOrchestrator.PERSONA_BIO.
    // Kept public for tests and external callers that build prompts directly.
    public static final String SYSTEM_PROMPT = ToolCallOrchestrator.PERSONA_BIO;

    @FunctionalInterface
    public interface SessionDataPort {
        AdvisorSavedData get(ServerLevel overworld);
    }

    @FunctionalInterface
    public interface ContextBuilderPort {
        String build(ServerPlayer player, ServerLevel level);
    }

    private final OpenRouterService openRouter;
    private final ToolCallOrchestrator orchestrator;
    private final SessionDataPort sessionData;
    private final ContextBuilderPort contextBuilder;
    private final ScheduledExecutorService scheduler;
    private final Predicate<ServerPlayer> buildToolCheck;

    public AdvisorChatHandler() {
        this(
            OpenRouterService.getInstance(),
            null, // orchestrator wired in DragonTweaksV2 after init
            AdvisorSessionManager::get,
            EnvironmentContextBuilder::build,
            Executors.newScheduledThreadPool(2),
            AdvisorChatHandler::hasBuildTool
        );
    }

    public AdvisorChatHandler(OpenRouterService openRouter, ToolCallOrchestrator orchestrator) {
        this(
            openRouter,
            orchestrator,
            AdvisorSessionManager::get,
            EnvironmentContextBuilder::build,
            Executors.newScheduledThreadPool(2),
            AdvisorChatHandler::hasBuildTool
        );
    }

    AdvisorChatHandler(OpenRouterService openRouter, ToolCallOrchestrator orchestrator,
                       SessionDataPort sessionData, ContextBuilderPort contextBuilder,
                       ScheduledExecutorService scheduler, Predicate<ServerPlayer> buildToolCheck) {
        this.openRouter = openRouter;
        this.orchestrator = orchestrator;
        this.sessionData = sessionData;
        this.contextBuilder = contextBuilder;
        this.scheduler = scheduler;
        this.buildToolCheck = buildToolCheck;
    }

    @SubscribeEvent
    public void onServerChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        LOG.info("[Advisor] chat received from {}", player.getName().getString());

        if (!openRouter.isEnabled()) {
            LOG.warn("[Advisor] OpenRouter not enabled, skipping");
            return;
        }

        var mainHand = player.getMainHandItem();
        var mainHandId = mainHand.isEmpty() ? "empty" : String.valueOf(BuiltInRegistries.ITEM.getKey(mainHand.getItem()));
        LOG.info("[Advisor] main hand item: {}", mainHandId);

        if (!buildToolCheck.test(player)) {
            StringBuilder inv = new StringBuilder();
            for (int i = 0; i < 9; i++) {
                var stack = player.getInventory().getItem(i);
                if (!stack.isEmpty())
                    inv.append(i).append(":").append(BuiltInRegistries.ITEM.getKey(stack.getItem())).append(" ");
            }
            LOG.warn("[Advisor] build tool not found. Checking for [{}]. hotbar=[{}]",
                BUILD_TOOL, inv.toString().trim());
            return;
        }

        handleChat(
            player.getName().getString(),
            player.getUUID(),
            event.getMessage().getString(),
            () -> contextBuilder.build(player, player.serverLevel()),
            () -> sessionData.get(player.getServer().overworld()),
            () -> event.setCanceled(true),
            msg -> player.sendSystemMessage(Component.literal(msg)),
            r -> player.getServer().execute(r),
            () -> isOnline(player),
            player
        );
    }

    void handleChat(String playerName, UUID playerId, String chatText,
                    Supplier<String> getContext,
                    Supplier<AdvisorSavedData> getSavedData,
                    Runnable cancelEvent,
                    Consumer<String> deliver,
                    Consumer<Runnable> dispatch,
                    BooleanSupplier isOnline,
                    ServerPlayer player) {
        cancelEvent.run();

        AdvisorSavedData savedData = getSavedData.get();
        AdvisorSession session = savedData.getOrCreate(playerId);
        String context = getContext.get();
        deliver.accept("<" + playerName + "> " + chatText);

        LOG.info("[Advisor] [{}] player: {}", playerName, chatText);

        ScheduledFuture<?> task5s = scheduler.schedule(
            () -> dispatch.accept(() -> { if (isOnline.getAsBoolean()) deliver.accept("Hmm..."); }),
            5, TimeUnit.SECONDS);

        ScheduledFuture<?> task10s = scheduler.schedule(
            () -> dispatch.accept(() -> { if (isOnline.getAsBoolean()) deliver.accept("How should I put this..."); }),
            10, TimeUnit.SECONDS);

        // Orchestrator owns its own 60s timeout — skip scheduling it here to avoid double timeout messages.
        ScheduledFuture<?> timeout = (orchestrator != null) ? null : scheduler.schedule(() -> {
            dispatch.accept(() -> { if (isOnline.getAsBoolean()) deliver.accept("Brain fart, sorry."); });
            LOG.warn("[Advisor] [{}] timeout", playerName);
        }, 60, TimeUnit.SECONDS);

        if (orchestrator != null) {
            orchestrator.handleQuery(chatText, player, session,
                response -> {
                    task5s.cancel(false);
                    task10s.cancel(false);
                    savedData.setDirty();
                    LOG.info("[Advisor] [{}] advisor: {}", playerName, response);
                    dispatch.accept(() -> { if (isOnline.getAsBoolean()) deliver.accept(response); });
                }
            ).exceptionally(err -> {
                task5s.cancel(false);
                task10s.cancel(false);
                LOG.error("[Advisor] [{}] query failed: {}", playerName, err.getMessage());
                dispatch.accept(() -> {
                    if (isOnline.getAsBoolean()) deliver.accept("[DragonTweaks] No response. Try again.");
                });
                return null;
            });
        } else {
            // Fallback: direct queryAsync path (pre-orchestrator or during init)
            String lore = LoreIndex.inject(chatText);
            String systemPrompt = SYSTEM_PROMPT + lore + context;
            session.addMessage("user", chatText);
            openRouter.queryAsync(systemPrompt, session.getMessages())
                .thenAccept(response -> {
                    task5s.cancel(false);
                    task10s.cancel(false);
                    timeout.cancel(false);
                    session.addMessage("advisor", response);
                    savedData.setDirty();
                    LOG.info("[Advisor] [{}] advisor: {}", playerName, response);
                    dispatch.accept(() -> { if (isOnline.getAsBoolean()) deliver.accept(response); });
                })
                .exceptionally(err -> {
                    task5s.cancel(false);
                    task10s.cancel(false);
                    timeout.cancel(false);
                    LOG.error("[Advisor] [{}] query failed: {}", playerName, err.getMessage());
                    dispatch.accept(() -> {
                        if (isOnline.getAsBoolean()) deliver.accept("[DragonTweaks] No response. Try again.");
                    });
                    return null;
                });
        }
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        scheduler.shutdown();
    }

    public static boolean hasBuildTool(ServerPlayer player) {
        return player.getInventory().hasAnyMatching(stack ->
            !stack.isEmpty() && BUILD_TOOL.equals(BuiltInRegistries.ITEM.getKey(stack.getItem())));
    }

    private static boolean isOnline(ServerPlayer player) {
        return player.getServer() != null &&
            player.getServer().getPlayerList().getPlayer(player.getUUID()) != null;
    }
}
