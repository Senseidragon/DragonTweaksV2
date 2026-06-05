# OpenRouter Integration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** On logical-server startup, asynchronously validate the OpenRouter API key, select the cheapest `flavor` and `advisory` models from `model_config.json`, prime both models, and disable the mod with a player chat message on any failure — without blocking the main thread.

**Architecture:** `EnvLoader` and `ModelSelector` are pure-Java utilities with no side effects, making them directly unit-testable. `OpenRouterService` owns a single-thread executor and a `java.net.http.HttpClient`; it accepts both via constructor so tests can inject mocks. `DragonTweaksV2` wires `ServerStartingEvent` → `initAsync` and `ServerStoppingEvent` → `shutdown`. The physical Minecraft client (`Dist.CLIENT`) has zero OpenRouter code.

**Tech Stack:** Java 21 (`java.net.http.HttpClient`), Gson (bundled with NeoForge), SLF4J (bundled with NeoForge), JUnit 5, Mockito 5

---

## File Map

| Action | Path | Responsibility |
|---|---|---|
| Create | `src/main/java/.../openrouter/EnvLoader.java` | Parse `.env` → `Map<String,String>` |
| Create | `src/main/java/.../openrouter/ModelSelector.java` | Pick cheapest model_id per role from parsed JSON |
| Create | `src/main/java/.../openrouter/OpenRouterService.java` | Async init orchestration, singleton |
| Modify | `src/main/java/.../DragonTweaksV2.java` | Wire server start/stop events |
| Create | `src/test/java/.../openrouter/EnvLoaderTest.java` | Unit tests for EnvLoader |
| Create | `src/test/java/.../openrouter/ModelSelectorTest.java` | Unit tests for ModelSelector |
| Create | `src/test/java/.../openrouter/OpenRouterServiceTest.java` | Unit tests for OpenRouterService |
| Modify | `build.gradle` | Add JUnit 5 + Mockito test dependencies |

All source paths expand from `src/main/java/io/github/senseidragon/dragontweaksv2/`.

---

## Task 1: Configure Test Infrastructure

**Files:**
- Modify: `build.gradle`

- [ ] **Step 1: Add test dependencies to build.gradle**

In `build.gradle`, add inside the existing `dependencies { }` block after the last existing dependency:

```groovy
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.2'
    testImplementation 'org.mockito:mockito-core:5.11.0'
    testImplementation 'org.mockito:mockito-junit-jupiter:5.11.0'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
```

Then add this block after the `dependencies { }` block:

```groovy
tasks.named('test', Test) {
    useJUnitPlatform()
}
```

- [ ] **Step 2: Verify Gradle resolves dependencies**

```bash
./gradlew dependencies --configuration testRuntimeClasspath
```

Expected: resolves without error; `junit-jupiter` and `mockito-core` appear in output.

- [ ] **Step 3: Verify test task runs with no tests**

```bash
./gradlew test
```

Expected: BUILD SUCCESSFUL, `0 tests completed`.

- [ ] **Step 4: Commit**

```bash
git add build.gradle
git commit -m "chore: add JUnit 5 and Mockito test dependencies"
```

---

## Task 2: EnvLoader — RED

**Files:**
- Create: `src/test/java/io/github/senseidragon/dragontweaksv2/openrouter/EnvLoaderTest.java`

- [ ] **Step 1: Create the test directory structure**

```bash
mkdir -p src/test/java/io/github/senseidragon/dragontweaksv2/openrouter
```

- [ ] **Step 2: Write the failing tests**

Create `src/test/java/io/github/senseidragon/dragontweaksv2/openrouter/EnvLoaderTest.java`:

```java
package io.github.senseidragon.dragontweaksv2.openrouter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EnvLoaderTest {

    @TempDir Path tempDir;

    @Test
    void loadsKeyValuePairs() throws IOException {
        Files.writeString(tempDir.resolve(".env"), "OPENROUTER_API_KEY=my-key\nOTHER=value\n");
        Map<String, String> env = EnvLoader.load(tempDir.resolve(".env"));
        assertEquals("my-key", env.get("OPENROUTER_API_KEY"));
        assertEquals("value", env.get("OTHER"));
    }

    @Test
    void throwsWhenFileMissing() {
        assertThrows(IOException.class, () -> EnvLoader.load(tempDir.resolve(".env")));
    }

    @Test
    void returnsMapWithoutAbsentKey() throws IOException {
        Files.writeString(tempDir.resolve(".env"), "OTHER=value\n");
        Map<String, String> env = EnvLoader.load(tempDir.resolve(".env"));
        assertFalse(env.containsKey("OPENROUTER_API_KEY"));
    }

    @Test
    void ignoresMalformedAndCommentLines() throws IOException {
        Files.writeString(tempDir.resolve(".env"), "# comment\n\nBAD_LINE\nGOOD=yes\n");
        Map<String, String> env = EnvLoader.load(tempDir.resolve(".env"));
        assertEquals("yes", env.get("GOOD"));
        assertEquals(1, env.size());
    }
}
```

