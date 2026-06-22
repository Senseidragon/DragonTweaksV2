package io.github.senseidragon.dragontweaksv2.advisor.tools;

import com.google.gson.JsonObject;
import io.github.senseidragon.dragontweaksv2.advisor.AdvisorTool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.*;

public class ScanAreaTool implements AdvisorTool {

    private static final int DEFAULT_RADIUS = 16;
    private static final int DEFAULT_DEPTH  = 4;
    private static final int MAX_FLOOD_VOLUME = 10_000;
    private static final int AIR_POCKET_THRESHOLD = 50;

    private record VoidRegion(int volume, String direction, int depthBelowPlayer, Set<Long> blockKeys) {}

    @Override
    public String name() { return "scan_area"; }

    @Override
    public JsonObject definition() {
        JsonObject props = new JsonObject();

        JsonObject radius = new JsonObject();
        radius.addProperty("type", "integer");
        radius.addProperty("description", "Block radius to scan from player position. Default: 16.");
        props.add("radius", radius);

        JsonObject depth = new JsonObject();
        depth.addProperty("type", "integer");
        depth.addProperty("description", "Y levels to scan downward from player Y+3. Default: 4 (surface scan).");
        props.add("depth", depth);

        JsonObject passives = new JsonObject();
        passives.addProperty("type", "boolean");
        passives.addProperty("description", "Include passive/animal entities in results. Default: true.");
        props.add("passives", passives);

        JsonObject neutrals = new JsonObject();
        neutrals.addProperty("type", "boolean");
        neutrals.addProperty("description", "Include neutral entities (wolves, endermen, piglins) in results. Default: true.");
        props.add("neutrals", neutrals);

        JsonObject hostiles = new JsonObject();
        hostiles.addProperty("type", "boolean");
        hostiles.addProperty("description", "Include hostile mob entities in results. Default: true.");
        props.add("hostiles", hostiles);

        JsonObject aggro = new JsonObject();
        aggro.addProperty("type", "boolean");
        aggro.addProperty("description", "Include list of entities currently targeting the player. Default: true.");
        props.add("aggro", aggro);

        JsonObject detectOres = new JsonObject();
        detectOres.addProperty("type", "boolean");
        detectOres.addProperty("description", "If true, report ore types found on exposed void surfaces. Default: false.");
        props.add("detectOres", detectOres);

        JsonObject params = new JsonObject();
        params.addProperty("type", "object");
        params.add("properties", props);

        JsonObject fn = new JsonObject();
        fn.addProperty("name", "scan_area");
        fn.addProperty("description",
            "Scans the area around the player. Returns nearby entity counts split by category " +
            "(passives, neutrals, hostiles, aggro-on-player) and underground cave void data. " +
            "Use category flags to request only what is needed.");
        fn.add("parameters", params);

        JsonObject tool = new JsonObject();
        tool.addProperty("type", "function");
        tool.add("function", fn);
        return tool;
    }

    @Override
    public String execute(JsonObject args, ServerPlayer player) {
        try {
            int radius       = args.has("radius")    ? args.get("radius").getAsInt()    : DEFAULT_RADIUS;
            int depth        = args.has("depth")     ? args.get("depth").getAsInt()     : DEFAULT_DEPTH;
            boolean doPass   = !args.has("passives") || args.get("passives").getAsBoolean();
            boolean doNeut   = !args.has("neutrals") || args.get("neutrals").getAsBoolean();
            boolean doHost   = !args.has("hostiles") || args.get("hostiles").getAsBoolean();
            boolean doAggro  = !args.has("aggro")    || args.get("aggro").getAsBoolean();
            boolean ores     = args.has("detectOres") && args.get("detectOres").getAsBoolean();

            BlockPos origin = player.blockPosition();

            List<String> results = new ArrayList<>();
            results.addAll(scanEntities(player, origin, radius, doPass, doNeut, doHost, doAggro));
            results.addAll(scanUnderground(player, origin, radius, depth, ores));

            return results.isEmpty() ? "Nothing notable detected nearby." : String.join("\n", results);
        } catch (Exception e) {
            return "[Tool error: scan unavailable]";
        }
    }

    // ── Surface / entity scan ────────────────────────────────────────────────

    private List<String> scanEntities(ServerPlayer self, BlockPos origin, int radius,
                                       boolean doPass, boolean doNeut, boolean doHost, boolean doAggro) {
        AABB box = AABB.ofSize(self.position(), radius * 2.0, 16.0, radius * 2.0);
        List<Entity> entities = self.serverLevel().getEntities(self, box,
            e -> e != self && e instanceof LivingEntity && !(e instanceof Player));

        Map<String, Integer> passiveCounts  = new LinkedHashMap<>();
        Map<String, Integer> neutralCounts  = new LinkedHashMap<>();
        Map<String, Integer> hostileCounts  = new LinkedHashMap<>();
        List<String> aggroList = new ArrayList<>();

        for (Entity e : entities) {
            LivingEntity le = (LivingEntity) e;
            String typeName = le.getType().getDescription().getString();
            boolean isHostile = le instanceof Monster;
            boolean isNeutral = !isHostile && le instanceof NeutralMob;
            boolean isPassive = !isHostile && !isNeutral;

            if (isHostile) {
                if (doHost) hostileCounts.merge(typeName, 1, Integer::sum);
                if (doAggro && le instanceof Monster m && self.equals(m.getTarget())) {
                    aggroList.add(typeName);
                }
            } else if (isNeutral) {
                if (doNeut) neutralCounts.merge(typeName, 1, Integer::sum);
                if (doAggro && le instanceof NeutralMob nm && nm.isAngryAt(self)) {
                    aggroList.add(typeName);
                }
            } else {
                if (doPass) passiveCounts.merge(typeName, 1, Integer::sum);
            }
        }

        List<String> lines = new ArrayList<>();
        if (doPass && !passiveCounts.isEmpty()) {
            List<String> parts = new ArrayList<>();
            passiveCounts.forEach((n, c) -> parts.add(c + "x " + n));
            lines.add("Passive: " + String.join(", ", parts));
        }
        if (doNeut && !neutralCounts.isEmpty()) {
            List<String> parts = new ArrayList<>();
            neutralCounts.forEach((n, c) -> parts.add(c + "x " + n));
            lines.add("Neutral: " + String.join(", ", parts));
        }
        if (doHost && !hostileCounts.isEmpty()) {
            List<String> parts = new ArrayList<>();
            hostileCounts.forEach((n, c) -> parts.add(c + "x " + n));
            lines.add("Hostile: " + String.join(", ", parts));
        }
        if (doAggro && !aggroList.isEmpty()) {
            lines.add("Targeting you: " + String.join(", ", aggroList));
        }
        return lines;
    }

