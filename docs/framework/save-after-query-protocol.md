# Candidate After Query Protocol

(Formerly "Save After Query Protocol" -- renamed to reflect candidate-only capture behavior.)

## Principle

Every external reasoning escalation must be followed by a value extraction step. Useful results must not be discarded. However, Claude Code must not save, approve, or index results directly. Durable-looking results are captured as candidates for user review and validation.

## Required Behavior

After any external reasoning result:

1. Identify whether the result contains durable value.
2. If yes, extract it and write it into the candidate batch file as a candidate entry.
3. If no, log as a defective query -- see `defective-query-log.md`.

Claude Code must not move a candidate into approved memory or index it.

## Candidate Entry Fields

```text
Domain:
Change type: [ADD | REPLACE | REMOVE | CLARIFY | DEPRECATE]
Title:
Type: [fact | rule | pattern | correction | negative finding]
Intent triggers:
Source/evidence:
Proposed fact/rule:
Reason flagged:
Durability concern:
Recommended action:
```

## Candidate Approval Pipeline

After Claude Code writes a candidate batch file:

1. The user reviews the candidate file manually.
2. The user deletes junk candidates.
3. The user edits or annotates retained candidates.
4. The user moves the cleaned file into the tentative-approved folder.
5. A validator evaluates whether each retained candidate passes durability checks.
6. Only validated candidates become approved/indexed memory.

Nothing skips this pipeline.

## Example Candidate

```text
Domain: DragonTweaksV2 (project)
Change type: ADD
Title: No blocking Minecraft main/server thread
Type: Architecture invariant
Intent triggers: blocking, synchronous, main thread, server thread, GUI, render, warehouse, advisor panel, diagnostics
Source/evidence: NeoForge architecture documentation + observed behavior
Proposed fact/rule: Nothing may block the Minecraft main/server thread. GUI/render display paths must read cached data only.
Reason flagged: Confirmed architectural constraint discovered via external research.
Durability concern: Low -- this is a well-established invariant.
Recommended action: ADD to project memory after user review.
```

## Rule

Claude Code must not discard external reasoning results without first determining whether they contain durable value. If they do, a candidate entry must be produced and written into the candidate batch file. Claude Code may not approve, ingest, index, or promote candidates without user review and validation.
