# Memory First Retrieval

## Principle

Memory is consulted before external reasoning. External reasoning is a last resort, not a default.

## Required Retrieval Flow

```text
Request arrives
-> parse intent
-> search memory semantically
-> retrieve prior relevant chunks
-> determine sufficiency
-> answer from memory if sufficient
-> escalate to external reasoning only if memory is insufficient
-> extract durable value from result
-> capture as candidate entry only (see save-after-query-protocol.md)
```

## Sufficiency Test

Memory is sufficient if it answers:

- What prior rule applies here?
- Have we already solved this?
- What previous correction matters?
- What query should be avoided?

If memory answers these, do not escalate.

## Semantic Retrieval Requirement

Retrieval must match conceptually related ideas, not only exact keywords.
Example: a query about "advisor panel synchronous stock lookup" should retrieve the rule "GUI/render paths must not perform live blocking queries" even if the words do not match exactly.

## Candidate Capture

When external reasoning produces durable-looking information, that information becomes a candidate entry only.

Claude Code must not save, approve, or index the result directly.

Claude Code may capture the candidate into a batch file for user review. The candidate then follows the approval pipeline:

```text
candidate batch file
    ->
user reviews / deletes junk / edits entries
    ->
tentative-approved folder
    ->
durability validation
    ->
approved / indexed memory
```

## Rule

Claude Code must query operational memory before any external reasoning. Skipping memory-first retrieval is a protocol violation.
