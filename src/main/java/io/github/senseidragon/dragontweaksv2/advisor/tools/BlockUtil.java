package io.github.senseidragon.dragontweaksv2.advisor.tools;

import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.stream.Collectors;

public class BlockUtil {

    private BlockUtil() {}

    public static String friendlyName(BlockState state) {
        String name = state.getBlock().getName().getString();
        if (name.contains(".") && !name.contains(" ")) {
            String[] parts = name.split("\\.");
            String last = parts[parts.length - 1];
            name = Arrays.stream(last.split("_"))
                .filter(w -> !w.isEmpty())
                .map(w -> Character.toUpperCase(w.charAt(0)) + w.substring(1))
                .collect(Collectors.joining(" "));
        }
        return name.isBlank() ? null : name;
    }
}
