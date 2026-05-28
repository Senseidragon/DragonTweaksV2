---
tags:
  - feature
  - planned
  - ai
  - openrouter
  - inference
status: planned
---

# OpenRouter Integration

Cloud LLM inference via OpenRouter API. The sole inference backend for [[Dialogue-System]], superseding local Ollama (v1) due to VRAM constraints limiting Ollama to anemic sub-7B models versus 20B–120B models available via OpenRouter at comparable cost.

## Key Details

**Status:** Not yet implemented.

### Planned Use Cases
- Dynamic NPC dialogue generation (see [[Dialogue-System]])
- Procedural observation text from [[ObservationTicker]]

### Technical Requirements
- HTTP calls must be async (off the game thread) — use CompletableFuture or a dedicated thread pool
- API key stored in [[Config]], never hardcoded in source
- Configurable model ID (e.g. `openai/gpt-4o-mini`, `anthropic/claude-haiku`)
- Rate limiting and timeout handling required to avoid hang on bad network

### Config Keys (Planned)
| Key | Type | Default |
|-----|------|---------|
| `openrouter.enabled` | Boolean | false |
| `openrouter.apiKey` | String | "" |
| `openrouter.model` | String | "openai/gpt-4o-mini" |
| `openrouter.timeoutMs` | Integer | 5000 |

## Relationships
- [[Dialogue-System]] — primary consumer of inference results
- [[Config]] — API key and model selection stored here
- [[NPC-Roles]] — role context passed as part of the prompt
