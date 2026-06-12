---
source_url: https://minecraft.wiki/w/Ruined_portal
title: "Ruined Portal – Minecraft Wiki"
source_version: "1.21.1"
source_type: official_wiki
cleaned: true
---

Ruined Portal

(Redirected from Ruined portal
)

For the April fools version, see Ruined Portatol.

Ruined Portal

*   Overworld
    
*   Nether
    

Normal

Giant

Normal

Giant

|     |     |
| --- | --- |
| Biomes | All <br>Overworld<br> biomes except the Deep Dark  <br>All <br>Nether<br> biomes |
| Consists of | See § Structure |

A **ruined portal** is a structure resembling a damaged Nether portal, which generates commonly in both the Nether and the Overworld. It contains some decoration and a loot chest around it.

Generation
----------

Underside of a ruined portal

Ruined portals are the only structures that generate in more than one dimension; they generate in all biomes in both the Nether and the Overworld, except the deep dark. They can spawn underground, underwater, or exposed to the air. If they generate underground, they have air pockets around them. Natural terrain around ruined portals generates as netherrack. They also generate a mass of netherrack underneath them (including "stalactite"-like shapes, and this may contain blackstone deposits in the Nether). Giant ruined portals have 3 distinct designs, and normal ruined portals have 10 designs. When a ruined portal generates, it has a 5% chance to be a giant ruined portal, for about a 1.67% chance per giant portal design. This gives normal ruined portals a 95% chance, for a 9.5% chance per normal ruined portal design.

Ruined portals generate in a grid of squares 25 chunks (400 blocks) wide with 15 chunks (240 blocks) of buffer space between the squares. In other words, a ruined portal can generate at X and Z coordinates between 0 and 399 mod 640. One ruined portal generates per square.

|     | Biome |     |     |     |     |     |     |     |     |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Standard |     | Desert | Jungle | Swamp | Mountain |     | Ocean | Nether |
| Vertical  <br>placement | Underground  <br>(50%) | Surface  <br>(50%) | Partly buried | Surface | Surface | Inside mountain  <br>(50%) | Surface  <br>(50%) | Ocean floor | Nether |
| Mossiness | 0.2 |     | 0   | 0.8 | 0.5 | 0.2 |     | 0.8 | 0   |
| Air Pocket | Yes | 50% | No  | 50% | No  | Yes | 50% | No  | 50% |
| Overgrown | No  |     | No  | Yes | No  | No  |     | No  | No  |
| Vines | No  |     | No  | Yes | Yes | No  |     | No  | No  |
| Blackstone | No  |     | No  | No  | No  | No  |     | No  | Yes |

Ruined portals that generate underground do so at Y-level from 15 to `n−n2`, where `n` is the highest block at the point of generation and `n2` is the height of the ruined portal structure. This means the terrain is always higher than or level with the top of the structure. Some ruined portal variants are short enough to generate completely on the surface.

Ruined portals that generate in windswept hills generate at y-level from 70 to `n−n2`.

Ruined portals that generate partially buried do so at y-level `n−n2`, plus a random integer from 2 to 8. This means that the structure has 2 to 8 layers raised above the surface.

In the Nether, ruined portals with air pockets generate from Y-level 32 to 100. Ruined portals without air pockets have a 50% chance to spawn from Y-level 27 to 29, and a 50% chance to spawn from Y-level 29 to 100.

Many blocks in ruined portals are replaced upon generation. A ruined portal is in a **cold** biome if the temperature is less than 0.15.

| Original Block | Replacing Block | Chance per Block |
| --- | --- | --- |
| <br>Block of Gold | <br>Air | 30% |
| <br>Lava | <br>Magma Block | On ocean floor: 100%  <br>Not on ocean floor: 20%  <br>Cold: 0% |
| <br>Netherrack | Not cold: 0%  <br>Cold: 100% |
| <br>Netherrack | <br>Magma Block | Not cold: 7%  <br>Cold: 0% |
| <br>Obsidian | <br>Crying Obsidian | 15%   <br>20%  |
| <br>Stone<br>  <br><br>Stone Bricks<br>  <br><br>Chiseled Stone Bricks | <br>Cracked Stone Bricks<br>  <br><br>Stone Brick Stairs | 50%×(1-Mossiness) |
| <br>Mossy Stone Bricks<br>  <br><br>Mossy Stone Brick Stairs | 50%×Mossiness |
| Any slabs | <br>Mossy Stone Brick Slab | Mossiness |
| Any stairs | <br>Stone Slab<br>  <br><br>Stone Brick Slab | 50%×(1-Mossiness) |
| <br>Mossy Stone Brick Stairs<br>  <br><br>Mossy Stone Brick Slab | 50%×Mossiness |
| Any walls | <br>Mossy Stone Brick Wall | Mossiness |

