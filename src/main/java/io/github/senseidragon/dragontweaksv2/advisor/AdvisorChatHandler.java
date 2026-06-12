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
import java.util.concurrent.*;

public class AdvisorChatHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AdvisorChatHandler.class);
    private static final ResourceLocation BUILD_TOOL = ResourceLocation.fromNamespaceAndPath("structurize", "build_tool");
    private static final String SYSTEM_PROMPT =
        "You are a seasoned adventurer — experienced, dry, darkly witty. Speak from hard experience.\n" +
        "No game mechanics, no modern concepts, nothing outside this world. 3–4 sentences. No lists.\n\n";

    @FunctionalInterface
    public interface SessionDataPort {
        AdvisorSavedData get(ServerLevel overworld);
    }

    @FunctionalInterface
    public interface ContextBuilderPort {
        String build(ServerPlayer player, ServerLevel level);
    }

    private final OpenRouterService openRouter;
    private final SessionDataPort sessionData;
    private final ContextBuilderPort contextBuilder;
    private final ScheduledExecutorService scheduler;
    private final java.util.function.Predicate<ServerPlayer> buildToolCheck;

    public AdvisorChatHandler() {
        this(
            OpenRouterService.getInstance(),
            AdvisorSessionManager::get,
            EnvironmentContextBuilder::build,
            Executors.newScheduledThreadPool(2),
            AdvisorChatHandler::hasBuildTool
        );
    }

    AdvisorChatHandler(OpenRouterService openRouter, SessionDataPort sessionData,
                       ContextBuilderPort contextBuilder, ScheduledExecutorService scheduler,
                       java.util.function.Predicate<ServerPlayer> buildToolCheck) {
        this.openRouter = openRouter;
        this.sessionData = sessionData;
        this.contextBuilder = contextBuilder;
        this.scheduler = scheduler;
        this.buildToolCheck = buildToolCheck;
    }

    @SubscribeEvent
    public void onServerChat(ServerChatEvent event) {
        if (!openRouter.isEnabled()) {
            openRouter.disable();
            return;
        }

        ServerPlayer player = event.getPlayer();
        if (!buildToolCheck.test(player)) return;

        event.setCanceled(true);

        ServerLevel overworld = player.getServer().overworld();
        AdvisorSavedData savedData = sessionData.get(overworld);
        AdvisorSession session = savedData.getOrCreate(player.getUUID());
        String context = contextBuilder.build(player, player.serverLevel());
        String playerText = event.getMessage().getString();
        String playerName = player.getName().getString();

        session.addMessage("user", playerText);
        LOG.info("[Advisor] [{}] player: {}", playerName, playerText);

        String systemPrompt = SYSTEM_PROMPT + context;

        ScheduledFuture<?> task5s = scheduler.schedule(
            () -> player.getServer().execute(() -> {
                if (isOnline(player)) player.sendSystemMessage(Component.literal("Hmm..."));
            }), 5, TimeUnit.SECONDS);

        ScheduledFuture<?> task10s = scheduler.schedule(
            () -> player.getServer().execute(() -> {
                if (isOnline(player)) player.sendSystemMessage(Component.literal("How should I put this..."));
            }), 10, TimeUnit.SECONDS);

        ScheduledFuture<?> timeout = scheduler.schedule(() -> {
            player.getServer().execute(() -> {
                if (isOnline(player)) player.sendSystemMessage(Component.literal("Brain fart, sorry."));
            });
            LOG.debug("[Advisor] [{}] timeout — disabling", playerName);
            openRouter.disable();
        }, 60, TimeUnit.SECONDS);

        openRouter.queryAsync(systemPrompt, session.getMessages())
            .thenAccept(response -> {
                task5s.cancel(false);
                task10s.cancel(false);
                timeout.cancel(false);
                session.addMessage("advisor", response);
                savedData.setDirty();
                LOG.info("[Advisor] [{}] advisor: {}", playerName, response);
                player.getServer().execute(() -> {
                    if (isOnline(player)) player.sendSystemMessage(Component.literal(response));
                });
            })
            .exceptionally(err -> {
                task5s.cancel(false);
                task10s.cancel(false);
                timeout.cancel(false);
                LOG.error("[Advisor] [{}] query failed: {}", playerName, err.getMessage());
                openRouter.disable();
                player.getServer().execute(() -> {
                    if (isOnline(player))
                        player.sendSystemMessage(Component.literal("[DragonTweaks] Advisor unavailable."));
                });
                return null;
            });
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        scheduler.shutdown();
    }

    private static boolean hasBuildTool(ServerPlayer player) {
        var item = BuiltInRegistries.ITEM.get(BUILD_TOOL);
        return player.getInventory().hasAnyMatching(stack -> !stack.isEmpty() && stack.getItem().equals(item));
    }

    private static boolean isOnline(ServerPlayer player) {
        return player.getServer() != null &&
            player.getServer().getPlayerList().getPlayer(player.getUUID()) != null;
    }
}
