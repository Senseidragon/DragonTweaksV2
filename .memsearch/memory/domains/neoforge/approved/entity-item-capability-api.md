**Title:** EntityCapability and ItemCapability -- factory, query, and registration API
**Type:** fact
**Intent triggers:** EntityCapability, ItemCapability, entity capability, item capability, getCapability entity, registerEntity, registerItem, capability no invalidation
**Source:** docs/stubs/net/neoforged/neoforge/capabilities/EntityCapability.java, NeoForge 21.1.230 source stub
**Rule or fact:** EntityCapability<T, C> provides access to objects of type T from entities.

Query: entity.getCapability(EntityCapability) -- returns T or null.

Factory methods:
- EntityCapability.createVoid(name, typeClass) -- no context
- EntityCapability.createSided(name, typeClass) -- @Nullable Direction context

Register via RegisterCapabilitiesEvent.registerEntity(capability, entityType, (entity, context) -> ...).

ItemCapability<T, C>: accessed from ItemStacks.
Register via RegisterCapabilitiesEvent.registerItem(capability, provider, items...).

Unlike BlockCapability, entity and item capabilities do not require explicit invalidation calls.
**Version scope:** NeoForge 21.1.x / Minecraft 1.21.1
**Promoted:** 2026-05-27
**Promoted from:** candidates/tentative-approved/core-api-2026-05-27.md
**Confidence:** 0.95