1.     This includes netherrack generated as part of the netherrack "spread" through nearby terrain.

Structure
---------

Main article: /Structure

Ruined portals generate damaged portal frames composed of obsidian, sometimes along with crying obsidian, although Nether portals cannot be activated with crying obsidian in the frame. Some frames generate flat on the ground, as if they toppled over. Others are free-standing separately, as if still being assembled/attached.

All ruined portals generate with a chest in , but in , the chest may be replaced if terrain overrides it (often lava). Each chest contains various gold items and items used to build portals, such as obsidian and flint and steel.

Any ruined portal generated in the Overworld is surrounded with structures made of stone, stone bricks, and iron bars
; in the Nether, it is surrounded by blackstone variants and chains. However, both the jungle and swamp variants of `ruined_portal/portal_6` may spawn without their stone brick slab foundations.

There are 13 variants: 10 normal size portals and 3 giant portals in varying states of decay.

Ruined portals can be generated by the player by loading `ruined_portal/portal_<1 to 10>` or `ruined_portal/giant_portal_<1 to 3>` with a structure block. These ruined portals generate as they are stored, meaning they are not modified as detailed in the Generation section above. All portals generated this way create air pockets if generated in other blocks.

### Overworld

Note that some blocks can be replaced with others (such as crying obsidian
) upon generation. A full list can be seen below.

