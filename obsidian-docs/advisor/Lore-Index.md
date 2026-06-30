# Lore Index

`LoreIndex` scans `docs/minecolonies-lore/` and `docs/minecraft-lore/` at startup and builds a keyword → document map. When a player message matches a keyword, the corresponding lore doc is injected into the round-1 prompt.

- Matching is case-insensitive substring; typo-tolerant in practice (confirmed: "sht dues my colony need for food" matched `food`)
- Entry count is not tracked here — it changes as files are added and will drift if noted as a snapshot

## minecraft-lore coverage

87 files across 7 categories. See `docs/minecraft-lore/MINECRAFT_LORE_INDEX.md` for the full file table.

| Category | Count | Contents |
|----------|-------|----------|
| `hostile/` | 34 | Zombie, Skeleton, Creeper, Warden, Blaze, Ghast, etc. |
| `passive/` | 26 | Sheep, Cow, Pig, Chicken, Horse, Axolotl, etc. |
| `neutral/` | 13 | Wolf, Bee, Enderman, Piglin, Fox, etc. |
| `structures/` | 9 | Village, Stronghold, Bastion, Ancient City, etc. |
| `npc/` | 2 | Villager, Wandering Trader |
| `utility/` | 2 | Iron Golem, Snow Golem |
| `dimensions/` | 1 | Nether |

**Two frontmatter schemas are in use** — do not mix them:
- Mob files (`hostile/`, `passive/`, `neutral/`, `npc/`, `utility/`): `advisor-artifact` schema with `topic/type/pipeline_stage` fields; files use `##` headers and bullet lists
- Structure/dimension files: `distilled-advisor-block` schema with `fact/confidence/usefulness` fields

## minecolonies-lore coverage

65 files in plain prose format. See [[Lore-Content]] for format conventions and file status.

| Category | Count | Obsidian Page |
|----------|-------|---------------|
| buildings | 51 | [[Lore-Buildings]] |
| systems | 9 | [[Lore-Systems]] |
| needs | 4 | [[Lore-Needs]] |
| items | 1 | [[Lore-Items]] |

← [[Advisor-System]]
