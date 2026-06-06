# Session Briefing — Read This First

Date written: 2026-06-06

---

## What was accomplished this session

### Wiki pipeline — fully proven end-to-end

`scripts/clean-wiki-scrape.py` is production-ready. It takes a raw firecrawl JSON dump and produces a clean, noise-stripped markdown file ready for distillation. Key numbers: ~100k raw → ~6–12k clean (pillager: 6k, fox: 11k).

The full pipeline is documented in `docs/convert-raw-to-poc.md`. Steps:
1. Firecrawl scrape → `docs/<mob>-raw.md`
2. `PYTHONUTF8=1 python3 scripts/clean-wiki-scrape.py docs/<mob>-raw.md docs/<mob>-clean.md`
3. Read clean file in full — watch for stat ambiguity (two contradictory numbers = one is garbled)
4. Check if `approved/<mob>.md` exists — if yes, include `supersedes: <mob>.md` in frontmatter
5. Write candidate to `.memsearch/memory/domains/minecraft/candidates/extracted/<Mob>.md`
6. Final evaluation pass against clean file
7. Dragon runs pwsh copy to promote: `Copy-Item candidates/extracted/<Mob>.md approved/<mob>.md`
8. Dragon runs memsearch index: `$env:PYTHONUTF8=1; memsearch index approved/<mob>.md --force -c ms_dragontweaksv2_4403422f`

**AI cannot write to `approved/` directly — permission denied. Dragon must do the copy and index steps.**

### Minecraft domain — two entries rebuilt

| Entry | Status | Notes |
|-------|--------|-------|
| `pillager.md` | Approved + indexed | Superseded old firecrawl trivia snapshot; old entry tombstoned to `deprecated/` |
| `fox.md` | Approved + indexed | New entry, no prior approved version existed |

### All old bad candidates purged

54 old firecrawl snapshot candidates in `candidates/extracted/` were deleted. They were raw trivia sections — useless. The domain will be rebuilt mob by mob using the pipeline.

### Prompt tuning — two-tier response tested

**Advisory model:** `openai/gpt-oss-120b`
- Persona: seasoned adventurer, hard-earned authority, in-world language only
- Length: 3–4 sentences
- Send: full advisor fact block content as context
- System prompt key: "Never mention game mechanics, rules, or technical terms. Respond in exactly 3 to 4 sentences. No lists."

**Flavor model:** `liquid/lfm-2-24b-a2b`
- Persona: simple farmer or shepherd, earthy plain speech
- Length: 1–2 sentences
- Send: stripped-down facts only (what it hunts, behavior, night/day, loyalty)
- System prompt key: "Respond in exactly 1 to 2 sentences. No lists."

Both tested successfully against fox. Responses were immersive and appropriately tiered.

---

## What's next

### Minecraft domain rebuild

All remaining `approved/` entries are bad firecrawl snapshots. Rebuild them mob by mob using the pipeline. Priority order is Dragon's call — start with whatever is most relevant to the advisor system (hostile mobs, colony threats first).

Entries still needing rebuild (from `approved/`):
- allay, ancient-city, armadillo, axolotl, bastion-remnant, bee, blaze, bogged, breeze, creeper, drowned, easter-eggs, elder-guardian, end-city, end-poem, ender-dragon, enderman, evoker, frog, ghast, guardian, herobrine, hoglin, illusioner, iron-golem, nether-fortress, ocean-monument, phantom, piglin, piglin-brute, ravager, ruined-portal, shulker, skeleton, sniffer, snow-golem, splash, strider, stronghold, trial-chambers, vex, villager, vindicator, warden, witch, wither, wither-skeleton, wolf, woodland-mansion, zombie, zombie-villager, zombified-piglin

**Scope rule:** Only `domains/minecraft/` gets rebuilt. Do NOT touch `domains/minecolonies/` or `domains/neoforge/` — those are Java-source developer memory.

### Advisor prompt wiring

The `#a` and `#f` chat commands currently send raw player input with no context. The next engineering step is wiring the memsearch retrieval + prompt frontloading into `OpenRouterService.query()` — persona + retrieved facts + player question.

---

## Key files to know

| File | Purpose |
|------|---------|
| `docs/convert-raw-to-poc.md` | Full pipeline documentation — the authoritative process doc |
| `scripts/clean-wiki-scrape.py` | Wiki cleaning script |
| `.memsearch/memory/domains/minecraft/approved/` | Live indexed domain entries |
| `.memsearch/memory/domains/minecraft/deprecated/` | Tombstoned old entries |
| `docs/model_config.json` | Model candidates by role — cheapest first |
| `src/.../openrouter/ChatCommandHandler.java` | `#a`/`#f` prefix handling |
| `src/.../openrouter/OpenRouterService.java` | API call logic |
