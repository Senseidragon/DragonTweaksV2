---
topic: Warden
type: advisor-artifact
source: "[[https://minecraft.wiki/w/Warden]]"
scraped: 2026-06-09
version: 1.21.1
pipeline_stage: advisor-artifact
---

# Warden

A 500 HP boss-tier hostile mob that spawns from sculk shriekers in the deep dark. The most dangerous mob in the game -- 30 HP melee on Normal, a ranged sonic boom that bypasses all armor and enchantments, 100% knockback resistance, and immune to fire and lava. The warden is designed to be avoided, not fought.

## Stats

- Health: 500 HP
- Melee: Easy 16 HP, Normal 30 HP, Hard 45 HP (disables shields for 5 seconds)
- Sonic boom (ranged): Easy 6 HP, Normal 10 HP, Hard 15 HP
  - Bypasses all armor, Protection enchantment, natural armor, and shields
  - Only reduced by: wolf armor (takes durability), witch magic resistance (85%), Resistance effect
- Knockback resistance: 100%
- Detection radius: 15 blocks (vibration), 49x51x49 box (entity awareness)

## Spawning

Not a natural mob spawn. Triggered by sculk shriekers:

Each player has a personal warning level (0-4). It increments each time that player activates any naturally-generated sculk shrieker. At warning level 4, a shrieker spawns a warden 4.5 seconds later. If already at 4, it spawns another one immediately.

Warning level resets to 0 only on death -- it does NOT reset by moving away, sleeping, or waiting. Calibrated sculk sensors can also activate shriekers.

The warden spawns within an 11x13x11 area centered on the shrieker. Can be disabled with the `spawn_wardens` game rule.

## Anger and Aggression

Wardens track anger per target (0-150):
- Projectile vibration: +10 anger
- Direct vibration, contact, or sniff: +35 anger
- Two projectiles from same player within 5 seconds: escalated to +35 (even if each was 10)
- Direct attack by mob: +100 anger instantly, immediate pursuit

At 80+ anger, the warden roars for 4.2 seconds, adds 20 more anger, then pursues. If the target is within 5 blocks, it skips the roar and charges immediately.

Anger decays at 1 per second when undisturbed. Clears entirely if target enters Creative/Spectator mode, leaves the dimension, or dies.

## Key Behavioral Rules

- Warden is biased toward player vibrations even when angrier at another mob -- it attacks the player first as long as the player has any anger
- Cannot detect sneaking players who are moving, jumping, falling, or shooting (same as sculk sensors)
- Can still sniff out sneaking players from within 6 blocks horizontally and 20 blocks vertically
- Inflicts Darkness on all players within 20 blocks every 6 seconds regardless of anger state
- Pursues through hazardous blocks that deter other mobs (rails, cacti, magma)
- Fits through any 1-block wide, 3-block tall space
- Does NOT despawn like normal mobs (no 32-block or 128-block despawn rules)

## Despawn

After 60 seconds of calm (no vibrations detected, no sniffs triggered), the warden burrows back and despawns. Named wardens do not despawn. The warden cannot detect vibrations or be damaged during its emerge/burrow animation (only `/kill` works).

## Sonic Boom Range

Activates when the warden cannot reach its target, after:
- 10 seconds since detecting target
- 5 seconds since last attack

Target must be within 14 blocks horizontally and 20 blocks vertically. The attack fires instantly after a 1.7-second charge, passes through all blocks, cannot be dodged, and hits only one target.

## Drops

- Sculk catalyst (always, 1)
