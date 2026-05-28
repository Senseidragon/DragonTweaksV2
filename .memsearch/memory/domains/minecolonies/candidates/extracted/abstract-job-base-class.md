**Title:** MineColonies AbstractJob base class — custom job implementation contract
**Type:** fact
**Intent triggers:** AbstractJob, custom job, IJob, generateAI, createAI, ICitizenData, JobEntry, job base class, implement job
**Source/evidence:** com.minecolonies.core.colony.jobs.AbstractJob (cloned source version/1.21)
**Rule or fact:** Custom jobs extend AbstractJob<AI, J> where AI is the paired AI class and J is the job class itself (self-referential). The only abstract method subclasses must implement is `generateAI()`.

Class signature:
```java
public abstract class AbstractJob<AI extends AbstractAISkeleton<J> & ITickingStateAI, J extends AbstractJob<AI, J>>
    implements IJob<AI>
```

Constructor:
```java
public AbstractJob(final ICitizenData entity)  // pass through to super
```

Abstract method subclasses must implement:
- `AI generateAI()` — returns a new instance of the paired AI class; called by `createAI()` which wires the result into the citizen entity

Optional overrides:
- `ResourceLocation getModel()` — defaults to `ModModelTypes.CITIZEN_ID`; override to use a custom citizen model
- `void triggerDeathAchievement(DamageSource, AbstractEntityCitizen)` — no-op by default
- `void processOfflineTime(long time)` — no-op by default; called when citizen was offline
- `boolean allowsAvoidance()` — returns true by default
- `double getDiseaseModifier()` — returns 1.0 by default

NBT fields serialized automatically by AbstractJob:
- `TAG_JOB_TYPE` — registry key string
- `asyncRequests` — set of async request tokens
- `actionsDone` — counter for inventory-dump trigger
- `workPos` — BlockPos of assigned work building

Key runtime fields (protected, accessible to subclasses):
- `BlockPos workBuildingPos`
- `IBuilding workBuilding`
- `IAssignsJob workModule`

`assignTo(IAssignsJob module)` validates the module's JobEntry matches this job's entry before assigning — mismatched entries return false silently.

**Version scope:** MineColonies 1.1.1299 / NeoForge 21.1.x / MC 1.21.1
**Confidence:** 0.95
**Status:** candidate
**Date:** 2026-05-28
