# Known-Good Regression Triage

Use this before editing code for any regression.

## Required pre-edit output

- Invariant:
- Known-good control:
- Current failure:
- Changed surface area:
- Files/methods compared:
- Disproven hypotheses:
- Smallest next causal test:
- Proposed single fix:

## Rules

Changed project code is guilty until cleared.

If a prior version works under the same environment and the current version fails, compare current code to the known-good version before blaming external systems.

Do not claim two versions are “basically the same” without citing exact files, methods, call paths, and relevant behavioral differences.

After two failed hypotheses, stop patching. List disproven hypotheses and reset the causal map before further edits.

## Fix cadence

Make one fix, run one test, then reassess.
