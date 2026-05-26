# Dragon Tweaks — Threading Rules

## Invariant

Nothing may block the Minecraft main thread or server thread.

## GUI and render paths

GUI and render display paths must read cached data only. They must not perform live blocking queries against world state, warehouse stock, or any synchronous I/O.

## Intent triggers

Apply this rule when implementing anything involving:

- panels or diagnostic displays
- warehouse stock lookups
- world queries from a render or tick context
- advisor-style UI panels
- async task dispatch or results
- any display path that could reach world or block state

## Event bus threading

- Mod bus events fire during load, not during game ticks. Safe for registration and config.
- Game bus events may fire on the server thread. Do not perform client-only operations inside game bus handlers.
- Client setup events (`FMLClientSetupEvent`) must use `event.enqueueWork()` for any work that must run on the main thread.

## Current state

No async or threading code exists in the codebase yet (2026-05-25). This rule applies when any such code is added.