| Structure name | Description | Consists of | Images |
| --- | --- | --- | --- |
| `ruined_portal/giant_portal_1` | A large incomplete portal with the top left and bottom right corners complete. | 263 <br>Netherrack<br>  <br>63 <br>Stone Bricks<br>  <br>38 <br>Stone Brick Slab<br>  <br>31 <br>Obsidian<br>  <br>20 <br>Stone Brick Stairs<br>  <br>18 <br>Iron Bars<br>  <br>11 <br>Lava<br>  <br>4 <br>Chiseled Stone Bricks<br>  <br>2 <br>Block of Gold<br>  <br>1 <br>Chest |  |
| `ruined_portal/giant_portal_2` | A large incomplete portal. | 237 <br>Netherrack<br>  <br>55 <br>Stone Bricks<br>  <br>30 <br>Stone Brick Slab<br>  <br>29 <br>Obsidian<br>  <br>22 <br>Iron Bars<br>  <br>19 <br>Lava<br>  <br>18 <br>Stone Brick Stairs<br>  <br>6 <br>Chiseled Stone Bricks<br>  <br>2 <br>Block of Gold<br>  <br>1 <br>Chest |  |
| `ruined_portal/giant_portal_3` | A large incomplete portal, with the most gold blocks. Obsidian is randomly spread through the ruined portal. | 324 <br>Netherrack<br>  <br>51 <br>Stone Bricks<br>  <br>40 <br>Stone Brick Slab<br>  <br>33 <br>Lava<br>  <br>25 <br>Obsidian<br>  <br>22 <br>Iron Bars<br>  <br>6 <br>Block of Gold<br>  <br>6 <br>Chiseled Stone Bricks<br>  <br>6 <br>Stone Brick Stairs<br>  <br>1 <br>Chest |  |
| `ruined_portal/portal_1` | A small incomplete portal, with stone slabs and no lava. These are one of the most common portals. | 55 <br>Netherrack<br>  <br>11 <br>Obsidian<br>  <br>10 <br>Stone Slab<br>  <br>9 <br>Stone Bricks<br>  <br>7 <br>Stone<br>  <br>6 <br>Stone Brick Stairs<br>  <br>3 <br>Chiseled Stone Bricks<br>  <br>2 <br>Block of Gold<br>  <br>1 <br>Chest<br>  <br>1 <br>Cracked Stone Bricks<br>  <br>1 <br>Stone Brick Slab |  |
| `ruined_portal/portal_2` | A hanging incomplete portal. | 115 <br>Netherrack<br>  <br>26 <br>Lava<br>  <br>19 <br>Stone Bricks<br>  <br>14 <br>Stone Brick Slab<br>  <br>11 <br>Obsidian<br>  <br>10 <br>Smooth Stone Slab<br>  <br>4 <br>Stone Brick Stairs<br>  <br>2 <br>Block of Gold<br>  <br>2 <br>Iron Bars<br>  <br>1 <br>Chest<br>  <br>1 <br>Chiseled Stone Bricks<br>  <br>1 <br>Mossy Stone Bricks |  |
| `ruined_portal/portal_3` | A small incomplete portal, with no gold block. | 132 <br>Netherrack<br>  <br>36 <br>Stone Bricks<br>  <br>16 <br>Stone Brick Slab<br>  <br>11 <br>Obsidian<br>  <br>2 <br>Lava<br>  <br>1 <br>Chest |  |
| `ruined_portal/portal_4` | A small incomplete portal. | 130 <br>Netherrack<br>  <br>36 <br>Stone Bricks<br>  <br>16 <br>Stone Brick Slab<br>  <br>11 <br>Obsidian<br>  <br>3 <br>Lava<br>  <br>1 <br>Block of Gold<br>  <br>1 <br>Chest |  |
| `ruined_portal/portal_5` | A small portal, where the top of the portal has fallen over. | 145 <br>Netherrack<br>  <br>22 <br>Stone Bricks<br>  <br>15 <br>Obsidian<br>  <br>12 <br>Stone Brick Slab<br>  <br>6 <br>Stone<br>  <br>5 <br>Stone Slab<br>  <br>3 <br>Block of Gold<br>  <br>2 <br>Mossy Stone Bricks<br>  <br>1 <br>Chest<br>  <br>1 <br>Cracked Stone Bricks<br>  <br>1 <br>Lava<br>  <br>1 <br>Stone Brick Stairs |  |
| `ruined_portal/portal_6` | A 5×5 portal, where the top center block is misplaced. | 41 <br>Netherrack<br>  <br>16 <br>Obsidian<br>  <br>4 <br>Stone Brick Slab<br>  <br>2 <br>Stone Brick Stairs<br>  <br>1 <br>Block of Gold<br>  <br>1 <br>Chest |  |
| `ruined_portal/portal_7` | An incomplete portal submerged in lava. | 92 <br>Netherrack<br>  <br>21 <br>Lava<br>  <br>12 <br>Obsidian<br>  <br>3 <br>Stone Brick Slab<br>  <br>2 <br>Block of Gold<br>  <br>1 <br>Chest<br>  <br>1 <br>Stone Bricks<br>  <br>1 <br>Stone Brick Stairs |  |
| `ruined_portal/portal_8` | An incomplete portal at the top of a staircase. The top has fallen into lava. | 144 <br>Netherrack<br>  <br>26 <br>Lava<br>  <br>17 <br>Obsidian<br>  <br>14 <br>Stone Bricks<br>  <br>6 <br>Stone Brick Stairs<br>  <br>4 <br>Stone Brick Wall<br>  <br>3 <br>Block of Gold<br>  <br>2 <br>Chiseled Stone Bricks<br>  <br>1 <br>Chest<br>  <br>1 <br>Stone Brick Slab |  |
| `ruined_portal/portal_9` | A small incomplete portal. | 63 <br>Netherrack<br>  <br>12 <br>Obsidian<br>  <br>11 <br>Stone Brick Stairs<br>  <br>2 <br>Block of Gold<br>  <br>1 <br>Chest<br>  <br>1 <br>Magma Block<br>  <br>1 <br>Stone Brick Slab |  |
| `ruined_portal/portal_10` | An incomplete portal that has fallen backward into lava. | 123 <br>Netherrack<br>  <br>19 <br>Lava<br>  <br>13 <br>Obsidian<br>  <br>13 <br>Stone Bricks<br>  <br>3 <br>Chiseled Stone Bricks<br>  <br>3 <br>Iron Bars<br>  <br>3 <br>Stone Brick Stairs<br>  <br>2 <br>Block of Gold<br>  <br>1 <br>Chest |  |

### Nether

