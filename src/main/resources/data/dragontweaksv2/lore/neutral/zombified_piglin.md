---
topic: Zombified Piglin
type: advisor-artifact
source: "[[https://minecraft.wiki/w/Zombified_Piglin]]"
scraped: 2026-06-08
version: 1.21.1
pipeline_stage: advisor-artifact
---

# Zombified Piglin

Zombified Piglins are neutral undead Nether monsters. They ignore players completely until attacked -- but hitting even one causes a massive group response, with all nearby zombified piglins converging on the attacker.

## Spawning

**Natural sources:**
- Nether Wastes (most common, groups of 4), Crimson Forest (groups of 2-4), Nether Fortresses
- Nether portals in the Overworld (0.05-0.15% chance per portal block random tick)
- Pigs struck by lightning transform into zombified piglins
- Piglins/Piglin Brutes in the Overworld or End zombify after 15 seconds (retain all equipment)
- As reinforcements from other zombified piglins (Hard difficulty leaders only)
- Strider jockeys (3.3% chance to spawn riding a strider with Warped Fungus on a Stick)

5% chance to spawn as baby. Do not spawn in Peaceful mode.

## Equipment

- 95% spawn with a golden sword (up to 25% chance enchanted on Normal/Hard)
- 5% spawn with a golden spear (may be enchanted; uses charge attack when provoked)
- Retain any equipment from a zombified piglin or brute

## Behavior

Zombified piglins are **completely neutral** until attacked. They do not respond to non-combat interactions (unlike regular piglins -- no gold bartering, no reaction to opening chests).

**Group aggro:** Attacking any zombified piglin (unless killed in one hit) triggers all zombified piglins within a **67x22x67 to 111x22x111 area** to become hostile. They make aggressive sounds when angered and pursue the attacker, even through Invisibility.

**Forgiveness timer:** Once out of the attacker's follow range (33-55 blocks) or line of sight is broken, a zombified piglin becomes neutral again after 20-55 seconds (always 25 seconds in Bedrock). **The timer does not advance in unloaded chunks** -- going through a Nether portal and returning will find them still hostile. Player death resets hostility (if `forgive_dead_players` gamerule is true, which is default).

**Alarm spreading:** Hostile zombified piglins "sound an alarm" every 4-6 seconds while maintaining line of sight to the target, recruiting all neutral zombified piglins within their alarm radius.

## Undead Properties

- Damaged by Instant Health, healed by Instant Damage
- Immune to Poison and Regeneration effects
- Immune to fire and lava
- Cannot swim but do not drown
- Affected by Smite enchantment
- Ignored by the Wither
- Makes armadillos roll up

## Combat

- Health: 20HP (leaders: 40-100HP)
- Attack (golden sword): 5HP (Easy), 8HP (Normal), 12HP (Hard)
- Spear variant uses charge attack -- runs at target, backs off, then charges again

## Drops

- Rotten Flesh
- Gold Nugget
- Rarely: golden sword or golden spear (with enchantments possible)
- 5 XP (adult), no XP from babies

## Notes

- Unlike regular piglins, zombified piglins do NOT attack villagers, cannot barter, and are not neutral around gold.
- Normal piglins and zombified piglins do NOT share aggro -- provoking one type does not anger the other.
- Zombified piglins from a zombified piglin brute will carry a golden axe.
