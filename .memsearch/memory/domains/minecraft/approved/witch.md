---
source_url: https://minecraft.wiki/w/Witch
retrieved_at: 2026-06-04T22:39:08.068Z
source_version: "1.21.1"
tool: firecrawl
format: markdown
content_hash: placeholder-witch-repair
source_type: official_wiki
repaired_at: 2026-06-05
repair_reason: original entry truncated -- only infobox stub present; spawning, drops, and behavior were missing
---

# Witch

A hostile mob that uses potions offensively and defensively. Spawns in swamp huts. Only mob with
built-in magic damage resistance. Converts villagers struck by lightning.

## Stats

- Health: 26 HP (13 hearts)
- Behavior: Hostile
- Mob type: Monster
- Speed: 0.25
- Hitbox (JE): 1.95 blocks tall, 0.6 blocks wide

## Attack Strength

Throws splash potions at targets. Does not melee attack.

## Spawning

- Light level 0 in any Overworld biome except mushroom fields and deep dark
- JE: also not rivers, frozen rivers, snowy plains
- BE: also excludes rivers, frozen rivers, snowy plains
- Groups of 1 (JE) / 1-3 (BE)

### Swamp Hut

Each swamp hut generates with exactly 1 witch (and 1 black cat in JE). These witches never
despawn, even if the player moves far away.

### Raids

Witches participate in raids starting at wave 3 (JE) or wave 4 (BE). More witches appear in
later waves.

### Lightning Conversion

If a villager is within 4 blocks of a lightning bolt, it instantly converts into a witch. The
converted witch does not despawn and cannot be reverted to a villager.

## Drops

### On Death (player or tamed wolf kill only)

- Redstone dust: 1-6 (guaranteed at least 1, avg ~4)
- Glass bottles: 0-6
- Glowstone dust: 0-6
- Gunpowder: 0-6
- Spider eyes: 0-6
- Sugar: 0-6
- Sticks: 0-6

## Behavior

### Potion Combat

Witches throw harmful splash potions at targets:
- Poison (most common)
- Slowness
- Weakness
- Instant Damage (when target is below half health)

They maintain distance and throw potions rather than approaching to melee.

### Self-Buffing

Witches drink potions to protect themselves:
- Fire Resistance when on fire
- Water Breathing when in water
- Healing when below half health
- Speed when chasing a fleeing target

### Magic Resistance

Witches have 85% magic damage resistance (JE) / 95% (BE). This applies to all magic damage
including the warden's sonic boom -- greatly reduces sonic boom damage to the witch.

## Sound Identifiers (Java Edition)

| Event | Identifier |
|-------|-----------|
| Ambient | entity.witch.ambient |
| Death | entity.witch.death |
| Hurt | entity.witch.hurt |
| Drink (potions) | entity.witch.drink |
| Throw (potions) | entity.witch.throw |

## Trivia

- One of the few mobs that can both attack and heal itself using potions.
- The witch's magic resistance is the highest of any mob in the game by this mechanic.
