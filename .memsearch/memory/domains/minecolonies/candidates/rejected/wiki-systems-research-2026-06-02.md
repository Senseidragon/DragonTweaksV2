---
title: MineColonies system — Research
domain: minecolonies
fact: Colony system: Research — governs a core aspect of colony operation.
confidence: 0.80
usefulness: medium
authority: wiki-derived
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: 079eb6dd0d71e07c19f939357139697a2eb2e886b269273c6781f341707e899e
validated_at: 2026-06-02T23:24:14.698989+00:00
approval_route: user-review
user_approved: true
---

At the university, researcher can research various upgrades to your colony. These are split into multiple trees: . You access these from the second page of the university GUI.

Each column of a research tree is also the level the university needs to be to begin a research from that column. So:

| Research Tree Column | Minimum university Level |
| -------------------- | ----------------------------------------------- |
| 1                    | 1                                               |
| 2                    | 2                                               |
| 3                    | 3                                               |
| 4                    | 4                                               |
| 5+                   | 5                                               |

You can only have one column 6 research in each of the trees. To unlock a different column 6 research for that tree, you must undo the completed one first.

| Symbol                                                               | Description                                                                                                                                                                                                                                                                                                                                 |
| -------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
|       | A research can be **blocked**, either by an unfinished prerequisite research, by a completed blocking research, or because the research tree has another column 6 research already active.                                                                                                                                                  |
|         | A **locked** research requires a building or buildings, or other unrelated research, before it can be started.                                                                                                                                                                                                                              |
|     | An **unlocked** research has all colony and research requirements met, but requires an item or items. **These items must be in the player's inventory.**                                                                                                                                                                                    |
|       | An **available** research is ready to begin. Clicking the title of the research will consume the items from the player's inventory and start the research.                                                                                                                                                                                  |
|  | A **progressing** research is being worked on currently. This research will show its current progression and a rough estimate of the remaining time to completion. A progressing research can be canceled by clicking the research title and then clicking the Cancel pop-up. Cancelling a research will **not** refund the material costs. |
|     | A **complete** research has been fully unlocked by your university. Its effects have been applied to the colony and colonists.                                                                                                                                                                                       |
|          | Some researches are **exclusive**, requiring such extreme focus that they aren't compatible with each other. Only one research from a specific **or** selection may be learned in a colony at a time.                                                                                                                                       |
|             | Some completed researches may be **undone** if they block another research in some way, do not have a completed research that depends on them, and are not marked with a redstone torch as irreversible. Undoing a research does *not* refund the research costs and consumes the displayed item.                                           |

**Source:** [[docs/wiki-ref/systems/research.mdoc]]
