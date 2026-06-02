---
title: MineColonies — AbstractJob base class (custom job implementation contract)
domain: minecolonies
fact: AbstractJob<AI, J> is the concrete base class all custom jobs must extend. AI is the paired AI class and J is the job class itself (self-referential). The only abstract method subclasses must implement is generateAI(). All IJob lifecycle methods (NBT, removal, action counter, food tracking) are implemented here. Key methods: generateAI (abstract), getModel, triggerDeathAchievement, processOfflineTime, allowsAvoidance, getDiseaseModifier, assignTo. Protected fields: workBuildingPos, workBuilding, workModule. NBT fields serialized: TAG_JOB_TYPE, asyncRequests, actionsDone, workPos.
confidence: 0.95
usefulness: high
supersedes: abstract-job-base-class.md
authority: authoritative
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: a055778e9d991e27c079583a363152f58694c6792c83516835dc095db5c8a860
validated_at: 2026-05-31T23:43:31.746141+00:00
approval_route: auto
---

`AbstractJob<AI, J>` is the concrete base class all custom jobs must extend. `AI` is the paired AI class and `J` is the job class itself (self-referential). The only abstract method subclasses must implement is `generateAI()`. All `IJob` lifecycle methods (NBT, removal, action counter, food tracking) are implemented here.

**Key API surfaces:**
- Class signature: `public abstract class AbstractJob<AI extends AbstractAISkeleton<J> & ITickingStateAI, J extends AbstractJob<AI, J>> implements IJob<AI>`
- `AbstractJob(ICitizenData entity)` — constructor; pass through from subclass
- `generateAI()` — **the one abstract method subclasses must implement**; returns a new AI instance
- `getModel()` — defaults to `ModModelTypes.CITIZEN_ID`; override for a custom citizen skin
- `triggerDeathAchievement(DamageSource, AbstractEntityCitizen)` — no-op by default
- `processOfflineTime(long time)` — no-op by default; called when citizen was offline
- `allowsAvoidance()` — returns true by default
- `getDiseaseModifier()` — returns 1.0 by default
- `assignTo(IAssignsJob module)` — validates `JobEntry` match before assigning; returns false silently on mismatch
- Protected fields accessible to subclasses: `workBuildingPos`, `workBuilding`, `workModule`

**NBT fields serialized automatically:**
- `TAG_JOB_TYPE` — registry key string
- `asyncRequests` — set of async request tokens
- `actionsDone` — inventory-dump counter
- `workPos` — `BlockPos` of assigned work building

**Useful for:** implementing a custom job class, overriding model or death achievements, reading the assigned building position from the job.

**Does not prove:** how to register the job in the registry (see `JobEntry.Builder`); how the AI state machine works (see `AbstractEntityAIBasic`); how to create item requests.

**Source:** [[docs/api/minecolonies/core/colony/jobs/AbstractJob.java]]
