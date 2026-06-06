---
source_url: https://minecraft.wiki/w/Bogged
retrieved_at: 2026-06-04T22:39:08.068Z
source_version: "1.21.1"
tool: firecrawl
format: markdown
content_hash: placeholder-bogged-repair
source_type: official_wiki
repaired_at: 2026-06-05
repair_reason: original entry truncated -- only infobox stub present; spawning, drops, and behavior were missing
---

# Bogged

A hostile undead mob that is a mushroom-covered variant of the skeleton. Shoots poison-tipped
arrows. Fires slower than a regular skeleton. Mushrooms can be sheared off. Found in swamps,
mangrove swamps, and trial chambers.

## Stats

- Health: 16 HP (8 hearts)
- Behavior: Hostile
- Mob type: Undead, Monster
- Hitbox (JE): 1.99 blocks tall, 0.6 blocks wide
- Hitbox (BE): 1.9 blocks tall, 0.6 blocks wide

## Attack Strength

Shoots arrows tipped with Poison (15 seconds, Poison I):
- Fires 1 arrow every 3.5 seconds (slower than skeleton's ~2 seconds)
- Selects optimal distance from target, similar to skeleton

## Spawning

### Swamps and Mangrove Swamps

- Replaces approximately 30% of skeletons spawning in swamp and mangrove swamp biomes
- JE: groups of 4, light level 0 only
- BE: groups of 1-2, light level 0-7 (can spawn in canopy shade during day)

### Trial Chambers

- Spawns from trial spawners as the ranged mob option
- 1/3 chance of being chosen as the ranged mob type in trial spawners
- Ominous trial spawner bogged do NOT drop equipment/armor on death (unlike regular bogged)

### Spider Jockeys

- BE only: can spawn as a spider jockey (riding a spider)

## Drops

### On Death

- Bones: 0-2 (avg 1.0; scales with Looting)
- Arrows: 0-2 (avg 1.0; scales with Looting)
- Arrows of Poison: 0-1 (50% base chance; scales with Looting)
- Bow: 8.5% base chance (scales with Looting +1% per level)
- 5 XP base + 1-3 XP per non-dropped equipment piece (typically 6-8 XP total)

Note: Bogged from ominous trial spawners do not drop armor, only base drops.

### Shearing

Using a pair of shears on a bogged removes the mushrooms:
- Drops 0-2 mushrooms (red or brown)
- After shearing, the bogged looks identical to a regular skeleton

## Behavior

### Poison Arrows

Inflicts Poison I for 15 seconds on hit. Poison does not kill -- targets cannot be reduced
below 1 HP by poison alone.

### Combat Pattern

Behaves like a standard skeleton: maintains range, strafes around target, retreats if too
close. Fires significantly slower (3.5-second intervals vs skeleton's ~2-second intervals).

### Sunlight

Burns in direct sunlight like all undead mobs. Wearing a helmet (even a pumpkin) prevents
burning.

### Undead Properties

- Damaged by Instant Health; healed by Instant Damage
- Unaffected by Poison and Regeneration
- Extra damage from Smite enchantment
- Affected by undead-specific interactions (e.g., armadillo hides from bogged)

## Sound Identifiers (Java Edition)

| Event | Identifier |
|-------|-----------|
| Ambient | entity.bogged.ambient |
| Death | entity.bogged.death |
| Hurt | entity.bogged.hurt |
| Step | entity.bogged.step |
| Shoot | entity.bogged.shoot |
| Shear | entity.bogged.shear |

## Trivia

- Added in Java Edition 1.21 (Tricky Trials) and Bedrock Edition 1.21.0.
- The first new skeleton variant added to the game since the stray (1.10).
- Shearing a bogged reveals it is structurally identical to a regular skeleton underneath.
