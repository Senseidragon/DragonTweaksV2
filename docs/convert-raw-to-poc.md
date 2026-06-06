# Convert Raw Wiki Scrape to Minecraft Domain Candidate

## Overview

Starting point: a raw firecrawl JSON file in `docs/<mob>-raw.md`
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

Write the raw JSON output verbatim to `docs/<mob>-raw.md`.

### Why firecrawl cannot do more filtering

The Minecraft wiki wraps each section's content in a `div id="content-collapsible-block-N"` where N is a **page-specific dynamic number** — block 6 is Achievements on the Pillager page but a different number on every other mob page. There is no way to target named sections (Achievements, History, Sounds, Bedrock Edition, etc.) reliably across pages using firecrawl's `excludeTags`.

Additionally, Bedrock Edition paragraphs carry no DOM marker — they are plain paragraphs identified only by their text content. Firecrawl has no mechanism for text-content-based filtering.

The `wikitable` CSS class exists on all wiki tables, but excluding it would remove useful stat tables along with drop tables — not viable.

**Conclusion:** The firecrawl settings above are optimal. All content-level filtering must be done in the cleaning script (Step 2). Do not attempt to expand firecrawl's excludeTags further for semantic filtering.

---

## Step 2 — Clean

Run the cleaning script:

```
PYTHONUTF8=1 python3 scripts/clean-wiki-scrape.py docs/<mob>-raw.md docs/<mob>-clean.md
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

## Step 5 — Write the candidate file

File: `.memsearch/memory/domains/minecraft/candidates/extracted/<Mob>.md`

This is the final output. There is no intermediate PoC file. Write directly to the candidates folder.

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

## Step 6 — Final evaluation

Read the candidate file against the clean file in full. Ask:

> Is there anything remaining in the clean file with in-character value for a reasoning advisor that is not in the candidate?

Candidates for inclusion: behavioral edge cases, spawn timing details, tactical nuances, consequences the advisor would warn about.
Candidates to leave out: anything meta, mechanical, or player-UI-facing.

Do not iterate after this step. One evaluation pass, then the file is done.

---

## Output summary

| File | Purpose | Target size |
|------|---------|-------------|
| `docs/<mob>-raw.md` | Verbatim firecrawl output | ~100k chars |
| `docs/<mob>-clean.md` | Cleaned, BE-stripped, noise-stripped | ~6–12k chars |
| `.memsearch/memory/domains/minecraft/candidates/extracted/<Mob>.md` | Advisor fact block, validation-ready candidate | ~2,000–5,000 chars |
