# Dragon Tweaks — Architecture Rules

## Source of truth
`src/main/java/io/github/senseidragon/dragontweaksv2/`

## Entry points

| Class | Role |
|---|---|
| `DragonTweaksV2.java` | `@Mod` entry point. Registers config spec with NeoForge. Subscribes to server lifecycle events on the mod event bus. |
| `DragonTweaksV2Client.java` | Client-only. `@Mod(dist=Dist.CLIENT)`. Registers `IConfigScreenFactory`. Must never be loaded on a dedicated server. |
| `Config.java` | Wraps `ModConfigSpec`. `@EventBusSubscriber(Bus.MOD)`. Listens for `ModConfigEvent` on reload. |

## Hard splits

- Common logic lives in `DragonTweaksV2.java` and `Config.java`.
- All client-only code lives in `DragonTweaksV2Client.java` and is gated by `Dist.CLIENT`.
- No client-only imports may appear in common classes.

## Event bus routing

- Mod lifecycle events (`FMLCommonSetupEvent`, `FMLClientSetupEvent`, `ModConfigEvent`) → **mod bus**
- Game/world events (`ServerStartingEvent`, registration events) → **game/Forge bus** (`NeoForge.EVENT_BUS`)
- Mixing mod bus and game bus silently fails.

## Deferred registers

- `DeferredRegister` instances must be created before `registerEventBus()` is called.

## Disabled features

- Mixins: currently commented out in `neoforge.mods.toml`. Re-enable the `[[mixins]]` block and provide a mixin config file before use.
- Access Transformers: currently commented out in `neoforge.mods.toml` and `build.gradle`. Re-enable `[[accessTransformers]]` and provide the AT file before use.

## Package root

`io.github.senseidragon.dragontweaksv2`

## Mod ID

`dragontweaksv2`
