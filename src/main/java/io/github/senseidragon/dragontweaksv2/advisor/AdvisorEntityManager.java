package io.github.senseidragon.dragontweaksv2.advisor;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.senseidragon.dragontweaksv2.DragonTweaksV2.ADVISOR_ENTITY_TYPE;

public class AdvisorEntityManager {

    private static final Map<UUID, AdvisorEntity> ACTIVE = new ConcurrentHashMap<>();

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        AdvisorEntity entity = new AdvisorEntity(ADVISOR_ENTITY_TYPE.get(), player.serverLevel());
        entity.setPos(player.position());
        player.serverLevel().addFreshEntity(entity);
        ACTIVE.put(player.getUUID(), entity);
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        AdvisorEntity entity = ACTIVE.remove(player.getUUID());
        if (entity != null) entity.discard();
    }

    public static Optional<AdvisorEntity> getEntity(ServerPlayer player) {
        return Optional.ofNullable(ACTIVE.get(player.getUUID()));
    }

    public static void syncPosition(ServerPlayer player) {
        getEntity(player).ifPresent(e -> e.setPos(player.position()));
    }
}
