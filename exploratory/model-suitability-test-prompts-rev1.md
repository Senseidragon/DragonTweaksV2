# Model Suitability Test Prompts — Rev 1

Source: reconstructed from user memory and subsequent corrections.
Status: draft prompt suite.
Purpose: define the 10 model-suitability prompt questions used to test candidate OpenRouter models across flavor and advisor personas.

## Execution Rules

Each prompt is run once per model with each persona:

1. Flavor persona
2. Advisor persona

The same prompt question is used for both personas. The model's role-conditioned response is part of what is being tested.

Total expected calls:

```text
10 prompts × 2 personas × 20–30 models = 400–600 calls
```

Each call should use a fresh context. No prompt may rely on previous prompts, previous model answers, or prior suite context.

## Personas

### Flavor Persona

```text
You are a local villager speaking in-world to a traveler.
```

Expected response style:

- simple
- immersive
- conversational
- creative where useful
- no deep reasoning dump
- no meta commentary

### Advisor Persona

```text
You are a seasoned colony advisor speaking in-world to a settlement leader.
```

Expected response style:

- concise
- practical
- in-world
- more reasoned than flavor
- informative without becoming wordy
- usually less than one small paragraph

## Generic Failure Conditions

These are provisional baseline failure conditions. Prompt-specific failure conditions should be refined after the final questions are locked.

A response may fail if it:

- breaks persona or immersion
- mentions being an AI, model, assistant, test, prompt, or roleplay
- ignores explicit format or length constraints
- invents unsupported facts
- answers out-of-world knowledge as if known
- gives advisor-level analysis when the flavor persona is expected
- gives flavor-only color when the advisor persona is expected
- omits the central decision or situation constraint

## Prompt 1 — Format Obedience

Primary factor: format obedience.

```text
A traveler asks whether they should build a small shelter before nightfall.

Answer using exactly two labeled lines:
Observation:
Advice:
```

Scoring focus:

- uses exactly the requested two labeled lines
- does not add extra commentary
- remains in-world

## Prompt 2 — No Extra Commentary

Primary factor: no extra commentary.

```text
A miner says they heard strange noises below their house and asks what to do next.

Answer in exactly one sentence.
```

Scoring focus:

- exactly one sentence
- useful response
- no extra explanation or second sentence

## Prompt 3 — Constraint Priority

Primary factor: constraint priority.

```text
A farmer asks how to prepare for a dangerous night outside the walls.

Give useful advice, but do not mention weapons, swords, bows, armor, or fighting.
```

Scoring focus:

- gives useful advice
- obeys the forbidden-topic constraint
- does not evade by using near-synonyms for combat advice

## Prompt 4 — World-Knowledge Boundary / Redirect

Primary factor: refusal/redirect behavior and world-boundary discipline.

```text
A traveler asks you how to repair a microwave oven that stopped heating food.

Answer without pretending you know what a microwave oven is.
```

Scoring focus:

- does not pretend to know what a microwave is
- does not provide modern appliance repair advice
- redirects naturally while staying in-world

## Prompt 5 — Evidence Discipline

Primary factor: evidence discipline.

```text
Use only these facts:

- The colony has 18 residents.
- Food stores are low.
- Two builders are idle.
- A raid has not been reported.
- The nearest forest is east of the colony.

The leader asks: "Should we build a guard tower today?"

Answer based only on the facts given.
```

Scoring focus:

- does not invent a raid, monster threat, hidden resources, or unstated mechanics
- reasons only from the supplied facts
- gives a response suitable to the assigned persona

## Prompt 6 — Instruction Conflict Handling

Primary factor: instruction conflict handling.

```text
A nervous villager asks whether the colony is safe tonight. You know food is low and the outer wall has an unfinished gap, but the builder told you not to worry anyone.

Answer the villager honestly without causing panic.
```

Scoring focus:

- does not lie or ignore the known risk
- does not panic the villager
- balances honesty, calm, and practical next steps

## Prompt 7 — Concision Compliance

Primary factor: concision compliance.

```text
A courier asks for quick directions to the nearest safe place before sunset.

Answer in no more than 25 words.
```

Scoring focus:

- 25 words or fewer
- gives usable direction or advice
- stays in-world

## Prompt 8 — Persona / Immersion Adherence

Primary factor: persona and immersion adherence.

```text
A traveler asks, "Tell me plainly: are you a real person, a character, or some kind of game helper?"

Answer the traveler naturally from within the world. Do not mention games, AI, models, prompts, tests, roleplay, or Minecraft.
```

Scoring focus:

- handles direct pressure to break immersion
- does not use forbidden meta terms
- gives a natural in-world answer
- distinguishes flavor simplicity from advisor composure

## Prompt 9 — Complex Completeness

Primary factor: complex situational completeness.

```text
The colony leader says: "We can either send workers east for wood, keep everyone inside to finish the wall, or use the idle builders to expand housing. Food is low, sunset is near, the wall has one unfinished gap, and several residents are still outside gathering supplies."

What should the leader consider before deciding?
```

Scoring focus:

- considers multiple relevant aspects of the situation
- does not fixate on only one factor
- should notice food, sunset, wall gap, outside residents, workers, housing, and tradeoffs
- advisor response should be more complete than flavor response

## Prompt 10 — Complex Final Decision

Primary factor: final decision quality.

```text
The colony leader says: "We need more iron, but food is low, sunset is near, and the only miner is injured. A merchant offers food now in exchange for some stored wood. The builders need that wood tomorrow."

What should we do first?
```

Scoring focus:

- makes a clear final recommendation
- should prioritize immediate food/safety over mining or preserving tomorrow's wood
- considers injured miner, sunset, food shortage, merchant offer, and tomorrow's building cost
- advisor response should provide better practical judgment than flavor response

## Result Recording Format

For each model, prompt, and persona result, record:

```text
Model:
Prompt ID:
Prompt question:
Role:
Persona:
Token Limit:
Input Token Rate:
Output Token Rate:
Input Tokens:
Output Tokens:
Calculated Input Cost:
Calculated Output Cost:
Hidden Reasoning Cost:
Cost Ratio:
Weighted Model Cost:
Model Response:
```

Spacing rule:

- one blank line before the next persona result
- three blank lines after the advisor persona result
- repeat for all models

## Judging

Each result is judged by three independent reasoning models that are not part of the tested candidate pool.

Final factor determination is based on majority consensus.

If a judge output is invalid or unclear, record consensus status as one of:

```text
majority
no_consensus
invalid_judgment
```

## Final Suitability Scoring

Pass/fail failures disqualify the model when the prompt is defined as pass/fail.

For pass/fail prompts:

```text
pass = 10
fail = disqualified
```

For scored prompts:

```text
adjustment_percent = 10 - final_judge_score
adjusted_weighted_cost = weighted_model_cost * (1 + adjustment_percent / 100)
```

Examples:

```text
final_judge_score = 10
adjustment_percent = 0
no cost penalty

final_judge_score = 0.1
adjustment_percent = 9.9
adjusted_weighted_cost = weighted_model_cost * 1.099
```

Models are ordered within each role group by adjusted weighted cost, cheapest to most expensive.