- [ ] **Step 3: Run tests and confirm they fail**

```bash
./gradlew test --tests "io.github.senseidragon.dragontweaksv2.openrouter.EnvLoaderTest"
```

Expected: FAIL — `cannot find symbol: EnvLoader`

---

## Task 3: EnvLoader — GREEN

**Files:**
- Create: `src/main/java/io/github/senseidragon/dragontweaksv2/openrouter/EnvLoader.java`

- [ ] **Step 1: Create the source directory**

```bash
mkdir -p src/main/java/io/github/senseidragon/dragontweaksv2/openrouter
```

- [ ] **Step 2: Write the implementation**

Create `src/main/java/io/github/senseidragon/dragontweaksv2/openrouter/EnvLoader.java`:

```java
package io.github.senseidragon.dragontweaksv2.openrouter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class EnvLoader {

    private EnvLoader() {}

    public static Map<String, String> load(Path envFile) throws IOException {
        Map<String, String> result = new HashMap<>();
        for (String line : Files.readAllLines(envFile)) {
            String trimmed = line.strip();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
            int eq = trimmed.indexOf('=');
            if (eq < 1) continue;
            result.put(trimmed.substring(0, eq).strip(), trimmed.substring(eq + 1).strip());
        }
        return result;
    }
}
```

- [ ] **Step 3: Run tests and confirm they pass**

```bash
./gradlew test --tests "io.github.senseidragon.dragontweaksv2.openrouter.EnvLoaderTest"
```

Expected: 4 tests, all PASS.

- [ ] **Step 4: Commit**

```bash
git add src/main/java/io/github/senseidragon/dragontweaksv2/openrouter/EnvLoader.java \
        src/test/java/io/github/senseidragon/dragontweaksv2/openrouter/EnvLoaderTest.java
git commit -m "feat: add EnvLoader for .env file parsing"
```

---

## Task 4: ModelSelector — RED

**Files:**
- Create: `src/test/java/io/github/senseidragon/dragontweaksv2/openrouter/ModelSelectorTest.java`

- [ ] **Step 1: Write the failing tests**

Create `src/test/java/io/github/senseidragon/dragontweaksv2/openrouter/ModelSelectorTest.java`:

```java
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
```

- [ ] **Step 2: Run tests and confirm they fail**

```bash
./gradlew test --tests "io.github.senseidragon.dragontweaksv2.openrouter.ModelSelectorTest"
```

Expected: FAIL — `cannot find symbol: ModelSelector`

---

## Task 5: ModelSelector — GREEN

**Files:**
- Create: `src/main/java/io/github/senseidragon/dragontweaksv2/openrouter/ModelSelector.java`

- [ ] **Step 1: Write the implementation**

Create `src/main/java/io/github/senseidragon/dragontweaksv2/openrouter/ModelSelector.java`:

```java
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
```

- [ ] **Step 2: Run tests and confirm they pass**

```bash
./gradlew test --tests "io.github.senseidragon.dragontweaksv2.openrouter.ModelSelectorTest"
```

Expected: 4 tests, all PASS.

- [ ] **Step 3: Commit**

```bash
git add src/main/java/io/github/senseidragon/dragontweaksv2/openrouter/ModelSelector.java \
        src/test/java/io/github/senseidragon/dragontweaksv2/openrouter/ModelSelectorTest.java
git commit -m "feat: add ModelSelector for cheapest-model-per-role selection"
```

---

## Task 6: OpenRouterService — RED

**Files:**
- Create: `src/test/java/io/github/senseidragon/dragontweaksv2/openrouter/OpenRouterServiceTest.java`

- [ ] **Step 1: Write the failing tests**

Create `src/test/java/io/github/senseidragon/dragontweaksv2/openrouter/OpenRouterServiceTest.java`:

