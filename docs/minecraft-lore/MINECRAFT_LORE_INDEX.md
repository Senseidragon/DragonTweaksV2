# Minecraft Lore Index

**Domain:** `docs/minecraft-lore`

This domain contains curated Minecraft game-knowledge and lore for use as advisor context.

## Frontmatter Schemas

Two distinct schemas are in use — do not mix them:

**Advisor-artifact schema** (used by `passive/`, `neutral/`, `hostile/`, `npc/`, `utility/`):
```yaml
topic: Wolf
type: advisor-artifact
pipeline_stage: advisor-artifact
source: "[[https://minecraft.wiki/...]]"
scraped: 2026-06-09
version: 1.21.1
```
Files use `##` section headers, bullet lists, and tables throughout.

**Distilled-advisor-block schema** (used by `structures/`, `dimensions/`):
```yaml
title: Minecraft -- Ancient City (...)
domain: minecraft
fact: <one-line summary>
confidence: 0.90
usefulness: high
authority: authoritative
source_url: https://...
source_version: "1.21.1"
source_type: official_wiki
format: distilled-advisor-block
```
Files open with a `**Role:**` line followed by structured headers and bullets.

## Contents

### Passive Mobs (`passive/`) — 26 files

| File | Mob |
|------|-----|
| `passive/allay.md` | Allay |
| `passive/armadillo.md` | Armadillo |
| `passive/axolotl.md` | Axolotl |
| `passive/bat.md` | Bat |
| `passive/camel.md` | Camel |
| `passive/cat.md` | Cat |
| `passive/chicken.md` | Chicken |
| `passive/cod.md` | Cod |
| `passive/cow.md` | Cow |
| `passive/donkey.md` | Donkey |
| `passive/frog.md` | Frog |
| `passive/glow_squid.md` | Glow Squid |
| `passive/horse.md` | Horse |
| `passive/mooshroom.md` | Mooshroom |
| `passive/mule.md` | Mule |
| `passive/ocelot.md` | Ocelot |
| `passive/parrot.md` | Parrot |
| `passive/pig.md` | Pig |
| `passive/rabbit.md` | Rabbit |
| `passive/salmon.md` | Salmon |
| `passive/sheep.md` | Sheep |
| `passive/sniffer.md` | Sniffer |
| `passive/squid.md` | Squid |
| `passive/tadpole.md` | Tadpole |
| `passive/tropical_fish.md` | Tropical Fish |
| `passive/turtle.md` | Turtle |

### Neutral Mobs (`neutral/`) — 13 files

| File | Mob |
|------|-----|
| `neutral/bee.md` | Bee |
| `neutral/dolphin.md` | Dolphin |
| `neutral/enderman.md` | Enderman |
| `neutral/fox.md` | Fox |
| `neutral/goat.md` | Goat |
| `neutral/llama.md` | Llama |
| `neutral/panda.md` | Panda |
| `neutral/piglin.md` | Piglin |
| `neutral/polar_bear.md` | Polar Bear |
| `neutral/spider.md` | Spider |
| `neutral/trader_llama.md` | Trader Llama |
| `neutral/wolf.md` | Wolf |
| `neutral/zombified_piglin.md` | Zombified Piglin |

### Hostile Mobs (`hostile/`) — 34 files

| File | Mob |
|------|-----|
| `hostile/blaze.md` | Blaze |
| `hostile/bogged.md` | Bogged |
| `hostile/breeze.md` | Breeze |
| `hostile/cave_spider.md` | Cave Spider |
| `hostile/creeper.md` | Creeper |
| `hostile/drowned.md` | Drowned |
| `hostile/elder_guardian.md` | Elder Guardian |
| `hostile/enderman.md` | Enderman |
| `hostile/endermite.md` | Endermite |
| `hostile/evoker.md` | Evoker |
| `hostile/ghast.md` | Ghast |
| `hostile/guardian.md` | Guardian |
| `hostile/hoglin.md` | Hoglin |
| `hostile/husk.md` | Husk |
| `hostile/magma_cube.md` | Magma Cube |
| `hostile/phantom.md` | Phantom |
| `hostile/piglin_brute.md` | Piglin Brute |
| `hostile/pillager.md` | Pillager |
| `hostile/ravager.md` | Ravager |
| `hostile/shulker.md` | Shulker |
| `hostile/silverfish.md` | Silverfish |
| `hostile/skeleton.md` | Skeleton |
| `hostile/slime.md` | Slime |
| `hostile/spider.md` | Spider |
| `hostile/stray.md` | Stray |
| `hostile/vex.md` | Vex |
| `hostile/vindicator.md` | Vindicator |
| `hostile/warden.md` | Warden |
| `hostile/witch.md` | Witch |
| `hostile/wither_skeleton.md` | Wither Skeleton |
| `hostile/zoglin.md` | Zoglin |
| `hostile/zombie.md` | Zombie |
| `hostile/zombie_villager.md` | Zombie Villager |
| `hostile/zombified_piglin.md` | Zombified Piglin |

> Note: Enderman, Spider, and Zombified Piglin have files in both neutral/ and hostile/ — both copies are intentional (behavior differs by context).

### NPCs (`npc/`) — 2 files

| File | Mob |
|------|-----|
| `npc/villager.md` | Villager |
| `npc/wandering_trader.md` | Wandering Trader |

### Utility Mobs (`utility/`) — 2 files

| File | Mob |
|------|-----|
| `utility/iron_golem.md` | Iron Golem |
| `utility/snow_golem.md` | Snow Golem |

### Structures (`structures/`)

World-generation features: villages, strongholds, bastions, monuments, etc.

| File | Title |
|------|-------|
| `structures/ancient_city.md` | Ancient City (deep dark structure, Warden source, exclusive loot) |
| `structures/bastion_remnant.md` | Bastion Remnant (piglin stronghold, netherite upgrade template source) |
| `structures/nether_fortress.md` | Nether Fortress (critical progression gate, blaze and wither skeleton source) |
| `structures/nether_portal.md` | Nether Portal (dimensional gateway, player-built) |
| `structures/pillager_outpost.md` | Pillager Outpost (raid captain source, persistent hostile structure) |
| `structures/ruined_portal.md` | Ruined Portal (cross-dimensional loot structure) |
| `structures/stronghold.md` | Stronghold (End Portal location, silverfish hazard) |
| `structures/village.md` | Village (overworld settlement, raid target) |
| `structures/woodland_mansion.md` | Woodland Mansion (illager fortress, one-time clearable dungeon) |

### Dimensions (`dimensions/`)

World-level mechanics that differ by dimension: water/fire behavior, light, navigation tools, native biomes and mobs.

| File | Title |
|------|-------|
| `dimensions/nether.md` | The Nether (dimension) |
