# Approved Memory Finalization

Use this only when candidates have already been manually approved into the final pre-promotion or pending-reindex state.

## Purpose

Finish the mechanical finalization step so Dragon does not have to remember to reindex approved material manually.

### SessionStart Memory Hooks

SessionStart may only:

- passively report pending candidate queues
- final-validate and promote files already in `tentative-approved`

`tentative-approved` means the file has either:
- passed first mechanical validation with high confidence, or
- been manually approved by Dragon after review

SessionStart must not:

- process raw or extracted candidates
- perform first validation
- approve review candidates
- infer user approval
- capture new raw data
- broaden indexing scope

## Allowed

- Read the explicit pending-reindex/final-approved list.
- Confirm each listed path is an approved/finalized path.
- Index only those approved paths.
- Flush/reload only as specified by the memory procedure.
- Remove or clear a coordination file only after successful finalization, if the procedure explicitly permits it.

## Not allowed

- Promote new candidates.
- Tombstone entries.
- Supersede entries.
- Validate unrelated candidate queues.
- Index raw, rejected, deprecated, tentative, candidate, or whole-tree paths.
- Run broad `.memsearch/` or `.memsearch/memory/` indexing.

## Failure handling

If any listed path is anomalous or not clearly approved, stop finalization for that item and report it. Continue unrelated requested work when safe.
