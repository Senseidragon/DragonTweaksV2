package io.github.senseidragon.dragontweaksv2.advisor.tools;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.senseidragon.dragontweaksv2.advisor.AdvisorTool;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class IdentifyNearbyTool implements AdvisorTool {

    private static final int SCAN_RADIUS = 8;
    private static final int SCAN_HEIGHT = 8;

    @Override
    public String name() { return "identify_nearby"; }

    @Override
    public JsonObject definition() {
        JsonObject targetProp = new JsonObject();
        targetProp.addProperty("type", "string");
        targetProp.addProperty("description",
            "What to identify. Can be any block type — e.g. 'spawner', 'oak log', 'flower', 'chest'. " +
            "Leave empty to list all visible blocks nearby.");

        JsonObject properties = new JsonObject();
        properties.add("target", targetProp);

        JsonArray required = new JsonArray();

        JsonObject params = new JsonObject();
        params.addProperty("type", "object");
        params.add("properties", properties);
        params.add("required", required);

        JsonObject fn = new JsonObject();
        fn.addProperty("name", "identify_nearby");
        fn.addProperty("description",
            "Identify specific blocks in the immediate area by name. Use when the player asks what kind or type " +
            "of something is nearby — e.g. what type of spawner, which flowers, what kind of stone. " +
            "scan_area gives overviews; this tool identifies specifics.");
        fn.add("parameters", params);

        JsonObject tool = new JsonObject();
        tool.addProperty("type", "function");
        tool.add("function", fn);
        return tool;
    }

    @Override
    public String execute(JsonObject args, ServerPlayer player) {
        String target = args.has("target") ? args.get("target").getAsString().trim().toLowerCase(Locale.ROOT) : "";

        BlockPos origin = player.blockPosition();
        Vec3 eye = player.getEyePosition();
        ServerLevel level = player.serverLevel();
        Map<String, Integer> found = new TreeMap<>();

        for (int dx = -SCAN_RADIUS; dx <= SCAN_RADIUS; dx++) {
            for (int dz = -SCAN_RADIUS; dz <= SCAN_RADIUS; dz++) {
                for (int dy = -SCAN_HEIGHT; dy <= SCAN_HEIGHT; dy++) {
                    BlockPos pos = origin.offset(dx, dy, dz);
                    BlockState bs = level.getBlockState(pos);
                    if (bs.isAir()) continue;

                    Vec3 tgt = Vec3.atCenterOf(pos);
                    BlockHitResult hit = level.clip(new ClipContext(
                        eye, tgt, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                    if (hit.getType() != HitResult.Type.MISS && !hit.getBlockPos().equals(pos)) continue;

                    String name = enrichBlockEntity(level, pos, bs, BlockUtil.friendlyName(bs));
                    if (name == null) continue;

                    if (!target.isEmpty() && !name.toLowerCase(Locale.ROOT).contains(target)) continue;

                    found.merge(name, 1, Integer::sum);
                }
            }
        }

        if (found.isEmpty()) return target.isEmpty() ? "No blocks visible nearby." : "None found nearby.";

        StringBuilder sb = new StringBuilder("Found nearby: ");
        boolean first = true;
        for (Map.Entry<String, Integer> e : found.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList())) {
            if (!first) sb.append(", ");
            sb.append(e.getKey()).append(" (").append(e.getValue()).append(")");
            first = false;
        }
        return sb.toString();
    }

    private static String enrichBlockEntity(ServerLevel level, BlockPos pos, BlockState bs, String base) {
        if (base == null || !bs.hasBlockEntity()) return base;
        try {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SpawnerBlockEntity spawner) {
                CompoundTag nbt = spawner.saveWithoutMetadata(level.registryAccess());
                String mobId = nbt.getCompound("SpawnData").getCompound("entity").getString("id");
                if (!mobId.isEmpty()) {
                    String segment = mobId.contains(":") ? mobId.substring(mobId.indexOf(':') + 1) : mobId;
                    String mobName = Arrays.stream(segment.split("_"))
                        .filter(w -> !w.isEmpty())
                        .map(w -> Character.toUpperCase(w.charAt(0)) + w.substring(1))
                        .collect(Collectors.joining(" "));
                    return base + " (spawns " + mobName + ")";
                }
            }
        } catch (Exception ignored) {}
        return base;
    }
}