    // ── Underground block scan ───────────────────────────────────────────────

    private List<String> scanUnderground(ServerPlayer player, BlockPos origin,
                                          int radius, int depth, boolean detectOres) {
        int topY = origin.getY() + 3;
        int botY = topY - depth;
        if (topY <= player.serverLevel().getMinBuildHeight()) return List.of();

        int step = Math.max(1, radius / 8);
        Set<Long> visited = new HashSet<>();
        List<VoidRegion> voids = new ArrayList<>();

        for (int dx = -radius; dx <= radius; dx += step) {
            for (int dz = -radius; dz <= radius; dz += step) {
                for (int dy = topY; dy >= botY; dy--) {
                    BlockPos pos = origin.offset(dx, dy, dz);
                    long key = pos.asLong();
                    if (visited.contains(key)) continue;
                    if (!player.serverLevel().getBlockState(pos).isAir()) continue;
                    VoidRegion region = floodFill(player, pos, visited, origin.getY());
                    if (region.volume() >= AIR_POCKET_THRESHOLD) voids.add(region);
                }
            }
        }

        List<String> lines = new ArrayList<>();
        for (VoidRegion v : voids) {
            lines.add(classifyVoid(v.volume()) + " to the " + v.direction()
                + ", ~" + v.depthBelowPlayer() + " blocks down");
        }

        if (detectOres && !voids.isEmpty()) {
            Map<String, Integer> oreCounts = new HashMap<>();
            for (VoidRegion v : voids) scanOres(player, v.blockKeys(), oreCounts);
            oreCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(4)
                .forEach(e -> lines.add("Ore detected: " + e.getKey()));
        }

        return lines;
    }

    private VoidRegion floodFill(ServerPlayer player, BlockPos start,
                                  Set<Long> globalVisited, int playerY) {
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<Long> regionKeys = new HashSet<>();
        long startKey = start.asLong();
        queue.add(start);
        regionKeys.add(startKey);
        globalVisited.add(startKey);

        while (!queue.isEmpty() && regionKeys.size() < MAX_FLOOD_VOLUME) {
            BlockPos pos = queue.poll();
            for (Direction dir : Direction.values()) {
                BlockPos next = pos.relative(dir);
                long key = next.asLong();
                if (globalVisited.contains(key)) continue;
                if (!player.serverLevel().getBlockState(next).isAir()) continue;
                globalVisited.add(key);
                regionKeys.add(key);
                queue.add(next);
            }
        }

        int depthBelow = Math.max(0, playerY - start.getY());
        String dir = relativeDirection(playerY, start.getX(), start.getZ(),
            player.blockPosition().getX(), player.blockPosition().getZ());
        return new VoidRegion(regionKeys.size(), dir, depthBelow, regionKeys);
    }

    private void scanOres(ServerPlayer player, Set<Long> airKeys,
                           Map<String, Integer> oreCounts) {
        for (long key : airKeys) {
            BlockPos air = BlockPos.of(key);
            for (Direction dir : Direction.values()) {
                BlockState state = player.serverLevel().getBlockState(air.relative(dir));
                String ore = detectOreType(state);
                if (ore != null) oreCounts.merge(ore, 1, Integer::sum);
            }
        }
    }

    private String detectOreType(BlockState state) {
        String id = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
        if (id.contains("coal_ore"))     return "Coal";
        if (id.contains("iron_ore"))     return "Iron";
        if (id.contains("gold_ore"))     return "Gold";
        if (id.contains("diamond_ore"))  return "Diamond";
        if (id.contains("emerald_ore"))  return "Emerald";
        if (id.contains("redstone_ore")) return "Redstone";
        if (id.contains("lapis_ore"))    return "Lapis";
        if (id.contains("copper_ore"))   return "Copper";
        return null;
    }

    private String classifyVoid(int volume) {
        if (volume < 200)  return "Large tunnel";
        if (volume < 500)  return "Small cave";
        if (volume < 1000) return "Dungeon room";
        if (volume < 5000) return "Large cave";
        return "Massive cavern";
    }

    private String relativeDirection(int playerY, int voidX, int voidZ, int originX, int originZ) {
        int dx = voidX - originX;
        int dz = voidZ - originZ;
        if (Math.abs(dx) < 4 && Math.abs(dz) < 4) return "center";
        String ns = dz < 0 ? "north" : "south";
        String ew = dx > 0 ? "east" : "west";
        if (Math.abs(dx) < Math.abs(dz) * 0.5) return ns;
        if (Math.abs(dz) < Math.abs(dx) * 0.5) return ew;
        return ns + ew;
    }
}
