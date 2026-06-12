---
title: Minecraft -- Pillager Outpost (raid captain source, persistent hostile structure)
domain: minecraft
fact: Pillager outpost is a 4-story dark oak watchtower generating in all village-adjacent biomes and several extra ones, always has a loot chest on the top level, spawns pillagers continuously within a wide radius (unlike mansions, it never clears), raid captains have a 6% spawn chance among all pillagers and drop ominous bottles that trigger village raids, and caged allays and iron golems can generate in the surrounding outbuildings.
confidence: 0.90
usefulness: high
authority: authoritative
source_url: https://minecraft.wiki/w/Pillager_Outpost
source_version: "1.21.1"
source_type: official_wiki
format: distilled-advisor-block
---

**Role:** Raid captain source; ominous bottle farm; allay rescue point; persistent danger zone.

## Generation

- Rarer than villages, less rare than woodland mansions
- Generates in all village biomes (plains, desert, savanna, taiga, snowy plains, meadow) plus grove, snowy slopes, jagged/frozen/stony peaks, cherry grove
- Never generates adjacent to a village -- intentional separation
- 4-story watchtower always present; up to 4 smaller structures scattered around periphery (cages, tents, log pile, target dummies)
- Watchtower may generate with mossy cobblestone and vines overlay (weathered variant, same structure)

## Mobs

- Pillagers spawn continuously within a ~72×72 area centered on the watchtower top -- the outpost never goes quiet
- 6% of all spawned pillagers are raid captains; raid captains wear the ominous banner on their heads
- Iron golem: 85% chance of generating in a cage; can reach through the fence and punch nearby pillagers
- Allays: 50% chance of a cage generating with 2 allays inside

Unlike the woodland mansion, pillagers never stop coming. The outpost cannot be cleared permanently.

## Structures

- **Watchtower** -- 4 stories; loot chest and ominous banners on the top floor; dark interior
- **Cages** -- dark oak fence cages that may contain an iron golem, 2 allays, or be empty; generated at world creation only
- **Tents** -- white wool tent with crafting table inside; occasionally pumpkins
- **Targets** -- two scarecrow-like practice dummies made of hay bales and carved pumpkins
- **Log pile** -- decorative only

## Bad Omen

Killing a raid captain drops an ominous bottle (random level 1–5). Drinking it applies Bad Omen for 100 minutes (5 in-game days). Entering a village boundary while Bad Omen is active converts it to Raid Omen and starts a raid when the effect expires. Bad Omen clears on death or by drinking milk.

Outpost captains are one of two sources of ominous bottles -- the other is patrol captains, which roam independently of outposts.

## Loot (Watchtower Chest)

- Crossbow (50%)
- Goat horn -- non-screaming variants only (50%)
- Bottle o' enchanting (~61%)
- Sentry armor trim smithing template (25%)
- Enchanted book (~11%)
- Food: wheat, carrot, potato
- Arrow, string, tripwire hook, iron ingot (moderate chance each)

## Notes

- Freeing an outpost iron golem does not give it any loyalty to the player -- it will attack the player if provoked, same as a village golem
- Allays freed from cages follow normal allay behavior once given an item; see `docs/minecraft-lore/passive/allay.md`
- Pillagers at outposts are connected to the village raid system; see `docs/minecraft-lore/structures/village.md` for raid mechanics
