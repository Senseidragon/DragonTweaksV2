**Title:** ExplosionEvent -- Start (cancellable) and Detonate (mutable block/entity lists)
**Type:** fact
**Intent triggers:** ExplosionEvent, explosion cancel, ExplosionEvent.Start, ExplosionEvent.Detonate, getAffectedBlocks, getAffectedEntities, explosion modifier
**Source:** docs/stubs/net/neoforged/neoforge/event/level/ExplosionEvent.java, NeoForge 21.1.230 source stub
**Rule or fact:** ExplosionEvent is abstract, fired on the NeoForge GAME event bus. Provides getLevel() and getExplosion().

Subclasses:
- ExplosionEvent.Start -- cancellable; fires BEFORE the explosion occurs; cancelling prevents the explosion entirely.
- ExplosionEvent.Detonate -- NOT cancellable; fires after the explosion has computed affected blocks and entities.
  - getAffectedBlocks() -- returns Explosion.getToBlow() (the live mutable list; modifying it changes explosion outcome).
  - getAffectedEntities() -- mutable list of entities affected; modifications change who takes damage/knockback.
**Version scope:** NeoForge 21.1.x / Minecraft 1.21.1
**Promoted:** 2026-05-27
**Promoted from:** candidates/tentative-approved/core-api-2026-05-27.md
**Confidence:** 0.95
