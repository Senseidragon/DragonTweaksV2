package io.github.senseidragon.dragontweaksv2.advisor.tools;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import io.github.senseidragon.dragontweaksv2.advisor.AdvisorTool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.level.levelgen.structure.Structure;

public class VillageLocatorTool implements AdvisorTool {

    // Matches vanilla's /locate command radius (in structure-chunk spacing units, not blocks)
    // and its skipKnownStructures=false setting -- same bounded, main-thread-safe call Mojang
    // already ships for /locate, not a new category of cost.
    private static final int SEARCH_RADIUS = 100;

    @Override
    public String name() { return "find_nearest_village"; }

    @Override
    public JsonObject definition() {
        JsonObject params = new JsonObject();
        params.addProperty("type", "object");
        params.add("properties", new JsonObject());

        JsonObject fn = new JsonObject();
        fn.addProperty("name", "find_nearest_village");
        fn.addProperty("description",
            "Returns the general direction and approximate distance to the nearest village. No exact coordinates.");
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

            Registry<Structure> structures = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
            HolderSet<Structure> villageHolders = structures.getTag(StructureTags.VILLAGE).orElse(null);
            if (villageHolders == null) {
                return "No village structures are configured in this world.";
            }

            Pair<BlockPos, Holder<Structure>> result = level.getChunkSource().getGenerator()
                .findNearestMapStructure(level, villageHolders, pos, SEARCH_RADIUS, false);
            if (result == null) {
                return "No village found within range.";
            }

            BlockPos villagePos = result.getFirst();
            int dx = villagePos.getX() - pos.getX();
            int dz = villagePos.getZ() - pos.getZ();
            double distance = Math.sqrt((double) dx * dx + (double) dz * dz);
            int rounded = (int) Math.round(distance / 50.0) * 50;

            if (rounded <= 0) {
                return "You're already near a village.";
            }
            return "The nearest village is roughly " + rounded + " blocks to the " + compassDirection(dx, dz) + ".";
        } catch (Exception e) {
            return "[Tool error: village locator unavailable]";
        }
    }

    private static String compassDirection(int dx, int dz) {
        double bearing = Math.toDegrees(Math.atan2(dx, -dz));
        if (bearing < 0) bearing += 360;
        String[] directions = {"north", "northeast", "east", "southeast", "south", "southwest", "west", "northwest"};
        int index = (int) Math.round(bearing / 45.0) % 8;
        return directions[index];
    }
}
