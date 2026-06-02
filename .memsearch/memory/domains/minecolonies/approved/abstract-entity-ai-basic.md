---
title: MineColonies — AbstractEntityAIBasic (custom worker AI base class)
domain: minecolonies
fact: AbstractEntityAIBasic<J, B> is the base class for all custom worker AIs. J is the paired job class and B is the paired building class. State transitions are declared via AITarget/AIEventTarget registered in the constructor via super.registerTargets(...). Provides utility methods for pathfinding, inventory dumping, tool requesting, and item requesting. Key constants/fields: STANDARD_DELAY=5, REQUEST_DELAY=60 ticks, NO_TOOL=-10, currentWorkingLocation. Key methods: walkToBlock, dumpInventory, holdEssentialItems, getOwnBuilding, checkForToolOrWeapon, checkForToolOrWeaponAsync, checkIfRequestForItemExistOrCreateAsynch, canGoIdle. Core AIWorkerState values: IDLE, INIT, START_WORKING, PREPARING, NEEDS_ITEM, INVENTORY_FULL, DECIDE, PAUSED.
confidence: 0.95
usefulness: high
supersedes: abstract-entity-ai-basic.md
authority: authoritative
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: 969454ab0c3a13e3db9ff23237e1034d1d3ba83c0a3c18db72a7fe9704c03a3b
validated_at: 2026-05-31T23:43:31.730114+00:00
approval_route: auto
---

`AbstractEntityAIBasic<J, B>` is the base class for all custom worker AIs. `J` is the paired job class and `B` is the paired building class. State transitions are declared via `AITarget`/`AIEventTarget` registered in the constructor via `super.registerTargets(...)`. The class provides utility methods for pathfinding, inventory dumping, tool requesting, and item requesting — the scaffolding every worker AI needs.

**Key API surfaces:**
- Class signature: `public abstract class AbstractEntityAIBasic<J extends AbstractJob<?, J>, B extends AbstractBuilding> extends AbstractAISkeleton<J>`
- `STANDARD_DELAY = 5` — default ticks between state transitions
- `REQUEST_DELAY = TICKS_20 * 3` — ticks between request-system polls (60 ticks)
- `NO_TOOL = -10` — sentinel for missing tool slot
- `currentWorkingLocation` (protected `BlockPos`) — block the AI is currently targeting
- `walkToBlock(BlockPos)` — pathfind to position; returns true while still walking
- `dumpInventory()` — request citizen dumps inventory at building; returns INVENTORY_FULL state
- `holdEssentialItems()` — prevent essential items from being dumped
- `getOwnBuilding()` — returns the typed `B` building instance
- `checkForToolOrWeapon(EquipmentTypeEntry, int, int)` — verify tool in inventory or request one
- `checkForToolOrWeaponAsync(...)` — async (non-blocking) tool request variant
- `checkIfRequestForItemExistOrCreateAsynch(ItemStack, int, int, boolean)` — item request helper
- `canGoIdle()` — override to return true when the worker has no queued tasks

**State machine registration pattern:**
```java
public MyWorkerAI(final J job) {
    super(job);
    super.registerTargets(
        new AITarget(IDLE, START_WORKING, 1),
        new AITarget(START_WORKING, this::startWorking, TICKS_20),
        new AIEventTarget(AIBlockingEventType.AI_BLOCKING, this::shouldDump, this::dump, TICKS_20)
    );
}
```

**Core AIWorkerState values:** IDLE, INIT, START_WORKING, PREPARING, NEEDS_ITEM, INVENTORY_FULL, DECIDE, PAUSED.

**Useful for:** implementing custom worker AI classes, requesting tools or items during AI states, pathfinding to work positions, triggering inventory dumps.

**Does not prove:** how to register a custom job (see `JobEntry.Builder`); guard AI specifics; pathfinding configuration details.

**Source:** [[docs/api/minecolonies/core/entity/ai/workers/AbstractEntityAIBasic.java]]
