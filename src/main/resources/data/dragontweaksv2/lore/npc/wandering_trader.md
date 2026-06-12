---
topic: Wandering Trader
type: advisor-artifact
source: "[[https://minecraft.wiki/w/Wandering_Trader]]"
scraped: 2026-06-09
version: 1.21.1
pipeline_stage: advisor-artifact
---

# Wandering Trader

A passive NPC that spawns randomly in the world and offers a rotating selection of rare trades for emeralds. Not tied to any village, cannot be assigned a job site, and despawns on a strict timer. Always accompanied by two trader llamas on leads.

## Spawning

Spawns randomly near the player -- preferring near bells, villages, or player spawn point. Appears with two trader llamas already leashed by lead.

Spawn timer: 48,000 ticks (40 minutes) after the last wandering trader despawns or is killed. The timer runs regardless of player location.

## Stats

- Health: 20 HP
- Passive; does not attack
- Becomes invisible at night (06:00 PM to 06:00 AM) but remains present; the llamas and leads remain visible

## Behavior

The wandering trader has no home base, no job site, and no schedule beyond the day/night visibility cycle. It wanders in a radius around the point where it spawned and does not path to new areas.

It cannot be given a new profession, reassigned, or integrated into a village economy. Reputation mechanics do not apply to it.

## Despawn

The wandering trader despawns after its timer expires (48,000 ticks = 40 minutes of game time), or immediately if it has no trades remaining. Killing it resets the spawn timer. It does not despawn while a player is actively trading with it.

Named wandering traders (via name tag) do not despawn.

## Trades

Offers 6 randomly selected trades from a fixed pool. Trades are locked at spawn -- the selection does not change during its stay. Typical stock includes:

- Rare biome plants and saplings (jungle, swamp, mesa, etc. saplings not obtainable by other means in some biomes)
- Coral blocks (only renewable source without an ocean)
- Sea grass, kelp, podzol, mycelium
- Nautilus shells (also obtainable via fishing/drowned)
- Blue ice (expensive -- 6 packed ice per block, or 3 blue ice for 6 emeralds)
- Flowers and dyes
- Sand, gravel, packed ice

All purchases cost emeralds. The wandering trader never buys items from the player -- all trades are one-directional (player pays emeralds, receives goods).

## Trader Llamas

The two trader llamas are always leashed to the wandering trader. If the trader is killed or the lead is cut, the llamas become untamed and aggressive -- they will spit at players or mobs that approach.

Killing the llamas does not affect the trader's timer or behavior. The llamas drop their leads on death.

If the wandering trader is killed while leashed, the llamas immediately become hostile to the killer.

## Night Invisibility

At 18:00 the wandering trader drinks a Potion of Invisibility and becomes invisible until 06:00. The trader is still present, tradeable, and targetable while invisible -- it simply cannot be seen. The llamas and their leads remain visible, which usually reveals the trader's position anyway.

At sunrise, the trader drinks a Potion of Night Vision (reversion to visible) -- this is a cosmetic distinction; the outcome is simply that visibility returns.

## Drops

- Wandering Trader: 1-3 emeralds (rare), 1 lead (from leash)
- Trader Llamas: 0-2 leather per llama

## Notes

- The trade pool is the only renewable source of coral blocks and certain biome-specific saplings without physically visiting those biomes.
- Blue ice trades are poor value compared to packing snow golems or finding ocean biomes, but are the only no-biome option.
- Killing a wandering trader resets the 40-minute spawn timer -- if trades are exhausted, it despawns anyway, so killing it to reset is only useful if you want a fresh trade pool sooner.
- The llama leads drop on trader death and can be collected as a lead source.
