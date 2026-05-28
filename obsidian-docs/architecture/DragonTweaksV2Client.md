---
tags:
  - java
  - architecture
  - client-only
  - neoforge
source: src/main/java/io/github/senseidragon/dragontweaksv2/DragonTweaksV2Client.java
---

# DragonTweaksV2Client (Client-Only Entry Point)

Client-side mod class. Must **never** be loaded on a dedicated server.

## Package
`io.github.senseidragon.dragontweaksv2`

## Annotations
- `@Mod(value = DragonTweaksV2.MODID, dist = Dist.CLIENT)` — client-only mod class
- `@EventBusSubscriber(modid = DragonTweaksV2.MODID, value = Dist.CLIENT)` — subscribes to mod bus on client only

## Constructor

`DragonTweaksV2Client(ModContainer container)`

Responsibilities:
1. Registers `ConfigurationScreen` factory with `IConfigScreenFactory` so the in-game config GUI works

## Methods

### `onClientSetup(FMLClientSetupEvent event)`
- Bus: **mod event bus** (client-only)
- Currently logs `"DragonTweaks V2 client setup complete."`
- Client-only setup goes here: keybindings, render layers, HUD elements, etc.

## Safety Guarantee

The `@Dist.CLIENT` annotation on both the class and the subscriber ensures NeoForge's classloading never touches this class on a dedicated server. Never reference this class from common code.

## Key Dependencies

| Import | Role |
|--------|------|
| `Dist.CLIENT` | Restricts loading to physical client |
| `FMLClientSetupEvent` | Client-side setup lifecycle event |
| `ConfigurationScreen` | NeoForge built-in config GUI |
| `IConfigScreenFactory` | Factory interface for config screen registration |

## Related Notes
- [[DragonTweaksV2-Main]] — common/server counterpart
- [[Config]] — config spec displayed by this screen
- [[NeoForge-Patterns]] — Dist separation pattern
