---
title: MineColonies building — Warehouse (workers: courier)
domain: minecolonies
fact: The Warehouse is the main building you need for colony automation. The Warehouse serves as the storage for your entire colony and employs Couriers to haul goods around your colony.
confidence: 0.80
usefulness: medium
authority: wiki-derived
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: bf6437e5b9968aa35ace418320d80a1473855c2ec0e42566459129e43bef1b33
validated_at: 2026-06-02T23:24:14.645780+00:00
approval_route: user-review
user_approved: true
---

The Warehouse is the central storage from where a warehouse will store and retrieve everything your workers harvest, craft, or need. Items will be stored in minecolonies/blockminecoloniesrack.

The level of the Warehouse will determine how many  will be able to use it at the same time. Level up the Warehouse to increase the amount of  that can work in it. Leveling up the Warehouse will also increase its storage capacity.

| Building Level | Max  |
| -------------- | ----------------------------- |
| 1              | 2                             |
| 2              | 4                             |
| 3              | 6                             |
| 4              | 8                             |
| 5              | 10                            |

## Interface

- Header:
    - **Building Name**: Shows the name of the building, including the level of the building.
    - **Pencil**: Allows you to rename the building. The level of the building will always be listed after the name.
- Controls:
    - **Build Options**: Lets you create a build, upgrade, or repair build order for this hut. To learn more about the building system, please visit the builder page.
- Footer:
    - **Info Button**: Some huts have an in-game guide. Press the ? button to access it.
    - **Inventory**: Here you can access the hut block’s storage, where the worker at this hut takes and deposits materials. They will also use any racks that were placed in the hut when it was built or upgraded, so be sure to check those as well!
    - **Chest Icon**: Click this button to see all the items in the hut’s storage (including the hut block’s inventory and any racks that came with the hut). Clicking the ? button next to an item’s count will highlight the storage container it’s in.

- **Assigned Workers:** A list of the  assigned to this Warehouse.
- **Manage Workers:** You can choose which  to hire at the Warehouse. **Note:** this only works if you have turned the worker hiring mode in the Warehouse block to manual, otherwise your  will be hired automatically.
- **Recall Workers:** Recalls the  at this Warehouse to the hut block. You might use it if they are stuck somewhere, you want to see what they have, or want to give them something directly.

- **Block of Emerald:** You can increase the max amount of stacks in each rack by pressing this button. This can only be done when the Warehouse is at level 5 and you have at least one block of emerald in your inventory. The storage can be increased 3 times.
- **Sort:** The sort option is available when the Warehouse reaches level 3. It sorts and stacks all the items in the racks.

**Source:** [[docs/wiki-ref/buildings/warehouse.mdoc]]
