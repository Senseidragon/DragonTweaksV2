**Title:** AttachmentType -- data attachment type registration, builder API, and holder-specific constraints
**Type:** fact
**Intent triggers:** AttachmentType, data attachment, IAttachmentHolder, NeoForgeRegistries, attachment builder, copyOnDeath, serialize attachment, sync attachment, attachment registration
**Source/evidence:** docs/stubs/net/neoforged/neoforge/attachment/AttachmentType.java, NeoForge 21.1.230 source stub
**Rule or fact:** AttachmentType<T> represents data attachable to any IAttachmentHolder (Entity, BlockEntity, Level, ChunkAccess).
Must be registered to NeoForgeRegistries.Keys.ATTACHMENT_TYPES via DeferredRegister.

Builder factory methods:
- AttachmentType.builder(Supplier<T>) -- simple; no holder reference
- AttachmentType.builder(Function<IAttachmentHolder, T>) -- captures holder reference at construction
- AttachmentType.serializable(Supplier<T>) -- shortcut for INBTSerializable types
- AttachmentType.serializable(Function<IAttachmentHolder, T>) -- with holder capture

Builder chain methods (call before .build()):
- .serialize(IAttachmentSerializer) -- persist to disk via custom serializer
- .serialize(Codec<T>) -- persist via codec; cannot capture holder reference
- .serialize(Codec<T>, Predicate<T> shouldSerialize) -- conditional codec serialization
- .copyOnDeath() -- copy on player respawn or living entity conversion; REQUIRES serializer set first; throws ISE if not
- .copyHandler(IAttachmentCopyHandler<T>) -- custom copy logic; REQUIRES serializer set first
- .sync(StreamCodec) -- sync to all clients tracking the holder
- .sync(BiPredicate<IAttachmentHolder, ServerPlayer>, StreamCodec) -- selective sync per player

Holder-specific rules:
- Entity: serializable attachments NOT copied on death by default; ARE copied on returning from the End. Opt into death-copy via .copyOnDeath() (serializer required first).
- BlockEntity: call BlockEntity.setChanged() after modifying an attachment.
- ChunkAccess: call ChunkAccess.setUnsaved(true) after modifying an attachment.
**Version scope:** NeoForge 21.1.x / Minecraft 1.21.1
**Promoted:** 2026-05-27
**Promoted from:** candidates/tentative-approved/core-api-2026-05-27.md
**Confidence:** 0.95
