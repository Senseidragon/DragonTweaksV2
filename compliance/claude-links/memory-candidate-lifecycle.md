# Memory Candidate Lifecycle

Memory candidates are not approved memory.

## Intake

New information may be captured only into the appropriate raw or candidate intake location defined by the memory procedure.

Raw candidate material is quarantine. It is not trusted memory and must not be indexed as approved knowledge.

## Classification

Validation must classify candidate material as one of:

- new fact
- clarification or update to an existing fact
- duplicate
- conflict requiring human review
- junk/rejected

## Limits

Do not promote, tombstone, supersede, or reindex candidates unless the current task explicitly invokes the validation/finalization procedure.

Do not treat `Index: false` or metadata as technical enforcement. Path selection and explicit indexing procedure are the enforcement layer.

## Stop condition

If candidate status, source, provenance, or validation state is unclear, stop candidate processing and report the ambiguity without changing memory state.