| Structure name | Description | Consists of | Images |
| --- | --- | --- | --- |
| `ruined_portal/giant_portal_1` | A large incomplete portal. | 263 <br>Netherrack<br>  <br>63 <br>Polished Blackstone Bricks<br>  <br>38 <br>Polished Blackstone Brick Slab<br>  <br>31 <br>Obsidian<br>  <br>20 <br>Polished Blackstone Brick Stairs<br>  <br>18 <br>Iron Chain<br>  <br>11 <br>Lava<br>  <br>4 <br>Chiseled Polished Blackstone<br>  <br>2 <br>Block of Gold<br>  <br>1 <br>Chest |  |
| `ruined_portal/giant_portal_2` | A large incomplete portal. | 237 <br>Netherrack<br>  <br>55 <br>Polished Blackstone Bricks<br>  <br>30 <br>Polished Blackstone Brick Slab<br>  <br>29 <br>Obsidian<br>  <br>22 <br>Iron Chain<br>  <br>19 <br>Lava<br>  <br>18 <br>Polished Blackstone Brick Stairs<br>  <br>6 <br>Chiseled Polished Blackstone<br>  <br>2 <br>Block of Gold<br>  <br>1 <br>Chest |  |
| `ruined_portal/giant_portal_3` | A large incomplete portal, with the most gold blocks, separate obsidian. | 324 <br>Netherrack<br>  <br>51 <br>Polished Blackstone Bricks<br>  <br>40 <br>Polished Blackstone Brick Slab<br>  <br>33 <br>Lava<br>  <br>25 <br>Obsidian<br>  <br>22 <br>Iron Chain<br>  <br>6 <br>Block of Gold<br>  <br>6 <br>Chiseled Polished Blackstone<br>  <br>6 <br>Polished Blackstone Bricks<br>  <br>1 <br>Chest |  |
| `ruined_portal/portal_1` | A small incomplete portal, with blackstone slabs and no lava. | 55 <br>Netherrack<br>  <br>11 <br>Obsidian<br>  <br>10 <br>Polished Blackstone Slab<br>  <br>9 <br>Polished Blackstone Bricks<br>  <br>7 <br>Polished Blackstone<br>  <br>6 <br>Polished Blackstone Brick Stairs<br>  <br>3 <br>Chiseled Polished Blackstone<br>  <br>2 <br>Block of Gold<br>  <br>1 <br>Chest<br>  <br>1 <br>Cracked Polished Blackstone Bricks<br>  <br>1 <br>Polished Blackstone Brick Slab |  |
| `ruined_portal/portal_2` | A hanging incomplete portal. | 115 <br>Netherrack<br>  <br>26 <br>Lava<br>  <br>20 <br>Polished Blackstone Bricks<br>  <br>14 <br>Polished Blackstone Slab<br>  <br>11 <br>Obsidian<br>  <br>10 <br>Polished Blackstone Slab<br>  <br>4 <br>Polished Blackstone Brick Stairs<br>  <br>2 <br>Block of Gold<br>  <br>2 <br>Iron Chain<br>  <br>1 <br>Chest<br>  <br>1 <br>Chiseled Polished Blackstone |  |
| `ruined_portal/portal_3` | A small incomplete portal, with no gold block. | 132 <br>Netherrack<br>  <br>36 <br>Polished Blackstone Bricks<br>  <br>16 <br>Polished Blackstone Brick Slab<br>  <br>11 <br>Obsidian<br>  <br>2 <br>Lava<br>  <br>1 <br>Chest |  |
| `ruined_portal/portal_4` | A small incomplete portal. | 130 <br>Netherrack<br>  <br>36 <br>Polished Blackstone Bricks<br>  <br>16 <br>Polished Blackstone Brick Slab<br>  <br>11 <br>Obsidian<br>  <br>3 <br>Lava<br>  <br>1 <br>Block of Gold<br>  <br>1 <br>Chest |  |
| `ruined_portal/portal_5` | A small portal, where the top of the portal has fallen over. | 145 <br>Netherrack<br>  <br>24 <br>Polished Blackstone Bricks<br>  <br>15 <br>Obsidian<br>  <br>12 <br>Polished Blackstone Brick Slab<br>  <br>6 <br>Polished Blackstone<br>  <br>5 <br>Polished Blackstone Slab<br>  <br>3 <br>Block of Gold<br>  <br>1 <br>Chest<br>  <br>1 <br>Cracked Polished Blackstone Bricks<br>  <br>1 <br>Lava<br>  <br>1 <br>Polished Blackstone Brick Stairs |  |
| `ruined_portal/portal_6` | A 5×5 portal, where the top center block is misplaced. | 41 <br>Netherrack<br>  <br>16 <br>Obsidian<br>  <br>4 <br>Polished Blackstone Brick Slab<br>  <br>2 <br>Polished Blackstone Brick Stairs<br>  <br>1 <br>Block of Gold<br>  <br>1 <br>Chest |  |
| `ruined_portal/portal_7` | An incomplete portal submerged in lava. | 92 <br>Netherrack<br>  <br>21 <br>Lava<br>  <br>12 <br>Obsidian<br>  <br>3 <br>Polished Blackstone Brick Slab<br>  <br>2 <br>Block of Gold<br>  <br>1 <br>Chest<br>  <br>1 <br>Polished Blackstone Bricks<br>  <br>1 <br>Polished Blackstone Brick Stairs |  |
| `ruined_portal/portal_8` | An incomplete portal at the top of a staircase. The top has fallen into lava. | 144 <br>Netherrack<br>  <br>26 <br>Lava<br>  <br>17 <br>Obsidian<br>  <br>14 <br>Polished Blackstone Bricks<br>  <br>6 <br>Polished Blackstone Brick Stairs<br>  <br>4 <br>Polished Blackstone Brick Wall<br>  <br>3 <br>Block of Gold<br>  <br>2 <br>Chiseled Polished Blackstone<br>  <br>1 <br>Chest<br>  <br>1 <br>Polished Blackstone Slab |  |
| `ruined_portal/portal_9` | A small incomplete portal. | 63 <br>Netherrack<br>  <br>12 <br>Obsidian<br>  <br>11 <br>Polished Blackstone Brick Stairs<br>  <br>2 <br>Block of Gold<br>  <br>1 <br>Chest<br>  <br>1 <br>Magma Block<br>  <br>1 <br>Polished Blackstone Brick Slab |  |
| `ruined_portal/portal_10` | An incomplete portal that has fallen backward into lava. | 123 <br>Netherrack<br>  <br>19 <br>Lava<br>  <br>13 <br>Obsidian<br>  <br>13 <br>Polished Blackstone Bricks<br>  <br>3 <br>Chiseled Polished Blackstone<br>  <br>3 <br>Iron Chain<br>  <br>3 <br>Polished Blackstone Brick Stairs<br>  <br>2 <br>Block of Gold<br>  <br>1 <br>Chest |  |

