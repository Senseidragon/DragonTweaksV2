---
topic: Phantom
type: advisor-artifact
source: "[[https://minecraft.wiki/w/Phantom]]"
scraped: 2026-06-09
version: 1.21.1
pipeline_stage: advisor-artifact
---

# Phantom

A flying undead hostile mob with 20 HP that spawns above players who have not slept or died for 3+ in-game days. Phantoms dive-bomb players from the sky, are among the fastest mobs in the game, and become progressively more common the longer sleep is avoided.

## Stats

- Health: 20 HP
- Attack: Easy/Normal 2 HP, Hard 3 HP per swoop
- Speed: up to 20 blocks per second (one of the fastest mobs)
- Target range: 64 blocks

## Spawning

Requires:
1. Player has not entered a bed or died for at least 72,000 ticks (3 in-game days)
2. Night or thunderstorm
3. Player above sea level (y=64) with sky directly visible above (cobwebs count as blocking, glass does not)
4. No light-blocking block overhead (leaves block phantom spawning; glass does not)

Spawn attempts occur every 1-2 minutes. Success probability increases with days without sleep: ~25% on day 4, ~40% on day 5, ~50% on day 6, ~57% on day 7, increasing further each day.

Group size: 1-2 (Easy), 1-3 (Normal), 1-4 (Hard). Phantoms appear 20-34 blocks above the player.

Phantom spawning ignores the hostile mob cap (they don't adhere to it) and ignores biome -- they can spawn in mushroom fields, deep dark, and the void. They DO count toward the mob cap but are not limited by it.

Sleep resets the timer. Death also resets the timer.

## Behavior

Idle: phantoms circle 15-25 blocks horizontally and 24-35 blocks vertically from the player. They trail gray smoke. They have a 64-block targeting range.

Attack: every 8-12 seconds, a phantom dives in to bite. It retreats back to high elevation after each attack or if hurt. If blocked from returning to elevation by a ceiling, it continues trying until it can attack again or moves clear.

Phantoms attack players in beds. They do not attack creative mode players.

If a player resets their insomnia timer (by sleeping or dying) while phantoms are targeting them, the phantoms become neutralized and stop attacking.

## Cat Counter

Cats hiss at phantoms that are attacking players. Phantoms stay at least 16 blocks away from cats regardless of line of sight. This is a hard avoidance -- a cat in a room will keep phantoms from diving into the space.

## Undead Properties

- Damaged by Instant Health, healed by Instant Damage
- Immune to Regeneration and Poison effects
- Ignored by the Wither
- Smite enchantment effective
- Armadillos hide from them

Phantoms can move through water at full speed and do not drown. They can spawn inside buildings if conditions are met (player outside, large enough cavity).

## Drops

- Phantom membrane (used to repair elytra and brew potions of Slow Falling)
