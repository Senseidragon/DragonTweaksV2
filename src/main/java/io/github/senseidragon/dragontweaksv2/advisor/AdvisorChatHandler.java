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
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AdvisorChatHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AdvisorChatHandler.class);
    private static final ResourceLocation BUILD_TOOL = ResourceLocation.fromNamespaceAndPath("structurize", "sceptergold");
    public static final String SYSTEM_PROMPT =
        "You are a friendly mentor and guide: helpful, warm, and concise. " +
        "Always speak in natural, conversational sentences — never use lists or sentence fragments. " +
        "Greetings and farewells: one brief reply, 4 words or fewer. " +
        "Questions and requests: answer in one or two natural sentences, then stop. " +
        "Speak only from the context below; if something is missing, say so briefly.\n\n";

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
    private final Predicate<ServerPlayer> buildToolCheck;

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
                       Predicate<ServerPlayer> buildToolCheck) {
        this.openRouter = openRouter;
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
            () -> isOnline(player)
        );
    }

    void handleChat(String playerName, UUID playerId, String chatText,
                    Supplier<String> getContext,
                    Supplier<AdvisorSavedData> getSavedData,
                    Runnable cancelEvent,
                    Consumer<String> deliver,
                    Consumer<Runnable> dispatch,
                    BooleanSupplier isOnline) {
        cancelEvent.run();

        AdvisorSavedData savedData = getSavedData.get();
        AdvisorSession session = savedData.getOrCreate(playerId);
        String context = getContext.get();
        deliver.accept("<" + playerName + "> " + chatText);

        session.addMessage("user", chatText);
        LOG.info("[Advisor] [{}] player: {}", playerName, chatText);

        String lore = LoreIndex.inject(chatText);
        String systemPrompt = SYSTEM_PROMPT + lore + context;
        LOG.info("[Advisor] prompt: {}", systemPrompt);

        ScheduledFuture<?> task5s = scheduler.schedule(
            () -> dispatch.accept(() -> { if (isOnline.getAsBoolean()) deliver.accept("Hmm..."); }),
            5, TimeUnit.SECONDS);

        ScheduledFuture<?> task10s = scheduler.schedule(
            () -> dispatch.accept(() -> { if (isOnline.getAsBoolean()) deliver.accept("How should I put this..."); }),
            10, TimeUnit.SECONDS);

        ScheduledFuture<?> timeout = scheduler.schedule(() -> {
            dispatch.accept(() -> { if (isOnline.getAsBoolean()) deliver.accept("Brain fart, sorry."); });
            LOG.warn("[Advisor] [{}] timeout — disabling", playerName);
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

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        scheduler.shutdown();
    }

    private static boolean hasBuildTool(ServerPlayer player) {
        return player.getInventory().hasAnyMatching(stack ->
            !stack.isEmpty() && BUILD_TOOL.equals(BuiltInRegistries.ITEM.getKey(stack.getItem())));
    }

    private static boolean isOnline(ServerPlayer player) {
        return player.getServer() != null &&
            player.getServer().getPlayerList().getPlayer(player.getUUID()) != null;
    }
}
