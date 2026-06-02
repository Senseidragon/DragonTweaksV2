**Title:** EntityJoinLevelEvent -- entity added to level; chunk-not-full warning
**Type:** fact
**Intent triggers:** EntityJoinLevelEvent, entity join level, entity added to world, addFreshEntity, loadedFromDisk, chunk deadlock, ChunkStatus.FULL, cancel entity spawn
**Source:** docs/stubs/net/neoforged/neoforge/event/entity/EntityJoinLevelEvent.java, NeoForge 21.1.230 source stub
**Rule or fact:** EntityJoinLevelEvent fires when an entity is added to a level (Level#addFreshEntity or PersistentEntitySectionManager#addNewEntity).

Event bus: NeoForge GAME bus (not mod bus).
Sides: both logical sides.
Cancellable: yes -- cancelling prevents the entity from being added.

Methods:
- getLevel() -- the level being joined
- loadedFromDisk() -- true if entity was loaded from disk; always false on logical client

WARNING: This event may fire before the underlying LevelChunk is promoted to ChunkStatus.FULL.
Accessing the world synchronously in this handler can cause chunk loading deadlocks.
Delay world-accessing logic (e.g. schedule on next tick) if needed.
**Version scope:** NeoForge 21.1.x / Minecraft 1.21.1
**Promoted:** 2026-05-27
**Promoted from:** candidates/tentative-approved/core-api-2026-05-27.md
**Confidence:** 0.96
