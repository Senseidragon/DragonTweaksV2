**Title:** Patch scope rules — what must not be mixed in a single task
**Type:** fact
**Intent triggers:** patch scope, task scope, mixed edits, settings.json, Java source, audit scripts, hooks, too many files, broad change, risky combinations, safe edit policy

## Combinations That Must Not Appear in One Task

1. `.claude/settings.json` (or `settings.local.json`) modified in the same task as Java source changes
2. `~/.claude/` global files modified during any task (requires explicit separate authorization)
3. Audit/hook scripts and Java gameplay source changed together
4. Too many files changed in one task — flag when the changeset is unusually broad for the stated goal

## Why

These combinations indicate either scope creep or that Claude is conflating infrastructure maintenance with gameplay work. They have historically led to unreviewed config changes being bundled with code changes, making audits and rollbacks harder.

**How to apply:** Before editing, check whether the planned file set crosses these boundaries. If it does, stop and split the task or confirm with Dragon before proceeding.
