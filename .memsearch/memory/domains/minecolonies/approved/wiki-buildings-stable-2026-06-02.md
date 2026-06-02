---
title: MineColonies building — Stable (workers: stablemaster, cavalry)
domain: minecolonies
fact: The Stable is where the Stablemaster trains horses into Cavalry Horses and prepares mounted units for combat.
confidence: 0.80
usefulness: medium
authority: wiki-derived
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: 5e4b9e35deb54648a95dbe5fbc43408ea29aec26d8bfe04e9a7df19058cddc66
validated_at: 2026-06-02T23:24:14.617567+00:00
approval_route: user-review
user_approved: true
---

The Stable is where the stablemaster trains horses into Cavalry Horses and keeps them ready for mounted combat. Each Stable employs one stablemaster and one cavalry per building level.

The stablemaster will train horses into Cavalry Horses, feed horses, retrieve wandering horses, and repair mount equipment so it is ready for combat. Mount equipment must be periodically repaired by the stablemaster before a Cavalry Horse can be ridden again.

The cavalry function similarly to guards at a guardtower, with assignable tasks such as patrol and guard. However, cavalry only patrol between  and [[gatehouse.mdoc]].

> **Note:** Mounted units can open gates but cannot open doors. If your Stable is enclosed, make sure it is accessible via a gate rather than a door.

The building requires 2 blocks tagged with `stall` per building level to designate horse stalls.

| Building Level | Cavalry Units |
| -------------- | ------------- |
| 1              | 1             |
| 2              | 2             |
| 3              | 3             |
| 4              | 4             |
| 5              | 5             |

## Interface

- **Breeding:** Here you can choose if the stablemaster will breed horses.
- **Task:** This is where you can choose if you want the cavalry to patrol, follow, or guard.
  - **Patrol:** The cavalry will patrol between gatehouses and stables.
  - **Guard:** You can set one area for the cavalry to stay in. When you click "Set Target", you will be taken to the **Selection Tools** tab, where you can get the Guard Scepter and designate the guard location.
  - **Follow:** The cavalry will follow you as your personal mounted bodyguard.
- **Retreat on low health:** Here you can choose if the cavalry will attempt to retreat when they have low health.
- **Hire Trainee:** If there is a vacancy, a new cavalry can be hired from an appropriate training facility instead of an unemployed colonist. This setting only matters if Assign Colonists to Jobs is turned to Automatic in the townhall GUI.
- **Patrol Mode:** This is where you can choose how you want the cavalry to patrol. Only available when the **Task** is set to **Patrol**.
  - **Automatic:** The cavalry will patrol between gatehouses and stables within patrol range.
  - **Manual:** You can set the patrol route when you click on **Set Positions**. You will be taken to the **Selection Tools** tab, where you can get the Guard Scepter and designate patrol positions.
- **Patrol Interval:** How long the cavalry waits before starting a new patrol. Only available when the **Task** is set to **Patrol**.
- **Follow Mode:** This is where you can choose how you want the cavalry to follow you. Only available when the **Task** is set to **Follow**.
  - **Loose Grouping:** The cavalry will stay a decent range away from you.
  - **Tight Grouping:** The cavalry will stay relatively close to you.

**Source:** [[docs/wiki-ref/buildings/stable.mdoc]]
