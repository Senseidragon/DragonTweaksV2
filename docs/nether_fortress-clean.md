---
source_version: "1.21.1"
source_type: official_wiki
cleaned: true
---

Nether Fortress
===============

"Fortress" redirects here. For other uses, see Fortress (disambiguation) "Fortress (disambiguation)")
.

A **Nether fortress** is an uncommon large structure made of Nether bricks found in the Nether, consisting of bridges, corridors, and towers. Nether fortresses are the only place where wither skeletons and blazes spawn. They also contain Nether wart.

Nether Fortress

|     |     |
| --- | --- |
| Biomes | Any Nether<br> biome |
| Mobs | <br>Blaze<br>  <br><br>Magma Cube<br>  <br><br>Skeleton<br>  <br><br>Wither Skeleton<br>  <br><br>Zombified Piglin<br>  <br><br>Chicken<br> (rarely through jockeys<br>)\_[Java Edition<br> only_\] |
| Consists of | *   See § Structure<br>    <br>*   See § Blocks |

There is a related tutorial page for this topic!

 

See Tutorial:Defeating a Nether fortress.

Generation
----------

Edit

Nether fortresses generate in all Nether biomes. To do so, the game splits the Nether into regions in which one of either a fortress or a bastion remnant can generate. The regions are 432×432 blocks in  and 480×480 blocks.

The Nether structure generation. The black lines represent each region and the dots represent coordinates. The green color is where they can generate and red is where they cannot.

Each region has a four chunk separation located on the south and east borders of the region in which neither a fortress nor a bastion can generate.

The Nether structure generation. The black lines represent each region and the dots represent coordinates. The green color is where they can generate and red is where they cannot.

This leaves only a 368×368 block section in  or 416×416 block section in  where a structure can generate.

Two structures never generate in the same region, although they might overlap if they generate close to the separation border. The chance of a fortress attempting to generate instead of a bastion is 2⁄5 (40%) in  and 1⁄3 (33.3%). However, if a bastion attempts to generate in the basalt deltas, a fortress generates instead, resulting in fortresses being the most common in the basalt deltas.

Nether fortresses can generate buried in netherrack. In such a case, the interior is not filled with netherrack
; all hallways and passages are clear except for open walkways and bridges. It is possible but rare for glowstone or crimson and warped huge fungi to generate inside the fortress pathways.

Structure
---------

Edit

Main article: /Structure

Nether fortresses are large complexes composed of Nether bricks supported by pillars that tower high above the lava seas.

Segments of a Nether fortress that are fully enclosed.

The fortress generation starts with a plain four-way crossing centered at chunk coordinates 11, ~, 11 of the designated chunk.

A crossroad found where walkways intersect.

A fortress has two areas, an exterior area of open bridges and an interior area of enclosed corridors. Both the bridges and corridors can end in an unfinished passageway structure or may simply end without elaboration. Fortresses can tunnel through netherrack, giving the "exterior" areas an appearance of tunnels with Nether brick floor and netherrack walls and ceilings. At broken sections the terrain is not cleared, which may create a tunnel that leads straight into a wall of netherrack.

The general pattern of the walkways.

A lava well found inside a Nether fortress.

The exterior consists of:

*   Straight bridges.
*   Up to five plain four-way crossings.
*   Up to four four-way crossings with arches made of Nether brick and Nether brick fences.
*   Up to four small rooms with a single entrance and full-block "stairs" leading to the roof, which may have a single path leading out.
*   Up to 2 blaze monster spawner platforms: structures consisting of three full-block "stairs" leading to a small platform fenced with Nether brick fence, with a blaze monster spawner in the center.

Stairs in a Nether fortress, with Nether wart growing next to them.

The interior of the structures have 1×2 windows with Nether brick fences as the windowpanes. The fences also form gate-like structures at the entrances of some rooms and corridors. Rooms include:

