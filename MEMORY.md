# MEMORY.md

This file is a compact routing index for durable project memory. It is not a task queue, validation procedure, or permission grant. Follow linked memory nodes only when they are relevant to the current user request or explicitly invoked by project procedure.

## Operating Lessons

- [Task pre-flight checklist](feedback_task_checklist.md) — before editing, identify intended files, risky areas, and any conflict with the main-thread invariant.
- [No fake verification output](feedback_no_fake_verification.md) — never hardcode success signals; if verification is incomplete or impossible, say so.
- [No redundant file reads](feedback_no_redundant_reads.md) — do not re-read files already in context unless Dragon asks for a refresh.
- [Document review protocol](feedback_document_review_protocol.md) — when asked to review, read and flag observations first; ask before writing memory candidates.
- [Guardrail boundary handling](memory-links/feedback_block_bypass_prohibited.md) — treat permission blocks as intentional boundaries unless Dragon explicitly authorizes otherwise.
- [Known-good regression triage](memory-links/process_known_good_regression_triage.md) — for regressions, compare against the working version first; changed code is guilty until cleared.
- [Unsupported external-cause claims](memory-links/feedback_known_bug_evidence.md) — claims such as “known bug,” “mod conflict,” or “JVM issue” require evidence and a V1/V2 explanation.

## Architecture and Runtime Invariants

- [Advisor system core design](project_advisor_system.md) — immersion-first NPC companion, two-model pipeline, shared environmental context, world boundary rule, MineColonies live state, BYOK, and short per-player memory.
- [Advisor NPC persona definitions](project_advisor_personas.md) — advisory `#a` is a seasoned adventurer in 3–4 sentences; flavor `#f` is farmer/shepherd style in 1–2 sentences.
- [Main-thread nonblocking invariant](memory-links/architecture_main_thread_nonblocking.md) — Minecraft main/client/render thread must never block; LLM/OpenRouter work must be failure-isolated from gameplay/rendering.
- [Model selection authorization](feedback_model_selection.md) — do not choose a model without Dragon’s authorization; do not default above Dragon’s cost ceiling.

## Memory System and Domain Knowledge

- [MemSearch refresh before CLI tests](feedback_memsearch_refresh.md) — run the refresh procedure before CLI tests and use collection `ms_dragontweaksv2_4403422f`.
- [Domain pack plan](project_domain_plan.md) — NeoForge, Minecraft, MineColonies, and other domains are separate domain packs.
- [Shared domain layout](project_shared_domain_layout.md) — planned shared layout for domains and stubs outside individual repos.
- [No javap stubs as memory source](feedback_no_javap_stubs.md) — javap stubs are not authoritative memory sources; use cloned source where available.
- [Wikilinks protocol for memory source fields](feedback_wikilinks_protocol.md) — source fields use explicit wikilinks such as `[[local/file]]`, `[[none]]`, `[[url-to-specific-file]]`, or `[[other-entry.md]]`.
- [Minecraft domain rebuild](project_minecraft_domain_rebuild.md) — legacy Minecraft wiki-derived approved entries are suspect; rebuild through the raw → cleaned → advisor → validation pipeline.
- [Web memory ingestion pipeline](memory-links/process_web_memory_ingestion_pipeline.md) — web/wiki data must be quarantined as raw, cleaned, distilled to advisor-grade memory, then validated before promotion.
- [Domain pack bulk audit cost](feedback_domain_pack_audit_cost.md) — large audits are context-expensive; warn Dragon before bulk operations.
- [Approved memory finalization](memory-links/process_approved_memory_finalization.md) — `tentative-approved` is finalization-ready; SessionStart may final-validate/promote only that state.
- 
## Project State

- [MineColonies wired](project_minecolonies.md) — MineColonies compileOnly dependency and local source baseline.
- [Goat horn sound patches](project_sound_patches.md) — goat horn and screaming goat sound patch state.
- [Compliance testing architecture](project_compliance_architecture.md) — compliance testing architecture is separate from finder logic; DB-backed by model ID.
- [Open items](project_open_items.md) — active unresolved items, including Windows encoding workaround and runtime domain retrieval limitations.

## Scope and Safety

- [Stay in project folder](feedback_stay_in_project.md) — do not operate outside DragonTweaksV2 without explicit approval; `%USERPROFILE%\.claude` is off-limits.
- [Cost hook estimates are overstated](feedback_cost_estimates.md) — hook dollar estimates may be inflated; verify actual costs from billing data when needed.