### Blocks

| All ruined portals | Overworld | Nether |
| --- | --- | --- |
| *   <br>    Block of Gold<br>    <br>*   <br>    Chest<br>    <br>*   <br>    Crying Obsidian<br>    <br>*   <br>    Lava<br>    <br>*   <br>    Magma Block<br>    <br>*   <br>    Netherrack<br>    <br>*   <br>    Obsidian | *   <br>    Iron Bars<br>    <br>*   <br>    Stone<br>    <br>*   <br>    Stone Slab<br>    <br>*   <br>    Smooth Stone Slab<br>    <br>*   <br>    Stone Bricks<br>    <br>*   <br>    Stone Brick Slab<br>    <br>*   <br>    Stone Brick Stairs<br>    <br>*   <br>    Stone Brick Wall<br>    <br>*   <br>    Chiseled Stone Bricks<br>    <br>*   <br>    Cracked Stone Bricks<br>    <br>*   <br>    Mossy Stone Bricks<br>    <br>*   <br>    Mossy Stone Brick Slab<br>    <br>*   <br>    Mossy Stone Brick Stairs<br>    <br>*   <br>    Mossy Stone Brick Wall | *   <br>    Iron Chain<br>    <br>*   <br>    Polished Blackstone<br>    <br>*   <br>    Polished Blackstone Slab<br>    <br>*   <br>    Polished Blackstone Bricks<br>    <br>*   <br>    Polished Blackstone Brick Slab<br>    <br>*   <br>    Polished Blackstone Brick Stairs<br>    <br>*   <br>    Polished Blackstone Brick Wall<br>    <br>*   <br>    Cracked Polished Blackstone Bricks<br>    <br>*   <br>    Chiseled Polished Blackstone |

Loot
----

In  and , each ruined portal chest contains items drawn from 2 pools, with the following distribution:

