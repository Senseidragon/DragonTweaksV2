---
topic: Villager
type: advisor-artifact
source: "[[https://minecraft.wiki/w/Villager]]"
scraped: 2026-06-09
version: 1.21.1
pipeline_stage: advisor-artifact
---

# Villager

A passive NPC with 20 HP that inhabits villages, trades using emeralds, and is the primary trigger for iron golem summoning. Villagers are the cornerstone of village economy and defense -- protecting them keeps the golem supply healthy and the trading system functional.

## Spawning

Villagers generate in villages across plains, desert, savanna, snowy plains, taiga, and swamp biomes. Each structure type produces a distinct skin variant. Additional sources: igloo basements (one zombie villager behind iron door, curable), and zombie villager curing anywhere a zombie villager exists.

A villager that travels more than 32 blocks from its original village within 6 seconds forgets which village it belongs to and will not contribute to golem summoning until it joins a new one.

## Stats

- Health: 20 HP
- Movement speed: 0.5 (same as player walking)
- Cannot attack; panics and flees when threatened

## Profession and Job Sites

Villagers claim unclaimed job site blocks to adopt or change professions. A villager without a claimed site is "unemployed" and can be nudged into any profession by placing the appropriate block nearby. Nitwits (green robe) cannot adopt any profession regardless of available job sites.

13 professions + nitwit + unemployed:

| Profession | Job Site Block |
|---|---|
| Armorer | Blast Furnace |
| Butcher | Smoker |
| Cartographer | Cartography Table |
| Cleric | Brewing Stand |
| Farmer | Composter |
| Fisherman | Barrel |
| Fletcher | Fletching Table |
| Leatherworker | Cauldron |
| Librarian | Lectern |
| Mason | Stonecutter |
| Shepherd | Loom |
| Toolsmith | Smithing Table |
| Weaponsmith | Grindstone |

## Daily Schedule

- 08:00-15:00: Work at job site (resupplies locked trades up to twice per day)
- 15:00-17:00: Gather at bell if present
- 18:00+: Sleep at claimed bed

Villagers will not restock trades unless they reach their job site block during the work phase. A villager that cannot path to its job site will not restock.

## Trading

Only employed adult villagers can trade; nitwits, unemployed, and babies cannot. All trades use emeralds as currency. Each villager has five experience tiers unlocked by completing trades:

| Level | XP Threshold |
|---|---|
| Novice | 0 |
| Apprentice | 10 |
| Journeyman | 70 |
| Expert | 150 |
| Master | 250 |

Trade prices are affected by individual reputation (not village-wide popularity). Supply and demand applies: frequently traded items become more expensive; prices recover if trading slows. Demand is tracked per item globally, not per villager.

Notable obtainables: enchanted books with treasure enchantments (Mending), bottles o' enchanting, chainmail armor -- all otherwise difficult or impossible to craft.

## Reputation System

Each player has an individual reputation score with each villager. Reputation affects trade prices and iron golem hostility.

| Action | Reputation Change |
|---|---|
| Trading | +2 |
| Curing zombie villager (hero of village) | +20 |
| Curing zombie villager (direct) | +25 |
| Attacking villager | -25 |
| Killing nearby villager | -25 (x -5 multiplier) |

Iron golem turns hostile toward a player at reputation -100 or lower, or village popularity -15 or lower.

## Iron Golem Summoning

Villagers summon iron golems under two conditions:

- **Gossip trigger**: 5 villagers must participate in gossip
- **Panic trigger**: 3 villagers panicking simultaneously

In both cases: each participating villager must have slept within the last 20 minutes, and must not have detected an iron golem within 16 blocks in the last 30 seconds.

## Threat Response

Villagers do not fight back. When threatened, they panic and flee. Panic detection ranges vary by threat type:

| Mob | Panic Detection Radius |
|---|---|
| Zombie, Zombie Villager, Drowned, Husk, Zoglin | 8 blocks |
| Vindicator, Zoglin (alternate) | 10 blocks |
| Evoker, Illusioner, Ravager | 12 blocks |
| Pillager | 15 blocks |

During raids, villagers flee to the nearest house with both a door and a bed.

## Zombie Conversion

A zombie attacking a villager converts it to a zombie villager with probability:

- Easy: 0%
- Normal: 50%
- Hard: 100%

Curing a zombie villager (weakness potion + golden apple) restores the villager, gives the curing player +25 reputation and discounted prices for life, and nearby villagers also give discounted prices.

## Breeding

Villagers breed when willing. Willingness requires food in inventory: 3 bread, or 12 carrots/potatoes/beetroots. Farmers automatically harvest and share food with nearby villagers, passively triggering willingness and breeding without player intervention.

Baby villagers jump on beds, fit through 1x1 gaps, and are ignored by illagers until adulthood.

## Lightning

A lightning strike within 3-4 blocks converts a villager into a witch. The resulting witch does not despawn naturally.

## Drops

- No items on normal death
- Farmer villagers drop bone meal (8.5% chance)
- Hero of the Village: each profession throws gifts related to its trade (seeds to chainmail armor range)

## Notes

- A villager that cannot reach its bed will not sleep, which prevents it from contributing to golem gossip the next day.
- Unemployed villagers can be "steered" into a profession by placing the desired job site block near them and removing competing blocks.
- Reputation bonuses from curing stack -- curing multiple zombie villagers in the same village compounds discounts.
- Illagers ignore baby villagers entirely; a baby cannot be targeted during a raid.
