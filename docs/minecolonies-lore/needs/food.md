---
topic: Food
type: advisor-artifact
source: unknown — not captured in docs/wiki-ref/needs/food.mdoc's frontmatter; needs verification
scraped: unknown — capture date not recorded in source
version: "1.1.1299-1.21.1"
pipeline_stage: advisor-artifact
---

# Food

A citizen need built around a production chain: Farmers harvest tiered crops, the Chef and Cook turn them into meals at the Cookery and Restaurant, and the Baker rounds out the supply with baked goods at the Bakery. Citizens track their own saturation bar — distinct from the player's hunger bar — that drains while working and overnight, and must be kept fed to avoid stalled work and leveling.

## Production Chain
- Farmers harvest tiered crops (biome-restricted: cold, temperate, humid, or dry) and supply the rest of the chain.
- The Chef, working at the Cookery, turns raw crops into meals.
- The Cook, working at the Restaurant, cooks and serves meals to hungry citizens.
- The Baker, working at the Bakery, supplies baked goods to round out the food supply.
- If no Restaurant is available, citizens will request one be built or ask the player to provide food manually.

## Tiers and Vanilla Food
- MineColonies food is tiered; higher tiers satisfy citizens better.
- Citizens can eat vanilla food, but it carries a satisfaction penalty — a dedicated production chain is worth building.
- Some higher-tier dishes require ingredients from multiple biomes, which may mean trading with other colonies.

## Saturation Mechanics
- Each citizen has its own saturation bar (visible in their GUI), analogous to the player's hunger bar.
- Saturation drains while working, and again overnight — higher-level workers lose more per night.
- At 0 saturation: the citizen stops working, stops leveling, gains Slowness, and repeatedly requests food in chat.
- Below a certain saturation threshold, healing stops entirely; a fully saturated citizen heals at double speed.
- Required food level scales with the citizen's residence level — they will not eat food below their requirement.

## Known Gap
- The live wiki page renders a dynamic crop/biome list (`{% food_list /%}`) not present in the captured source. Specific crop names, tiers, and biome assignments are not yet available in this entry and must be sourced separately before it's complete.
