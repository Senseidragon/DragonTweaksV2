# Unsupported External-Cause Claims

Use this when considering claims such as:

- known bug
- mod conflict
- JVM issue
- Java bug
- NeoForge issue
- environment problem
- worldgen/modpack lockup

## Evidence required

An external-cause claim is invalid unless it includes:

1. A citation, issue, log source, or local evidence.
2. Matching symptom or stack trace.
3. Affected version match.
4. Explanation for why the known-good control works while the current version fails.

## Diagnostic priority

If V1 works and V2 fails under the same environment, V2 deltas are guilty until cleared.

Do not redirect diagnosis to external causes until local code has been inspected and cleared.
