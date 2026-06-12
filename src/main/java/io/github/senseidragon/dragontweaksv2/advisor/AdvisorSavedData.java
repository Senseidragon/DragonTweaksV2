package io.github.senseidragon.dragontweaksv2.advisor;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AdvisorSavedData extends SavedData {

    public static final String NAME = "dragontweaksv2_advisor_sessions";
    private static final Logger LOG = LoggerFactory.getLogger(AdvisorSavedData.class);

    private final int historyCap;
    private final Map<UUID, AdvisorSession> sessions = new HashMap<>();

    public AdvisorSavedData(int historyCap) {
        this.historyCap = historyCap;
    }

    public AdvisorSession getOrCreate(UUID playerUuid) {
        return sessions.computeIfAbsent(playerUuid, id -> new AdvisorSession(historyCap));
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag sessionsTag = new CompoundTag();
        sessions.forEach((uuid, session) -> sessionsTag.put(uuid.toString(), session.toNbt()));
        tag.put("sessions", sessionsTag);
        return tag;
    }

    public static AdvisorSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        AdvisorSavedData data = new AdvisorSavedData(20);
        if (!tag.contains("sessions")) return data;
        CompoundTag sessionsTag = tag.getCompound("sessions");
        for (String key : sessionsTag.getAllKeys()) {
            try {
                data.sessions.put(UUID.fromString(key), AdvisorSession.fromNbt(sessionsTag.getCompound(key)));
            } catch (Exception e) {
                LOG.warn("Failed to load advisor session for key '{}': {}", key, e.getMessage());
            }
        }
        return data;
    }

    public static SavedData.Factory<AdvisorSavedData> factory(int historyCap) {
        return new SavedData.Factory<>(
            () -> new AdvisorSavedData(historyCap),
            AdvisorSavedData::load,
            null
        );
    }
}
