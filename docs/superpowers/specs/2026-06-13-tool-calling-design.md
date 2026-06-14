# Advisor Tool-Calling — Design Spec

**Date:** 2026-06-13
**Mod:** DragonTweaksV2 (NeoForge 1.21.1)
**Branch:** advisor
**Status:** Brainstorm complete — pending user spec review. Not yet approved for implementation.

---

## Overview

Replace the current "push all context upfront" approach with tool-calling: the model requests only the context it needs, on demand, for each query. Baseline context and lore remain injected; inventory and world scanning become tools the model calls explicitly. A knowledge base lookup step retrieves relevant lore before every round trip. A status monitor proactively notifies the model when detrimental effects are applied to the player.

---

## Architecture

### New classes

| Class | Responsibility |
|---|---|
| `ToolCallOrchestrator` | 2-round-trip protocol, tool definitions, history inclusion decision, `modelRetainsContext` flag, lore lookup integration |
| `AdvisorTool` | Interface: `name()`, `definition()` (JSON schema), `execute(args, player)` → `String` |
| `InventoryTool` | Implements `get_inventory()` |
| `ScanAreaTool` | Implements `scan_area(radius, depth, detectOres)` |
| `LoreIndex` | Loads lore from classpath at mod startup; keyword index; `query(String)` → `List<String>` |
| `AdvisorStatusMonitor` | NeoForge event handler for effect-applied events; circuit breaker; self-disable on spam |

### Modified classes

| Class | Change |
|---|---|
| `OpenRouterService` | Adds capability probe to init sequence |
| `AdvisorChatHandler` | Calls `ToolCallOrchestrator` instead of `OpenRouterService` directly |

### Unchanged

`AdvisorSession`, `AdvisorSavedData`, `EnvironmentContextBuilder`, `AdvisorSessionManager`, `ChatMessage`

---

## Advisor Entity

The advisor is a server-side entity — it exists on the server even in single-player (integrated server). It is:

- **Invisible** — no rendered model
- **Non-targetable** — cannot be selected or attacked by players or mobs
- **Player-bonded** — always at the player's position; no independent movement or AI
- **The conceptual origin of all world queries** — `scan_area()` and any future positional tools use the entity's position as their origin, not the player's position directly (they are always equal, but the entity is the semantic subject)

The entity is spawned on player login and despawned on player disconnect. No world remnant is left behind.

---

## Always-Injected Context (Baseline)

Injected into every system prompt regardless of query:

- Time of day
- Weather
- Biome
- Sky visibility / position descriptor
- Status effects — **only if present** (absent → omitted entirely)
- **Hunger state** — always included; expressed as calibrated language, not a raw number: `Sated` (18–20), `Peckish` (13–17), `Hungry` (8–12), `Very Hungry` (3–7), `Starving` (0–2)

---

## Knowledge Base (Lore)

### Storage

Lore files live at `src/main/resources/data/dragontweaksv2/lore/` and are bundled in the JAR. No loose files at runtime.

### Format

Each lore file uses YAML frontmatter with a `keywords` list:

```
---
keywords: [enderman, endermen, ender]
---
<lore content>
```

### LoreIndex

Loaded at mod startup from the classpath. Builds a keyword → content map. Exposes `query(String playerMessage) → List<String>` which tokenizes the input and returns matching lore file contents.

### Lookup step

Runs before every round trip 1, for both player-initiated queries and status monitor proactive pushes. Relevant lore is injected into the system prompt alongside baseline context.

### Lore content requirements

Each lore file must include:
- Mob/topic behavior relevant to player decision-making
- **Items that cure, prevent, or mitigate** the relevant threat or condition
- Food quality notes where applicable (e.g., rotten flesh is edible but inadvisable)

---

## Tools

### `get_inventory()`

No parameters.

**Scope:** armor (4 slots), main hand, off-hand, full 36-slot player inventory.

**Format:** `"[Enchanted] <FriendlyName> x<qty>"`

- Item IDs translated to friendly names (e.g., `minecraft:bread` → `"Bread"`)
- Enchanted items prefixed with `"Enchanted"` — enchantment type not reported
- Mod-added containers: identified by name only — `"Backpack (contents not scanned)"` — no recursion
- Cosmetic overlays and mod-added cosmetic inventories: excluded

---

### `scan_area(radius, depth, detectOres)`

**Parameters:**

| Parameter | Type | Default | Description |
|---|---|---|---|
| `radius` | int | standard visibility range | Block radius from advisor entity position |
| `depth` | int | 4 | Y levels to scan, starting at player Y+3 downward |
| `detectOres` | boolean | false | Whether to report ore presence in detected voids |

**Y range:** player Y+3 − depth to player Y+3. Default (depth 4) = surface scan. Depth 10 = Y+3 to Y-7.

---

**Surface / entity scan** (when depth stays above surface level):

AABB query against entity chunk entity lists. No block iteration.

---

**Underground block scan** (below surface):

