---
title: MineColonies building — Plantation (workers: planter)
domain: minecolonies
fact: The Plantation is the place to go for any plants or non-farmland food that your colony may need.
confidence: 0.80
usefulness: medium
authority: wiki-derived
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: 0275283478c800d7469c721e95797e43c69d918bdfbe14974f71dafc81d277f4
validated_at: 2026-06-02T23:24:14.579062+00:00
approval_route: user-review
user_approved: true
---

The Plantation is where the plantation will grow either sugar cane, bamboo, or cactus.

The Plantation its level determines how many crops the plantation can plant at a time.

| Building Level | Crops Grown |
| -------------- | ----------- |
| 1              | 4           |
| 2              | 8           |
| 3              | 12          |
| 4              | 16          |
| 5              | 20          |

The Plantation is where the plantation will grow a variety of plants:

- [Sugar Cane](https://minecraft.wiki/w/Sugar_Cane)
- [Cactus](https://minecraft.wiki/w/Cactus)
- [Bamboo](https://minecraft.wiki/w/Bamboo)
- [Cocoa Beans](https://minecraft.wiki/w/Cocoa_Beans)
- [Vine](https://minecraft.wiki/w/Vine)
- [Kelp](https://minecraft.wiki/w/Kelp)
- [Seagrass](https://minecraft.wiki/w/Seagrass)
- [Sea Pickles](https://minecraft.wiki/w/Sea_Pickle)
- [Glow Berries](https://minecraft.wiki/w/Glow_Berries)
- [Weeping Vines](https://minecraft.wiki/w/Weeping_Vines)
- [Twisting Vines](https://minecraft.wiki/w/Twisting_Vines)
- Crimson Plants ([Crimson Roots](https://minecraft.wiki/w/Roots) and [Crimson Fungus](https://minecraft.wiki/w/Fungus))
- Warped Plants ([Warped Roots](https://minecraft.wiki/w/Roots) and [Warped Fungus](https://minecraft.wiki/w/Fungus))

Each plant is grown on fields, which can be schematics (part of your style pack) that the builder can construct.
However, these fields have different requirements as outlined on the [[schematics.mdoc]] page.

> These do **not** work like farmer fields where you only have to place a scarecrow down!

The Plantation has a limit of fields, based on it's building level, as well as one accompanying research.

| Building Level | Number of Fields | Number of Fields with "Crop Rotation" Research |
| -------------- | ---------------- | ---------------------------------------------- |
| 1              | 1                | 2                                              |
| 2              | 1                | 2                                              |
| 3              | 2                | 3                                              |
| 4              | 2                | 3                                              |
| 5              | 3                | 4                                              |

The Plantation is also limited by the amount of concurrent plants it can work on, so if you were to have a field of Sugar Cane and Cactus, those are two different plants.
Unlike the field limit, this one does not increase by the research, meaning that - with the research unlocked - you will not be able to have four different kinds of fields.

| Building Level | Number of Concurrent Plants |
| -------------- | --------------------------- |
| 1              | 1                           |
| 2              | 1                           |
| 3              | 2                           |
| 4              | 2                           |
| 5              | 3                           |

The plantation can also craft paper, books, sugar, and anything made with bamboo. The plantation will only make these items when they have been taught the recipes, receive a request for an item, and have the needed materials.

> **Note:** The plantation can only learn a certain amount of recipes per their hut level. 

| Building Level | Number of Recipes |
| -------------- | ----------------- |
| 1              | 10                |
| 2              | 20                |
| 3              | 40                |
| 4              | 80                |
| 5              | 160               |

## Interface

- **Recipe Mode:** This is unlocked by researching minecolonies/technology/warehousemaster in the university. This changes how multiple recipes for the same item are prioritized.
  - **Priority:** This is the default setting. The hut will try to use recipes that are higher up in their recipe list first.
  - **Warehouse Stock:** The hut will look in the warehouse first to see what resource you have more of before deciding what recipe it will use.

**Source:** [[docs/wiki-ref/buildings/plantation.mdoc]]
