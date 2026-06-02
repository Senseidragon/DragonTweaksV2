---
title: MineColonies building — Forester's Hut (workers: forester)
domain: minecolonies
fact: The Forester will cut down trees in your colony, you can tell them what trees to cut down and even in what area they're allowed to cut trees down.
confidence: 0.80
usefulness: medium
authority: wiki-derived
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: c8864ca614fd96d99df1613fcda836975a4365f68bb5b69ad72f68d2b6c70dec
validated_at: 2026-06-02T23:24:14.559049+00:00
approval_route: user-review
user_approved: true
---

The Forester's Hut is where the lumberjack will go in between chopping down trees. The lumberjack will cut down any tree in an approximate 150 block area (from themselves) that is not in a hut schematic and doesn't have cobblestone placed beneath it. Or you can optionally configure a specific chopping zone that they will use instead.

> **Note:** In addition to axes for chopping down trees, lumberjack require hoes for breaking leaves.

## Interface

It shows a list of recognized saplings the lumberjack can work with, even modded ones (if coded correctly).
Here you can turn on or off which type of trees the lumberjack will chop down.

- **Replanting:** Where you can select if you want the lumberjack to replant trees that are chopped down. They will only do this if they have enough saplings.
- **Zoning Mode:** This lets you turn on or off if you want the lumberjack to be restricted to a certain area when chopping trees. To choose the area, use the Obtain Tool button on the next tab.
- **Break Leaves:** This lets you decide whether you want the lumberjack to also remove all of the leaves of the tree, else he will only cut those which are in their way.
- **Recipe Mode:** This is unlocked by researching minecolonies/technology/warehousemaster in the university. This changes how multiple recipes for the same item are prioritized.
  - **Priority:** This is the default setting. The hut will try to use recipes that are higher up in their recipe list first.
  - **Warehouse Stock:** The hut will look in the warehouse first to see what resource you have more of before deciding what recipe it will use.
- **Use Shears:** Whether the lumberjack will use shears or a hoe to cut down the leaves.

This tool will let you define an area for the lumberjack to work. Right click one corner of the area you want, then left click the opposite corner, and this will set a box inside which the lumberjack will search for trees. Vertical coordinates are important as well. For best results, ensure that your box contains the base of the trees as well as at least some of the leaf blocks. It's not necessary to contain the entire tree, but it won't hurt. Defining a zone is optional, but recommended if you want a dedicated tree farm area.

**Source:** [[docs/wiki-ref/buildings/lumberjack.mdoc]]
