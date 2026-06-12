# Session Resume — Minecraft Lore Pipeline

## What we are doing

Building a library of advisor-artifact files for the DragonTweaksV2 NPC advisor system. Each file is a distilled, in-world-knowledge-appropriate summary of one Minecraft mob, used as frontloaded prompt data sent to an LLM that answers player questions in 4–5 sentences.

Files live in `docs/minecraft-lore/` organized by mob category:
- `passive/` — 26 files, complete
- `neutral/` — 13 files, complete and reviewed
- `hostile/` — not started
- `utility/` — not started (Iron Golem, Snow Golem)
- `npcs/` — not started (Villager, Wandering Trader)

## Current status

Neutral mobs are done. All 13 files have been written, reviewed against their clean source files, corrected, and scraps logs added to the clean files.

**Passive mobs need review.** 26 files were written in an earlier session before the scraps log process existed and before the review checklist was added to `docs/convert-raw-to-poc.md`. Those files have never been reviewed against their source. Several clean files may also be truncated (same root cause as polar_bear and spider: drop table toggle links cause the cleaning script to terminate early). Raw files exist at `docs/<mob>-raw.md` for most of them.

## Next task

Review the passive mob advisor files against their clean sources. Start by checking which clean files are truncated:

```bash
wc -l docs/*-clean.md | sort -n
```

Short files (under ~80 lines) are suspect. For any truncated clean file, pull the behavior section from the raw file directly (search for `'Behavior\n'` in the raw). Then compare each advisor file against the clean/raw source using the Step 6 checklist in `docs/convert-raw-to-poc.md`.

Passive mob list: allay, armadillo, axolotl, bat, camel, cat, chicken, cod, cow, donkey, frog, glow_squid, horse, mooshroom, mule, ocelot, parrot, pig, rabbit, salmon, sheep, sniffer, squid, tadpole, tropical_fish, turtle.

After passives: start hostile mobs from scratch (scrape → clean → write → review in one pass).

## Key pipeline files

- `docs/convert-raw-to-poc.md` — full scrape/clean/write/review procedure including the advisor framing, review checklist, and scraps log requirement
- `docs/minecraft-lore/neutral/` — reference these for format and tone
- `scripts/clean-wiki-scrape.py` — cleaning script; always prefix with `PYTHONUTF8=1`

## Known cleaning script bug

The Decimal/Fraction/Distribution/Expectation toggle links in wiki drop tables cause the cleaning script to terminate early, producing truncated clean files. When this happens, pull the behavior and drops content directly from the raw file. The raw files are large JSON-wrapped markdown — search for the section by text, not by line number.

## Format reminder

All advisor files use this frontmatter:
```yaml
---
topic: <MobName>
type: advisor-artifact
source: "[[https://minecraft.wiki/w/<MobName>]]"
scraped: <date>
version: 1.21.1
pipeline_stage: advisor-artifact
---
```

Source field must be Obsidian wikilink format `"[[url]]"` — plain URLs break Obsidian import.
