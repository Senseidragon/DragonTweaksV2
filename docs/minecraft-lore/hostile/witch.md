---
topic: Witch
type: advisor-artifact
source: "[[https://minecraft.wiki/w/Witch]]"
scraped: 2026-06-09
version: 1.21.1
pipeline_stage: advisor-artifact
---

# Witch

A ranged hostile mob with 26 HP that uses potions for both offense and defense. Witches are among the most durable common hostile mobs due to constant self-healing, have high magical damage resistance, and appear in raids starting wave 4. A witch created from a villager by lightning never despawns.

## Stats

- Health: 26 HP
- Speed: 0.25
- Attack: Splash Potion of Harming (6 HP, falloff by distance), Splash Potion of Poison (up to 45 sec), Splash Potion of Slowness, Splash Potion of Weakness
- Magical damage resistance: 85% reduction from poison, instant damage, evoker fangs, warden sonic boom, and Thorns
- Potion throw range: 10 blocks; 3-second interval

## Spawning

At light level 0 in all biomes except mushroom fields and deep dark. Rare in most biomes (~1% weight vs. 18-19% for zombie/skeleton). Always spawn alone.

Swamp huts always contain one witch that never despawns. Only witches can spawn inside the 7x7x9 hut volume after generation.

Raids: witches appear starting wave 4 on Java (wave 3 or 4 on Bedrock). Count of witches scales with difficulty.

Lightning within 4 blocks of a villager converts it to a witch that never despawns. This witch is immediately hostile -- even to players who traded with it.

## Offense

Witches throw potions based on the target's state:
- Slowness: if target is 8-10 blocks away and not already slowed
- Poison: if target has 8+ HP and is not already poisoned
- Weakness: 25% chance if target is within 3 blocks AND (target has 8 HP or less OR is already poisoned)
- Harming: default fallback (6 HP magical damage)

The Weakness throw is exploitable: if a zombie villager is in the area and the witch throws Weakness at a nearby target, the splash can hit the zombie villager -- enabling a cure without the player having to brew the potion themselves.

## Defense

Witches drink defensive potions every tick (1.6-second animation during which they cannot attack):
- Water Breathing: if 80%+ submerged and lacking effect (15% chance)
- Fire Resistance: if on fire or took fire damage in last 2 seconds (15% chance)
- Healing (4 HP): if not at full health (5% chance)
- Swiftness: if 11+ blocks from target and no speed effect (50% chance)

Due to frequent healing, killing a witch requires sustained damage output for 40-50 seconds.

## Raid Behavior

During raids, witches do NOT attack villagers or wandering traders directly (though splash potions can hit them by accident). They seek out illagers and ravagers to throw Regeneration potions on and Instant Health if their allies have 4 HP or less.

Witches that join illager patrols ARE hostile to villagers, wandering traders, and iron golems. Witches cannot open doors.

When a raid is won by the raiders (all villagers dead or all beds destroyed), witches celebrate audibly.

## Cross-Mob Interactions

- A witch is neither an illager nor a villager -- it does not trigger iron golem protection of villagers, but a village iron golem will attack a witch that attacks near it
- Bell ringing within 32 blocks gives the witch Glowing for 3 seconds (applies even outside raids)
- Witches cannot be converted back from villagers
- Witches join illager patrols if near a patrol captain

## Drops

- Redstone dust (always, 4-8)
- Glowstone dust, gunpowder, spider eye, sugar, glass bottle, stick (all low chance)
- Potion of Healing, Fire Resistance, Swiftness, or Water Breathing (8.5% if killed while drinking)
