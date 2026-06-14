# Session Resume — Tool-Calling Design Brainstorm

## Current system prompt (AdvisorChatHandler.java line 25)

```java
"You are a friendly mentor and guide: helpful, warm, and concise. " +
"Always speak in natural, conversational sentences — never use lists or sentence fragments. " +
"Greetings and farewells: one brief reply, 4 words or fewer. " +
"Questions and requests: answer in one or two natural sentences, then stop. " +
"Speak only from the context below; if something is missing, say so briefly.\n\n"
```

Prompt has been revised 3 times this session. Current version not yet fully validated in-game. Hallucination discussion revealed that session history ("Chad") is a feature, not a bug. Actual hallucination was sensory bread details (texture, temperature) which context cannot support.

## What we are doing

Mid-brainstorm: designing tool-calling for the advisor system. Brainstorming skill is active.

**Full in-progress design doc:** `docs/superpowers/specs/2026-06-13-tool-calling-design.md`

Resume at: **User reviews spec** — then invoke writing-plans.

Brainstorming skill checklist state:
- [x] Explore project context
- [x] Ask clarifying questions
- [x] Propose approaches (Option B: ToolCallOrchestrator selected)
- [x] Present all design sections (ToolCallOrchestrator internals, edge cases, entity lifecycle)
- [x] Present Testing section
- [x] Get design approval (section by section)
- [x] Write design doc (complete — all sections including Testing, entity clarifications, hunger state)
- [x] Spec self-review
- [ ] User reviews spec — **NEXT**
- [ ] Invoke writing-plans

## Document update state (as of last save)

| Document | Status |
|---|---|
| `docs/superpowers/specs/2026-06-13-tool-calling-design.md` | Complete — all sections including Testing, entity design, hunger state |
| `feed-me.md` | Updated — this file |
| `MEMORY.md` | Updated — tool-calling design link added |

## Design decisions locked

### Architecture

**New classes:**
| Class | Responsibility |
|---|---|
| `ToolCallOrchestrator` | 2-round-trip protocol, tool definitions, history inclusion decision, `modelRetainsContext` flag, lore lookup |
| `AdvisorTool` | Interface: `name()`, `definition()` (JSON schema), `execute(args, player)` → `String` |
| `InventoryTool` | Implements `get_inventory()` |
| `ScanAreaTool` | Implements `scan_area(radius, depth, detectOres)` |
| `LoreIndex` | Classpath lore loader, keyword index, `query(String)` → `List<String>` |
| `AdvisorStatusMonitor` | Effect-applied event handler, circuit breaker, self-disable |

**Modified classes:**
- `OpenRouterService` — adds capability probe to init sequence
- `AdvisorChatHandler` — calls `ToolCallOrchestrator` instead of `OpenRouterService` directly

**Unchanged:** `AdvisorSession`, `AdvisorSavedData`, `EnvironmentContextBuilder`, `AdvisorSessionManager`, `ChatMessage`

### Baseline context additions
- Food level (0–20) added alongside existing status effects

### Lore / Knowledge Base
- Files at `src/main/resources/data/dragontweaksv2/lore/`
- YAML frontmatter with `keywords` list per file
- `LoreIndex` loaded at mod startup from classpath — no loose files at runtime
- Lookup runs before every round trip 1 (player query AND status monitor push)
- Lore files must include: behavior, cure/treatment items, food quality notes where applicable

### System prompt — proactive tool guidance
- Model instructed to call `get_inventory()` proactively when:
  - Detrimental status effect present
  - Food level is low (language calibrated: peckish/hungry/very hungry/starving)
  - Player query involves a threat, mob, or dangerous situation

### AdvisorStatusMonitor
- Event-driven: fires on `MobEffectEvent.Added` (not a timer)
- One notification per effect acquisition per session
- `notifiedEffects: Set<ResourceLocation>` lives in `AdvisorSession`
- Effect removed/expired → clears notified flag → re-triggers if reapplied
- One consolidated message per check (model handles multi-effect prioritization)
- Circuit breaker: self-disables on spam, logs effect+player+rate, notifies player in-game
- Resets on session restart — no disk state

### Entity lifecycle requirement
- On disconnect: session state flushed, in-flight requests discarded, advisor entity despawned cleanly
- On login: `AdvisorSavedData` restored, entity spawned fresh, monitor re-enabled
- No orphaned entities — must be explicitly handled in implementation plan

### Tool-call protocol
- Max 2 HTTP round-trips per query
- 60s timeout covers full flow
- Parallel tool calls in one model response; all results sent back in one batch

### Session history / context management
- Session record always written regardless of API history decision
- History decision: follow-up signals → include; pure state queries → suppress; default → include

### Model context retention capability probe
- Runs at OpenRouterService init every session
- 2-call probe (apple test); result stored on ToolCallOrchestrator; not cached to disk

### Edge cases (locked)
- Disconnect between round trips: discard result, write history, no response
- Tool failure: return structured error string per tool, don't abort full flow
- Unrecognized tool: return `[Unknown tool: {name}]`, log warning, continue
- Malformed response: catch per tool call, log DEBUG, treat as failure
- Round trip 2 timeout: fallback message to player, log
- LLM unavailable: existing OpenRouterService isolation handles it

## Key files

| File | Purpose |
|---|---|
| `docs/superpowers/specs/2026-06-13-tool-calling-design.md` | Full in-progress design spec |
| `src/main/java/.../advisor/AdvisorChatHandler.java` | Contains `SYSTEM_PROMPT`; calls orchestrator after this design |
| `src/main/java/.../advisor/EnvironmentContextBuilder.java` | Builds baseline context string |
| `src/main/java/.../openrouter/OpenRouterService.java` | HTTP layer; gets capability probe added |
| `src/main/resources/data/dragontweaksv2/lore/` | Lore files (to be created) |
| `test-audit-trail.md` | Append-only; must be updated after every code change |

## Constraints

- Do not commit without explicit authorization from Dragon
- Do not run `runClient` until all tests pass
- Pre-flight checklist required before any Java source edit
- `test-audit-trail.md` is append-only
- Nothing blocks the Minecraft main/server/render thread
- No source scanning (Java/JSON/config) without explicit authorization
- `./gradlew test` required before any change is reported complete
