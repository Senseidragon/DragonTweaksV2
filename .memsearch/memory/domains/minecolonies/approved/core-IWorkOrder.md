---
title: MineColonies — IWorkOrder interface (building construction and repair orders)
domain: minecolonies
fact: IWorkOrder represents a single pending construction, upgrade, or repair task queued in the colony work manager. Builders poll the work manager for unclaimed orders, claim one, then execute it step by step using the blueprint. Each order tracks current and target building levels, the schematic path, claim state, and construction progress stage. Key methods: getID, getPriority/setPriority, getWorkOrderType (BUILD/UPGRADE/REPAIR/REMOVE), getCurrentLevel/getTargetLevel, getStructurePack/getStructurePath, getLocation, getRotationMirror, isClaimed, getClaimedBy/setClaimedBy, loadBlueprint, getBlueprint/setBlueprint/clearBlueprint, getBoundingBox, getStage (BuildingProgressStage), getDisplayName.
confidence: 0.95
usefulness: high
supersedes: core-IWorkOrder.md
authority: authoritative
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: fef88a775a25debbf07bf6c4863157d3428df72a52ab725a46133694b2e7ffb8
validated_at: 2026-05-31T23:43:31.933806+00:00
approval_route: auto
---

`IWorkOrder` represents a single pending construction, upgrade, or repair task queued in the colony's work manager. Builders poll the work manager for unclaimed orders, claim one, then execute it step by step using the blueprint. Each order tracks current and target building levels, the schematic path, claim state, and construction progress stage.

**Key API surfaces:**
- `getID()` — unique work order ID within the colony
- `getPriority()` / `setPriority(int)` — builder assignment priority
- `getWorkOrderType()` — `WorkOrderType` enum: BUILD, UPGRADE, REPAIR, REMOVE
- `getCurrentLevel()` / `getTargetLevel()` — building level before and after this order
- `getStructurePack()` / `getStructurePath()` — schematic pack name and relative path
- `getLocation()` — `BlockPos` of the building being constructed
- `getRotationMirror()` — `RotationMirror` for schematic orientation
- `isClaimed()` — true if a builder has already taken this order
- `getClaimedBy()` / `setClaimedBy(BlockPos)` — builder hut position that owns this order
- `loadBlueprint(Level)` — loads the `Blueprint` from the schematic pack
- `getBlueprint()` / `setBlueprint(Blueprint)` / `clearBlueprint()` — in-memory blueprint access
- `getBoundingBox()` — `AABB` covering the full structure footprint
- `getStage()` — `BuildingProgressStage` (CLEAR, BUILD, DECORATE, SPAWN, COMPLETE)
- `getDisplayName()` — `Component` shown in the builder's work order UI

**Useful for:** reading queued construction tasks, checking what a builder is working on, implementing custom work order inspection or prioritization logic.

**Does not prove:** how to create or queue a work order (use `IBuilding.requestUpgrade/requestRepair`); builder AI internals; schematic loading details.

**Source:** [[docs/api/minecolonies/colony/workorders/IWorkOrder.java]]
