# Defective Query Log

## Principle

Bad queries must be saved with the actual query text, not just the reason they failed.

## Log Entry Format

```text
Timestamp/session:
Original user request:
Defective query text:
Defect category:
Why it was defective:
Reframed replacement query:
Expected durable value from replacement query:
Outcome after retry:
```

## Defect Categories

- Too narrow
- Too vague
- Too verbose
- Wrong target
- Redundant
- One-off/disposable
- Ambiguous entity
- Overfit to wording
- Wrong source
- Missing durable-value target

## Example

```text
Timestamp/session: 2026-05-24 / session-001
Original user request: Why did this archive entry mention planner_dependencies.json?
Defective query text: Why did the May 22 archive say planner_dependencies.json was copied from docs/deprecated?
Defect category: Too narrow
Why it was defective: Only explains one historical sentence. Extracts no reusable project knowledge.
Reframed replacement query: What durable project facts about planner_dependencies.json should future planning tasks retrieve before touching blueprint dependency logic?
Expected durable value: Canonical location, purpose, dependency count, and when to consult it.
Outcome after retry: [pending]
```

## Log

(No entries yet.)
