**Title:** IAttachmentHolder -- interface methods and getData side-effect
**Type:** fact
**Intent triggers:** IAttachmentHolder, getData, getExistingData, hasData, setData, removeData, syncData, attachment side effect, default value creation
**Source:** docs/stubs/net/neoforged/neoforge/attachment/IAttachmentHolder.java, NeoForge 21.1.230 source stub
**Rule or fact:** IAttachmentHolder is implemented by Entity, BlockEntity, Level, and ChunkAccess.

Methods:
- hasData(AttachmentType<?>) -- true if attachment exists; does NOT create default
- getData(AttachmentType<T>) -- returns attachment; if absent, CREATES AND STORES the default value then returns it
- getExistingData(AttachmentType<T>) -- returns Optional<T>; returns Optional.empty() if absent; does NOT create default
- getExistingDataOrNull(AttachmentType<T>) -- returns null if absent; backwards-compatible form
- setData(AttachmentType<T>, T) -- sets value; returns previous value or null
- removeData(AttachmentType<T>) -- removes; returns previous value or null
- syncData(AttachmentType<?>) -- syncs to clients if AttachmentSyncHandler is configured on the type

Critical distinction:
- getData() has a side effect: it creates AND stores the default value if the attachment is absent.
- getExistingData() / getExistingDataOrNull() do NOT create the default.
- Use getExistingData() when checking or reading without wanting to trigger default creation.
**Version scope:** NeoForge 21.1.x / Minecraft 1.21.1
**Promoted:** 2026-05-27
**Promoted from:** candidates/tentative-approved/core-api-2026-05-27.md
**Confidence:** 0.96
