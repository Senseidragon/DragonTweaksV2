package io.github.senseidragon.dragontweaksv2.advisor.tools;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.senseidragon.dragontweaksv2.advisor.AdvisorTool;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class IdentifyNearbyTool implements AdvisorTool {

    private static final int SCAN_RADIUS = 8;
    private static final int SCAN_HEIGHT = 8;

    // Ordered: first match wins on partial lookup. Keys are singular and lowercase.
    private static final LinkedHashMap<String, Predicate<BlockState>> TARGETS = new LinkedHashMap<>();
    static {
        TARGETS.put("log",  bs -> bs.is(BlockTags.LOGS));
        TARGETS.put("wood", bs -> bs.is(BlockTags.LOGS));
        TARGETS.put("stone", bs ->
            bs.is(BlockTags.BASE_STONE_OVERWORLD)
            || bs.is(Blocks.COBBLESTONE) || bs.is(Blocks.MOSSY_COBBLESTONE)
            || bs.is(Blocks.COBBLED_DEEPSLATE)
            || bs.is(Blocks.POLISHED_GRANITE) || bs.is(Blocks.POLISHED_DIORITE) || bs.is(Blocks.POLISHED_ANDESITE)
            || bs.is(Blocks.POLISHED_DEEPSLATE)
            || bs.is(Blocks.STONE_BRICKS) || bs.is(Blocks.MOSSY_STONE_BRICKS)
            || bs.is(Blocks.CRACKED_STONE_BRICKS) || bs.is(Blocks.CHISELED_STONE_BRICKS)
            || bs.is(Blocks.BLACKSTONE) || bs.is(Blocks.POLISHED_BLACKSTONE)
            || bs.is(Blocks.BASALT) || bs.is(Blocks.SMOOTH_BASALT));
        TARGETS.put("flower",          bs -> bs.is(BlockTags.FLOWERS));
        TARGETS.put("ore",             bs -> bs.is(Tags.Blocks.ORES));
        TARGETS.put("crafting station",bs ->
            bs.is(Blocks.CRAFTING_TABLE)
            || bs.is(Blocks.ANVIL) || bs.is(Blocks.CHIPPED_ANVIL) || bs.is(Blocks.DAMAGED_ANVIL)
            || bs.is(Blocks.GRINDSTONE) || bs.is(Blocks.CARTOGRAPHY_TABLE) || bs.is(Blocks.LOOM)
            || bs.is(Blocks.STONECUTTER) || bs.is(Blocks.FLETCHING_TABLE) || bs.is(Blocks.SMITHING_TABLE)
            || bs.is(Blocks.ENCHANTING_TABLE) || bs.is(Blocks.BREWING_STAND));
        TARGETS.put("furnace",         bs ->
            bs.is(Blocks.FURNACE) || bs.is(Blocks.SMOKER) || bs.is(Blocks.BLAST_FURNACE));
        TARGETS.put("campfire",        bs ->
            bs.is(Blocks.CAMPFIRE) || bs.is(Blocks.SOUL_CAMPFIRE));
        TARGETS.put("lily pad",        bs -> bs.is(Blocks.LILY_PAD));
        TARGETS.put("mushroom",        bs ->
            bs.is(Blocks.RED_MUSHROOM) || bs.is(Blocks.BROWN_MUSHROOM)
            || bs.is(Blocks.RED_MUSHROOM_BLOCK) || bs.is(Blocks.BROWN_MUSHROOM_BLOCK)
            || bs.is(Blocks.MUSHROOM_STEM));
    }

    @Override
    public String name() { return "identify_nearby"; }

    @Override
    public JsonObject definition() {
        JsonObject targetProp = new JsonObject();
        targetProp.addProperty("type", "string");
        targetProp.addProperty("description",
            "What to identify. Examples: logs, stone, flowers, ores, crafting stations, furnaces, campfires, lily pads, mushrooms.");

        JsonObject properties = new JsonObject();
        properties.add("target", targetProp);

        JsonArray required = new JsonArray();
        required.add("target");

        JsonObject params = new JsonObject();
        params.addProperty("type", "object");
        params.add("properties", properties);
        params.add("required", required);

        JsonObject fn = new JsonObject();
        fn.addProperty("name", "identify_nearby");
        fn.addProperty("description",
            "Identify specific block types in the immediate area. Use when the player asks what kind of something is nearby — " +
            "e.g. what type of log, which stone, what flowers. Only call this when specific identification is needed; " +
            "scan_area handles general overviews.");
        fn.add("parameters", params);

        JsonObject tool = new JsonObject();
        tool.addProperty("type", "function");
        tool.add("function", fn);
        return tool;
    }

    @Override
    public String execute(JsonObject args, ServerPlayer player) {
        String raw = args.has("target") ? args.get("target").getAsString().trim().toLowerCase(Locale.ROOT) : "";
        if (raw.isEmpty()) return "No target specified.";

        // Simple singularization: strip trailing 's' (handles "logs", "stones", "lily pads", etc.)
        String normalized = (raw.endsWith("s") && raw.length() > 1)
            ? raw.substring(0, raw.length() - 1) : raw;

        // Exact match first, then partial (key contains normalized or vice-versa)
        Predicate<BlockState> matcher = TARGETS.get(normalized);
        if (matcher == null) {
            for (Map.Entry<String, Predicate<BlockState>> e : TARGETS.entrySet()) {
                if (normalized.contains(e.getKey()) || e.getKey().contains(normalized)) {
                    matcher = e.getValue();
                    break;
                }
            }
        }
        if (matcher == null) {
            String known = TARGETS.keySet().stream()
                .filter(k -> !k.equals("wood")) // suppress "wood" alias from the hint — "logs" is the canonical name
                .map(k -> k + "s")
                .collect(Collectors.joining(", "));
            return "I don't know how to search for \"" + raw + "\". Recognized: " + known + ".";
        }

        BlockPos origin = player.blockPosition();
        Vec3 eye = player.getEyePosition();
        Map<String, Integer> found = new TreeMap<>();
        final Predicate<BlockState> finalMatcher = matcher;

        for (int dx = -SCAN_RADIUS; dx <= SCAN_RADIUS; dx++) {
            for (int dz = -SCAN_RADIUS; dz <= SCAN_RADIUS; dz++) {
                for (int dy = -SCAN_HEIGHT; dy <= SCAN_HEIGHT; dy++) {
                    BlockPos pos = origin.offset(dx, dy, dz);
                    BlockState bs = player.serverLevel().getBlockState(pos);
                    if (bs.isAir() || !finalMatcher.test(bs)) continue;

                    // Occlusion: COLLIDER+NONE passes through non-solid blocks.
                    // Skip only if a solid block intercepts the path before the target.
                    Vec3 tgt = Vec3.atCenterOf(pos);
                    BlockHitResult hit = player.serverLevel().clip(new ClipContext(
                        eye, tgt, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                    if (hit.getType() != HitResult.Type.MISS && !hit.getBlockPos().equals(pos)) continue;

                    found.merge(bs.getBlock().getName().getString(), 1, Integer::sum);
                }
            }
        }

        if (found.isEmpty()) return "None found nearby.";

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
}
