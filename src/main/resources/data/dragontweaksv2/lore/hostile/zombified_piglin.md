---
topic: Zombified Piglin
type: advisor-artifact
source: "[[https://minecraft.wiki/w/Zombified_Piglin]]"
scraped: 2026-06-09
version: 1.21.1
pipeline_stage: advisor-artifact
---

# Zombified Piglin

A neutral undead mob with 20 HP found in the Nether and occasionally spawning from Nether portals in the Overworld. Completely passive until attacked -- but attacking one triggers a mass aggro in a 67x22x67 area. Once hostile, they remain hostile for 20-55 seconds after losing line of sight AND being outside follow range. Player death resets their hostility (default gamerule).

## Stats

- Health: 20 HP (leaders: 40-100 HP on Hard)
- Armor: 2 points
- Golden sword: Easy 5 HP, Normal 8 HP, Hard 12 HP
- Follow range: 33-55 blocks when hostile
- Speed: 0.23 idle, 0.24 angry

## Spawning

Natural Nether spawn: Nether wastes (groups of 4, most common), Nether fortresses (groups of 4), crimson forests (groups of 2-4). Also:
- Nether portals in Overworld: 0.05%/0.1%/0.15% chance per portal block per random tick (Easy/Normal/Hard) -- spawns directly in portal, ignores mob cap
- Lightning within 4 blocks of a pig converts it to a zombified piglin
- Piglins or piglin brutes spending 15 sec in Overworld/End convert (keeping gear)
- Strider jockey: 3.3% of adult striders spawn with a rider (uses warped fungus on a stick)

5% chance to spawn as baby. Fireproof -- can spawn on magma blocks.

## Aggro Mechanics

A single hit on any zombified piglin (that doesn't one-shot it) triggers all zombified piglins within a **67x22x67** area to become hostile toward the attacker. Exceptions: goats and ghasts do not trigger aggro.

Once hostile, a zombified piglin stays aggressive as long as it has line of sight to the player AND the player is within follow range. Breaking either condition starts a forgiveness timer (20-55 sec on Java, 25 sec always on Bedrock). The timer does NOT advance if the piglin's chunk is unloaded -- portaling away and returning preserves their hostility.

**Player death** resets all hostile zombified piglins to neutral (if `forgive_dead_players` gamerule is true, default).

Hostile piglins can "alarm" other neutral ones every 4-6 seconds (recruits those in 67x22x67 range with line of sight to the player).

Hostile zombified piglins pathfind more aggressively -- will fall off ledges to reach their target.

## Notable Behavior

- Does NOT attack villagers or wandering traders (unlike regular zombies)
- CAN turn villagers into zombie villagers if they hit one
- When angered, normal piglins are NOT also angered (separate faction)
- 5% spawn with a golden spear; spear users back away between charge attacks

## Undead Properties

- Fire immune
- Damaged by Instant Health, healed by Instant Damage
- Smite enchantment effective
- Ignored by the Wither

## Drops

- Rotten flesh
- Gold nugget
- Golden sword (8.5% if held, +1% per Looting)
