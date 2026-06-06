---
source_url: https://minecraft.wiki/w/Vex
retrieved_at: 2026-06-04T22:39:08.068Z
source_version: "1.21.1"
tool: firecrawl
format: markdown
content_hash: 25e9f972ce524947d6e19f55147ce57013546ed8e9c7fbf6b8a1ac0424b73cca
source_type: official_wiki
repaired_at: 2026-06-05
repair_reason: original entry truncated -- only infobox stub and Trivia present; spawning, drops, and behavior were missing
---

# Vex

A small flying hostile mob summoned by evokers. Wields an iron sword. Can phase through any block.
Attacks from behind; difficult to shield-block. Dies on a timer if evoker-summoned.

## Stats

- Health: 14 HP (7 hearts)
- Behavior: Hostile
- Mob type: Monster
- Hitbox: 0.8 blocks tall, 0.4 blocks wide

## Attack Strength

**Armed (iron sword):**
- Easy: 5.5 HP (JE) / slightly lower (BE)
- Normal: 9 HP
- Hard: 13.5 HP

**Unarmed:**
- JE: Easy 3, Normal 4, Hard 6 HP
- BE: slightly lower values

## Spawning

Only spawned by evokers as part of their summoning attack:
- Evoker is surrounded by white particles and makes a magical horn sound
- Summons a group of 3 vexes near the evoker
- Evoker can summon additional vexes even if previous summoned vexes are still alive
- Blocks can obstruct spawn locations (vex is placed at a valid adjacent location)

Vexes spawned via monster spawner, spawn egg, or `/summon` command do NOT automatically target
players (only attack if provoked or commanded).

## Drops

- Iron sword: does NOT drop (HandDropChances = 0 in JE / SlotDropChances = 0 in BE); not affected
  by Looting enchantment
- 5 XP when killed by a player or tamed wolf

## Behavior

### Targeting

Attacks:
- Players
- Adult villagers
- Iron golems
- Wandering traders
- Any mob that attacks the evoker (JE: only mobs that attack the evoker; BE: any mob)

If any mob attacks a vex, ALL vexes in the area become hostile toward that mob. This includes
"Johnny" vindicators attacking them (both are illager-allied but vexes retaliate).

Unarmed vexes still target players/villagers but deal no damage (raises both hands when attacking).

### Flight and Phasing

- Flies freely through the air
- Can pass through ANY block: solid blocks, water, lava, bedrock
- Does NOT take damage from water or lava while phasing through
- CAN die in the void if it flies below the world
- Cobwebs do NOT slow vexes (JE)
- Honey blocks CAN slow vexes (BE only)
- Can be bounced by slime blocks pushed by pistons

### Attack Pattern

While attacking, vexes glow red and lunge at their target. They frequently fly to the back of
their target and attack from behind, making shield-blocking very difficult.

Do not count toward the raid bossbar; the game does not classify them as raiders.

### Lifespan (Evoker-Summoned Only)

Vexes summoned by an evoker begin taking damage after 30-119 seconds and eventually die. This
lifespan timer applies even while riding a minecart or boat.

Vexes from spawners, spawn eggs, or `/summon` do NOT have this lifespan timer.

### Idle Range (Java Edition)

When idle, vexes stay within a 15×11×15 block cuboid centered on the evoker's position at the
time the vex was summoned. They roam freely within this range.

## Sound Identifiers

| Event | JE Identifier | BE Identifier |
|-------|--------------|--------------|
| Ambient | entity.vex.ambient | mob.vex.ambient |
| Charge (attacking) | entity.vex.charge | mob.vex.charge |
| Death | entity.vex.death | mob.vex.death |
| Hurt | entity.vex.hurt | mob.vex.hurt |

## Trivia

- Despite their ghostly appearance and ability to phase through walls, vexes are NOT undead mobs.
- The retexturing and remodeling (from the 1.20.2 redesign) was long requested, per JAPPA.
