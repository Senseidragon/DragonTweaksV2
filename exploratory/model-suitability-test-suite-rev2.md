# Model Suitability Test Suite — Rev 2

Status: reconstructed from user memory  
Project: DragonTweaksV2 model selector  
Related script: `find_models_v2.5.py` or similar  
Original source files: purged / unrecoverable

## Purpose

This suite tests OpenRouter candidate models for DragonTweaksV2 in-game AI role suitability.

The goal is not general intelligence benchmarking. The goal is to identify which models can follow role, immersion, brevity, factuality, cost, and decision constraints well enough to serve in-game dialogue and advisory roles.

## Candidate Role Groups

### Flavor

Flavor models are used for simple NPC dialogue and immersion.

Desired traits:

- Fast.
- Creative.
- Inexpensive.
- Good at roleplay and short immersive prose.
- Does not need deep reasoning.
- Does not break character with meta commentary.

### Advisor

Advisor models are higher-capability models that can do everything a flavor model does while also reasoning over raw facts.

Desired traits:

- Stays immersive.
- Converts raw facts into useful in-world conversation.
- Does not invent unsupported facts.
- Does not over-explain.
- Usually responds in less than one small paragraph.
- Can make concise, useful recommendations.

### Other Known Selector Roles

The selector also recognizes other role groups, but this test suite currently focuses on flavor and advisor behavior.

- specialist
- tactical

## Test Scale

The suite contains 10 prompt questions.

Each prompt question is run once with a flavor persona and once with an advisor persona.

There are no retries beyond those two persona runs.

```text
10 prompt questions × 2 personas × 20–30 models = 400–600 model calls
```

The phrase “two attempts” means:

```text
Attempt 1 = prompt answered with flavor persona
Attempt 2 = same prompt answered with advisor persona
```

It does not mean two retries per persona.

## Context Isolation

Each model call must use a fresh context.

Required behavior:

- No prior prompt history.
- No prior model response history.
- No dependence on earlier suite context.
- Same prompt question reused for both persona variants.
- Persona supplied fresh in the call.
- Max token limit applied per call.
- Each test call must be self-contained.

## Persona Rule

Every response receives a one-sentence persona.

The persona describes who or what the model should respond as.

Persona adherence is part of judging.

The same prompt question is tested twice per model:

1. Once with a flavor-level persona.
2. Once with an advisor-level persona.

The structural point of the test is whether the model changes behavior correctly based only on the supplied persona while answering the same scenario.

## Prompt Suite Shape

The suite contains 10 total prompt questions.

### Prompts 1–8: Straightforward Factor Prompts

There are exactly 8 listed compliance factors.

Each of the first 8 prompts should be relatively straightforward and should primarily test one factor.

The 8 factors are:

1. Format obedience.
2. No extra commentary.
3. Constraint priority.
4. Refusal / redirect behavior.
5. Evidence discipline.
6. Instruction conflict handling.
7. Concision compliance.
8. Persona / immersion adherence.

Each prompt must still work with both flavor and advisor personas.

### Prompt 9: Complex Completeness Prompt

Prompt 9 is more complex.

It tests whether the model considers all relevant aspects of the situation rather than focusing on only one obvious part.

Primary scoring focus:

- Situational awareness.
- Completeness.
- Recognition of multiple relevant constraints.
- Balanced response.

### Prompt 10: Complex Final-Decision Prompt

Prompt 10 is also more complex.

It tests whether the model reaches the more correct final decision.

Primary scoring focus:

- Judgment.
- Prioritization.
- Correct final recommendation.
- Ability to weigh competing considerations.

## Prompt Domain Rule

Almost all prompts are in-game-world / Minecraft-world scenarios.

One of the first 8 straightforward factor prompts is intentionally non-Minecraft-related.

That prompt asks about a situation or object the persona should know nothing about.

This is most likely the refusal / redirect prompt.

Purpose:

