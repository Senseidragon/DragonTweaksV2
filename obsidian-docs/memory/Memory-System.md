---
tags:
  - memory
  - memsearch
  - framework
  - ai-ops
source: docs/active/memory-system-architecture.md, CLAUDE.md
---

# Memory System

AI-assisted knowledge management built on MemSearch (Milvus vector database). Separates knowledge into three layers so domain facts can be reused across projects.

## Key Details

### Three Layers

| Layer | Scope | Path |
|-------|-------|------|
| **Framework** | Portable ops rules | `.memsearch/memory/framework/` |
| **Domain packs** | Reusable API knowledge (NeoForge, MineColonies, etc.) | `.memsearch/memory/domains/<domain>/` |
| **Project** | Local facts, constraints, decisions | `.memsearch/memory/projects/dragontweaksv2/` |

### Querying
```bash
memsearch search "<query>" --top-k 5 -c ms_dragontweaksv2_4403422f
```
Run `scripts/memsearch-refresh.ps1` before CLI tests.

### Candidate Pipeline
```
External source -> Raw capture -> Candidate file -> Validation -> Approved
```
- Confidence >= 0.85: auto-promote
- Confidence < 0.85: human review patch

### NeoForge Domain Pack
20+ approved chunks: `DeferredRegister`, `DeferredHolder`, capability APIs, event hierarchies, `IAttachmentHolder`, `IBlockEntityExtension`, `IItemExtension`.

### Encoding Rule
ASCII-only in all memory files (Windows PowerShell 5.1 compatibility).

## Relationships
- [[Conversation-History]] — per-NPC conversation store, a runtime application of persistence principles
- [[DragonTweaksV2-Main]] — session start triggers candidate queue processing