```java
package io.github.senseidragon.dragontweaksv2.openrouter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenRouterServiceTest {

    @TempDir Path tempDir;
    @Mock HttpClient mockClient;
    @Mock HttpResponse<String> okResponse;
    @Mock HttpResponse<String> failResponse;

    private static final String MINIMAL_CONFIG = """
        {
          "roles": {
            "flavor": {
              "candidates": [{"model_id": "flavor-model", "role_weighted_cost_per_1m": 0.05}]
            },
            "advisory": {
              "candidates": [{"model_id": "advisory-model", "role_weighted_cost_per_1m": 0.07}]
            }
          }
        }
        """;

    @BeforeEach
    void setUp() throws Exception {
        Files.writeString(tempDir.resolve(".env"), "OPENROUTER_API_KEY=test-key\n");
        Files.writeString(tempDir.resolve("model_config.json"), MINIMAL_CONFIG);
        when(okResponse.statusCode()).thenReturn(200);
        when(failResponse.statusCode()).thenReturn(401);
    }

    @Test
    void isDisabledByDefault() {
        OpenRouterService service = new OpenRouterService(mockClient, tempDir);
        assertFalse(service.isEnabled());
    }

    @Test
    void enablesOnFullSuccess() throws Exception {
        when(mockClient.send(any(HttpRequest.class), any())).thenReturn(okResponse);
        OpenRouterService service = new OpenRouterService(mockClient, tempDir);
        List<String> failures = new ArrayList<>();
        service.initAsync(failures::add).get(5, TimeUnit.SECONDS);
        assertTrue(service.isEnabled());
        assertTrue(failures.isEmpty());
        assertEquals("flavor-model", service.getFlavorModelId());
        assertEquals("advisory-model", service.getAdvisoryModelId());
    }

    @Test
    void disablesWhenEnvFileMissing() throws Exception {
        Files.delete(tempDir.resolve(".env"));
        OpenRouterService service = new OpenRouterService(mockClient, tempDir);
        List<String> failures = new ArrayList<>();
        service.initAsync(failures::add).get(5, TimeUnit.SECONDS);
        assertFalse(service.isEnabled());
        assertTrue(failures.get(0).contains(".env file not found"));
    }

    @Test
    void disablesWhenApiKeyMissing() throws Exception {
        Files.writeString(tempDir.resolve(".env"), "OTHER=value\n");
        OpenRouterService service = new OpenRouterService(mockClient, tempDir);
        List<String> failures = new ArrayList<>();
        service.initAsync(failures::add).get(5, TimeUnit.SECONDS);
        assertFalse(service.isEnabled());
        assertTrue(failures.get(0).contains("OPENROUTER_API_KEY not set"));
    }

    @Test
    void disablesWhenModelConfigMissing() throws Exception {
        Files.delete(tempDir.resolve("model_config.json"));
        OpenRouterService service = new OpenRouterService(mockClient, tempDir);
        List<String> failures = new ArrayList<>();
        service.initAsync(failures::add).get(5, TimeUnit.SECONDS);
        assertFalse(service.isEnabled());
        assertTrue(failures.get(0).contains("model_config.json unreadable"));
    }

    @Test
    void disablesWhenKeyValidationFails() throws Exception {
        when(mockClient.send(any(HttpRequest.class), any())).thenReturn(failResponse);
        OpenRouterService service = new OpenRouterService(mockClient, tempDir);
        List<String> failures = new ArrayList<>();
        service.initAsync(failures::add).get(5, TimeUnit.SECONDS);
        assertFalse(service.isEnabled());
        assertTrue(failures.get(0).contains("API key rejected"));
    }

    @Test
    void disablesWhenFlavorModelPrimeFails() throws Exception {
        when(mockClient.send(any(HttpRequest.class), any()))
            .thenReturn(okResponse)   // key validation passes
            .thenReturn(failResponse); // flavor prime fails
        OpenRouterService service = new OpenRouterService(mockClient, tempDir);
        List<String> failures = new ArrayList<>();
        service.initAsync(failures::add).get(5, TimeUnit.SECONDS);
        assertFalse(service.isEnabled());
        assertTrue(failures.get(0).contains("did not respond"));
    }

    @Test
    void disablesWhenAdvisoryModelPrimeFails() throws Exception {
        when(mockClient.send(any(HttpRequest.class), any()))
            .thenReturn(okResponse)   // key validation passes
            .thenReturn(okResponse)   // flavor prime passes
            .thenReturn(failResponse); // advisory prime fails
        OpenRouterService service = new OpenRouterService(mockClient, tempDir);
        List<String> failures = new ArrayList<>();
        service.initAsync(failures::add).get(5, TimeUnit.SECONDS);
        assertFalse(service.isEnabled());
        assertTrue(failures.get(0).contains("did not respond"));
    }

    @Test
    void apiKeyNeverAppearsInFailureReason() throws Exception {
        when(mockClient.send(any(HttpRequest.class), any())).thenReturn(failResponse);
        OpenRouterService service = new OpenRouterService(mockClient, tempDir);
        List<String> failures = new ArrayList<>();
        service.initAsync(failures::add).get(5, TimeUnit.SECONDS);
        failures.forEach(reason ->
            assertFalse(reason.contains("test-key"), "API key must not appear in failure reason"));
    }
}
```