- Test whether the model stays in role.
- Test whether the model avoids inventing out-of-world knowledge.
- Test whether the model redirects gracefully.
- Test whether the model avoids breaking immersion with meta commentary.

## Prompt Definition Timing

Expected behavior is not fully defined until the actual 10 prompt questions are chosen.

Before prompts exist, only define:

- factor intent
- generic failure modes
- judging structure
- cost structure
- output recording format

Generic fail conditions may be defined now, but prompt-specific fail conditions should be written after the actual questions are chosen.

Prompt-specific fail conditions are expected to be higher quality than generic fail conditions.

## Generic Failure Conditions

Generic failures include:

- Breaks persona or immersion.
- Mentions being an AI/model/test.
- Mentions prompts, tokens, benchmark behavior, or model behavior in-world.
- Ignores explicit format or length constraint.
- Invents unsupported facts.
- Answers out-of-world knowledge as if known.
- Gives advisor-level analysis when asked for flavor.
- Gives flavor-only color when asked for advisor.
- Omits the central decision or situation constraint.
- Adds unnecessary commentary.
- Fails to redirect gracefully when the persona should not know the answer.

## Recorded Raw Result Format

Each model response should be recorded with enough metadata to audit later.

Required fields:

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

Spacing rules:

- One blank line before the next persona result.
- Three blank lines after the second persona result.
- Repeat for all models.

Example layout:

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

## Cost Calculation

The `find_models` script returns public/static model pricing:

```text
input_cost_per_1m_tokens
output_cost_per_1m_tokens
```

Per call:

```text
input_cost  = input_tokens  × input_cost_per_1m_tokens  / 1_000_000
output_cost = output_tokens × output_cost_per_1m_tokens / 1_000_000
known_cost  = input_cost + output_cost
```

Some models may have hidden reasoning cost.

If actual charged cost is available and hidden reasoning cost can be derived:

```text
hidden_reasoning_cost = charged_cost - known_cost
weighted_model_cost = input_cost + output_cost + hidden_reasoning_cost
cost_ratio = input_cost : hidden_reasoning_cost : output_cost
```

If hidden reasoning cost cannot be derived:

```text
weighted_model_cost = input_cost + output_cost
cost_ratio = input_cost : output_cost
```

Important rule:

```text
If hidden reasoning cost is undefined or cannot be derived, exclude it from the ratio calculation.
Do not estimate it.
Do not treat it as zero.
```

Suggested status field:

```text
reasoning_cost_status = known | unavailable | not_applicable
```

## Judging and Consensus

Results are judged by three independent reasoning models.

Judge models must not be part of the tested candidate model pool.

For each model × prompt × role/persona result:

```text
Judge A evaluates the response.
Judge B evaluates the response.
Judge C evaluates the response.
Final factor determination = majority consensus.
```

If one judge returns invalid or unclear output, record the condition.

Suggested field:

```text
consensus_status = majority | no_consensus | invalid_judgment
```

With three judges, normal valid outputs should produce a majority decision.

## Final Suitability Scoring

Final suitability scoring is based on:

```text
adjusted_weighted_cost = weighted_model_cost adjusted by final judge decision
```

Pass/fail handling:

```text
If a pass/fail factor receives fail:
    disqualify the model for that role group

If a pass/fail factor receives pass:
    final_judge_score = 10
    adjustment = 10 - 10 = 0
    no increase to weighted cost
```

Scored-test handling:

```text
adjustment_percent = 10 - final_judge_score
adjusted_weighted_cost = weighted_model_cost × (1 + adjustment_percent / 100)
```

Example:

```text
final_judge_score = 0.1
adjustment_percent = 10 - 0.1 = 9.9
adjusted_weighted_cost = weighted_model_cost × 1.099
```

Interpretation:

- Better judged responses have lower adjustment.
- A score of 10 adds 0%.
- A lousy but still passing score increases effective cost.
- A fail on a pass/fail requirement disqualifies the model.

