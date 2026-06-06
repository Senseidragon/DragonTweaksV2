---
title: Minecraft — Fox (passive wildlife, livestock predator, item scavenger)
domain: minecraft
fact: Fox is a passive/neutral mob. 10 HP. Hunts chickens, rabbits, and fish; can jump fences. Scavenges any ground item. Red foxes prefer land prey, snow foxes prefer fish. Trusted foxes (bred by player) defend their player. mobGriefing=false disables scavenging. Low direct threat but relevant to open livestock pens and dropped items.
confidence: 0.90
usefulness: medium
authority: authoritative
source_url: https://minecraft.wiki/w/Fox
source_version: "1.21.1"
source_type: official_wiki
format: distilled-advisor-block
---

# Fox — Advisor Reference

A passive/neutral wildlife mob. Low direct threat; notable for item-scavenging behavior and trusted-defender mechanic that has colony implications.

## Stats
- **Health:** 10 HP (5 hearts)
- **Attack (melee):** Easy 2 HP / Normal 2 HP / Hard 3 HP
- **Detection range:** not specified; flees from wolves, polar bears, and non-trusted players on sight
- **Pounce range:** up to 4 blocks (jump attack on small prey)
- **Safe fall height:** 5 blocks before taking damage

## Variants
- **Red fox:** spawns in taiga, old growth taiga biomes; prefers land prey (chickens, rabbits, baby turtles)
- **Snow fox:** spawns in grove and snowy taiga biomes; prefers aquatic prey (cod, salmon, tropical fish)

## Spawn Conditions
- Groups of 2–4 on grass blocks, coarse dirt, podzol, or snow blocks
- Spawns at any light level in valid biomes
- No timing threshold — spawns naturally throughout the game

## Threat Assessment
- **Targets:** chickens, rabbits, cod, salmon, tropical fish, baby turtles; will actively hunt these
- **Does not target:** adult villagers, players (unless player attacks first)
- **Aggro mechanic:** flees from wolves, polar bears, and non-trusted players; will not flee from a trusted player
- **Trusted-defender behavior:** a fox that trusts the player will attack mobs that harm that player — but NOT attacks from iron golems, wolves, polar bears, bees, slimes, magma cubes, hoglins, or zoglins; also does not retaliate against projectiles. The trusted fox will still retreat from polar bears and untamed wolves even while defending.
- **Fox does not retaliate when struck directly:** hitting a fox does not cause it to attack back. It only fights on behalf of its trusted player.
- **Item-scavenging:** picks up any item on the ground; prioritizes food items; this can strip dropped resources or weapons from the environment
- **Totem of undying exploit:** if a fox is holding a totem of undying (either found or dropped), it will consume the totem on fatal damage — the totem is lost, not dropped

## Colony Relevance
- **Livestock threat:** foxes actively hunt chickens and rabbits; any open chicken or rabbit pen is a legitimate target
- **Item scavenging:** foxes will pick up items dropped by colonists or the player; a fox near a work site could lift dropped materials
- **Trust mechanic:** foxes bred in captivity trust the breeding player; their kits are tameable allies — a trusted fox defends its player

## Awareness & Detection
- Foxes sleep during the day (sitting, eyes closed) — passive and non-reactive while sleeping unless approached
- At night, foxes become active; roam toward villages
- Foxes scream at night unless a trusted player is nearby — audible alarm in proximity
- No creative mode or invisibility edge cases documented; flee behavior applies to all non-trusted entities

## Tactical Notes
- **Fence vulnerability:** foxes can jump over fences — standard fencing does not physically exclude them from livestock areas. However, foxes cannot see through fences; they will not aggro on a mob at the same y-level if a fence stands between them. A fenced pen stops aggro targeting even if it doesn't stop entry.
- **mobGriefing gamerule:** setting `mobGriefing` to `false` disables fox item pickup entirely — the scavenging threat is nullified server-side by this gamerule.
- **Sweet berry bushes:** foxes sprint toward stage 3–4 sweet berry bushes to eat them; immune to berry bush damage and movement slowdown — cannot be deterred with berry bushes
- **Weapon amplification:** a fox holding a sword uses its melee attack with that sword's damage — base 2–3 HP becomes much higher
- **Looting enchantment:** if a fox holds a sword with Looting, its drops benefit from that enchantment — affects what it drops, not colony mechanics directly
- **Breeding for trust:** sweet berries or glow berries trigger breeding; the resulting kit trusts the player who bred them — this is the only path to a trusted fox
- **Night scream:** the screaming behavior at night is audible and distinctive; a screaming fox near a colony is not a threat indicator but an environmental alert

## Drops
- Any item currently held in its mouth (100%)
- Chance of: emerald, rabbit's foot, rabbit hide, egg, wheat, leather, or feather (held-item pool; not all guaranteed)
