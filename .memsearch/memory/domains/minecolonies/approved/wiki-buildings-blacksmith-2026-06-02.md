---
title: MineColonies building — Blacksmith's Hut (workers: blacksmith)
domain: minecolonies
fact: The Blacksmith is responsible for creating tools and armor of any material for your colonists.
confidence: 0.80
usefulness: medium
authority: wiki-derived
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: 398649de11ec893e2dc2e6f3405c67c683b19c9fbb1bfb448ace2e6e9b2a4654
validated_at: 2026-06-02T23:24:14.465563+00:00
approval_route: user-review
user_approved: true
---

The Blacksmith's Hut is a 3x3 crafter and can make any vanilla tools, armor, swords, and shields (no bows or redstone items). The Blacksmith's Hut will work when they receive a request for any of those items from another worker. 

> **Note:** You will need to teach the Blacksmith's Hut the recipes of the items you want them to create. The number of items the Blacksmith's Hut can learn are listed below:

| Building Level | Number of Recipes |
| -------------- | ----------------- |
| 1              | 10                |
| 2              | 20                |
| 3              | 40                |
| 4              | 80                |
| 5              | 160               |

Additionally, upon reaching level 5, the Blacksmith's Hut learns the nine netherite recipes (shovel, hoe, pickaxe, axe, sword, helmet, chestplate, leggings, and boots), which count toward the recipe total above.

When a colonist is requesting a tool from the Blacksmith's Hut with multiple accepted levels, the blacksmith will craft whichever tool type is highest in their list of recipes that they have the materials for (when you teach them a new recipe, it'll go on the bottom).

## Interface

- **Recipe Mode:** This is unlocked by researching minecolonies/technology/warehousemaster in the university. This changes how multiple recipes for the same item are prioritized.
  - **Priority:** This is the default setting. The hut will try to use recipes that are higher up in their recipe list first.
  - **Warehouse Stock:** The hut will look in the warehouse first to see what resource you have more of before deciding what recipe it will use.

**Source:** [[docs/wiki-ref/buildings/blacksmith.mdoc]]
