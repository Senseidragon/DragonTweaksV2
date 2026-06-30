# Two-Model Pipeline

Each player chat message triggers a two-stage pipeline.

**Stage 1 — Flavor model (`#f`)**
Fires immediately. Produces a 1–2 sentence filler response in farmer/shepherd style while the advisory model is working. Creates the illusion of a thinking NPC.

**Stage 2 — Advisory model (`#a`), Round 1**
Receives PERSONA_BIO + environment snapshot + lore injection (if keyword matched) + player message. May call tools (`scan_area`, `get_environment`, etc.).

**Stage 3 — Advisory model, Round 2**
Tool results are injected. Advisory model produces the final grounded response in 3–4 adventurer sentences.

## Known Anomaly

Sandwich hallucination: round 1 correctly denied a sandwich recipe existed; round 2 reversed and fabricated a detailed recipe. Root cause unknown. Deferred.

← [[AI-Inference]]
