---
title: MineColonies building — Graveyard (workers: undertaker)
domain: minecolonies
fact: The Graveyard is where your deceased citizens will be laid to rest. Every citizen will get a gravestone with their name on it, as long as there is room.
confidence: 0.80
usefulness: medium
authority: wiki-derived
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: 5a31b1d676474dffed860a9b7ffadc1f49b1bf16ce50968de236dd94af5c7c54
validated_at: 2026-06-02T23:24:14.538330+00:00
approval_route: user-review
user_approved: true
---

The Graveyard is where the graveyard will bury your deceased citizens.

The grave (decayed or not) will hold all the items the citizen had in their inventory at the time of death. The player can right-click the grave to open its inventory and retrieve the items. The graveyard will walk (run if you've completed the relevant research) toward the grave and retrieve its inventory, then go back to the Graveyard.

The recommended maximum grave count per Graveyard level is below. This is **not mandatory**, and the actual amount will vary between styles.

| Building Level | Number of Graves |
| -------------- | ---------------- |
| 1              | 14               |
| 2              | 18               |
| 3              | 27               |
| 4              | 36               |
| 5              | 50               |

Once the graveyard gets to the Graveyard, they will attempt to revive the deceased citizen. The chance for them to succeed can be increased by:
- Researches (+1% and +2%)
- The graveyard its Mana skill (+0.125% per Mana Skill Point)
- The level of the Graveyard (+0.5% per Level)
- The use of totems unlocked by research (Totem gets used up with a chance of 1%)

By default, the chance of reviving is capped at 2.5%. This cap can be boosted by upgrading the mysticalsite (0.5% per Level) and the use of totems (5% for 1 Totem, 7.5% for multiple totems). In total, the maximum chance is 12.5%.

If the citizen cannot be revived, the graveyard will bury them in the Graveyard. Another grave will be placed with the citizen's name on it (this grave does not store items).

The graveyard is exempt from mourning so they can complete their job.

## Interface

The top half is a list of the graves the graveyard needs to recover. The second half is a list of currently-buried citizens.

**Source:** [[docs/wiki-ref/buildings/graveyard.mdoc]]
