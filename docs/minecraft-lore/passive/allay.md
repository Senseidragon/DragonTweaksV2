---
topic: Allay
type: advisor-artifact
source: "[[https://minecraft.wiki/w/Allay]]"
scraped: 2026-06-09
version: 1.21.1
pipeline_stage: advisor-artifact
---

# Allay

A small flying passive mob that collects dropped items for the player who gave it an item to hold. Allays are found imprisoned in pillager outposts and woodland mansions -- once freed and given an item, they become loyal item-fetching companions.

## Spawning

Allays spawn only during world generation -- they never appear from natural spawning after the world is created. They have a 50% chance to appear in dark oak cages near pillager outposts (groups of 1-3) and a 50% chance to appear in jail cells inside woodland mansions (groups of 1-3). The only other source is duplication (see below).

## Behavior

An allay flies aimlessly until a player gives it any item. Once given an item, it locks onto that player, follows them within a 64-block radius, and actively searches for dropped copies of that item within 32 blocks of the player, collecting up to a full stack and delivering them. There is a 3-second cooldown between deliveries. Taking the item back with an empty hand dismisses the allay back to wandering.

The allay seeks by item type only -- it does not distinguish enchantments, custom names, or contents of shulker boxes. It will still pick those up and deliver them, just in separate trips.

If a note block plays within 16 blocks of the allay while it is delivering items, the allay locks onto that note block instead and spends 30 seconds fetching items to it rather than to the player. The 30-second timer resets each time that same note block plays again. Multiple allays can each be locked to separate note blocks at the same time -- this is the basis for automated item sorting contraptions. Placing wool between the note block and the allay blocks the vibration that triggers the lock-on.

Allays dance when a jukebox nearby is playing and stop when the music ends or they move too far away.

The allay cannot enter water, but it will attempt to collect items that have sunk underwater -- and fail.

## Combat / Survival

20 HP. Allays cannot be harmed by the player who gave them their item. If harmed by any other source, they fly away briefly. They regenerate 2 HP per second. If an allay is holding a totem of undying when it dies, the totem activates and the allay returns to full health.

## Drops

- The item held in its hand (if any)
- No other drops; no XP

## Duplication

While an allay is dancing near a playing jukebox, giving it an amethyst shard causes it to split into two allays. The amethyst shard is consumed. Both allays then have a 5-minute cooldown before they can duplicate again. The second allay spawns fresh -- it does not inherit custom names or attributes from the original.

## Notes

- Allays can crush turtle eggs when they land on top of them, despite being a flying mob.
- In Java Edition, a named name tag cannot be given directly to an allay -- the player must first tether it to a fence with a lead, then hand it the name tag.
- Wool blocks the vibration particle from a note block, preventing an allay from locking onto it -- useful for controlling which allays respond to which note blocks.
