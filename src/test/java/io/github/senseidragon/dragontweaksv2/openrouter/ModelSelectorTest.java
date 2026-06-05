package io.github.senseidragon.dragontweaksv2.openrouter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelSelectorTest {

    private static final Gson GSON = new Gson();

    private static final String TWO_CANDIDATE_CONFIG = """
        {
          "roles": {
            "flavor": {
              "candidates": [
                {"model_id": "expensive-model", "role_weighted_cost_per_1m": 0.20},
                {"model_id": "cheap-model",     "role_weighted_cost_per_1m": 0.05}
              ]
            }
          }
        }
        """;

    @Test
    void selectsCheapestByWeightedCost() {
        JsonObject config = GSON.fromJson(TWO_CANDIDATE_CONFIG, JsonObject.class);
        assertEquals("cheap-model", ModelSelector.selectCheapest(config, "flavor"));
    }

    @Test
    void throwsOnMissingRole() {
        JsonObject config = GSON.fromJson(TWO_CANDIDATE_CONFIG, JsonObject.class);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> ModelSelector.selectCheapest(config, "nonexistent"));
        assertTrue(ex.getMessage().contains("nonexistent"));
    }

    @Test
    void throwsOnEmptyCandidatesList() {
        JsonObject config = GSON.fromJson(
            "{\"roles\":{\"flavor\":{\"candidates\":[]}}}", JsonObject.class);
        assertThrows(IllegalArgumentException.class,
            () -> ModelSelector.selectCheapest(config, "flavor"));
    }

    @Test
    void throwsWhenRolesObjectMissing() {
        JsonObject config = GSON.fromJson("{}", JsonObject.class);
        assertThrows(IllegalArgumentException.class,
            () -> ModelSelector.selectCheapest(config, "flavor"));
    }
}
