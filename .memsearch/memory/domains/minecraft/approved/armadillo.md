---
source_url: https://minecraft.wiki/w/Armadillo
retrieved_at: 2026-06-05T15:00:00.000Z
source_version: "1.21.1"
tool: firecrawl
format: markdown
content_hash: 8e8485cc4d2f5ac3c4ddf81b0d143f48482c1f18e1dc6a8ed8ed3c039fd73cfc
source_type: official_wiki
repaired_at: 2026-06-05
repair_reason: original entry truncated -- drops, behavior, breeding, and entity data were missing
---

# Armadillo

A passive mob found in badlands and savannas. Rolls up when hurt or threatened. The only source
of armadillo scutes. Repels spiders and cave spiders.

## Stats

- Health: 12 HP (6 hearts)
- Behavior: Passive
- Mob type: Animal
- Speed: 0.14
- Hitbox (adult): 0.65 blocks tall, 0.7 blocks wide
- Hitbox (baby): 0.39 blocks tall, 0.42 blocks wide

## Spawning

- Savannas, savanna plateaus, windswept savannas: groups of 2-3 (JE) / 4 (BE)
- Badlands, eroded badlands, wooded badlands: groups of 1-2
- Light level: 9+ (JE) / 7+ (BE)
- Bedrock Edition: 5% chance to spawn as baby

## Drops

### Passive scute shedding

Armadillos drop 1 armadillo scute every 5-10 minutes automatically, similar to chickens laying eggs.

### On death

- 1-3 XP if killed by a player or tamed wolf
- Killing a baby armadillo yields no items or XP

### Brushing

- Use a brush on an armadillo to obtain 1 armadillo scute
- Each brush use costs 16 durability
- An unenchanted brush yields 4 scutes (JE) / 5 scutes (BE) before breaking
- Dispensers can also brush armadillos

### Breeding XP

- 1-7 XP orbs dropped on successful breed

## Behavior

### Spider repulsion

Spiders and cave spiders flee from armadillos within 6 blocks. Only applies when the armadillo
is not rolled up.

### Rolling up

Triggers when the armadillo is:
- Hurt
- Near undead mobs
- Near players who are sprinting or riding anything

While rolled up:
- Does not walk, cannot eat, not tempted by food
- Damage reduced: (original damage - 1) / 2
- Occasionally peeks out to check for threats
- Unrolls after detecting no threats for 3 seconds (60 ticks)
- Unrolls immediately if it touches water or is attached to a lead

Does NOT roll up when:
- Fleeing
- In water
- On fire (exception: campfires do trigger roll-up)
- In the air
- Being led

Threat detection range: hitbox inflated by 7 blocks horizontally and 2 blocks vertically.

### Breeding

- Bred with spider eyes
- Follows players holding spider eyes within 10 blocks (JE) / 16 blocks (BE)
- Baby armadillos follow adults
- Baby growth time: 24000 ticks (20 minutes)
- Growth accelerated by spider eyes: each use removes 10% of remaining growth time
- Breeding cooldown: 5 minutes
- If armadillo rolls up after being fed but before reaching its mate, willingness is lost;
  must be fed another spider eye

## Entity Data (Java Edition)

- scute_time (Int): ticks until next scute drop; resets to random value between 6000-12000
- state (String): current posture
  - "idle" -- standing normally, not rolled up
  - "scared" -- rolled up, threatened
  - "unrolling" -- playing unroll animation, exiting scared state
  - Any other value defaults to idle behavior

## Sound Identifiers (Java Edition)

| Event | Identifier |
|-------|-----------|
| Ambient | entity.armadillo.ambient |
| Death | entity.armadillo.death |
| Hurt (unrolled) | entity.armadillo.hurt |
| Hurt (rolled up) | entity.armadillo.hurt_reduced |
| Eat | entity.armadillo.eat |
| Footstep | entity.armadillo.step |
| Rolls up (lands) | entity.armadillo.land |
| Peeks | entity.armadillo.peek |
| Rolls up | entity.armadillo.roll |
| Starts unrolling | entity.armadillo.unroll_start |
| Finishes unrolling | entity.armadillo.unroll_finish |
| Brushed | entity.armadillo.brush |
| Scute shed | entity.armadillo.scute_drop |

