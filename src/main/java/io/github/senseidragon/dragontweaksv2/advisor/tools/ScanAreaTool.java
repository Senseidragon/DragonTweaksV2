package io.github.senseidragon.dragontweaksv2.advisor.tools;

import com.google.gson.JsonObject;
import io.github.senseidragon.dragontweaksv2.DragonTweaksV2;
import io.github.senseidragon.dragontweaksv2.advisor.AdvisorTool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.Tags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ScanAreaTool implements AdvisorTool {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScanAreaTool.class);

    private static final int DEFAULT_RADIUS    = 16;
    private static final int DEFAULT_DEPTH     = 4;
    private static final int MAX_FLOOD_VOLUME  = 10_000;
    private static final int AIR_POCKET_THRESHOLD = 50;
    private static final int CAVERN_SCAN_RADIUS   = 20;
    private static final int DENSE_RADIUS      = 4;
    private static final int SPARSE_RADIUS     = 8;
    private static final int DENSE_Y_RANGE     = 4;
    private static final int SPARSE_Y_RANGE    = 8;
    // Surface mode scans further downward so terrain on adjacent slopes and in valleys is visible.
    private static final int SURFACE_BELOW_RANGE = 20;

    /** Concept tags: functional blocks detectable by tag. Safe at class-load (plain TagKeys only). */
    private static final Map<String, TagKey<Block>> VANILLA_CONCEPT_TAGS = buildVanillaConceptTags();

    private static Map<String, TagKey<Block>> buildVanillaConceptTags() {
        Map<String, TagKey<Block>> tags = new LinkedHashMap<>();
        tags.put("ladder",          BlockTags.CLIMBABLE);
        tags.put("bed",             BlockTags.BEDS);
        tags.put("door",            BlockTags.DOORS);
        tags.put("stairs",          BlockTags.STAIRS);
        tags.put("crafting table",  Tags.Blocks.PLAYER_WORKSTATIONS_CRAFTING_TABLES);
        tags.put("furnace",         Tags.Blocks.PLAYER_WORKSTATIONS_FURNACES);
        tags.put("chest",           Tags.Blocks.CHESTS);
        tags.put("barrel",          Tags.Blocks.BARRELS);
        return tags;
    }

    private Map<String, TagKey<Block>> conceptTags() {
        Map<String, TagKey<Block>> tags = new LinkedHashMap<>(VANILLA_CONCEPT_TAGS);
        if (ModList.get().isLoaded("projecte")) {
            tags.put("transmutation table", BlockTags.create(
                ResourceLocation.fromNamespaceAndPath(DragonTweaksV2.MODID, "transmutation_table")));
        }
        return tags;
    }

    private record VoidRegion(int volume, String direction, int depthBelowPlayer, Set<Long> blockKeys, int skyExits) {}

    private static class BlockDistribution {
        final Map<String, Integer> ground  = new LinkedHashMap<>();
        final Map<String, Integer> above   = new LinkedHashMap<>();
        final Map<String, Integer> visible = new LinkedHashMap<>(); // underground flat bucket
        final Map<String, Integer> fluids  = new LinkedHashMap<>();
        final List<String> lightSources    = new ArrayList<>();
        void addGround(String cat)         { ground.merge(cat, 1, Integer::sum); }
        void addAbove(String cat)          { above.merge(cat, 1, Integer::sum); }
        void addVisible(String cat)        { visible.merge(cat, 1, Integer::sum); }
        void addFluid(String cat)          { fluids.merge(cat, 1, Integer::sum); }
        void addLightSource(String note)   { lightSources.add(note); }
    }

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
            "Scans the visible area around the player. Returns what the player can actually see: " +
            "lighting conditions (ambient level and light sources), terrain and block composition, " +
            "nearby entities (passives, neutrals, hostiles, aggro-on-player), cave morphology when " +
            "underground, and notable fixtures within reach (ladder, bed, chest, door). " +
            "Use this tool when asked about surroundings, what is nearby, or what can be seen. " +
            "Use category flags to limit results to what is needed.");
        fn.add("parameters", params);

        JsonObject tool = new JsonObject();
        tool.addProperty("type", "function");
        tool.add("function", fn);
        return tool;
    }

    @Override
    public String execute(JsonObject args, ServerPlayer player) {
        try {
            int radius      = args.has("radius")    ? args.get("radius").getAsInt()    : DEFAULT_RADIUS;
            int depth       = args.has("depth")     ? args.get("depth").getAsInt()     : DEFAULT_DEPTH;
            boolean doPass  = !args.has("passives") || args.get("passives").getAsBoolean();
            boolean doNeut  = !args.has("neutrals") || args.get("neutrals").getAsBoolean();
            boolean doHost  = !args.has("hostiles") || args.get("hostiles").getAsBoolean();
            boolean doAggro = !args.has("aggro")    || args.get("aggro").getAsBoolean();

            BlockPos origin = player.blockPosition();
            boolean underground = player.serverLevel()
                .getBrightness(net.minecraft.world.level.LightLayer.SKY, origin) <= 0;

            // Blind players see nothing; players at light level 0 see nothing
            boolean blind = player.hasEffect(net.minecraft.world.effect.MobEffects.BLINDNESS);
            int lightLevel = player.serverLevel().getMaxLocalRawBrightness(origin);
            boolean canSee = !blind && lightLevel > 0;

            // Sky line: if origin appears underground, BFS through connected air to see if any
            // nearby cell has sky light — handles cave-entrance positions where stone/water
            // overhead blocks sky at the player's exact block but sky is visible nearby.
            String skyText;
            if (!underground) {
                skyText = "open";
            } else if (checkNearbySkyAccess(player, origin)) {
                skyText = "sky visible";
            } else {
                skyText = "enclosed";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Sky: ").append(skyText).append("\n");
            if (player.isInLava()) {
                sb.append("In fluid: lava\n");
            } else if (player.isUnderWater()) {
                sb.append("In fluid: water (submerged)\n");
            } else if (player.isInWater()) {
                sb.append("In fluid: water\n");
            }
            if (!canSee) {
                sb.append("Visibility: none (").append(blind ? "blinded" : "light level 0").append(")");
                return sb.toString().trim();
            }

            // Block distribution scan
            BlockDistribution dist = scanBlockDistribution(player, origin, underground);

            // Lighting summary — ambient bucket + detected light sources
            // Name sky as the primary source when it's accessible so the model doesn't
            // credit a single glow-lichen block for a well-lit open ravine.
            String lightSummary = summarizeLightSources(dist.lightSources);
            String skySource = skyText.equals("enclosed") ? "" : "sky";
            String fullLightSummary = skySource.isEmpty() ? lightSummary
                : lightSummary.isEmpty() ? skySource : skySource + ", " + lightSummary;
            sb.append("Lighting: ").append(lightBucket(lightLevel));
            if (!fullLightSummary.isEmpty()) sb.append(" — ").append(fullLightSummary);
            sb.append("\n");

            if (!dist.ground.isEmpty())
                sb.append("Ground cover: ").append(formatCounts(dist.ground)).append("\n");
            if (!dist.above.isEmpty())
                sb.append("Above surface: ").append(formatCounts(dist.above)).append("\n");
            if (!dist.visible.isEmpty())
                sb.append("Visible blocks: ").append(formatCounts(dist.visible)).append("\n");
            if (!dist.fluids.isEmpty())
                sb.append("Fluids: ").append(formatCounts(dist.fluids)).append("\n");

            // Concept scan (ladders, beds, chests, etc.)
            String concepts = buildConceptLine(player, origin);
            if (concepts != null) sb.append(concepts).append("\n");

            // Underground: cave morphology + ores
            if (underground) {
                List<String> caveLines = scanUnderground(player, origin, radius, depth, true);
                caveLines.forEach(l -> sb.append(l).append("\n"));
            }

            // Entities
            List<String> entityLines = scanEntities(player, origin, radius, doPass, doNeut, doHost, doAggro);
            if (entityLines.isEmpty()) {
                sb.append("Entities: none");
            } else {
                entityLines.forEach(l -> sb.append(l).append("\n"));
            }

            return sb.toString().trim();
        } catch (Exception e) {
            return "[Tool error: scan unavailable]";
        }
    }

    // ── Block distribution scan ─────────────────────────────────────────────

    private BlockDistribution scanBlockDistribution(ServerPlayer player, BlockPos origin, boolean underground) {
        BlockDistribution dist = new BlockDistribution();
        var level = player.serverLevel();
        int px = origin.getX(), py = origin.getY(), pz = origin.getZ();
        Vec3 eye = player.getEyePosition();

        // Surface bucketing only applies outdoors; underground uses flat visible bucket
        Map<Long, Integer> surfaceY = new HashMap<>();
        if (!underground) {
            for (int dx = -SPARSE_RADIUS; dx <= SPARSE_RADIUS; dx++) {
                for (int dz = -SPARSE_RADIUS; dz <= SPARSE_RADIUS; dz++) {
                    int topY = py + SPARSE_Y_RANGE;
                    int botY = py - SURFACE_BELOW_RANGE;
                    for (int y = topY; y >= botY; y--) {
                        BlockPos p = new BlockPos(px + dx, y, pz + dz);
                        BlockState bs = level.getBlockState(p);
                        if (bs.isAir() || !level.getFluidState(p).isEmpty()) continue;
                        // Pierce through tree canopy, thin snow layers, and vines so trunks/leaves
                        // above actual ground count as above-surface, not silently dropped.
                        // Thin snow layers (Blocks.SNOW) accumulate on canopy tops in snowy
                        // biomes and would otherwise masquerade as the terrain surface.
                        // Full snow blocks are genuine terrain and stop the scan.
                        // Vines (Blocks.VINE) hang from jungle canopy and occupy column positions
                        // above tree trunks — same masquerade risk as snow in snowy biomes.
                        Block pierceBlock = bs.getBlock();
                        if (pierceBlock == net.minecraft.world.level.block.Blocks.SNOW) continue;
                        if (pierceBlock == net.minecraft.world.level.block.Blocks.VINE) continue;
                        String cat = categorizeBlock(bs, level, p);
                        if ("tree_leaves".equals(cat) || "tree_log".equals(cat)) continue;
                        surfaceY.put(columnKey(px + dx, pz + dz), y);
                        break;
                    }
                }
            }
        }

        // Surface mode scans further below the player to catch terrain on lower slopes.
        int denseBelow  = underground ? DENSE_Y_RANGE  : SURFACE_BELOW_RANGE;
        int sparseBelow = underground ? SPARSE_Y_RANGE : SURFACE_BELOW_RANGE;

        // Dense zone: radius 0-4, every block
        for (int dx = -DENSE_RADIUS; dx <= DENSE_RADIUS; dx++) {
            for (int dz = -DENSE_RADIUS; dz <= DENSE_RADIUS; dz++) {
                for (int dy = -denseBelow; dy <= DENSE_Y_RANGE; dy++) {
                    samplePos(level, dist, px+dx, py+dy, pz+dz, surfaceY, eye, player, underground);
                }
            }
        }

        // Sparse zone: radius 5-8, every other block
        for (int dx = -SPARSE_RADIUS; dx <= SPARSE_RADIUS; dx++) {
            for (int dz = -SPARSE_RADIUS; dz <= SPARSE_RADIUS; dz++) {
                int adx = Math.abs(dx), adz = Math.abs(dz);
                if (adx <= DENSE_RADIUS && adz <= DENSE_RADIUS) continue;
                if ((adx + adz) % 2 != 0) continue;
                for (int dy = -sparseBelow; dy <= SPARSE_Y_RANGE; dy++) {
                    samplePos(level, dist, px+dx, py+dy, pz+dz, surfaceY, eye, player, underground);
                }
            }
        }

        return dist;
    }

    private long columnKey(int x, int z) { return ((long) x << 32) | (z & 0xFFFFFFFFL); }

    private void samplePos(net.minecraft.server.level.ServerLevel level, BlockDistribution dist,
                            int x, int y, int z, Map<Long, Integer> surfaceY, Vec3 eye,
                            ServerPlayer player, boolean underground) {
        BlockPos pos = new BlockPos(x, y, z);

        BlockState bs = level.getBlockState(pos);
        if (bs.isAir()) return;

        FluidState fs = level.getFluidState(pos);

        // Occlusion: COLLIDER passes through non-solid blocks and fluids (no collision shape).
        // Skip the target only if a solid block intercepts the path before reaching it.
        // Non-solid targets (flowers, torches, vines, sugar cane) return MISS — the ray passes
        // through — which means reachable, not invisible. No block-specific bypass needed.
        Vec3 tgt = Vec3.atCenterOf(pos);
        BlockHitResult hit = level.clip(new net.minecraft.world.level.ClipContext(eye, tgt,
            net.minecraft.world.level.ClipContext.Block.COLLIDER,
            net.minecraft.world.level.ClipContext.Fluid.NONE, player));
        if (hit.getType() != HitResult.Type.MISS && !hit.getBlockPos().equals(pos)) return;

        if (!fs.isEmpty()) {
            boolean isWater = fs.getType() == Fluids.WATER || fs.getType() == Fluids.FLOWING_WATER;
            boolean isLava  = fs.getType() == Fluids.LAVA  || fs.getType() == Fluids.FLOWING_LAVA;
            if (isWater) dist.addFluid(fs.isSource() ? "still_water" : "flowing_water");
            if (isLava) {
                String lavaType = fs.isSource() ? "still_lava" : "flowing_lava";
                dist.addFluid(lavaType);
                LOGGER.info("[ScanArea] {} at ({},{},{})", lavaType, x, y, z);
                dist.addLightSource("lava (" + verticalRelation(y, eye.y) + ")");
            }
            return;
        }

        String cat = categorizeBlock(bs, level, pos);
        if (cat == null) return;
        if (cat.equals("light_source")) {
            dist.addLightSource(lightSourceType(bs) + " (" + verticalRelation(y, eye.y) + ")");
            return;
        }
        if (cat.startsWith("ore_")) LOGGER.info("[ScanArea] {} at ({},{},{})", cat, x, y, z);

        if (underground) {
            dist.addVisible(cat);
        } else {
            Integer surf = surfaceY.get(columnKey(x, z));
            if (surf != null && y == surf) dist.addGround(cat);
            else if (surf == null || y > surf) dist.addAbove(cat);
        }
    }

    private String categorizeBlock(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos) {
        // Ores first to avoid misclassifying as stone
        if (state.is(Tags.Blocks.ORES_COAL))     return "ore_coal";
        if (state.is(Tags.Blocks.ORES_IRON))     return "ore_iron";
        if (state.is(Tags.Blocks.ORES_GOLD))     return "ore_gold";
        if (state.is(Tags.Blocks.ORES_DIAMOND))  return "ore_diamond";
        if (state.is(Tags.Blocks.ORES_EMERALD))  return "ore_emerald";
        if (state.is(Tags.Blocks.ORES_REDSTONE)) return "ore_redstone";
        if (state.is(Tags.Blocks.ORES_LAPIS))    return "ore_lapis";
        if (state.is(Tags.Blocks.ORES_COPPER))   return "ore_copper";
        // Terrain
        var b = state.getBlock();
        if (b == net.minecraft.world.level.block.Blocks.GRASS_BLOCK
            || b == net.minecraft.world.level.block.Blocks.PODZOL
            || b == net.minecraft.world.level.block.Blocks.MYCELIUM) return "grass";
        if (state.is(BlockTags.DIRT))             return "dirt";
        if (state.is(BlockTags.SAND))             return "sand";
        if (b == net.minecraft.world.level.block.Blocks.GRAVEL) return "gravel";
        if (state.is(BlockTags.ICE))              return "ice";
        if (state.is(BlockTags.SNOW))             return "snow";
        if (b == net.minecraft.world.level.block.Blocks.MUD
            || b == net.minecraft.world.level.block.Blocks.MUDDY_MANGROVE_ROOTS) return "mud";
        if (state.is(BlockTags.STONE_ORE_REPLACEABLES)
            || b == net.minecraft.world.level.block.Blocks.COBBLESTONE
            || b == net.minecraft.world.level.block.Blocks.STONE) return "stone";
        // Vegetation / structure
        if (state.is(BlockTags.LOGS))             return "tree_log";
        if (state.is(BlockTags.LEAVES))           return "tree_leaves";
        if (state.is(BlockTags.PLANKS))           return "planks";
        if (state.is(BlockTags.STONE_BRICKS))     return "stone_brick";
        if (state.is(BlockTags.CROPS))            return "crops";
        if (state.is(BlockTags.FLOWERS))          return "flower";
        // Light sources (torches, lanterns, glowstone, etc.) — checked last
        if (state.getLightEmission(level, pos) > 0) return "light_source";
        // Fallback: return the block's in-game display name so no solid block is silently dropped.
        // Named categories above aggregate variants (e.g. all log types → "tree_log");
        // unknown blocks fall back to their display name (e.g. "Packed Ice", "Calcite").
        String displayName = state.getBlock().getName().getString();
        return displayName.isBlank() ? null : displayName;
    }

    private String formatCounts(Map<String, Integer> counts) {
        if (counts.isEmpty()) return "none";
        return counts.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .map(e -> quantify(e.getValue()) + e.getKey())
            .collect(java.util.stream.Collectors.joining(", "));
    }

    static String quantify(int n) {
        if (n >= 21) return "a lot of ";
        if (n >= 6)  return "some ";
        return "a little ";
    }

    static String lightBucket(int level) {
        if (level == 0)  return "dark";
        if (level <= 3)  return "dim";
        if (level <= 6)  return "low";
        if (level <= 10) return "moderate";
        if (level <= 13) return "well-lit";
        return "bright";
    }

    private String verticalRelation(int y, double eyeY) {
        if (y > eyeY + 1.5) return "above";
        if (y < eyeY - 1.5) return "below";
        return "at level";
    }

    private String lightSourceType(BlockState bs) {
        Block b = bs.getBlock();
        if (b == net.minecraft.world.level.block.Blocks.TORCH
                || b == net.minecraft.world.level.block.Blocks.WALL_TORCH) return "torch";
        if (b == net.minecraft.world.level.block.Blocks.SOUL_TORCH
                || b == net.minecraft.world.level.block.Blocks.SOUL_WALL_TORCH) return "soul torch";
        if (b == net.minecraft.world.level.block.Blocks.REDSTONE_TORCH
                || b == net.minecraft.world.level.block.Blocks.REDSTONE_WALL_TORCH) return "redstone torch";
        if (b == net.minecraft.world.level.block.Blocks.GLOW_LICHEN) return "glow lichen";
        if (b == net.minecraft.world.level.block.Blocks.GLOWSTONE) return "glowstone";
        if (b == net.minecraft.world.level.block.Blocks.SEA_LANTERN) return "sea lantern";
        if (b == net.minecraft.world.level.block.Blocks.MAGMA_BLOCK) return "magma";
        if (b == net.minecraft.world.level.block.Blocks.REDSTONE_LAMP) return "redstone lamp";
        if (b == net.minecraft.world.level.block.Blocks.LANTERN
                || b == net.minecraft.world.level.block.Blocks.SOUL_LANTERN) return "lantern";
        return "light source";
    }

    static String summarizeLightSources(List<String> sources) {
        if (sources.isEmpty()) return "";
        // Group by type; collect distinct vertical positions per type
        Map<String, Integer> countByType     = new LinkedHashMap<>();
        Map<String, Set<String>> posByType   = new LinkedHashMap<>();
        for (String entry : sources) {
            int p = entry.lastIndexOf(" (");
            if (p < 0) continue;
            String type = entry.substring(0, p);
            String vert = entry.substring(p + 2, entry.length() - 1);
            countByType.merge(type, 1, Integer::sum);
            posByType.computeIfAbsent(type, k -> new LinkedHashSet<>()).add(vert);
        }
        // Format: "torch x3 (above, at level); glow lichen x1 (below)"
        List<String> parts = new ArrayList<>();
        countByType.forEach((type, count) -> {
            String pos = String.join(", ", posByType.get(type));
            parts.add(type + " x" + count + " (" + pos + ")");
        });
        return String.join("; ", parts);
    }

    private String buildConceptLine(ServerPlayer player, BlockPos origin) {
        Set<Long> reachable = scanReachableAirSpace(player, origin);
        Map<String, TagKey<Block>> tags = conceptTags();
        Set<String> found = new LinkedHashSet<>();
        for (long key : reachable) {
            BlockPos air = BlockPos.of(key);
            for (Direction dir : Direction.values()) {
                BlockState neighbor = player.serverLevel().getBlockState(air.relative(dir));
                tags.forEach((name, tag) -> { if (neighbor.is(tag)) found.add(name); });
            }
        }
        return found.isEmpty() ? null : "Within reach: " + String.join(", ", found);
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
        int topY   = origin.getY() + 3;
        int botY   = origin.getY() - 10;
        int probeY = origin.getY() - 20; // probe through solid down to 20 blocks below player
        if (topY <= player.serverLevel().getMinBuildHeight()) return List.of();

        int step = Math.max(1, radius / 8);
        Set<Long> visited = new HashSet<>();
        List<VoidRegion> voids = new ArrayList<>();
        List<BlockPos> probeAirCells = new ArrayList<>();

        for (int dx = -radius; dx <= radius; dx += step) {
            for (int dz = -radius; dz <= radius; dz += step) {
                // Standard scan: find air blocks top-down within range
                for (int dy = topY; dy >= botY; dy--) {
                    BlockPos pos = origin.offset(dx, dy, dz);
                    long key = pos.asLong();
                    if (visited.contains(key)) continue;
                    if (!player.serverLevel().getBlockState(pos).isAir()) continue;
                    VoidRegion region = floodFill(player, pos, visited, origin);
                    if (region.volume() >= AIR_POCKET_THRESHOLD) voids.add(region);
                }
                // Downward probe: penetrate solid blocks below normal scan range to find hidden air.
                // Only truly solid (non-fluid) blocks set hitSolid — water above an entrance air
                // cell is not the same as stone above a cave pocket.
                boolean hitSolid = false;
                for (int dy = botY - 1; dy >= probeY; dy--) {
                    BlockPos pos = origin.offset(dx, dy, dz);
                    long key = pos.asLong();
                    if (visited.contains(key)) continue;
                    BlockState probeState = player.serverLevel().getBlockState(pos);
                    if (!probeState.isAir()) {
                        if (player.serverLevel().getFluidState(pos).isEmpty()) hitSolid = true;
                        continue;
                    }
                    if (!hitSolid) continue;
                    // Skip cells with any sky-light access — they're connected to the surface
                    // (through an entrance, ravine, or crack) and are not sealed cave pockets.
                    if (player.serverLevel().getBrightness(net.minecraft.world.level.LightLayer.SKY, pos) > 0) continue;
                    probeAirCells.add(pos);
                    VoidRegion region = floodFill(player, pos, visited, origin);
                    if (region.volume() >= AIR_POCKET_THRESHOLD) voids.add(region);
                }
            }
        }

        List<String> lines = new ArrayList<>();
        for (VoidRegion v : voids) {
            String exitNote = v.skyExits() == 0 ? ""
                : v.skyExits() == 1 ? ", 1 apparent exit to surface"
                : ", " + v.skyExits() + " apparent exits to surface";
            lines.add("Cave: " + classifyVoid(v.volume()) + " (~" + v.volume() + " blocks)"
                + exitNote);
        }

        if (detectOres && !voids.isEmpty()) {
            Map<String, Integer> oreCounts = new HashMap<>();
            for (VoidRegion v : voids) scanOres(player, v.blockKeys(), oreCounts);
            if (!oreCounts.isEmpty()) {
                StringBuilder sb = new StringBuilder("Ores:");
                oreCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(6)
                    .forEach(e -> sb.append(' ').append(quantify(e.getValue())).append(e.getKey()));
                lines.add(sb.toString());
            }
        }

        // For each probe air cell below the floor, run a distribution scan from that vantage point.
        // Skips blindness/light gates — this is sensing, not seeing.
        if (!probeAirCells.isEmpty()) {
            BlockDistribution probeDist = new BlockDistribution();
            Set<BlockPos> scannedProbeOrigins = new HashSet<>();
            for (BlockPos cell : probeAirCells) {
                // Deduplicate by rough proximity — one scan per 4-block cluster
                boolean tooClose = false;
                for (BlockPos scanned : scannedProbeOrigins) {
                    if (Math.abs(cell.getX() - scanned.getX()) <= 4
                        && Math.abs(cell.getY() - scanned.getY()) <= 4
                        && Math.abs(cell.getZ() - scanned.getZ()) <= 4) {
                        tooClose = true; break;
                    }
                }
                if (tooClose) continue;
                scannedProbeOrigins.add(cell);
                Vec3 probeEye = Vec3.atCenterOf(cell);
                Map<Long, Integer> emptySurfaceY = new HashMap<>();
                // Only scan downward from probe cell — do not look up into the cave above
                for (int dx = -DENSE_RADIUS; dx <= DENSE_RADIUS; dx++)
                    for (int dz = -DENSE_RADIUS; dz <= DENSE_RADIUS; dz++)
                        for (int dy = -DENSE_Y_RANGE; dy <= 0; dy++)
                            samplePos(player.serverLevel(), probeDist,
                                cell.getX()+dx, cell.getY()+dy, cell.getZ()+dz,
                                emptySurfaceY, probeEye, player, true);
            }
            if (!probeDist.visible.isEmpty())
                lines.add("Below floor: " + formatCounts(probeDist.visible));
            if (!probeDist.fluids.isEmpty())
                lines.add("Below floor fluids: " + formatCounts(probeDist.fluids));
        }

        return lines;
    }

    /** BFS through air, stopped by solid blocks. Bounded to CAVERN_SCAN_RADIUS around the player
     *  (not the seed) so one connected void can't be chopped into repeated reports by MAX_FLOOD_VOLUME.
     *  A cell with direct sky line-of-sight is excluded from the region and not expanded past -- this
     *  invalidates only that cell, not the whole region, so a real cave with a surface opening still
     *  gets reported. Each such cell increments skyExits, surfaced later as an "apparent exit" note. */
    private VoidRegion floodFill(ServerPlayer player, BlockPos start,
                                  Set<Long> globalVisited, BlockPos playerOrigin) {
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<Long> regionKeys = new HashSet<>();
        int skyExits = 0;
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
                if (!withinRadius(playerOrigin, next, CAVERN_SCAN_RADIUS)) continue;
                if (player.serverLevel().getBrightness(net.minecraft.world.level.LightLayer.SKY, next) > 0) {
                    skyExits++;
                    continue;
                }
                regionKeys.add(key);
                queue.add(next);
            }
        }

        int playerY = playerOrigin.getY();
        int depthBelow = Math.max(0, playerY - start.getY());
        String dir = relativeDirection(playerY, start.getX(), start.getZ(),
            player.blockPosition().getX(), player.blockPosition().getZ());
        return new VoidRegion(regionKeys.size(), dir, depthBelow, regionKeys, skyExits);
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
        if (state.is(Tags.Blocks.ORES_COAL))     return "Coal";
        if (state.is(Tags.Blocks.ORES_IRON))     return "Iron";
        if (state.is(Tags.Blocks.ORES_GOLD))     return "Gold";
        if (state.is(Tags.Blocks.ORES_DIAMOND))  return "Diamond";
        if (state.is(Tags.Blocks.ORES_EMERALD))  return "Emerald";
        if (state.is(Tags.Blocks.ORES_REDSTONE)) return "Redstone";
        if (state.is(Tags.Blocks.ORES_LAPIS))    return "Lapis";
        if (state.is(Tags.Blocks.ORES_COPPER))   return "Copper";
        return null;
    }

    /** BFS from origin through connected air; returns true if any reachable air cell has sky light.
     *  Used to distinguish "enclosed cave" from "cave entrance with sky visible nearby." */
    private boolean checkNearbySkyAccess(ServerPlayer player, BlockPos origin) {
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<Long> visited = new HashSet<>();
        queue.add(origin);
        visited.add(origin.asLong());
        while (!queue.isEmpty() && visited.size() < MAX_FLOOD_VOLUME) {
            BlockPos pos = queue.poll();
            for (Direction dir : Direction.values()) {
                BlockPos next = pos.relative(dir);
                long key = next.asLong();
                if (visited.contains(key)) continue;
                if (!withinRadius(origin, next, CAVERN_SCAN_RADIUS)) continue;
                visited.add(key);
                if (!player.serverLevel().getBlockState(next).isAir()) continue;
                if (player.serverLevel().getBrightness(net.minecraft.world.level.LightLayer.SKY, next) > 0)
                    return true;
                queue.add(next);
            }
        }
        return false;
    }

    /** BFS through air, stopped by solid blocks, bounded by SPARSE_RADIUS in every axis. */
    private Set<Long> scanReachableAirSpace(ServerPlayer player, BlockPos start) {
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<Long> visited = new HashSet<>();
        queue.add(start);
        visited.add(start.asLong());

        while (!queue.isEmpty() && visited.size() < MAX_FLOOD_VOLUME) {
            BlockPos pos = queue.poll();
            for (Direction dir : Direction.values()) {
                BlockPos next = pos.relative(dir);
                long key = next.asLong();
                if (visited.contains(key)) continue;
                if (!withinRadius(start, next, SPARSE_RADIUS)) continue;
                if (!player.serverLevel().getBlockState(next).isAir()) continue;
                visited.add(key);
                queue.add(next);
            }
        }
        return visited;
    }

    private boolean withinRadius(BlockPos center, BlockPos pos, int radius) {
        return Math.abs(pos.getX() - center.getX()) <= radius
            && Math.abs(pos.getY() - center.getY()) <= radius
            && Math.abs(pos.getZ() - center.getZ()) <= radius;
    }

    // package-private for testing
    String classifyVoid(int volume) {
        if (volume < 200)  return "Large tunnel";
        if (volume < 500)  return "Small cave";
        if (volume < 1000) return "Dungeon room";
        return "Large cave";
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
