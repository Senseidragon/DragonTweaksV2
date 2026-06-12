---
topic: Blaze
type: advisor-artifact
source: "[[https://minecraft.wiki/w/Blaze]]"
scraped: 2026-06-09
version: 1.21.1
pipeline_stage: advisor-artifact
---

# Blaze

A fiery hostile mob with 20 HP exclusive to Nether fortresses. Blazes are the only source of blaze rods, fire volley attackers that alert all other blazes within 48 blocks when hit, and immune to fire damage. Snow golems deal 3 HP per snowball to blazes -- one of the few practical counters.

## Stats

- Health: 20 HP
- Attack (fireball): Easy 3.5 HP, Normal 5 HP, Hard 7.5 HP + 1 HP/sec fire (5 sec)
- Attack (melee contact): Easy 4 HP, Normal 6 HP, Hard 9 HP
- Target/alert range: 48 blocks

## Spawning

Nether fortresses only. Spawn at light level 11 or less. Two blaze spawners generate in every Nether fortress on small platforms surrounded by Nether brick fences with a 3-block staircase.

Natural spawns also occur within fortress structure bounds (any block) and within the broader fortress area bounding box (Nether bricks only).

## Behavior

Blazes target players within 48 blocks. When damaged, they alert all other blazes within 48 blocks to also target the attacker.

Attack pattern: float upward, fire a volley of 3 small fireballs over 0.9 seconds, then wait 5 seconds before attacking again. Blazes shoot only when they have a clear line of sight. If line of sight breaks mid-volley, the blaze pauses the remaining shots until line of sight returns.

Blaze fireballs cannot be deflected (unlike ghast fireballs or breeze wind charges). They can be blocked by a shield.

Melee contact attack (when touching the target) deals physical damage -- this is NOT fire damage and is NOT mitigated by Fire Resistance. Fire Resistance blocks the fireball's ignition DoT but not the contact attack.

If a neutral or hostile mob is accidentally hit by a blaze's fireball, that mob retaliates against the blaze.

## Snow Golem Counter

Snow golems deal 3 HP per snowball to blazes (unique to blazes -- snowballs deal 0 to everything else). This makes snow golems a practical blaze-killing tool in the Nether if given Fire Resistance. A snow golem in the Nether without Fire Resistance dies almost instantly from the biome heat.

## Drops

- Blaze rod (only source, dropped only when killed by player or tamed wolf)