*Coarse pass:*
- Step = `max(1, radius / 8)` in X and Z; step 1 in Y (every layer scanned)
- radius 8 → step 1 | radius 16 → step 2 | radius 32 → step 4 | radius 64 → step 8

*On air block hit:*
- If hit point falls within a previously sub-scanned region → skip
- Otherwise: flood-fill from hit point (capped at max volume), count connected air blocks
- Classify by volume (thresholds tunable via config):

| Classification | Reported? |
|---|---|
| Air pocket | No — discarded |
| Large tunnel | Yes |
| Small cave | Yes |
| Dungeon room | Yes |
| Large cave | Yes |
| Massive cavern/ravine | Yes |

- Mark subscan bounds; coarse scan skips future hits within those bounds
- Returns: classification, relative direction from player, approximate depth

*Ore detection* (when `detectOres = true`):
- Scans exposed block surfaces of detected voids only — no scanning through solid stone
- Reports top 4 ore types by frequency — names only, no counts, no locations
- Volume thresholds and ore type list tunable via config

---

## Tool-Call Protocol

- Model enumerates all tools it needs in a single response (parallel tool calls)
- All tools executed; all results collected and returned in one batch
- **Maximum 2 HTTP round-trips** per query:
  1. Initial query (with tool definitions + lore + baseline context) → text response or tool calls
  2. If tool calls: execute all → send all results → final text response
- 60-second timeout applies to the full flow

---

## System Prompt — Proactive Tool Guidance

The system prompt includes explicit behavioral instructions telling the model when to call `get_inventory()` proactively, regardless of what the player asked:

> "You have access to the player's inventory. Call `get_inventory()` proactively when:
> - The player has an active detrimental status effect that an item could cure or prevent
> - The player's hunger state is Hungry, Very Hungry, or Starving — check for consumables and advise based on quality (rotten flesh can be eaten but is not advisable)
> - The player's query involves a threat, hostile mob, or dangerous situation where specific items would matter
>
> In all cases: if the player has the relevant item, tell them. If not, suggest acquiring it."

---

## Session History and Context Management

**Session record:** Every query and response always appended to `AdvisorSession` regardless of what was sent to the model. The record is always complete.

**API call history:** A separate per-query decision owned by `ToolCallOrchestrator`:
- Stateless queries (inventory lookups, scans, time/biome checks) → history omitted
- Conversational / follow-up queries → history included
- Manual "fresh context" capability to be exposed (exact mechanism TBD)

**History decision heuristic:**
1. Follow-up signals present ("you said", "earlier", "what about", "tell me more") → always include history
2. Pure state-query patterns ("what do I have", "what's around me", "scan") with no follow-up signals → suppress history
3. Default: include history

---

## Model Context Retention Capability Probe

Runs during `OpenRouterService` init, after existing model priming. Not cached — re-run every session.

1. Call 1: `"I have an apple in my left hand."` (no history, no context)
2. Call 2: `"What am I holding?"` (no history, no context)
3. Parse response:
   - References apple → `modelRetainsContext = true`
   - No reference → `modelRetainsContext = false`

Result stored on `ToolCallOrchestrator`. Informs whether "clear context" means suppressing history or requires a different mechanism.

---

## ToolCallOrchestrator Internals

### Threading model

Server thread dispatches and receives only — never waits.

```
Player chat message
      │
      ▼
AdvisorChatHandler (server thread)
      │
      └── submit to background executor
              │
              ▼
      ToolCallOrchestrator (background thread)
              │
              ├── LoreIndex.query()
              ├── History decision
              ├── Round trip 1 [async HTTP]
              │
              ├── Text-only response → callback to server thread → send to player
              │
              └── Tool calls?
                      │
                      ├── Dispatch each tool via player.getServer().submit()
                      │   [background thread waits — not server thread]
                      ├── Collect results
                      └── Round trip 2 → callback to server thread → send to player
```

### Game-thread dispatch for tool execution

`AdvisorTool.execute(args, player)` is always called on the server thread. The orchestrator owns dispatch:

```java
List<CompletableFuture<ToolResult>> futures = toolCalls.stream()
    .map(call -> {
        AdvisorTool tool = registry.get(call.name());
        return CompletableFuture.supplyAsync(
            () -> tool.execute(call.args(), player),
            serverThreadExecutor
        );
    }).toList();

List<ToolResult> results = futures.stream()
    .map(f -> f.get(TOOL_TIMEOUT_MS, MILLISECONDS))
    .toList();
```

Tool implementations read world/player state synchronously on the server thread with no threading concerns of their own.

---

## AdvisorStatusMonitor

### Trigger

NeoForge `MobEffectEvent.Added` — fires when a detrimental/curable effect is applied to the player.

### Per-player state (in `AdvisorSession`)

`Set<ResourceLocation> notifiedEffects` — tracks effects for which a notification has already been sent this session.

### Flow

