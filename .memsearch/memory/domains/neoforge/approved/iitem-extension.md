**Title:** IItemExtension -- selected NeoForge-added methods on all Items
**Type:** fact
**Intent triggers:** IItemExtension, onItemUseFirst, getBurnTime, canPerformAction, ItemAbility, getEquipmentSlot, canElytraFly, elytraFlightTick, supportsEnchantment, canDisableShield, getFoodProperties, NeoForge item extension
**Source:** docs/stubs/net/neoforged/neoforge/common/extensions/IItemExtension.java, NeoForge 21.1.230 source stub
**Rule or fact:** IItemExtension is automatically mixed into all Item instances by NeoForge.

Key overrideable methods (@ApiStatus.OverrideOnly methods must be invoked via IItemStackExtension on the ItemStack, not called directly on Item):
- onItemUseFirst(ItemStack, UseOnContext) -- fires before block activation; return InteractionResult.PASS to allow vanilla handling
- getBurnTime(ItemStack, @Nullable RecipeType) -- furnace fuel value; 0 = not a fuel, -1 = defer to vanilla. Takes PRECEDENCE over NeoForgeDataMaps.FURNACE_FUELS data map. Prefer the data map unless burn time must be dynamic (e.g. NBT-dependent).
- canPerformAction(ItemStack, ItemAbility) -- return true if this stack can perform the ItemAbility (axe strip, shovel flatten, hoe till, etc.)
- getEquipmentSlot(ItemStack) -- return non-null to override the equipment slot; return null to use vanilla LivingEntity logic
- canElytraFly(ItemStack, LivingEntity) -- return true to allow elytra flight with this item in the chest slot
- elytraFlightTick(ItemStack, LivingEntity, int flightTicks) -- return true to continue flight; consume durability or energy here
- getDamage(ItemStack) / setDamage(ItemStack, int) / getMaxDamage(ItemStack) -- stack-sensitive damage; internally use DataComponents.DAMAGE and DataComponents.MAX_DAMAGE
- supportsEnchantment(ItemStack, Holder<Enchantment>) -- @OverrideOnly; whether enchantment can be applied via anvil or similar mechanisms
- isPrimaryItemFor(ItemStack, Holder<Enchantment>) -- @OverrideOnly; whether enchantment table can select this enchantment; subset of supportsEnchantment
- canDisableShield(ItemStack, ItemStack shield, LivingEntity, LivingEntity attacker) -- default: only AxeItem returns true
- getFoodProperties(ItemStack, @Nullable LivingEntity) -- stack-sensitive food properties; reads DataComponents.FOOD by default

Deprecated in 21.1 (scheduled for removal):
- onEntitySwing(ItemStack, LivingEntity) -- use onEntitySwing(ItemStack, LivingEntity, InteractionHand) instead
**Version scope:** NeoForge 21.1.x / Minecraft 1.21.1
**Promoted:** 2026-05-27
**Promoted from:** candidates/tentative-approved/core-api-2026-05-27.md
**Confidence:** 0.93
