---
tags:
  - dev-log
  - sessions
  - history
---

# Dev Log

Chronological session timeline. Raw session notes in `.memsearch/memory/` as dated files.

---

## 2026-05-24

**Focus:** Initial framework decisions.

- MineColonies integration deferred pending framework decisions (see [[MineColonies-API]])
- Domain pack plan: NeoForge and Minecraft as separate domains; Minecraft deferred until training data gaps appear
- Shared domain layout designed: domains + stubs to move outside repos, symlinked in, shared Milvus collection per domain

---

## 2026-05-25

**Focus:** Memory system and NeoForge domain pack.

- NeoForge domain memory pack initiated
- Framework rules memory seeded (`seed-framework-rules.md`)
- MemSearch collection established: `ms_dragontweaksv2_4403422f`

---

## 2026-05-26

**Focus:** Domain memory expansion and candidate salvage.

- NeoForge domain approved memory expanded (20+ chunks)
- Salvage candidates batch created: `.memsearch/candidates/inbox/2026-05-26-salvage-candidates.md`
- Framework memory corrections: candidate routing rule, pipeline correction, LLM coding behavior guidelines

---

## 2026-05-27

**Focus:** Checkpoint, stubs index, onboarding doc, Obsidian vault.

- Committed checkpoint: NeoForge domain memory, framework rules, stubs index
- `docs/STUB_INDEX.md` built (51.9 KB — use index to route; do not bulk-load)
- `docs/ONBOARDING.md` created
- Obsidian vault generated: `obsidian-docs/` (this session)

---

## Next Steps

- Begin first gameplay feature (NPC roles, research tree, or blueprint packs)
- Decide MineColonies integration approach
- Build MineColonies domain memory pack when integration begins
