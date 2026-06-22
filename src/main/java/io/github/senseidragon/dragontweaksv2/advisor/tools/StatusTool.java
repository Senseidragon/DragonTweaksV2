package io.github.senseidragon.dragontweaksv2.advisor.tools;

import com.google.gson.JsonObject;
import io.github.senseidragon.dragontweaksv2.advisor.AdvisorTool;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.List;

public class StatusTool implements AdvisorTool {

    @Override
    public String name() { return "get_status"; }

    @Override
    public JsonObject definition() {
        JsonObject params = new JsonObject();
        params.addProperty("type", "object");
        params.add("properties", new JsonObject());

        JsonObject fn = new JsonObject();
        fn.addProperty("name", "get_status");
        fn.addProperty("description",
            "Returns the player's active detrimental status effects, including effect name and remaining duration in seconds.");
        fn.add("parameters", params);

        JsonObject tool = new JsonObject();
        tool.addProperty("type", "function");
        tool.add("function", fn);
        return tool;
    }

    @Override
    public String execute(JsonObject args, ServerPlayer player) {
        try {
            int currentHealth = Math.round(player.getHealth());
            int maxHealth = Math.round(player.getMaxHealth());

            List<String> effects = new ArrayList<>();
            for (MobEffectInstance instance : player.getActiveEffects()) {
                MobEffect effect = instance.getEffect().value();
                if (effect.getCategory() == MobEffectCategory.HARMFUL) {
                    int seconds = instance.getDuration() / 20;
                    effects.add(effect.getDisplayName().getString() + " (" + seconds + "s remaining)");
                }
            }
            String effectsText = effects.isEmpty()
                ? "No active detrimental effects."
                : "Active effects: " + String.join(", ", effects) + ".";

            return "Health: " + currentHealth + "/" + maxHealth + ". " + effectsText;
        } catch (Exception e) {
            return "[Tool error: status unavailable]";
        }
    }

    public static boolean playerHasDetrimentalEffects(ServerPlayer player) {
        return player.getActiveEffects().stream()
            .anyMatch(i -> i.getEffect().value().getCategory() == MobEffectCategory.HARMFUL);
    }
}
