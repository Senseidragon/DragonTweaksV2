# Convert Raw Wiki Scrape to Minecraft Domain Candidate

## Overview

Starting point: a raw firecrawl JSON file in `docs/lore-pipeline/<mob>-raw.md`
End point: a distilled advisor fact block at `.memsearch/memory/domains/minecraft/candidates/extracted/<Mob>.mc`, fully ready for validation

---

## Step 1 — Scrape

Use firecrawl with these settings to minimize noise at the source:

```
url: https://minecraft.wiki/w/<MobName>
formats: ["markdown"]
onlyMainContent: true
excludeTags: ["img", "script", "style", "nav", "footer", "table.navbox", ".mw-editsection", ".reference", ".toc"]
removeBase64Images: true
```

Write the raw JSON output verbatim to `docs/lore-pipeline/<mob>-raw.md`.

### Follow relevant links — mandatory

A single-page scrape captures only what the wiki says about this mob *from its own perspective*. Cross-mob utility facts — that this mob is hunted by X, lures Y, or is used as currency/food/bait by Z — live on the other mob's page, not here.

After scraping the main page, identify and also scrape the pages of any mobs or items that:

1. **Are named as predators of this mob** — e.g. the Axolotl page lists every mob it hunts; those prey mobs' pages should note the threat
2. **Use this mob as food, lure, bait, or currency** — e.g. axolotls are bred with buckets of tropical fish; that fact lives on the Axolotl page, not the Tropical Fish page
3. **Are explicitly cross-referenced in the main page's Behavior or See Also sections** as ecological partners or competitors

For each related page scraped, extract only the sentences or paragraphs that refer back to the current mob. Append these as a `## Cross-References` section at the bottom of `docs/lore-pipeline/<mob>-raw.md` before proceeding to Step 2.

The clean file and advisor artifact must reflect this combined data — not just the main page alone.

### Why firecrawl cannot do more filtering

The Minecraft wiki wraps each section's content in a `div id="content-collapsible-block-N"` where N is a **page-specific dynamic number** — block 6 is Achievements on the Pillager page but a different number on every other mob page. There is no way to target named sections (Achievements, History, Sounds, Bedrock Edition, etc.) reliably across pages using firecrawl's `excludeTags`.

Additionally, Bedrock Edition paragraphs carry no DOM marker — they are plain paragraphs identified only by their text content. Firecrawl has no mechanism for text-content-based filtering.

The `wikitable` CSS class exists on all wiki tables, but excluding it would remove useful stat tables along with drop tables — not viable.

**Conclusion:** The firecrawl settings above are optimal. All content-level filtering must be done in the cleaning script (Step 2). Do not attempt to expand firecrawl's excludeTags further for semantic filtering.

---

## Step 2 — Clean

Run the cleaning script:

```
PYTHONUTF8=1 python3 scripts/clean-wiki-scrape.py docs/lore-pipeline/<mob>-raw.md docs/lore-pipeline/<mob>-clean.md
```

The script handles:
- Unwrapping the firecrawl JSON envelope; reconstructing YAML frontmatter from metadata
- Removing image links (`![alt](url)` and `[](url)` forms)
- De-linking all markdown links `[text](url)` → `text`
- Stripping embedded JSON blobs
- Removing all Bedrock Edition content (paragraphs, subsections, inline qualifiers)
- Stripping "In _Java Edition_," prefixes (redundant once BE is gone)
- Removing spawn egg references
- Removing NBSP and invisible unicode
- Removing "Jump up to" / `↑` footnote links
- Removing XP table lines
- Collapsing drop tables to a single item list line
- Removing wave bonus/increasing data
- Fixing split italic markers and orphaned continuation lines
- Stripping entire noise sections: History, Achievements, Advancements, Data values, Gallery, Trivia, References, External links, Issues, Sounds, Notes
- Stripping wiki navigation chrome: language lists, jump links, categories, navigation menu
- Removing `[hide]`/`[show]` toggle markers, maintenance notices, `_upcoming:` and `[verify]` markers
- Collapsing excessive blank lines

**Note:** Always prefix with `PYTHONUTF8=1` — wiki content contains emoji that break Windows cp1252 output.

**UTF-8 / ASCII compliance (mandatory):** All advisor-artifact files written to `docs/minecraft-lore/` must contain only ASCII characters (codepoints 0–127). Before writing any advisor file, replace all typographic characters with ASCII equivalents:

| Character | Replace with |
|-----------|-------------|
| — (em dash, U+2014) | `--` |
| – (en dash, U+2013) | `-` |
| × (multiplication, U+00D7) | `x` |
| − (minus sign, U+2212) | `-` |
| ' ' (curly quotes, U+2018/2019) | `'` |
| " " (curly quotes, U+201C/201D) | `"` |
| &nbsp; (non-breaking space, U+00A0) | ` ` |
| … (ellipsis, U+2026) | `...` |

Any remaining non-ASCII character must be removed or replaced before the file is written. All Python commands touching these files must use `PYTHONUTF8=1` prefix and `encoding='utf-8'` on all file open calls.

---

## Step 3 — Read and evaluate the clean file

Read the clean file in full before writing anything. Do not skim.

