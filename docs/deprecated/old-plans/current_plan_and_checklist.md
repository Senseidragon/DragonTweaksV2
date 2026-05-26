# Project Operations Reset: Memory, Skills, Repo Strategy, and Current Plan

**Date:** 2026-05-23  
**Purpose:** Capture the full decision path from the recent discussion and turn it into a usable plan/checklist.

---

## 1. Core Problem Identified

The current framework drifted away from the original intent.

### Original intent

The desired system was not a manual archive. The intended flow was:

```text
User request
→ check memory first
→ retrieve useful prior context/rules/answers
→ only escalate to external reasoning if memory is insufficient
→ extract durable reusable facts from the result
→ save useful memory automatically or semi-automatically
```

The goal was to reduce:

- repeated prompt walls
- repeated reasoning
- repeated LLM calls
- manual “remember this” rituals
- stale context between Claude.ai and Claude Code
- Claude AI asking the user to do Claude Code’s job

### What was actually built

The implemented system became mostly:

```text
Manual prompt discipline
+ working memory file
+ dated archive entries
+ manual cleanup/pruning
+ end-of-session archive extraction
```

This created a well-governed archive, but not a true operational memory system.

### Diagnosis

The current system is useful only as **source material**. It is not the desired product.

The archive system should be treated as disposable except for the small number of durable facts worth migrating.

---

## 2. Important Distinction: Archive vs Operational Memory

### Archive

An archive stores what happened.

Useful for:

- historical record
- evidence after failures
- recovery notes
- auditing prior decisions

Weak for:

- automatic recall
- reducing token burn
- answering “have we solved this before?”
- guiding Claude Code before it makes decisions

### Operational memory

Operational memory actively constrains and informs work.

It should answer:

- What prior rule applies here?
- Have we already solved this?
- What previous correction matters?
- What query should be avoided?
- Is external reasoning necessary?
- What should be saved from this result?

### Decision

The archive is **not** the memory system. It should be mined once for useful facts, then deprecated as the primary workflow.

---

## 3. MemSearch Is the Correct Memory Direction

The missing component was identified as **MemSearch**.

MemSearch represents the desired behavior because it supports semantic/vector-style recall rather than relying only on exact keyword matching or manual reading.

### Desired MemSearch behavior

```text
Request arrives
→ parse intent
→ search memory semantically
→ retrieve prior relevant chunks
→ determine sufficiency
→ answer from memory if sufficient
→ escalate only if memory is insufficient
→ save useful delta after escalation
```

### Why this matters

A semantic memory system can match conceptually related ideas:

```text
"advisor panel synchronous stock lookup"
```

should retrieve something like:

```text
GUI/render paths must not perform live warehouse/world/blocking queries.
Use cached diagnostic data instead.
```

even if the words do not match exactly.

---

## 4. External Reasoning Value Rule

A major requirement was clarified:

External reasoning is only justified if it produces durable value.

### Rule

If memory is insufficient and Claude Code escalates to external reasoning, the result must produce at least one of:

1. a durable reusable fact
2. a correction/update to existing memory
3. a reusable pattern/rule
4. a useful negative finding
5. a defective-query record showing the query was poor and needs improvement

If none of those exist, the escalation failed.

### Reasoning

If a query produces no reusable value, then either:

- the query was badly framed
- the scope was too narrow
- the target source was wrong
- the answer was one-off/disposable
- the system should not have escalated

This creates feedback for improving the memory/query process.

---

## 5. Query Quality and Defective Query Logging

A major addition was made to the memory framework design:

If a query is defective, the system must save the bad query itself, not merely the reason.

### Why

Seeing the actual bad queries helps diagnose whether the system tends to make queries that are:

- too verbose
- too vague
- too narrow
- wrongly targeted
- redundant
- overfit to wording
- disposable
- ambiguous

### Defective Query Log fields

Each defective query should store:

```text
Timestamp/session
Original user request
Defective query text
Defect category
Why it was defective
Reframed replacement query
Expected durable value from replacement query
Outcome after retry
```

### Defect categories

```text
Too narrow
Too vague
Too verbose
Wrong target
Redundant
One-off/disposable
Ambiguous entity
Overfit to wording
Wrong source
Missing durable-value target
```

### Example