| Item | Stack Size |     | Weight |     | Chance | Avg.  <br>per chest | hide Avg. # chests  <br>to search |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 4–8× | 1×  | 4–8× | 1×  |
| --- | --- | --- | --- | --- | --- | --- | --- |
| <br>Lodestone | —   | 1–2 | —   | 2⁄3 | 66.7% | 1.000 | 1.5 |
| <br>Iron Nugget | 9–18 | —   | 40⁄398 | —   | 46.4% | 8.141 | 2.2 |
| <br>Flint | 1–4 | —   | 40⁄398 | —   | 46.4% | 1.508 | 2.2 |
| <br>Obsidian | 1–2 | —   | 40⁄398 | —   | 46.4% | 0.905 | 2.2 |
| <br>Fire Charge | 1   | —   | 40⁄398 | —   | 46.4% | 0.603 | 2.2 |
| <br>Flint and Steel | 1   | —   | 40⁄398 | —   | 46.4% | 0.603 | 2.2 |
| Nothing | —   | 1   | —   | 1⁄3 | 33.3% | 0.333 | 3.0 |
| <br>Gold Nugget | 4–24 | —   | 15⁄398 | —   | 20.5% | 3.166 | 4.9 |
| <br>Golden Apple | 1   | —   | 15⁄398 | —   | 20.5% | 0.226 | 4.9 |
| <br>Enchanted Golden Axe | 1   | —   | 15⁄398 | —   | 20.5% | 0.226 | 4.9 |
| <br>Enchanted Golden Hoe | 1   | —   | 15⁄398 | —   | 20.5% | 0.226 | 4.9 |
| <br>Enchanted Golden Pickaxe | 1   | —   | 15⁄398 | —   | 20.5% | 0.226 | 4.9 |
| <br>Enchanted Golden Shovel | 1   | —   | 15⁄398 | —   | 20.5% | 0.226 | 4.9 |
| <br>Enchanted Golden Sword | 1   | —   | 15⁄398 | —   | 20.5% | 0.226 | 4.9 |
| <br>Enchanted Golden Helmet | 1   | —   | 15⁄398 | —   | 20.5% | 0.226 | 4.9 |
| <br>Enchanted Golden Chestplate | 1   | —   | 15⁄398 | —   | 20.5% | 0.226 | 4.9 |
| <br>Enchanted Golden Leggings | 1   | —   | 15⁄398 | —   | 20.5% | 0.226 | 4.9 |
| <br>Enchanted Golden Boots | 1   | —   | 15⁄398 | —   | 20.5% | 0.226 | 4.9 |
| <br>Glistering Melon Slice | 4–12 | —   | 5⁄398 | —   | 7.3% | 0.603 | 13.7 |
| <br>Golden Carrot | 4–12 | —   | 5⁄398 | —   | 7.3% | 0.603 | 13.7 |
| <br>Gold Ingot | 2–8 | —   | 5⁄398 | —   | 7.3% | 0.377 | 13.7 |
| <br>Clock | 1   | —   | 5⁄398 | —   | 7.3% | 0.075 | 13.7 |
| <br>Light Weighted Pressure Plate | 1   | —   | 5⁄398 | —   | 7.3% | 0.075 | 13.7 |
| <br>Golden Horse Armor | 1   | —   | 5⁄398 | —   | 7.3% | 0.075 | 13.7 |
| <br>Block of Gold | 1–2 | —   | 1⁄398 | —   | 1.5% | 0.023 | 66.8 |
| <br>Bell | 1   | —   | 1⁄398 | —   | 1.5% | 0.015 | 66.8 |
| <br>Enchanted Golden Apple | 1   | —   | 1⁄398 | —   | 1.5% | 0.015 | 66.8 |

1.     The size of stacks (or for unstackable items, number) of this item on any given roll.
2.     The weight of this item relative to other items in the pool.
3.     The odds of finding any of this item in a single chest.
4.     The number of items expected per chest, averaged over a large number of chests.
5.     The average number of chests the player should expect to search to find any of this item.
6.     'Nothing' does not refer to the chance of an empty chest. Instead, it refers to the chance that the random loot generator does not add any loot _on a single roll_.
     c d
     e f
     g h
     i
     All enchantments are equally probable, _including_ treasure enchantments
     (except Soul Speed
    , Swift Sneak
    , and Wind Burst
    ), and any level of the enchantment is equally probable.

Most ruined portal structures contain enough obsidian to make a complete portal, although in the smaller portals, there may not enough because each obsidian has a 15% or 20% chance to be replaced by crying obsidian.
