---
tags:
  - neoforge
  - patterns
  - events
  - architecture
---

# NeoForge Patterns

Core patterns used throughout the mod. Violating these causes silent failures.

## Key Details

### Two-Bus Architecture

| Bus | Access | Events |
|-----|--------|--------|
| **Mod event bus** | `modEventBus` (injected into `@Mod` constructor) | `FMLCommonSetupEvent`, `FMLClientSetupEvent`, `ModConfigEvent`, registration events |
| **Forge/game bus** | `NeoForge.EVENT_BUS` | `ServerStartingEvent`, block/entity/player events |

Rule: FML lifecycle events go on the mod bus. Gameplay events go on the Forge bus. Mixing them silently does nothing.

### DeferredRegister

```java
// Create BEFORE calling registerEventBus()
public static final DeferredRegister<Block> BLOCKS =
    DeferredRegister.create(BuiltInRegistries.BLOCK, DragonTweaksV2.MODID);

// In @Mod constructor:
BLOCKS.register(modEventBus);
```

### Dist Separation

- `DragonTweaksV2.java` — common; runs on both sides
- `DragonTweaksV2Client.java` — `@Dist.CLIENT`; never loaded on dedicated server
- Never reference client classes from common code

### Mixins / Access Transformers

Currently commented out in `neoforge.mods.toml`. Re-enable before use:
```toml
# mixins = [{file="dragontweaksv2.mixins.json"}]
# accessTransformers = [{file="META-INF/accesstransformer.cfg"}]
```

### Config Registration

Must happen in the `@Mod` constructor (not static init):
```java
modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
```

## Relationships
- [[DragonTweaksV2-Main]] — applies two-bus pattern
- [[DragonTweaksV2Client]] — applies `@Dist.CLIENT` separation
- [[Config]] — config spec registration pattern
