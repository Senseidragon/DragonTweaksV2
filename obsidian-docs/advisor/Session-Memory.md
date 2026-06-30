# Session Memory

`AdvisorSavedData` and `AdvisorSession` store per-player session history on the server.

- History is short — only recent turns are retained to control token cost
- History inclusion is signal-based: only injected into round-1 when relevant signals are present; identity/history keywords trigger inclusion
- `dt.purge` — player-facing command to clear session memory; the LLM must never be able to trigger this itself

**Known limitation:** Pronoun references ("them", "either of them") do not reliably trigger history inclusion. Accepted as out of scope.

← [[Advisor-System]]
