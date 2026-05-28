---
tags:
  - java
  - architecture
  - entry-point
  - neoforge
source: src/main/java/io/github/senseidragon/dragontweaksv2/DragonTweaksV2.java
---

# DragonTweaksV2 (Main Entry Point)

The `@Mod`-annotated class that NeoForge uses to bootstrap the mod.

## Package
`io.github.senseidragon.dragontweaksv2`

## Annotations
- `@Mod(DragonTweaksV2.MODID)` — registers this class as the mod entry point

## Constants

| Field | Type | Value |
|-------|------|-------|
| `MODID` | `String` | `"dragontweaksv2"` |
| `LOGGER` | `Logger` | SLF4J logger via `LogUtils.getLogger()` |

## Constructor

`DragonTweaksV2(IEventBus modEventBus, ModContainer modContainer)`

Responsibilities:
1. Adds `commonSetup` listener to the **mod event bus**
2. Registers `this` with `NeoForge.EVENT_BUS` (Forge/game bus) for gameplay events
3. Registers `Config.SPEC` with `modContainer` (type: `ModConfig.Type.COMMON`)

## Methods

### `commonSetup(FMLCommonSetupEvent event)`
- Bus: **mod event bus**
- Currently logs `"DragonTweaks V2 common setup complete."`
- Cross-side setup code goes here (registries, data attachment, etc.)

### `onServerStarting(ServerStartingEvent event)`
- Bus: **Forge/game event bus**
- Currently logs `"DragonTweaks V2 loaded on server."`
- Entry point for server-side initialization

## Key Dependencies

| Import | Role |
|--------|------|
| `IEventBus` | Mod event bus, injected by NeoForge |
| `NeoForge.EVENT_BUS` | Forge/game bus for gameplay events |
| `FMLCommonSetupEvent` | Setup lifecycle, common to both sides |
| `ServerStartingEvent` | Server lifecycle hook |
| `ModContainer` | Used to register config spec |

## Related Notes
- [[DragonTweaksV2Client]] — client-only counterpart
- [[Config]] — config spec registered here
- [[NeoForge-Patterns]] — explanation of the two-bus pattern
