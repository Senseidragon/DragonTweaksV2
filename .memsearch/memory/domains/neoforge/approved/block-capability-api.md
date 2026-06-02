**Title:** BlockCapability -- querying, providing, and invalidation rules
**Type:** fact
**Intent triggers:** BlockCapability, capability, getCapability, RegisterCapabilitiesEvent, invalidateCapabilities, BlockCapabilityCache, registerBlockEntity, registerBlock, capability invalidation
**Source:** docs/stubs/net/neoforged/neoforge/capabilities/BlockCapability.java, NeoForge 21.1.230 source stub
**Rule or fact:** BlockCapability<T, C> provides flexible access to objects of type T at a block position.

Querying:
- level.getCapability(BlockCapability, BlockPos, context) -- returns T or null
- For repeated queries at the same position: use BlockCapabilityCache to avoid repeated lookup overhead

Providing (register via RegisterCapabilitiesEvent on mod bus):
- event.registerBlockEntity(capability, blockEntityType, (be, context) -> ...) -- for block entities
- event.registerBlock(capability, provider, blocks...) -- for plain blocks

Invalidation (REQUIRED):
- If a previously returned capability becomes invalid or a new one is available, call level.invalidateCapabilities(pos).
- Plain blocks MUST call level.invalidateCapabilities(pos) in onPlace() AND onRemove().
- BlockEntities may call IBlockEntityExtension.invalidateCapabilities() as a convenience.
- Failure to invalidate causes stale BlockCapabilityCache results with no visible error.

Factory methods:
- BlockCapability.create(name, typeClass, contextClass)
- BlockCapability.createVoid(name, typeClass) -- no context
- BlockCapability.createSided(name, typeClass) -- @Nullable Direction context (most common pattern)

Proxyability: not proxyable by default. Capability definer marks proxyable via RegisterCapabilitiesEvent.setProxyable() at HIGH/HIGHEST priority. Mods forwarding "all" capabilities must skip non-proxyable ones.
**Version scope:** NeoForge 21.1.x / Minecraft 1.21.1
**Promoted:** 2026-05-27
**Promoted from:** candidates/tentative-approved/core-api-2026-05-27.md
**Confidence:** 0.97
