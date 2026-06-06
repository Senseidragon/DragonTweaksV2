---
source_url: https://minecraft.wiki/w/Bastion_Remnant
retrieved_at: 2026-06-04T18:08:03.040Z
source_version: "1.21.1"
tool: firecrawl
format: markdown
content_hash: 3e24a74ad808a8e31d6fac1ec88adb77b3bf9f1670acef50b4b807f69e679183
source_type: official_wiki
repaired_at: 2026-06-05
repair_reason: original entry truncated -- only Trivia section present; generation, structure, mobs, and loot were missing
---

# Bastion Remnant

Large castle-like generated structures in the Nether. Inhabited by piglins, piglin brutes, and
hoglins. The only source of gilded blackstone, Pigstep music disc, snout banner pattern/armor trim,
magma cube spawners, and the netherite upgrade smithing template.

## Overview

- Location: All Nether biomes EXCEPT basalt deltas (may extend into one)
- 4 variants: bridge, hoglin stables, housing units, treasure room
- If a bastion would generate in a basalt delta, a Nether fortress generates instead

## Generation

Bastions use a region-based system shared with Nether fortresses:
- Region size: 432×432 blocks (JE) / 480×480 blocks (BE)
- One structure (bastion OR fortress) per region; 4-chunk buffer on south/east borders
- Effective generation zone: 368×368 blocks (JE) / 416×416 blocks (BE) per region
- Chance bastion generates over fortress: 60% (JE) / 66.6% (BE)
- All 4 bastion variants have equal 25% chance

## Mobs

### On Generation (Non-Respawning)

Piglins, piglin brutes, and hoglins spawn when the structure generates:
- Do NOT despawn naturally after generation
- Do NOT hunt each other (piglins and hoglins coexist peacefully)
- If killed, they do NOT respawn
- Piglins and hoglins continue to spawn from normal biome spawning after structure generation
- Piglin brutes spawn EXCLUSIVELY in bastion remnants (no other natural spawn)

### Normal Spawning

After the structure generates, normal biome mob spawning continues in the surrounding area.
New piglins and hoglins (but not piglin brutes) can spawn normally in the biome.

## Structure Variants

### Bridge

Large ruined rampart with a piglin face carved into it. Inside the "mouth": multiple levels of
walkways surrounded by lava. A damaged bridge extends from the main structure. Hoglins may spawn
on generation.

Key features:
- Polished blackstone brick ramparts with piglin face motif
- Lava-surrounded interior walkways
- Chest loot: bridge chests (gold armor, golden sword, crossbow, iron ingots, spectral arrows)

### Hoglin Stables

Large barn-like structure designed to house hoglins. Multiple hoglins spawn here on generation.

Key features:
- Open stalls with hay-like areas
- Hoglin spawner areas
- Chest loot: stables chests (food, leather, saddles, gold nuggets/ingots)

### Housing Units

Residential-style structure with multiple floors and room divisions.

Key features:
- Multiple rooms and corridors
- Chest loot: generic/unit chests (gold armor, arrows, string, various items)

### Treasure Room

The rarest and most valuable variant. Features a central treasure room guarded by piglin brutes
and surrounded by gold blocks.

Key features:
- Central chest surrounded by blocks of gold
- Magma cube spawner
- Heavily guarded by piglin brutes
- Chest loot: treasure chests (best loot -- ancient debris, netherite, enchanted gear,
  Pigstep music disc, netherite upgrade template, snout armor trim)

## Exclusive Loot

Only found in bastion remnants:
- **Gilded Blackstone** (generated as building material)
- **Pigstep Music Disc** (treasure room chests only)
- **Snout Banner Pattern** (treasure room chests)
- **Snout Armor Trim Smithing Template** (treasure room chests)
- **Netherite Upgrade Smithing Template** (treasure room chests)
- **Magma Cube Spawner** (treasure room, fixed structure)

## Chest Loot Categories

| Zone | Notable Items |
|------|--------------|
| Bridge | Gold armor (enchanted), golden sword, crossbow, spectral arrows, iron ingots |
| Hoglin Stables | Cooked porkchop, leather, saddle, gold nuggets/ingots |
| Housing Units | Gold armor, arrows, string, gold nuggets |
| Generic (all) | Gold nuggets, iron nuggets, cooked porkchop, leather |
| Treasure | Ancient debris, blocks of gold, enchanted diamond armor, Pigstep disc, netherite upgrade template, snout armor trim |

## Trivia

- The `bastion/hoglin_stable/starting_pieces/stairs_1_mirrored` structure is NOT stored as mirrored
  despite its name; this has no effect on generation due to jigsaw block placement.
- In BE, unused structure files include blocks not added to the game: `blackstone_bricks`,
  `blackstone_brick_stairs`, `cracked_blackstone_bricks`, `chiseled_blackstone_bricks`.
- A test structure `bastion/jigsaw_test` in BE contains signs, jigsaw blocks with invalid
  orientation states, stained glass, and stone — leftover developer test artifact.
- Buried sections and ores can generate within or alongside bastions; glowstone blobs can appear
  in the air space of the structure.
