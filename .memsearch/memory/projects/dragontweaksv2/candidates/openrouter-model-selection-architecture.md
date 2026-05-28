**Title:** OpenRouter model selection -- live cost-ranked polling architecture
**Type:** fact
**Intent triggers:** OpenRouter, model selection, model_config.json, role tier, flavor, advisory, specialized, tactical, cost ranking, Python poller, atomic rename, inference backend
**Source/evidence:** model_config.json (Archived\DragonTweaks\model_config.json), Dragon verbal description 2026-05-27
**Rule or fact:** DragonTweaksV2 uses a two-layer inference selection system. A Python script polls the OpenRouter model catalog every 15 minutes, filters by guardrails, ranks by role-weighted cost, and writes the result atomically to model_config.json. The mod reads model_config.json at runtime to select the current best model per NPC role tier.

Architecture layers:

Layer 1 -- Python poller (external, server-side):
- Queries OpenRouter model catalog every 15 minutes
- Filters by guardrails: min_context >= 128000, min_params_b >= 20, excludes free tier, excludes router models
- Ranks candidates by role_weighted_cost = (input_cost * input_weight) + (output_cost * output_weight)
- Writes ranked output to a .tmp file first, then atomically renames to model_config.json to prevent concurrent access errors (mod may be mid-read)
- Runs continuously while server is live; snapshot-only when server is down

Layer 2 -- Mod runtime (Java, reads model_config.json):
- Reads model_config.json to resolve current top candidate per role tier
- Does not contain pricing logic -- all cost intelligence is in the Python layer
- Re-reads model_config.json on a 15-minute timer to stay honest -- if the poller has demoted a model since last check, the mod picks up the new top candidate automatically without a restart or rebuild
- 15-minute granularity matches the poller interval; not event-driven, just periodic re-validation

NPC role tiers and their inference characteristics:
- flavor: Tier 1 -- idle chatter, immersion, no colony data. reasoning_excluded=true. input_weight=0.6, output_weight=0.4.
- advisory: Tier 2 -- colony state, planning, instruction-following. reasoning_required=true. input_weight=0.8, output_weight=0.2.
- specialized: Tier 2 -- Ranch Hand, Scout, procedural/pathfinding-adjacent. reasoning_required=true. input_weight=0.75, output_weight=0.25.
- tactical: Tier 3 -- Military General, threat assessment, multi-step planning. reasoning_required=true. input_weight=0.9, output_weight=0.1.

Design rationale:
- OpenRouter ranks by availability, not cost. The poller provides cost-aware ranking that OpenRouter's own router cannot do with role-aware weighting.
- Atomic rename prevents torn reads when the poller fires mid-read.
- External Python layer means pricing logic can be updated without rebuilding the mod JAR.
- Protects against "attract users" pricing -- models that spike in cost are automatically demoted on the next poll cycle.

Key file: model_config.json -- fields: generated (ISO 8601), guardrails (min_context, min_params_b, excludes_free_tier, excludes_router_models), roles keyed by tier, each with candidates array (model_id, parameter_size, context_window, input_cost_per_1m, output_cost_per_1m, role_weighted_cost_per_1m).
See also: [[OpenRouter-Integration]], [[Dialogue-System]], [[NPC-Roles]]

**Version scope:** DragonTweaksV2 / NeoForge 21.1.x
**Confidence:** 0.95
**Status:** candidate
**Date:** 2026-05-27
