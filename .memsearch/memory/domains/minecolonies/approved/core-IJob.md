---
title: MineColonies — IJob interface (citizen job contract)
domain: minecolonies
fact: IJob is the server-side contract every MineColonies citizen job must satisfy. It ties a JobEntry registry key to a paired AI class, manages the action counter used to trigger inventory dumps, and coordinates lifecycle events (wake, death, removal, offline time). Custom jobs extend AbstractJob rather than implementing IJob directly. Key methods: generateAI (abstract, returns new AI instance), createAI (wires AI into citizen entity), assignTo (binds job to work building module), getJobRegistryEntry, getWorkerAI, incrementActionsDone/clearActionsDone, markRequestSync, onRemoval, onWakeUp, isIdling, allowsAvoidance, getDiseaseModifier, isGuard, getInactivityLimit.
confidence: 0.95
usefulness: high
supersedes: core-IJob.md
authority: authoritative
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: 27b2c8839e4417832832aecfec718066c3824bdb0206f879d5dcd6477f1d8e6f
validated_at: 2026-05-31T23:43:31.867292+00:00
approval_route: auto
---

`IJob` is the server-side contract every MineColonies citizen job must satisfy. It ties a `JobEntry` registry key to a paired AI class, manages the action counter used to trigger inventory dumps, and coordinates lifecycle events (wake, death, removal, offline time). Custom jobs extend `AbstractJob` (which implements this) rather than implementing `IJob` directly.

**Key API surfaces:**
- `generateAI()` — the one abstract method subclasses must implement; returns a new AI instance
- `createAI()` — called by the colony to wire the AI into the citizen entity
- `assignTo(IAssignsJob)` — binds the job to its work building module; validates registry entry match
- `getJobRegistryEntry()` — returns the `JobEntry` identifying this job type
- `getWorkerAI()` — returns the live AI instance attached to the citizen entity
- `incrementActionsDone()` / `clearActionsDone()` — action counter used to trigger inventory dumps
- `markRequestSync(IToken)` — converts an async request to blocking; triggers citizen interaction UI
- `onRemoval()` — called on job change or citizen death; clean up AI state and building assignment
- `onWakeUp()` — called each colony day start; resets food-check flag
- `isIdling()` — true when AI is in `AIWorkerState.IDLE`
- `allowsAvoidance()` — whether pathfinder may use avoidance; default true
- `getDiseaseModifier()` — disease risk multiplier; default 1.0
- `isGuard()` — default false; guard jobs override to true
- `getInactivityLimit()` — seconds before job considers itself inactive; default -1 (disabled)

**Useful for:** implementing custom citizen jobs, reading job state from colony data, diagnosing idle or stuck workers, configuring disease/avoidance behavior per job type.

**Does not prove:** how to register a JobEntry (see `JobEntry.Builder` in `colony/jobs/registry/`); how the AI state machine works (see `AbstractEntityAIBasic`); how request fulfilment flows (see `IRequestManager`).

**Source:** [[docs/api/minecolonies/colony/jobs/IJob.java]]