*   The lava well room, which is the connection between the interior and exterior areas.
*   Straight corridors.
*   Up to five four-way crossings.
*   Up to 20 corridor turns (10 right-turns and 10 left-turns), each with a 1⁄3 chance of having a loot chest in the corner.
*   Up to three stairways made from actual stair blocks, leading downward.
*   Up to two three-way intersections with a small exterior balcony.
*   Up to two stairways leading up to a garden of soul sand and Nether wart at the base of the stairs, a corridor leading away from the upper landing and a corridor behind the stairs. If the room is generated embedded in netherrack, only one block above the landing is cleared, with a further indent finishing above the stairs.

An outline of the "bounding boxes".

### Blocks

Edit

| Block |
| --- |
| <br>Nether Bricks |
| <br>Nether Brick Fence |
| <br>Nether Brick Stairs |
| <br>Soul Sand |
| <br>Nether Wart |
| <br>Chest |
| <br>Blaze Monster Spawner |
| <br>Lava |

### Bounding boxes

Edit

The structure bounding box for the 4-way intersection is pictured above (top-down view), and consists of a 19×11×19 volume centered on the floor block in the center of the intersection. This contrasts many of the other structure bounding boxes as their outlines tend to tightly follow the physical bounds of the structure. Notably, this also means that the fortress supports, which generate outside of that volume and go down to the ground and often into the lava ocean, are not considered part of the fortress for spawning purposes.

The area bounding box is a rectangular box that covers the entire fortress (again, excluding the supports). This is simply the smallest rectangular bounding box that can hold the bounding boxes of all structure pieces in the fortress.

Mobs
----

Edit

Fortresses use a list of possible mobs to spawn that is separate from the rest of the Nether, regardless of the biome the fortress generates in. This includes zombified piglins, skeletons, and magma cubes, as well as two exclusive mobs not found anywhere else: blazes and wither skeletons.

A blaze monster spawner generated in the Nether fortress.

