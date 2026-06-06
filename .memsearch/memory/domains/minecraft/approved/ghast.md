---
source_url: https://minecraft.wiki/w/Ghast
retrieved_at: 2026-06-04T22:39:08.068Z
source_version: "1.21.1"
tool: firecrawl
format: markdown
content_hash: placeholder-ghast-repair
source_type: official_wiki
repaired_at: 2026-06-05
repair_reason: original entry truncated -- only infobox stub present; spawning, drops, and behavior were missing
---

# Ghast

A large flying hostile mob native to the Nether. Shoots explosive fireballs. Can be killed by
reflecting its own fireball back. The reflected-fireball kill is the only way to obtain the
Music Disc "Tears". Despite its ghost-like appearance, it is NOT an undead mob.

## Stats

- Health: 10 HP (5 hearts)
- Behavior: Hostile
- Mob type: Monster (NOT undead)
- Speed: 0.7
- Hitbox: 4x4x4 blocks (9 tentacles are NOT part of the hitbox)

## Attack Strength

**Fireball explosion:**
- Deals up to ~17 HP at point-blank to unarmored player
- Creates a small explosion; can damage the environment

## Spawning

**Biomes (JE):**
- Basalt Deltas: groups of 1
- Nether Wastes: groups of 4
- Soul Sand Valley: groups of 4

**Spawn conditions:**
- JE: Only 5% of spawn attempts succeed
- Requires a 5x5x4 block open space with a solid block below
- BE: Always spawns individually (not in groups)
- Does NOT spawn in Nether fortresses

**Light:** All light levels (Nether)

## Drops

- Ghast Tears: 0-1 (50% base chance; scales with Looting)
- Gunpowder: 0-2 (avg 1.0; scales with Looting)
- Music Disc "Tears": ONLY when killed by a player-deflected fireball (not affected by Looting)
- 5 XP when killed by a player or tamed wolf

Note: The Tears disc is exclusive to this kill method -- cannot be obtained any other way.

## Behavior

### Floating and Targeting

Ghasts float through the air with eyes closed when idle. Opens eyes and targets players
within 64 horizontal blocks / 4 vertical blocks (JE) or 28 blocks normal / 64 if hit (BE).
Strafes while targeting; does NOT pursue the player -- maintains current position or drifts.

### Fireball Attack

Fires one fireball approximately every 3 seconds when a player is in range. The fireball
travels in a straight line and explodes on impact. The explosion does not destroy obsidian
or bedrock but can destroy most Nether blocks and set fire to the area.

### Fireball Reflection

A ghast's fireball can be deflected back using:
- Melee attack (hit directly)
- Arrow or other projectile
- Wind charge

If the reflected fireball kills the ghast, it drops the Music Disc "Tears".

### Immunities

- Fire and lava damage (immune)
- Slowed by lava (not damaged)

### NOT Immune To

- Drowning in water (can take water damage)
- Fall damage (theoretical; rarely applicable)

### Mob Interactions

Ignored by (do not target ghast):
- Wolves
- Snow golems
- Zoglins
- "Johnny" vindicators

Actively attack ghasts:
- Iron golems (JE only)
- Wardens (JE only)

## Sound Identifiers (Java Edition)

| Event | Identifier |
|-------|-----------|
| Ambient (idle cry) | entity.ghast.ambient |
| Death | entity.ghast.death |
| Hurt | entity.ghast.hurt |
| Scream (target) | entity.ghast.scream |
| Shoot | entity.ghast.shoot |
| Warn (fire) | entity.ghast.warn |

## Trivia

- Despite ghostly appearance, ghasts are NOT undead. They take full damage from Smite (no
  bonus), can be poisoned/regenerated, and are not affected by undead-targeting mechanics.
- Music Disc "Tears" has the same name as the ghast tear item drop (different items).
- The 4x4x4 hitbox makes ghasts among the largest in the game by hitbox volume.
