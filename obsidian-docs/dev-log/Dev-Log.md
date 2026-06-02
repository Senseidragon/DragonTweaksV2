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

## 2026-06-02

**Focus:** MineColonies integration and domain memory expansion.

- **MineColonies wired:** Added as `compileOnly` v1.1.1299 dependency in `build.gradle`
- **Wiki evaluation:** MineColonies Wiki cloned locally; 65 wiki pages extracted (51 buildings, 9 systems, 4 needs, 1 items)
- **Candidate extraction:** `scripts/extract-wiki-candidates.ps1` written for batch mdoc→candidate processing
- **Domain promotion:** 65 wiki-derived candidates validated and promoted to approved via `scripts/memory_pipeline.py`
- **Memory collection:** `ms_dragontweaksv2_4403422f` now contains 1110+ entries across NeoForge, MineColonies (API + gameplay), framework, and project domains
- **Dependency cleanup:** All JAR files removed from git tracking; `libs/*.jar` now gitignored
- **Advisor system:** Design phase complete (immersion-first companion, book=scout, book-and-quill=colony advisor, BYOK, ~20-turn per-player memory)
- **Compliance testing:** Architecture exists in `scripts/poller/` (separate from finder, DB-backed, model-specific thresholds)

---

## Next Steps

- Implement advisor system core (player state management, message queue)
- Begin first gameplay feature integration (NPC roles, research tree sync, or blueprint packs)
- Extend MineColonies domain memory with gameplay integration patterns as features develop
