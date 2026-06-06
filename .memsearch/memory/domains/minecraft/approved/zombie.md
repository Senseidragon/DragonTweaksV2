---
source_url: https://minecraft.wiki/w/Zombie
retrieved_at: 2026-06-04T22:39:08.068Z
source_version: "1.21.1"
tool: firecrawl
format: markdown
content_hash: placeholder-zombie-repair
source_type: official_wiki
repaired_at: 2026-06-05
repair_reason: original entry truncated -- only infobox stub present; spawning, drops, and behavior were missing
---

# Zombie

The most common hostile undead mob. Spawns nearly everywhere at low light levels. Burns in
sunlight. Can break wooden doors on Hard difficulty. Can convert villagers into zombie
villagers. Converts to drowned when submerged. Can call reinforcements (JE only).

## Stats

- Health: 20 HP (10 hearts)
- Behavior: Hostile
- Mob type: Undead, Monster
- Speed: 0.23 (baby: 0.35)
- Knockback resistance: 0-5% (random per entity)

## Attack Strength

- Easy: 2.5 HP
- Normal: 3 HP
- Hard: 4.5 HP

Baby zombies deal the same damage as adults.

## Spawning

### Natural Spawning

- Any Overworld biome at light level 0, except mushroom fields and deep dark
- JE: groups of 4
- BE: groups of 2-4
- Requires 2-block vertical clearance
- 5% chance to spawn as a zombie villager
- 5% chance to spawn as a baby; baby has 4.75% chance to be a chicken jockey (JE)

### Desert Variants

In deserts, 80% (JE) / 70% (BE) of zombie spawns are husks instead.

### Zombie Sieges (JE Only)

Zombie sieges can occur when a player is within a village at night. Multiple zombies
spawn in and around the village regardless of light level.

### Other Sources

- Monster spawners (dungeons, etc.)
- Trial spawners (trial chambers)

## Drops

### On Death

- Rotten Flesh: 0-2 (avg 1.0; JE) / 0-2 (avg 1.0; BE); scales with Looting
- Iron Ingot: ~0.83% chance (player/wolf kill only; scales with Looting)
- Carrot: ~0.83% chance (player/wolf kill only; scales with Looting)
- Potato: ~0.83% chance (not when on fire/Fire Aspect; player/wolf kill only)
- Baked Potato: ~0.83% chance (only when on fire or killed with Fire Aspect)
- Zombie Head: 1 (only when killed by charged creeper)
- 5 XP base + 1-3 XP per non-dropped equipment piece

### Equipment Drops

Zombies can spawn with armor and weapons (chance increases with regional difficulty).
Each piece has an 8.5% base drop chance.

## Behavior

### Targeting

Pursues players from 35 blocks away. Detection range halved (17.5 blocks) when player
wears zombie head. Also targets:
- Villagers (35-52.5 blocks, can see through walls)
- Wandering traders (35 blocks, can see through walls)
- Iron golems (42 blocks)
- Baby turtles (and destroys turtle eggs)

JE: Prefers players over villagers when both visible. Ignores villagers while chasing player.
BE: Targets nearest of player/villager/golem.

### Breaking Doors

Up to 10% of zombies (scaled by regional difficulty) pathfind through closed wooden and
copper doors. On Hard difficulty, they can break them down (~10 seconds to break, not
affected by Haste or tools). Can only break the top half of a door.

### Burning in Sunlight

Burns when exposed to direct sunlight. Suppressed by:
- Sunlight level 14 or less in that area
- Being in water
- Wearing a helmet (50% chance per tick to lose 1 durability on the helmet)
- Fire Resistance effect
- Standing in cobwebs

Transparent blocks (e.g., glass) do NOT prevent burning.

### Reinforcements (JE Only)

On Hard difficulty, damaged zombies can spawn additional same-type zombies nearby:
- Normal zombies: 0-5% base reinforcement chance
- Leader zombies: 50-75% bonus chance (have 20-80 extra HP)
- Reinforcements spawn at light level 0, 7+ blocks away
- Can spawn in normally non-spawnable biomes (End, Nether, mushroom fields)
- Controlled by `spawn_reinforcements` attribute

### Villager Conversion

Attacking a villager may convert it to a zombie villager:
- Easy and Peaceful: 0% chance
- Normal: 50% chance
- Hard: 100% chance

### Converting to Drowned

After 30 seconds with head submerged in water, zombie begins converting (shakes for 15
seconds) then becomes a drowned. The process cannot be stopped once started. Drowned always
spawns at full health. Only normal zombies convert -- zombie villagers and zombified piglins
cannot. Husks convert to zombies first, then to drowned.

### Item Pickup

Zombies can pick up dropped items. They prefer better armor/weapons; will swap equipped
items. A zombie carrying a picked-up item does not despawn.

### Mob Type

Undead properties:
- Damaged by Instant Health; healed by Instant Damage
- Unaffected by Poison and Regeneration
- Affected by Smite enchantment
- Causes armadillos to roll up within 6 blocks

## Sound Identifiers (Java Edition)

| Event | Identifier |
|-------|-----------|
| Ambient | entity.zombie.ambient |
| Death | entity.zombie.death |
| Hurt | entity.zombie.hurt |
| Step | entity.zombie.step |
| Attack | entity.zombie.attack_wooden_door / entity.zombie.break_wooden_door |
| Infect | entity.zombie.infect |
| Convert (drowned) | entity.zombie.converted_to_drowned |

## Trivia

- Zombies are among the oldest mobs in Minecraft, added in Indev.
- Baby zombies are permanently babies and cannot grow up.
- Despite armor, zombies still burn in sunlight unless the helmet absorbs it.
