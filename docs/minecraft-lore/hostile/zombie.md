---
topic: Zombie
type: advisor-artifact
source: "[[https://minecraft.wiki/w/Zombie]]"
scraped: 2026-06-09
version: 1.21.1
pipeline_stage: advisor-artifact
---

# Zombie

A common undead hostile mob with 20 HP that spawns at light level 0. Zombies are melee fighters, can break down wooden doors on Hard, convert villagers, and call reinforcements when attacked. The primary nightly threat to an undefended settlement.

## Stats

- Health: 20 HP (base); leaders spawn with 20-80 extra HP (40-100 HP total)
- Attack: Easy 2.5 HP, Normal 3 HP, Hard 4.5 HP (melee punch; weapon adds damage on top)
- Speed: 0.23 (baby: 0.35)
- Armor: 2 natural armor points (1.6-8% damage reduction)
- Knockback resistance: 0-5%

Armed zombies deal weapon damage on top of punch damage. An iron shovel zombie on Normal deals 6.5 HP total.

## Spawning

Groups of 2-4 at light level 0. Not in mushroom fields or deep dark. Desert biomes replace 70-80% of zombie spawns with husks. Each zombie spawn has a 5% chance of being a zombie villager. Baby zombies make up 5% of spawns.

Special sources:
- Zombie siege at midnight (up to 20) if player is near a village
- Monster room spawners (50% of rooms)
- Trial chamber spawners (1/3 melee slot)
- Husk that drowns converts to zombie; zombie that drowns for 30 seconds converts to drowned
- Leader zombies (up to 5% chance on Normal/Hard) have extra HP and high reinforcement chance

## Behavior

Pursues players and villagers within 35 blocks (can see through walls for villagers). Detects villagers up to 52.5 blocks on Hard. Detection range halved if player wears a zombie head.

Targets priority: if zombie sees both player and villager, it targets the player first. Once locked on a villager, it ignores the player -- unless the player approaches or attacks.

Zombies do NOT seek shelter from daylight; they burn from 27 seconds before dawn unless wearing a helmet, in water, cobwebs, shade (sunlight level 14 or less), or under Fire Resistance. Glass does not prevent burning. Burning zombies may ignite targets on hit (regional difficulty scaling).

Baby zombies are faster, fit through 1x1 gaps, never grow up, and can spawn as chicken jockeys.

## Door Breaking

Up to 10% of pursuing zombies pathfind through closed wooden and copper doors. On Hard, they can break doors down in ~10 seconds. They can only break the top half of a door -- a zombie cannot break a door if it's facing the bottom half. Iron doors are immune.

## Reinforcements

When attacked, all same-type zombies within a 67x21x67 area are alerted to target the attacker. On Hard, damaged zombies can spawn additional zombies nearby at light level 0. Leader zombies spawn up to 11-17 reinforcements. Reinforcements ignore mob caps and can spawn in biomes where zombies normally cannot.

## Villager Conversion

Any zombie variant can convert a villager it kills:
- Easy: 0%
- Normal: 50%
- Hard: 100%

The converted zombie villager can be cured (splash Weakness potion + golden apple). Only normal zombies and zombie villagers can convert; husks convert differently (zombie first, then drowned).

## Cross-Mob Interactions

- Iron golems attack zombies on sight within 42 blocks; zombies pursue iron golems within 42 blocks
- Zombies ignore snow golems unless hit by one
- Zombies attack baby turtles and seek out turtle eggs within 24 blocks horizontally, jumping on them to break them
- Zombies target wandering traders within 35 blocks (see through walls)
- Undead mobs: damaged by Instant Health, healed by Instant Damage; ignored by the Wither; Smite enchantment effective; armadillos hide from them

## Item Pickup

Some zombies can pick up dropped items, upgrading their gear. A zombie holding a picked-up item never despawns. A zombie that picks up a totem of undying will activate it on death -- the totem cannot be reclaimed by killing the zombie.

## Drops

- Rotten flesh (primary, always)
- Iron ingot, carrot, potato (rare)
- Zombie head (rare, requires charged creeper kill)
- Naturally-spawned gear: 8.5% chance (iron shovel, sword, or spear; +1% per Looting level)
- All gear drops at full durability when zombie converts to drowned
