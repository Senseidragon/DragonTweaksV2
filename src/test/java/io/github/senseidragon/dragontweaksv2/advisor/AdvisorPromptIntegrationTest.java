package io.github.senseidragon.dragontweaksv2.advisor;

import io.github.senseidragon.dragontweaksv2.openrouter.OpenRouterService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Integration tests that send real OpenRouter API calls.
 * Skipped automatically if run/client/.env is absent.
 * All 12 tests must pass before changes go into the mod.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdvisorPromptIntegrationTest {

    // Contexts matching EnvironmentContextBuilder output format (always includes animal/threat lines)
    private static final String CTX_NIGHT_PLAINS =
        "Main hand: Build Tool. Hotbar: Bread, Iron Sword, Torch. " +
        "Time: night. Weather: clear. Location: Y=63, open sky. " +
        "Nearby animals: one chicken, several sheep. No threats nearby. Biome: plains.";

    private static final String CTX_MORNING_FOREST =
        "Main hand: Wooden Axe. Hotbar: empty. Time: morning. Weather: clear. " +
        "Location: Y=70, open sky. No animals nearby. No threats nearby. Biome: forest.";

    private static final String CTX_THUNDERSTORM_SWAMP =
        "Main hand: Iron Sword. Hotbar: empty. Time: midnight. Weather: thunderstorm. " +
        "Location: Y=58, open sky. No animals nearby. " +
        "Nearby threats: several zombie, one skeleton. Biome: swamp.";

    private static final String CTX_DEEP_UNDERGROUND =
        "Main hand: Iron Pickaxe. Hotbar: Torch, Stone, Coal. " +
        "Time: night. Weather: clear. Location: Y=12, deep underground. " +
        "No animals nearby. No threats nearby. Biome: plains.";

    private static final String CTX_NO_CREATURES =
        "Main hand: Build Tool. Hotbar: empty. Time: night. Weather: clear. " +
        "Location: Y=63, open sky. No animals nearby. No threats nearby. Biome: plains.";

    // Items that must never appear hallucinated (not in any pool or context above)
    private static final List<String> HALLUCINATION_TERMS =
        List.of("biscuit", "flask", "coyote", "lantern", "seeds", "rope", "knife", "rusted key");

    // Pool for random hotbar tests — no overlap with hallucination terms
    private static final List<String> HOTBAR_POOL = List.of(
        "Bread", "Iron Sword", "Wooden Pickaxe", "Torch", "Arrow",
        "Bow", "Apple", "Coal", "Iron Ingot", "Feather",
        "Bone", "Stick", "Diamond", "Compass", "Fishing Rod",
        "Bucket", "Flint and Steel", "Shears", "Emerald", "Oak Log"
    );

    private OpenRouterService service;

    @BeforeAll
    void setUp() throws Exception {
        Path envPath = Path.of("run/client/.env");
        assumeTrue(Files.exists(envPath), "Skipping — run/client/.env not found");
        service = new OpenRouterService(Path.of("run/client"));
        service.initAsync(reason -> fail("OpenRouter init failed: " + reason))
               .get(15, TimeUnit.SECONDS);
        assumeTrue(service.isEnabled(), "Skipping — OpenRouter failed to enable");
    }

    @AfterAll
    void tearDown() {
        if (service != null) service.shutdown();
    }

    // ── 1 ─────────────────────────────────────────────────────────────────────
    @Test
    void t01_timeQuery_nightContext_atMostThreeSentences() throws Exception {
        String r = ask("what time is it", CTX_NIGHT_PLAINS);
        assertSentences(r, 3);
    }

    // ── 2 ─────────────────────────────────────────────────────────────────────
    @Test
    void t02_timeQuery_morningContext_atMostThreeSentences() throws Exception {
        String r = ask("what time is it", CTX_MORNING_FOREST);
        assertSentences(r, 3);
    }

    // ── 3 ─────────────────────────────────────────────────────────────────────
    @Test
    void t03_inventoryQuery_emptyHotbar_atMostThreeSentences() throws Exception {
        String r = ask("what is in my inventory", CTX_NO_CREATURES);
        assertSentences(r, 3);
    }

    // ── 4 ─────────────────────────────────────────────────────────────────────
    @Test
    void t04_inventoryQuery_randomHotbar_atMostThreeSentences() throws Exception {
        String ctx = buildRandomHotbarContext();
        System.out.println("[t04] context: " + ctx);
        String r = ask("what is in my inventory", ctx);
        assertSentences(r, 3);
    }

    // ── 5 ─────────────────────────────────────────────────────────────────────
    @Test
    void t05_creaturesQuery_chickenAndSheep_atMostThreeSentences() throws Exception {
        String r = ask("what creatures are near?", CTX_NIGHT_PLAINS);
        assertSentences(r, 3);
    }

    // ── 6 ─────────────────────────────────────────────────────────────────────
    @Test
    void t06_creaturesQuery_noAnimals_atMostThreeSentences() throws Exception {
        String r = ask("what creatures are near?", CTX_NO_CREATURES);
        assertSentences(r, 3);
        String lower = r.toLowerCase();
        assertFalse(lower.contains("wolf") || lower.contains("jackal") || lower.contains("bear") ||
                    lower.contains("zombie") || lower.contains("skeleton") || lower.contains("coyote"),
            "Model hallucinated creatures when none were in context. Response: " + r);
    }

    // ── 7 ─────────────────────────────────────────────────────────────────────
    @Test
    void t07_inventoryQuery_emptyHotbar_doesNotHallucinate() throws Exception {
        String r = ask("what is in my inventory", CTX_NO_CREATURES);
        assertNoHallucination(r, CTX_NO_CREATURES);
    }

    // ── 8 ─────────────────────────────────────────────────────────────────────
    @Test
    void t08_inventoryQuery_randomHotbar_doesNotHallucinate() throws Exception {
        String[] pair = buildRandomHotbarContextWithItems();
        String ctx = pair[0];
        List<String> selectedItems = List.of(pair[1].split(", "));
        System.out.println("[t08] context: " + ctx);
        String r = ask("what is in my inventory", ctx);
        System.out.println("[t08] selected: " + selectedItems);
        // Verify model didn't mention pool items that were NOT in the hotbar
        String lower = r.toLowerCase();
        for (String poolItem : HOTBAR_POOL) {
            if (!selectedItems.stream().anyMatch(s -> s.equalsIgnoreCase(poolItem))) {
                assertFalse(lower.contains(poolItem.toLowerCase()),
                    "Model mentioned '" + poolItem + "' which was not in the hotbar. Response: " + r);
            }
        }
    }

    // ── 9 ─────────────────────────────────────────────────────────────────────
    @Test
    void t09_creaturesQuery_chickenAndSheep_doesNotInventCreatures() throws Exception {
        String r = ask("what creatures are near?", CTX_NIGHT_PLAINS);
        String lower = r.toLowerCase();
        // Context has chicken and sheep only
        assertFalse(lower.contains("coyote"), "Invented coyote. Response: " + r);
        assertFalse(lower.contains("mouse"),  "Invented mouse.  Response: " + r);
        assertFalse(lower.contains("wolf"),   "Invented wolf.   Response: " + r);
        assertFalse(lower.contains("fox"),    "Invented fox.    Response: " + r);
        assertFalse(lower.contains("spider"), "Invented spider. Response: " + r);
        assertFalse(lower.contains("zombie"), "Invented zombie. Response: " + r);
    }

    // ── 10 ────────────────────────────────────────────────────────────────────
    @Test
    void t10_timeQuery_nightContext_doesNotHallucinateTimeOfDay() throws Exception {
        String r = ask("what time is it", CTX_NIGHT_PLAINS);
        String lower = r.toLowerCase();
        // Context says night — must not describe afternoon or dawn
        assertFalse(lower.contains("afternoon"), "Invented afternoon. Response: " + r);
        assertFalse(lower.contains("sunrise"),   "Invented sunrise.  Response: " + r);
        // Note: "dusk" or "dawn" referenced poetically may appear in "heading toward dawn" etc.
        // Core check: model should not claim it is currently a daytime period
        assertFalse(lower.contains("midday") || lower.contains("noon"),
            "Invented midday/noon. Response: " + r);
    }

    // ── 11 ────────────────────────────────────────────────────────────────────
    @Test
    void t11_threatsQuery_zombiesAndSkeleton_mentionsThreat() throws Exception {
        String r = ask("what threats are nearby?", CTX_THUNDERSTORM_SWAMP);
        String lower = r.toLowerCase();
        assertSentences(r, 3);
        // Context explicitly lists zombie and skeleton — model should name at least one
        assertTrue(lower.contains("zombie") || lower.contains("skeleton") || lower.contains("undead"),
            "Response does not mention the threats from context. Response: " + r);
    }

    // ── 12 ────────────────────────────────────────────────────────────────────
    @Test
    void t12_unknownQuestion_doesNotInventAnswer() throws Exception {
        // Player name is not in any context — model must say it doesn't know, not invent
        String r = ask("what is my name?", CTX_NIGHT_PLAINS);
        assertSentences(r, 3);
        String lower = r.toLowerCase();
        assertNoHallucination(r, CTX_NIGHT_PLAINS);
        // Should not claim to know the player's name
        assertFalse(lower.matches(".*\\bmy name is\\b.*") || lower.matches(".*\\byour name is\\b.*"),
            "Model invented player name. Response: " + r);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String ask(String question, String context) throws Exception {
        String prompt = AdvisorChatHandler.SYSTEM_PROMPT + context;
        String r = service.queryAsync(prompt, List.of(new ChatMessage("user", question)))
                          .get(30, TimeUnit.SECONDS);
        System.out.println("[" + question + "] → " + r);
        return r;
    }

    private static void assertSentences(String response, int max) {
        int count = countSentences(response);
        assertTrue(count <= max,
            "Expected at most " + max + " sentences, got " + count + ". Response: " + response);
    }

    private static void assertNoHallucination(String response, String context) {
        String lower = response.toLowerCase();
        String ctxLower = context.toLowerCase();
        for (String term : HALLUCINATION_TERMS) {
            if (!ctxLower.contains(term)) {
                assertFalse(lower.contains(term),
                    "Hallucinated '" + term + "'. Response: " + response);
            }
        }
    }

    private static String buildRandomHotbarContext() {
        return buildRandomHotbarContextWithItems()[0];
    }

    private static String[] buildRandomHotbarContextWithItems() {
        int count = 1 + new Random().nextInt(8); // 1–8
        List<String> pool = new ArrayList<>(HOTBAR_POOL);
        Collections.shuffle(pool);
        String items = pool.subList(0, count).stream().collect(Collectors.joining(", "));
        String ctx = "Main hand: Build Tool. Hotbar: " + items + ". " +
                     "Time: night. Weather: clear. Location: Y=63, open sky. " +
                     "No animals nearby. No threats nearby. Biome: plains.";
        return new String[]{ctx, items};
    }

    private static int countSentences(String text) {
        if (text == null || text.isBlank()) return 0;
        String[] parts = text.trim().split("[.!?]+\\s+");
        // Single-word fragments (e.g. "Night.") are stylistic punctuation, not content sentences
        return (int) java.util.Arrays.stream(parts)
            .filter(p -> p.trim().split("\\s+").length >= 2)
            .count();
    }
}
