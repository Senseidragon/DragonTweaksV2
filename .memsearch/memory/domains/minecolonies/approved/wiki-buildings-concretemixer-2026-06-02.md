---
title: MineColonies building — Concrete Mixer's Hut (workers: concretemixer)
domain: minecolonies
fact: The Concrete Mixer is responsible for creating concrete powder and concrete in your colony.
confidence: 0.80
usefulness: medium
authority: wiki-derived
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: d2b772ec4f9b41e8c56d140716ead7bf5892a4a73b7ccaaa7bfd7cd659794804
validated_at: 2026-06-02T23:24:14.485784+00:00
approval_route: user-review
user_approved: true
---

The Concrete Mixer's Hut will craft all types of concrete powder and place them in flowing water (built in to their hut), then mine the resulting concrete. The Concrete Mixer's Hut will only make concrete and concrete powder when they receive a request for a block and have the needed materials. (All their recipes are pretaught.)

## Interface

- **Recipe Mode:** This is unlocked by researching minecolonies/technology/warehousemaster in the university. This changes how multiple recipes for the same item are prioritized.
  - **Priority:** This is the default setting. The hut will try to use recipes that are higher up in their recipe list first.
  - **Warehouse Stock:** The hut will look in the warehouse first to see what resource you have more of before deciding what recipe it will use.

**Source:** [[docs/wiki-ref/buildings/concretemixer.mdoc]]