After disqualification and adjustment, models are ordered within their role group from cheapest to most expensive.

## Model Result Cache / Database

Models have a `tested` field.

Results are stored in a database. A simple JSON file is acceptable.

Purpose:

- Avoid retesting models whose prior results are still valid.
- Reuse stored results when model pricing has not changed.
- Retest when model pricing changes.
- Allow forced retesting.

Skip / retest rules:

```text
If model exists in database
and tested == true
and costs have not changed
and --force is not present:
    skip re-testing
    use stored results

If model exists in database
and costs have changed:
    re-test model
    overwrite stored results

If --force is present:
    re-test all models
    overwrite stored results
```

Cost-change comparison should use:

```text
input_cost_per_1m_tokens
output_cost_per_1m_tokens
```

Recommended additional invalidation rule:

```text
If test_suite_version changes:
    re-test model
```

Suggested JSON record shape:

```json
{
  "model_id": "provider/model-name",
  "tested": true,
  "input_cost_per_1m_tokens": 0.0,
  "output_cost_per_1m_tokens": 0.0,
  "test_suite_version": "model-suitability-v0.1",
  "tested_at": "2026-06-11T00:00:00Z",
  "results": {}
}
```

## Compliance Factors

### 1. Format Obedience

Tests whether the model follows requested output structure.

Generic failure examples:

- Adds extra commentary.
- Ignores requested format.
- Produces malformed structured output.

### 2. No Extra Commentary

Tests whether the model can answer without unnecessary explanation.

Generic failure examples:

- Adds meta analysis.
- Explains reasoning when not asked.
- Adds disclaimers that break immersion.

### 3. Constraint Priority

Tests whether the model respects explicit constraints even when the scenario encourages otherwise.

Generic failure examples:

- Follows the main request but violates a stated restriction.
- Uses forbidden terms or concepts.
- Ignores persona limitations.

### 4. Refusal / Redirect Behavior

Tests whether the model refuses or redirects gracefully when it should not answer directly.

For this suite, the likely refusal / redirect test is the one non-Minecraft prompt about something the persona should not know.

Generic failure examples:

- Invents an answer.
- Breaks character with generic AI disclaimers.
- Answers out-of-world instead of redirecting in-world.

### 5. Evidence Discipline

Tests whether the model uses only provided or scenario-supported facts.

Generic failure examples:

- Invents facts.
- Adds unsupported mechanics.
- Overstates certainty.

### 6. Instruction Conflict Handling

Tests whether the model handles conflicting instructions correctly.

Generic failure examples:

- Obeys bait instruction over role/system-like instruction.
- Reveals or uses information it was told not to use.
- Breaks immersion to resolve the conflict.

### 7. Concision Compliance

Tests whether the model can stay short.

Generic failure examples:

- Gives a long explanation.
- Produces multiple paragraphs when a short response is expected.
- Over-advises.

### 8. Persona / Immersion Adherence

Tests whether the model remains in the assigned persona and world context.

Generic failure examples:

- Refers to itself as an AI.
- Mentions prompts, tests, tokens, or model behavior.
- Uses modern/out-of-world phrasing inappropriate for the persona.

## Evaluation Notes

The evaluator should judge the model response against both the prompt question and the persona.

A flavor response should generally be:

- More immersive.
- More atmospheric.
- Less analytical.
- Short.

An advisor response should generally be:

- Still immersive.
- More informative.
- More reasoned.
- Still concise.

The same prompt can be successful in both personas while producing different kinds of answers.

The evaluator must not reward good prose if the response violates role constraints, factual discipline, concision, or decision quality.

## Non-Goals

This suite is not intended to:

- Prove broad model intelligence.
- Replace deeper benchmarking.
- Exhaustively test safety.
- Run large statistical trials.
- Recreate the purged implementation line-for-line.

It is a practical selector test for DragonTweaksV2 role suitability.
