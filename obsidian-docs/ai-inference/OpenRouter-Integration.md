# OpenRouter Integration

`OpenRouterService` handles all model calls via OpenRouter's API.

- BYOK (bring your own key) — player-supplied API key stored in config
- All calls are async; responses are delivered via callback; nothing touches the Minecraft main/render thread
- Failure-isolated from gameplay — an inference error produces no in-game crash

## Current Models

| Role | Model |
|------|-------|
| Advisory | `openai/gpt-oss-120b` |
| Flavor | `liquid/lfm-2-24b-a2b` |

Model selection requires Dragon's explicit authorization; do not change without it.

← [[AI-Inference]]