- [ ] **Step 2: Run tests and confirm they fail**

```bash
./gradlew test --tests "io.github.senseidragon.dragontweaksv2.openrouter.OpenRouterServiceTest"
```

Expected: FAIL — `cannot find symbol: OpenRouterService`

---

## Task 7: OpenRouterService — GREEN

**Files:**
- Create: `src/main/java/io/github/senseidragon/dragontweaksv2/openrouter/OpenRouterService.java`

- [ ] **Step 1: Write the implementation**

Create `src/main/java/io/github/senseidragon/dragontweaksv2/openrouter/OpenRouterService.java`:

```java
package io.github.senseidragon.dragontweaksv2.openrouter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class OpenRouterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenRouterService.class);
    private static final String BASE_URL = "https://openrouter.ai/api/v1";
    private static final Gson GSON = new Gson();

    private static volatile OpenRouterService instance;

    private final HttpClient httpClient;
    private final Path workingDir;
    private final ExecutorService executor;

    private volatile boolean enabled = false;
    private String apiKey;
    private String flavorModelId;
    private String advisoryModelId;

    OpenRouterService(HttpClient httpClient, Path workingDir) {
        this.httpClient = httpClient;
        this.workingDir = workingDir;
        this.executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "openrouter-init");
            t.setDaemon(true);
            return t;
        });
    }

    public static OpenRouterService getInstance() {
        if (instance == null) {
            instance = new OpenRouterService(
                HttpClient.newHttpClient(),
                Path.of(System.getProperty("user.dir"))
            );
        }
        return instance;
    }

    public CompletableFuture<Void> initAsync(Consumer<String> onFailure) {
        LOGGER.debug("OpenRouter init started");
        return CompletableFuture.runAsync(() -> {
            try {
                init();
            } catch (Exception ex) {
                String reason = ex.getMessage() != null ? ex.getMessage() : "unexpected error";
                LOGGER.debug("OpenRouter init failed: {}", reason);
                onFailure.accept(reason);
            }
        }, executor);
    }

    private void init() throws Exception {
        // Step 1: Load API key
        Map<String, String> env;
        try {
            env = EnvLoader.load(workingDir.resolve(".env"));
        } catch (NoSuchFileException e) {
            throw new IllegalStateException(".env file not found.");
        }
        LOGGER.debug("API key present: {}", env.containsKey("OPENROUTER_API_KEY"));
        apiKey = env.get("OPENROUTER_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENROUTER_API_KEY not set.");
        }

        // Steps 2 & 3: Load model config and select cheapest per role
        String configJson;
        try {
            configJson = Files.readString(workingDir.resolve("model_config.json"));
        } catch (NoSuchFileException e) {
            throw new IllegalStateException("model_config.json unreadable.");
        }
        JsonObject config = GSON.fromJson(configJson, JsonObject.class);
        flavorModelId = ModelSelector.selectCheapest(config, "flavor");
        advisoryModelId = ModelSelector.selectCheapest(config, "advisory");
        LOGGER.debug("Selected models — flavor: {}, advisory: {}", flavorModelId, advisoryModelId);

        // Step 4: Validate API key
        HttpRequest keyRequest = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/auth/key"))
            .header("Authorization", "Bearer " + apiKey)
            .GET()
            .build();
        HttpResponse<String> keyResp = httpClient.send(keyRequest, HttpResponse.BodyHandlers.ofString());
        if (keyResp.statusCode() < 200 || keyResp.statusCode() >= 300) {
            throw new IllegalStateException("API key rejected by OpenRouter.");
        }
        LOGGER.debug("API key validated successfully");

        // Steps 5 & 6: Prime models
        primeModel(flavorModelId);
        primeModel(advisoryModelId);

        enabled = true;
        LOGGER.debug("OpenRouter ready. flavor={}, advisory={}", flavorModelId, advisoryModelId);
    }

    private void primeModel(String modelId) throws Exception {
        String body = String.format(
            "{\"model\":\"%s\",\"messages\":[{\"role\":\"user\",\"content\":\"ping\"}]}",
            modelId
        );
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/chat/completions"))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new IllegalStateException("model '" + modelId + "' did not respond.");
        }
        LOGGER.debug("Model primed: {}", modelId);
    }

    public boolean isEnabled() { return enabled; }
    public String getFlavorModelId() { return flavorModelId; }
    public String getAdvisoryModelId() { return advisoryModelId; }

    public void shutdown() {
        executor.shutdown();
        instance = null;
    }
}
```

