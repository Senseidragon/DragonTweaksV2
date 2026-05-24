# Save After Query Protocol

## Principle

Every external reasoning escalation must be followed by a value extraction step. Useful results must not be discarded.

## Required Behavior

After any external reasoning result:

1. Identify whether the result contains durable value
2. If yes, extract and save as a memory chunk
3. If no, log as a defective query — see `defective-query-log.md`

## Save Candidate Fields

```text
Title:
Type: [fact | rule | pattern | correction | negative finding]
Intent triggers:
Content:
When to apply:
```

## Example

```text
Title: No blocking Minecraft main/server thread
Type: Architecture invariant
Intent triggers: blocking, synchronous, main thread, server thread, GUI, render, warehouse, advisor panel, diagnostics
Content: Nothing may block the Minecraft main/server thread. GUI/render display paths must read cached data only.
When to apply: Any implementation involving panels, diagnostics, warehouse stock, world queries, async tasks, or display paths.
```

## Rule

Claude Code must not discard external reasoning results without first determining whether they contain durable value. If they do, a save candidate must be produced and added to memory.
