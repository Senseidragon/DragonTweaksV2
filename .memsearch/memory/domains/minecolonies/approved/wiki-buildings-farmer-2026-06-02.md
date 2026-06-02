---
title: MineColonies building — Farmer's Hut (workers: farmer)
domain: minecolonies
fact: The Farm is one of the main sources of food production for your colony, farms are able to grow any root crop that has to be placed on farmland.
confidence: 0.80
usefulness: medium
authority: wiki-derived
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: 6f84084cf55eb21d0d9c448e2ba5fd898ba044bfa95b79f3b8adc5ad717d6680
validated_at: 2026-06-02T23:24:14.513599+00:00
approval_route: user-review
user_approved: true
---

The Farmer's Hut is where the farmer will grow crops for your colony. The crops the farmer currently cultivates are wheat, carrots, potatoes, beets, melons, pumpkins, and most crops from other mods (as long as they have normal growth behavior). Before the farmer can start, you will have to give the farmer a hoe, an axe (for harvesting crops), and the crop you want to cultivate.

**Note:** The farmer requires the day/night cycle to be enabled to work. Each day, the farmer will only perform one action per field: hoe, plant, or harvest.

The farmer will also craft seeds, carved pumpkins, hay bales and coarse dirt. They will only make items when they have been taught the recipes, receive a request for an item, and have the needed materials.

> **Note:** The farmer can only learn a set number of recipes based on their hut level. So:

| Building Level | Recipes |
| -------------- | ------- |
| 1              | 10      |
| 2              | 20      |
| 3              | 40      |
| 4              | 80      |
| 5              | 160     |

For the farmer to start, you will also need to place fields. Place the Field block (it looks like a scarecrow) in the plot of farmland you want the farmer to work on and right-click on it to access its GUI. Here you will place the seed of the crop you want this specific field to cultivate. (For potatoes, carrots, and other plants without seeds, just put the raw potato/carrot/etc in the Field.) If you decide later to change the type of crop you want cultivated in that farmland, just go into the Field's GUI and switch the seed there.

You can click on the arrows to increase the size of the area the farmer will farm. (Right-clicking will decrease the area.) The max size is 5 blocks in each direction from the Field block, or 11x11 total.

**IMPORTANT:** The farmer will farm up to five fields, depending on the level of the building. The level of the building is the number of Fields the farmer can cultivate:

| Building Level | Fields |
| -------------- | ------ |
| 1              | 1      |
| 2              | 2      |
| 3              | 3      |
| 4              | 4      |
| 5              | 5      |

## Minecolonies Crops

Minecolonies adds crops that can only be grown by a farmer and not by players. Those crops are needed to make high-quality food for your citizens. For a full list of crops and their required biomes, see the [[food#crops.mdoc]] page.

## Interface

- **Request Fertilizer:** On by default. This lets you choose whether the farmer will request fertilizer.
- **Recipe Mode:** This is unlocked by researching minecolonies/technology/warehousemaster in the university. This changes how multiple recipes for the same item are prioritized.
  - **Priority:** This is the default setting. The hut will try to use recipes that are higher up in their recipe list first.
  - **Warehouse Stock:** The hut will look in the warehouse first to see what resource you have more of before deciding what recipe it will use.

**Source:** [[docs/wiki-ref/buildings/farmer.mdoc]]
