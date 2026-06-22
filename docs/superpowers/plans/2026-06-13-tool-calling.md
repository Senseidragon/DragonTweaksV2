# Advisor Tool-Calling Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [x]`) syntax for tracking.

**Goal:** Replace the advisor's push-all-context approach with tool-calling: the model requests inventory and world-scan data on demand, lore is auto-injected before each query, and a status monitor proactively notifies the advisor when detrimental effects are applied.

**Architecture:** A new `ToolCallOrchestrator` manages the 2-round-trip HTTP protocol, tool dispatch to the server thread, and history inclusion decisions. `AdvisorChatHandler` delegates to the orchestrator instead of calling `OpenRouterService` directly. A server-side `AdvisorEntity` anchors all world queries to the player's position.

**Tech Stack:** NeoForge 1.21.1, Java 21, Gson (bundled), JUnit 5 + Mockito (tests), OpenRouter tool-calling API (OpenAI-compatible format).

**Spec corrections:** The spec lists `AdvisorSession` and `EnvironmentContextBuilder` as "Unchanged" — both require modification. `AdvisorSession` gains `notifiedEffects`. `EnvironmentContextBuilder` gains hunger state translation.

**Read before editing any class:** Check current method signatures in the file before adding to it. The plan shows the intended shape; adapt to what already exists.

---

## File Map

### New files
| File | Responsibility |
|---|---|
| `...advisor/model/ToolCall.java` | Record: tool call ID, name, parsed args |
| `...advisor/model/ToolResult.java` | Record: tool call ID, result string |
| `...advisor/model/OpenRouterResponse.java` | Record: text content OR list of tool calls |
| `...advisor/AdvisorTool.java` | Interface: `name()`, `definition()`, `execute()` |
| `...advisor/tools/InventoryTool.java` | Implements `get_inventory()` |
| `...advisor/tools/ScanAreaTool.java` | Implements `scan_area(radius, depth, detectOres)` |
| `...advisor/LoreIndex.java` | Classpath lore loader + keyword index |
| `...advisor/ToolCallOrchestrator.java` | 2-round-trip protocol, history decision, tool dispatch |
| `...advisor/AdvisorStatusMonitor.java` | NeoForge event handler, circuit breaker |
| `...advisor/AdvisorEntity.java` | Invisible, non-targetable, player-bonded server entity |
| `...advisor/AdvisorEntityManager.java` | Spawn/despawn lifecycle, player-to-entity map |
| `src/main/resources/data/dragontweaksv2/lore/hunger.md` | Lore: food and hunger |
| `src/main/resources/data/dragontweaksv2/lore/poison.md` | Lore: Poison effect |
| `src/main/resources/data/dragontweaksv2/lore/wither.md` | Lore: Wither effect |
| `src/main/resources/data/dragontweaksv2/lore/enderman.md` | Lore: Endermen |
| `src/main/resources/data/dragontweaksv2/lore/cave_spider.md` | Lore: Cave spiders |
| `src/main/resources/data/dragontweaksv2/lore/fire.md` | Lore: Fire and lava |

### Modified files
| File | Change |
|---|---|
| `...advisor/AdvisorSession.java` | Add `notifiedEffects: Set<ResourceLocation>` |
| `...advisor/EnvironmentContextBuilder.java` | Add hunger state (calibrated language) |
| `...advisor/AdvisorChatHandler.java` | Delegate to `ToolCallOrchestrator` |
| `...openrouter/OpenRouterService.java` | Add `sendWithTools()`, `sendWithToolResults()`, `probeContextRetention()` |
| `...DragonTweaksV2.java` | Register entity type, `AdvisorStatusMonitor`, `AdvisorEntityManager` events |

### Test files
| File | Tests |
|---|---|
| `...advisor/LoreIndexTest.java` | Keyword match, no-match, case-insensitive, multi-keyword |
| `...advisor/ToolCallOrchestratorTest.java` | Text path, tool path, history decision, edge cases |
| `...advisor/AdvisorStatusMonitorTest.java` | Effect events, circuit breaker |
| `...openrouter/CapabilityProbeTest.java` | Probe true/false outcomes |

---

## Phase 1 — Model Types

### Task 1: ToolCall, ToolResult, OpenRouterResponse records

**Files:**
- Create: `src/main/java/io/github/senseidragon/dragontweaksv2/advisor/model/ToolCall.java`
- Create: `src/main/java/io/github/senseidragon/dragontweaksv2/advisor/model/ToolResult.java`
- Create: `src/main/java/io/github/senseidragon/dragontweaksv2/advisor/model/OpenRouterResponse.java`

No tests needed — pure data records.

- [x] **Create ToolCall.java**

```java
package io.github.senseidragon.dragontweaksv2.advisor.model;

import com.google.gson.JsonObject;

public record ToolCall(String id, String name, JsonObject args) {}
```

- [x] **Create ToolResult.java**

```java
package io.github.senseidragon.dragontweaksv2.advisor.model;

public record ToolResult(String toolCallId, String content) {}
```

- [x] **Create OpenRouterResponse.java**

```java
package io.github.senseidragon.dragontweaksv2.advisor.model;

import java.util.List;

public record OpenRouterResponse(String textContent, List<ToolCall> toolCalls) {
    public boolean hasToolCalls() {
        return toolCalls != null && !toolCalls.isEmpty();
    }
}
```

- [x] **Run tests to confirm nothing broken**

```
./gradlew test
```
Expected: BUILD SUCCESSFUL

- [x] **Commit**

```
git add src/main/java/io/github/senseidragon/dragontweaksv2/advisor/model/
git commit -m "feat: add ToolCall, ToolResult, OpenRouterResponse model types"
```

---

### Task 2: AdvisorTool interface

**Files:**
- Create: `src/main/java/io/github/senseidragon/dragontweaksv2/advisor/AdvisorTool.java`

- [x] **Create AdvisorTool.java**

```java
package io.github.senseidragon.dragontweaksv2.advisor;

import com.google.gson.JsonObject;
import io.github.senseidragon.dragontweaksv2.advisor.model.ToolResult;
import net.minecraft.server.level.ServerPlayer;

public interface AdvisorTool {
    String name();
    JsonObject definition();
    String execute(JsonObject args, ServerPlayer player);
}
```

- [x] **Run tests**

```
./gradlew test
```
Expected: BUILD SUCCESSFUL

- [x] **Commit**

```
git add src/main/java/io/github/senseidragon/dragontweaksv2/advisor/AdvisorTool.java
git commit -m "feat: add AdvisorTool interface"
```

---

## Phase 2 — LoreIndex

### Task 3: LoreIndex implementation

**Files:**
- Create: `src/main/java/io/github/senseidragon/dragontweaksv2/advisor/LoreIndex.java`
- Create: `src/test/java/io/github/senseidragon/dragontweaksv2/advisor/LoreIndexTest.java`

- [x] **Write the failing tests first**

```java
package io.github.senseidragon.dragontweaksv2.advisor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class LoreIndexTest {
    static LoreIndex index;

    @BeforeAll
    static void load() {
        index = LoreIndex.loadFromClasspath();
    }

    @Test
    void queryMatchesSingleKeyword() {
        List<String> results = index.query("enderman attacked me");
        assertFalse(results.isEmpty(), "Expected enderman lore to match");
    }

    @Test
    void queryNoMatchReturnsEmpty() {
        List<String> results = index.query("xyzzy foobar baz");
        assertTrue(results.isEmpty());
    }

    @Test
    void queryCaseInsensitive() {
        List<String> lower = index.query("enderman");
        List<String> upper = index.query("ENDERMAN");
        assertEquals(lower.size(), upper.size());
    }

    @Test
    void queryMultipleKeywordsReturnsDedupedResults() {
        List<String> results = index.query("poison cave spider bite");
        // both poison and cave_spider lore share no overlap — should return both without duplication
        long distinctContents = results.stream().distinct().count();
        assertEquals(results.size(), distinctContents, "Duplicate lore entries returned");
    }
}
```

- [x] **Run to confirm failure**

```
./gradlew test --tests "io.github.senseidragon.dragontweaksv2.advisor.LoreIndexTest"
```
Expected: FAIL — `LoreIndex` does not exist yet.

- [x] **Create LoreIndex.java**

```java
package io.github.senseidragon.dragontweaksv2.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LoreIndex {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoreIndex.class);
    private static final String LORE_PATH = "data/dragontweaksv2/lore/";

    // keyword (lowercase) → lore file content
    private final Map<String, String> keywordToContent = new HashMap<>();

    private LoreIndex() {}

    public static LoreIndex loadFromClasspath() {
        LoreIndex index = new LoreIndex();
        try {
            Enumeration<URL> resources = LoreIndex.class.getClassLoader().getResources(LORE_PATH);
            // loadFromClasspath discovers lore files via the classpath manifest file approach
            // since getResources on a directory may not enumerate contents in a JAR,
            // we use a manifest index file: data/dragontweaksv2/lore/index.txt
            InputStream manifest = LoreIndex.class.getClassLoader()
                .getResourceAsStream(LORE_PATH + "index.txt");
            if (manifest == null) {
                LOGGER.warn("[LoreIndex] No lore index file found at {}", LORE_PATH + "index.txt");
                return index;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(manifest, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.strip();
                    if (line.isEmpty()) continue;
                    index.loadFile(LORE_PATH + line);
                }
            }
        } catch (IOException e) {
            LOGGER.error("[LoreIndex] Failed to load lore index", e);
        }
        LOGGER.info("[LoreIndex] Loaded {} keyword entries", index.keywordToContent.size());
        return index;
    }

    private void loadFile(String path) {
        try (InputStream is = LoreIndex.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                LOGGER.warn("[LoreIndex] Lore file not found: {}", path);
                return;
            }
            String raw = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            parseFrontmatter(raw);
        } catch (IOException e) {
            LOGGER.warn("[LoreIndex] Failed to read lore file: {}", path, e);
        }
    }

    private void parseFrontmatter(String raw) {
        // Format: ---\nkeywords: [kw1, kw2]\n---\n<content>
        if (!raw.startsWith("---")) return;
        int secondDash = raw.indexOf("---", 3);
        if (secondDash < 0) return;
        String frontmatter = raw.substring(3, secondDash).strip();
        String content = raw.substring(secondDash + 3).strip();
        for (String fmLine : frontmatter.split("\n")) {
            fmLine = fmLine.strip();
            if (!fmLine.startsWith("keywords:")) continue;
            String listPart = fmLine.substring(fmLine.indexOf('[') + 1, fmLine.lastIndexOf(']'));
            for (String kw : listPart.split(",")) {
                keywordToContent.put(kw.strip().toLowerCase(), content);
            }
        }
    }

    public List<String> query(String playerMessage) {
        String lower = playerMessage.toLowerCase();
        Set<String> seen = new LinkedHashSet<>();
        for (Map.Entry<String, String> entry : keywordToContent.entrySet()) {
            if (lower.contains(entry.getKey())) {
                seen.add(entry.getValue());
            }
        }
        return new ArrayList<>(seen);
    }
}
```

