---
title: MineColonies building — Guard Tower (workers: archer, druid, knight)
domain: minecolonies
fact: The Guard Tower is your primary defense, every tower will employ a single Guard and can greatly expand your colony claim radius.
confidence: 0.80
usefulness: medium
authority: wiki-derived
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: bd603ac5326ea0387a944a688d8b93798c2e53dc3b7ec4e32bafffaae6c71ffc
validated_at: 2026-06-02T23:24:14.542383+00:00
approval_route: user-review
user_approved: true
---

The Guard Tower will employ and house one Guard to protect your colony. For a detailed description of each guard type, see the barracks page. The new guard will need a bed in a house in order to spawn. However, once they are hired at the Guard Tower, that becomes their new home and the bed in their original home will open up for another new citizen (child or recruit). Citizens like feeling safe, so building  close to colonists' work and homes can improve their [[happiness.mdoc]]. Additionally, if you place  near your colony border and level them up, your border will [[border.mdoc]].

The guard will patrol a set distance around their building, which is based on their building its level.

| Building Level | Max Patrol Distance |
| -------------- | ------------------- |
| 1              | 80 blocks           |
| 2              | 110 blocks          |
| 3              | 140 blocks          |
| 4              | 170 blocks          |
| 5              | 200 blocks          |

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

**Source:** [[docs/wiki-ref/buildings/guardtower.mdoc]]
