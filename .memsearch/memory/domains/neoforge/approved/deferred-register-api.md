**Title:** DeferredRegister -- registration helper, factory methods, and usage constraints
**Type:** fact
**Intent triggers:** DeferredRegister, registration helper, register blocks, register items, mod event bus, registerEventBus, DeferredRegister.Blocks, DeferredRegister.Items, DeferredBlock, DeferredItem
**Source/evidence:** docs/stubs/net/neoforged/neoforge/registries/DeferredRegister.java, NeoForge 21.1.230 source stub
**Rule or fact:** DeferredRegister<T> is the standard object registration helper in NeoForge 21.1.x.

Factory methods:
- DeferredRegister.create(Registry<T>, String namespace)
- DeferredRegister.create(ResourceKey<Registry<T>>, String namespace)
- DeferredRegister.create(ResourceLocation registryName, String modid)
- DeferredRegister.createBlocks(String modid) -- returns DeferredRegister.Blocks; store as that concrete type
- DeferredRegister.createItems(String modid) -- returns DeferredRegister.Items; store as that concrete type
- DeferredRegister.createDataComponents(ResourceKey<Registry<DataComponentType<?>>>, String modid)

Constraints:
- Must call .register(IEventBus modBus) in the mod constructor to wire to the mod event bus.
- Cannot call .register(name, supplier) after RegisterEvent fires -- throws IllegalStateException.
- Cannot register a DeferredRegister to more than one event bus -- throws IllegalStateException.
- The Supplier passed to .register() must return a NEW instance every invocation; do not cache.

Subclass API:
- DeferredRegister.Blocks: register() returns DeferredBlock<B>. Extra helpers: registerBlock(name, func, props), registerSimpleBlock(name, props), registerSimpleBlock(name).
- DeferredRegister.Items: register() returns DeferredItem<I>. Extra helpers: registerItem(name, func, props), registerSimpleItem(name), registerSimpleBlockItem(Holder<Block>, props).
- Must store DeferredRegister.Blocks / DeferredRegister.Items as their concrete types, not as DeferredRegister<Block> / DeferredRegister<Item>.
**Version scope:** NeoForge 21.1.x / Minecraft 1.21.1
**Promoted:** 2026-05-27
**Promoted from:** candidates/tentative-approved/core-api-2026-05-27.md
**Confidence:** 0.97