- [ ] **Step 2: Run tests and confirm they pass**

```bash
./gradlew test --tests "io.github.senseidragon.dragontweaksv2.openrouter.OpenRouterServiceTest"
```

Expected: 8 tests, all PASS.

- [ ] **Step 3: Run the full test suite**

```bash
./gradlew test
```

Expected: All tests PASS (12 total across all three test classes).

- [ ] **Step 4: Commit**

```bash
git add src/main/java/io/github/senseidragon/dragontweaksv2/openrouter/OpenRouterService.java \
        src/test/java/io/github/senseidragon/dragontweaksv2/openrouter/OpenRouterServiceTest.java
git commit -m "feat: add OpenRouterService with async init, model selection, and priming"
```

---

## Task 8: Wire into DragonTweaksV2

**Files:**
- Modify: `src/main/java/io/github/senseidragon/dragontweaksv2/DragonTweaksV2.java`

No unit test is possible here without the NeoForge runtime — the event system requires the full mod loader. Verification is via `./gradlew build` (compile check) followed by manual `./gradlew runClient`.

- [ ] **Step 1: Update DragonTweaksV2.java**

Replace the entire contents of `src/main/java/io/github/senseidragon/dragontweaksv2/DragonTweaksV2.java` with:

```java
package io.github.senseidragon.dragontweaksv2;

import com.mojang.logging.LogUtils;
import io.github.senseidragon.dragontweaksv2.openrouter.OpenRouterService;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.slf4j.Logger;

@Mod(DragonTweaksV2.MODID)
public class DragonTweaksV2 {

    public static final String MODID = "dragontweaksv2";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DragonTweaksV2(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("DragonTweaks V2 common setup complete.");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("DragonTweaks V2 loaded on server.");
        var server = event.getServer();
        OpenRouterService.getInstance().initAsync(reason -> {
            String msg = "[DragonTweaks] AI advisor unavailable — " + reason;
            server.execute(() ->
                server.getPlayerList().getPlayers().forEach(player ->
                    player.sendSystemMessage(Component.literal(msg))
                )
            );
        });
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        OpenRouterService.getInstance().shutdown();
    }
}
```

- [ ] **Step 2: Verify the project compiles**

```bash
./gradlew build
```

Expected: BUILD SUCCESSFUL, JAR produced in `build/libs/`.

- [ ] **Step 3: Run the full test suite one more time**

```bash
./gradlew test
```

Expected: All 12 tests PASS.

- [ ] **Step 4: Commit**

```bash
git add src/main/java/io/github/senseidragon/dragontweaksv2/DragonTweaksV2.java
git commit -m "feat: wire OpenRouterService into server start/stop events"
```

---

## Manual Verification Checklist

Run `./gradlew runClient` with `.env` and `model_config.json` present in `run/client/`:

- [ ] DEBUG log shows `OpenRouter init started`
- [ ] DEBUG log shows `API key present: true`
- [ ] DEBUG log shows `Selected models — flavor: ..., advisory: ...`
- [ ] DEBUG log shows `API key validated successfully`
- [ ] DEBUG log shows `Model primed: <flavor-id>`
- [ ] DEBUG log shows `Model primed: <advisory-id>`
- [ ] DEBUG log shows `OpenRouter ready. flavor=..., advisory=...`
- [ ] API key value does not appear anywhere in logs

**Failure path verification** — rename `.env` to `.env.bak`, relaunch:
- [ ] Player receives chat message: `[DragonTweaks] AI advisor unavailable — .env file not found.`
- [ ] DEBUG log shows failure reason (no key value)
