---
title: Minecraft -- Village (overworld settlement, raid target)
domain: minecraft
fact: Village generates in 5 biome-matched styles (plains/savanna/taiga/snowy/desert), has a 2% abandoned variant with zombie villagers and no iron golem, is targeted by illager raids triggered by Bad Omen from ominous bottles dropped by raid captains, and Java-only villages with 20+ beds face a 10% nightly zombie siege.
confidence: 0.90
usefulness: high
authority: authoritative
source_url: https://minecraft.wiki/w/Village
source_version: "1.21.1"
source_type: official_wiki
format: distilled-advisor-block
---

**Role:** Primary NPC settlement and trade hub; iron golem source; illager raid target.

## Generation

- Biomes: desert, plains (including sunflower plains, meadow), savanna, snowy plains, taiga (including snowy taiga, taiga hills, snowy taiga hills)
- Style matches biome: desert uses sandstone, plains uses oak, savanna uses acacia, snowy/taiga use spruce
- 2% chance of generating as an abandoned village: all villagers replaced by zombie villagers (do not despawn, no sunlight resistance), torches and doors missing, random blocks replaced by cobwebs, glass panes replaced by brown stained glass, NO iron golems spawn naturally
- Jungle and swamp villager types exist but their village STRUCTURES do not generate in jungle or swamp biomes -- they only appear when a village overlaps those biomes, or from breeding/curing

## Mobs

On generation: villagers (regular villages only), iron golem near the meeting point, horses, pigs, cows, sheep; camels in desert villages only; zombie villagers in abandoned villages only

Periodically: cats (1 per 4 beds, max 5); wandering trader + 2 trader llamas at meeting point; iron golems if village has 10+ villagers and 20+ beds

During raids: pillagers, vindicators, evokers, vexes, ravagers, witches; illusioners if player-spawned; higher Bad Omen level means more enchanted enemy weapons

During zombie sieges (Java only): zombies, zombie villagers, husks, drowned, zombie horses -- ignore lighting and walls

## Village Mechanics

- Village exists as long as at least one villager is linked to a bed; all beds unlinked = village ceases to exist and villagers lose access to job sites and bells
- Max population equals number of valid beds; villagers breed back up if 2+ villagers remain
- Village center is the median POI position (beds, bells, job site blocks); center defines where cats and iron golems can spawn
- Villagers can only link to unclaimed job site blocks matching their profession; unemployed villagers can claim any unclaimed site

## Villager Professions

Each villager takes a profession from their job site block and trades accordingly. Professions worth seeking out:

- **Cartographer** -- sells maps including woodland explorer maps; the only reliable way to locate a mansion
- **Librarian** -- trades enchanted books; the selection varies by villager and improves with reputation
- **Armorer / Weaponsmith / Toolsmith** -- iron and diamond gear; enchanted at higher tiers
- **Cleric** -- trades ender pearls, glowstone, potions, and bottles of enchanting
- **Fletcher** -- arrows, bows, crossbows; useful early
- **Farmer / Butcher / Fisherman** -- food supply; reliable and cheap
- **Leatherworker** -- saddles and leather armor; saddles are otherwise hard to obtain without fishing or dungeon loot
- **Shepherd** -- wool, beds, colored carpet, banners, and paintings; the person to find if you need cloth, color, or something for the walls
- **Mason** -- cut stone, terracotta, quartz, bricks, granite, diorite, andesite; useful for building materials in bulk

Nitwits and unemployed villagers will not trade. Unemployed villagers claim any unclaimed job site block nearby, so placing one down will give them a profession.

## Village Disposition

Villages are tolerant until they aren't. Each villager tracks a reputation score per player; attacking or killing villagers drops it sharply, while trading and curing zombie villagers raises it. If a player's standing falls far enough, any iron golem present turns hostile toward that player -- not the village, not the mobs, just that player.

This is worth surfacing when a player asks whether a village is safe to operate in long-term, or asks about what the iron golem does. Full reputation mechanics and thresholds are in `docs/minecraft-lore/npc/villager.md` under `## Reputation System`.

## Raid Trigger

- Raid captains drop ominous bottles on death; drinking one applies Bad Omen for 100 minutes
- Entering a village boundary with Bad Omen converts it to Raid Omen; raid begins when Raid Omen expires
- Bad Omen clears on death or by drinking milk
- Raid targets any village the game recognizes (any chunk section within a 3x3x3 cube of sections around a section containing a bed, bell, or job site)

## Zombie Siege (Java Edition Only)

- 10% chance of triggering at midnight each night or during thunderstorms if the village has 20+ valid beds
- Zombies spawn regardless of light level or physical barriers -- walling or lighting a village does not prevent a siege
- No in-game warning; only indicator is an unusual surge of zombies
- Naturally spawned zombie variants (zombie villagers, husks, drowned) join the siege
