---
topic: Shulker
type: advisor-artifact
source: "[[https://minecraft.wiki/w/Shulker]]"
scraped: 2026-06-09
version: 1.21.1
pipeline_stage: advisor-artifact
---

# Shulker

A cubic hostile mob exclusive to End cities with 30 HP. Fires homing bullets that inflict Levitation for 10 seconds. Its closed shell has 20 armor points (equivalent to near-full diamond armor); its open shell has 0. Only source of shulker shells. Does not despawn naturally.

## Stats

- Health: 30 HP
- Armor: 20 (closed shell) / 0 (open shell)
- Bullet: Easy 3 HP, Normal 4 HP, Hard 6 HP + Levitation 10 sec (~9 blocks upward)
- Hitbox: 1x1 (closed), 1.2x1 (peeking), 2x1 (open)

## Spawning

Spawns during End city generation on walls and End ships. Does not respawn after being killed. Can be duplicated (see below).

## Shulker Bullet

Homing projectile that tracks target along X, Y, or Z axis. Leaves white particle trails. Upon hitting a mob or player it deals damage and applies Levitation 10 sec. If it hits a neutral/hostile mob by accident, that mob retaliates against the shulker after Levitation ends.

**Destruction**: Any melee attack, arrow, or shield block destroys the bullet. Contact with any block or lava also destroys it.

## Duplication

A shulker CAN spawn a copy of itself when hit by a shulker bullet (including its own) under these conditions:
1. Lid must be open (peeking counts)
2. Must take damage and survive
3. Health must be above 50% after the hit (below 50% triggers a 20% chance to teleport without duplicating)
4. Must successfully teleport away
5. Random check: 100% - 20% per shulker within 8 blocks (impossible if 5+ nearby shulkers)

If all checks pass, a fresh shulker spawns at the old position. This is how shulker farms produce renewable shells.

## Teleportation

Shulkers teleport when: their attachment block is removed, they take damage below 50% HP (20% chance), or a bullet hits their open shell. Each teleport attempt picks a random position in a 17x17x17 cube. The target position must be air, not overlapping any hitbox, and adjacent to an attachable surface the shulker can fully open from.

## Behavior

- Attaches to any solid block face (prefers below, then above, then cardinal directions)
- Peeks out periodically and shoots bullets at players within range
- Does not despawn even in Peaceful difficulty
- Can be moved via boats or minecarts
- Can be transported through End gateways and the exit portal (in a boat/minecart)

## Drops

- Shulker shell (50% chance)
