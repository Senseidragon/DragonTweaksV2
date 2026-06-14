# Minecraft Status Effect Atlas (1.21)

Source: minecraft.wiki/w/Effect

| Effect | Resource ID | Type | Description |
|---|---|---|---|
| Absorption | `minecraft:absorption` | Positive | Adds temporary health that absorbs damage |
| Bad Omen | `minecraft:bad_omen` | Neutral | Causes ominous event when entering village or trial chamber |
| Blindness | `minecraft:blindness` | Negative | Impairs vision with black fog, disables sprinting and critical hits |
| Breath of the Nautilus | `minecraft:breath_of_the_nautilus` | Positive | Freezes an entity's oxygen bar |
| Conduit Power | `minecraft:conduit_power` | Positive | Increases underwater visibility and mining speed, prevents drowning |
| Darkness | `minecraft:darkness` | Negative | Adds pulsating darkening effect to screen |
| Dolphin's Grace | `minecraft:dolphins_grace` | Positive | Increases swimming speed |
| Fire Resistance | `minecraft:fire_resistance` | Positive | Prevents fire, lava, and heat damage |
| Glowing | `minecraft:glowing` | Neutral | Gives entity outline visible through blocks |
| Haste | `minecraft:haste` | Positive | Increases mining and attack speed |
| Health Boost | `minecraft:health_boost` | Positive | Increases maximum health |
| Hero of the Village | `minecraft:hero_of_the_village` | Positive | Gives trade discounts with villagers |
| Hunger | `minecraft:hunger` | Negative | Increases food exhaustion (drains food bar faster) |
| Infested | `minecraft:infested` | Negative | 10% chance to spawn 1–3 silverfish when hurt |
| Instant Damage | `minecraft:instant_damage` | Negative | Damages living entities, heals undead |
| Instant Health | `minecraft:instant_health` | Positive | Heals living entities, damages undead |
| Invisibility | `minecraft:invisibility` | Positive | Makes entity invisible, reduces mob detection range |
| Jump Boost | `minecraft:jump_boost` | Positive | Increases jump height and reduces fall damage |
| Levitation | `minecraft:levitation` | Negative | Floats the affected entity upward |
| Luck | `minecraft:luck` | Positive | Increases loot quality from loot tables |
| Mining Fatigue | `minecraft:mining_fatigue` | Negative | Decreases mining and attack speed |
| Nausea | `minecraft:nausea` | Negative | Wobbles and warps the player's screen |
| Night Vision | `minecraft:night_vision` | Positive | Enables clear vision in darkness and underwater |
| Oozing | `minecraft:oozing` | Negative | Spawns 2 slimes upon death |
| Poison | `minecraft:poison` | Negative | Inflicts damage over time, cannot reduce health below 1 HP |
| Bad Luck | `minecraft:unluck` | Negative | Decreases loot quality from loot tables |
| Raid Omen | `minecraft:raid_omen` | Neutral | Starts a raid when effect expires in a village |
| Regeneration | `minecraft:regeneration` | Positive | Restores health over time |
| Resistance | `minecraft:resistance` | Positive | Reduces all incoming damage |
| Saturation | `minecraft:saturation` | Positive | Restores hunger and saturation points |
| Slow Falling | `minecraft:slow_falling` | Positive | Decreases falling speed and negates fall damage |
| Slowness | `minecraft:slowness` | Negative | Decreases walking speed |
| Speed | `minecraft:speed` | Positive | Increases movement speed on land |
| Strength | `minecraft:strength` | Positive | Increases melee damage dealt |
| Trial Omen | `minecraft:trial_omen` | Neutral | Transforms nearby trial spawners into ominous variants |
| Water Breathing | `minecraft:water_breathing` | Positive | Prevents drowning and enables underwater breathing |
| Weakness | `minecraft:weakness` | Negative | Decreases melee damage dealt |
| Weaving | `minecraft:weaving` | Negative | Reduces cobweb slowness by 25%, spreads cobwebs on death |
| Wind Charged | `minecraft:wind_charged` | Negative | Affected entity emits wind burst upon death |
| Wither | `minecraft:wither` | Negative | Inflicts damage over time, can be lethal |

## Notes for Advisor System

- **Drowning** is not a status effect — it is the air bubble mechanic. No effect ID exists to hook into.
- **Hunger** (`minecraft:hunger`) increases food exhaustion (drains food bar faster). This is distinct from the food bar level (0–20) going low through normal play.
- **Water Breathing** and **Conduit Power** both prevent drowning — relevant if AdvisorStatusMonitor covers beneficial effects.
- **Fatal Poison** does not appear in the 1.21 Java Edition effect list; Poison alone is the correct identifier.
