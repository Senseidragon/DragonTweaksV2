---
title: Minecraft -- Ancient City (deep dark structure, Warden source, exclusive loot)
domain: minecraft
fact: Ancient City is a massive ~220-block-footprint structure at Y=-51 in deep dark biomes only; sculk shrieker density is higher here than anywhere else, making Warden summons faster; gray/colored wool floors reduce vibration and are exploitable for stealth; exclusive loot includes echo shards, Swift Sneak books, Ward/Silence armor trims, and the second-highest enchanted golden apple rate in the game.
confidence: 0.90
usefulness: high
authority: authoritative
source_url: https://minecraft.wiki/w/Ancient_City
source_version: "1.21.1"
source_type: official_wiki
format: distilled-advisor-block
---

**Role:** Extreme-danger deep dark structure; sole natural source of Swift Sneak and several exclusive items; Warden-dense environment requiring maximum stealth.

## Generation

- Deep Dark biome only, always at Y=-51
- Footprint ~220 blocks per horizontal axis -- one of the largest structures in the game
- Cannot overlap another ancient city, but can generate directly adjacent to one; worth checking for a second city nearby
- Often found below mountains and terrain with low erosion (deep dark requires this terrain profile)

## Structure

- Long corridors with 2-block-deep floors of gray wool; side areas use blue, light blue, and cyan wool or carpet
- **Wool and carpet floors reduce vibrations** -- movement on these surfaces generates less vibration than stone, reducing sculk sensor trigger range; intentional design, fully exploitable
- City center features a frame of reinforced deepslate -- unobtainable in Survival, cannot be mined; do not attempt
- Sculk shriekers generate significantly more frequently here than in normal deep dark -- Warden summons are faster and more likely
- Basement beneath the city center contains redstone circuits with piston doors:
  - city_center_1: sculk sensor triggers door open; closes 180 game ticks after sensor deactivates
  - city_center_2: **only eating or drinking (vibration frequency 8) opens this door** -- all other vibrations are filtered; this is the only way in
  - city_center_3: T flip-flop toggle -- each sculk sensor activation opens or closes the door alternately
- Ice box (rare sub-structure): stairs leading up to a room; **stone pressure plates at the entrance activate note blocks** -- stepping on them creates a vibration event that can trigger nearby sculk sensors; approach from the side or crouch past

## Threat

- No naturally spawning mobs other than the Warden (via sculk shrieker activation)
- Sculk shrieker density is the highest of any location in the game; Warden can be summoned faster here than anywhere else
- Wool floors help suppress vibration, but corridor layout and dense shrieker population make sound discipline critical throughout

## Loot

**Regular chests (scattered throughout ruins):**
- Echo shards -- ~30% per chest; needed to craft the recovery compass
- Disc Fragment 5 -- ~30% per chest; assembles into Music Disc 5
- Ward armor trim smithing template -- ~5% per chest; exclusive to ancient city
- Silence armor trim smithing template -- ~1.2% per chest; exclusive to ancient city
- Swift Sneak enchanted book -- ~24% per chest; **only natural source in the game**
- Enchanted Golden Apple -- ~8.6% per chest; second highest rate of any loot table (ominous vaults are higher at ~22.5%)
- Enchanted diamond leggings, enchanted diamond hoe, diamond horse armor
- Enchanted iron leggings, potions of regeneration, sculk/sculk sensor/sculk catalyst
- Music Disc (13), (cat), (otherside)

**Ice box chests (rare sub-structure, separate loot table):**
- Snowballs (very common), packed ice, baked potato, golden carrot, suspicious stew

## Notes

- Strongholds can overlap ancient cities (both underground); if locating a stronghold near deep dark terrain, watch for sculk density before mining blindly
- The eating/drinking trick (city_center_2) is non-obvious and the only way to open that specific door -- combat, walking, and mining will not work
- Two adjacent ancient cities are rare but possible; an unusually large city footprint may be two merged structures
