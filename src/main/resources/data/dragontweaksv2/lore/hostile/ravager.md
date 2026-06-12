---
topic: Ravager
type: advisor-artifact
source: "[[https://minecraft.wiki/w/Ravager]]"
scraped: 2026-06-09
version: 1.21.1
pipeline_stage: advisor-artifact
---

# Ravager

A massive raid-exclusive illager beast with 100 HP and devastating melee and roar attacks. Ravagers are the structural threat of raids -- they destroy crops and many plants on contact, and their roar knocks back everything nearby. The primary colony annihilation vector during high-wave raids.

## Stats

- Health: 100 HP
- Attack (bite): Easy 7 HP, Normal 12 HP, Hard 18 HP
- Attack (roar): Easy 4 HP, Normal 6 HP, Hard 9 HP (knockback 5 blocks)
- Speed: 0.4
- Knockback resistance: 70-75%
- Hitbox: 2.2 blocks tall, 1.95 blocks wide

## Spawning

Raid-only. Never spawns outside raids. Wave composition:

**Normal:**
- Wave 3: 1 unridden ravager
- Wave 5: ravager ridden by a pillager
- Wave 6 (extra): 1-2 unridden ravagers

**Hard:**
- Wave 3: 1 unridden ravager
- Wave 5: ravager ridden by a pillager
- Wave 7: 2 ravagers (one with vindicator, one with evoker rider)
- Wave 8 (extra): same as wave 7

## Behavior

Ravagers charge and bite. Their bite can be blocked by a shield -- blocking deals no damage but drains significant shield durability, and the ravager has a 50% chance to become stunned for 2 seconds (grey/purple particles). After the stun, the ravager opens its mouth and roars.

The roar:
- Deals 6 HP (Normal) and knocks back all nearby entities 5 blocks
- Knocks back nearby illagers (no damage to illagers)
- Has no effect on other ravagers
- Illagers knocked back by the roar still take fall damage

## Block Destruction

Ravagers destroy blocks by charging through them. Core agricultural impact:
- Beetroots, carrots, potatoes, wheat: always destroyed (Java and Bedrock)
- All leaves: always destroyed
- Pitcher crop, torchflower crop: Java only

An enormous list of additional decorative blocks (saplings, flowers, bamboo, mushrooms, vines, pumpkins, melons, sugar cane, etc.) are destroyed on Bedrock. On Java, only the crops and leaves listed above.

Disabled if `mob_griefing` game rule is false.

## Rider Mechanics

Ravagers can spawn with illager riders (pillager, vindicator, evoker). If a ridden ravager enters water 2+ blocks deep, the rider is dismounted and cannot remount. Riders that fall off cannot be ridden by the player.

Ravagers themselves cannot be ridden by the player without commands.

## Shield Counter

Blocking a ravager's bite with a shield is the main counter to its melee. Shield durability drops significantly per block but the stun window lets players attack freely for 2 seconds before the roar knockback.

## Cross-Mob Interactions

- Ravagers ignore villagers (they target players, iron golems, wandering traders, and adult villagers -- but the primary raid threat is structural destruction)
- A "Johnny" vindicator causes a ravager to target and damage itself
- Other illagers knocked back by a ravager roar take fall damage

## Drops

- Saddle (always)
