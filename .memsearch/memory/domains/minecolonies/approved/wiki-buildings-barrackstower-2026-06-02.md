---
title: MineColonies building — Barracks Tower (workers: archer, druid, knight)
domain: minecolonies
fact: The Barracks Towers are part of the Barracks and can employ up to 5 Guards.
confidence: 0.80
usefulness: medium
authority: wiki-derived
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: 9eddc0c78235cd946cfc059f6c1bbd349bc487eec2a9e21b235dc28d6b3d99f5
validated_at: 2026-06-02T23:24:14.457354+00:00
approval_route: user-review
user_approved: true
---

The Barracks Tower will employ and house one Guard for every level built (unlike the normal guardtower, which can only have 1 Guard at a time). For a detailed description of each guard type, see the barracks page. Each new Guard will need a bed in a house in order to spawn. However, once they are hired at the Barracks Tower, that becomes their new residence and the bed in the house will open up for another new citizen (child or recruit).

| Building Level | Max # of Guards |
| -------------- | --------------- |
| 1              | 1               |
| 2              | 2               |
| 3              | 3               |
| 4              | 4               |
| 5              | 5               |

The Barracks Tower locations are predetermined by the barracks that you choose. They are placed in specific locations to fit within the barracks. 

The maximum level of the Barracks Tower is the same as the barracks.

Guard(s) will patrol a set distance around their tower, which is based on their tower's level.

| Tower Level | Max Patrol Distance |
| ----------- | ------------------- |
| 1           | 80 blocks           |
| 2           | 110 blocks          |
| 3           | 140 blocks          |
| 4           | 170 blocks          |
| 5           | 200 blocks          |

> **Note:** If you place barracks/Barracks Tower near your colony border and level them up, your border will [[border.mdoc]].

## Interface

Click the **Obtain Tools** button to get the Guard Scepter. Right-clicking on a block with the Guard Scepter will set it as a guard spot or a patrol point.

- **Task:** This is where you can choose if you want the guard to patrol, follow, or guard.
  - **Patrol:** The guard will patrol an area you designate in **Patrol Settings**.
  - **Guard:** You can set one area for the guard to stay in. When you click "Set Target", you will be taken to the **Selection Tools** tab, where you can get the the Guard Scepter and designate the guard location.
  - **Follow:** The guard will follow you around as your personal bodyguard, protecting you or fighting alongside you. They will even go outside the colony when following! You can designate how they follow you in **Follow Settings**.
- **Retreat on low health:** Here you can choose if the Guard will attempt to retreat when they have low health.
- **Hire Trainee:** If there is a vacancy at this tower, a new knight or archer can be hired from the respective training facility (combatacademy for knight and archery for archer) instead of an unemployed colonist. This setting only matters if Assign Colonists to Jobs is turned to Automatic in the townhall GUI.
- **Patrol Mode:** This is where you can choose how you want the guard to patrol. Only available when the **Task** is set to **Patrol**.
  - **Automatic:** The guard will patrol from hut to hut and back to their tower. They will only patrol huts within the patrol range of their tower.
  - **Manual:** You can set the patrol route when you click on **Set Positions**. You will be taken to the **Selection Tools** tab, where you can get the the Guard Scepter and designate patrol positions for the guard to patrol between. To delete patrol positions, simply get a new Guard Scepter and click a new patrol position. The old ones should disappear.
- **Follow Mode:** This is where you can choose how you want the guard to follow you. Only available when the **Task** is set to **Follow**.
  - **Loose Grouping:** The guard will stay a decent range away from you.
  - **Tight Grouping:** The guard will stay relatively close to you.

**Source:** [[docs/wiki-ref/buildings/barrackstower.mdoc]]
