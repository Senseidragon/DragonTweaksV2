# Known-Good Regression Triage

Source: [[none]]

For regressions, compare against the working version before blaming external systems.

Required sequence:

1. State the invariant.
2. State the known-good control.
3. State the current failure.
4. Identify changed surface area.
5. Compare exact files, methods, and call paths.
6. Prove local code innocent before blaming external systems.
7. Make one fix, one test, then reassess.

If two hypotheses fail, stop patching and rebuild the causal map.
