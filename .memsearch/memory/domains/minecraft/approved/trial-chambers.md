---
source_url: https://minecraft.wiki/w/Trial_Chambers
retrieved_at: 2026-06-04T22:39:08.068Z
source_version: "1.21.1"
tool: firecrawl
format: markdown
content_hash: placeholder-trial-chambers-repair
source_type: official_wiki
repaired_at: 2026-06-05
repair_reason: original entry truncated -- only Trivia present; generation, structure, loot, and mob data were missing
---

# Trial Chambers

An uncommon underground structure serving as a mid-game combat challenge. The only natural
source of trial spawners, vaults, breezes, breeze rods, heavy cores, and maces. Contains
exclusive loot including the bolt armor trim, guster banner pattern, and Music Disc "Precipice".

## Overview

- Location: Underground Overworld (all biomes except deep dark)
- Y-level: Starting room at -40 to -20; most rooms between Y=-20 and Y=0
- Structure origin (corridor_end) cannot generate in deep dark, but parts can intersect it
- Generates only in new chunks (post-JE 1.21 / BE 1.21.0)

## Generation

Trial chambers follow a 34x34-chunk region grid centered on world origin (X=0, Z=0).
One trial chambers structure is guaranteed per region at a random location, unless a
deep dark biome cancels the generation.

### Locating

Trial explorer maps can be purchased from journeyman-level cartographer villagers for
12 emeralds. These maps mark the nearest trial chambers.

## Mobs

Spawned from trial spawners (not natural spawns):
- Zombie, Baby Zombie, Husk
- Skeleton, Stray, Bogged
- Spider, Cave Spider
- Slime, Silverfish
- Breeze (exclusive to trial chambers; only source of breeze rods)
- Chicken (rarely via jockeys in JE, or from dispenser eggs)

## Structure

Built primarily from copper blocks (all oxidation stages) and tuff bricks.

### Main Components

**Corridors:** The primary connecting passages between areas. Types:
- Entrance: Entry corridor with pressure plates and dispensers
- Atrium: Open multi-level central hub
- Slices: Modular corridor segments (multiple variants)
- End: Terminal corridor sections

**Intersections:** Junction rooms connecting multiple corridors

**Hallways:** Narrower connecting passages with encounters (spider webs, etc.)

**Chambers:** The primary combat rooms containing trial spawners. Multiple variants
(small, medium, large, special rooms)

**Miscellaneous:**
- Decor rooms (no combat)
- Dispenser traps
- Loot containers (chests, barrels, decorated pots)
- Trial spawner configurations

### Key Blocks

| Block | Role |
|-------|------|
| Copper (all stages) | Primary structural material |
| Tuff Bricks | Secondary structural material |
| Trial Spawner | Combat mechanic (unique to structure) |
| Vault | Locked loot container (unique to structure) |
| Dispenser | Traps (arrows, potions) |
| Barrel | Loot containers |
| Chest | Loot containers |
| Decorated Pot | Loot containers |
| Candle | Lighting |

## Loot (Vaults and Reward Chests)

Vault loot draws from 3 pools. Key items:

**Common:**
- Emeralds
- Arrows (regular and Tipped Arrow of Poison)
- Iron Ingots
- Wind Charges
- Honey Bottles
- Ominous Bottles (levels I-II)

**Uncommon:**
- Damaged Shield
- Enchanted Bow
- Diamonds
- Golden Apple, Golden Carrot
- Enchanted Books
- Enchanted Crossbow
- Enchanted Iron Axe
- Enchanted Iron Chestplate

**Rare/Exclusive:**
- Bolt Armor Trim (exclusive to trial chambers vaults)
- Music Disc "Precipice" (exclusive to trial chambers vaults)
- Guster Banner Pattern (exclusive to trial chambers vaults)
- Enchanted Diamond Axe
- Enchanted Diamond Chestplate
- Trident

**Ominous Vaults (better loot):**
- All of the above at higher rates
- Heavy Core (required for crafting mace -- exclusive)
- Additional rare enchanted gear

## Trial Spawners

Trial spawners are unique to trial chambers. They:
- Activate only when a player is nearby
- Spawn a set number of mobs based on player count
- Provide rewards (drop items on ground) after all spawned mobs are killed
- Ominous trial spawners (active during Trial Omen) are harder and give better rewards

## Exclusive Items

Items only obtainable in trial chambers:
- Breeze Rod (from breezes)
- Heavy Core (ominous vaults)
- Bolt Armor Trim (vaults/ominous vaults)
- Guster Banner Pattern (vaults)
- Music Disc "Precipice" (vaults)
- Trial Key / Ominous Trial Key (from trial spawner rewards)
- Wind Charge (crafted from breeze rod; also found in vaults)
- Mace (crafted from heavy core + breeze rod)

## Identifiers

- JE structure: `trial_chambers`
- BE structure: `trial_chambers`

## Trivia

- Added in Java Edition 1.21 (Tricky Trials) and Bedrock Edition 1.21.0.
- The first structure designed specifically around a new combat mechanic (trial spawners).
- All copper in trial chambers oxidizes naturally over time if the player does not use
  a wax or scrape it.