- [x] **Create lore index manifest** at `src/main/resources/data/dragontweaksv2/lore/index.txt`

```
hunger.md
poison.md
wither.md
enderman.md
cave_spider.md
fire.md
```

- [x] **Create starter lore files** (see Task 4 for full content — create empty stubs now so tests compile)

Create `src/main/resources/data/dragontweaksv2/lore/enderman.md`:
```
---
keywords: [enderman, endermen, ender]
---
Endermen are tall neutral mobs that become hostile if you look directly at their face. Wearing a carved pumpkin prevents eye contact entirely. Water damages Endermen severely — lead them into rain or a body of water. They teleport to avoid projectiles and water, so melee near water is effective. Endermen drop Ender Pearls used for crafting Eyes of Ender and teleporting.
```

Create `src/main/resources/data/dragontweaksv2/lore/poison.md`:
```
---
keywords: [poison, poisoned, cave spider, spider bite, witch]
---
Poison drains health over time but cannot kill you — it stops at 1 health point. Sources include cave spiders, witches, and certain foods like spider eyes. Milk cures all status effects including Poison immediately. Honey Bottles specifically cure Poison and are stackable. If you have neither, wait — Poison is not lethal on its own.
```

Create `src/main/resources/data/dragontweaksv2/lore/wither.md`:
```
---
keywords: [wither, wither effect, wither skeleton]
---
The Wither effect deals damage over time and unlike Poison CAN kill you — it has no floor. It is caused by Wither Skeletons and the Wither boss. Milk cures it immediately. There is no potion to cure Wither directly; Milk is the primary counter. Keep your health high if fighting Wither Skeletons.
```

Create `src/main/resources/data/dragontweaksv2/lore/hunger.md`:
```
---
keywords: [hunger, food, hungry, starving, eat, eating, rotten flesh, bread]
---
Maintaining food is essential — below half hunger you cannot sprint, and at empty you take starvation damage. Cooked meats, bread, and golden carrots are the most efficient foods. Rotten flesh from zombies is edible but causes the Hunger effect which drains your food bar faster — only eat it in emergencies. Chorus Fruit teleports you but also restores food. Always carry food in your hotbar.
```

Create `src/main/resources/data/dragontweaksv2/lore/cave_spider.md`:
```
---
keywords: [cave spider, cave spiders, spider, spiders, mineshaft, cobweb]
---
Cave spiders are small venomous spiders found in mineshafts surrounded by cobwebs. Their bite inflicts the Poison effect. They can fit through 1x1 gaps. Milk cures Poison. Honey Bottles also cure Poison. Shears can quickly clear cobwebs. On Hard difficulty their Poison lasts longer — prioritize killing them before they can bite.
```

Create `src/main/resources/data/dragontweaksv2/lore/fire.md`:
```
---
keywords: [fire, lava, burning, flame, on fire, fire resistance]
---
Fire and lava deal continuous damage while you are in contact. Fire Resistance potions provide complete immunity to fire and lava damage. Without Fire Resistance, get out of the fire source immediately — you continue burning briefly after leaving. Water extinguishes fire on you. Netherite gear does not burn in lava but you still take damage without Fire Resistance. Keep a Fire Resistance potion when exploring the Nether.
```

- [x] **Run tests**

```
./gradlew test --tests "io.github.senseidragon.dragontweaksv2.advisor.LoreIndexTest"
```
Expected: 4 tests PASS

- [x] **Run all tests**

```
./gradlew test
```
Expected: BUILD SUCCESSFUL

- [x] **Append to test-audit-trail.md**

```
## 2026-06-13 — LoreIndex
Changed: LoreIndex classpath loader + keyword index + 6 starter lore files
Tests: LoreIndexTest (4 unit tests) — PASS via ./gradlew test
```

- [x] **Commit**

```
git add src/main/java/io/github/senseidragon/dragontweaksv2/advisor/LoreIndex.java
git add src/test/java/io/github/senseidragon/dragontweaksv2/advisor/LoreIndexTest.java
git add src/main/resources/data/dragontweaksv2/lore/
git commit -m "feat: add LoreIndex with classpath lore loader and 6 starter lore files"
```

---

## Phase 3 — Spec Corrections (AdvisorSession + EnvironmentContextBuilder)

### Task 4: Add notifiedEffects to AdvisorSession

> **Spec note:** The spec lists `AdvisorSession` as "Unchanged" but the `AdvisorStatusMonitor` design requires `Set<ResourceLocation> notifiedEffects` to live here. This task corrects that.

**Files:**
- Modify: `src/main/java/io/github/senseidragon/dragontweaksv2/advisor/AdvisorSession.java`

- [x] **Read AdvisorSession.java to understand its current shape before editing**

- [x] **Add notifiedEffects field and accessors**