Identify:
- What sections are present (spawning, behavior, hostility, raids, drops, etc.)
- What has in-character value for an advisor (tactical, colony-relevant, behavioral nuance)

**Watch for stat ambiguity.** The wiki sometimes states the same mechanic in two different places with apparently contradictory numbers. When this happens, identify which statement is the behavioral description (authoritative) and which is a summary or garbled artifact of the cleaning pass. Example: pillager attack range was listed as both "8 blocks" (reload trigger distance) and "64 blocks" (pursuit/fire range) — the behavioral description at line 98 was correct; the summary at line 74 was garbled.

---

## Step 4 — Check for existing approved entry

Before writing anything, check whether an approved entry already exists:

```bash
ls .memsearch/memory/domains/minecraft/approved/<mob>.md
```

- **Exists** → include `supersedes: <mob>.md` in the frontmatter. The validator will tombstone the old entry on promotion.
- **Does not exist** → omit `supersedes`. This is a new canonical entry.

---

---

## Step 4.1 — Insert metadata

### Frontmatter (required)

```yaml
---
title: Minecraft — <Mob> (<brief role descriptor>)
domain: minecraft
fact: <one-line retrieval key — stats, spawn, primary threat, most useful behavioral fact>
confidence: 0.90
usefulness: high | medium | low
supersedes: <mob>.md        # only if approved entry exists — omit otherwise
authority: authoritative
source_url: https://minecraft.wiki/w/<MobName>
source_version: "1.21.1"
source_type: official_wiki
format: distilled-advisor-block
---
```

`fact` is the most important field — it is what memsearch uses to surface this entry. Write it as a dense single sentence covering: health, key behavior, spawn trigger, and the fact most likely to matter to a colony advisor.

`confidence` and `usefulness` are your judgment. `authority: authoritative` is correct for direct wiki-sourced content.

The validator pipeline adds `validator_version`, `validator_hash`, `validated_at`, and `approval_route` on promotion. Do not add those here.

### Body content

Include:
- One-line role description (threat tier, colony relevance)
- Full stats (health, attack by difficulty, detection/pursuit range, notable durability)
- Spawn conditions with timing and thresholds
- Threat assessment (who it targets, alert/aggro mechanics, exploitable behaviors)
- Raid or event mechanics with colony-specific consequences
- Awareness/detection edge cases (invisibility, creative mode, etc.)
- Tactical notes (reload windows, persistent spawn sources, counters, scavenging behaviors, unusual ammo)
- Drops (functional only — no probability tables)

Strip:
- All Bedrock Edition content
- XP and experience tables
- Achievements and advancements
- History/changelog tables
- Gallery, media, concept art
- References and bug tracker links
- NBT/entity format data
- Reverted behaviors (check history before including)
- Trivia (design origin stories, etc.)

---

## Step 5 — Write the candidate file

File: `.memsearch/memory/domains/minecraft/candidates/extracted/<Mob>.md`

This is the final output. There is no intermediate PoC file. Write directly to the candidates folder.

---

## Step 6 — Final evaluation

Read the candidate file against the clean file in full using the following framing:

> An advisor is an experienced explorer/adventurer — they know what a mob does from having observed or fought it. They know behavioral quirks, surprising interactions, and things that would get someone killed or help someone survive. They do not know internal numbers (attack values), game mechanics (mobGriefing flags), or wiki metadata. When in doubt, ask: "Would a seasoned traveler who'd seen this mob dozens of times know this?"

### Review checklist

Scan the clean file explicitly for each of these categories before closing:

- **Inter-mob interactions** — does this mob trigger, threaten, or react to other specific mobs?
- **Behavioral state switches** — conditions that change the mob from neutral to hostile, passive to active, or alter its targeting
- **"Gotcha" facts** — things that contradict what a player might assume (e.g. "does NOT retaliate when hit", "cake cannot be used for breeding")
- **Exploitable behaviors** — paralysis windows, aggro radius, forgiveness timers, sight-line breaks, trust mechanics
- **Spawn edge cases** — unusual dimensions, dimension-specific behaviors, biome nuances beyond the basic spawn table

### Scraps log (mandatory)

After the checklist, write a scraps comment at the bottom of the clean file before closing it:

```
<!-- scraps: wolf teleport constraints (meta), wolf taming bone RNG (meta), wolf/llama flee radius (useful — added) -->
```

List every item you explicitly considered and either added or rejected, with a one-word reason (`meta`, `BE-only`, `redundant`, `trivial`, `added`). If you cannot name anything you considered, you have not read the clean file carefully enough.

The scraps comment goes on the **clean file**, not the advisor file. It is a working artifact, not permanent content.

Do not iterate after this step. One evaluation pass, then the file is done.

---

## Output summary

| File | Purpose | Target size |
|------|---------|-------------|
| `docs/lore-pipeline/<mob>-raw.md` | Verbatim firecrawl output | ~100k chars |
| `docs/lore-pipeline/<mob>-clean.md` | Cleaned, BE-stripped, noise-stripped | ~6–12k chars |
| `.memsearch/memory/domains/minecraft/candidates/extracted/<Mob>.md` | Advisor fact block, validation-ready candidate | ~2,000–5,000 chars |
