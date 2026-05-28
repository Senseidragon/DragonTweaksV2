---
tags:
  - java
  - architecture
  - config
  - neoforge
source: src/main/java/io/github/senseidragon/dragontweaksv2/Config.java
---

# Config

`ModConfigSpec` wrapper. Declares configuration options and reacts to reload events.

## Key Details

**Package:** `io.github.senseidragon.dragontweaksv2`
**Annotation:** `@EventBusSubscriber(modid = DragonTweaksV2.MODID, bus = EventBusSubscriber.Bus.MOD)`

| Field | Type | Purpose |
|-------|------|---------|
| `BUILDER` | `ModConfigSpec.Builder` | Used to declare config values |
| `SPEC` | `ModConfigSpec` | Final built spec; registered in [[DragonTweaksV2-Main]] |

**`onLoad(ModConfigEvent event)`** — Reacts to config load/reload on the mod event bus. Cache values into local fields here for performance.

### Adding Values (Pattern)
```java
public static final ForgeConfigSpec.BooleanValue MY_FLAG =
    BUILDER.comment("Description").define("myFlag", true);
// declared before SPEC = BUILDER.build()
```

Config file lands at `config/dragontweaksv2-common.toml` (type `COMMON`).

## Relationships
- [[DragonTweaksV2-Main]] — registers `Config.SPEC` with `ModContainer`
- [[DragonTweaksV2Client]] — `ConfigurationScreen` renders this spec as an in-game GUI
- [[NeoForge-Patterns]] — mod bus vs Forge bus; config registration pattern
