# OpenRouter Integration — Design Spec

**Date:** 2026-06-05  
**Mod:** DragonTweaksV2 (NeoForge 1.21.1)  
**Status:** Approved

---

## Overview

On world-load (logical server startup), the mod asynchronously establishes a connection to OpenRouter, selects the cheapest model for the `flavor` and `advisory` roles from `model_config.json`, validates the API key, and primes both models with a throwaway request to eliminate first-query latency. If any step fails the mod disables itself and notifies the player via chat. Nothing may block the main thread.

---

## Deployment Contexts

| Context | Who runs OpenRouterService | API key source |
|---|---|---|
| Singleplayer | Integrated server (client JVM) | `run/client/.env` |
| Dedicated server | Dedicated server JVM | Server working directory `.env` |
| Multiplayer client | **Not run** — server is authoritative | N/A |

The physical Minecraft client (`Dist.CLIENT`) has no OpenRouter code. In multiplayer, clients receive advisor responses exclusively via server→client packets (future work).

---

## Components

### `EnvLoader`
- **Side:** Common (no `Dist` restriction)
- **Responsibility:** Read `.env` from `System.getProperty("user.dir")`, parse `KEY=VALUE` lines, return `Map<String, String>`.
- **Pure utility** — no side effects, no logging of key values.

### `ModelSelector`
- **Side:** Common
- **Responsibility:** Accept a parsed `JsonObject` (Gson) and a role name string. Return the `model_id` of the candidate with the lowest `role_weighted_cost_per_1m` in that role's `candidates` array.
- **Pure function** — no I/O, no side effects.
- `OpenRouterService` is responsible for loading `model_config.json` from `Path.of(System.getProperty("user.dir"), "model_config.json")` and passing the parsed `JsonObject` to `ModelSelector`. This keeps `ModelSelector` testable without touching the filesystem.

### `OpenRouterService`
- **Side:** Logical server only
- **Responsibility:** Orchestrate the full init sequence asynchronously. Own a single-thread `ExecutorService` and a `java.net.http.HttpClient`.
- **Singleton** — one instance per server lifecycle.
- **Exposes:**
  - `initAsync()` → `CompletableFuture<Void>`
  - `shutdown()` — graceful executor shutdown
  - `isEnabled()` → `boolean` (defaults `false`, set `true` only on full success)
  - `getFlavorModelId()` → `String`
  - `getAdvisoryModelId()` → `String`
- **API key is never logged.** Only a masked indicator (`key present: true/false`) may appear in logs.

### `DragonTweaksV2` (modified)
- Registers `ServerStartingEvent` → calls `OpenRouterService.getInstance().initAsync()`, chains failure callback to send chat message and set `isEnabled = false`.
- Registers `ServerStoppingEvent` → calls `OpenRouterService.getInstance().shutdown()`.

### `DragonTweaksV2Client` (unchanged)
- No OpenRouter code. Handles rendering and GUI only.

---

## Initialization Flow

```
ServerStartingEvent  [game thread]
  └─ OpenRouterService.initAsync()  [background thread — ExecutorService]
       │
       ├─ 1. EnvLoader.load()
       │       → OPENROUTER_API_KEY present?  fail → disable
       │
       ├─ 2. ModelSelector.selectCheapest("flavor")
       │       → cheapest model_id by role_weighted_cost_per_1m  fail → disable
       │
       ├─ 3. ModelSelector.selectCheapest("advisory")
       │       → cheapest model_id by role_weighted_cost_per_1m  fail → disable
       │
       ├─ 4. GET https://openrouter.ai/api/v1/auth/key
       │       → 2xx?  fail → disable
       │
       ├─ 5. POST https://openrouter.ai/api/v1/chat/completions
       │       model: flavor model_id, messages: [{"role":"user","content":"ping"}]
       │       → 2xx?  fail → disable
       │
       └─ 6. POST https://openrouter.ai/api/v1/chat/completions
               model: advisory model_id, messages: [{"role":"user","content":"ping"}]
               → 2xx?  fail → disable

  On success: isEnabled = true
              LOG.debug("OpenRouter ready. flavor={}, advisory={}", flavorId, advisoryId)

  On failure: isEnabled = false (default)
              game thread: server chat message to all players
              LOG.debug("OpenRouter init failed: {}", reason)  [no key in reason]
```

---

## Error Handling

| Failure point | Condition | Chat message |
|---|---|---|
| EnvLoader | `.env` missing | `[DragonTweaks] AI advisor unavailable — .env file not found.` |
| EnvLoader | `OPENROUTER_API_KEY` absent | `[DragonTweaks] AI advisor unavailable — OPENROUTER_API_KEY not set.` |
| ModelSelector | `model_config.json` missing or malformed | `[DragonTweaks] AI advisor unavailable — model_config.json unreadable.` |
| ModelSelector | Role missing or no candidates | `[DragonTweaks] AI advisor unavailable — no candidates for role '<role>'.` |
| Key validation | Non-2xx from `/api/v1/auth/key` | `[DragonTweaks] AI advisor unavailable — API key rejected by OpenRouter.` |
| Model prime | Non-2xx from either prime call | `[DragonTweaks] AI advisor unavailable — model '<id>' did not respond.` |
| Exception | Timeout, malformed response, etc. | `[DragonTweaks] AI advisor unavailable — unexpected error: <short message>.` |

All failures log at DEBUG. The API key value is **never** written to any log output.

---

## Logging Policy

| Event | Level | Content |
|---|---|---|
| Init started | DEBUG | `"OpenRouter init started"` |
| Key present check | DEBUG | `"API key present: true"` or `"API key present: false"` — never the key value |
| Models selected | DEBUG | `"Selected models — flavor: <id>, advisory: <id>"` |
| Key validated | DEBUG | `"API key validated successfully"` |
| Model primed | DEBUG | `"Model primed: <id>"` |
| Init complete | DEBUG | `"OpenRouter ready. flavor=<id>, advisory=<id>"` |
| Any failure | DEBUG | Short reason string, no key value |

---

## Testing

Plain JUnit 5 unit tests (no Minecraft runtime required — all three new classes are pure Java).

| Test class | Coverage |
|---|---|
| `EnvLoaderTest` | Valid `.env`; missing file; key absent; malformed lines ignored |
| `ModelSelectorTest` | Selects cheapest by `role_weighted_cost_per_1m`; missing role; empty candidates; missing/malformed JSON |
| `OpenRouterServiceTest` | Mock `HttpClient` — correct endpoints called; `isEnabled` false by default; disable-on-failure for each of the 6 steps; true only on full success; API key never appears in any log output |

---

## Out of Scope

- Client→server packet protocol (future advisor work)
- Retry logic on startup failure (player reconnects to retry)
- Config screen integration for API key entry
- Per-player BYOK in multiplayer (future work)
