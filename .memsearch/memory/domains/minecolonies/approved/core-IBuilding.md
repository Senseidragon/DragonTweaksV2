---
title: MineColonies — IBuilding interface (colony building, requests, modules, lifecycle)
domain: minecolonies
fact: IBuilding is the server-side interface for every colony building. It manages the building's block position, level, construction state, module system (settings, crafting, worker assignment), and request resolver registration. Key methods: getID, getBuildingLevel/getMaxBuildingLevel, isBuilt/isPendingConstruction, requestUpgrade/requestRepair/requestRemoval, markDirty, onColonyTick, onUpgradeComplete, createRequest, overruleNextOpenRequestOfCitizenWithStack, cancelAllRequestsOfCitizenOrBuilding, hasWorkerOpenRequests, getOpenRequestsOfType, getAllAssignedCitizen, canAssignCitizens, getSetting, isInBuilding, getModulesByType.
confidence: 0.95
usefulness: high
supersedes: core-IBuilding.md
authority: authoritative
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: fe2abcc68e156a11c9cf344502ab0999a44d460a985b0abe07c3b63926816d1c
validated_at: 2026-05-31T23:43:31.781440+00:00
approval_route: auto
---

`IBuilding` is the server-side interface for every colony building. It manages the building's block position, level, construction state, module system (settings, crafting, worker assignment), and request resolver registration. Most mod interaction with buildings goes through this interface or its subtype `IBuildingWorker`.

**Key API surfaces:**
- `getID()` — `BlockPos` of the building's hut block (unique key within the colony)
- `getBuildingLevel()` / `getMaxBuildingLevel()` — current and maximum upgrade level
- `isBuilt()` / `isPendingConstruction()` — construction state flags
- `requestUpgrade(Player, BlockPos)` / `requestRepair(BlockPos)` / `requestRemoval(Player)` — queue construction work orders
- `markDirty()` — flag the building for save and client sync
- `onColonyTick(Colony)` — called each colony tick; modules may override
- `onUpgradeComplete(int)` — called after a successful upgrade; notify modules
- `createRequest(ICitizenData, IRequestable, boolean)` — submit an item/tool request for a citizen
- `overruleNextOpenRequestOfCitizenWithStack(ICitizenData, ItemStack)` — manually complete a pending request
- `cancelAllRequestsOfCitizenOrBuilding(ICitizenData)` — cancel all open requests for a citizen
- `hasWorkerOpenRequests(ICitizenData)` — check if a citizen has unresolved requests
- `getOpenRequestsOfType(ICitizenData, TypeToken)` — typed request query
- `getAllAssignedCitizen()` — list of citizens assigned to this building
- `canAssignCitizens()` — whether more workers can be assigned
- `getSetting(ISettingKey)` — read a typed building setting
- `isInBuilding(BlockPos)` — check if a position is inside the building's bounding box
- `getModulesByType(Class)` — retrieve all modules of a given type

**Useful for:** checking construction state, queuing upgrades/repairs, managing item requests for workers, reading building settings, iterating assigned citizens.

**Does not prove:** how module implementations work internally; how to register a custom building type (see `BuildingEntry`); client-side `IBuildingView` fields.

**Source:** [[docs/api/minecolonies/colony/buildings/IBuilding.java]]
