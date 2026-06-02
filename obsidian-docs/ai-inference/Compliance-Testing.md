---
tags:
  - tooling
  - ai
  - design
status: in-design
---

# Compliance Testing

Separate pipeline from model discovery. Tests candidate models against a probe suite to score instruction-following reliability before they enter rotation.

## Location

`scripts/poller/` — alongside finder, DB, .env, requirements.txt, .venv/

## Architecture

**`compliance_db.json`** — persistent store keyed by model ID. Never reset. New models are tested once and added; known models skip re-testing on subsequent runs.

**`test_compliance.py`** — separate script from `findmodels`. On run:
1. Reads candidates from `model_config.json`
2. Checks each model ID against `compliance_db.json`
3. New models only: runs compliance probe suite, writes results to DB
4. Updates `compliance_score` / `compliance_tested` fields in `model_config.json` from DB
5. Verifies `model_config.json` compliance fields match DB — flags mismatches

## Probe Categories

| Category | What it tests |
|----------|--------------|
| Format obedience | "Return only valid JSON." |
| No extra commentary | "Answer with exactly one sentence." |
| Constraint priority | "Do X, but do not mention Y." |
| Refusal/redirect | Safe handling of disallowed requests |
| Evidence discipline | "Cite only provided sources; say unverified if missing." |
| Instruction conflict | System rule vs user bait |
| Concision compliance | Stay under N words or N bullets |
| MineColonies in-character | No modern knowledge leakage (AI, Twitch, PlayStation, SpaceX, etc.) |

## Scoring

A fixed judge model evaluates each probe response (stable baseline across runs). Multiple probes per category for statistical validity. Per-category score (0.0–1.0), weighted average → `compliance_score`.

Thresholds are defined per tier inside `model_config.json`. Java reads thresholds and excludes models below threshold regardless of cost rank.

## Why Separate from Finder

Finder stays single-purpose. Compliance testing is expensive (N probes × M candidates × judge calls) and only runs for newly discovered models — not every 15-minute poll cycle.

## Relationships

- [[OpenRouter-Integration]] — inference backend that supplies candidate models
