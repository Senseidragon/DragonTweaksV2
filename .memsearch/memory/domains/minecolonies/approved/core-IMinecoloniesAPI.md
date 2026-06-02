---
title: MineColonies — IMinecoloniesAPI interface (mod entry point, all top-level registries)
domain: minecolonies
fact: IMinecoloniesAPI is the static entry point for the entire MineColonies API. Access it via IMinecoloniesAPI.getInstance(), which returns the MinecoloniesAPIProxy singleton. All top-level registries (jobs, buildings, guard types, research, crafting, quests, happiness) are accessed from here, as is the IColonyManager. Key methods: getInstance, getColonyManager, getJobRegistry, getJobDataManager, getBuildingRegistry, getBuildingDataManager, getGuardTypeRegistry, getGlobalResearchTree, getResearchRequirementRegistry, getResearchEffectRegistry, getCraftingTypeRegistry, getRecipeTypeRegistry, getEquipmentTypeRegistry, getHappinessTypeRegistry, getHappinessFunctionRegistry, getQuestRewardRegistry, getQuestObjectiveRegistry, getQuestTriggerRegistry, getConfig, getEventBus.
confidence: 0.95
usefulness: high
supersedes: core-IMinecoloniesAPI.md
authority: authoritative
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: 153c8dfa2cebe230dea7a4720f6a8482d9cb0eab297a0fc37879ec53f1df2a99
validated_at: 2026-05-31T23:43:31.889804+00:00
approval_route: auto
---

`IMinecoloniesAPI` is the static entry point for the entire MineColonies API. Access it via `IMinecoloniesAPI.getInstance()`, which returns the `MinecoloniesAPIProxy` singleton. All top-level registries (jobs, buildings, guard types, research, crafting, quests, happiness) are accessed from here, as is the `IColonyManager`.

**Key API surfaces:**
- `getInstance()` — static accessor returning the `MinecoloniesAPIProxy`
- `getColonyManager()` — the `IColonyManager` singleton
- `getJobRegistry()` — `DeferredRegister`-compatible job type registry
- `getJobDataManager()` — `IJobDataManager` for job data lookup
- `getBuildingRegistry()` — building type registry
- `getBuildingDataManager()` — building data lookup
- `getGuardTypeRegistry()` — guard type (knight, ranger, druid) registry
- `getGlobalResearchTree()` — the full MineColonies research tree
- `getResearchRequirementRegistry()` — custom research requirement types
- `getResearchEffectRegistry()` — custom research effect types
- `getCraftingTypeRegistry()` — crafting job type registry
- `getRecipeTypeRegistry()` — building recipe type registry
- `getEquipmentTypeRegistry()` — equipment slot / tool type registry
- `getHappinessTypeRegistry()` / `getHappinessFunctionRegistry()` — citizen happiness modifier registries
- `getQuestRewardRegistry()` / `getQuestObjectiveRegistry()` / `getQuestTriggerRegistry()` — quest system registries
- `getConfig()` — `Configurations<ClientConfiguration, ServerConfiguration, CommonConfiguration>`
- `getEventBus()` — MineColonies internal event bus

**Useful for:** obtaining the colony manager, accessing any MineColonies registry for reading or registration, retrieving the global research tree, reading config values.

**Does not prove:** how to register content against these registries (use NeoForge `DeferredRegister` pointing at the registry key); implementation details of any individual manager.

**Source:** [[docs/api/minecolonies/IMinecoloniesAPI.java]]