```text
Original request:
"Why did this archive entry mention planner_dependencies.json?"

Defective query:
"Why did the May 22 archive say planner_dependencies.json was copied from docs/deprecated?"

Defect:
Too narrow

Why defective:
This only explains one historical sentence. It does not extract reusable project knowledge.

Replacement query:
"What durable project facts about planner_dependencies.json should future planning tasks retrieve before touching blueprint dependency logic?"

Expected value:
Canonical location, purpose, dependency count, and when to consult it.
```

---

## 6. Claude AI vs Claude Code Role Boundary

The memory system does not let either agent off the hook.

Claude AI and Claude Code still require strict role constraints.

### Claude AI role

Claude AI is a:

- prompt architect
- reviewer
- classifier
- strategy assistant
- violation analyst

Claude AI must not act as the repository executor.

### Claude AI must not

```text
request files from Dragon when Claude Code can inspect them directly
ask Dragon to paste source files, logs, diffs, or configs that Claude Code should read
recommend code changes based on imagined file contents
perform Claude Code’s implementation role from incomplete context
tell Dragon to manually inspect files unless Claude Code is unavailable
drift into speculative patch design without file evidence
```

### Claude AI may ask Dragon for

```text
intent
priority
authorization
subjective preference
desired behavior
clarification when necessary
```

### Claude Code role

Claude Code is the repository executor.

Claude Code must:

```text
inspect authorized files directly
avoid asking Dragon to paste file contents
stay inside authorized read/write scope
report when additional file access is needed
avoid wandering into unapproved exploration
avoid hallucinating repository state
query operational memory before external reasoning
```

### Rule name

**No Human-as-File-Transport Rule**

If Claude Code can inspect the file or output, Claude AI must not ask Dragon to paste it.

---

## 7. Git and Shell Capability

A distinction was made between Git as a tool and Git as a risk.

### Important clarification

The no-git rule is not an ideal permanent engineering rule. It exists because prior AI behavior violated trust.

In normal software development, this is healthy:

```text
implement feature
→ build/test succeeds
→ review diff
→ commit with clear message
```

The actual issue is not Git itself. The issue is irresponsible AI use of Git.

### Git maturity model

```text
Git Level 0 — Hard lockout
- No git commands.
- No commits.
- No status, checkout, branch, reset, stash.
- Used when the agent has violated trust.

Git Level 1 — Read-only Git
- git status/diff/log allowed only when explicitly authorized.
- No state-changing Git commands.

Git Level 2 — Human-approved commit
- After successful build/test.
- Agent proposes files and commit message.
- Human explicitly approves.
- One commit only.

Git Level 3 — Trusted workflow
- Agent may use branches/worktrees/commits within a preapproved protocol.
- Not appropriate until proven reliable.
```

### Current posture

Current practical posture is Level 0 or Level 2 only when explicitly authorized.

### Bash on Windows

Claude Code can potentially use Git Bash on Windows as a Bash capability provider.

But:

```text
Bash availability does not authorize Git usage.
```

Bash may be useful for:

- scripts
- builds
- audits
- file inspection
- grep/find-style operations
- non-Git shell workflows

Git commands remain maturity-gated.

---

## 8. Claude.ai Project Context Sync via Google Drive

Claude.ai Projects can use project files, but manual drag-and-drop is tedious and creates stale context.

### Desired workflow

```text
successful build
→ local docs updated
→ selected docs pushed/copied to Google Drive
→ Claude.ai Project references linked Drive docs
→ Claude.ai uses current linked docs instead of stale uploads
```

### Key decision

Linked Google Drive / Google Docs files are acceptable as the sync mechanism.

### Constraint

Automatic replacement of Claude.ai Project files from Drive is not confirmed as a reliable native feature.

Therefore, the likely best workflow is:

```text
local docs are canonical
→ sync/export copies to Google Drive
→ Claude.ai Project links Drive docs
→ future sessions explicitly say: use current Drive project docs
```

### Important rule

Claude.ai must not assume project files are current unless:

- they are linked to current Drive docs, or
- Claude Code/user confirms they were refreshed

### Reusable note title

**Claude.ai Project Context Sync via Google Drive Linked Docs**

---

## 9. Portable Framework Requirement

