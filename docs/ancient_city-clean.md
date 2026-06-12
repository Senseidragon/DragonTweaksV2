---
source_url: https://minecraft.wiki/w/Ancient_City
title: "Ancient City – Minecraft Wiki"
source_version: "1.21.1"
source_type: official_wiki
cleaned: true
---

Ancient City
============

.

An **ancient city** is a palatial structure found in deep dark biomes at Y=-51, that harbors chests containing items, such as disc fragments, armor trims and echo shards, that cannot be found anywhere else. It is also one of the only two places where sculk sensors can be found.

Ancient City

|     |     |
| --- | --- |
| Biomes | <br>Deep Dark |
| Y-level | \-51 |
| Mobs | <br>Warden<br> (from sculk shrieker) |
| Consists of | *   See § Blocks |

There is a related tutorial page for this topic!

 

See Tutorial:Exploring an ancient city.

Generation
----------

Edit

Ancient cities cannot generate overlapping each other; however, they can generate directly adjacent to each other.[\[1\]](https://minecraft.wiki/w/Ancient_City#cite_note-1)

Because they can only generate in the deep dark, they will often be found under mountains and other places with low erosion.

Structure
---------

Edit

For the technical details behind ancient city structures, see Ancient City/Structure. For the blueprints of ancient city structures, see Ancient City/Structure/Blueprints.

### Environment aesthetics

Edit

An ancient city features a very large palace with a footprint of around 220 blocks on each horizontal axis, stretching throughout a deep dark biome. The floor of the city always generates at Y=-51. The palace is made up of long corridors with 2-block-deep floors of gray wool, with the occasional rugs of blue, light blue, and cyan wool or carpet floors to reduce vibrations. Off to the sides of the main corridors are smaller ruin structures, which often contain one or two loot chests. The city's center features a frame that contains exclusive reinforced deepslate blocks, though these are unobtainable in Survival mode. Other unique blocks such as soul lanterns, candles, and different forms of deepslate and sculk can be found throughout the city. Sculk shriekers generate much more frequently in ancient cities than in the normal deep dark biome. The city always generates at the same position within a chunk (for example, the blocks that make up the center frame structure are always on the western edge of a chunk).

Some of the city's structures resemble other elements of the game. The city center bears a resemblance to early iterations of the warden's design, and other structures are similar in appearance to pillager outposts, the wool rooms in woodland mansions, and abandoned mineshafts.

#### Overall view

Edit

The following three renders are of the same individual ancient city.

*   
    
    The view from the direction of the entrance
    
*   
    
    Another direction
    
*   ")
    
    The vertical view (The entrance is in the lower part of this picture)
    

#### Redstone circuits

Edit

Underneath the frame at the city center lies a series of hidden basement rooms with naturally generating redstone circuitry.

The secret entrance is located at the base of the structure, underneath the wooden bridge between the city center and the wall. There is a piston door that is controlled by a sculk sensor in the basement's redstone circuits.

*   
    
    Basement of `city_center_1`
    
*   
    
    Basement of `city_center_2`
    
*   
    
    Basement of `city_center_3`
    
*   
    
    Piston door circuit in the basement of `city_center_3`
    
*   
    
    Furnace in the basement of `city_center_2`
    

For `city_center_1`, there is a chiseled deepslate block in front of the frame. Making a vibration around it can activate the sculk sensor and then open the piston door. There is also a pulse extender between the sculk sensor and piston door, which makes the piston door close 180 game ticks after the sculk sensor is deactivated.

The circuit in `city_center_2` is similar to the former, but with a signal strength filter. Only vibrations with a frequency equal to 8 (eating or drinking) can activate the piston door. Creating these vibrations on the path in the front of the frame in the city center activates the door.

For `city_center_3`, making a vibration on the path in front of the frame on the city center can activate the sculk sensor. Additionally, there is a T flip-flop between the sculk sensor and piston door. After the sculk sensor is activated, the piston door remains open until the sculk sensor is activated again.

There are three basic redstone circuits in the basement:

*   
    
    Circuit of the target block
    
*   
    
    Circuit of the lectern block
    
*   
    
    Circuit to show that some blocks can transfer a signal with a repeater while some cannot
    

### File structure

Edit

All ancient city structures found below are located in the folder client.jar
/data/minecraft/structures/ancient\_city, data/structures/ancient\_city.

| List  |
| --- |
| *   ancient\_city<br>    *   city<br>        *   entrance<br>            *   entrance\_connector.nbt<br>            *   entrance\_path\_1.nbt<br>            *   entrance\_path\_2.nbt<br>            *   entrance\_path\_3.nbt<br>            *   entrance\_path\_4.nbt<br>            *   entrance\_path\_5.nbt<br>    *   city\_center<br>        *   city\_center\_1.nbt<br>        *   city\_center\_2.nbt<br>        *   city\_center\_3.nbt<br>        *   walls<br>            *   bottom\_1.nbt<br>            *   bottom\_2.nbt<br>            *   bottom\_left\_corner.nbt<br>            *   bottom\_right\_corner.nbt<br>            *   bottom\_right\_corner\_1.nbt<br>            *   bottom\_right\_corner\_2.nbt<br>            *   left.nbt<br>            *   right.nbt<br>            *   top\_left\_corner.nbt<br>            *   top\_right\_corner.nbt<br>            *   top.nbt<br>    *   structures<br>        *   barracks.nbt<br>        *   camp\_1.nbt<br>        *   camp\_2.nbt<br>        *   camp\_3.nbt<br>        *   chamber\_1.nbt<br>        *   chamber\_2.nbt<br>        *   chamber\_3.nbt<br>        *   ice\_box\_1.nbt<br>        *   large\_pillar\_1.nbt<br>        *   large\_ruin\_1.nbt<br>        *   medium\_pillar\_1.nbt<br>        *   medium\_ruin\_1.nbt<br>        *   medium\_ruin\_2.nbt<br>        *   sauna\_1.nbt<br>        *   small\_ruin\_1.nbt<br>        *   small\_ruin\_2.nbt<br>        *   small\_statue.nbt<br>        *   tall\_ruin\_1.nbt<br>        *   tall\_ruin\_2.nbt<br>        *   tall\_ruin\_3.nbt<br>        *   tall\_ruin\_4.nbt<br>    *   walls<br>        *   intact\_corner\_wall\_1.nbt<br>        *   intact\_horizontal\_wall\_1.nbt<br>        *   intact\_horizontal\_wall\_2.nbt<br>        *   intact\_horizontal\_wall\_bridge.nbt<br>        *   intact\_horizontal\_wall\_passage\_1.nbt<br>        *   intact\_horizontal\_wall\_stairs\_1.nbt<br>        *   intact\_horizontal\_wall\_stairs\_2.nbt<br>        *   intact\_horizontal\_wall\_stairs\_3.nbt<br>        *   intact\_horizontal\_wall\_stairs\_4.nbt<br>        *   intact\_intersection\_wall\_1.nbt<br>        *   intact\_lshape\_wall\_1.nbt<br>        *   ruined\_corner\_wall\_1.nbt<br>        *   ruined\_corner\_wall\_2.nbt<br>        *   ruined\_horizontal\_wall\_stairs\_1.nbt<br>        *   ruined\_horizontal\_wall\_stairs\_2.nbt<br>        *   ruined\_horizontal\_wall\_stairs\_3.nbt<br>        *   ruined\_horizontal\_wall\_stairs\_4.nbt |

### Components

Edit

#### Entrance

Edit

| Structure name | Description | Consists of | Image |
| --- | --- | --- | --- |
| `ancient_city/city/entrance/entrance_connector` | A path, with a staircase at the end. It is enclosed by arches. There are three pillars on each side of the path. | 3 <br>Candle<br> (1 candle in one block)  <br>6 <br>Candle<br> (2 candles in one block)  <br>6 <br>Candle<br> (3 candles in one block)  <br>5 <br>Candle<br> (4 candles in one block)  <br>72 <br>Chiseled Deepslate<br>  <br>928 <br>Deepslate<br>  <br>21 <br>Deepslate Brick Slab<br>  <br>9 <br>Deepslate Brick Stairs<br>  <br>96 <br>Deepslate Bricks<br>  <br>299 <br>Deepslate Tile Stairs<br>  <br>595 <br>Deepslate Tiles<br>  <br>138 <br>Polished Basalt<br>  <br>416 <br>Polished Deepslate<br>  <br>12 <br>Polished Deepslate Wall<br>  <br>40 <br>Smooth Basalt<br>  <br>6 <br>Soul Lantern |  |
| `ancient_city/city/entrance/entrance_path_1` |     | 12 <br>Candle<br> (1 candle in one block)  <br>5 <br>Candle<br> (2 candles in one block)  <br>5 <br>Candle<br> (3 candles in one block)  <br>2 <br>Candle<br> (4 candles in one block)  <br>78 <br>Chiseled Deepslate<br>  <br>3 <br>Cobbled Deepslate Slab<br>  <br>899 <br>Deepslate<br>  <br>25 <br>Deepslate Brick Slab<br>  <br>6 <br>Deepslate Brick Stairs<br>  <br>41 <br>Deepslate Bricks<br>  <br>305 <br>Deepslate Tile Stairs<br>  <br>731 <br>Deepslate Tiles<br>  <br>138 <br>Polished Basalt<br>  <br>437 <br>Polished Deepslate<br>  <br>3 <br>Polished Deepslate Slab<br>  <br>14 <br>Polished Deepslate Wall<br>  <br>48 <br>Smooth Basalt<br>  <br>7 <br>Soul Lantern |  |
| `ancient_city/city/entrance/entrance_path_2` |     | 10 <br>Candle<br> (1 candle in one block)  <br>4 <br>Candle<br> (2 candles in one block)  <br>4 <br>Candle<br> (3 candles in one block)  <br>2 <br>Candle<br> (4 candles in one block)  <br>59 <br>Chiseled Deepslate<br>  <br>21 <br>Dark Oak Planks<br>  <br>810 <br>Deepslate<br>  <br>22 <br>Deepslate Brick Slab<br>  <br>6 <br>Deepslate Brick Stairs<br>  <br>39 <br>Deepslate Bricks<br>  <br>9 <br>Deepslate Tile Slab<br>  <br>254 <br>Deepslate Tile Stairs<br>  <br>2,357 <br>Deepslate Tiles<br>  <br>12 <br>Ladder<br>  <br>113 <br>Polished Basalt<br>  <br>348 <br>Polished Deepslate<br>  <br>15 <br>Polished Deepslate Wall<br>  <br>39 <br>Smooth Basalt<br>  <br>7 <br>Soul Lantern |  |
| `ancient_city/city/entrance/entrance_path_3` |     | 8 <br>Candle<br> (1 candle in one block)  <br>3 <br>Candle<br> (2 candles in one block)  <br>3 <br>Candle<br> (3 candles in one block)  <br>2 <br>Candle<br> (4 candles in one block)  <br>36 <br>Chiseled Deepslate<br>  <br>4 <br>Cobbled Deepslate<br>  <br>12 <br>Cobbled Deepslate Wall<br>  <br>21 <br>Dark Oak Fence<br>  <br>26 <br>Dark Oak Log<br>  <br>93 <br>Dark Oak Planks<br>  <br>566 <br>Deepslate<br>  <br>20 <br>Deepslate Brick Slab<br>  <br>4 <br>Deepslate Brick Stairs<br>  <br>50 <br>Deepslate Bricks<br>  <br>4 <br>Deepslate Tile Slab<br>  <br>153 <br>Deepslate Tile Stairs<br>  <br>2,163 <br>Deepslate Tiles<br>  <br>18 <br>Ladder<br>  <br>90 <br>Polished Basalt<br>  <br>210 <br>Polished Deepslate<br>  <br>8 <br>Polished Deepslate Wall<br>  <br>31 <br>Smooth Basalt<br>  <br>4 <br>Soul Lantern<br>  <br>4 <br>Torch |  |
| `ancient_city/city/entrance/entrance_path_4` |     | 8 <br>Candle<br> (1 candle in one block)  <br>3 <br>Candle<br> (2 candles in one block)  <br>3 <br>Candle<br> (3 candles in one block)  <br>2 <br>Candle<br> (4 candles in one block)  <br>28 <br>Chiseled Deepslate<br>  <br>2 <br>Cobbled Deepslate<br>  <br>9 <br>Cobbled Deepslate Wall<br>  <br>13 <br>Dark Oak Fence<br>  <br>20 <br>Dark Oak Log<br>  <br>81 <br>Dark Oak Planks<br>  <br>548 <br>Deepslate<br>  <br>20 <br>Deepslate Brick Slab<br>  <br>4 <br>Deepslate Brick Stairs<br>  <br>56 <br>Deepslate Bricks<br>  <br>3 <br>Deepslate Tile Slab<br>  <br>139 <br>Deepslate Tile Stairs<br>  <br>2,137 <br>Deepslate Tiles<br>  <br>18 <br>Ladder<br>  <br>4 <br>Light Blue Carpet<br>  <br>91 <br>Polished Basalt<br>  <br>184 <br>Polished Deepslate<br>  <br>4 <br>Polished Deepslate Wall<br>  <br>28 <br>Smooth Basalt<br>  <br>2 <br>Soul Lantern<br>  <br>1 <br>Torch |  |
| `ancient_city/city/entrance/entrance_path_5` |     | 5 <br>Candle<br> (1 candle in one block)  <br>3 <br>Candle<br> (2 candles in one block)  <br>1 <br>Candle<br> (3 candles in one block)  <br>2 <br>Candle<br> (4 candles in one block)  <br>13 <br>Chiseled Deepslate<br>  <br>3 <br>Dark Oak Log<br>  <br>18 <br>Dark Oak Planks<br>  <br>373 <br>Deepslate<br>  <br>13 <br>Deepslate Brick Slab<br>  <br>2 <br>Deepslate Brick Stairs<br>  <br>39 <br>Deepslate Bricks<br>  <br>2 <br>Deepslate Tile Slab<br>  <br>76 <br>Deepslate Tile Stairs<br>  <br>1,924 <br>Deepslate Tiles<br>  <br>12 <br>Ladder<br>  <br>3 <br>Light Blue Carpet<br>  <br>72 <br>Polished Basalt<br>  <br>93 <br>Polished Deepslate<br>  <br>19 <br>Smooth Basalt |  |

#### Center

Edit

| Structure name | Description | Consists of | Image |
| --- | --- | --- | --- |
| `ancient_city/city_center/city_center_1` |     | 2 <br>Block of Redstone<br>  <br>23 <br>Candle<br> (1 candle in one block)  <br>12 <br>Candle<br> (2 candles in one block)  <br>13 <br>Candle<br> (3 candles in one block)  <br>2 <br>Candle<br> (4 candles in one block)  <br>8 <br>Chiseled Deepslate<br>  <br>353 <br>Cobbled Deepslate<br>  <br>6 <br>Cracked Deepslate Bricks<br>  <br>4 <br>Cracked Deepslate Tiles<br>  <br>1,476 <br>Deepslate<br>  <br>29 <br>Deepslate Brick Slab<br>  <br>9 <br>Deepslate Brick Wall<br>  <br>3,346 <br>Deepslate Bricks<br>  <br>69 <br>Deepslate Tile Stairs<br>  <br>1,731 <br>Deepslate Tiles<br>  <br>4 <br>Dirt<br>  <br>1 <br>Glass<br>  <br>138 <br>Glass Pane<br>  <br>44 <br>Grass Block<br>  <br>221 <br>Gray Carpet<br>  <br>199 <br>Gray Wool<br>  <br>1 <br>Lectern<br>  <br>1 <br>Lever<br>  <br>4 <br>Piston Head<br>  <br>9 <br>Polished Deepslate Wall<br>  <br>8 <br>Redstone Comparator<br>  <br>20 <br>Redstone Lamp<br>  <br>8 <br>Redstone Repeater<br>  <br>1 <br>Redstone Wall Torch<br>  <br>41 <br>Redstone Dust<br>  <br>56 <br>Reinforced Deepslate<br>  <br>1 <br>Sculk Sensor<br> (Waterlogged)  <br>57 <br>Soul Fire<br>  <br>6 <br>Soul Lantern<br>  <br>57 <br>Soul Sand<br>  <br>5 <br>Sticky Piston<br>  <br>1 <br>Target |  |
| `ancient_city/city_center/city_center_2` |     | 2 <br>Block of Redstone<br>  <br>23 <br>Candle<br> (1 candle in one block)  <br>12 <br>Candle<br> (2 candles in one block)  <br>14 <br>Candle<br> (3 candles in one block)  <br>3 <br>Candle<br> (4 candles in one block)  <br>1 <br>Chest<br> (with  <br>1 <br>Golden Apple<br>)  <br>6 <br>Chiseled Deepslate<br>  <br>350 <br>Cobbled Deepslate<br>  <br>6 <br>Cracked Deepslate Bricks<br>  <br>4 <br>Cracked Deepslate Tiles<br>  <br>1,476 <br>Deepslate<br>  <br>40 <br>Deepslate Brick Slab<br>  <br>31 <br>Deepslate Brick Stairs<br>  <br>9 <br>Deepslate Brick Wall<br>  <br>3,232 <br>Deepslate Bricks<br>  <br>11 <br>Deepslate Tile Slab<br>  <br>74 <br>Deepslate Tile Stairs<br>  <br>1,751 <br>Deepslate Tiles<br>  <br>4 <br>Dirt<br>  <br>1 <br>Furnace<br> (with  <br>1 <br>Wooden Shovel<br>  <br>24 <br>Deepslate<br>  <br>and some <br>xp<br>)  <br>4 <br>Glass<br>  <br>136 <br>Glass Pane<br>  <br>44 <br>Grass Block<br>  <br>221 <br>Gray Carpet<br>  <br>158 <br>Gray Wool<br>  <br>1 <br>Lectern<br>  <br>4 <br>Piston Head<br>  <br>2 <br>Polished Deepslate Slab<br>  <br>9 <br>Polished Deepslate Wall<br>  <br>9 <br>Redstone Comparator<br>  <br>19 <br>Redstone Lamp<br>  <br>8 <br>Redstone Repeater<br>  <br>3 <br>Redstone Wall Torch<br>  <br>45 <br>Redstone Dust<br>  <br>56 <br>Reinforced Deepslate<br>  <br>1 <br>Sculk Sensor<br> (Waterlogged)  <br>57 <br>Soul Fire<br>  <br>6 <br>Soul Lantern<br>  <br>57 <br>Soul Sand<br>  <br>5 <br>Sticky Piston<br>  <br>1 <br>Target |  |
| `ancient_city/city_center/city_center_3` |     | 2 <br>Block of Redstone<br>  <br>23 <br>Candle<br> (1 candle in one block)  <br>12 <br>Candle<br> (2 candles in one block)  <br>13 <br>Candle<br> (3 candles in one block)  <br>2 <br>Candle<br> (4 candles in one block)  <br>7 <br>Chiseled Deepslate<br>  <br>350 <br>Cobbled Deepslate<br>  <br>6 <br>Cracked Deepslate Bricks<br>  <br>4 <br>Cracked Deepslate Tiles<br>  <br>1,476 <br>Deepslate<br>  <br>29 <br>Deepslate Brick Slab<br>  <br>9 <br>Deepslate Brick Wall<br>  <br>3,287 <br>Deepslate Bricks<br>  <br>65 <br>Deepslate Tile Stairs<br>  <br>10 <br>Deepslate Tile Wall<br>  <br>1,704 <br>Deepslate Tiles<br>  <br>4 <br>Dirt<br>  <br>2 <br>Glass<br>  <br>136 <br>Glass Pane<br>  <br>44 <br>Grass Block<br>  <br>221 <br>Gray Carpet<br>  <br>173 <br>Gray Wool<br>  <br>1 <br>Lectern<br>  <br>4 <br>Piston Head<br>  <br>2 <br>Polished Deepslate Slab<br>  <br>6 <br>Polished Deepslate Stairs<br>  <br>9 <br>Polished Deepslate Wall<br>  <br>2 <br>Redstone Comparator<br>  <br>4 <br>Redstone Lamp<br>  <br>13 <br>Redstone Repeater<br>  <br>4 <br>Redstone Torch<br>  <br>8 <br>Redstone Wall Torch<br>  <br>52 <br>Redstone Dust<br>  <br>56 <br>Reinforced Deepslate<br>  <br>1 <br>Sculk Sensor<br> (Waterlogged)  <br>57 <br>Soul Fire<br>  <br>6 <br>Soul Lantern<br>  <br>57 <br>Soul Sand<br>  <br>5 <br>Sticky Piston<br>  <br>1 <br>Target |  |
| `ancient_city/city_center/walls/bottom_1` |     | 117 <br>Chiseled Deepslate<br>  <br>9 <br>Cracked Deepslate Bricks<br>  <br>11 <br>Cracked Deepslate Tiles<br>  <br>12 <br>Dark Oak Fence<br>  <br>28 <br>Dark Oak Planks<br>  <br>281 <br>Deepslate<br>  <br>41 <br>Deepslate Brick Stairs<br>  <br>669 <br>Deepslate Bricks<br>  <br>4 <br>Deepslate Tile Slab<br>  <br>169 <br>Deepslate Tile Stairs<br>  <br>12 <br>Deepslate Tile Wall<br>  <br>52 <br>Deepslate Tiles<br>  <br>493 <br>Gray Wool<br>  <br>30 <br>Polished Deepslate<br>  <br>57 <br>Polished Deepslate Stairs<br>  <br>4 <br>Redstone Lamp<br>  <br>4 <br>Sculk Sensor<br> (Waterlogged) |  |
| `ancient_city/city_center/walls/bottom_2` |     | 87 <br>Chiseled Deepslate<br>  <br>9 <br>Cracked Deepslate Bricks<br>  <br>11 <br>Cracked Deepslate Tiles<br>  <br>10 <br>Dark Oak Fence<br>  <br>20 <br>Dark Oak Planks<br>  <br>260 <br>Deepslate<br>  <br>40 <br>Deepslate Brick Stairs<br>  <br>651 <br>Deepslate Bricks<br>  <br>2 <br>Deepslate Tile Slab<br>  <br>125 <br>Deepslate Tile Stairs<br>  <br>12 <br>Deepslate Tile Wall<br>  <br>52 <br>Deepslate Tiles<br>  <br>484 <br>Gray Wool<br>  <br>31 <br>Polished Deepslate<br>  <br>32 <br>Polished Deepslate Stairs<br>  <br>3 <br>Redstone Lamp<br>  <br>1 <br>Sculk Sensor<br>  <br>2 <br>Sculk Sensor<br> (Waterlogged) |  |
| `ancient_city/city_center/walls/bottom_left_corner` |     | 59 <br>Chiseled Deepslate<br>  <br>63 <br>Deepslate<br>  <br>15 <br>Deepslate Brick Stairs<br>  <br>198 <br>Deepslate Bricks<br>  <br>1 <br>Deepslate Tile Slab<br>  <br>109 <br>Deepslate Tile Stairs<br>  <br>8 <br>Deepslate Tile Wall<br>  <br>14 <br>Deepslate Tiles<br>  <br>172 <br>Gray Wool |  |
| `ancient_city/city_center/walls/bottom_right_corner` |     | 63 <br>Chiseled Deepslate<br>  <br>67 <br>Deepslate<br>  <br>15 <br>Deepslate Brick Stairs<br>  <br>223 <br>Deepslate Bricks<br>  <br>4 <br>Deepslate Tile Slab<br>  <br>142 <br>Deepslate Tile Stairs<br>  <br>8 <br>Deepslate Tile Wall<br>  <br>14 <br>Deepslate Tiles<br>  <br>247 <br>Gray Wool |  |
| `ancient_city/city_center/walls/bottom_right_corner_1` |     | 43 <br>Chiseled Deepslate<br>  <br>8 <br>Dark Oak Planks<br>  <br>36 <br>Deepslate<br>  <br>15 <br>Deepslate Brick Stairs<br>  <br>203 <br>Deepslate Bricks<br>  <br>4 <br>Deepslate Tile Slab<br>  <br>109 <br>Deepslate Tile Stairs<br>  <br>6 <br>Deepslate Tile Wall<br>  <br>20 <br>Deepslate Tiles<br>  <br>154 <br>Gray Wool<br>  <br>3 <br>Light Blue Carpet |  |
| `ancient_city/city_center/walls/bottom_right_corner_2` |     | 48 <br>Chiseled Deepslate<br>  <br>65 <br>Deepslate<br>  <br>15 <br>Deepslate Brick Stairs<br>  <br>195 <br>Deepslate Bricks<br>  <br>4 <br>Deepslate Tile Slab<br>  <br>121 <br>Deepslate Tile Stairs<br>  <br>6 <br>Deepslate Tile Wall<br>  <br>14 <br>Deepslate Tiles<br>  <br>172 <br>Gray Wool |  |
| `ancient_city/city_center/walls/left` |     | 41 <br>Chiseled Deepslate<br>  <br>1 <br>Cracked Deepslate Bricks<br>  <br>120 <br>Deepslate<br>  <br>16 <br>Deepslate Brick Stairs<br>  <br>302 <br>Deepslate Bricks<br>  <br>95 <br>Deepslate Tile Stairs<br>  <br>6 <br>Deepslate Tile Wall<br>  <br>7 <br>Deepslate Tiles<br>  <br>242 <br>Gray Wool<br>  <br>8 <br>Polished Deepslate Stairs |  |
| `ancient_city/city_center/walls/right` |     | 41 <br>Chiseled Deepslate<br>  <br>1 <br>Cracked Deepslate Bricks<br>  <br>130 <br>Deepslate<br>  <br>16 <br>Deepslate Brick Stairs<br>  <br>288 <br>Deepslate Bricks<br>  <br>98 <br>Deepslate Tile Stairs<br>  <br>6 <br>Deepslate Tile Wall<br>  <br>7 <br>Deepslate Tiles<br>  <br>246 <br>Gray Wool<br>  <br>8 <br>Polished Deepslate Stairs |  |
| `ancient_city/city_center/walls/top` |     | 96 <br>Chiseled Deepslate<br>  <br>362 <br>Deepslate<br>  <br>44 <br>Deepslate Brick Stairs<br>  <br>599 <br>Deepslate Bricks<br>  <br>270 <br>Deepslate Tile Stairs<br>  <br>10 <br>Deepslate Tile Wall<br>  <br>20 <br>Deepslate Tiles<br>  <br>566 <br>Gray Wool<br>  <br>7 <br>Polished Deepslate Stairs |  |
| `ancient_city/city_center/walls/top_left_corner` |     | 59 <br>Chiseled Deepslate<br>  <br>63 <br>Deepslate<br>  <br>15 <br>Deepslate Brick Stairs<br>  <br>198 <br>Deepslate Bricks<br>  <br>113 <br>Deepslate Tile Stairs<br>  <br>8 <br>Deepslate Tile Wall<br>  <br>14 <br>Deepslate Tiles<br>  <br>172 <br>Gray Wool |  |
| `ancient_city/city_center/walls/top_right_corner` |     | 55 <br>Chiseled Deepslate<br>  <br>72 <br>Deepslate<br>  <br>14 <br>Deepslate Brick Stairs<br>  <br>186 <br>Deepslate Bricks<br>  <br>1 <br>Deepslate Tile Slab<br>  <br>115 <br>Deepslate Tile Stairs<br>  <br>8 <br>Deepslate Tile Wall<br>  <br>14 <br>Deepslate Tiles<br>  <br>182 <br>Gray Wool |  |

#### Ruins

Edit

| Structure name | Description | Consists of | Image |
| --- | --- | --- | --- |
| `ancient_city/structures/barracks` | A large, relatively spacious area containing 2 chests, a statue resembling the warden's head (Built identically to small\_statue), and 2 shrine-like structures. | 2 <br>Chest<br> (with  <br>some loot: chests/ancient\_city)  <br>3 <br>Chiseled Deepslate<br>  <br>28 <br>Cobbled Deepslate<br>  <br>10 <br>Cobbled Deepslate Stairs<br>  <br>1,140 <br>Deepslate<br>  <br>7 <br>Deepslate Brick Slab<br>  <br>45 <br>Deepslate Brick Stairs<br>  <br>6 <br>Deepslate Bricks<br>  <br>2 <br>Deepslate Tile Slab<br>  <br>6 <br>Deepslate Tile Stairs<br>  <br>205 <br>Deepslate Tiles<br>  <br>95 <br>Polished Deepslate<br>  <br>2 <br>Polished Deepslate Wall<br>  <br>1 <br>Skeleton Skull<br>  <br>2 <br>Soul Fire<br>  <br>2 <br>Soul Sand<br>  <br>7 <br>White Candle<br> (1 white candle in one block)  <br>10 <br>White Candle<br> (2 white candles in one block)  <br>5 <br>White Candle<br> (3 white candles in one block) |  |
| `ancient_city/structures/camp_1` | A elevated platform with clumps of blue, cyan, and light blue wool on it with cobbled deepslate ruins scattered around it. | 32 <br>Blue Wool<br>  <br>117 <br>Cobbled Deepslate<br>  <br>38 <br>Cobbled Deepslate Stairs<br>  <br>19 <br>Cyan Wool<br>  <br>3 <br>Deepslate Bricks<br>  <br>25 <br>Light Blue Wool |  |
| `ancient_city/structures/camp_2` | A more intact version of camp\_1 with dark oak logs, a still-lit campfire, and a sort of table in the middle of the platform. | 13 <br>Blue Wool<br>  <br>1 <br>Campfire<br>  <br>117 <br>Cobbled Deepslate<br>  <br>44 <br>Cobbled Deepslate Stairs<br>  <br>13 <br>Cyan Wool<br>  <br>13 <br>Dark Oak Log<br>  <br>3 <br>Deepslate Bricks<br>  <br>16 <br>Light Blue Wool |  |
| `ancient_city/structures/camp_3` | An even more ruined version of camp\_1, with most of its wool missing. | 3 <br>Blue Carpet<br>  <br>13 <br>Blue Wool<br>  <br>1 <br>Campfire<br>  <br>102 <br>Cobbled Deepslate<br>  <br>27 <br>Cobbled Deepslate Stairs<br>  <br>3 <br>Deepslate Bricks<br>  <br>6 <br>Light Blue Wool |  |
| `ancient_city/structures/chamber_1` |     | 1 <br>Chest<br> (with  <br>some loots: chests/ancient\_city)  <br>10 <br>Chiseled Deepslate<br>  <br>390 <br>Cobbled Deepslate<br>  <br>2 <br>Deepslate<br>  <br>197 <br>Deepslate Bricks<br>  <br>64 <br>Deepslate Tiles |  |
| `ancient_city/structures/chamber_2` | A basic chamber made from deepslate materials with a chest at the back. | 1 <br>Chest<br> (with  <br>some loots: chests/ancient\_city)  <br>5 <br>Chiseled Deepslate<br>  <br>155 <br>Cobbled Deepslate<br>  <br>1 <br>Deepslate<br>  <br>78 <br>Deepslate Bricks<br>  <br>33 <br>Deepslate Tiles |  |
| `ancient_city/structures/chamber_3` | Another basic chamber with 2 larger pillars near its entrance and a chest in the back. | 1 <br>Chest<br> (with  <br>some loots: chests/ancient\_city)  <br>3 <br>Chiseled Deepslate<br>  <br>85 <br>Cobbled Deepslate<br>  <br>1 <br>Deepslate<br>  <br>75 <br>Deepslate Bricks<br>  <br>40 <br>Deepslate Tiles |  |
| `ancient_city/structures/ice_box_1` | A rarer structure consisting of a room with stairs leading up to it. At the entrance are stone pressure plates that activate note blocks.<br><br>Inside is a chest with loot from a loot table specific to the ice box structure, as well as ice blocks and top snow scattered around the inside of the room. | 12 <br>Blue Ice<br>  <br>1 <br>Chest<br> (with  <br>some loots: chests/ancient\_city\_ice\_box)  <br>10 <br>Chiseled Deepslate<br>  <br>384 <br>Cobbled Deepslate<br>  <br>21 <br>Cracked Deepslate Bricks<br>  <br>2 <br>Deepslate<br>  <br>2 <br>Deepslate Brick Stairs<br>  <br>228 <br>Deepslate Bricks<br>  <br>150 <br>Deepslate Tiles<br>  <br>8 <br>Gray Carpet<br>  <br>9 <br>Ice<br>  <br>1 <br>Iron Trapdoor<br>  <br>3 <br>Ladder<br>  <br>1 <br>Lever<br>  <br>2 <br>Note Block<br>  <br>62 <br>Packed Ice<br>  <br>4 <br>Polished Basalt<br>  <br>1 <br>Polished Deepslate Slab<br>  <br>8 <br>Polished Deepslate Stairs<br>  <br>30 <br>Snow<br>  <br>3 <br>Soul Lantern<br>  <br>3 <br>Stone Pressure Plate |  |
| `ancient_city/structures/large_pillar_1` | A large 4×4 pillar with patterns of chiselled and polished deepslate on its sides. | 16 <br>Chiseled Deepslate<br>  <br>135 <br>Deepslate<br>  <br>64 <br>Deepslate Tile Stairs<br>  <br>20 <br>Deepslate Tiles<br>  <br>84 <br>Polished Deepslate |  |
| `ancient_city/structures/large_ruin_1` | A large scattered square frame of small cobbled deepslate piles. | 35 <br>Cobbled Deepslate<br>  <br>3 <br>Deepslate Bricks |  |
| `ancient_city/structures/medium_pillar_1` | A 2x2 pillar with a skeleton skull on its base. | 4 <br>Chiseled Deepslate<br>  <br>87 <br>Deepslate<br>  <br>2 <br>Deepslate Tile Slab<br>  <br>20 <br>Deepslate Tile Stairs<br>  <br>4 <br>Deepslate Tiles<br>  <br>10 <br>Polished Deepslate<br>  <br>1 <br>Skeleton Skull |  |
| `ancient_city/structures/medium_ruin_1` | A medium scattered assortment of cobbled deepslate piles. | 19 <br>Cobbled Deepslate |  |
| `ancient_city/structures/medium_ruin_2` | A larger scattered assortment of cobbled deepslate piles. | 29 <br>Cobbled Deepslate |  |
| `ancient_city/structures/sauna_1` | A rare, larger structure featuring a pool and an elevated heated pool with 3 changing rooms, each containing a chest. | 3 <br>Chest<br> (with  <br>some loots: chests/ancient\_city)  <br>46 <br>Chiseled Deepslate<br>  <br>157 <br>Cracked Deepslate Bricks<br>  <br>23 <br>Cracked Deepslate Tiles<br>  <br>73 <br>Deepslate<br>  <br>1 <br>Deepslate Brick Slab<br>  <br>1,038 <br>Deepslate Bricks<br>  <br>3 <br>Deepslate Tile Slab<br>  <br>69 <br>Deepslate Tile Stairs<br>  <br>17 <br>Deepslate Tile Stairs<br> (Waterlogged)  <br>342 <br>Deepslate Tiles<br>  <br>43 <br>Water<br>  <br>10 <br>White Candle<br> (1 white candle in one block)  <br>14 <br>White Candle<br> (2 white candles in one block)  <br>4 <br>White Candle<br> (3 white candles in one block)  <br>3 <br>White Candle<br> (4 white candles in one block) |  |
| `ancient_city/structures/small_ruin_1` | Scattered piles of cobbled deepslate. | 16 <br>Cobbled Deepslate |  |
| `ancient_city/structures/small_ruin_2` | A few small piles of cobbled deepslate. | 16 <br>Cobbled Deepslate |  |
| `ancient_city/structures/small_statue` | A small, slightly elevated altar with a statue resembling the warden's head and white candles in front of it. | 22 <br>Cobbled Deepslate<br>  <br>32 <br>Deepslate<br>  <br>4 <br>Deepslate Brick Slab<br>  <br>2 <br>Deepslate Brick Stairs<br>  <br>2 <br>Deepslate Tile Slab<br>  <br>2 <br>Deepslate Tiles<br>  <br>44 <br>Polished Deepslate<br>  <br>2 <br>Polished Deepslate Wall<br>  <br>3 <br>White Candle<br> (1 white candle in one block)  <br>1 <br>White Candle<br> (4 white candles in one block) |  |
| `ancient_city/structures/tall_ruin_1` | A tower with multiple ladder entrances to the second floor, and a 4-sided ladder pillar to the third floor, which has a chest. | 1 <br>Chest<br> (with  <br>some loots: chests/ancient\_city)  <br>5 <br>Chiseled Deepslate<br>  <br>123 <br>Deepslate Bricks<br>  <br>84 <br>Deepslate Tile Slab<br>  <br>40 <br>Deepslate Tile Stairs<br>  <br>4 <br>Deepslate Tile Wall<br>  <br>199 <br>Deepslate Tiles<br>  <br>92 <br>Ladder<br>  <br>43 <br>Polished Basalt<br>  <br>40 <br>Polished Deepslate Wall<br>  <br>4 <br>Soul Lantern |  |
| `ancient_city/structures/tall_ruin_2` | A 2-story building containing 2 chests and a staircase to its second floor, lined with gray carpets. | 2 <br>Chest<br> (with  <br>some loots: chests/ancient\_city)  <br>336 <br>Deepslate Bricks<br>  <br>68 <br>Deepslate Tile Stairs<br>  <br>65 <br>Deepslate Tile Wall<br>  <br>371 <br>Deepslate Tiles<br>  <br>66 <br>Gray Carpet<br>  <br>7 <br>Soul Lantern |  |
| `ancient_city/structures/tall_ruin_3` | A heavily damaged version of tall\_ruin\_1. It still has a chest on its top floor. | 1 <br>Chest<br> (with  <br>some loots: chests/ancient\_city)  <br>87 <br>Deepslate Bricks<br>  <br>30 <br>Deepslate Tile Slab<br>  <br>33 <br>Deepslate Tile Stairs<br>  <br>2 <br>Deepslate Tile Wall<br>  <br>112 <br>Deepslate Tiles<br>  <br>1 <br>Ladder<br>  <br>21 <br>Polished Deepslate Wall |  |
| `ancient_city/structures/tall_ruin_4` | A heavily damaged version of tall\_ruin\_2, which is missing one chest. | 1 <br>Chest<br> (with  <br>some loots: chests/ancient\_city)  <br>230 <br>Deepslate Bricks<br>  <br>42 <br>Deepslate Tile Stairs<br>  <br>22 <br>Deepslate Tile Wall<br>  <br>225 <br>Deepslate Tiles<br>  <br>24 <br>Gray Carpet |  |

#### Walls

Edit

| Structure name | Description | Consists of | Image |
| --- | --- | --- | --- |
| `ancient_city/walls/intact_corner_wall_1` |     | 1 <br>Chiseled Deepslate<br>  <br>8 <br>Deepslate Brick Stairs<br>  <br>305 <br>Deepslate Bricks<br>  <br>157 <br>Deepslate Tiles<br>  <br>436 <br>Gray Wool<br>  <br>20 <br>Ladder<br>  <br>5 <br>Polished Basalt<br>  <br>89 <br>Polished Deepslate<br>  <br>9 <br>Soul Lantern |  |
| `ancient_city/walls/intact_horizontal_wall_1` |     | 20 <br>Chiseled Deepslate<br>  <br>183 <br>Deepslate Bricks<br>  <br>82 <br>Deepslate Tiles<br>  <br>216 <br>Gray Wool<br>  <br>24 <br>Polished Deepslate<br>  <br>2 <br>Soul Lantern |  |
| `ancient_city/walls/intact_horizontal_wall_2` |     | 18 <br>Chiseled Deepslate<br>  <br>182 <br>Deepslate Bricks<br>  <br>79 <br>Deepslate Tiles<br>  <br>216 <br>Gray Wool<br>  <br>24 <br>Polished Deepslate<br>  <br>2 <br>Soul Lantern |  |
| `ancient_city/walls/intact_horizontal_wall_bridge` |     | 2 <br>Chiseled Deepslate<br>  <br>20 <br>Deepslate Brick Stairs<br>  <br>319 <br>Deepslate Bricks<br>  <br>20 <br>Deepslate Tile Slab<br>  <br>28 <br>Deepslate Tile Stairs<br>  <br>127 <br>Deepslate Tiles<br>  <br>176 <br>Gray Wool<br>  <br>10 <br>Soul Lantern |  |
| `ancient_city/walls/intact_horizontal_wall_passage_1` |     | 13 <br>Chiseled Deepslate<br>  <br>4 <br>Deepslate Brick Stairs<br>  <br>161 <br>Deepslate Bricks<br>  <br>16 <br>Deepslate Tile Stairs<br>  <br>36 <br>Deepslate Tiles<br>  <br>112 <br>Gray Wool<br>  <br>11 <br>Polished Deepslate<br>  <br>4 <br>Soul Lantern |  |
| `ancient_city/walls/intact_horizontal_wall_stairs_1` |     | 16 <br>Chiseled Deepslate<br>  <br>166 <br>Deepslate Bricks<br>  <br>12 <br>Deepslate Tile Stairs<br>  <br>81 <br>Deepslate Tiles<br>  <br>226 <br>Gray Wool<br>  <br>27 <br>Polished Deepslate<br>  <br>3 <br>Soul Lantern |  |
| `ancient_city/walls/intact_horizontal_wall_stairs_2` |     | 20 <br>Chiseled Deepslate<br>  <br>3 <br>Deepslate Brick Stairs<br>  <br>177 <br>Deepslate Bricks<br>  <br>9 <br>Deepslate Tile Slab<br>  <br>3 <br>Deepslate Tile Stairs<br>  <br>79 <br>Deepslate Tiles<br>  <br>220 <br>Gray Wool<br>  <br>26 <br>Polished Deepslate<br>  <br>3 <br>Soul Lantern |  |
| `ancient_city/walls/intact_horizontal_wall_stairs_3` |     | 20 <br>Chiseled Deepslate<br>  <br>2 <br>Deepslate Brick Stairs<br>  <br>171 <br>Deepslate Bricks<br>  <br>11 <br>Deepslate Tile Slab<br>  <br>3 <br>Deepslate Tile Stairs<br>  <br>81 <br>Deepslate Tiles<br>  <br>220 <br>Gray Wool<br>  <br>22 <br>Polished Deepslate<br>  <br>5 <br>Soul Lantern |  |
| `ancient_city/walls/intact_horizontal_wall_stairs_4` |     | 12 <br>Chiseled Deepslate<br>  <br>3 <br>Deepslate Brick Stairs<br>  <br>4 <br>Deepslate Brick Wall<br>  <br>100 <br>Deepslate Bricks<br>  <br>9 <br>Deepslate Tile Slab<br>  <br>25 <br>Deepslate Tile Stairs<br>  <br>80 <br>Deepslate Tiles<br>  <br>182 <br>Gray Wool<br>  <br>20 <br>Polished Deepslate<br>  <br>6 <br>Soul Lantern |  |
| `ancient_city/walls/intact_intersection_wall_1` |     | 1 <br>Chiseled Deepslate<br>  <br>3 <br>Deepslate Brick Stairs<br>  <br>264 <br>Deepslate Bricks<br>  <br>127 <br>Deepslate Tiles<br>  <br>331 <br>Gray Wool<br>  <br>15 <br>Ladder<br>  <br>5 <br>Polished Basalt<br>  <br>80 <br>Polished Deepslate<br>  <br>8 <br>Soul Lantern |  |
| `ancient_city/walls/intact_lshape_wall_1` |     | 1 <br>Chiseled Deepslate<br>  <br>189 <br>Deepslate Bricks<br>  <br>91 <br>Deepslate Tiles<br>  <br>246 <br>Gray Wool<br>  <br>10 <br>Ladder<br>  <br>5 <br>Polished Basalt<br>  <br>59 <br>Polished Deepslate<br>  <br>5 <br>Soul Lantern |  |
| `ancient_city/walls/ruined_corner_wall_1` |     | 4 <br>Cobbled Deepslate<br>  <br>12 <br>Cobbled Deepslate Wall<br>  <br>16 <br>Dark Oak Fence<br>  <br>12 <br>Dark Oak Log<br>  <br>97 <br>Dark Oak Planks<br>  <br>297 <br>Deepslate Bricks<br>  <br>149 <br>Deepslate Tiles<br>  <br>360 <br>Gray Wool<br>  <br>5 <br>Ladder<br>  <br>20 <br>Light Blue Carpet<br>  <br>20 <br>Light Blue Wool<br>  <br>5 <br>Polished Basalt<br>  <br>81 <br>Polished Deepslate<br>  <br>8 <br>Soul Lantern<br>  <br>4 <br>Torch |  |
| `ancient_city/walls/ruined_corner_wall_2` |     | 1 <br>Cobbled Deepslate<br>  <br>2 <br>Dark Oak Log<br>  <br>37 <br>Dark Oak Planks<br>  <br>250 <br>Deepslate Bricks<br>  <br>146 <br>Deepslate Tiles<br>  <br>344 <br>Gray Wool<br>  <br>15 <br>Light Blue Carpet<br>  <br>20 <br>Light Blue Wool<br>  <br>59 <br>Polished Deepslate<br>  <br>7 <br>Soul Lantern |  |
| `ancient_city/walls/ruined_horizontal_wall_stairs_1` |     | 7 <br>Chiseled Deepslate<br>  <br>83 <br>Deepslate Bricks<br>  <br>3 <br>Deepslate Tile Stairs<br>  <br>67 <br>Deepslate Tiles<br>  <br>142 <br>Gray Wool<br>  <br>14 <br>Polished Deepslate<br>  <br>1 <br>Soul Lantern |  |
| `ancient_city/walls/ruined_horizontal_wall_stairs_2` |     | 10 <br>Blue Carpet<br>  <br>12 <br>Chiseled Deepslate<br>  <br>3 <br>Dark Oak Log<br>  <br>10 <br>Dark Oak Planks<br>  <br>3 <br>Deepslate Brick Stairs<br>  <br>111 <br>Deepslate Bricks<br>  <br>29 <br>Deepslate Tile Slab<br>  <br>3 <br>Deepslate Tile Stairs<br>  <br>72 <br>Deepslate Tiles<br>  <br>177 <br>Gray Wool<br>  <br>16 <br>Polished Deepslate<br>  <br>1 <br>Soul Lantern |  |
| `ancient_city/walls/ruined_horizontal_wall_stairs_3` |     | 7 <br>Chiseled Deepslate<br>  <br>10 <br>Cyan Carpet<br>  <br>10 <br>Dark Oak Planks<br>  <br>61 <br>Deepslate Bricks<br>  <br>3 <br>Deepslate Tile Slab<br>  <br>1 <br>Deepslate Tile Stairs<br>  <br>52 <br>Deepslate Tiles<br>  <br>102 <br>Gray Wool<br>  <br>11 <br>Polished Deepslate<br>  <br>3 <br>Soul Lantern |  |
| `ancient_city/walls/ruined_horizontal_wall_stairs_4` |     | 2 <br>Chiseled Deepslate<br>  <br>15 <br>Deepslate Brick Slab<br>  <br>6 <br>Deepslate Brick Stairs<br>  <br>61 <br>Deepslate Bricks<br>  <br>112 <br>Deepslate Tile Slab<br>  <br>28 <br>Deepslate Tiles<br>  <br>30 <br>Gray Wool<br>  <br>6 <br>Polished Deepslate |  |

### Blocks

Edit

|     |     |
| --- | --- |
| Main components | *   <br>    Deepslate<br>    <br>*   <br>    Cobbled Deepslate<br>    <br>*   <br>    Polished Deepslate<br>    <br>*   <br>    Deepslate Bricks<br>    <br>*   <br>    Deepslate Tiles<br>    <br>*   <br>    Chiseled Deepslate<br>    <br>*   <br>    Cracked Deepslate Bricks<br>    <br>*   <br>    Cracked Deepslate Tiles<br>    <br>*   <br>    Cobbled Deepslate Slab<br>    <br>*   <br>    Cobbled Deepslate Stairs<br>    <br>*   <br>    Cobbled Deepslate Wall<br>    <br>*   <br>    Polished Deepslate Slab<br>    <br>*   <br>    Polished Deepslate Stairs<br>    <br>*   <br>    Polished Deepslate Wall<br>    <br>*   <br>    Deepslate Brick Slab<br>    <br>*   <br>    Deepslate Brick Stairs<br>    <br>*   <br>    Deepslate Brick Wall<br>    <br>*   <br>    Deepslate Tile Slab<br>    <br>*   <br>    Deepslate Tile Stairs<br>    <br>*   <br>    Deepslate Tile Wall<br>    <br>*   <br>    Gray Wool<br>    <br>*   <br>    Polished Basalt<br>    <br>*   <br>    Smooth Basalt<br>    <br>*   <br>    Dark Oak Log<br>    <br>*   <br>    Dark Oak Planks<br>    <br>*   <br>    Dark Oak Fence<br>    <br>*   <br>    Ladder<br>    <br>*   <br>    Candle<br>    <br>*   <br>    Soul Lantern<br>    <br>*   <br>    Torch<br>    <br>*   <br>    Chest |
| Blocks unique to some structures | *   <br>    Reinforced Deepslate<br>    <br>*   <br>    Sculk Sensor<br>    <br>*   <br>    Redstone Lamp<br>    <br>*   <br>    Soul Fire<br>    <br>*   <br>    Soul Sand<br>    <br>*   <br>    Redstone Dust<br>    <br>*   <br>    Redstone Comparator<br>    <br>*   <br>    Redstone Repeater<br>    <br>*   <br>    Redstone Torch<br>    <br>*   <br>    Redstone Wall Torch<br>    <br>*   <br>    Block of Redstone<br>    <br>*   <br>    Lectern<br>    <br>*   <br>    Target<br>    <br>*   <br>    Furnace<br>    <br>*   <br>    Lever<br>    <br>*   <br>    Sticky Piston<br>    <br>*   <br>    Piston Head<br>    <br>*   <br>    Gray Carpet<br>    <br>*   <br>    Glass<br>    <br>*   <br>    Glass Pane<br>    <br>*   <br>    Dirt<br>    <br>*   <br>    Grass Block<br>    <br>*   <br>    Blue Wool<br>    <br>*   <br>    Cyan Wool<br>    <br>*   <br>    Light Blue Wool<br>    <br>*   <br>    Campfire<br>    <br>*   <br>    Iron Trapdoor<br>    <br>*   <br>    Ice<br>    <br>*   <br>    Packed Ice<br>    <br>*   <br>    Blue Ice<br>    <br>*   <br>    Snow<br>    <br>*   <br>    Note Block<br>    <br>*   <br>    Stone Pressure Plate<br>    <br>*   <br>    Skeleton Skull<br>    <br>*   <br>    White Candle<br>    <br>*   <br>    Water<br>    <br>*   <br>    Blue Carpet<br>    <br>*   <br>    Cyan Carpet<br>    <br>*   <br>    Light Blue Carpet |

Loot
----

Edit

See also: Chest loot

The loot in the chests includes several useful items for exploring the ancient city, such as snowballs and the Swift Sneak enchantment, as well as items that cannot be found anywhere else, such as echo shards and two exclusive armor trims. Ancient city chests also have the second highest chance of any loot table to contain an enchanted golden apple
—ominous vaults have a 22.5% chance.

### Normal

Edit

In  and , each ancient city chest contains items drawn from 2 pools, with the following distribution:

| Item | Stack Size [\[A\]](https://minecraft.wiki/w/Ancient_City#cite_note-stacksize-2) |     | Weight [\[B\]](https://minecraft.wiki/w/Ancient_City#cite_note-weight-3) |     | Chance [\[C\]](https://minecraft.wiki/w/Ancient_City#cite_note-chance-4) | Avg.  <br>per chest [\[D\]](https://minecraft.wiki/w/Ancient_City#cite_note-items-5) | hide Avg. # chests  <br>to search [\[E\]](https://minecraft.wiki/w/Ancient_City#cite_note-chests-6) |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 5–10× | 1×  | 5–10× | 1×  |
| --- | --- | --- | --- | --- | --- | --- | --- |
| Nothing[\[F\]](https://minecraft.wiki/w/Ancient_City#cite_note-nothing-7) | —   | 1   | —   | 75⁄80 | 93.8% | 0.938 | 1.1 |
| <br>Coal | 6–15 | —   | 7⁄84 | —   | 47.4% | 6.562 | 2.1 |
| <br>Bone | 1–15 | —   | 5⁄84 | —   | 36.5% | 3.571 | 2.7 |
| <br>Soul Torch | 1–15 | —   | 5⁄84 | —   | 36.5% | 3.571 | 2.7 |
| <br>Book | 3–10 | —   | 5⁄84 | —   | 36.5% | 2.902 | 2.7 |
| <br>Potion of Regeneration | 1–3 | —   | 5⁄84 | —   | 36.5% | 0.893 | 2.7 |
| <br>Enchanted Book<br>[\[G\]](https://minecraft.wiki/w/Ancient_City#cite_note-enchant-randomly-8) | 1   | —   | 5⁄84 | —   | 36.5% | 0.446 | 2.7 |
| <br>Disc Fragment (5) | 1–3 | —   | 4⁄84 | —   | 30.4% | 0.714 | 3.3 |
| <br>Echo Shard | 1–3 | —   | 4⁄84 | —   | 30.4% | 0.714 | 3.3 |
| <br>Amethyst Shard | 1–15 | —   | 3⁄84 | —   | 23.7% | 2.143 | 4.2 |
| <br>Glow Berries | 1–15 | —   | 3⁄84 | —   | 23.7% | 2.143 | 4.2 |
| <br>Sculk | 4–10 | —   | 3⁄84 | —   | 23.7% | 1.875 | 4.2 |
| <br>Candle | 1–4 | —   | 3⁄84 | —   | 23.7% | 0.670 | 4.2 |
| <br>Bottle o' Enchanting | 1–3 | —   | 3⁄84 | —   | 23.7% | 0.536 | 4.2 |
| <br>Sculk Sensor | 1–3 | —   | 3⁄84 | —   | 23.7% | 0.536 | 4.2 |
| <br>Enchanted Book<br>[\[H\]](https://minecraft.wiki/w/Ancient_City#cite_note-enchant-randomly-swift-sneak-9) | 1   | —   | 3⁄84 | —   | 23.7% | 0.268 | 4.2 |
| <br>Enchanted Iron Leggings<br>[\[I\]](https://minecraft.wiki/w/Ancient_City#cite_note-enchant-with-levels-20-39-10) | 1   | —   | 3⁄84 | —   | 23.7% | 0.268 | 4.2 |
| <br>Leather | 1–5 | —   | 2⁄84 | —   | 16.5% | 0.536 | 6.1 |
| <br>Sculk Catalyst | 1–2 | —   | 2⁄84 | —   | 16.5% | 0.268 | 6.1 |
| <br>Compass | 1   | —   | 2⁄84 | —   | 16.5% | 0.179 | 6.1 |
| <br>Music Disc (13) | 1   | —   | 2⁄84 | —   | 16.5% | 0.179 | 6.1 |
| <br>Music Disc (cat) | 1   | —   | 2⁄84 | —   | 16.5% | 0.179 | 6.1 |
| <br>Lead | 1   | —   | 2⁄84 | —   | 16.5% | 0.179 | 6.1 |
| <br>Damaged Enchanted Diamond Hoe<br>[\[J\]](https://minecraft.wiki/w/Ancient_City#cite_note-enchant-with-levels-30-50-11)<br>[\[K\]](https://minecraft.wiki/w/Ancient_City#cite_note-damaged-0.8-1.0-12) | 1   | —   | 2⁄84 | —   | 16.5% | 0.179 | 6.1 |
| <br>Diamond Horse Armor | 1   | —   | 2⁄84 | —   | 16.5% | 0.179 | 6.1 |
| <br>Enchanted Diamond Leggings<br>[\[J\]](https://minecraft.wiki/w/Ancient_City#cite_note-enchant-with-levels-30-50-11) | 1   | —   | 2⁄84 | —   | 16.5% | 0.179 | 6.1 |
| <br>Enchanted Golden Apple | 1–2 | —   | 1⁄84 | —   | 8.6% | 0.134 | 11.7 |
| <br>Music Disc (otherside) | 1   | —   | 1⁄84 | —   | 8.6% | 0.089 | 11.7 |
| <br>Ward Armor Trim Smithing Template | —   | 1   | —   | 4⁄80 | 5.0% | 0.050 | 20.0 |
| <br>Silence Armor Trim Smithing Template | —   | 1   | —   | 1⁄80 | 1.2% | 0.013 | 80.0 |

1.     The size of stacks (or for unstackable items, number) of this item on any given roll.
2.     The weight of this item relative to other items in the pool.
3.     The odds of finding any of this item in a single chest.
4.     The number of items expected per chest, averaged over a large number of chests.
5.     The average number of chests the player should expect to search to find any of this item.
6.     'Nothing' does not refer to the chance of an empty chest. Instead, it refers to the chance that the random loot generator does not add any loot _on a single roll_.
7.     All enchantments are equally probable, _including_ treasure enchantments
     (except Soul Speed
    , Swift Sneak
    , and Wind Burst
    ), and any level of the enchantment is equally probable.
8.     Enchanted with a random level of Swift Sneak
    .
9.     Enchantment probabilities are the same as a level-20 to level-39 enchantment would be on an enchanting table that had no cap at level 30, and that was able to apply treasure enchantments
     (except Soul Speed
    , Swift Sneak
    , and Wind Burst
    ), and where the chance of multiple enchantments is not reduced.
     Enchantment probabilities are the same as a level-30 to level-50 enchantment would be on an enchanting table that had no cap at level 30, and that was able to apply treasure enchantments
     (except Soul Speed
    , Swift Sneak
    , and Wind Burst
    ), and where the chance of multiple enchantments is not reduced.
11.     The item has between 80% and 100% of its total durability.

### Ice boxes

Edit

In  and , each ancient city ice box chest contains 4–10 item stacks, with the following distribution:

| Item | Stack Size [\[A\]](https://minecraft.wiki/w/Ancient_City#cite_note-stacksize-13) | Weight [\[B\]](https://minecraft.wiki/w/Ancient_City#cite_note-weight-14) | Chance [\[C\]](https://minecraft.wiki/w/Ancient_City#cite_note-chance-15) | Avg.  <br>per chest [\[D\]](https://minecraft.wiki/w/Ancient_City#cite_note-items-16) | hide Avg. # chests  <br>to search [\[E\]](https://minecraft.wiki/w/Ancient_City#cite_note-chests-17) |
| --- | --- | --- | --- | --- | --- |
| <br>Snowball | 2–6 | 4⁄9 | 97.0% | 12.444 | 1.0 |
| <br>Packed Ice | 2–6 | 2⁄9 | 80.5% | 6.222 | 1.2 |
| <br>Baked Potato | 1–10 | 1⁄9 | 54.9% | 4.278 | 1.8 |
| <br>Golden Carrot | 1–10 | 1⁄9 | 54.9% | 4.278 | 1.8 |
| <br>Suspicious Stew<br>[\[F\]](https://minecraft.wiki/w/Ancient_City#cite_note-suspicious-stew-2-18) | 2–6 | 1⁄9 | 54.9% | 3.111 | 1.8 |

1.     The size of stacks (or for unstackable items, number) of this item on any given roll.
2.     The weight of this item relative to other items in the pool.
3.     The odds of finding any of this item in a single chest.
4.     The number of items expected per chest, averaged over a large number of chests.
5.     The average number of chests the player should expect to search to find any of this item.
6.     The stew grants one of the following effects: in     , 5-7 seconds of Blindness
    , or 7-10 seconds of Night Vision
    ; in     , 7 seconds of Blindness
    , or 5 seconds of Night Vision
    .

See also
--------

Edit

*   
    Warden
    
*   
    Stronghold
    
*   
    Music Disc
