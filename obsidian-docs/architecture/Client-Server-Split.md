# Client-Server Split

`DragonTweaksV2Client` must never be loaded on a dedicated server. All client-only code lives there.

The advisor system runs server-side — `AdvisorChatHandler` subscribes to `ServerChatEvent` on the game/Forge bus. NPC entity lifecycle is server-side. Inference happens on background threads and results are delivered back to the server thread via callbacks.

← [[Architecture]]
