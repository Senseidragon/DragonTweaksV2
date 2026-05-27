**Title:** RegisterCapabilitiesEvent -- event bus assignment and registration methods
**Type:** fact
**Intent triggers:** RegisterCapabilitiesEvent, mod bus capability, capability registration, registerBlock, registerBlockEntity, registerEntity, registerItem, setProxyable, wrong event bus capability
**Source/evidence:** docs/stubs/net/neoforged/neoforge/capabilities/RegisterCapabilitiesEvent.java, NeoForge 21.1.230 source stub
**Rule or fact:** RegisterCapabilitiesEvent fires on the MOD event bus (implements IModBusEvent).

Registration methods:
- event.registerBlock(capability, provider, blocks...) -- at least one block required; throws IAE if array is empty
- event.registerBlockEntity(capability, blockEntityType, (be, context) -> ...) -- includes runtime type-check; if block entity type has changed at position, provider returns null
- event.registerEntity(capability, entityType, (entity, context) -> ...)
- event.registerItem(capability, provider, items...) -- at least one item required; throws IAE if array is empty
- event.setProxyable(capability) -- call at HIGHEST or HIGH priority; only by the capability definer; throws ISE if already set non-proxyable
- event.setNonProxyable(capability) -- throws ISE if already set proxyable

Failure mode: Registering on the game bus instead of the mod bus silently drops all providers with no error.
**Version scope:** NeoForge 21.1.x / Minecraft 1.21.1
**Promoted:** 2026-05-27
**Promoted from:** candidates/tentative-approved/core-api-2026-05-27.md
**Confidence:** 0.97
