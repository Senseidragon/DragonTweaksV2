---
title: MineColonies building — Builder's Hut (workers: builder)
domain: minecolonies
fact: The Builder is the most vital building in your colony, the Builder will construct any other building and is the first building you need in your colony.
confidence: 0.80
usefulness: medium
authority: wiki-derived
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: 3a4fe926df83536f2332c711ca9424b9339c4d6a0c78cad70783bae85c356f50
validated_at: 2026-06-02T23:24:14.469562+00:00
approval_route: user-review
user_approved: true
---

### Before you build *any* other building, you must build the Builder's Hut. If the Builder's Hut is not built, the builder cannot build other buildings.

Before you choose a place to build the Builder's Hut, take into account the distances among the other possible building sites and obstacles like water, trees, caves, mountains, lava sources, etc. After you have selected a place for the hut, you have to craft the Builder's Hut block and place it with your structurize/sceptergold. Once the hut is placed, the builder will be automatically assigned (or you can manually assign one with the best [[worker.mdoc]] for a builder if you changed this in the settings tab in the townhall).

Now you will have to issue the build assignment so the builder can build their own hut first. The builder will ask for the materials they need. Make sure to check the minecolonies/resourcescroll or the Required Resources tab of the Builder's Hut GUI to see what materials the builder is requesting for any build/upgrade. Any material in the list that is still missing will be in red letters.

Once the Builder's Hut is built you can now build anything you want, like worker huts, buildings, decorations, or your own schematics.

- **Note:** The builder can only build or upgrade any other hut up to the level of their own hut. So, in order for the builder to upgrade any building, the Builder's Hut must be upgraded first. Then the builder will be able to upgrade any other building(s).

### Hints and Tips

For the placement of the Builder's Hut, you should consider having the hut in the middle of where you plan to have the rest of your buildings so that the builder has less of a distance to walk between their hut and the build sites.

The builder will not start another build assignment until they have finished the current one.

You can go into the townhall it's GUI and click on the work orders tab to cancel builds as well as arrange the priorities of the other build orders you have there. If you cancel a work order and it was being built already, if you assign the build order again, the builder will continue where they left off.

If the builder removes a block while building and/or upgrading, they will keep it in their inventory and dump any items in their inventory at the end of a build into the Builder's Hut inventory.

## Interface

- **Task Assignment Mode:** Here you can set your builder to Manual or Automatic mode (Automatic by default).
  - **Automatic:** The builder chooses which build order they'll complete next themselves (based on the order of the build requests in the townhall GUI's work orders tab).
  - **Manual:** You choose their next build order yourself by clicking Select next to the build order's name.
- **Recipe Mode:** This is unlocked by researching minecolonies/technology/warehousemaster in the university. This changes how multiple recipes for the same item are prioritized.
  - **Priority:** This is the default setting. The hut will try to use recipes that are higher up in their recipe list first.
  - **Warehouse Stock:** The hut will look in the warehouse first to see what resource you have more of before deciding what recipe it will use.
- **Construction Strategy:** This is unlocked by researching minecolonies/technology/buildermodes in the university. This allows you to change how the builder builds, reducing pathfinding and speeding up builds (especially on large builds). Any one of these can be set in the [[structurize.mdoc]] config, but once the minecolonies/technology/buildermodes research is done in the university, the one set here takes precedence.
  - **Default:** The default row-by-row pattern.
  - **Hilbert:** Hilbert does [this pattern](https://en.wikipedia.org/wiki/Hilbert_curve), with a little difference to work for rectangular areas.
  - **Inward Circle:** Blocks are placed like a square spiral from the outside in.
  - **Inward Circle Height 1-4**: Same as `inwardcircle`, but configurable to go X amount of blocks up as well.
  - **Random:** Blocks are placed in an entirely random order. Note that this slows down builds.
- **Use Shears:** Whether the builder will use shears to harvest certain blocks like leaves or grass.
- **Fill Block:** Here you can select what block the builder uses to fill in holes/gaps in the schematic. The default is the dirt block.

**Source:** [[docs/wiki-ref/buildings/builder.mdoc]]
