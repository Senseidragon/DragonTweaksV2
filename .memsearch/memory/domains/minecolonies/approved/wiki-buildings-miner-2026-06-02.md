---
title: MineColonies building — Mine (workers: miner)
domain: minecolonies
fact: The Miner will gather stone and ores from the depths below, they will make a vast tunnel network underground to gather all of your valuables.
confidence: 0.80
usefulness: medium
authority: wiki-derived
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: cd7a955771180db7d6e7990278dcf1363e68b2586358b275ca91dcd1cab147f3
validated_at: 2026-06-02T23:24:14.567155+00:00
approval_route: user-review
user_approved: true
---

The Mine is where you can hire a miner to work the mine, or a quarrier to work the quarry. If you hire a quarrier, there will be no miner at this Mine. 

At the Mine, the miner will mine for ores and materials. Once they are hired, they will first create a shaft downward.

The miner digs their shaft down until to the first mining level below it, increasing one further for each building level past 1.

These mining levels are based on the ores you can find there most:
| Ore     | Y-level |
| ------- | ------- |
| Copper  | 48      |
| Iron    | 16      |
| Gold    | -16     |
| Diamond | Bedrock |

To give some examples:
If the miner builds their first platform at Y 48 or above:
- Level 1 can dig down to Y 48
- Level 2 can dig down to Y 16
- Level 3 can dig down to Y -16
- Etc

If the miner builds their first platform between Y 48 and Y 16:
- Level 1 can dig down to Y 16
- Level 2 can dig down to Y -16
- Level 3 can dig down to bedrock

Once the main shaft is completed, the miner will then branch out.

At the Mine, the miner will mine for ores and materials. Once they are hired, they will first create a shaft downward to a specific depth depending on the level of the Mine. Once the main shaft is completed, the miner will then branch out.

The miner will never dig further down than the Y-level specified in the "maximum depth" setting of the building. It is by default set to -100, which effectively means bedrock level.

While mining, sometimes the miner will get lucky and get an ore block instead of a basic stone block. The chance of getting "Lucky Ores" is set in the [[configfile.mdoc]].

> **Note:** When the miner encounters air whilst building the shaft downwards, they don't make platforms there, as they think they encountered a cave. In particular, that means you should not help them with mining. Even though they skip platforms, they still check the Y-level against the depth threshold and stop digging down if they aren't allowed to dig down further.

> **Note:** Placing the Mine hut below the maximum Y level it can mine will cause the miner not work and complain the hut needs to be upgraded. To avoid this error, place the hut at least 4 blocks above the maximum depth for the hut level. If you want your Mine to be lower, you will need to upgrade it before the miner will work.

| Building Level | Shaft Y Level |
| -------------- | ------------- |
| 1              | 40            |
| 2              | 20            |
| 3              | 0             |
| 4              | Bedrock       |
| 5              | "             |

## Interface

The level refers to the platforms the miner will place every 3 blocks down. Here you can assign what level of the Mine the miner will create their mineshafts (nodes).
If a level has a red number next to it, that means the miner is currently mining that level.
The miner will ignore orders to mine at a specific level until the entire mineshaft is completed to the maximum depth their hut's level allows.
You can also click Repair, to tell the miner to restore that level to its original state. This can be useful if a fire breaks out in the mineshaft.

- **Recipe Mode:** This is unlocked by researching minecolonies/technology/warehousemaster in the university. This changes how multiple recipes for the same item are prioritized.
  - **Priority:** This is the default setting. The hut will try to use recipes that are higher up in their recipe list first.
  - **Warehouse Stock:** The hut will look in the warehouse first to see what resource you have more of before deciding what recipe it will use.
- **Fill Block:** Here you can select what block the miner uses to fill in holes/gaps in the schematic. The default is the dirt block.
- **Max Depth:** Here you can overide the default maximum depth of the Mine, if you don't want the miner to dig as deep as the Mine level allows. The default is -100.
- **Use Shears:** Whether the miner will use shears to break certain blocks.

Here is where you can assign guards to patrol this Mine. If assigned, they will patrol the level the miner is currently mining at, to help protect them from hostile mobs.
Only guards set to the `Patrol Mine` task will show up here; tasks can be set in the guardtower it's GUI (barrackstower do not have the Patrol Mine task).

The amount of guards you can assign to the Mine changes based on the building it's level.

| Building Level | Amount of guards |
| -------------- | ---------------- |
| 1              | 1                |
| 2              | 1                |
| 3              | 2                |
| 4              | 2                |
| 5              | 3                |

**Source:** [[docs/wiki-ref/buildings/miner.mdoc]]
