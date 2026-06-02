**Title:** DeferredHolder -- lazy registry reference and bind behavior
**Type:** fact
**Intent triggers:** DeferredHolder, DeferredBlock, DeferredItem, registry reference, isBound, get() NPE, unbound value, Holder, Supplier
**Source:** docs/stubs/net/neoforged/neoforge/registries/DeferredHolder.java, NeoForge 21.1.230 source stub
**Rule or fact:** DeferredHolder<R, T> implements both Holder<R> and Supplier<T>, resolving lazily from the registry by ResourceKey.

Behavior:
- .get() and .value() throw NullPointerException ("Trying to access unbound value: ...") if called before the registry is populated.
- .isBound() returns false until the registry contains the entry.
- .asOptional() returns Optional.empty() if not bound.
- .kind() always returns Holder.Kind.REFERENCE.

Specialized subtypes:
- DeferredBlock<B extends Block> -- returned by DeferredRegister.Blocks.register()
- DeferredItem<I extends Item> -- returned by DeferredRegister.Items.register()
**Version scope:** NeoForge 21.1.x / Minecraft 1.21.1
**Promoted:** 2026-05-27
**Promoted from:** candidates/tentative-approved/core-api-2026-05-27.md
**Confidence:** 0.96
