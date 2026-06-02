**Title:** EntityAttributeCreationEvent -- registering attributes for custom entity types
**Type:** fact
**Intent triggers:** EntityAttributeCreationEvent, entity attributes, AttributeSupplier, LivingEntity attributes, createAttributes, DefaultAttributes, mod bus entity event
**Source:** docs/stubs/net/neoforged/neoforge/event/entity/EntityAttributeCreationEvent.java, NeoForge 21.1.230 source stub
**Rule or fact:** EntityAttributeCreationEvent is used to register AttributeSupplier instances for custom LivingEntity subclasses.

Event bus: MOD event bus (implements IModBusEvent).
Timing: fired after registration events, before FMLCommonSetupEvent.

Usage:
  event.put(MY_ENTITY_TYPE.get(), MyEntity.createAttributes().build());

Constraint: calling put() for an EntityType that already has a DefaultAttributes entry throws IllegalStateException ("Duplicate DefaultAttributes entry: ...").

Failure mode: Subscribing on the game bus instead of the mod bus silently skips registration; entity spawning then crashes with a missing attribute error at runtime.
**Version scope:** NeoForge 21.1.x / Minecraft 1.21.1
**Promoted:** 2026-05-27
**Promoted from:** candidates/tentative-approved/core-api-2026-05-27.md
**Confidence:** 0.97
