# Start Here

Read this first at the start of any session on DragonTweaksV2. It exists so picking up work doesn't depend on remembering where things live or stumbling onto `codify/` by accident — that's how this file came to exist in the first place.

## 1. Project rules — already loaded

`CLAUDE.md` is injected automatically as project instructions every session; you already have it before reading anything else. It is the highest-authority document in this repo. Nothing below overrides it.

## 2. Session continuity — the actual "where did we leave off"

`codify/codify00.md` through the highest-numbered file present is the authoritative session-by-session history: what was done, why, what's still open, what was explicitly rejected, and standing directives that aren't written anywhere else yet. Read every file **in numeric order**, not just the latest — later snapshots assume earlier ones and don't repeat their content.

This supersedes `feed-me.md` and `docs/ONBOARDING.md` for anything dated 2026-06-19 or later (when `/codify` snapshots began).

## 3. Verification history

`test-audit-trail.md` (project root) — append-only log of every code change to the mod: what changed, which test(s) covered it, pass/fail. Check the most recent entries for current state. Never overwritten or truncated — treat any apparent gap as real, not a display artifact.

## 4. Current working-tree / git state

Read `feedback_git_access_revoked.md` in the memory system **before** running any git command — including read-only ones like `git status` or `git log`. Do not run a git command to find out whether access is open; that has to come from the memory file, not from git itself.

If that memory says access is revoked: stop. No git command of any kind, full stop, until Dragon explicitly states access is restored. This does not reset just because a new session started.

Only once that memory confirms access is open does `git status`/`git log` become safe to use, to see what's actually sitting uncommitted versus what the latest codify snapshot describes.

## 5. Architecture and domain docs — as needed, not upfront

- `docs/` — topic-tiered project-ops and domain documentation. See `docs/MemorySystemDocIndex.md` to route within it.
- `docs/ONBOARDING.md` — static tech-stack/build/convention reference. Not a continuity log; don't expect it to reflect day-to-day state.
- `notes.md` (project root) — read-only scratch analysis of a legacy codebase review. Historical reference, not a task list.
- `feed-me.md` (project root) — frozen 2026-06-16 session wrap-up, the precursor to `codify/`. Kept for archival continuity only.

## 6. Memory system

The auto-loaded memory index (`~/.claude/projects/.../memory/MEMORY.md`) is injected automatically every session — don't re-fetch it manually. A separate `MEMORY.md` also exists at the project root and may drift from the auto-loaded copy; if they disagree, the auto-loaded one is authoritative.

## Maintenance

Running `/codify` appends a new file to `codify/` — nothing here needs to change for that. If a new root-level doc is ever added that should be part of session bootstrap, add one line for it here rather than leaving it to be rediscovered by accident.
