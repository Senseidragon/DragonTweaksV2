---
source_url: https://minecraft.wiki/w/Snow_Golem
retrieved_at: 2026-06-04T22:39:08.068Z
source_version: "1.21.1"
tool: firecrawl
format: markdown
content_hash: 18f0d35a9eb6cc38a200ac2e7e9792215892ee20010d318abe8e469efbb6a9be
source_type: official_wiki
repaired_at: 2026-06-05
repair_reason: original entry truncated -- only infobox stub present; creation, drops, behavior, and entity data were missing
---

# Snow Golem

A buildable passive mob that throws snowballs at monsters. Leaves a snow trail. Melts in hot biomes.
Only source of renewable snowballs.

## Stats

- Health: 4 HP (2 hearts)
- Behavior: Passive (attacks monsters but not players)
- Speed: 0.2
- Hitbox (JE): 1.9 blocks tall, 0.7 blocks wide
- Hitbox (BE): 1.8 blocks tall, 0.4 blocks wide

## Creation (Spawning)

Build by stacking two snow blocks vertically, then placing a carved pumpkin or jack o'lantern on
top. Rules:
- Pumpkin must be the LAST block placed
- Works sideways or upside-down (orientation doesn't matter, just pumpkin last)
- Can be built by: player, dispenser, enderman
- CANNOT be built by pistons
- Snow golem spawns at the location of the lower snow block

## Drops

### When Sheared

- 1 carved pumpkin (removes it from head)

### On Death

- 0-15 snowballs (average 7.5)
- Looting does NOT increase snowball drops

## Behavior

### Movement

Wanders aimlessly. Avoids water, obstacles, and environmental hazards. Immune to:
- Fall damage
- Powder snow damage

Takes 1 HP/tick (0.5 hearts/tick) when in:
- Rain or water
- Biomes with temperature > 1.0 (savannas, badlands [JE], deserts, Nether biomes)

Fire Resistance effect allows snow golems to survive in hot biomes without taking damage.

### Snow Trail

Leaves a 1-layer snow trail on walkable surfaces as it moves:
- JE: leaves snow in ALL biomes
- BE: does NOT leave snow in high-temperature biomes (jungle, mushroom fields, savanna, stony peaks,
  desert, badlands, Nether biomes)
- Trail disabled if `mobGriefing` game rule is `false`

### Combat

- Targets all monsters within 10 blocks (except ghasts and undead mounts)
- Also targets creepers
- Does NOT target players or other golems
- Throws 1 snowball per second
- Normal range: 10 blocks
- Range when enclosed/caged: 16 blocks (Euclidean distance)

Snowball damage:
- Blazes: 3 HP per hit
- All other mobs: 0 damage (knockback only)

BE-specific: Snowball through a lava block or burning block can set target mobs on fire.

BE-specific: If a snowball accidentally hits a player, tamed wolves and trusting foxes turn hostile
toward the snow golem.

### Provocation by Other Mobs

Snow golems are not provoked by other golems (snow or iron) attacking them. However, an iron golem
accidentally hit by a snow golem's snowball WILL attack the snow golem back.

### Pumpkin Head

Snow golem wears its carved pumpkin as a helmet. Actual face is hidden underneath. Shearing removes
the pumpkin, revealing its face. The sheared pumpkin is an item drop (1 carved pumpkin).

After shearing, the pumpkin can only be restored via command (`/data merge entity ...`).

## Entity Data (Java Edition)

- `Pumpkin` (Byte): 1 = has pumpkin on head, 0 = sheared/no pumpkin
  - Stored separately from ArmorItems; a snow golem without pumpkin still shows no helmet slot

## Sound Identifiers (Java Edition)

| Event | Identifier |
|-------|-----------|
| Ambient | entity.snow_golem.ambient |
| Death | entity.snow_golem.death |
| Hurt | entity.snow_golem.hurt |
| Shoot (throw snowball) | entity.snow_golem.shoot |
