---
title: MineColonies — ICitizenData interface (citizen state, job, family, handlers)
domain: minecolonies
fact: ICitizenData is the server-side data record for a single colony citizen. It holds job assignment, home and work building references, happiness/skill/disease handlers, family relationships, sleep and saturation state, and the link to the live entity. Implements ICivilianData, IQuestGiver, and IQuestParticipant. Key methods: getJob/setJob, getWorkBuilding/getHomeBuilding, getEntity (Optional), getCitizenSkillHandler, getCitizenHappinessHandler, getCitizenDiseaseHandler, getCitizenFoodHandler, setSaturation, getJobStatus/setJobStatus, setVisibleStatus, isAsleep/setAsleep, scheduleRestart, applyResearchEffects, getPartner/getChildren/getParents, isRelatedTo, needsBetterFood, onDeath.
confidence: 0.95
usefulness: high
supersedes: core-ICitizenData.md
authority: authoritative
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: bc9d6db4fe9a45c7a5c10de0f2570c17151b9f8461c3588f2a21b7359bd35e46
validated_at: 2026-05-31T23:43:31.801049+00:00
approval_route: auto
---

`ICitizenData` is the server-side data record for a single colony citizen. It holds job assignment, home and work building references, happiness/skill/disease handlers, family relationships, sleep and saturation state, and the link to the live entity. Implements `ICivilianData`, `IQuestGiver`, and `IQuestParticipant`.

**Key API surfaces:**
- `getJob()` / `setJob(IJob)` — current job assignment; setJob(null) fires onRemoval on the old job
- `getWorkBuilding()` / `getHomeBuilding()` — assigned buildings (may be null)
- `getEntity()` — `Optional<AbstractEntityCitizen>` — live entity reference (absent if chunk unloaded)
- `getCitizenSkillHandler()` — read and modify citizen skill levels
- `getCitizenHappinessHandler()` — happiness breakdown per modifier
- `getCitizenDiseaseHandler()` — disease state and immunity
- `getCitizenFoodHandler()` — food consumption and saturation tracking
- `setSaturation(double)` — directly set food saturation (capped at `MAX_SATURATION`)
- `getJobStatus()` / `setJobStatus(JobStatus)` — visible work status shown in UI
- `setVisibleStatus(VisibleCitizenStatus, BlockPos)` — set the status icon shown above citizen
- `isAsleep()` / `setAsleep(boolean, BlockPos)` — sleep state
- `scheduleRestart()` — flag the citizen entity for a full AI restart next tick
- `applyResearchEffects()` — recompute research bonuses for this citizen
- `getPartner()` / `getChildren()` / `getParents()` — family graph access
- `isRelatedTo(ICitizenData)` — check family relationship
- `needsBetterFood()` — true if citizen is requesting higher-tier food
- `onDeath(DamageSource)` — trigger death logic; colony handles respawn scheduling

**Useful for:** reading citizen state from mod events, modifying job or building assignments, checking happiness and skill levels, managing citizen lifecycle.

**Does not prove:** how citizen requests are created (use `IBuilding.createRequest`); client-side `ICitizenDataView` fields; entity pathfinding details.

**Source:** [[docs/api/minecolonies/colony/ICitizenData.java]]
