package io.github.senseidragon.dragontweaksv2.advisor;

import com.google.gson.JsonObject;
import io.github.senseidragon.dragontweaksv2.advisor.tools.EnvironmentTool;
import io.github.senseidragon.dragontweaksv2.advisor.tools.InventoryTool;
import io.github.senseidragon.dragontweaksv2.advisor.tools.ScanAreaTool;
import io.github.senseidragon.dragontweaksv2.advisor.tools.StatusTool;
import io.github.senseidragon.dragontweaksv2.openrouter.OpenRouterService;
import net.minecraft.server.level.ServerPlayer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Generative replacement for AdvisorPromptIntegrationTest and EnvironmentToolSimulationTest.
 * Runs randomized-context, paraphrased-intent trials against the real ToolCallOrchestrator
 * and asserts a pass-rate threshold per property instead of one fixed example per case.
 * Skipped automatically if run/client/.env is absent.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdvisorPersonaGenerativeTest {

    private static final int TRIALS_PER_INTENT = 2;
    private static final double PASS_RATE_THRESHOLD = 0.8;

    private static final List<String> BANNED_PHRASES =
        List.of("scan", "data", "results", "that's all", "hope that helps");

    private static final List<String> HALLUCINATION_POOL =
        List.of("biscuit", "flask", "coyote", "lantern", "rope", "knife", "rusted key");

    private record Intent(String name, List<String> paraphrases, String expectedTool) {}

    private static final List<Intent> INTENTS = List.of(
        new Intent("environment",
            List.of("what time is it", "what's the weather like", "what biome is this"),
            "get_environment"),
        new Intent("inventory",
            List.of("what's in my inventory", "what am i carrying", "what do i have on me"),
            "get_inventory"),
        new Intent("status",
            List.of("how am i feeling", "what's affecting me right now", "do i have any effects on me"),
            "get_status"),
        new Intent("nearby",
            List.of("what's around me", "what creatures are nearby", "what do you see around us"),
            "scan_area"),
        new Intent("chitchat",
            List.of("hello there", "hey, thanks for the help", "good to see you"),
            null)
    );

    private record TrialResult(String intent, String question, String response,
                                boolean correctToolCall, boolean noBannedPhrase,
                                boolean noHallucination, boolean personaConsistent) {}

    private OpenRouterService service;
    private final List<TrialResult> results = new ArrayList<>();

    @BeforeAll
    void setUpAndRunTrials() throws Exception {
        Path envPath = Path.of("run/client/.env");
        assumeTrue(Files.exists(envPath), "Skipping — run/client/.env not found");
        service = new OpenRouterService(Path.of("run/client"));
        service.initAsync(reason -> fail("OpenRouter init failed: " + reason))
               .get(15, TimeUnit.SECONDS);
        assumeTrue(service.isEnabled(), "Skipping — OpenRouter failed to enable");

        Random random = new Random();
        for (Intent intent : INTENTS) {
            for (int i = 0; i < TRIALS_PER_INTENT; i++) {
                String question = intent.paraphrases().get(random.nextInt(intent.paraphrases().size()));
                results.add(runTrial(intent, question, random));
            }
        }
    }

    @AfterAll
    void tearDown() {
        if (service != null) service.shutdown();
    }

    private TrialResult runTrial(Intent intent, String question, Random random) throws Exception {
        TrackingTool envTool = new TrackingTool("get_environment", new EnvironmentTool().definition(),
            randomEnvironmentReading(random));
        TrackingTool invTool = new TrackingTool("get_inventory", new InventoryTool().definition(),
            randomInventoryReading(random));
        TrackingTool statusTool = new TrackingTool("get_status", new StatusTool().definition(),
            randomStatusReading(random));
        TrackingTool scanTool = new TrackingTool("scan_area", new ScanAreaTool().definition(),
            randomScanReading(random));

        ToolCallOrchestrator orchestrator = new ToolCallOrchestrator(
            service, List.of(envTool, invTool, statusTool, scanTool), false);

        AtomicReference<String> response = new AtomicReference<>();
        AdvisorSession session = new AdvisorSession(20);
        orchestrator.handleQuery(question, null, session, response::set, Runnable::run, () -> true)
            .get(30, TimeUnit.SECONDS);

        String reply = response.get() != null ? response.get() : "";

        boolean correctToolCall = intent.expectedTool() == null
            ? Stream.of(envTool, invTool, statusTool, scanTool).noneMatch(TrackingTool::wasCalled)
            : toolNamed(intent.expectedTool(), envTool, invTool, statusTool, scanTool).wasCalled();

        String lowerReply = reply.toLowerCase(Locale.ROOT);
        boolean noBannedPhrase = BANNED_PHRASES.stream().noneMatch(lowerReply::contains);
        boolean noHallucination = HALLUCINATION_POOL.stream().noneMatch(lowerReply::contains);
        boolean personaConsistent = judgePersonaConsistency(question, reply);

        System.out.println("[" + intent.name() + "] Q: " + question + " -> A: " + reply);
        return new TrialResult(intent.name(), question, reply, correctToolCall, noBannedPhrase,
            noHallucination, personaConsistent);
    }

    private TrackingTool toolNamed(String name, TrackingTool... tools) {
        for (TrackingTool t : tools) if (t.name().equals(name)) return t;
        throw new IllegalArgumentException("No tracking tool named " + name);
    }

    private boolean judgePersonaConsistency(String question, String response) {
        if (response.isBlank()) return false;
        String judgePrompt =
            "You are grading a roleplay response for character consistency. " +
            "The character is a seasoned adventurer: plain-spoken, speaks from experience, never lectures, " +
            "answers only what was asked, never tacks on a closing line, and never reveals it checked tools, " +
            "scans, or data. " +
            "Question asked: \"" + question + "\"\n" +
            "Character's response: \"" + response + "\"\n" +
            "Does the response stay in character on ALL of these traits? Reply with exactly one word: PASS or FAIL.";
        try {
            String verdict = service.query("advisory", judgePrompt).get(30, TimeUnit.SECONDS);
            return verdict != null && verdict.toUpperCase(Locale.ROOT).contains("PASS");
        } catch (Exception e) {
            return false;
        }
    }

    // ── pass-rate assertions ──────────────────────────────────────────────────

    @Test
    void correctToolCallPassRate() {
        assertPassRate(TrialResult::correctToolCall, "correct tool call");
    }

    @Test
    void noBannedPhrasePassRate() {
        assertPassRate(TrialResult::noBannedPhrase, "no banned phrase");
    }

    @Test
    void noHallucinationPassRate() {
        assertPassRate(TrialResult::noHallucination, "no hallucination");
    }

    @Test
    void personaConsistencyPassRate() {
        assertPassRate(TrialResult::personaConsistent, "persona consistency");
    }

    private void assertPassRate(Predicate<TrialResult> property, String label) {
        assumeTrue(!results.isEmpty(), "No trials ran — skipping");
        long passed = results.stream().filter(property).count();
        double rate = (double) passed / results.size();
        assertTrue(rate >= PASS_RATE_THRESHOLD,
            String.format("%s pass rate %.0f%% below threshold %.0f%% (%d/%d). Failures: %s",
                label, rate * 100, PASS_RATE_THRESHOLD * 100, passed, results.size(),
                results.stream().filter(property.negate())
                    .map(r -> "[" + r.intent() + "] \"" + r.question() + "\" -> \"" + r.response() + "\"")
                    .collect(Collectors.joining("; "))));
    }

    // ── randomized tool-result generators ────────────────────────────────────

    private static String randomEnvironmentReading(Random r) {
        List<String> times = List.of("morning", "midday", "dusk", "night", "midnight");
        List<String> weathers = List.of("clear", "raining", "thunderstorm");
        List<String> biomes = List.of("plains", "forest", "swamp", "desert", "taiga");
        return "Time: " + pick(times, r) + ". Day: " + (1 + r.nextInt(50)) + ". Weather: " + pick(weathers, r) +
               ". Biome: " + pick(biomes, r) + ". Elevation: " + (r.nextInt(80) - 20) + " blocks above sea level.";
    }

    private static String randomInventoryReading(Random r) {
        List<String> pool = List.of("Bread", "Iron Sword", "Torch", "Arrow", "Apple", "Coal", "Stick", "Bucket");
        List<String> items = new ArrayList<>(pool);
        Collections.shuffle(items, r);
        int count = 1 + r.nextInt(4);
        return items.subList(0, count).stream().map(i -> i + " x1").collect(Collectors.joining("\n"));
    }

    private static String randomStatusReading(Random r) {
        if (r.nextBoolean()) return "No active detrimental effects.";
        List<String> effects = List.of("Poison", "Slowness", "Weakness", "Hunger");
        return "Active effects: " + pick(effects, r) + " (" + (5 + r.nextInt(30)) + "s remaining).";
    }

    private static String randomScanReading(Random r) {
        if (r.nextBoolean()) return "Nothing notable detected nearby.";
        List<String> creatures = List.of("Passive: 2x Sheep", "Hostile: 1x Zombie", "Neutral: 1x Wolf");
        return pick(creatures, r);
    }

    private static <T> T pick(List<T> options, Random r) {
        return options.get(r.nextInt(options.size()));
    }

    // ── tracking tool ─────────────────────────────────────────────────────────

    private static class TrackingTool implements AdvisorTool {
        private final String name;
        private final JsonObject def;
        private final String returnValue;
        private boolean called = false;

        TrackingTool(String name, JsonObject def, String returnValue) {
            this.name = name;
            this.def = def;
            this.returnValue = returnValue;
        }

        @Override public String name() { return name; }
        @Override public JsonObject definition() { return def; }
        @Override public String execute(JsonObject args, ServerPlayer player) {
            called = true;
            return returnValue;
        }
        public boolean wasCalled() { return called; }
    }
}
