---
topic: Slime
type: advisor-artifact
source: "[[https://minecraft.wiki/w/Slime]]"
scraped: 2026-06-09
version: 1.21.1
pipeline_stage: advisor-artifact
---

# Slime

A cube-shaped hostile mob that spawns in three natural sizes and splits into smaller ones on death. Large slimes are dangerous; small slimes deal 0 damage but still chase players. Primary source of slimeballs. Move by jumping. Spawns in swamps (moon-phase dependent) and underground slime chunks.

## Sizes and Stats

| Size | HP | Attack (Easy/Normal/Hard) | Hitbox |
|------|----|--------------------------|--------|
| Large | 16 HP | 3 / 4 / 6 HP | 2.08 x 2.08 blocks |
| Medium | 4 HP | 2 / 2 / 3 HP | 1.04 x 1.04 blocks |
| Small | 1 HP | 0 HP | 0.52 x 0.52 blocks |

On death: large splits into 2-4 medium; medium splits into 2-4 small; small drops slimeballs and XP only.

Regional difficulty affects size distribution: from ~33% each at low difficulty, to 50% large / 33% medium / 16% small at high difficulty.

## Spawning

**Swamps / Mangrove Swamps**: Y=51-69, light level 7 or below. Spawn chance scales with moon phase -- most on full moon, impossible on new moon. 50% base spawn chance once conditions are met. This light-level threshold (7) is higher than most hostile mobs (0), enabling swamp slime farms in dimly lit but not pitch-dark areas.

**Slime chunks**: ~1 in 10 chunks (determined by world seed) spawn slimes below Y=40 at any light level and any time. Groups of 4.

**Trial chambers**: 25% chance to select slimes as "small melee" mob. Spawn as medium and semi-large sizes only. Ominous trials: 1-in-7 chance to dispense Oozing potions -- an affected entity that dies spawns 2 medium slimes.

Spawning requires a 2.04x2.04x2.04 clear space -- even a glass pane blocks spawning.

## Behavior

- Always hostile; moves by jumping toward its target
- Attacks on contact (damage on touch)
- Small slimes pursue players but deal no damage -- they are harmless individually but disorienting in groups
- Cannot be leashed
- Immune to fall damage (bounces)

## Drops

- Slimeball (small slimes only; 0-2 per kill)
- Large/medium: XP only (no direct item drops before splitting)
