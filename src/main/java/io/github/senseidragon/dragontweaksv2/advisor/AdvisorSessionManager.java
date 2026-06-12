package io.github.senseidragon.dragontweaksv2.advisor;

import io.github.senseidragon.dragontweaksv2.Config;
import net.minecraft.server.level.ServerLevel;

public final class AdvisorSessionManager {

    private AdvisorSessionManager() {}

    public static AdvisorSavedData get(ServerLevel overworld) {
        return overworld.getDataStorage()
            .computeIfAbsent(AdvisorSavedData.factory(Config.ADVISOR_HISTORY_CAP.get()), AdvisorSavedData.NAME);
    }
}
