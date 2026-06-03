# MineColonies API Index

Root of the MineColonies API reference tree. Each entry links to a package-level index.
Source: `com.minecolonies.api.*` and `com.minecolonies.core.*` (version/1.21, tag v1.21.1-1.1.1320-SNAPSHOT)

> **Supersedes:** `docs/MINECOLONIES_API_INDEX.md` (flat index — tombstoned, use this tree instead)

---

## Root files

### IMinecoloniesAPI.java
**Summary:** Static entry point for all MineColonies APIs; access via `IMinecoloniesAPI.getInstance()`.
**Source:** [[docs/api/minecolonies/IMinecoloniesAPI.java]]

### MinecoloniesAPIProxy.java
**Summary:** Concrete implementation of `IMinecoloniesAPI`; singleton returned by `getInstance()`.
**Source:** [[docs/api/minecolonies/MinecoloniesAPIProxy.java]]

---

## Subdirectories

- [[advancements/]] — advancement triggers fired by colony milestones
- [[blocks/]] — colony block types (huts, graves, racks, decorative)
- [[client/]] — client-only rendering, key mappings
- [[colony/]] — core colony interfaces: `IColony`, `ICitizenData`, `IBuilding`, jobs, requests, research
- [[compatibility/]] — third-party mod compatibility hooks
- [[configuration/]] — client, server, and common config objects
- [[crafting/]] — colony crafting types and recipe interfaces
- [[creativetab/]] — creative mode tab registration
- [[enchants/]] — MineColonies enchantment types
- [[entity/]] — citizen entity, AI state machine, mob raiders
- [[equipment/]] — tool and equipment type registry
- [[eventbus/]] — MineColonies internal event bus events
- [[inventory/]] — citizen inventory types
- [[items/]] — colony items and item components
- [[loot/]] — loot table integration
- [[quests/]] — quest system interfaces
- [[research/]] — research tree, effects, requirements
- [[sounds/]] — sound event constants
- [[tileentities/]] — block entity interfaces
- [[util/]] — utility classes
- [[core/]] — implementation-layer classes (AbstractJob, AbstractEntityAIBasic)
