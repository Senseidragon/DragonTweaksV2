package io.github.senseidragon.dragontweaksv2.advisor.tools;

import com.google.gson.JsonObject;
import io.github.senseidragon.dragontweaksv2.advisor.AdvisorTool;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class EnvironmentTool implements AdvisorTool {

    @Override
    public String name() { return "get_environment"; }

    @Override
    public JsonObject definition() {
        JsonObject params = new JsonObject();
        params.addProperty("type", "object");
        params.add("properties", new JsonObject());

        JsonObject fn = new JsonObject();
        fn.addProperty("name", "get_environment");
        fn.addProperty("description",
            "Returns current environmental data: time of day, weather, biome, and elevation relative to sea level (Y=63).");
        fn.add("parameters", params);

        JsonObject tool = new JsonObject();
        tool.addProperty("type", "function");
        tool.add("function", fn);
        return tool;
    }

    @Override
    public String execute(JsonObject args, ServerPlayer player) {
        try {
            ServerLevel level = player.serverLevel();
            BlockPos pos = player.blockPosition();

            String time = timeOfDay(level.getDayTime());
            long day = level.getDayTime() / 24000;
            String weather = weather(level);
            String biome = level.getBiome(pos).unwrapKey()
                .map(k -> k.location().getPath().replace("_", " "))
                .orElse("unknown");
            int elevation = pos.getY() - 63;
            String elevDesc = elevation > 0
                ? elevation + " blocks above sea level"
                : elevation < 0
                    ? Math.abs(elevation) + " blocks below sea level"
                    : "at sea level";

            return "Time: " + time + ". Day: " + day + ". Weather: " + weather +
                   ". Biome: " + biome + ". Elevation: " + elevDesc + ".";
        } catch (Exception e) {
            return "[Tool error: environment unavailable]";
        }
    }

    private static String timeOfDay(long dayTime) {
        long t = dayTime % 24000;
        if (t < 1000)  return "dawn";
        if (t < 6000)  return "morning";
        if (t < 12000) return "midday";
        if (t < 13000) return "dusk";
        if (t < 18000) return "night";
        if (t < 20000) return "midnight";
        return "pre-dawn";
    }

    private static String weather(ServerLevel level) {
        if (level.isThundering()) return "thunderstorm";
        if (level.isRaining())    return "raining";
        return "clear";
    }
}
