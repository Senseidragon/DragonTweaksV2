# Query Quality Rules

## Principle

External reasoning is only justified if it produces durable value.

## Durable Value Requirements

A query to external reasoning must produce at least one of:

1. A durable reusable fact
2. A correction or update to existing memory
3. A reusable pattern or rule
4. A useful negative finding
5. A defective query record showing the query was poor and needs improvement

If none of these exist, the escalation failed.

## Query Construction Rules

- Queries must target reusable knowledge, not one-off explanations
- Queries must be scoped broadly enough to extract generalizable facts
- Queries must not be overfit to specific wording, dates, or narrow context
- Queries must identify a clear expected durable value before being issued

## Defect Categories

| Category | Description |
|---|---|
| Too narrow | Only explains one specific instance |
| Too vague | No clear retrieval target |
| Too verbose | Excess detail obscures intent |
| Wrong target | Aimed at wrong source or system |
| Redundant | Already answered in memory |
| One-off/disposable | Answer has no reuse value |
| Ambiguous entity | Target is unclear |
| Overfit to wording | Too tied to exact phrasing |
| Wrong source | Correct question, wrong system |
| Missing durable-value target | No reusable output defined |

## Rule

If a query produces no durable value, it must be logged as defective. See `defective-query-log.md`.
