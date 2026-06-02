---
title: MineColonies building — Bakery (workers: baker)
domain: minecolonies
fact: The Bakery will make several products like bread, cookies, pastries, pies and so forth.
confidence: 0.80
usefulness: medium
authority: wiki-derived
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: db818f936d9ce39d5435fea2b9957c4648e7768ce9992cd39ffe6c2025e2b62a
validated_at: 2026-06-02T23:24:14.449308+00:00
approval_route: user-review
user_approved: true
---

The Bakery will craft bread dough, cookie dough, cake dough, and raw pumpkin pie, then bake these in a furnace to create bread, cookies, cakes, and pumpkin pies. They will only do this upon request, whether from the cook, the minecolonies/blockpostbox, or as a minimum stock in the warehouse.

The baker can also craft some non-vanilla breads:

- Sweet bread, made from wheat and a honey bottle. Available at Bakery level 3. Has slightly higher saturation than normal bread, also gives you a speed boost and removes poison.
- Milk-infused bread, made from wheat and a milk bucket. Available at Bakery level 4. Removes all potion effects (like milk buckets do).
- Golden bread, made from wheat and a gold ingot. Available at Bakery level 5. Instantly heals 2 hearts.
- Chorus bread, made from wheat and a chorus fruit. Available after completing the Know the End research in the university. Has higher saturation than normal bread and teleports you to the surface after eating it.

## Interface

- **Recipe Mode:** This is unlocked by researching minecolonies/technology/warehousemaster in the university. This changes how multiple recipes for the same item are prioritized.
  - **Priority:** This is the default setting. The hut will try to use recipes that are higher up in their recipe list first.
  - **Warehouse Stock:** The hut will look in the warehouse first to see what resource you have more of before deciding what recipe it will use.

**Source:** [[docs/wiki-ref/buildings/baker.mdoc]]
