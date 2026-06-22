package io.github.senseidragon.dragontweaksv2.advisor.tools;

import com.google.gson.JsonObject;
import io.github.senseidragon.dragontweaksv2.advisor.AdvisorTool;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InventoryTool implements AdvisorTool {

    @Override
    public String name() { return "get_inventory"; }

    @Override
    public JsonObject definition() {
        JsonObject params = new JsonObject();
        params.addProperty("type", "object");
        params.add("properties", new JsonObject());

        JsonObject fn = new JsonObject();
        fn.addProperty("name", "get_inventory");
        fn.addProperty("description",
            "Returns the player's current inventory including armor, off-hand, and all 36 inventory slots.");
        fn.add("parameters", params);

        JsonObject tool = new JsonObject();
        tool.addProperty("type", "function");
        tool.add("function", fn);
        return tool;
    }

    @Override
    public String execute(JsonObject args, ServerPlayer player) {
        try {
            List<String> lines = new ArrayList<>();

            for (ItemStack stack : player.getArmorSlots()) {
                if (!stack.isEmpty()) lines.add(format(stack));
            }

            ItemStack offhand = player.getOffhandItem();
            if (!offhand.isEmpty()) lines.add(format(offhand));

            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (!stack.isEmpty()) lines.add(format(stack));
            }

            return lines.isEmpty() ? "Inventory is empty." : String.join("\n", lines);
        } catch (Exception e) {
            return "[Tool error: inventory unavailable]";
        }
    }

    private String format(ItemStack stack) {
        String name = stack.getHoverName().getString();
        boolean container = stack.has(DataComponents.CONTAINER);
        if (container) return name + " (contents not scanned) x" + stack.getCount();
        boolean enchanted = stack.isEnchanted();
        return (enchanted ? "Enchanted " : "") + name + " x" + stack.getCount();
    }
}
