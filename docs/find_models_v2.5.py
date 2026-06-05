import os, re, json, sys, time, requests, argparse
from datetime import datetime, timezone
from dotenv import load_dotenv

load_dotenv()
API_KEY = os.getenv("OPENROUTER_API_KEY")
JSON_FILE = "model_config.json"

# --- HARD GUARDRAILS (all tiers) ---
MIN_CONTEXT = 128000
MIN_PARAMS = 20
LIMIT = 20  # candidates per role section
SIZE_PATTERN = re.compile(r'(\d+)b', re.IGNORECASE)

# --- ROLE TIER DEFINITIONS ---
# Each tier defines:
#   input_weight / output_weight: cost ranking formula
#   exclude_reasoning: whether reasoning models are filtered out
#   require_reasoning: whether non-reasoning models are filtered out
#   description: human-readable label
ROLE_TIERS = {
    "flavor": {
        "description": "Tier 1 — Flavor NPCs (idle chatter, immersion, no colony data)",
        "input_weight": 0.60,
        "output_weight": 0.40,
        "exclude_reasoning": True,
        "require_reasoning": False,
    },
    "advisory": {
        "description": "Tier 2 — Advisory / Planner (colony state, reasoning, instruction-following)",
        "input_weight": 0.80,
        "output_weight": 0.20,
        "exclude_reasoning": False,
        "require_reasoning": True,
    },
    "specialized": {
        "description": "Tier 2 — Specialized roles (Ranch Hand, Scout — procedural, pathfinding-adjacent)",
        "input_weight": 0.75,
        "output_weight": 0.25,
        "exclude_reasoning": False,
        "require_reasoning": True,
    },
    "tactical": {
        "description": "Tier 3 — Tactical roles (Military General, threat assessment, multi-step planning)",
        "input_weight": 0.90,
        "output_weight": 0.10,
        "exclude_reasoning": False,
        "require_reasoning": True,
    },
}


def is_reasoning_model(m_id, description):
    # Match full path segments to avoid false positives (e.g. "v0.1", "mistral-7b")
    segments = re.split(r'[/:\-.]', m_id)
    return (
        any(s in ('r1', 'o1', 'o3') for s in segments)
        or any(x in m_id or x in description for x in ('thinking', 'reasoning'))
    )


def fetch_candidates(headers):
    """Fetch all models from OpenRouter and return the raw list."""
    response = requests.get("https://openrouter.ai/api/v1/models", headers=headers)
    response.raise_for_status()
    return response.json().get('data', [])


def apply_hard_filters(model):
    """
    Returns a parsed candidate dict if the model passes hard guardrails,
    or None if it should be excluded from all tiers.
    """
    m_id = model.get('id', '').lower()
    context = model.get('context_length', 0)
    pricing = model.get('pricing', {})
    description = model.get('description', '').lower()
    input_mods = model.get('architecture', {}).get('input_modalities', [])

    # Exclude free-tier and router models
    if m_id.endswith(":free") or "router" in m_id:
        return None

    # Exclude models without parseable parameter size
    size_match = SIZE_PATTERN.search(m_id)
    if not size_match or int(size_match.group(1)) < MIN_PARAMS:
        return None

    # Exclude insufficient context windows
    if context < MIN_CONTEXT:
        return None

    try:
        input_cost = float(pricing.get('prompt', 0)) * 1_000_000
        output_cost = float(pricing.get('completion', 0)) * 1_000_000
    except (ValueError, TypeError):
        return None

    return {
        "model_id": model.get('id'),
        "parameter_size": f"{size_match.group(1)}B",
        "context_window": context,
        "input_cost_per_1m": round(input_cost, 4),
        "output_cost_per_1m": round(output_cost, 4),
        "is_reasoning": is_reasoning_model(m_id, description),
        "has_vision": "image" in input_mods,
        # compliance_score is null until test_compliance.py runs
        "compliance_score": None,
        "compliance_tested": False,
    }


def rank_for_tier(candidates, tier_config):
    """
    Filter and rank a candidate list for a specific role tier.
    Returns top LIMIT models sorted by role-weighted cost.
    """
    iw = tier_config["input_weight"]
    ow = tier_config["output_weight"]

    filtered = []
    for c in candidates:
        if tier_config["exclude_reasoning"] and c["is_reasoning"]:
            continue
        if tier_config["require_reasoning"] and not c["is_reasoning"]:
            continue

        role_cost = round(
            (c["input_cost_per_1m"] * iw) + (c["output_cost_per_1m"] * ow), 4
        )

        entry = dict(c)  # copy — don't mutate the master candidate list
        entry["role_weighted_cost_per_1m"] = role_cost
        # Drop the raw reasoning flag from output (implicit in tier)
        del entry["is_reasoning"]
        filtered.append(entry)

    filtered.sort(key=lambda x: x["role_weighted_cost_per_1m"])
    return filtered[:LIMIT]


def main():
    parser = argparse.ArgumentParser(
        description="DragonTweaks model scraper — role-tiered candidate discovery."
    )
    parser.add_argument(
        "--tiers",
        nargs="+",
        choices=list(ROLE_TIERS.keys()) + ["all"],
        default=["all"],
        help="Which role tiers to include in output. Default: all.",
    )
    parser.add_argument(
        "--daemon",
        action="store_true",
        help="Run continuously, polling every 15 minutes.",
    )
    parser.add_argument(
        "--interval",
        type=int,
        default=900,
        metavar="SECONDS",
        help="Poll interval in seconds when --daemon is set. Default: 900 (15 min).",
    )
    args = parser.parse_args()

    tiers_to_run = list(ROLE_TIERS.keys()) if "all" in args.tiers else args.tiers

    headers = {
        "Authorization": f"Bearer {API_KEY}",
        "HTTP-Referer": "https://github.com/SenseiDragon/dragontweaks",
    }

    while True:
        try:
            print(f"[{datetime.now().strftime('%H:%M:%S')}] Fetching OpenRouter model list...")
            all_models = fetch_candidates(headers)
            print(f"  {len(all_models)} models returned from API.")

            candidates = []
            for model in all_models:
                parsed = apply_hard_filters(model)
                if parsed:
                    candidates.append(parsed)
            print(f"  {len(candidates)} models passed hard filters.")

            output = {
                "generated": datetime.now(timezone.utc).isoformat(),
                "guardrails": {
                    "min_context": MIN_CONTEXT,
                    "min_params_b": MIN_PARAMS,
                    "excludes_free_tier": True,
                    "excludes_router_models": True,
                },
                "roles": {}
            }

            for tier_key in tiers_to_run:
                tier_config = ROLE_TIERS[tier_key]
                ranked = rank_for_tier(candidates, tier_config)
                output["roles"][tier_key] = {
                    "description": tier_config["description"],
                    "input_weight": tier_config["input_weight"],
                    "output_weight": tier_config["output_weight"],
                    "reasoning_required": tier_config["require_reasoning"],
                    "reasoning_excluded": tier_config["exclude_reasoning"],
                    "candidates": ranked,
                }
                print(f"  {tier_key}: {len(ranked)} candidates ranked.")

            tmp = JSON_FILE + ".tmp"
            with open(tmp, "w") as f:
                json.dump(output, f, indent=4)
            os.replace(tmp, JSON_FILE)

            print(f"Done. Written to {JSON_FILE}.")

        except Exception as e:
            print(f"[{datetime.now().strftime('%H:%M:%S')}] Error: {e}", file=sys.stderr)

        if not args.daemon:
            break

        print(f"  Next poll in {args.interval}s. Ctrl+C to stop.\n")
        time.sleep(args.interval)


if __name__ == "__main__":
    main()