## Trivia

- Only mob vote winner released in a minor update (Armored Paws -- JE 1.20.5 / BE 1.20.80)
  instead of the next major update.
- Final mob added through a mob vote.
- Shortest dev time of any mob vote winner -- added to snapshots two months after winning
  Minecraft Live 2023.
- Originally had eyes on the front of its face (Jasper Boerstra, for player connection);
  reverted after feedback. Plush toy tag still uses the original front-eye design.
- First iteration had simple animations due to sniffer feedback; later made more expressive.
- Head originally peeked out while balled up in early dev because it was "deemed amusing";
  removed before release.
- Classified as passive but referred to as neutral on the official minecraft.net website.

Armadillo
=========

From Minecraft Wiki

[Jump to navigation](https://minecraft.wiki/w/Armadillo#mw-head)
 [Jump to search](https://minecraft.wiki/w/Armadillo#searchInput)

![](https://minecraft.wiki/images/Disambig_color.svg?2db52) For other uses, see [Armadillo (disambiguation)](https://minecraft.wiki/w/Armadillo_(disambiguation) "Armadillo (disambiguation)")
.

Armadillo

*   [Standing](https://minecraft.wiki/w/Armadillo#)
    
*   [Rolled up](https://minecraft.wiki/w/Armadillo#)
    

[![Armadillo JE2 BE2.png: Infobox image for Minecraftentity Armadillo](https://minecraft.wiki/images/thumb/Armadillo_JE2_BE2.png/300px-Armadillo_JE2_BE2.png?9cdf1)](https://minecraft.wiki/w/File:Armadillo_JE2_BE2.png)

Adult

[![Baby Armadillo JE3.png: Infobox image for Minecraftentity Armadillo](https://minecraft.wiki/images/thumb/Baby_Armadillo_JE3.png/180px-Baby_Armadillo_JE3.png?8d5bc)](https://minecraft.wiki/w/File:Baby_Armadillo_JE3.png)

Baby

[![Armadillo Armor JE2 BE2.png: Infobox image for Minecraftentity Armadillo](https://minecraft.wiki/images/thumb/Armadillo_Armor_JE2_BE2.png/300px-Armadillo_Armor_JE2_BE2.png?74e95)](https://minecraft.wiki/w/File:Armadillo_Armor_JE2_BE2.png)

Adult

[![Baby Armadillo Armor JE3.png: Infobox image for Minecraftentity Armadillo](https://minecraft.wiki/images/thumb/Baby_Armadillo_Armor_JE3.png/180px-Baby_Armadillo_Armor_JE3.png?2da43)](https://minecraft.wiki/w/File:Baby_Armadillo_Armor_JE3.png)

Baby

![Invicon Armadillo Spawn Egg.png: Inventory sprite for Armadillo Spawn Egg in Minecraft as shown in-game with description: Armadillo Spawn Egg](https://minecraft.wiki/images/Invicon_Armadillo_Spawn_Egg.png?75994)

|     |     |
| --- | --- |
| [Health points](https://minecraft.wiki/w/Health "Health") | 12HP ![❤️](https://minecraft.wiki/images/Heart_%28icon%29.png?faf83) × 6 |
| Behavior | Passive |
| [Mob type](https://minecraft.wiki/w/Mob_type "Mob type") | [![](https://minecraft.wiki/images/EntitySprite_animal.png?df5c6)](https://minecraft.wiki/w/Animal "Animal")<br>[Animal](https://minecraft.wiki/w/Animal "Animal") |
| [Hitbox size](https://minecraft.wiki/w/Hitbox "Hitbox") | **Adult:**  <br>Height: 0.65 blocks  <br>Width: 0.7 blocks  <br><br>**Baby:**  <br>Height: 0.39 blocks  <br>Width: 0.42 blocks |
| [Speed](https://minecraft.wiki/w/Attribute#Movement_speed "Attribute") | 0.14 |
| [Spawn](https://minecraft.wiki/w/Mob_spawning "Mob spawning") | *   [![](https://minecraft.wiki/images/BiomeSprite_badlands.png?67438)](https://minecraft.wiki/w/Badlands "Badlands")<br>    [Badlands](https://minecraft.wiki/w/Badlands "Badlands")<br>    <br>*   [![](https://minecraft.wiki/images/BiomeSprite_eroded-badlands.png?c5dd5)](h

---

Trivia
------

\[[edit](https://minecraft.wiki/w/Armadillo?section=20&veaction=edit "Edit section: Trivia")\
 | [edit source](https://minecraft.wiki/w/Armadillo?action=edit&section=20 "Edit section's source code: Trivia")\
\]

*   The first iteration of the armadillo had deliberately basic animations due to feedback that the sniffer's animations were a bit too advanced. However, the developers felt that quality expectations had been raised, so subsequent armadillo animations were made more expressive.[\[1\]](https://minecraft.wiki/w/Armadillo#cite_note-3)
    [\[2\]](https://minecraft.wiki/w/Armadillo#cite_note-4)
    
*   When first added to the game, the armadillo had its eyes on the front of its face. According to [Jasper Boerstra](https://minecraft.wiki/w/Jasper_Boerstra "Jasper Boerstra")
     this is because he felt that it would be difficult for the player to form a personal connection with the armadillo if the eyes were on the side of the head, since they wouldn't be visible while facing the mob directly.[\[3\]](https://minecraft.wiki/w/Armadillo#cite_note-5)
     This decision was reverted following feedback.[\[4\]](https://minecraft.wiki/w/Armadillo#cite_note-6)
    *   The tag on the armadillo plush however uses this initial design, despite the plush itself using its final design.
*   In earlier development versions, though not entirely realistic, the armadillo's head peeked out when balled up because it was deemed amusing.[\[5\]](https://minecraft.wiki/w/Armadillo#cite_note-7)
    
*   Despite its passive nature, the armadillo has been referred to as a neutral mob on the official [minecraft.net](https://minecraft.wiki/w/Minecraft.net "Minecraft.net")
     website.[\[6\]](https://minecraft.wiki/w/Armadillo#cite_note-8)
    [\[7\]](https://minecraft.wiki/w/Armadillo#cite_note-9)
    
*   The armadillo is the only mob voted in during a [mob vote](https://minecraft.wiki/w/Mob_Vote "Mob Vote")
     that was released in a minor update (officially the _[Armored Paws](https://minecraft.wiki/w/Armored_Paws "Armored Paws")
    _ drop as _[Java Edition](https://minecraft.wiki/w/Java_Edition "Java Edition")
    _ [1.20.5](https://minecraft.wiki/w/Java_Edition_1.20.5 "Java Edition 1.20.5")
     and _[Bedrock Edition](https://minecraft.wiki/w/Bedrock_Edition "Bedrock Edition")
    _ [1.20.80](https://minecraft.wiki/w/Bedrock_Edition_1.20.80 "Bedrock Edition 1.20.80")
    ), instead of the [next major update](https://minecraft.wiki/w/Tricky_Trials "Tricky Trials")
    .
    *   It is also the final mob to be added through a mob vote.
*   The development time for the armadillo was the shortest for any mob vote winner, introduced in a [snapshot](https://minecraft.wiki/w/Java_Edition_23w51a "Java Edition 23w51a")
     just two months after winning the vote at [Minecraft Live 2023](https://minecraft.wiki/w/Minecraft_Live_2023 "Minecraft Live 2023")
    .

