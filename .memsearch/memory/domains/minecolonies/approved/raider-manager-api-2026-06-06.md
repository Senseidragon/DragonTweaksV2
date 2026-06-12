---
title: MineColonies — IRaiderManager (raid state, scheduling, spawn mechanics)
domain: minecolonies
fact: IRaiderManager governs raid state, scheduling, difficulty, and spawn mechanics. Accessed via colony.getRaiderManager(). Key queries: isRaided(), willRaidTonight(), getNightsSinceLastRaid(), getColonyRaidLevel(). isColonyUnderAttack() on IColony is a direct shortcut for the same check.
confidence: 0.92
usefulness: high
authority: authoritative
---

## API Chain

```java
IRaiderManager raider = colony.getRaiderManager();

// State queries
boolean isRaided        = raider.isRaided();
boolean raidTonight     = raider.willRaidTonight();
int     nightsSinceLast = raider.getNightsSinceLastRaid();
int     raidLevel       = raider.getColonyRaidLevel();      // scales with colony size
double  difficulty      = raider.getRaidDifficultyModifier(); // default 1.0
int     raiderCount     = raider.calculateRaiderAmount(raidLevel);

// Shortcut on IColony (same check as isRaided)
boolean attacked = colony.isColonyUnderAttack();

// Scheduling
raider.setCanHaveRaiderEvents(true);
raider.setRaidNextNight(RaidSettings.defaultRaidSettings());

// Spawn telemetry
BlockPos          spawnPos   = raider.calculateSpawnLocation();
List<BlockPos>    lastSpawns = raider.getLastSpawnPoints();
BlockPos          target     = raider.getRandomBuilding();

// Aftermath
int lostCitizens = raider.getLostCitizen();
raider.setNightsSinceLastRaid(0);
```

## Key Facts

- `isRaided()` is the correct method for "is a raid active right now" — use this in diagnostic checks.
- `colony.isColonyUnderAttack()` is a direct equivalent shortcut on `IColony`.
- `willRaidTonight()` is useful for proactive advisor warnings before a raid begins.
- `getColonyRaidLevel()` scales with colony population — drives difficulty.
- No `RaidStartedEvent` stub was confirmed in the V2 API files (as of 2026-06-06). Use polling via `isRaided()` state change detection if event-driven notification is needed.

**Source:** [[docs/api/minecolonies/colony/managers/interfaces/IRaiderManager.java]]
