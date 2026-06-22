package io.github.senseidragon.dragontweaksv2.advisor;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class AdvisorStatusMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvisorStatusMonitor.class);

    private final ToolCallOrchestrator orchestrator;
    private final Function<ServerPlayer, AdvisorSession> sessionProvider;
    private final int circuitBreakerThreshold;
    private final long windowSeconds;

    private final Map<UUID, List<Long>> eventTimes = new ConcurrentHashMap<>();
    private final Set<UUID> disabledPlayers = ConcurrentHashMap.newKeySet();

    public AdvisorStatusMonitor(ToolCallOrchestrator orchestrator,
                                 Function<ServerPlayer, AdvisorSession> sessionProvider,
                                 int circuitBreakerThreshold,
                                 long windowSeconds) {
        this.orchestrator = orchestrator;
        this.sessionProvider = sessionProvider;
        this.circuitBreakerThreshold = circuitBreakerThreshold;
        this.windowSeconds = windowSeconds;
    }

    @SubscribeEvent
    public void onEffectAdded(MobEffectEvent.Added event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        var effect = event.getEffectInstance().getEffect();
        boolean detrimental = effect.value().getCategory() == MobEffectCategory.HARMFUL;
        ResourceLocation effectId = effect.unwrapKey().map(k -> k.location()).orElse(null);
        if (effectId == null) return;
        AdvisorSession session = sessionProvider.apply(player);
        handleEffectApplied(
            player.getUUID(), effectId, detrimental, session,
            player.getName().getString(),
            msg -> player.sendSystemMessage(Component.literal(msg)),
            player
        );
    }

    @SubscribeEvent
    public void onEffectRemoved(MobEffectEvent.Remove event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ResourceLocation effectId = event.getEffect().unwrapKey()
            .map(k -> k.location()).orElse(null);
        if (effectId == null) return;
        AdvisorSession session = sessionProvider.apply(player);
        handleEffectRemoved(player.getUUID(), effectId, session);
    }

    // package-private for testing — no ServerPlayer needed
    void handleEffectApplied(UUID playerUUID, ResourceLocation effectId, boolean detrimental,
                              AdvisorSession session, String playerName,
                              Consumer<String> messageSender, ServerPlayer playerRef) {
        if (!detrimental) return;
        if (disabledPlayers.contains(playerUUID)) return;
        if (session == null || session.hasBeenNotified(effectId)) return;
        if (isCircuitBroken(playerUUID, effectId, playerName, messageSender)) return;
        session.markNotified(effectId);
        String prompt = "I sense you have been afflicted with " + effectId.getPath() + ". Are you alright?";
        orchestrator.handleQuery(prompt, playerRef, session, msg -> messageSender.accept(msg));
    }

    void handleEffectRemoved(UUID playerUUID, ResourceLocation effectId, AdvisorSession session) {
        if (session != null) session.clearNotified(effectId);
    }

    private boolean isCircuitBroken(UUID uuid, ResourceLocation effectId,
                                     String playerName, Consumer<String> messageSender) {
        long now = System.currentTimeMillis() / 1000;
        List<Long> times = eventTimes.computeIfAbsent(uuid, k -> new ArrayList<>());
        times.removeIf(t -> now - t > windowSeconds);
        times.add(now);

        if (times.size() > circuitBreakerThreshold) {
            disabledPlayers.add(uuid);
            LOGGER.warn("[AdvisorStatusMonitor] Circuit breaker triggered — player={}, effect={}, events={}, window={}s",
                playerName, effectId, times.size(), windowSeconds);
            messageSender.accept("I can no longer sense your condition — too much has happened at once.");
            return true;
        }
        return false;
    }
}
