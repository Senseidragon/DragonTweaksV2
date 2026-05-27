**Title:** BlockEvent -- block lifecycle event subclass hierarchy
**Type:** fact
**Intent triggers:** BlockEvent, BreakEvent, EntityPlaceEvent, NeighborNotifyEvent, FluidPlaceBlockEvent, FarmlandTrampleEvent, PortalSpawnEvent, BlockToolModificationEvent, block break cancel, block place cancel
**Source/evidence:** docs/stubs/net/neoforged/neoforge/event/level/BlockEvent.java, NeoForge 21.1.230 source stub
**Rule or fact:** BlockEvent is abstract, fired on the NeoForge GAME event bus. All subclasses expose getLevel(), getPos(), getState().

Subclasses:
- BlockEvent.BreakEvent -- server-side; cancellable; player attempts to break a block. May arrive pre-cancelled if Player#blockActionRestricted is true or block is a GameMasterBlock -- un-cancelling does not override those restrictions.
- BlockEvent.EntityPlaceEvent -- cancellable; entity places a block; provides getPlacedBlock(), getPlacedAgainst(), getEntity() (nullable).
- BlockEvent.EntityMultiPlaceEvent -- multi-block placement (e.g. beds); extends EntityPlaceEvent; provides getReplacedBlockSnapshots().
- BlockEvent.NeighborNotifyEvent -- server-only; physics/redstone neighbor update; cancellable; provides getNotifiedSides(), getForceRedstoneUpdate().
- BlockEvent.FluidPlaceBlockEvent -- liquid creates a block (cobblestone generator, obsidian); cancellable; use setNewState(BlockState) to change the result.
- BlockEvent.FarmlandTrampleEvent -- cancellable; entity tramples farmland; provides getEntity(), getFallDistance().
- BlockEvent.PortalSpawnEvent -- cancellable; nether portal spawn attempt; provides getPortalSize().
- BlockEvent.BlockToolModificationEvent -- cancellable; tool modifies block state (axe strip, shovel flatten, hoe till). Check isSimulated() before performing world modifications. Use setFinalState(BlockState) to change result.
**Version scope:** NeoForge 21.1.x / Minecraft 1.21.1
**Promoted:** 2026-05-27
**Promoted from:** candidates/tentative-approved/core-api-2026-05-27.md
**Confidence:** 0.94
