**Title:** LevelEvent -- world lifecycle event subclass hierarchy
**Type:** fact
**Intent triggers:** LevelEvent, level load, level unload, level save, CreateSpawnPosition, PotentialSpawns, world lifecycle, mob spawning candidates
**Source/evidence:** docs/stubs/net/neoforged/neoforge/event/level/LevelEvent.java, NeoForge 21.1.230 source stub
**Rule or fact:** LevelEvent is abstract, fired on the NeoForge GAME event bus. All subclasses expose getLevel().

Subclasses:
- LevelEvent.Load -- level loads (ClientLevel constructor or server createLevels); both sides; not cancellable.
- LevelEvent.Unload -- level unloads (server stop or client switch); both sides; not cancellable.
- LevelEvent.Save -- level saved (ServerLevel#save); server-only; not cancellable.
- LevelEvent.CreateSpawnPosition -- first-time ServerLevel initialization choosing a spawn; server-only; cancellable. Cancelling skips vanilla spawn position selection.
- LevelEvent.PotentialSpawns -- natural spawner mob candidate list; cancellable (cancel yields empty list). Mutable: addSpawnerData(data), removeSpawnerData(data). Provides getMobCategory(), getPos(), getSpawnerDataList().
**Version scope:** NeoForge 21.1.x / Minecraft 1.21.1
**Promoted:** 2026-05-27
**Promoted from:** candidates/tentative-approved/core-api-2026-05-27.md
**Confidence:** 0.95
