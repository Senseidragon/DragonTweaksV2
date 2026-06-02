---
title: MineColonies — IColony interface (colony state, managers, world lifecycle)
domain: minecolonies
fact: IColony is the central interface for a MineColonies colony. It exposes identity (ID, name, dimension, center position), all manager sub-systems (citizens, buildings, raiders, requests, research, quests), world lifecycle hooks, and NBT persistence. Both the server Colony and client ColonyView implement it; use isRemote() to distinguish sides. Key methods: getID, getCenter, getDimension, isRemote, getState/isActive, getCitizenManager, getServerBuildingManager, getCommonBuildingManager, getRequestManager, getResearchManager, getRaiderManager, getQuestManager, getDay, getOverallHappiness, markDirty, isCoordInColony, getPermissions, write/read, getSettings.
confidence: 0.95
usefulness: high
supersedes: core-IColony.md
authority: authoritative
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: a5bd4cad8f279fe3b9735047661cc22d4e77ea6728f2cdea6299d366380c4417
validated_at: 2026-05-31T23:43:31.826318+00:00
approval_route: auto
---

`IColony` is the central interface for a MineColonies colony. It exposes identity (ID, name, dimension, center position), all manager sub-systems (citizens, buildings, raiders, requests, research, quests), world lifecycle hooks, and NBT persistence. Both the server `Colony` and client `ColonyView` implement it; use `isRemote()` to distinguish sides.

**Key API surfaces:**
- `getID()` — unique integer colony ID
- `getCenter()` — `BlockPos` of the Town Hall
- `getDimension()` — `ResourceKey<Level>` the colony lives in
- `isRemote()` — true on client (ColonyView); false on server
- `getState()` / `isActive()` — `ColonyState` enum and active flag
- `getCitizenManager()` — enumerate and manage citizens
- `getServerBuildingManager()` / `getCommonBuildingManager()` — look up buildings by position
- `getRequestManager()` — access the colony's request/delivery system
- `getResearchManager()` — completed and available research
- `getRaiderManager()` — raid state and attacker tracking
- `getQuestManager()` — quest progress and assignment
- `getDay()` — current colony day counter
- `getOverallHappiness()` — aggregate happiness (0–10 scale)
- `markDirty()` — schedule colony save and client sync
- `isCoordInColony(Level, BlockPos)` — check if a position is within colony bounds
- `getPermissions()` — rank and access-control interface
- `write(CompoundTag, HolderLookup.Provider)` / `read(...)` — NBT persistence
- `getSettings()` — `ICommonSettingsModule` for colony-level settings

**Useful for:** querying colony state from event handlers, accessing sub-managers, checking territory bounds, triggering saves after mod-side changes.

**Does not prove:** how to find a colony by position or ID (use `IColonyManager`); client-side view-only fields; packet handling in `ColonyView`.

**Source:** [[docs/api/minecolonies/colony/IColony.java]]
