---
source_url: https://minecraft.wiki/w/Warden
retrieved_at: 2026-06-04T22:39:08.068Z
source_version: "1.21.1"
tool: firecrawl
format: markdown
content_hash: placeholder-warden-repair
source_type: official_wiki
repaired_at: 2026-06-05
repair_reason: original entry truncated -- only infobox stub present; spawning, drops, and behavior were missing
---

# Warden

The most powerful hostile mob in the game. Completely blind; detects players via vibrations and
scent. Found only in deep dark biomes. Source of sculk catalysts. Has a unique sonic boom attack
that bypasses all armor and shields.

## Stats

- Health: 500 HP (250 hearts)
- Behavior: Hostile
- Mob type: Monster
- Knockback resistance: 100%
- Hitbox: 2.9 blocks tall, 0.9 blocks wide (1.0 tall while digging/emerging)

## Attack Strength

**Melee:**
- Easy: 16 HP
- Normal: 30 HP
- Hard: 45 HP
- Attack cooldown: 36 ticks (1.8 seconds)
- Disables shields for 5 seconds on hit

**Sonic Boom:**
- Easy: 6 HP
- Normal: 10 HP
- Hard: 15 HP
- Bypasses ALL armor, shields, enchantments, and natural armor
- Only reduced by: wolf armor, witch's magic resistance (85%/95%), Resistance effect

## Spawning

Spawned only by sculk shriekers:
- Each sculk shrieker activation increases the player's warning level by 1 (per player)
- At warning level 4, a spawn attempt is made
- 20 spawn attempts are made in an 11x13x11 area around the shrieker
- Warning level resets after ~10 minutes without triggering, or after the warden despawns
- JE: `spawn_wardens` game rule can disable spawning

Wardens can only spawn in deep dark biomes (where sculk shrieker and sculk sensor naturally
generate). They dig up from the ground on spawn and dig back down when losing interest.

## Drops

- 1 sculk catalyst (always)
- 5 XP when killed by a player or tamed wolf

## Behavior

### Sensing (Completely Blind)

Wardens cannot see players at all. They detect threats via:

**Vibrations (radius ~15 blocks ovoid):**
- Footsteps, projectiles, blocks breaking/placing, item drops
- Sneaking players do NOT produce detectable footsteps
- 2-second cooldown between detections from the same source
- Wool and carpets reduce vibration distance

**Direct contact:**
- Being hit or touched by any entity, even accidentally

**Periodic sniffing:**
- Sniffs every ~4.5 seconds when stationary
- Can detect players within ~9 blocks regardless of sneaking

### Anger System

Warden tracks anger toward entities (0-150):
- +10 per vibration detected
- +35 per direct hit or touch
- Resets to 0 when target dies or leaves the dimension
- Wardens share anger information with other nearby wardens (JE)
- Warden becomes fully hostile at anger level 80+
- Heartbeat sound speeds up as anger increases

### Darkness Effect

Inflicts Darkness status effect on all players within 20-block ovoid every 6 seconds for 13
seconds, dimming their vision.

### Sonic Boom

Requirements before firing:
- At least 10 seconds since last detecting the target via vibration
- At least 5 seconds since last melee attack
- Target within 14-block horizontal / 20-block vertical range

Behavior:
- 1.7-second charge animation
- Fires a visible projectile-like beam
- Bypasses all conventional defenses
- Cannot fire sonic boom while targeting a mob that is directly adjacent

### Digging

Wardens dig into the ground and disappear after 60 seconds without sensing a threat. They
re-emerge when a sculk shrieker is triggered again.

## Sound Identifiers (Java Edition)

| Event | Identifier |
|-------|-----------|
| Ambient | entity.warden.ambient |
| Death | entity.warden.death |
| Hurt | entity.warden.hurt |
| Step | entity.warden.step |
| Emerge | entity.warden.emerge |
| Dig | entity.warden.dig |
| Roar | entity.warden.roar |
| Sonic Boom | entity.warden.sonic_boom |
| Sonic Charge | entity.warden.sonic_charge |
| Heartbeat | entity.warden.heartbeat |
| Agitated | entity.warden.agitated |
| Tendril Click | entity.warden.tendril_clicks |
| Listening Angry | entity.warden.listening_angry |
| Sniff | entity.warden.sniff |

## Trivia

- Has more health than any other mob in the game (500 HP vs Ender Dragon's 200 HP).
- The only mob designed to be avoided rather than fought.
- Despite its power, the warden drops only 5 XP -- same as a standard hostile mob.
