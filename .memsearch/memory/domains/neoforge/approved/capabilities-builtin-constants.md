**Title:** Capabilities -- NeoForge built-in capability constants (ItemHandler, EnergyStorage, FluidHandler)
**Type:** fact
**Intent triggers:** Capabilities, IItemHandler, IEnergyStorage, IFluidHandler, built-in capabilities, neoforge capability keys, ItemHandler.BLOCK, EnergyStorage.BLOCK, FluidHandler.BLOCK
**Source:** docs/stubs/net/neoforged/neoforge/capabilities/Capabilities.java, NeoForge 21.1.230 source stub
**Rule or fact:** NeoForge provides built-in capability constants in net.neoforged.neoforge.capabilities.Capabilities.
All keys use namespace "neoforge".

ItemHandler:
- Capabilities.ItemHandler.BLOCK -- BlockCapability<IItemHandler, @Nullable Direction>
- Capabilities.ItemHandler.ENTITY -- EntityCapability<IItemHandler, @Nullable Void> (combined inventory view of all subparts)
- Capabilities.ItemHandler.ENTITY_AUTOMATION -- EntityCapability<IItemHandler, @Nullable Direction> (automation-accessible; used by hoppers, droppers, and similar)
- Capabilities.ItemHandler.ITEM -- ItemCapability<IItemHandler, @Nullable Void>

EnergyStorage:
- Capabilities.EnergyStorage.BLOCK -- BlockCapability<IEnergyStorage, @Nullable Direction>
- Capabilities.EnergyStorage.ENTITY -- EntityCapability<IEnergyStorage, @Nullable Direction>
- Capabilities.EnergyStorage.ITEM -- ItemCapability<IEnergyStorage, @Nullable Void>

FluidHandler:
- Capabilities.FluidHandler.BLOCK -- BlockCapability<IFluidHandler, @Nullable Direction>
- Capabilities.FluidHandler.ENTITY -- EntityCapability<IFluidHandler, @Nullable Direction>
- Capabilities.FluidHandler.ITEM -- ItemCapability<IFluidHandlerItem, @Nullable Void> (note: IFluidHandlerItem, not IFluidHandler)
**Version scope:** NeoForge 21.1.x / Minecraft 1.21.1
**Promoted:** 2026-05-27
**Promoted from:** candidates/tentative-approved/core-api-2026-05-27.md
**Confidence:** 0.97
