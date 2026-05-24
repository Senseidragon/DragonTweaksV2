# Git Maturity Model

## Principle

Git is not the problem. Irresponsible AI use of Git is the problem. Git operations are maturity-gated.

## Maturity Levels

### Level 0 — Hard Lockout

- No git commands of any kind.
- No status, diff, log, checkout, branch, reset, or stash.
- Applied when the agent has violated trust.

### Level 1 — Read-Only Git

- `git status`, `git diff`, `git log` allowed only when explicitly authorized.
- No state-changing Git commands.

### Level 2 — Human-Approved Commit

- After successful build/test only.
- Agent proposes changed files and commit message.
- Dragon explicitly approves.
- One commit only per authorization.

### Level 3 — Trusted Workflow

- Agent may use branches, worktrees, and commits within a pre-approved protocol.
- Not appropriate until proven reliable over time.

## Current Posture

**Level 0** by default. Level 2 only when explicitly authorized by Dragon for a specific commit.

## Rule

Bash availability does not authorize Git usage. Git level must be explicitly granted per session or per task.
