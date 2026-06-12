# Advisor Chat — Design Spec

**Date:** 2026-06-12
**Mod:** DragonTweaksV2 (NeoForge 1.21.1)
**Branch:** advisor
**Status:** Approved

---

## Overview

A chat-driven advisor that responds to every message from a player carrying the Structurize build tool. Single advisory model. Response is private to the sender. Both sides logged. Conversation history persists across server restarts via NBT SavedData. Nothing blocks the main thread.

---

## Trigger

The advisor activates for a player when `structurize:build_tool` is present anywhere in that player's inventory. No prefix or keyword required — every chat message from a qualifying player is intercepted.

---

## Architecture

Seven focused classes:

| Class | Responsibility |
|---|---|
| `AdvisorChatHandler` | Event listener — inventory check, orchestrates, never blocks |
| `AdvisorSessionManager` | Named accessor for `AdvisorSavedData` via overworld data storage |
| `AdvisorSession` | Per-player conversation history, NBT-serializable |
| `ChatMessage` | Value object — role + content, NBT-serializable |
| `EnvironmentContextBuilder` | Pure utility — player + world → context string |
| `AdvisorSavedData` | NeoForge `SavedData` subclass — NBT persistence for all sessions |
| `OpenRouterService` | Extended with `queryAsync()` — system prompt + messages → `CompletableFuture<String>` |

`AdvisorChatHandler` is the only class that touches NeoForge events. All other classes are plain Java with no event dependencies — independently testable.

---

## Data Flow

```
ServerChatEvent [game thread]
  │
  ├─ OpenRouterService.isEnabled()? → no → disable mod, return
  ├─ player has structurize:build_tool in inventory? → no → pass through (normal chat)
  ├─ AdvisorSessionManager.getOrCreate(overworld, playerUuid) → AdvisorSession
  ├─ EnvironmentContextBuilder.build(player, level) → contextString
  ├─ session.addMessage("user", playerText)
  ├─ LOG.info "[Advisor] [{}] player: {}"
  ├─ cancel event (suppress public chat)
  │
  └─ executor.submit() [background thread]
       │
       ├─ schedule 5s task  → server.execute() → player.sendSystemMessage("Hmm...")
       ├─ schedule 10s task → server.execute() → player.sendSystemMessage("How should I put this...")
       ├─ schedule timeout  → cancel query, send "Brain fart, sorry.", log, disable mod
       │
       └─ OpenRouterService.queryAsync(systemPrompt, session.getMessages())
            .thenAccept(response →
                cancel pending timeout tasks
                session.addMessage("advisor", response)
                AdvisorSavedData.setDirty()
                LOG.info "[Advisor] [{}] advisor: {}"
                server.execute() [game thread]
                  └─ if player still online → player.sendSystemMessage(response)
            )
            .exceptionally(err →
                log error, disable mod
                server.execute() → player.sendSystemMessage("[DragonTweaks] Advisor unavailable.")
            )
```

Two thread crossings only: game thread → executor on dispatch, executor → game thread on delivery.

---

## Session and Persistence

### `ChatMessage`
- Fields: `String role` (`"user"` or `"advisor"`), `String content`
- `toNbt()` / `fromNbt(CompoundTag)`
- Note: `"advisor"` is translated to `"assistant"` in `queryAsync()` for the OpenRouter wire format. This translation exists in one place only.

### `AdvisorSession`
- `ArrayDeque<ChatMessage>` capped at `Config.ADVISOR_HISTORY_CAP` (default 20, in `dragontweaks-common.toml`)
- `addMessage(role, content)` — appends, drops oldest if over cap
- `getMessages()` → unmodifiable `List<ChatMessage>`
- `toNbt()` / `fromNbt(CompoundTag)`

### `AdvisorSavedData`
- NeoForge `SavedData` subclass, attached to the overworld
- `Map<UUID, AdvisorSession> sessions`
- `getOrCreate(UUID)` → `AdvisorSession`
- `save(CompoundTag)` / static `load(CompoundTag)` — iterates map, delegates to session NBT
- `setDirty()` called after every advisor message — NeoForge flushes on world save
- NBT deserialization failure: log at WARN, start player with empty session, do not crash

