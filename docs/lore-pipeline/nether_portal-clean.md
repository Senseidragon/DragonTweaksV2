# Cleaned: Nether Portal

source_url: https://minecraft.wiki/w/Nether_Portal
source_version: "1.21.1"
pipeline_stage: clean

---

## Construction

A Nether portal is a player-built vertical frame of obsidian, from 4x5 (minimum) up to 23x23 (maximum). Corner blocks are optional. Adjacent portals can share obsidian blocks. The frame cannot be built horizontally (unlike an End portal).

## Activation

Light the inside of the completed frame with flint and steel, a fire charge, a fireball impact, or a lightning strike; fire can also spread in naturally from adjacent flammable blocks. The frame must already be complete when the fire is placed -- lighting an incomplete frame does not activate it later when the last obsidian block is placed. Portals cannot be activated in the End.

## Travel

Standing in the portal teleports you after 4 seconds in Survival (instant in Creative); you can step out early to cancel. After arriving, a 15-second cooldown prevents you from being yanked back through immediately. Withers and the Ender Dragon cannot use portals at all.

Distance is scaled 1:8 between dimensions -- one block of travel in the Nether covers eight blocks in the Overworld. This makes the Nether useful for long-distance Overworld travel.

If no linked portal exists on the other side, the game builds one automatically near the equivalent coordinates. The new portal can land somewhere inconvenient -- inside a wall, over a lava pool, off a cliff -- so it is worth being ready to dig or bridge the moment you step through. A portal built wider than 18 blocks splits across two separate linked portals on the other side instead of one.

## Mob interactions

Active portals repel hoglins -- they will not walk into one. Zombified piglins occasionally spawn at the base of an active Overworld-side portal frame; they arrive with the full 15-second travel cooldown already active, so they cannot immediately step back through. No other mob spawns directly from a portal.

## Using one

You cannot open your inventory while standing in an active portal, and any other open menu (a chest, a villager's trades) snaps shut the moment you step in.

<!-- scraps: chunk loading mechanics (meta), coordinate-conversion algorithm internals / Euclidean distance portal selection (meta), forced-portal Y-clamping internals (meta), connected-portal-invisibility trivia (trivial), intersecting-portal x-axis-wins trivia (trivial), pre-lit ruined-portal/mansion intersection trivia (trivial), History/Achievements/Sounds/Bedrock+Legacy Console sections (BE-only/noise, stripped at source), GUI restriction while standing in portal (added), hoglins repelled by active portals (added), zombified piglin portal-frame spawn with active cooldown (added), wide portal splits to two Overworld links (added), withers/ender dragon cannot use portals (added), 1:8 coordinate ratio (added -- observable travel fact, not internal mechanics) -->
