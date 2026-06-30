# Entry Points

- `DragonTweaksV2.java` — mod entry point; mod bus subscriber; registers deferred registers before `registerEventBus()`
- `DragonTweaksV2Client.java` — client-only code; must **never** be class-loaded on a dedicated server
- `Config.java` — wraps NeoForge config values; subscribes to config reload events

← [[Architecture]]