A correction was made: this framework must not be Dragon Tweaks-specific.

### Goal

Build a portable, reusable project-operations framework that can be applied to any future project.

Dragon Tweaks is only one implementation target.

### Portable modules

```text
memory-first retrieval
save-after-query value capture
defective query logging
Claude AI / Claude Code role boundaries
safe shell/tool policy
prompt/header resolver
violation classifier
project-specific rule packs
systematic debugging / verification
Superpowers compatibility
Skill Creator workflow
```

### Distinction

Portable framework modules:

```text
role boundaries
memory-first retrieval
defective query logging
safe shell policy
query quality rules
save-after-query behavior
skills/agent methodology
```

Project-specific rule packs:

```text
Minecraft threading rules
MineColonies architecture constraints
Dragon Tweaks blueprint mappings
specific build/test commands
project-specific docs
```

---

## 10. Claude Code Skills

Claude Code skills were discussed as reusable instruction packages for recurring workflows.

### What skills are for

Skills are useful for:

```text
repeatable workflows
domain rules
checklists
tool-use procedures
review patterns
memory-write/retrieval procedures
debugging discipline
safe shell handling
project-specific rule packs
```

### Skills are not

Skills are not hard enforcement.

They do not inherently:

```text
guarantee compliance
prevent hallucination
replace hooks or permissions
know repo state without inspection
prevent bad behavior from conflicting instructions
```

### Desired skill categories

Initial target order:

1. **Superpowers**
2. **Systematic Debugging / Verification**
3. **Skill Creator**
4. **MemSearch framework implementation**

TDD should come primarily through Superpowers rather than a separate generic TDD skill, because the separate TDD skill was viewed as less compelling.

---

## 11. Superpowers Evaluation

Superpowers was identified as potentially useful, but not safe to install blindly.

### What it can provide

Superpowers may help with:

```text
structured engineering discipline
brainstorming before coding
design approval
implementation planning
systematic debugging
TDD workflow
verification before completion
code review checkpoints
skill creation patterns
```

### Why it may help

It could reduce:

- rush-to-edit behavior
- guess-and-patch cycles
- unverified completion claims
- poor debugging discipline
- lack of structured review

### Gotchas

Potential problems:

```text
Git-heavy defaults
branch/worktree/commit assumptions
token burn
Windows hook issues
subagent discipline may not carry reliably
installation/cache reliability issues
too much ceremony for small tasks
```

### Decision

Superpowers is worth testing, but not blindly installing into a serious repo unmodified.

Best use:

```text
Use Superpowers as an implementation aid and sanity framework.
Customize or constrain it to fit Dragon's workflow.
Do not let it override local safety/maturity rules.
```

---

## 12. Current Repository Strategy

A new strategic plan was chosen.

### Current state

The current repo has a semi-functional framework, but the user is dissatisfied with the archive-centered memory design.

### Decision

Commit the current state for archival purposes, then start clean.

### Why commit current state?

The commit is not an endorsement.

It is a checkpoint:

```text
preserve current semi-functional state
create rollback/reference point
capture the work before replacing the memory architecture
avoid losing potentially useful code/docs
```

Suggested commit intent:

```text
checkpoint: archive semi-functional framework state before redesign
```

### Same repo vs new repo

#### Staying in same repo advantages

```text
history remains in one place
easy comparison with old implementation
possible cherry-pick/revert
no need to recreate setup
```

#### Staying in same repo disadvantages

```text
stale docs remain nearby
old archive framework may confuse future agents
old assumptions can leak into new work
refactoring becomes archaeology
harder to enforce a clean base
```

#### New repo advantages

```text
clean Minecraft + MineColonies baseline
no stale framework baggage
reusable base for multiple future projects
clear separation between old museum and new workshop
fewer files/less confusion for Claude Code
```

#### New repo disadvantages

```text
must recreate/import build setup
must migrate useful docs/configs deliberately
old history is not directly available
useful material must be copied intentionally
```

### Final recommendation

Do both:

```text
1. Old repo:
   Commit current state as archival checkpoint.

2. New repo:
   Create clean Minecraft + MineColonies base.

3. New repo:
   Add portable MemSearch/agent-ops framework.

4. New repo:
   Add LLM integration after memory is operational.
```

