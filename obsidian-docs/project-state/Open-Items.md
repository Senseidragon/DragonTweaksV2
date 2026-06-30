# Open Items

Active deferred work as of branch `advisor-persona-grounding`.

## Advisor / Persona

- **PERSONA_BIO — XP ban** — advisor mentioned "XP" in a sheep-drop answer; Dragon: "mention of xp forbidden." Not implemented.
- **PERSONA_BIO — game-meta language ban** — "in the game", "vanilla Minecraft", "mod" observed repeatedly on knowledge questions. Not implemented.
- **Finding #5** — "see" keyword false-routing into location category
- **Finding #6** — Persona 4th-wall slip

## Model Behavior

- **Sandwich hallucination** — round 2 fabricated a sandwich recipe after round 1 correctly denied it exists. Root cause unknown.
- **Rain verbosity** — model produces overly verbose responses about rain. No code fix path identified.
- **Pronoun reference history gap** — "either of them" doesn't trigger history inclusion. Accepted as out of scope; no fix planned.

## Testing

- **Test 3 (sycophancy)** — partially confirmed (sheep/porkchop honesty held); not formally closed
- **food.md in-game re-test** — confirm prose rewrite improved response quality and removed "tier" language from advisor output

## Infrastructure

- **PreToolUse hook** — Bash path enforcement to close `.gradle` access gap; Dragon's call on timing
- **prickle missing dependency** — Dragon's call

← [[Project-State]]
