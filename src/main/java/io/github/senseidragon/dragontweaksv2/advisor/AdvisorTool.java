package io.github.senseidragon.dragontweaksv2.advisor;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;

public interface AdvisorTool {
    String name();
    JsonObject definition();
    String execute(JsonObject args, ServerPlayer player);
}