The old repo becomes the museum.  
The new repo becomes the workshop.

---

## 13. Planned Build Order

### Phase 0 — Archive current work

Goal:

```text
Commit current semi-functional state for preservation.
```

Checklist:

```text
[ ] Confirm build state.
[ ] Confirm no obviously dangerous/stub-generated files are being committed unintentionally.
[ ] Review changed files at a high level.
[ ] Commit as archival checkpoint with honest message.
[ ] Do not treat this as endorsement of the current architecture.
```

Suggested checkpoint label:

```text
checkpoint: archive semi-functional framework state before redesign
```

---

### Phase 1 — Create clean base repo

Goal:

```text
Create a barebones, clean Minecraft + MineColonies base repo/checkpoint.
```

Baseline contents:

```text
Minecraft
NeoForge
MineColonies
minimum required dependencies
known-good build/run config
minimal documentation
```

Checklist:

```text
[ ] Create new repo or new clean working directory.
[ ] Add clean Minecraft/NeoForge/MineColonies setup.
[ ] Verify build.
[ ] Verify launch if applicable.
[ ] Document exact versions.
[ ] Commit/checkpoint clean base.
```

Suggested checkpoint label:

```text
checkpoint: clean Minecraft MineColonies base
```

---

### Phase 2 — Add portable project-ops framework

Goal:

```text
Add the reusable framework before feature/refactor work begins.
```

Must include:

```text
MemSearch-first retrieval
save-after-query value capture
defective query logging
role-boundary rules
safe shell policy
Git maturity gating
Skill Creator support
Superpowers compatibility/sanity checks
systematic debugging/verification workflow
project-specific rule-pack structure
```

Checklist:

```text
[ ] Define portable framework docs.
[ ] Define project-specific rule pack location.
[ ] Define memory-first retrieval protocol.
[ ] Define save-after-query protocol.
[ ] Define defective-query log format.
[ ] Define Claude AI / Claude Code role boundary.
[ ] Define safe shell policy.
[ ] Define Git maturity model.
[ ] Add or prepare MemSearch installation/config.
[ ] Add skill structure.
[ ] Test retrieval on a small memory sample.
[ ] Confirm framework does not depend on dated archive workflow.
[ ] Commit/checkpoint framework.
```

Suggested checkpoint label:

```text
checkpoint: add portable MemSearch operational framework
```

---

### Phase 3 — Install/evaluate Superpowers and supporting skills

Goal:

```text
Use Superpowers, systematic debugging/verification, and Skill Creator to implement and refine the framework safely.
```

Checklist:

```text
[ ] Test Superpowers in an isolated/disposable repo or controlled context.
[ ] Identify Git-heavy skills/workflows.
[ ] Disable, override, or maturity-gate Git behavior.
[ ] Verify Windows hook behavior.
[ ] Verify no unacceptable startup/session behavior.
[ ] Evaluate token cost.
[ ] Keep useful TDD/debugging/planning workflows.
[ ] Reject workflows that create manual burden or unsafe autonomy.
[ ] Use Skill Creator to build custom portable skills.
```

---

### Phase 4 — Activate MemSearch operational memory

Goal:

```text
Get memory working before more feature work.
```

Required behavior:

```text
Before substantive work:
- query memory first
- determine sufficiency
- reuse prior answers if sufficient
- escalate only if needed
- capture durable value after external reasoning
```

Checklist:

```text
[ ] Install/configure MemSearch.
[ ] Decide memory source folders.
[ ] Create retrieval-ready chunk format.
[ ] Convert only useful facts from old archive/MEMORY.md.
[ ] Index memory.
[ ] Test semantic retrieval.
[ ] Implement Memory Value Report.
[ ] Implement Defective Query Log.
[ ] Implement Save Candidate handling.
[ ] Verify bad queries are logged with actual query text.
[ ] Verify no manual end-session archive ritual is required.
```

---

### Phase 5 — Add LLM integration last

Goal:

```text
Add the LLM integration only after clean base + memory framework are stable.
```

Reasoning:

The LLM integration was considered the part that was most likely moving in the correct direction. It should be added after the operational foundation is in place, so the system captures useful implementation knowledge as work proceeds.

Checklist:

