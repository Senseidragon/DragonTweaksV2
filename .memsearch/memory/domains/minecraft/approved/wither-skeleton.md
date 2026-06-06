---
source_url: https://minecraft.wiki/w/Wither_Skeleton
retrieved_at: 2026-06-04T22:39:08.068Z
source_version: "1.21.1"
tool: firecrawl
format: markdown
content_hash: 3bb7ae7da02e11e5757dd3b9904201a620bb2d6fd0c5682876338fb75ebb7a88
source_type: official_wiki
repaired_at: 2026-06-05
repair_reason: original entry truncated -- only infobox stub present; spawning, drops, behavior, and entity data were missing
---

# Wither Skeleton

A tall hostile undead mob found only in Nether Fortresses. Wields a stone sword and inflicts Wither
effect on hit. Only source of wither skeleton skulls and the only renewable source of coal.

## Stats

- Health: 20 HP (10 hearts)
- Behavior: Hostile
- Mob type: Undead, Monster
- Speed: 0.25 (idle) / 0.3125 (attacking)
- Hitbox (JE): 2.4 blocks tall, 0.7 blocks wide
- Hitbox (BE): 2.412 blocks tall, 0.864 blocks wide

## Attack Strength

**Armed (stone sword):**
- Easy: 5 HP (JE) / 5.5 HP (BE)
- Normal: 8 HP (JE) / 9 HP (BE)
- Hard: 12 HP (JE) / 13.5 HP (BE)

**Unarmed:**
- Easy: 3 HP
- Normal: 3 HP
- Hard: 4.5 HP

All attacks inflict Wither II for 10 seconds.

## Spawning

- Location: Nether Fortresses only
- Light level: 0-7 (JE) / any (BE)
- Group size: 5 (JE) / 2-3 (BE)
- Also spawned by the Wither boss at half health (BE only)

### Halloween Spawning (JE Only)

On Halloween (October 31), wither skeletons have a chance to spawn wearing pumpkins:
- 22.5% chance: carved pumpkin
- 2.5% chance: jack o'lantern
- These items are never dropped on death

### Spider Jockeys

- 1% chance to spawn as a spider jockey (riding a spider) in Nether (BE only)
- This mechanic was removed from JE

## Drops

### On Death

- Coal: ~2.5% base chance (scales with Looting)
- Bones: 0-2 (avg 0.33-1.83 with Looting I-III)
- Wither skeleton skull: 2.5% base (only when killed by a charged creeper explosion; not affected by Looting)
- Stone sword: 8.5% + 1% per Looting level (chance to drop equipped sword)
- 5 XP base + 1-3 XP per equipment piece not dropped (typically yields 6-8 XP total)

### Equipment

Spawns with:
- Stone sword (always)

## Behavior

### Targeting

Attacks:
- Players
- Iron golems
- Baby turtles
- Piglins
- Piglin brutes

Sprint-attacks when in range. Does not attack adult turtles, villagers, or snow golems.

### Wolf Avoidance

Flees from wolves (JE only, not present in BE).

### Wither Effect

Inflicts Wither II for 10 seconds on every hit. Wither effect deals 1 HP damage every 2 seconds and
turns hearts black, hiding the player's true health.

### Immunities

- Fire and lava damage
- Wither effect
- All undead mob interactions (smite enchantment deals extra damage)

### Size Constraints

At 2.4 blocks tall, wither skeletons cannot fit through 2-block-high gaps. They require 3-block-tall
openings to navigate freely.

### Pathfinding

Wither skeletons navigate standard Nether fortress corridors. They can open doors and will path
through fire to reach targets.

## Sound Identifiers (Java Edition)

| Event | Identifier |
|-------|-----------|
| Ambient | entity.wither_skeleton.ambient |
| Death | entity.wither_skeleton.death |
| Hurt | entity.wither_skeleton.hurt |
| Step | entity.wither_skeleton.step |

## Trivia

- Only renewable source of coal in the game.
- The 2.4-block height is intentional to prevent wither skeletons from easily escaping or navigating
  certain fortress rooms.
- Despite being undead, wither skeletons do not burn in sunlight (they remain in the Nether).
- Wither skeleton skulls are required to summon the Wither boss (3 skulls + 4 soul sand).
