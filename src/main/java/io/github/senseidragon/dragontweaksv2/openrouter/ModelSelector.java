package io.github.senseidragon.dragontweaksv2.openrouter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class ModelSelector {

    private ModelSelector() {}

    public static String selectCheapest(JsonObject config, String role) {
        JsonObject roles = config.getAsJsonObject("roles");
        if (roles == null || !roles.has(role)) {
            throw new IllegalArgumentException("no candidates for role '" + role + "'.");
        }
        JsonArray candidates = roles.getAsJsonObject(role).getAsJsonArray("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalArgumentException("no candidates for role '" + role + "'.");
        }
        String cheapestId = null;
        double cheapestCost = Double.MAX_VALUE;
        for (JsonElement el : candidates) {
            JsonObject c = el.getAsJsonObject();
            double cost = c.get("role_weighted_cost_per_1m").getAsDouble();
            if (cost < cheapestCost) {
                cheapestCost = cost;
                cheapestId = c.get("model_id").getAsString();
            }
        }
        return cheapestId;
    }
}