1. Effect applied → check if detrimental/curable
2. If already in `notifiedEffects` → skip
3. Otherwise → add to `notifiedEffects`, fire proactive advisor message through `ToolCallOrchestrator`
4. Effect removed (`MobEffectEvent.Remove` / `Expired`) → remove from `notifiedEffects`
5. If effect reapplied after expiry → triggers again (one notification per acquisition)

### Proactive message format

One consolidated message per check — if multiple effects are active simultaneously, one message covers all. The model handles prioritization and inventory check.

### Circuit breaker

Tracks rolling event count per player within a configurable time window.

- If count exceeds configurable threshold → scanner self-disables for the session
- Logs: `[AdvisorStatusMonitor] Circuit breaker triggered — player: {name}, effect: {effect_id}, events in window: {count}, window: {seconds}s`
- Sends one in-game message to the player: advisor acknowledges it can no longer sense their condition
- All other advisor functionality continues normally
- **Resets on session restart** — no disk state

---

## Entity Lifecycle Requirement

The advisor NPC entity must be properly managed across the player session lifecycle. **No orphaned entities.**

### On player disconnect

1. All session state (`AdvisorSession`, `notifiedEffects`, circuit breaker state) flushed to `AdvisorSavedData`
2. Any in-flight LLM request: result discarded after online check — not sent to disconnected player; session history still written
3. Advisor entity despawned — no server-side entity object remains

### On player login

1. `AdvisorSavedData` restored
2. Advisor entity spawned fresh
3. Status monitor enabled (circuit breaker reset)

### Implementation note

The disconnect lifecycle must be explicitly handled in the implementation plan — do not assume the existing code covers it.

---

## Edge Cases and Error Handling

**Client disconnect between round trips**
Check player online status after tool execution, before round trip 2. If offline: discard result silently, write session history, do not send response.

**Tool execution failure**
Catch per-tool. Return structured error string: `"[Tool error: inventory unavailable]"`. Model receives it and responds gracefully. Do not abort the full flow for one failed tool.

**Unrecognized tool name**
Check each tool call name against the registered registry. Unknown name → return `"[Unknown tool: {name}]"` as result. Log warning. Continue with other tool calls.

**Malformed model response / unparseable tool arguments**
Catch parse exceptions per tool call. Treat as tool execution failure. Log raw response at DEBUG level.

**Round trip 2 timeout**
60-second timeout covers full flow. On timeout: send player fallback message ("I got a bit turned around — ask me again"). Log the timeout.

**LLM unavailable / OpenRouter error**
Handled by existing `OpenRouterService` failure isolation. Player gets brief in-game message. Game continues.

---

## Testing

Testing is organized into three tiers by what runtime environment each requires.

### Tier 1 — Pure Java unit tests (`./gradlew test`)

No Minecraft environment needed. `OpenRouterService` and tool dispatch are mocked. `ToolCallOrchestrator` is tested against fake `AdvisorTool` implementations returning canned strings.

| Class | Coverage |
|---|---|
| `LoreIndex` | Classpath loading; keyword tokenization; single/multi-keyword match; no-match returns empty; case-insensitive matching |
| `ToolCallOrchestrator` | Text-only response path (no tool calls); tool-call path (round trip 1 → dispatch → round trip 2); history inclusion decision — follow-up signals → include, pure state query → suppress, default → include; parallel tool dispatch order-independence; disconnect check between round trips; tool failure → structured error string; unrecognized tool name → `[Unknown tool: x]`; malformed parse exception → treated as tool failure; round trip 2 timeout → fallback message sent to player |
| `AdvisorStatusMonitor` | Effect applied → notification fires; same effect not re-notified while still active; effect removed → flag clears → re-notified on reapply; multiple simultaneous effects → one consolidated message; circuit breaker fires at threshold; sends one in-game message then stops; all other advisor functionality continues after circuit breaker |
| Capability probe | Apple-reference response → `modelRetainsContext = true`; non-reference response → `false` |

### Tier 2 — Game tests (`runGameTestServer`)

Require a real server thread and world. Not run by `./gradlew test`. Each game test is noted in `test-audit-trail.md` as requiring `runGameTestServer`.

- `InventoryTool.execute()` — real player with known inventory; verify output format and enchanted prefix
- `ScanAreaTool.execute()` — real world with controlled blocks; verify scan origin is the advisor entity's position; verify air void detection and ore detection flag

### Tier 3 — Live manual verification only

- End-to-end advisor response in a running Minecraft client
- Advisor entity is server-side, invisible, non-targetable, and player-bonded — verify no orphaned entity remains after player disconnect (server entity list or debug log)
- Status monitor proactive message delivery to player chat

---

## Constraints

- Do not commit without explicit authorization from Dragon
- Do not run `runClient` until all tests pass
- Pre-flight checklist required before any Java source edit
- `test-audit-trail.md` is append-only
- Nothing blocks the Minecraft main/server/render thread
- No source scanning (Java/JSON/config) without explicit authorization
- `./gradlew test` required before any change is reported complete
