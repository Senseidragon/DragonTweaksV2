package io.github.senseidragon.dragontweaksv2.advisor;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class EnvironmentContextBuilder {

    private EnvironmentContextBuilder() {}

    public static String build(ServerPlayer player, ServerLevel level) {
        BlockPos pos = player.blockPosition();
        StringBuilder sb = new StringBuilder();

        var mainHand = player.getMainHandItem();
        var offHand = player.getOffhandItem();
        if (!mainHand.isEmpty())
            sb.append("Main hand: ").append(mainHand.getHoverName().getString()).append(". ");
        if (!offHand.isEmpty())
            sb.append("Off hand: ").append(offHand.getHoverName().getString()).append(". ");

        String hotbar = hotbarItems(player);
        sb.append(hotbar.isEmpty() ? "Hotbar: empty. " : "Hotbar: " + hotbar + ". ");

        sb.append("Time: ").append(timeOfDay(level.getDayTime())).append(". ");
        sb.append("Weather: ").append(weather(level)).append(". ");
        sb.append("Location: Y=").append(pos.getY()).append(", ").append(surroundings(level, pos)).append(". ");

        String animals = nearbyAnimals(level, pos);
        sb.append(animals.isEmpty() ? "No animals nearby. " : "Nearby animals: " + animals + ". ");

        String threats = nearbyThreats(level, pos);
        sb.append(threats.isEmpty() ? "No threats nearby. " : "Nearby threats: " + threats + ". ");

        String biome = biomeName(level, pos);
        if (!biome.isEmpty()) sb.append("Biome: ").append(biome).append(".");

        return sb.toString().trim();
    }

    private static String timeOfDay(long dayTime) {
        long t = dayTime % 24000;
        if (t < 1000) return "dawn";
        if (t < 6000) return "morning";
        if (t < 12000) return "midday";
        if (t < 13000) return "dusk";
        if (t < 18000) return "night";
        if (t < 20000) return "midnight";
        return "pre-dawn";
    }

    private static String weather(ServerLevel level) {
        if (level.isThundering()) return "thunderstorm";
        if (level.isRaining()) return "raining";
        return "clear";
    }

    private static String surroundings(ServerLevel level, BlockPos pos) {
        if (level.canSeeSky(pos)) return "open sky";
        if (pos.getY() > 50) return "sheltered";
        if (pos.getY() > 20) return "underground";
        return "deep underground";
    }

    private static String hotbarItems(ServerPlayer player) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            var stack = player.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(stack.getHoverName().getString());
            }
        }
        return sb.toString();
    }

    private static String nearbyAnimals(ServerLevel level, BlockPos pos) {
        List<Animal> animals = level.getEntitiesOfClass(Animal.class, new AABB(pos).inflate(32), e -> true);
        if (animals.isEmpty()) return "";
        Map<String, Long> counts = animals.stream().collect(
            Collectors.groupingBy(a -> a.getType().toShortString(), Collectors.counting()));
        return counts.entrySet().stream()
            .map(e -> approximate(e.getValue()) + " " + e.getKey())
            .collect(Collectors.joining(", "));
    }

    private static String nearbyThreats(ServerLevel level, BlockPos pos) {
        List<Monster> monsters = level.getEntitiesOfClass(Monster.class, new AABB(pos).inflate(32), e -> true);
        if (monsters.isEmpty()) return "";
        Map<String, Long> counts = monsters.stream().collect(
            Collectors.groupingBy(m -> m.getType().toShortString(), Collectors.counting()));
        return counts.entrySet().stream()
            .map(e -> approximate(e.getValue()) + " " + e.getKey())
            .collect(Collectors.joining(", "));
    }

    private static String approximate(long count) {
        if (count == 1) return "one";
        if (count <= 3) return "a few";
        if (count <= 7) return "several";
        return "many";
    }

    private static String biomeName(ServerLevel level, BlockPos pos) {
        return level.getBiome(pos).unwrapKey()
            .map(k -> k.location().getPath().replace("_", " "))
            .orElse("");
    }
}
