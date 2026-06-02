**Title:** IBlockEntityExtension -- NeoForge-added methods on all BlockEntities
**Type:** fact
**Intent triggers:** IBlockEntityExtension, onLoad, getPersistentData, invalidateCapabilities, requestModelDataUpdate, getModelData, handleUpdateTag, onDataPacket, block entity NeoForge extension
**Source:** docs/stubs/net/neoforged/neoforge/common/extensions/IBlockEntityExtension.java, NeoForge 21.1.230 source stub
**Rule or fact:** IBlockEntityExtension is automatically mixed into all BlockEntity instances by NeoForge.

Key methods:
- onDataPacket(Connection, ClientboundBlockEntityDataPacket, HolderLookup.Provider) -- default loads CompoundTag via loadWithComponents if tag is not empty
- handleUpdateTag(CompoundTag, HolderLookup.Provider) -- client receives chunk update tag; default calls loadWithComponents
- getPersistentData() -- returns a CompoundTag written to and read from disk; use for arbitrary custom data not covered by DataComponents
- onLoad() -- called when first added to world, or before the first tick after chunk load/generation. Override this instead of a firstTick boolean in tick().
- requestModelDataUpdate() -- notifies renderer that getModelData() output has changed; client-only, no-op on server
- getModelData() -- return ModelData for custom BakedModel rendering; default is ModelData.EMPTY
- invalidateCapabilities() -- calls level.invalidateCapabilities(be.getBlockPos()); safe to call when level is null (no-op in that case)
- hasCustomOutlineRendering(Player) -- return true to enable the custom outline rendering pass

Usage pattern: override onLoad() for initialization code instead of checking firstTick in the tick method.
**Version scope:** NeoForge 21.1.x / Minecraft 1.21.1
**Promoted:** 2026-05-27
**Promoted from:** candidates/tentative-approved/core-api-2026-05-27.md
**Confidence:** 0.94