Add to the class body (exact position depends on what's there):
```java
import net.minecraft.resources.ResourceLocation;
import java.util.HashSet;
import java.util.Set;

// Inside AdvisorSession class:
private final Set<ResourceLocation> notifiedEffects = new HashSet<>();

public boolean hasBeenNotified(ResourceLocation effectId) {
    return notifiedEffects.contains(effectId);
}

public void markNotified(ResourceLocation effectId) {
    notifiedEffects.add(effectId);
}

public void clearNotified(ResourceLocation effectId) {
    notifiedEffects.remove(effectId);
}
```

- [x] **Run tests**

```
./gradlew test
```
Expected: BUILD SUCCESSFUL

- [x] **Append to test-audit-trail.md**

```
## 2026-06-13 — AdvisorSession.notifiedEffects
Changed: Added notifiedEffects Set<ResourceLocation> with hasBeenNotified/markNotified/clearNotified
Tests: No unit test possible without Minecraft runtime (ResourceLocation is a Minecraft class). Covered by AdvisorStatusMonitor integration. — DEFERRED
```

- [x] **Commit**

```
git add src/main/java/io/github/senseidragon/dragontweaksv2/advisor/AdvisorSession.java
git commit -m "feat: add notifiedEffects tracking to AdvisorSession"
```

---

### Task 5: Add hunger state to EnvironmentContextBuilder

> **Spec note:** The spec lists `EnvironmentContextBuilder` as "Unchanged" but the baseline context spec requires hunger state expressed as calibrated language. This task corrects that.

**Files:**
- Modify: `src/main/java/io/github/senseidragon/dragontweaksv2/advisor/EnvironmentContextBuilder.java`

- [x] **Read EnvironmentContextBuilder.java to understand its current shape before editing**

- [x] **Add hunger state translation method**

```java
// Add this method to EnvironmentContextBuilder:
private static String hungerState(int foodLevel) {
    if (foodLevel >= 18) return "Sated";
    if (foodLevel >= 13) return "Peckish";
    if (foodLevel >= 8)  return "Hungry";
    if (foodLevel >= 3)  return "Very Hungry";
    return "Starving";
}
```

- [x] **Inject hunger state into the context string**

Find where the context string is built (likely a StringBuilder or string concatenation). Add a line like:

```java
// Inside wherever the context string is assembled, after existing fields:
context.append("Hunger: ").append(hungerState(player.getFoodData().getFoodLevel())).append("\n");
```

- [x] **Run tests**

```
./gradlew test
```
Expected: BUILD SUCCESSFUL

- [x] **Append to test-audit-trail.md**

```
## 2026-06-13 — EnvironmentContextBuilder hunger state
Changed: Added hunger state calibrated language injection (Sated/Peckish/Hungry/Very Hungry/Starving)
Tests: No isolated unit test — covered by AdvisorPromptIntegrationTest if it exercises context building. Tier 3 live verification required.
```

- [x] **Commit**

```
git add src/main/java/io/github/senseidragon/dragontweaksv2/advisor/EnvironmentContextBuilder.java
git commit -m "feat: inject hunger state as calibrated language into advisor context"
```

---

## Phase 4 — OpenRouterService Extensions

### Task 6: Add tool-calling support to OpenRouterService

**Files:**
- Modify: `src/main/java/io/github/senseidragon/dragontweaksv2/openrouter/OpenRouterService.java`
- Modify: `src/test/java/io/github/senseidragon/dragontweaksv2/openrouter/OpenRouterServiceTest.java`

- [x] **Read OpenRouterService.java to understand current HTTP request construction and response parsing before editing**

- [x] **Write failing tests**

```java
// Add to OpenRouterServiceTest.java:

@Test
void sendWithToolsParsesPureTextResponse() throws Exception {
    // Arrange: mock HTTP to return a response with no tool_calls
    String fakeResponse = """
        {"choices":[{"message":{"role":"assistant","content":"Hello there!","tool_calls":null},"finish_reason":"stop"}]}
        """;
    // (Wire the mock HTTP client to return fakeResponse — match existing test pattern in this file)

    List<JsonObject> tools = List.of(); // no tools needed for this test
    OpenRouterResponse response = service.sendWithTools("sys", List.of(), "hi", tools).get(5, SECONDS);

    assertFalse(response.hasToolCalls());
    assertEquals("Hello there!", response.textContent());
}

@Test
void sendWithToolsParsesToolCallResponse() throws Exception {
    String fakeResponse = """
        {"choices":[{"message":{"role":"assistant","content":null,
        "tool_calls":[{"id":"call_1","type":"function","function":{"name":"get_inventory","arguments":"{}"}}]},
        "finish_reason":"tool_calls"}]}
        """;
    // (Wire mock HTTP to return fakeResponse)

    List<JsonObject> tools = List.of(inventoryToolDefinition());
    OpenRouterResponse response = service.sendWithTools("sys", List.of(), "what do I have?", tools).get(5, SECONDS);

    assertTrue(response.hasToolCalls());
    assertEquals("get_inventory", response.toolCalls().get(0).name());
}

private JsonObject inventoryToolDefinition() {
    JsonObject fn = new JsonObject();
    fn.addProperty("name", "get_inventory");
    fn.addProperty("description", "Returns player inventory contents");
    fn.add("parameters", new JsonObject());
    JsonObject tool = new JsonObject();
    tool.addProperty("type", "function");
    tool.add("function", fn);
    return tool;
}
```

- [x] **Run to confirm failure**

```
./gradlew test --tests "io.github.senseidragon.dragontweaksv2.openrouter.OpenRouterServiceTest"
```
Expected: FAIL — `sendWithTools` does not exist.

- [x] **Add sendWithTools() to OpenRouterService**

Add this method. The request body format follows OpenAI/OpenRouter tool-calling spec:

```java
import io.github.senseidragon.dragontweaksv2.advisor.model.OpenRouterResponse;
import io.github.senseidragon.dragontweaksv2.advisor.model.ToolCall;
import com.google.gson.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public CompletableFuture<OpenRouterResponse> sendWithTools(
        String systemPrompt,
        List<ChatMessage> history,
        String userMessage,
        List<JsonObject> toolDefinitions) {

    return CompletableFuture.supplyAsync(() -> {
        JsonObject body = buildRequestBody(systemPrompt, history, userMessage);
        if (!toolDefinitions.isEmpty()) {
            JsonArray toolsArray = new JsonArray();
            toolDefinitions.forEach(toolsArray::add);
            body.add("tools", toolsArray);
            body.addProperty("tool_choice", "auto");
        }

        String rawResponse = sendHttpRequest(body); // reuse existing HTTP send method
        return parseOpenRouterResponse(rawResponse);
    }, executor); // reuse existing executor field
}

private OpenRouterResponse parseOpenRouterResponse(String rawJson) {
    JsonObject root = JsonParser.parseString(rawJson).getAsJsonObject();
    JsonObject message = root.getAsJsonArray("choices")
        .get(0).getAsJsonObject()
        .getAsJsonObject("message");

    JsonElement toolCallsEl = message.get("tool_calls");
    if (toolCallsEl != null && !toolCallsEl.isJsonNull()) {
        List<ToolCall> calls = new ArrayList<>();
        for (JsonElement el : toolCallsEl.getAsJsonArray()) {
            JsonObject tc = el.getAsJsonObject();
            String id = tc.get("id").getAsString();
            JsonObject fn = tc.getAsJsonObject("function");
            String name = fn.get("name").getAsString();
            JsonObject args = JsonParser.parseString(fn.get("arguments").getAsString()).getAsJsonObject();
            calls.add(new ToolCall(id, name, args));
        }
        return new OpenRouterResponse(null, calls);
    }

    String text = message.get("content").getAsString();
    return new OpenRouterResponse(text, List.of());
}
```

- [x] **Add sendWithToolResults() to OpenRouterService**

This method reconstructs the full message sequence and makes round trip 2:

```java
import io.github.senseidragon.dragontweaksv2.advisor.model.ToolResult;

public CompletableFuture<String> sendWithToolResults(
        String systemPrompt,
        List<ChatMessage> history,
        String userMessage,
        List<ToolCall> priorToolCalls,
        List<ToolResult> results,
        List<JsonObject> toolDefinitions) {

    return CompletableFuture.supplyAsync(() -> {
        JsonObject body = buildRequestBody(systemPrompt, history, userMessage);

        // Append the assistant's tool-call message
        JsonArray messages = body.getAsJsonArray("messages");

        JsonArray callsArray = new JsonArray();
        for (ToolCall tc : priorToolCalls) {
            JsonObject fn = new JsonObject();
            fn.addProperty("name", tc.name());
            fn.addProperty("arguments", tc.args().toString());
            JsonObject call = new JsonObject();
            call.addProperty("id", tc.id());
            call.addProperty("type", "function");
            call.add("function", fn);
            callsArray.add(call);
        }
        JsonObject assistantMsg = new JsonObject();
        assistantMsg.addProperty("role", "assistant");
        assistantMsg.add("content", JsonNull.INSTANCE);
        assistantMsg.add("tool_calls", callsArray);
        messages.add(assistantMsg);

        // Append tool result messages
        for (ToolResult result : results) {
            JsonObject toolMsg = new JsonObject();
            toolMsg.addProperty("role", "tool");
            toolMsg.addProperty("tool_call_id", result.toolCallId());
            toolMsg.addProperty("content", result.content());
            messages.add(toolMsg);
        }

        if (!toolDefinitions.isEmpty()) {
            JsonArray toolsArray = new JsonArray();
            toolDefinitions.forEach(toolsArray::add);
            body.add("tools", toolsArray);
        }

        String rawResponse = sendHttpRequest(body);
        return parseOpenRouterResponse(rawResponse).textContent();
    }, executor);
}
```

- [x] **Run tests**

```
./gradlew test --tests "io.github.senseidragon.dragontweaksv2.openrouter.OpenRouterServiceTest"
```
Expected: PASS

- [x] **Run all tests**

```
./gradlew test
```
Expected: BUILD SUCCESSFUL

- [x] **Append to test-audit-trail.md**

```
## 2026-06-13 — OpenRouterService tool-calling
Changed: Added sendWithTools(), sendWithToolResults(), parseOpenRouterResponse()
Tests: OpenRouterServiceTest — sendWithToolsParsesPureTextResponse, sendWithToolsParsesToolCallResponse — PASS via ./gradlew test
```

- [x] **Commit**

```
git add src/main/java/io/github/senseidragon/dragontweaksv2/openrouter/OpenRouterService.java
git add src/test/java/io/github/senseidragon/dragontweaksv2/openrouter/OpenRouterServiceTest.java
git commit -m "feat: add tool-calling support to OpenRouterService (sendWithTools, sendWithToolResults)"
```

---

### Task 7: Capability probe

**Files:**
- Modify: `src/main/java/io/github/senseidragon/dragontweaksv2/openrouter/OpenRouterService.java`
- Create: `src/test/java/io/github/senseidragon/dragontweaksv2/openrouter/CapabilityProbeTest.java`

- [x] **Write failing tests**

```java
package io.github.senseidragon.dragontweaksv2.openrouter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CapabilityProbeTest {

    @Test
    void probeReturnsTrueWhenModelReferencesApple() {
        // Arrange: stub sendHttpRequest to return apple-referencing response for call 2
        // (match the pattern used in OpenRouterServiceTest for HTTP mocking)
        // Call 1 response: anything
        // Call 2 response: {"choices":[{"message":{"content":"You are holding an apple."}}]}

        // Act
        boolean retains = service.probeContextRetention();

        // Assert
        assertTrue(retains);
    }

    @Test
    void probeReturnsFalseWhenModelDoesNotReferenceApple() {
        // Call 2 response: {"choices":[{"message":{"content":"I don't know what you are holding."}}]}

        boolean retains = service.probeContextRetention();

        assertFalse(retains);
    }
}
```

- [x] **Run to confirm failure**

```
./gradlew test --tests "io.github.senseidragon.dragontweaksv2.openrouter.CapabilityProbeTest"
```
Expected: FAIL

- [x] **Add probeContextRetention() to OpenRouterService**

```java
public boolean probeContextRetention() {
    try {
        // Call 1: state a fact with no system prompt and no history
        JsonObject body1 = new JsonObject();
        body1.addProperty("model", getModelId()); // reuse existing model accessor
        JsonArray msgs1 = new JsonArray();
        JsonObject u1 = new JsonObject();
        u1.addProperty("role", "user");
        u1.addProperty("content", "I have an apple in my left hand.");
        msgs1.add(u1);
        body1.add("messages", msgs1);
        sendHttpRequest(body1); // response ignored

        // Call 2: ask about it, still no history
        JsonObject body2 = new JsonObject();
        body2.addProperty("model", getModelId());
        JsonArray msgs2 = new JsonArray();
        JsonObject u2 = new JsonObject();
        u2.addProperty("role", "user");
        u2.addProperty("content", "What am I holding?");
        msgs2.add(u2);
        body2.add("messages", msgs2);
        String response = sendHttpRequest(body2);

        JsonObject root = JsonParser.parseString(response).getAsJsonObject();
        String content = root.getAsJsonArray("choices")
            .get(0).getAsJsonObject()
            .getAsJsonObject("message")
            .get("content").getAsString().toLowerCase();

        return content.contains("apple");
    } catch (Exception e) {
        LOGGER.warn("[OpenRouterService] Capability probe failed, defaulting to false", e);
        return false;
    }
}
```

- [x] **Run tests**

```
./gradlew test --tests "io.github.senseidragon.dragontweaksv2.openrouter.CapabilityProbeTest"
```
Expected: PASS

- [x] **Run all tests**

```
./gradlew test
```
Expected: BUILD SUCCESSFUL

- [x] **Append to test-audit-trail.md**

```
## 2026-06-13 — Capability probe
Changed: Added probeContextRetention() to OpenRouterService
Tests: CapabilityProbeTest — probeReturnsTrueWhenModelReferencesApple, probeReturnsFalseWhenModelDoesNotReferenceApple — PASS
```

- [x] **Commit**

```
git add src/main/java/io/github/senseidragon/dragontweaksv2/openrouter/OpenRouterService.java
git add src/test/java/io/github/senseidragon/dragontweaksv2/openrouter/CapabilityProbeTest.java
git commit -m "feat: add context retention capability probe to OpenRouterService"
```

---

## Phase 5 — Tool Implementations

### Task 8: InventoryTool

**Files:**
- Create: `src/main/java/io/github/senseidragon/dragontweaksv2/advisor/tools/InventoryTool.java`

Note: Tier 2 game test required (`runGameTestServer`) — not part of `./gradlew test`.

- [x] **Create InventoryTool.java**

```java
package io.github.senseidragon.dragontweaksv2.advisor.tools;

import com.google.gson.JsonObject;
import io.github.senseidragon.dragontweaksv2.advisor.AdvisorTool;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import java.util.ArrayList;
import java.util.List;

public class InventoryTool implements AdvisorTool {

    @Override
    public String name() { return "get_inventory"; }

    @Override
    public JsonObject definition() {
        JsonObject fn = new JsonObject();
        fn.addProperty("name", "get_inventory");
        fn.addProperty("description",
            "Returns the player's current inventory including armor, held items, and all 36 inventory slots.");
        fn.add("parameters", new JsonObject()); // no parameters

        JsonObject tool = new JsonObject();
        tool.addProperty("type", "function");
        tool.add("function", fn);
        return tool;
    }

    @Override
    public String execute(JsonObject args, ServerPlayer player) {
        try {
            List<String> lines = new ArrayList<>();

            // Armor slots (head, chest, legs, feet)
            for (ItemStack stack : player.getArmorSlots()) {
                if (!stack.isEmpty()) lines.add(format(stack));
            }

            // Main hand and off hand
            addIfPresent(lines, player.getMainHandItem());
            addIfPresent(lines, player.getOffhandItem());

            // Main 36-slot inventory
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (!stack.isEmpty()) lines.add(format(stack));
            }

            return lines.isEmpty() ? "Inventory is empty." : String.join("\n", lines);
        } catch (Exception e) {
            return "[Tool error: inventory unavailable]";
        }
    }

    private void addIfPresent(List<String> lines, ItemStack stack) {
        if (!stack.isEmpty()) lines.add(format(stack));
    }

    private String format(ItemStack stack) {
        String name = stack.getHoverName().getString();
        boolean enchanted = stack.isEnchanted();
        int count = stack.getCount();
        return (enchanted ? "Enchanted " : "") + name + " x" + count;
    }
}
```

- [x] **Run tests** (compile check only — Tier 2 game test deferred)

```
./gradlew test
```
Expected: BUILD SUCCESSFUL

- [x] **Append to test-audit-trail.md**

```
## 2026-06-13 — InventoryTool
Changed: InventoryTool implementing get_inventory()
Tests: Unit test not possible without Minecraft runtime. Tier 2 game test required (runGameTestServer). — DEFERRED
```

- [x] **Commit**

```
git add src/main/java/io/github/senseidragon/dragontweaksv2/advisor/tools/InventoryTool.java
git commit -m "feat: add InventoryTool implementing get_inventory()"
```

---

### Task 9: ScanAreaTool — surface scan

**Files:**
- Create: `src/main/java/io/github/senseidragon/dragontweaksv2/advisor/tools/ScanAreaTool.java`

- [x] **Create ScanAreaTool.java with surface/entity scan**

```java
package io.github.senseidragon.dragontweaksv2.advisor.tools;

import com.google.gson.JsonObject;
import io.github.senseidragon.dragontweaksv2.advisor.AdvisorEntity;
import io.github.senseidragon.dragontweaksv2.advisor.AdvisorEntityManager;
import io.github.senseidragon.dragontweaksv2.advisor.AdvisorTool;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import java.util.*;

public class ScanAreaTool implements AdvisorTool {

    private static final int DEFAULT_RADIUS = 16;
    private static final int DEFAULT_DEPTH  = 4;

    @Override
    public String name() { return "scan_area"; }

    @Override
    public JsonObject definition() {
        JsonObject params = new JsonObject();
        JsonObject props = new JsonObject();

        JsonObject radius = new JsonObject();
        radius.addProperty("type", "integer");
        radius.addProperty("description", "Block radius to scan from player position. Default: 16.");
        props.add("radius", radius);

        JsonObject depth = new JsonObject();
        depth.addProperty("type", "integer");
        depth.addProperty("description", "Y levels to scan downward from player Y+3. Default: 4 (surface scan).");
        props.add("depth", depth);

        JsonObject detectOres = new JsonObject();
        detectOres.addProperty("type", "boolean");
        detectOres.addProperty("description", "If true, report ore types found on exposed void surfaces.");
        props.add("detectOres", detectOres);

        params.addProperty("type", "object");
        params.add("properties", props);

        JsonObject fn = new JsonObject();
        fn.addProperty("name", "scan_area");
        fn.addProperty("description",
            "Scans the area around the advisor for entities and underground voids. " +
            "Returns nearby mobs and, if underground, cave/tunnel classifications with direction.");
        fn.add("parameters", params);

        JsonObject tool = new JsonObject();
        tool.addProperty("type", "function");
        tool.add("function", fn);
        return tool;
    }

    @Override
    public String execute(JsonObject args, ServerPlayer player) {
        try {
            int radius = args.has("radius") ? args.get("radius").getAsInt() : DEFAULT_RADIUS;
            int depth  = args.has("depth")  ? args.get("depth").getAsInt()  : DEFAULT_DEPTH;
            boolean detectOres = args.has("detectOres") && args.get("detectOres").getAsBoolean();

            // Use advisor entity as query origin (always co-located with player)
            Entity origin = AdvisorEntityManager.getEntity(player).map(e -> (Entity) e).orElse(player);

            List<String> results = new ArrayList<>();
            results.addAll(scanEntities(origin, radius, player));

            if (depth > 4) {
                results.addAll(scanUnderground(origin, radius, depth, detectOres, player));
            }

            return results.isEmpty() ? "Nothing notable detected nearby." : String.join("\n", results);
        } catch (Exception e) {
            return "[Tool error: scan unavailable]";
        }
    }

    private List<String> scanEntities(Entity origin, int radius, ServerPlayer self) {
        AABB box = AABB.ofSize(origin.position(), radius * 2.0, 16.0, radius * 2.0);
        List<Entity> entities = origin.level().getEntities(origin, box,
            e -> e != self && e != origin && e instanceof LivingEntity);

        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Entity e : entities) {
            String name = e.getType().getDescription().getString();
            counts.merge(name, 1, Integer::sum);
        }

        List<String> lines = new ArrayList<>();
        counts.forEach((name, count) -> lines.add(count + "x " + name + " nearby"));
        return lines;
    }

    private List<String> scanUnderground(Entity origin, int radius, int depth,
                                          boolean detectOres, ServerPlayer player) {
        // Implemented in Task 10
        return List.of();
    }
}
```

- [x] **Run tests**

```
./gradlew test
```
Expected: BUILD SUCCESSFUL

- [x] **Commit**

```
git add src/main/java/io/github/senseidragon/dragontweaksv2/advisor/tools/ScanAreaTool.java
git commit -m "feat: add ScanAreaTool with surface entity scan (underground scan stub)"
```

---

### Task 10: ScanAreaTool — underground block scan + ore detection

**Files:**
- Modify: `src/main/java/io/github/senseidragon/dragontweaksv2/advisor/tools/ScanAreaTool.java`

- [x] **Read the current ScanAreaTool.java to confirm the stub location**

- [x] **Add VoidRegion inner record and underground scan implementation**

Replace the `scanUnderground` stub and add supporting methods:

```java
private record VoidRegion(int volume, String direction, int depthBelowPlayer, Set<Long> blockKeys) {}

private List<String> scanUnderground(Entity origin, int radius, int depth,
                                      boolean detectOres, ServerPlayer player) {
    var level = origin.level();
    int ox = origin.blockX();
    int oy = origin.blockY();
    int oz = origin.blockZ();
    int topY = oy + 3;
    int botY = topY - depth;

    int step = Math.max(1, radius / 8);
    Set<Long> visited = new HashSet<>();
    List<VoidRegion> voids = new ArrayList<>();

    for (int dx = -radius; dx <= radius; dx += step) {
        for (int dz = -radius; dz <= radius; dz += step) {
            for (int dy = topY; dy >= botY; dy--) {
                long key = packPos(ox + dx, dy, oz + dz);
                if (visited.contains(key)) continue;
                var pos = new net.minecraft.core.BlockPos(ox + dx, dy, oz + dz);
                if (!level.getBlockState(pos).isAir()) continue;
                VoidRegion region = floodFill(level, pos, visited, oy);
                if (region.volume() >= 50) { // discard air pockets < 50
                    voids.add(region);
                }
            }
        }
    }

    List<String> lines = new ArrayList<>();
    for (VoidRegion v : voids) {
        lines.add(classifyVoid(v.volume()) + " detected to the " + v.direction()
            + " at depth " + v.depthBelowPlayer() + " blocks");
    }

    if (detectOres && !voids.isEmpty()) {
        Map<String, Integer> oreCounts = new HashMap<>();
        for (VoidRegion v : voids) {
            scanOres(level, v.blockKeys(), oreCounts);
        }
        oreCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(4)
            .forEach(e -> lines.add("Ore detected nearby: " + e.getKey()));
    }

    return lines;
}

private VoidRegion floodFill(net.minecraft.world.level.Level level,
                              net.minecraft.core.BlockPos start,
                              Set<Long> globalVisited, int playerY) {
    final int MAX_VOLUME = 10_000;
    Queue<net.minecraft.core.BlockPos> queue = new ArrayDeque<>();
    Set<Long> regionKeys = new HashSet<>();
    queue.add(start);
    regionKeys.add(packPos(start.getX(), start.getY(), start.getZ()));
    globalVisited.add(packPos(start.getX(), start.getY(), start.getZ()));

    while (!queue.isEmpty() && regionKeys.size() < MAX_VOLUME) {
        var pos = queue.poll();
        for (var dir : net.minecraft.core.Direction.values()) {
            var next = pos.relative(dir);
            long key = packPos(next.getX(), next.getY(), next.getZ());
            if (globalVisited.contains(key)) continue;
            if (!level.getBlockState(next).isAir()) continue;
            globalVisited.add(key);
            regionKeys.add(key);
            queue.add(next);
        }
    }

    int depthBelow = Math.max(0, playerY - start.getY());
    String dir = relativeDirection(start.getX() - playerY, start.getZ() - playerY); // simplified
    return new VoidRegion(regionKeys.size(), dir, depthBelow, regionKeys);
}

private void scanOres(net.minecraft.world.level.Level level,
                       Set<Long> airKeys, Map<String, Integer> oreCounts) {
    for (long key : airKeys) {
        int x = (int)(key >> 40);
        int y = (int)((key >> 20) & 0xFFFFF) - 500_000;
        int z = (int)(key & 0xFFFFF) - 500_000;
        for (var dir : net.minecraft.core.Direction.values()) {
            var adjacent = new net.minecraft.core.BlockPos(x + dir.getStepX(),
                y + dir.getStepY(), z + dir.getStepZ());
            var state = level.getBlockState(adjacent);
            String ore = detectOreType(state);
            if (ore != null) oreCounts.merge(ore, 1, Integer::sum);
        }
    }
}

private String detectOreType(net.minecraft.world.level.block.state.BlockState state) {
    String id = net.minecraft.core.registries.BuiltInRegistries.BLOCK
        .getKey(state.getBlock()).getPath();
    if (id.contains("coal_ore"))     return "Coal";
    if (id.contains("iron_ore"))     return "Iron";
    if (id.contains("gold_ore"))     return "Gold";
    if (id.contains("diamond_ore"))  return "Diamond";
    if (id.contains("emerald_ore"))  return "Emerald";
    if (id.contains("redstone_ore")) return "Redstone";
    if (id.contains("lapis_ore"))    return "Lapis";
    if (id.contains("copper_ore"))   return "Copper";
    return null;
}

private String classifyVoid(int volume) {
    if (volume < 200)  return "Large tunnel";
    if (volume < 500)  return "Small cave";
    if (volume < 1000) return "Dungeon room";
    if (volume < 5000) return "Large cave";
    return "Massive cavern";
}

private String relativeDirection(int dx, int dz) {
    if (Math.abs(dx) > Math.abs(dz)) return dx > 0 ? "east" : "west";
    return dz > 0 ? "south" : "north";
}

private long packPos(int x, int y, int z) {
    // Offset y and z to handle negatives; pack into a long
    return ((long) x << 40) | ((long)(y + 500_000) << 20) | (z + 500_000);
}
```

- [x] **Run tests**

```
./gradlew test
```
Expected: BUILD SUCCESSFUL

- [x] **Append to test-audit-trail.md**

```
## 2026-06-13 — ScanAreaTool underground + ore detection
Changed: ScanAreaTool underground block scan with coarse pass, flood fill, void classification, ore detection
Tests: Unit test not possible without Minecraft runtime. Tier 2 game test required (runGameTestServer). — DEFERRED
```

- [x] **Commit**

```
git add src/main/java/io/github/senseidragon/dragontweaksv2/advisor/tools/ScanAreaTool.java
git commit -m "feat: add underground block scan and ore detection to ScanAreaTool"
```

---

## Phase 6 — Advisor Entity

### Task 11: AdvisorEntity class and registration

**Files:**
- Create: `src/main/java/io/github/senseidragon/dragontweaksv2/advisor/AdvisorEntity.java`
- Modify: `src/main/java/io/github/senseidragon/dragontweaksv2/DragonTweaksV2.java`

- [x] **Read DragonTweaksV2.java to see how existing DeferredRegisters are declared before editing**

- [x] **Create AdvisorEntity.java**

```java
package io.github.senseidragon.dragontweaksv2.advisor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.network.syncher.SynchedEntityData;

public class AdvisorEntity extends Entity {

    public AdvisorEntity(EntityType<?> type, Level level) {
        super(type, level);
        setInvisible(true);
        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        // No synced data — server-side only
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    public boolean isPickable() { return false; }

    @Override
    public boolean isAttackable() { return false; }

    @Override
    public boolean shouldBeSaved() { return false; }
}
```

- [x] **Register entity type in DragonTweaksV2.java**

Add to existing deferred register declarations:
```java
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import io.github.senseidragon.dragontweaksv2.advisor.AdvisorEntity;

public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
    DeferredRegister.create(Registries.ENTITY_TYPE, MOD_ID);

public static final DeferredHolder<EntityType<?>, EntityType<AdvisorEntity>> ADVISOR_ENTITY_TYPE =
    ENTITY_TYPES.register("advisor", () ->
        EntityType.Builder.<AdvisorEntity>of(AdvisorEntity::new, MobCategory.MISC)
            .sized(0.0f, 0.0f)
            .noSave()
            .noSummon()
            .build("advisor"));
```

In the mod constructor, register the deferred register on the mod bus (add alongside existing registers):
```java
ENTITY_TYPES.register(modEventBus);
```

- [x] **Run tests**

```
./gradlew test
```
Expected: BUILD SUCCESSFUL

- [x] **Commit**

```
git add src/main/java/io/github/senseidragon/dragontweaksv2/advisor/AdvisorEntity.java
git add src/main/java/io/github/senseidragon/dragontweaksv2/DragonTweaksV2.java
git commit -m "feat: add AdvisorEntity (invisible, non-targetable, no-save) and register entity type"
```

---

### Task 12: AdvisorEntityManager — spawn/despawn lifecycle

**Files:**
- Create: `src/main/java/io/github/senseidragon/dragontweaksv2/advisor/AdvisorEntityManager.java`
- Modify: `src/main/java/io/github/senseidragon/dragontweaksv2/DragonTweaksV2.java`

- [x] **Create AdvisorEntityManager.java**

```java
package io.github.senseidragon.dragontweaksv2.advisor;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.senseidragon.dragontweaksv2.DragonTweaksV2.ADVISOR_ENTITY_TYPE;

public class AdvisorEntityManager {

    private static final Map<UUID, AdvisorEntity> ACTIVE = new ConcurrentHashMap<>();

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        AdvisorEntity entity = new AdvisorEntity(ADVISOR_ENTITY_TYPE.get(), player.serverLevel());
        entity.setPos(player.position());
        player.serverLevel().addFreshEntity(entity);
        ACTIVE.put(player.getUUID(), entity);
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        AdvisorEntity entity = ACTIVE.remove(player.getUUID());
        if (entity != null) entity.discard();
    }

    public static Optional<AdvisorEntity> getEntity(ServerPlayer player) {
        return Optional.ofNullable(ACTIVE.get(player.getUUID()));
    }

    /** Update entity position to match the player. Call before any world query. */
    public static void syncPosition(ServerPlayer player) {
        getEntity(player).ifPresent(e -> e.setPos(player.position()));
    }
}
```

- [x] **Register AdvisorEntityManager on the game event bus in DragonTweaksV2.java**

```java
// In mod constructor, after existing event bus registrations:
NeoForge.EVENT_BUS.register(new AdvisorEntityManager());
```

- [x] **Run tests**

```
./gradlew test
```
Expected: BUILD SUCCESSFUL

- [x] **Append to test-audit-trail.md**

```
## 2026-06-13 — AdvisorEntityManager lifecycle
Changed: AdvisorEntityManager spawn on login, despawn on logout
Tests: Unit test not possible without Minecraft server runtime. Tier 3 live verification required. — DEFERRED
```

- [x] **Commit**

```
git add src/main/java/io/github/senseidragon/dragontweaksv2/advisor/AdvisorEntityManager.java
git add src/main/java/io/github/senseidragon/dragontweaksv2/DragonTweaksV2.java
git commit -m "feat: add AdvisorEntityManager — spawn on login, despawn on logout"
```

---

## Phase 7 — ToolCallOrchestrator

### Task 13: History decision logic

**Files:**
- Create: `src/main/java/io/github/senseidragon/dragontweaksv2/advisor/ToolCallOrchestrator.java`
- Create: `src/test/java/io/github/senseidragon/dragontweaksv2/advisor/ToolCallOrchestratorTest.java`

- [x] **Write failing tests for history decision**

```java
package io.github.senseidragon.dragontweaksv2.advisor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ToolCallOrchestratorTest {

    private final ToolCallOrchestrator orchestrator = new ToolCallOrchestrator(null, null, null, false);

    @Test
    void followUpSignalIncludesHistory() {
        assertTrue(orchestrator.shouldIncludeHistory("you said I should avoid spiders earlier"));
        assertTrue(orchestrator.shouldIncludeHistory("tell me more about that"));
        assertTrue(orchestrator.shouldIncludeHistory("what about the cave?"));
    }

    @Test
    void pureStateSuppressesHistory() {
        assertFalse(orchestrator.shouldIncludeHistory("what do I have"));
        assertFalse(orchestrator.shouldIncludeHistory("scan the area"));
        assertFalse(orchestrator.shouldIncludeHistory("what's around me"));
    }

    @Test
    void defaultIncludesHistory() {
        assertTrue(orchestrator.shouldIncludeHistory("how do I make a sword?"));
        assertTrue(orchestrator.shouldIncludeHistory("good morning"));
    }
}
```

- [x] **Run to confirm failure**

```
./gradlew test --tests "io.github.senseidragon.dragontweaksv2.advisor.ToolCallOrchestratorTest"
```
Expected: FAIL — class does not exist.

- [x] **Create ToolCallOrchestrator.java with history decision and constructor**

```java
package io.github.senseidragon.dragontweaksv2.advisor;

import io.github.senseidragon.dragontweaksv2.advisor.model.*;
import io.github.senseidragon.dragontweaksv2.openrouter.OpenRouterService;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ToolCallOrchestrator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToolCallOrchestrator.class);
    static final long TOTAL_TIMEOUT_MS = 60_000;
    static final long TOOL_TIMEOUT_MS  = 10_000;

    private final OpenRouterService openRouter;
    private final LoreIndex loreIndex;
    private final List<AdvisorTool> tools;
    private final boolean modelRetainsContext;

    // package-private for testing
    final Map<String, AdvisorTool> registry;

    public ToolCallOrchestrator(OpenRouterService openRouter, LoreIndex loreIndex,
                                 List<AdvisorTool> tools, boolean modelRetainsContext) {
        this.openRouter = openRouter;
        this.loreIndex = loreIndex;
        this.tools = tools != null ? tools : List.of();
        this.modelRetainsContext = modelRetainsContext;
        this.registry = new HashMap<>();
        if (tools != null) tools.forEach(t -> registry.put(t.name(), t));
    }

    // package-private for testing
    boolean shouldIncludeHistory(String playerMessage) {
        String lower = playerMessage.toLowerCase();
        if (lower.contains("you said") || lower.contains("earlier") ||
            lower.contains("what about") || lower.contains("tell me more")) {
            return true;
        }
        if (lower.contains("what do i have") || lower.contains("scan") ||
            lower.contains("what's around") || lower.contains("what is around")) {
            return false;
        }
        return true;
    }

    public List<JsonObject> toolDefinitions() {
        return tools.stream().map(AdvisorTool::definition).collect(Collectors.toList());
    }
}
```

(Add `import com.google.gson.JsonObject;` at the top.)

- [x] **Run tests**

```
./gradlew test --tests "io.github.senseidragon.dragontweaksv2.advisor.ToolCallOrchestratorTest"
```
Expected: 3 tests PASS

- [x] **Run all tests**

```
./gradlew test
```
Expected: BUILD SUCCESSFUL

- [x] **Commit**

```
git add src/main/java/io/github/senseidragon/dragontweaksv2/advisor/ToolCallOrchestrator.java
git add src/test/java/io/github/senseidragon/dragontweaksv2/advisor/ToolCallOrchestratorTest.java
git commit -m "feat: add ToolCallOrchestrator skeleton with history decision logic"
```

---

### Task 14: ToolCallOrchestrator — 2-round-trip protocol and edge cases

**Files:**
- Modify: `src/main/java/io/github/senseidragon/dragontweaksv2/advisor/ToolCallOrchestrator.java`
- Modify: `src/test/java/io/github/senseidragon/dragontweaksv2/advisor/ToolCallOrchestratorTest.java`

- [x] **Add failing tests for the two round-trip paths and edge cases**

```java
// Add to ToolCallOrchestratorTest — requires Mockito mocks for OpenRouterService and ServerPlayer

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import io.github.senseidragon.dragontweaksv2.openrouter.OpenRouterService;
import io.github.senseidragon.dragontweaksv2.advisor.model.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
class ToolCallOrchestratorTest {

    @Mock OpenRouterService openRouter;
    @Mock ServerPlayer player;
    @Mock MinecraftServer server;

    // --- history decision tests remain as written above (no mocks needed) ---

    @Test
    void textOnlyResponsePathDeliveredToPlayer() throws Exception {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(openRouter, stubLore(), List.of(), false);
        when(player.isOnline()).thenReturn(true);
        when(openRouter.sendWithTools(any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(new OpenRouterResponse("Hello!", List.of())));

        List<String> delivered = new ArrayList<>();
        orc.handleQuery("hi", player, stubSession(), delivered::add).get(5, TimeUnit.SECONDS);

        assertEquals(List.of("Hello!"), delivered);
    }

    @Test
    void toolCallPathExecutesToolsAndDeliversFinalResponse() throws Exception {
        AdvisorTool fakeTool = new AdvisorTool() {
            public String name() { return "get_inventory"; }
            public JsonObject definition() { return new JsonObject(); }
            public String execute(JsonObject args, ServerPlayer p) { return "Bread x5"; }
        };

        ToolCallOrchestrator orc = new ToolCallOrchestrator(openRouter, stubLore(), List.of(fakeTool), false);
        when(player.isOnline()).thenReturn(true);
        when(player.getServer()).thenReturn(server);
        doAnswer(inv -> { ((Runnable) inv.getArgument(0)).run(); return null; }).when(server).execute(any());

        ToolCall call = new ToolCall("id1", "get_inventory", new JsonObject());
        when(openRouter.sendWithTools(any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(new OpenRouterResponse(null, List.of(call))));
        when(openRouter.sendWithToolResults(any(), any(), any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture("You have Bread x5."));

        List<String> delivered = new ArrayList<>();
        orc.handleQuery("what do I have?", player, stubSession(), delivered::add).get(5, TimeUnit.SECONDS);

        assertEquals(List.of("You have Bread x5."), delivered);
    }

    @Test
    void unrecognizedToolReturnsErrorString() throws Exception {
        ToolCallOrchestrator orc = new ToolCallOrchestrator(openRouter, stubLore(), List.of(), false);
        when(player.isOnline()).thenReturn(true);
        when(player.getServer()).thenReturn(server);
        doAnswer(inv -> { ((Runnable) inv.getArgument(0)).run(); return null; }).when(server).execute(any());

        ToolCall call = new ToolCall("id1", "unknown_tool", new JsonObject());
        when(openRouter.sendWithTools(any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(new OpenRouterResponse(null, List.of(call))));
        when(openRouter.sendWithToolResults(any(), any(), any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture("Sorry."));

        // Capture tool results passed to round trip 2
        List<ToolResult> capturedResults = new ArrayList<>();
        doAnswer(inv -> {
            capturedResults.addAll(inv.<List<ToolResult>>getArgument(4));
            return CompletableFuture.completedFuture("Sorry.");
        }).when(openRouter).sendWithToolResults(any(), any(), any(), any(), any(), any());

        List<String> delivered = new ArrayList<>();
        orc.handleQuery("do something", player, stubSession(), delivered::add).get(5, TimeUnit.SECONDS);

        assertTrue(capturedResults.stream().anyMatch(r -> r.content().startsWith("[Unknown tool:")));
    }

    @Test
    void disconnectBetweenRoundTripsDiscardsResponse() throws Exception {
        AdvisorTool fakeTool = new AdvisorTool() {
            public String name() { return "get_inventory"; }
            public JsonObject definition() { return new JsonObject(); }
            public String execute(JsonObject args, ServerPlayer p) { return "Bread x5"; }
        };

        ToolCallOrchestrator orc = new ToolCallOrchestrator(openRouter, stubLore(), List.of(fakeTool), false);
        when(player.isOnline()).thenReturn(false); // already offline
        when(player.getServer()).thenReturn(server);
        doAnswer(inv -> { ((Runnable) inv.getArgument(0)).run(); return null; }).when(server).execute(any());

        ToolCall call = new ToolCall("id1", "get_inventory", new JsonObject());
        when(openRouter.sendWithTools(any(), any(), any(), any()))
            .thenReturn(CompletableFuture.completedFuture(new OpenRouterResponse(null, List.of(call))));

        List<String> delivered = new ArrayList<>();
        orc.handleQuery("what do I have?", player, stubSession(), delivered::add).get(5, TimeUnit.SECONDS);

        assertTrue(delivered.isEmpty(), "Should not deliver to offline player");
        verify(openRouter, never()).sendWithToolResults(any(), any(), any(), any(), any(), any());
    }

    private LoreIndex stubLore() {
        return new LoreIndex() {{ /* empty — override query to return empty list */ }};
        // If LoreIndex can't be subclassed, use a spy or a factory that allows injection
    }

    private AdvisorSession stubSession() {
        return mock(AdvisorSession.class);
    }
}
```

> **Note:** `LoreIndex` may need a package-private constructor or a static factory variant for testing. If it cannot be subclassed/mocked, add a `LoreIndex(Map<String,String> entries)` constructor for test injection.

- [x] **Run to confirm failure**

```
./gradlew test --tests "io.github.senseidragon.dragontweaksv2.advisor.ToolCallOrchestratorTest"
```
Expected: FAIL — `handleQuery` does not exist.

- [x] **Add handleQuery() to ToolCallOrchestrator**

```java
import com.google.gson.JsonObject;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public CompletableFuture<Void> handleQuery(
        String playerMessage,
        ServerPlayer player,
        AdvisorSession session,
        Consumer<String> responseCallback) {

    return CompletableFuture.runAsync(() -> {
        try {
            // 1. Lore lookup + system prompt assembly
            List<String> lore = loreIndex != null ? loreIndex.query(playerMessage) : List.of();
            String systemPrompt = buildSystemPrompt(lore);

            // 2. History decision
            List<ChatMessage> history = shouldIncludeHistory(playerMessage)
                ? session.getHistory() : List.of();

            List<JsonObject> defs = toolDefinitions();

            // 3. Round trip 1
            OpenRouterResponse rt1 = openRouter.sendWithTools(systemPrompt, history, playerMessage, defs)
                .get(TOTAL_TIMEOUT_MS, TimeUnit.MILLISECONDS);

            if (!rt1.hasToolCalls()) {
                // Text-only path
                session.addExchange(playerMessage, rt1.textContent());
                if (player.isOnline()) responseCallback.accept(rt1.textContent());
                return;
            }

            // 4. Execute tools on server thread
            List<ToolResult> results = executeTools(rt1.toolCalls(), player);

            // 5. Disconnect check
            if (!player.isOnline()) {
                session.addExchange(playerMessage, "[disconnected]");
                return;
            }

            // 6. Round trip 2
            String finalText = openRouter.sendWithToolResults(
                systemPrompt, history, playerMessage, rt1.toolCalls(), results, defs)
                .get(TOTAL_TIMEOUT_MS, TimeUnit.MILLISECONDS);

            session.addExchange(playerMessage, finalText);
            responseCallback.accept(finalText);

        } catch (java.util.concurrent.TimeoutException e) {
            LOGGER.warn("[ToolCallOrchestrator] Timeout for player {}", player.getName().getString());
            if (player.isOnline()) responseCallback.accept("I got a bit turned around — ask me again.");
        } catch (Exception e) {
            LOGGER.error("[ToolCallOrchestrator] Unexpected error", e);
        }
    });
}

private List<ToolResult> executeTools(List<ToolCall> calls, ServerPlayer player) {
    List<CompletableFuture<ToolResult>> futures = calls.stream()
        .map(call -> CompletableFuture.supplyAsync(() -> {
            AdvisorTool tool = registry.get(call.name());
            if (tool == null) {
                LOGGER.warn("[ToolCallOrchestrator] Unrecognized tool: {}", call.name());
                return new ToolResult(call.id(), "[Unknown tool: " + call.name() + "]");
            }
            try {
                AdvisorEntityManager.syncPosition(player);
                String result = tool.execute(call.args(), player);
                return new ToolResult(call.id(), result);
            } catch (Exception e) {
                LOGGER.debug("[ToolCallOrchestrator] Tool '{}' failed: {}", call.name(), e.getMessage());
                return new ToolResult(call.id(), "[Tool error: " + call.name() + " unavailable]");
            }
        }, player.getServer()))
        .toList();

    return futures.stream()
        .map(f -> {
            try {
                return f.get(TOOL_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                return new ToolResult("unknown", "[Tool error: timeout]");
            }
        })
        .collect(Collectors.toList());
}

private String buildSystemPrompt(List<String> lore) {
    StringBuilder sb = new StringBuilder();
    sb.append("You are a friendly mentor and guide: helpful, warm, and concise. ");
    sb.append("Always speak in natural, conversational sentences — never use lists or sentence fragments. ");
    sb.append("Greetings and farewells: one brief reply, 4 words or fewer. ");
    sb.append("Questions and requests: answer in one or two natural sentences, then stop. ");
    sb.append("Speak only from the context below; if something is missing, say so briefly.\n\n");
    sb.append("You have access to the player's inventory. Call get_inventory() proactively when:\n");
    sb.append("- The player has an active detrimental status effect that an item could cure or prevent\n");
    sb.append("- The player's hunger state is Hungry, Very Hungry, or Starving\n");
    sb.append("- The player's query involves a threat, hostile mob, or dangerous situation\n");
    sb.append("In all cases: if the player has the relevant item, tell them. If not, suggest acquiring it.\n\n");
    if (!lore.isEmpty()) {
        sb.append("Relevant knowledge:\n");
        lore.forEach(entry -> sb.append(entry).append("\n\n"));
    }
    return sb.toString();
}
```

Also add missing import for `ChatMessage` and ensure `session.getHistory()` and `session.addExchange()` match the actual `AdvisorSession` API (read the file to confirm method names before finalizing).

- [x] **Run tests**

```
./gradlew test --tests "io.github.senseidragon.dragontweaksv2.advisor.ToolCallOrchestratorTest"
```
Expected: all tests PASS

- [x] **Run all tests**

```
./gradlew test
```
Expected: BUILD SUCCESSFUL

- [x] **Append to test-audit-trail.md**

```
## 2026-06-13 — ToolCallOrchestrator
Changed: handleQuery() — 2-round-trip protocol, tool dispatch, disconnect check, timeout fallback
Tests: ToolCallOrchestratorTest — textOnlyPath, toolCallPath, unrecognizedTool, disconnectBetweenRoundTrips — PASS
```

- [x] **Commit**

```
git add src/main/java/io/github/senseidragon/dragontweaksv2/advisor/ToolCallOrchestrator.java
git add src/test/java/io/github/senseidragon/dragontweaksv2/advisor/ToolCallOrchestratorTest.java
git commit -m "feat: implement ToolCallOrchestrator 2-round-trip protocol with edge case handling"
```

---

## Phase 8 — AdvisorStatusMonitor

### Task 15: Effect event handling and circuit breaker

**Files:**
- Create: `src/main/java/io/github/senseidragon/dragontweaksv2/advisor/AdvisorStatusMonitor.java`
- Create: `src/test/java/io/github/senseidragon/dragontweaksv2/advisor/AdvisorStatusMonitorTest.java`
- Modify: `src/main/java/io/github/senseidragon/dragontweaksv2/DragonTweaksV2.java`

- [x] **Write failing tests**

```java
package io.github.senseidragon.dragontweaksv2.advisor;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class AdvisorStatusMonitorTest {

    @Mock ToolCallOrchestrator orchestrator;
    @Mock AdvisorSessionManager sessionManager;
    @Mock net.minecraft.server.level.ServerPlayer player;

    AdvisorStatusMonitor monitor;

    @BeforeEach
    void setup() {
        monitor = new AdvisorStatusMonitor(orchestrator, sessionManager, 5, 10);
        when(player.getUUID()).thenReturn(java.util.UUID.randomUUID());
        when(player.isOnline()).thenReturn(true);
    }

    @Test
    void effectAppliedFiresNotification() {
        AdvisorSession session = mock(AdvisorSession.class);
        when(sessionManager.getSession(player)).thenReturn(session);
        when(session.hasBeenNotified(any())).thenReturn(false);

        ResourceLocation poisonId = ResourceLocation.withDefaultNamespace("poison");
        monitor.handleEffectApplied(player, poisonId, true);

        verify(orchestrator).handleQuery(contains("poison"), eq(player), eq(session), any());
        verify(session).markNotified(poisonId);
    }

    @Test
    void sameEffectNotReFiredWhileActive() {
        AdvisorSession session = mock(AdvisorSession.class);
        when(sessionManager.getSession(player)).thenReturn(session);
        when(session.hasBeenNotified(any())).thenReturn(true); // already notified

        monitor.handleEffectApplied(player, ResourceLocation.withDefaultNamespace("poison"), true);

        verify(orchestrator, never()).handleQuery(any(), any(), any(), any());
    }

    @Test
    void effectRemovedClearsFlag() {
        AdvisorSession session = mock(AdvisorSession.class);
        when(sessionManager.getSession(player)).thenReturn(session);

        ResourceLocation poisonId = ResourceLocation.withDefaultNamespace("poison");
        monitor.handleEffectRemoved(player, poisonId);

        verify(session).clearNotified(poisonId);
    }

    @Test
    void circuitBreakerDisablesAfterThreshold() {
        AdvisorSession session = mock(AdvisorSession.class);
        when(sessionManager.getSession(player)).thenReturn(session);
        when(session.hasBeenNotified(any())).thenReturn(false);

        ResourceLocation poisonId = ResourceLocation.withDefaultNamespace("poison");
        // Fire 6 times (threshold is 5)
        for (int i = 0; i < 6; i++) {
            monitor.handleEffectApplied(player, poisonId, true);
            when(session.hasBeenNotified(any())).thenReturn(false); // reset for next call
        }

        // After circuit break, orchestrator should stop being called
        int callsAfterBreak = 6; // last call should not reach orchestrator
        verify(orchestrator, atMost(5)).handleQuery(any(), any(), any(), any());
    }

    @Test
    void nonDetrimentalEffectIgnored() {
        monitor.handleEffectApplied(player, ResourceLocation.withDefaultNamespace("speed"), false);
        verify(orchestrator, never()).handleQuery(any(), any(), any(), any());
    }
}
```

- [x] **Run to confirm failure**

```
./gradlew test --tests "io.github.senseidragon.dragontweaksv2.advisor.AdvisorStatusMonitorTest"
```
Expected: FAIL — class does not exist.

- [x] **Create AdvisorStatusMonitor.java**

```java
package io.github.senseidragon.dragontweaksv2.advisor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AdvisorStatusMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvisorStatusMonitor.class);

    private final ToolCallOrchestrator orchestrator;
    private final AdvisorSessionManager sessionManager;
    private final int circuitBreakerThreshold;
    private final long windowSeconds;

    // Per-player: list of event timestamps (epoch seconds) for circuit breaker
    private final Map<UUID, List<Long>> eventTimes = new ConcurrentHashMap<>();
    private final Set<UUID> disabledPlayers = ConcurrentHashMap.newKeySet();

    public AdvisorStatusMonitor(ToolCallOrchestrator orchestrator,
                                 AdvisorSessionManager sessionManager,
                                 int circuitBreakerThreshold,
                                 long windowSeconds) {
        this.orchestrator = orchestrator;
        this.sessionManager = sessionManager;
        this.circuitBreakerThreshold = circuitBreakerThreshold;
        this.windowSeconds = windowSeconds;
    }

    @SubscribeEvent
    public void onEffectAdded(MobEffectEvent.Added event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        boolean detrimental = event.getEffectInstance().getEffect().value()
            .getCategory() == MobEffectCategory.HARMFUL;
        ResourceLocation effectId = event.getEffectInstance().getEffect()
            .unwrapKey().map(k -> k.location()).orElse(null);
        if (effectId == null) return;
        handleEffectApplied(player, effectId, detrimental);
    }

    @SubscribeEvent
    public void onEffectRemoved(MobEffectEvent.Remove event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ResourceLocation effectId = event.getEffect().unwrapKey()
            .map(k -> k.location()).orElse(null);
        if (effectId != null) handleEffectRemoved(player, effectId);
    }

    @SubscribeEvent
    public void onEffectExpired(MobEffectEvent.Expired event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ResourceLocation effectId = event.getEffect().unwrapKey()
            .map(k -> k.location()).orElse(null);
        if (effectId != null) handleEffectRemoved(player, effectId);
    }

    // package-private for testing
    void handleEffectApplied(ServerPlayer player, ResourceLocation effectId, boolean detrimental) {
        if (!detrimental) return;
        if (disabledPlayers.contains(player.getUUID())) return;

        AdvisorSession session = sessionManager.getSession(player);
        if (session == null || session.hasBeenNotified(effectId)) return;

        if (isCircuitBroken(player, effectId)) return;

        session.markNotified(effectId);
        String prompt = "I sense you have been afflicted with " + effectId.getPath() + ". Are you alright?";
        orchestrator.handleQuery(prompt, player, session, msg ->
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(msg)));
    }

    void handleEffectRemoved(ServerPlayer player, ResourceLocation effectId) {
        AdvisorSession session = sessionManager.getSession(player);
        if (session != null) session.clearNotified(effectId);
    }

    private boolean isCircuitBroken(ServerPlayer player, ResourceLocation effectId) {
        UUID uuid = player.getUUID();
        long now = System.currentTimeMillis() / 1000;
        List<Long> times = eventTimes.computeIfAbsent(uuid, k -> new ArrayList<>());
        times.removeIf(t -> now - t > windowSeconds);
        times.add(now);

        if (times.size() > circuitBreakerThreshold) {
            disabledPlayers.add(uuid);
            LOGGER.warn("[AdvisorStatusMonitor] Circuit breaker triggered — player: {}, effect: {}, events in window: {}, window: {}s",
                player.getName().getString(), effectId, times.size(), windowSeconds);
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                "I can no longer sense your condition — too much has happened at once."));
            return true;
        }
        return false;
    }
}
```

- [x] **Register AdvisorStatusMonitor in DragonTweaksV2.java**

After constructing `ToolCallOrchestrator` (in the server startup or mod init — match the pattern used by `AdvisorEntityManager`):
```java
NeoForge.EVENT_BUS.register(new AdvisorStatusMonitor(orchestrator, sessionManager, 5, 10));
```

- [x] **Run tests**

```
./gradlew test --tests "io.github.senseidragon.dragontweaksv2.advisor.AdvisorStatusMonitorTest"
```
Expected: all tests PASS

- [x] **Run all tests**

```
./gradlew test
```
Expected: BUILD SUCCESSFUL

- [x] **Append to test-audit-trail.md**

```
## 2026-06-13 — AdvisorStatusMonitor
Changed: Effect event handling, circuit breaker, per-player disable on spam
Tests: AdvisorStatusMonitorTest — effectAppliedFiresNotification, sameEffectNotReFired, effectRemovedClearsFlag, circuitBreakerDisablesAfterThreshold, nonDetrimentalIgnored — PASS
```

- [x] **Commit**

```
git add src/main/java/io/github/senseidragon/dragontweaksv2/advisor/AdvisorStatusMonitor.java
git add src/test/java/io/github/senseidragon/dragontweaksv2/advisor/AdvisorStatusMonitorTest.java
git add src/main/java/io/github/senseidragon/dragontweaksv2/DragonTweaksV2.java
git commit -m "feat: add AdvisorStatusMonitor with circuit breaker and NeoForge effect event handlers"
```

---

## Phase 9 — Wire Up

### Task 16: Wire AdvisorChatHandler to ToolCallOrchestrator

**Files:**
- Modify: `src/main/java/io/github/senseidragon/dragontweaksv2/advisor/AdvisorChatHandler.java`

- [x] **Read AdvisorChatHandler.java to see the current call site and parameter shape before editing**

- [x] **Replace direct OpenRouterService call with ToolCallOrchestrator**

Find the method that handles incoming chat and currently calls `openRouterService.sendMessage(...)`. Replace with:

```java
// Before: openRouterService.sendMessage(systemPrompt, history, message)
// After:
orchestrator.handleQuery(
    message,
    player,
    session,
    response -> player.sendSystemMessage(Component.literal(response))
);
```

The `orchestrator` field is injected via constructor or obtained from wherever `openRouterService` was obtained. If `AdvisorChatHandler` currently builds its own `OpenRouterService`, pass `ToolCallOrchestrator` in instead.

Remove the `SYSTEM_PROMPT` constant from `AdvisorChatHandler` — it is now built inside `ToolCallOrchestrator.buildSystemPrompt()`.

- [x] **Run tests**

```
./gradlew test
```
Expected: BUILD SUCCESSFUL

- [x] **Append to test-audit-trail.md**

```
## 2026-06-13 — AdvisorChatHandler wire-up
Changed: AdvisorChatHandler now delegates to ToolCallOrchestrator; removed SYSTEM_PROMPT constant
Tests: Covered by ToolCallOrchestratorTest for the path logic. AdvisorChatHandlerTest for handler wiring — run ./gradlew test — PASS
```

- [x] **Commit**

```
git add src/main/java/io/github/senseidragon/dragontweaksv2/advisor/AdvisorChatHandler.java
git commit -m "feat: wire AdvisorChatHandler to ToolCallOrchestrator"
```

---

### Task 17: Register capability probe result in ToolCallOrchestrator

**Files:**
- Modify: `src/main/java/io/github/senseidragon/dragontweaksv2/openrouter/OpenRouterService.java`
- Modify: `src/main/java/io/github/senseidragon/dragontweaksv2/advisor/ToolCallOrchestrator.java`
- Modify: `src/main/java/io/github/senseidragon/dragontweaksv2/DragonTweaksV2.java`

- [x] **Read OpenRouterService.java to see where the existing init/priming sequence runs before editing**

- [x] **Call probeContextRetention() in OpenRouterService init and pass result to ToolCallOrchestrator**

In `OpenRouterService`, after the existing model priming call, run the probe:
```java
// At end of init sequence (background thread — never server thread):
boolean retainsContext = probeContextRetention();
LOGGER.info("[OpenRouterService] Model context retention probe: {}", retainsContext);
// Store for retrieval
this.modelRetainsContext = retainsContext;
```

Add a getter:
```java
public boolean isModelRetainsContext() { return modelRetainsContext; }
```

In `DragonTweaksV2.java`, after the service finishes init, pass the probe result when constructing `ToolCallOrchestrator`:
```java
ToolCallOrchestrator orchestrator = new ToolCallOrchestrator(
    openRouterService, loreIndex, List.of(new InventoryTool(), new ScanAreaTool()),
    openRouterService.isModelRetainsContext()
);
```

- [x] **Run tests**

```
./gradlew test
```
Expected: BUILD SUCCESSFUL

- [x] **Commit**

```
git add src/main/java/io/github/senseidragon/dragontweaksv2/openrouter/OpenRouterService.java
git add src/main/java/io/github/senseidragon/dragontweaksv2/advisor/ToolCallOrchestrator.java
git add src/main/java/io/github/senseidragon/dragontweaksv2/DragonTweaksV2.java
git commit -m "feat: run capability probe at init and wire modelRetainsContext into ToolCallOrchestrator"
```

---

## Phase 10 — Final Verification

### Task 18: Run full test suite and audit

- [x] **Run all tests**

```
./gradlew test
```
Expected: BUILD SUCCESSFUL — all Tier 1 tests green.

- [x] **Append final audit entry to test-audit-trail.md**

```
## 2026-06-13 — Tool-calling feature complete (Tier 1)
All Tier 1 unit tests pass via ./gradlew test.
Tier 2 game tests deferred (InventoryTool, ScanAreaTool) — require runGameTestServer.
Tier 3 live verification deferred — require running Minecraft client.
```

- [x] **Commit**

```
git add test-audit-trail.md
git commit -m "chore: final audit entry — tool-calling Tier 1 tests all pass"
```

---

## Self-Review Notes

**Spec coverage check:**
- ✅ LoreIndex + keyword index (Task 3)
- ✅ Lore files with cure/treatment items (Task 3)
- ✅ InventoryTool (Task 8)
- ✅ ScanAreaTool surface + underground + ore detection (Tasks 9–10)
- ✅ Tool-call protocol max 2 round trips (Task 14)
- ✅ System prompt proactive guidance (Task 14 — buildSystemPrompt)
- ✅ History inclusion decision (Task 13)
- ✅ Capability probe → modelRetainsContext (Tasks 7, 17)
- ✅ Hunger state as calibrated language (Task 5)
- ✅ AdvisorStatusMonitor effect events + circuit breaker (Task 15)
- ✅ Advisor entity spawn/despawn lifecycle (Tasks 11–12)
- ✅ Disconnect between round trips → discard (Task 14)
- ✅ Tool failure → structured error string (Task 14)
- ✅ Unrecognized tool → [Unknown tool: x] (Task 14)
- ✅ Round trip 2 timeout → fallback message (Task 14)
- ⚠️ "Manual fresh context" mechanism — marked TBD in spec, not implemented here
- ⚠️ Tier 2 game tests (InventoryTool, ScanAreaTool) — deferred to runGameTestServer

**Type consistency check:** `ToolCall(id, name, args)` and `ToolResult(toolCallId, content)` used consistently across Tasks 1, 6, 14. `OpenRouterResponse(textContent, toolCalls)` used in Tasks 6 and 14.