Mobs spawn at a much higher rate if the fortress is surrounded by soul sand valley or warped forest biomes, as hostile mobs in these biomes spawn much less frequently, allowing more hostile mobs to spawn in the fortress[\[1\]](https://minecraft.wiki/w/Nether_Fortress#cite_note-1)
.

### Java Edition

Edit

The spawning algorithm has two checks:

1.  It checks if the spawn coordinates are within the "bounding box" of a single piece (e.g. corridor or walkway) of the fortress. (referred as "structure bounding box" above) In this case the block type of the ground does not matter.
2.  It checks if the spawn coordinates are within the "bounding box" (referred as "area bounding box" above) of the entire fortress and whether the ground consists of Nether bricks
     (_not_ Nether brick slabs
    ).

If either check passes, it uses the special mob list for fortresses rather than the list for the biome when choosing the mob to spawn. The actual mob spawning proceeds as normal for the mob chosen from this list.

|     |     |     |
| --- | --- | --- |In _**Java Edition
**_
| Mob | Spawn weight | Group size |
| Monster category |     |     |
| <br>Blaze | 10⁄28 | 2–3 |
| <br>Wither Skeleton | 8⁄28 | 5   |
| <br>Zombified Piglin | 5⁄28 | 4   |
| <br>Magma Cube | 3⁄28 | 4   |
| <br>Skeleton | 2⁄28 | 5   |

{ "notes": \[\], "1": { "totalWeight": 28, "mobs": \[ { "size": "2-3", "mob": "Blaze", "weight": 10 }, { "size": "5", "mob": "Wither Skeleton", "weight": 8 }, { "size": "4", "mob": "Zombified Piglin", "weight": 5 }, { "size": "4", "mob": "Magma Cube", "weight": 3 }, { "size": "5", "mob": "Skeleton", "weight": 2 } \], "category": "monster" } }

### Bedrock Edition

Edit

To identify these spawning columns, glass panes or iron bars can be placed all over the fortress, 1 block above surface blocks. This keeps the mobs stationary. (This technique works in Bedrock because structure spawns occur in the northwest corner of blocks.) Note that these spots may be on top of the raised side blocks, so these side blocks have to be removed before a glass pane grid can be placed.

|     |     |     |
| --- | --- | --- |In _**Bedrock Edition
**_
| Mob | Spawn weight | Group size |
| Monster category |     |     |
| <br>Blaze | 10  | 1–2 |
| <br>Wither Skeleton | 8   | 2–3 |
| <br>Zombified Piglin | 5   | 1   |
| <br>Magma Cube | 3   | 2–3 |
| <br>Skeleton | 2   | 2–3 |

{ "notes": \[\], "1": { "totalWeight": 28, "mobs": \[ { "size": "1-2", "mob": "Blaze", "weight": 10 }, { "size": "2-3", "mob": "Wither Skeleton", "weight": 8 }, { "size": "1", "mob": "Zombified Piglin", "weight": 5 }, { "size": "2-3", "mob": "Magma Cube", "weight": 3 }, { "size": "2-3", "mob": "Skeleton", "weight": 2 } \], "category": "monster" } }

Loot
----

Edit

See also: Chest loot

A chest that generated in a Nether fortress.

Fortresses generate Nether fortress loot with chests in the indoor sections placed at some corridor turns.

In  and , each nether fortress chest contains items drawn from 2 pools, with the following distribution:

| Item | Stack Size [\[A\]](https://minecraft.wiki/w/Nether_Fortress#cite_note-stacksize-2) |     | Weight [\[B\]](https://minecraft.wiki/w/Nether_Fortress#cite_note-weight-3) |     | Chance [\[C\]](https://minecraft.wiki/w/Nether_Fortress#cite_note-chance-4) | Avg.  <br>per chest [\[D\]](https://minecraft.wiki/w/Nether_Fortress#cite_note-items-5) | hide Avg. # chests  <br>to search [\[E\]](https://minecraft.wiki/w/Nether_Fortress#cite_note-chests-6) |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 2–4× | 1×  | 2–4× | 1×  |
| --- | --- | --- | --- | --- | --- | --- | --- |
| Nothing[\[F\]](https://minecraft.wiki/w/Nether_Fortress#cite_note-nothing-7) | —   | 1   | —   | 14⁄15 | 93.3% | 0.933 | 1.1 |
| <br>Gold Ingot | 1–3 | —   | 15⁄78 | —   | 46.5% | 1.154 | 2.2 |
| <br>Saddle | 1   | —   | 10⁄78 | —   | 33.3% | 0.385 | 3.0 |
| <br>Golden Horse Armor | 1   | —   | 8⁄78 | —   | 27.4% | 0.308 | 3.6 |
| <br>Nether Wart | 3–7 | —   | 5⁄78 | —   | 17.9% | 0.962 | 5.6 |
| <br>Iron Ingot | 1–5 | —   | 5⁄78 | —   | 17.9% | 0.577 | 5.6 |
| <br>Diamond | 1–3 | —   | 5⁄78 | —   | 17.9% | 0.385 | 5.6 |
| <br>Copper Horse Armor | 1   | —   | 5⁄78 | —   | 17.9% | 0.192 | 5.6 |
| <br>Flint and Steel | 1   | —   | 5⁄78 | —   | 17.9% | 0.192 | 5.6 |
| <br>Iron Horse Armor | 1   | —   | 5⁄78 | —   | 17.9% | 0.192 | 5.6 |
| <br>Golden Sword | 1   | —   | 5⁄78 | —   | 17.9% | 0.192 | 5.6 |
| <br>Golden Chestplate | 1   | —   | 5⁄78 | —   | 17.9% | 0.192 | 5.6 |
| <br>Diamond Horse Armor | 1   | —   | 3⁄78 | —   | 11.1% | 0.115 | 9.0 |
| <br>Obsidian | 2–4 | —   | 2⁄78 | —   | 7.5% | 0.231 | 13.4 |
| <br>Rib Armor Trim Smithing Template | —   | 1   | —   | 1⁄15 | 6.7% | 0.067 | 15.0 |

1.     The size of stacks (or for unstackable items, number) of this item on any given roll.
2.     The weight of this item relative to other items in the pool.
3.     The odds of finding any of this item in a single chest.
4.     The number of items expected per chest, averaged over a large number of chests.
5.     The average number of chests the player should expect to search to find any of this item.
6.     'Nothing' does not refer to the chance of an empty chest. Instead, it refers to the chance that the random loot generator does not add any loot _on a single roll_.

Videos
------

Edit
