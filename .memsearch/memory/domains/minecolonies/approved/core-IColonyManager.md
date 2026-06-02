---
title: MineColonies — IColonyManager interface (colony lookup, creation, deletion)
domain: minecolonies
fact: IColonyManager is the singleton for all cross-colony operations: finding, creating, and deleting colonies. Access it via IColonyManager.getInstance() or IMinecoloniesAPI.getInstance().getColonyManager(). It is side-neutral — on servers it returns live IColony objects; on clients it returns IColonyView proxies. Key methods: getInstance, createColony, deleteColonyByWorld, deleteColonyByDimension, getColonyByWorld, getColonyByDimension, getColonyByPosFromWorld, getClosestColony, getIColony, getAllColonies, getColonies, isFarEnoughFromColonies, isCoordinateInAnyColony, getBuilding, getCompatibilityManager, getRecipeManager.
confidence: 0.95
usefulness: high
supersedes: core-IColonyManager.md
authority: authoritative
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: 6ab1c173a9bfad1ab6f9a334cbb4cac381d1bda8e36eb6187da305323aeefbb9
validated_at: 2026-05-31T23:43:31.845287+00:00
approval_route: auto
---

`IColonyManager` is the singleton for all cross-colony operations: finding, creating, and deleting colonies. Access it via `IColonyManager.getInstance()`. It is side-neutral — on servers it returns live `IColony` objects; on clients it returns `IColonyView` proxies. Obtain it from `IMinecoloniesAPI.getInstance().getColonyManager()`.

**Key API surfaces:**
- `getInstance()` — static entry point
- `createColony(ServerLevel, BlockPos, Player, String, String)` — found a new colony at a position
- `deleteColonyByWorld(int, boolean, ServerLevel)` / `deleteColonyByDimension(...)` — remove a colony
- `getColonyByWorld(int, Level)` / `getColonyByDimension(int, ResourceKey<Level>)` — look up by ID
- `getColonyByPosFromWorld(Level, BlockPos)` — find the colony that owns a world position
- `getClosestColony(Level, BlockPos)` — nearest colony by distance
- `getIColony(Level, BlockPos)` — side-neutral lookup; returns view on client, colony on server
- `getAllColonies()` — all colonies across all dimensions
- `getColonies(Level)` — colonies in one world
- `isFarEnoughFromColonies(Level, BlockPos)` — validate new colony placement distance
- `isCoordinateInAnyColony(Level, BlockPos)` — check if a position is already claimed
- `getBuilding(Level, BlockPos)` — look up a building by world position directly
- `getCompatibilityManager()` — mod compatibility interface
- `getRecipeManager()` — colony-wide recipe data

**Useful for:** finding which colony owns a position, iterating all colonies, creating or removing colonies programmatically, side-neutral colony access in event handlers.

**Does not prove:** `IColony` sub-manager access (use `IColony` after retrieval); chunk claim internals; `ColonyView`-specific packet handling.

**Source:** [[docs/api/minecolonies/colony/IColonyManager.java]]