### `AdvisorSessionManager`
- Stateless accessor: `getOrCreate(ServerLevel overworld, UUID playerUuid)` → `AdvisorSession`
- Calls `overworld.getDataStorage().computeIfAbsent(...)` internally
- `AdvisorChatHandler` never calls `getDataStorage()` directly

---

## Environmental Context

`EnvironmentContextBuilder.build(ServerPlayer player, ServerLevel level)` returns a plain string injected into the system prompt. Pure function — no side effects, no I/O. All reads are non-blocking game-thread-safe calls.

| Field | Source | Format |
|---|---|---|
| Time of day | `level.getDayTime()` | dawn / morning / midday / afternoon / dusk / night |
| Weather | `level.isRaining()` / `level.isThundering()` | clear / raining / thunderstorm |
| Biome | `level.getBiome(player.blockPosition())` | registry name, lowercased |
| Sky visibility | `level.canSeeSky(pos)` + Y position | terrain descriptor — never a Y number |
| Nearby hostiles | `level.getEntitiesOfClass(Monster.class, AABB 32-block radius)` | approximate counts by type — never exact numbers |

---

## Advisor Persona

System prompt (static header, assembled once per query):

```
You are a seasoned adventurer — experienced, dry, darkly witty. Speak from hard experience.
No game mechanics, no modern concepts, nothing outside this world. 3–4 sentences. No lists.

{environmental_context}
```

---

## Progressive Timeout

Managed by a `ScheduledExecutorService`. All three tasks scheduled at dispatch time; cancelled if response arrives first.

| Elapsed | Action |
|---|---|
| 5s | Send `"Hmm..."` to player, continue waiting |
| 10s | Send `"How should I put this..."` to player, continue waiting |
| 60s | Send `"Brain fart, sorry."`, log at DEBUG, disable mod |

---

## Error Handling

| Failure | Behavior |
|---|---|
| `isEnabled() == false` | Disable mod. Do not exit game. |
| Build tool not in inventory | Pass through as normal chat |
| `queryAsync()` throws | Log error, disable mod |
| Progressive timeout (see above) | Staged messages, then disable mod on final timeout |
| Player disconnects before response | Drop silently — check online status before `sendSystemMessage` |
| NBT deserialization failure | Log at WARN, empty session, no crash |

---

## Logging Policy

| Event | Level | Content |
|---|---|---|
| Player message | INFO | `[Advisor] [<name>] player: <text>` |
| Advisor response | INFO | `[Advisor] [<name>] advisor: <text>` |
| Query failure | DEBUG | Short reason, no key value |
| Timeout | DEBUG | `[Advisor] [<name>] timeout — disabling` |
| NBT load failure | WARN | Player UUID + short reason |

---

## Testing

| Class | Approach |
|---|---|
| `ChatMessage` | Pure Java — NBT round-trip, role values |
| `AdvisorSession` | Pure Java — cap enforcement, trim order, NBT round-trip, empty session |
| `AdvisorSavedData` | Pure Java — multi-player NBT round-trip, missing player returns empty session, dirty flag set after write |
| `EnvironmentContextBuilder` | Mockito — stubs for `ServerPlayer` and `ServerLevel`; one test per field; sky/underground descriptor boundaries; hostile approximation thresholds |
| `AdvisorChatHandler` | Mock `OpenRouterService`, mock inventory — verify: disabled skips, no tool passes through, correct messages dispatched, progressive timeout fires in order |
| `OpenRouterService.queryAsync()` | Extend existing mock `HttpClient` pattern with timeout scenarios |

No Minecraft runtime required for any test class.

---

## Out of Scope

- MineColonies live state integration (future)
- Flavor model / `#f` command (future)
- Client→server packet protocol for multiplayer (future)
- Per-player BYOK (future)
- Config screen for API key entry (future)
