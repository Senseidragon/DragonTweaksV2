---
title: MineColonies building — Crusher's Hut (workers: crusher)
domain: minecolonies
fact: The Crusher will crush certain blocks into other blocks, like cobblestone into gravel, allowing your colony access to new materials.
confidence: 0.80
usefulness: medium
authority: wiki-derived
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: 35f4f5e87bd2812f157332f2098d1251cd8bac34637824ef01e8b0dd9a027aa6
validated_at: 2026-06-02T23:24:14.497510+00:00
approval_route: user-review
user_approved: true
---

The Crusher's Hut is where the crusher will take items and crush them into other blocks. The defaults are: 

| Starting Item | Created Item                 |
| ------------- | ---------------------------- |
| Bone          | Bonemeal                     |
| Bone Block    | Bonemeal                     |
| Cobblestone   | Gravel (chance to get flint) |
| Clay          | Clay Ball                    |
| Gravel        | Sand                         |
| Sand          | Clay                         |

> **Note:** By default the crusher their ratio is 2:1, but by unlocking the minecolonies/technology/gildedhammer research in the university you can make them work on a 1:1 ratio.

The higher the level of the Crusher's Hut, the more daily output the crusher can handle. So:

| Building Level | Daily Max |
| -------------- | --------- |
| 1              | 16        |
| 2              | 64        |
| 3              | 144       |
| 4              | 256       |
| 5              | 999       |

The crusher works similarly to a crafter, producing items on request. You can optionally configure a daily output limit, which defaults to 0 (unlimited).

## Interface

- **Recipe Mode:** This is unlocked by researching minecolonies/technology/warehousemaster in the university. This changes how multiple recipes for the same item are prioritized.
  - **Priority:** This is the default setting. The hut will try to use recipes that are higher up in their recipe list first.
  - **Warehouse Stock:** The hut will look in the warehouse first to see what resource you have more of before deciding what recipe it will use.
- **Item:** Here you can set what item you want the crusher to make. (See the above lists.)
- **Daily Limit:** This setting allows you to further limit the number of blocks that can be crushed in a day. The maximum value is set by the hut level (see above).
- **Daily Limit:** This setting allows you to set an optional daily output limit. Defaults to 0 (unlimited).

**Source:** [[docs/wiki-ref/buildings/crusher.mdoc]]