```text
[ ] Define LLM integration requirements.
[ ] Use MemSearch before implementation decisions.
[ ] Use Superpowers/TDD workflow carefully.
[ ] Add feature in small increments.
[ ] Verify build after each increment.
[ ] Capture reusable facts after each external query or design decision.
[ ] Commit/checkpoint after successful verified milestones.
```

---

## 14. Memory Salvage Plan

The old archive should not be preserved as active workflow.

### Salvage only durable operational facts

Useful categories:

```text
hard safety rules
architecture invariants
reusable project facts
prior failure corrections
prompt/header rules that still matter
user preferences
workflow constraints
```

Discard/deprecate:

```text
daily archive requirement
manual end-session archive extraction
cleanup/pruning as primary value
archive promotion ritual
routine session summaries
historical “what happened today” logs
```

### Example chunk format

```text
Title:
No blocking Minecraft main/server thread

Type:
Architecture invariant

Intent triggers:
blocking, synchronous, main thread, server thread, GUI, render, warehouse, advisor panel, diagnostics

Rule:
Nothing may block the Minecraft main/server thread. GUI/render display paths must read cached data only.

When to apply:
Any implementation involving panels, diagnostics, warehouse stock, world queries, async tasks, or display paths.
```

---

## 15. Current Plan Summary

The current plan is:

```text
1. Commit current semi-functional repo state for archival purposes.
2. Start a new clean Minecraft + MineColonies base repo/checkpoint.
3. Add portable, non-project-specific project-ops framework.
4. Bring up MemSearch-first operational memory from the beginning.
5. Use Superpowers + systematic debugging/verification + Skill Creator to implement the framework correctly.
6. Save that as a checkpoint.
7. Add LLM integration after the framework is operational.
8. Use careful TDD/refactoring-as-you-go from that point forward.
```

---

## 16. Master Checklist

### Immediate

```text
[ ] Commit current repo for archival purposes.
[ ] Use honest checkpoint message.
[ ] Do not continue expanding old archive framework.
```

### Clean base

```text
[ ] Create new clean repo.
[ ] Add Minecraft + NeoForge + MineColonies.
[ ] Verify build.
[ ] Document versions.
[ ] Commit clean base.
```

### Framework foundation

```text
[ ] Define portable project-ops structure.
[ ] Add role-boundary rules.
[ ] Add safe shell policy.
[ ] Add Git maturity model.
[ ] Add query-quality rules.
[ ] Add defective-query log format.
[ ] Add save-after-query value rule.
[ ] Add project-specific rule-pack structure.
```

### Skills / methodology

```text
[ ] Evaluate Superpowers in controlled context.
[ ] Identify and constrain Git-heavy workflows.
[ ] Evaluate systematic debugging/verification behavior.
[ ] Use Skill Creator for custom portable skills.
[ ] Keep TDD through Superpowers if it proves useful.
```

### MemSearch

```text
[ ] Install/configure MemSearch.
[ ] Define memory source folders.
[ ] Define chunk schema.
[ ] Salvage useful facts from old system.
[ ] Index memory.
[ ] Test semantic recall.
[ ] Require memory-first lookup.
[ ] Require Memory Value Report.
[ ] Require Defective Query Log.
```

### LLM integration

```text
[ ] Add only after memory framework is active.
[ ] Implement in small verified steps.
[ ] Use TDD/refactoring carefully.
[ ] Save durable implementation lessons as they occur.
```

---

## 17. Guiding Principles

```text
The archive is not the memory system.

Memory must be retrieval-first.

External reasoning must produce durable value.

Bad queries must be saved and classified.

Claude AI must not do Claude Code’s job.

Dragon must not be used as file/shell transport.

Git is not evil; irresponsible AI Git usage is the problem.

Git operations are maturity-gated.

The framework must be portable across projects.

Dragon Tweaks is one implementation target, not the framework boundary.

The old repo is the museum.

The new repo is the workshop.
```

---

## 18. Confidence

Confidence in the plan: **0.91**

Main uncertainty:

```text
Superpowers may require customization or selective adoption.
MemSearch integration details may vary by local environment.
Claude.ai Drive-linked Project behavior may still require manual confirmation.
```

The strategic direction is clear:

```text
preserve old state
start clean
install operational memory first
then build/refactor with memory active from the beginning
```
