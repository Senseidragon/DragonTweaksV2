# NeoForge Patterns

- Event subscribers go on the **mod bus** or **game/Forge bus** as appropriate — mixing them up silently fails.
- Deferred registers must be created before `registerEventBus()` is called.
- Mixins and Access Transformers are currently commented out in `neoforge.mods.toml`; re-enable the relevant lines before using them.
- Generated resources from `runData` land in `src/generated/resources/`.

← [[Architecture]]
