# Tool Calling

`ToolCallOrchestrator` runs a two-round inference loop per player chat message.

**Round 1** — advisory model receives PERSONA_BIO + environment snapshot + player message. It may call tools.

**Round 2** — tool results are injected. Advisory model produces the final grounded response.

## Tools

| Tool | Purpose |
|------|---------|
| `scan_area` | Block distribution scan in a radius; reports fluid state; uses COLLIDER raycast for occlusion |
| `get_environment` | Time, weather, biome, light level, player coordinates |
| `get_inventory` | Player inventory contents |
| `get_status` | Player health, hunger, armor |
| `identify_nearby` | Visible entities within range; substring-filterable; enriches spawners with mob type via NBT |

## Routing

Categories with keyword signals route messages to the appropriate tool. Zero-arg tools (get_environment, get_inventory, get_status) are force-injected at startup. `identify_nearby` is excluded from force-inject — it must be explicitly requested.

## Dev Comment Filter

Messages starting with `--` are dropped in `AdvisorChatHandler.onServerChat()` before any pipeline processing. The message still appears in game chat.

← [[Advisor-System]]
