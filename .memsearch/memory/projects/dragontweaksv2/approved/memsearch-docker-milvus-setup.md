---
title: MemSearch Docker/Milvus Setup and Collection Trap
type: operational-fact
domain: projects/dragontweaksv2
status: approved
source: .memsearch/candidates/tentative-approved/2026-05-26-salvage-candidates.md
date: 2026-05-26
intent_triggers:
  - MemSearch
  - Docker
  - Milvus
  - docker compose
  - reindex
  - plugin collection
  - ms_dragontweaksv2_4403422f
  - memsearch_chunks
  - flush
  - manual flush
  - collection mismatch
  - Claude Code recall
  - memsearch index
  - MemSearch recovery
---

# MemSearch Docker/Milvus Setup and Collection Trap

MemSearch is operational on Windows using Docker-hosted Milvus with three containers
(milvus-etcd, milvus-minio, milvus-standalone). The single-container approach failed
and must not be used.

Compose file: `C:\Users\sense\.memsearch\milvus-docker\docker-compose.yml`

## Restart after reboot

```
cd ~/.memsearch/milvus-docker
docker compose up -d
```

## Reindex (correct collection)

```
memsearch index "C:/Users/sense/Desktop/DragonTweaksV2/.memsearch/memory/" --force -c ms_dragontweaksv2_4403422f
```

## Manual flush (required after every index)

```
python -c "from pymilvus import MilvusClient; c=MilvusClient(uri='http://localhost:19530'); c.flush('ms_dragontweaksv2_4403422f')"
```

## Verification search

```
memsearch search "Docker Milvus" --top-k 5 -c ms_dragontweaksv2_4403422f
```

## TRAP — Default collection mismatch

The default CLI collection is `memsearch_chunks`.
The Claude Code plugin collection for this repository is `ms_dragontweaksv2_4403422f`.
Indexing into `memsearch_chunks` makes direct CLI search work but Claude Code recall
still fails. Always pass `-c ms_dragontweaksv2_4403422f` to index and search commands.

## TRAP — Premature success report

MemSearch 0.4.4 may report "Indexed 1 chunks" before stats/search can see the
inserted rows. Manual flush is required before verifying retrieval.

## Stale path warning

The canonical recovery doc is `docs/recovery/MemSearchRecovery.md`.
Do not use the old path `docs/MemSearchRecovery.md`.
