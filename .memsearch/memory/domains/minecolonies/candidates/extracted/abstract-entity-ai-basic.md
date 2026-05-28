**Title:** MineColonies AbstractEntityAIBasic — custom worker AI base class
**Type:** fact
**Intent triggers:** AbstractEntityAIBasic, AbstractAISkeleton, AITarget, AIEventTarget, worker AI, custom AI, IAIState, AIWorkerState, state machine, citizen AI
**Source/evidence:** com.minecolonies.core.entity.ai.workers.AbstractEntityAIBasic (cloned source version/1.21), AIWorkerState enum (stub)
**Rule or fact:** Custom worker AIs extend AbstractEntityAIBasic<J, B> where J is the paired job class and B is the associated building class. State transitions are declared via AITarget/AIEventTarget registered in the constructor.

Class signature:
```java
public abstract class AbstractEntityAIBasic<J extends AbstractJob<?, J>, B extends AbstractBuilding>
    extends AbstractAISkeleton<J>
```

State machine pattern:
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

Core AIWorkerState values used by all worker AIs:
- `IDLE` — default resting state
- `INIT` — first-run initialization
- `START_WORKING` — begin work cycle
- `PREPARING` — gather required items
- `NEEDS_ITEM` — waiting for request system to fulfil item request
- `INVENTORY_FULL` — need to dump inventory
- `DECIDE` — pick next task
- `PAUSED` — externally paused

Constants from AbstractEntityAIBasic:
- `STANDARD_DELAY = 5` ticks between actions
- `REQUEST_DELAY = TICKS_20 * 3` (60 ticks) between request checks
- `NO_TOOL = -10` sentinel for missing tool slot

Useful protected fields:
- `BlockPos currentWorkingLocation` — block the AI is currently targeting

Key utility methods inherited:
- `walkToBlock(BlockPos)` — pathfind to a position; returns true while walking
- `dumpInventory()` — request citizen dumps inventory at building
- `holdEssentialItems()` — prevent dumping of essential held items
- `getOwnBuilding()` — returns the typed B building

**Version scope:** MineColonies 1.1.1299 / NeoForge 21.1.x / MC 1.21.1
**Confidence:** 0.90
**Status:** candidate
**Date:** 2026-05-28
