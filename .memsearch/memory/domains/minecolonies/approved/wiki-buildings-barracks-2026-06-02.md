---
title: MineColonies building — Barracks (workers: archer, druid, knight)
domain: minecolonies
fact: The Barracks is the ultimate protection for your colony. The Barracks consists of multiple towers that house up to 5 Guards, allowing you to get your military up and running fast.
confidence: 0.80
usefulness: medium
authority: wiki-derived
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: 2c5032d40f7898813bad1c8f4fd68159a2b6d35570d42f6e6699cb721c5995a8
validated_at: 2026-06-02T23:24:14.453355+00:00
approval_route: user-review
user_approved: true
---

The Barracks is the ultimate protection for your colony. Each Barracks can hold multiple barrackstower within its structure. The barrackstower (unlike the normal guardtower) will employ *and* house one Guard for every level built, for a total of 5 Guards per barrackstower! Each new Guard will need a bed in a residence in order to spawn. However, once they are hired at the barrackstower, that becomes their new residence and the bed in the house will open up for another new citizen. Currently, all official-style Barracks contain 4 barrackstower for a total of 20 Guards per Barracks for your colony. However, custom styles can have more or fewer than 4 barrackstower. Colonists like feeling safe, so building Barracks close to colonists' work and homes can improve their [[happiness.mdoc]].

| Building Level | Max # of barrackstower | Max Level of barrackstower |
| -------------- | --------------------------------------------- | ------------------------------------------------- |
| 1              | 1                                             | 1                                                 |
| 2              | 2                                             | 2                                                 |
| 3              | 3                                             | 3                                                 |
| 4              | 4                                             | 4                                                 |
| 5              | 4                                             | 5                                                 |

> **Note:** The Barracks has slightly higher border expansion than other buildings. See the [[border.mdoc]] page for more information.

## Guard Types

There are three types of guards that can be assigned to towers: knight, archer, and druid.

### knight

knight are melee guards. They require a sword to fight and can optionally use a shield and armor.

Before being hired as an actual guard, knight can first be trained at the combatacademy to level up their skills without risk of dying to mobs.

### archer

archer are ranged guards. They require a bow to fight and can optionally use arrows and armor.

Before being hired as an actual guard, archer can first be trained at the archery to level up their skills without risk of dying to mobs.

### druid

druid are support guards. They throw potions at fellow guards to improve their combat effectiveness. There is no dedicated training building for druid.

## Interface

- Header:
    - **Building Name**: Shows the name of the building, including the level of the building.
    - **Pencil**: Allows you to rename the building. The level of the building will always be listed after the name.
    - **Current Barbarian Position and Last Barbarian Spawn**: A tracker system for Barbarians. **Note:** you can only see the current barbarian position if you have **hired spies** (see below) during the current raid.
- Controls:
    - **Build Options**: Lets you create a build, upgrade, or repair build order for this hut. To learn more about the building system, please visit the builder page.
    - **Hire Spies**: This option is only available after the hut is level 3. Here you can hire spies during raids.
- Footer:
    - **Info Button**: Some huts have an in-game guide. Press the ? button to access it.
    - **Inventory**: Here you can access the hut block’s storage, where the worker at this hut takes and deposits materials. They will also use any racks that were placed in the hut when it was built or upgraded, so be sure to check those as well!
    - **Chest Icon**: Click this button to see all the items in the hut’s storage (including the hut block’s inventory and any racks that came with the hut). Clicking the ? button next to an item’s count will highlight the storage container it’s in.

Before each raid, you are able to hire spies for 5 gold per raid, these spies will go out into the world and if barbarians are spotted, the spies will tell you exactly where the barbarians are.

**Source:** [[docs/wiki-ref/buildings/barracks.mdoc]]
