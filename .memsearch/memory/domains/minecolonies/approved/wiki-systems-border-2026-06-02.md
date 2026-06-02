---
title: MineColonies system — Colony Border
domain: minecolonies
fact: Colony system: Colony Border — governs a core aspect of colony operation.
confidence: 0.80
usefulness: medium
authority: wiki-derived
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: b4ac03cedb144d28863a1a7a03265f870f54426f689ea3f2deee1d8870e30792
validated_at: 2026-06-02T23:24:14.669102+00:00
approval_route: user-review
user_approved: true
---

Colonies have an area of claimed chunks (16x16 block areas) around them. Upon colony creation, a colony claims chunks in a square around it. The radius is set with the [[configfile.mdoc]] setting *initialColonySize*, which has a default of 4 chunks (not including the chunk the townhall is in).

Those claims are protected from modification through other players. See the [[protection.mdoc]] page for more information.

Extending your claim area can be done by building huts. Huts claim a square area around them after they are built, expanding all sides from the chunk the hutblock is located in. How much they claim depends on the building. They won't claim over the max range, set with the [[configfile.mdoc]] setting *maxColonySize*, which has a default radius of 20 chunks from the chunk the townhall is in. 

Deconstructing a building *will* remove the chunks it claimed. If you want to delete the colony, check the townhall page.

### Building Claim Areas:

#### townhall

| Level | Additional Chunks |
| ----- | ----------------- |
| 1     | 1 Chunk Radius    |
| 2     | 1 Chunk Radius    |
| 3     | 2 Chunk Radius    |
| 4     | 3 Chunk Radius    |
| 5     | 5 Chunk Radius    |

#### guardtower

| Level | Additional Chunks |
| ----- | ----------------- |
| 1     | 2 Chunk Radius    |
| 2     | 3 Chunk Radius    |
| 3     | 3 Chunk Radius    |
| 4     | 4 Chunk Radius    |
| 5     | 5 Chunk Radius    |

#### barracks

| Level | Additional Chunks |
| ----- | ----------------- |
| 1     | 2 Chunk Radius    |
| 2     | 2 Chunk Radius    |
| 3     | 2 Chunk Radius    |
| 4     | 2 Chunk Radius    |
| 5     | 2 Chunk Radius    |

> When the barracks is level 4, and all it's barrackstower are level 4 as well, the radius is increased to **3 Chunks**.

#### Other buildings

| Level | Additional Chunks |
| ----- | ----------------- |
| 1     | 1 Chunk Radius    |
| 2     | 1 Chunk Radius    |
| 3     | 1 Chunk Radius    |
| 4     | 2 Chunk Radius    |
| 5     | 2 Chunk Radius    |

**Source:** [[docs/wiki-ref/systems/border.mdoc]]
