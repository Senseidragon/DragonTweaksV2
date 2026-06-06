---
source_url: https://minecraft.wiki/w/Ocean_Monument
retrieved_at: 2026-06-04T18:21:13.992Z
source_version: "1.21.1"
tool: firecrawl
format: markdown
content_hash: 454c1372c31137230271b5710b58cf39c3cef986d7a96f46125dd3c7773d7b0c
source_type: official_wiki
repaired_at: 2026-06-05
repair_reason: original entry truncated -- only infobox stub present; generation, mob spawning, and structure details were missing
---

# Ocean Monument

Rare underwater structure in deep ocean biomes. Inhabited by guardians and elder guardians. Only
source of sponges and one of two sources of prismarine (other: ocean ruins). Contains a hidden
treasure chamber with 8 blocks of gold.

## Overview

- Location: Deep ocean biome variants
- Biomes: Deep Ocean, Deep Frozen Ocean, Deep Cold Ocean, Deep Lukewarm Ocean, Deep Warm Ocean (BE only)
- Inhabitants: Guardians (area spawn), Elder Guardians (3 fixed)
- Materials: Prismarine, prismarine bricks, dark prismarine, sea lanterns, block of gold
- Does NOT generate in existing chunks (generates only in new chunks)

## Generation

- Requires deep ocean biome within 16-block radius of center point
- Maximum 1 monument per 512×512 block area
- BE: roof always at Y=56; surrounding seafloor is cut off if it's higher than the monument base
- Can be located with ocean explorer maps (bought from apprentice-level cartographer villagers)
- Can overwrite other structures (e.g., strongholds) during generation

## Mobs

### Elder Guardians (Fixed)

Always 3 elder guardians per monument:
- 1 at the top of the central section
- 1 in each wing (one per wing)

These do not respawn if killed. Elder guardians inflict Mining Fatigue III on nearby players.
In BE, the Mining Fatigue particle is a separate entity called the elder guardian ghost.

### Guardians (Area Spawn)

The 58×58 block area around the monument spawns guardians exclusively. This is the only natural
spawn location for guardians. Guardian spawning persists indefinitely; they do not stop spawning
after the elder guardians are killed.

Spawning area: approximately 3,364 square blocks around the monument center.

## Structure

Built entirely from prismarine variants and lit by sea lanterns. Layout:
- Large central section (randomized chambers) flanked by 2 wings
- 23 giant pillars extending from base to ocean floor
- Main entrance at the front of the central section

### Central Section

Contains randomly generated chambers. Every monument has at minimum 6 rooms. Rooms are arranged
uniquely per monument. Small random chambers are scattered throughout. The treasure chamber is
at the heart of the central section, encased in dark prismarine.

### Sponge Rooms (Optional)

Some monuments contain one or more sponge rooms with approximately 30 wet sponges on the ceiling.
Sponge rooms have a floor opening only. Not guaranteed to generate.

### Wings

- **Wing 1**: Large room with a small pillar at center; contains the second elder guardian.
- **Wing 2**: Large open space with a large square-shaped platform; contains the third elder guardian.

### Treasure Chamber

Located deep in the central section. Contains 8 blocks of gold (the only "loot" in the monument).
The gold block room is typically sealed by dark prismarine.

## Key Blocks

| Block | Role |
|-------|------|
| Prismarine | Main exterior and room walls |
| Prismarine Bricks | Decorative/structural |
| Dark Prismarine | Treasure chamber, accents |
| Sea Lantern | Only light source |
| Block of Gold | Treasure (8 total, treasure chamber) |
| Wet Sponge | Found in sponge rooms (ceiling) |

## Identifiers

- JE structure type: `ocean_monument`
- JE/BE structure: `monument`
