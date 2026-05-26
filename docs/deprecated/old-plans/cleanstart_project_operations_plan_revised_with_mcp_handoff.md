# Clean Start Project Operations Plan

**Date:** 2026-05-25  
**Purpose:** Define a clean, portable project-operations framework that uses local authority, enforced memory-first retrieval, clear agent role boundaries, safe shell/Git policy, and measurable acceptance tests before feature work begins.

---

## 1. Executive Decision

The previous system produced a useful historical archive, but it did not become operational memory.

The new plan is:

1. Preserve the old work only as a reference point if explicitly authorized.
2. Start from a clean repo, clean branch, or clean working directory.
3. Build the smallest viable project-operations framework first.
4. Enforce memory-first retrieval with a task wrapper or required preflight artifact.
5. Use current local repo state, current Claude Code output, current memory artifacts, and Dragon-confirmed facts as the only active authority.
6. Add feature work only after the minimum framework works and passes retrieval tests.

The old system is not the active workflow.  
The clean base is the workshop.

---

## 2. Core Problem

### Original goal

The intended system was:

```text
User request
→ check operational memory first
→ retrieve applicable rules, facts, prior corrections, or known answers
→ decide whether memory is sufficient
→ escalate only when memory is insufficient
→ capture durable new value when escalation produces it
```

The goal was to reduce:

- repeated prompt walls
- repeated reasoning
- repeated LLM calls
- stale context
- manual “remember this” rituals
- Claude AI asking Dragon to do Claude Code’s job

### What actually happened

The previous workflow became:

```text
manual prompt discipline
+ working notes
+ dated archive entries
+ manual cleanup
+ end-of-session archive extraction
```

That is a governed archive, not operational memory.

### Decision

The archive model is retired as the active workflow. Old archive material is not imported by default. Any old item Dragon handpicks later must be treated as untrusted until validated against current evidence.

---

## 3. Archive vs Operational Memory

### Archive

An archive records what happened.

Useful for:

- recovery notes
- historical auditing
- failure postmortems
- human reference

Weak for:

- automatic recall
- reducing token burn
- preventing repeated mistakes
- guiding Claude Code before it acts

### Operational memory

Operational memory constrains and informs current work.

It should answer:

- What rule applies here?
- What prior failure must not repeat?
- What project fact is currently valid?
- What command or workflow is verified?
- Is external reasoning necessary?
- Should anything from this task be saved?

### Active rule

The archive is not the memory system. It is only an inactive reference source. Nothing from the archive becomes active memory without explicit user selection and current validation.

---

## 4. What “Seed Chunks” Mean

Seed chunks are not imported archive data.

They are the initial set of small, high-value operational memory records needed so the memory-first loop has something useful to retrieve on day one.

A seed chunk can come from:

- Dragon-confirmed standing rules
- current repo facts verified by Claude Code
- current environment constraints
- current workflow constraints
- verified commands
- explicit project invariants
- known failure corrections that are still current
- role-boundary rules
- safe shell/Git policy

A seed chunk should not come from old archive material unless Dragon deliberately selects it and it passes validation.

### Seed quality rule

The old “20–30 chunks” target is not the definition of done. Count is secondary.

Seed memory is complete only when it covers the required operational areas and passes retrieval tests.

Required initial coverage:

```text
1. Git/shell policy
2. Claude.ai vs Claude Code role boundary
3. No Human-as-File-Transport rule
4. Windows PowerShell 5.1 constraints
5. current repo authority / stale path rejection
6. hard-rule retrieval behavior
7. task wrapper or preflight artifact requirement
8. evidence classification requirement
9. disposable-query / durability scoring rule
10. DragonTweaksV2 project invariants, if applicable and current-file verified
```

The likely result may still be around 20–30 chunks, but that is an estimate, not a success criterion.

---

## 5. First 5 Deliverables

Nothing else is in progress until these ship in strict order:

```text
1. Clean base exists and builds.
2. Minimum project-ops folder structure exists.
3. Task wrapper or preflight artifact exists.
4. Keyword/tag memory retrieval stub works against local chunks.
5. Initial acceptance tests prove hard-rule retrieval works.
```

Do not begin any of the following until the first five deliverables are complete:

- LLM integration
- advanced skills
- semantic/vector backend work
- broad framework expansion
- optional workflow tooling
- feature implementation

This prevents the checklist from becoming a swamp.

---

## 6. Local Authority Model

All active project context must come from current, local, verifiable sources.

Authoritative sources:

```text
current local repo state
Claude Code output from the current task
current generated context bundle
current memory retrieval artifact
Dragon-confirmed current facts
```

Not authoritative by default:

```text
old archive entries
old repo paths
old phase plans
old document names
old memory-system assumptions
AI recollection
unverified summaries
```

### Stale-path rejection rule

A file path, directory, build command, config file, or framework feature does not exist for planning purposes unless current evidence confirms it.

Example:

```text
Claim: docs/MemorySystemDoc.md exists
Required validation: current repo inspection
Result: reject the claim if the file is absent
```

---

## 7. Minimum Project-Ops Framework

The minimum viable framework must include:

```text
ops/framework/
ops/projects/<project-name>/
ops/rules/<project-name>/
memory/rules/global/
memory/rules/<project-name>/
memory/decisions/
memory/failures/
memory/commands/
memory/context/
memory/inbox/
.task/
```

### Required modules

```text
memory retrieval interface
keyword/tag retrieval stub
task wrapper or required preflight artifact
role-boundary rules
safe shell policy
Git maturity model
evidence classification template
query-quality rules
save-after-query durability scoring
memory inbox governance
local context bundle or session digest
retrieval acceptance tests
```

### Deferred modules

```text
semantic/vector retrieval
advanced automation
large analytics layer
optional external workflow tools
feature implementation
```

---

## 8. Memory Retrieval Interface

The memory backend is replaceable. The interface comes first.

### Input

```text
task/request text
project name
optional memory type filter
optional enforcement level filter
optional recency/freshness requirement
optional source scope
```

### Output

```text
retrieved chunks
source path
memory type
enforcement level
provenance/status
relevance signal, if available
applicability classification
sufficiency judgment
external escalation decision
```

### First backend

The first backend is a simple keyword/tag search over local Markdown chunks.

Semantic/vector retrieval may be added later only after the stub proves the workflow.

---

## 9. Retrieval Ranking Rule

Hard rules outrank similarity.

Retrieval priority:

```text
1. Hard-rule exact/tag matches
2. Project-specific rule-pack matches
3. Verified command / failure-correction matches
4. Soft-context semantic or fuzzy matches
5. Deprecated or unverified notes, excluded unless explicitly requested
```

A soft-context result must never override a hard-rule result.

Retrieved chunks must be classified as:

```text
applicable
possibly applicable
irrelevant
stale
deprecated
```

---

## 10. Memory Chunk Schema

Every active memory chunk must have metadata.

```text
Title:
Type:
Enforcement level:
Source:
Provenance:
Status:
Validation method:
Intent triggers:
Rule or fact:
When to apply:
When not to apply:
Last verified date:
```

### Memory types

```text
hard_rule
soft_preference
project_fact
failure_correction
workflow_pattern
temporary_context
decision
verified_command
deprecated_note
```

### Enforcement levels

```text
MUST
SHOULD
MAY
DO NOT
EXPERIMENTAL
DEPRECATED
```

Rules, facts, preferences, workflow patterns, and temporary context must not be mixed without type and enforcement metadata.

---

## 11. Memory-First Enforcement

Memory-first retrieval must be enforced by workflow, not requested as a favor.

Before substantive work begins:

```text
1. Generate memory query.
2. Execute retrieval.
3. Write or display retrieval result.
4. Classify applicability.
5. Judge sufficiency.
6. Decide whether external escalation is justified.
7. Proceed only after the artifact exists.
```

### Required artifact

```text
.task/preflight-memory-query.md
```

Required contents:

```text
task summary
queries executed
retrieved chunks
applicability classification
sufficiency judgment
external escalation decision
```

### Preferred enforcement

Use a task wrapper command that performs memory search, writes the artifact, and prints the Claude Code execution prompt only after retrieval.

Example:

```powershell
.\task-start.ps1 -Task "Add advisor diagnostic cache rule"
```

---

## 12. Claude AI vs Claude Code Boundary

### Claude AI role

Claude AI is a:

```text
prompt architect
reviewer
classifier
strategy assistant
violation analyst
```

Claude AI must not act as the repository executor.

### Claude AI must not

```text
ask Dragon to paste files Claude Code can inspect
ask Dragon to paste logs Claude Code can read
recommend code changes from imagined file contents
perform implementation from incomplete context
tell Dragon to manually inspect files unless Claude Code is unavailable
drift into speculative patch design without evidence
```

### Claude Code role

Claude Code is the repository executor.

Claude Code must:

```text
inspect authorized files directly
stay inside authorized read/write scope
query memory before substantive work
report when more file access is needed
avoid hallucinating repository state
avoid asking Dragon to transport file contents
```

### No Human-as-File-Transport Rule

If Claude Code can inspect the file, log, config, or command output directly, Claude AI must not ask Dragon to paste it.

### Required Claude AI output for implementation tasks

Claude AI should produce one self-contained Claude Code execution block per task.

The block must include:

```text
task objective
authorized read paths
authorized write paths
required memory queries
applicable constraints
success criteria
forbidden actions
expected report format
```

---


## 13. Claude.ai Sandbox Artifact Handoff — Proof of Concept

A limited MCP sandbox bridge may be tested as a future transport mechanism between Claude.ai and Claude Code.

This bridge exists to reduce manual copy/paste, document download/upload friction, lost prompt formatting, and Dragon-as-file-transport behavior.

### Current status

```text
Proof of concept only.
Not an active dependency of the project-ops framework.
Not required for the first five deliverables.
Dropped entirely if Anthropic cannot reliably connect to it and transmit artifacts.
```

### Core workflow

```text
Claude.ai
→ MCP packet validation
→ artifact-class-specific sandbox inbox
→ Claude Code review and validation
→ accepted, rejected, superseded, or invalid
→ archive with status
```

Claude.ai may deposit proposed prompts or document artifacts only into a designated sandbox path.

The sandbox is not the authoritative repo.

A sandbox artifact is not approval to execute, move, apply, or trust the content.

Claude Code remains the only repository executor.

### Initial allowed MCP operations

```text
file_read inside sandbox
file_write inside sandbox
directory list inside sandbox
```

### Initial forbidden MCP behavior

```text
read outside sandbox
write outside sandbox
execute shell commands
run Git commands
move files into repo paths directly
overwrite repo files directly
treat deposited files as approved instructions
treat deposited files as active memory
```

### Inbound artifact classes

The MCP bridge distinguishes between prompt artifacts and document artifacts.

Prompt artifacts:

```text
contain execution intent
must use the prompt packet schema
must obey the stricter prompt size limit
must land only in prompts/inbox
must be reviewed by Claude Code before execution
must be archived after review regardless of outcome
```

Document artifacts:

```text
contain draft, reference, or editable content
must use the document packet schema
may use a larger document size limit
must land only in the document sandbox area
must be reviewed before being copied, moved, or applied to repo files
must be archived or rejected according to document-handling rules
```

A document artifact must not be treated as an execution prompt.

A prompt artifact must not be treated as an approved document edit.

### Suggested sandbox layout

```text
sandbox/
  prompts/
    inbox/
    archive/
  documents/
    inbox/
    archive/
  scratch/
```

For the first proof of concept, only this may be needed:

```text
sandbox/
  prompts/
    inbox/
    archive/
```

### Prompt inbox/archive rule

Claude.ai writes proposed prompts to:

```text
sandbox/prompts/inbox/
```

After Claude Code reviews an inbox prompt, Claude Code moves it to:

```text
sandbox/prompts/archive/
```

This happens whether the prompt is:

```text
accepted
rejected
superseded
invalid
```

Archive entries should preserve review status.

Suggested metadata:

```text
Status:
Reviewed by:
Reviewed at:
Reason:
Action taken:
Destination, if applied:
```

### Sandbox confinement requirements

The bridge must be designed so Claude.ai tool calls cannot read or write outside the configured sandbox.

Before the bridge is treated as usable, tests must verify:

```text
path normalization
absolute path rejection
Windows drive-letter path rejection
UNC path rejection
parent traversal rejection
wildcard rejection
symlink escape rejection
oversized prompt rejection
oversized document rejection
unknown tool rejection
read/write extension policy
directory-list scope
```

Before any live exposure, rotate any token, certificate, or key material that has been shared outside the deployment environment.

Before any non-PoC exposure, add a request-body limit before reading the full HTTP body.

### Promotion condition

The bridge may become part of the normal workflow only after verified tests prove:

```text
Claude.ai can connect to the MCP server.
Claude.ai can write a prompt artifact into prompts/inbox.
Claude.ai can read allowed sandbox files.
Claude.ai can list allowed sandbox directories.
Claude Code can reliably discover and read the deposited artifact.
Claude Code can archive the artifact after review.
Sandbox confinement tests still pass.
```

Until then, the bridge remains optional and may be dropped without changing the core operations plan.

## 14. Evidence Classification

Before Claude AI gives implementation advice, it must classify evidence.

Template:

```text
Evidence Provided:
- File-verified:
- Claude Code output:
- User-reported:
- Inferred:
- Unverified:

Allowed next action:
```

If the evidence section cannot be filled, Claude AI cannot issue implementation advice.

---

## 15. Safe Shell and Git Policy

Git is useful, but AI Git behavior must be maturity-gated.

### Current default

```text
Git Level 0 — Hard lockout
- No git commands.
- No commits.
- No status.
- No checkout.
- No branch.
- No reset.
- No stash.
- No clean.
```

The standard Claude Code prompt header must include:

```text
Do not commit anything.
Do not run git commands of any kind.
```

### One-shot Git authorization

Git commands are allowed only when Dragon explicitly authorizes them for a specific task.

A one-shot authorization must list:

```text
allowed commands
forbidden commands
authorized files or scope
required report format
```

### Bash on Windows

Bash availability does not authorize Git usage.

Bash may be used for:

```text
scripts
builds
audits
file inspection
grep/find-style operations
non-Git shell workflows
```

Git commands remain separately gated.

### Windows PowerShell rule

Default commands must be compatible with Windows PowerShell 5.1 unless explicitly marked otherwise.

Do not use PowerShell 7-only parameters such as:

```text
Invoke-RestMethod -SkipCertificateCheck
```

unless the task explicitly authorizes PowerShell 7.

---

## 16. External Reasoning and Save-After-Query

External reasoning is justified only when memory is insufficient or current information is required.

If external reasoning is used, classify the result before saving.

### Durability score

```text
0 = do not save
1 = session-only
2 = project memory candidate
3 = hard reusable rule
```

Only score 2–3 may become persistent memory.

### Disposable-query exemptions

Do not permanently save by default:

```text
current price
current availability
current version
current mod availability
current retailer state
news/current event
one-time troubleshooting output
temporary error output
```

### Auto-indexable categories

Only these categories may be auto-indexed if validated:

```text
hard safety rule
confirmed repo fact
repeated failure correction
verified command
architecture invariant
```

Everything else stays session-local or goes to the inbox.

---

## 17. Memory Inbox Governance

The inbox is temporary staging, not a second archive.

```text
memory/inbox/
```

Every inbox item must eventually end as one of:

```text
promoted to active memory
rejected
merged into an existing chunk
marked session-only and discarded
```

No inbox item enters active memory without explicit classification.

Claude AI may draft formatted memory candidates into the inbox after a task, but promotion into active memory must remain explicit and authorized.

---

## 18. Defective Query Handling

Defective-query logging exists to improve future retrieval, not to create paperwork.

### Reduced live log

During normal work, record only:

```text
timestamp
failed query
failure/output
```

### Full defect record trigger

Create a full defective-query record only when a bad query caused:

```text
wrong answer
wasted tool call
missed known memory
unsafe recommendation
repeated prior mistake
external escalation with zero durable value
more than two failed query iterations for the same task
```

### Full record fields

```text
Timestamp/session
Original user request
Defective query text
Defect category
Why it was defective
Replacement query
Expected durable value from replacement query
Outcome after retry
```

### Improvement rule

Repeated defect patterns must become operational memory rules.

Serious defective-query entries should become retrieval regression tests.

---

## 19. Context Bundle / Session Digest

The local repo may generate a context bundle for planned work.

Suggested files:

```text
CLAUDE_PROJECT_CONTEXT.md
PROJECT_MIND_MAP.md
CURRENT_CONTEXT_BUNDLE.md
```

A context bundle should include:

```text
generation timestamp
source paths
hash/checksum if useful
current phase
active constraints
project-specific rule-pack summary
memory command summary
known failures
next recommended task
```

For immediate recovery, Claude Code may produce a short session-open digest:

```text
current repo state
current phase
recent decisions
active constraints
known failures
next recommended task
```

Claude AI must not assume context freshness unless it comes from a current bundle, current Claude Code output, current memory artifact, or Dragon-confirmed current fact.

---

## 20. Portability Requirement

The framework must be portable. DragonTweaksV2 is one implementation target, not the framework boundary.

### Portable modules

```text
memory-first retrieval
save-after-query value capture
defective query handling
Claude AI / Claude Code role boundary
safe shell policy
Git maturity policy
evidence classification
context bundle/session digest
project-specific rule-pack mechanism
systematic debugging / verification
```

### Project-specific rule packs

Project-specific content belongs under project-specific paths.

Examples:

```text
ops/rules/dragontweaksv2/
ops/projects/dragontweaksv2/
memory/rules/dragontweaksv2/
```

Portable rules must not depend on Minecraft, NeoForge, MineColonies, or DragonTweaksV2 specifics.

### Portability test

Before locking the framework layout, run a paper test against a second project type, such as:

```text
Python CLI tool
no Minecraft
no NeoForge
no MineColonies
no game-specific threading rules
```

Any required DragonTweaksV2-specific assumption is a portability defect.

---

## 21. Planned Build Order

### Phase 0 — Preserve old state only if explicitly authorized

Goal:

```text
Preserve current semi-functional state for reference without treating it as the new architecture.
```

Rules:

```text
Dragon performs Git manually, or Dragon gives one-shot authorization.
Claude Code does not run Git by default.
No old file becomes active context merely because it exists.
```

Checklist:

```text
[ ] Confirm whether preservation is needed.
[ ] Confirm Git handling: human-only or one-shot authorization.
[ ] Preserve current state only if explicitly authorized.
[ ] Do not continue expanding the old archive framework.
```

---

### Phase 1 — Create clean base

Goal:

```text
Create a clean Minecraft + NeoForge + MineColonies base, if DragonTweaksV2 is the first implementation target.
```

Checklist:

```text
[ ] Create new repo, branch, worktree, or clean working directory.
[ ] Add minimal required project dependencies.
[ ] Verify build.
[ ] Verify launch if applicable.
[ ] Document exact versions.
[ ] Checkpoint only with explicit Git authorization.
```

---

### Phase 2 — Add minimum viable project-ops framework

Goal:

```text
Add the smallest reusable framework before feature/refactor work begins.
```

Checklist:

```text
[ ] Create project-ops folder structure.
[ ] Define memory retrieval interface.
[ ] Build keyword/tag retrieval stub.
[ ] Add task wrapper or preflight artifact requirement.
[ ] Add role-boundary rules.
[ ] Add evidence classification template.
[ ] Add safe shell policy.
[ ] Add Git maturity model.
[ ] Add memory chunk schema.
[ ] Add durability scoring.
[ ] Add memory inbox governance.
[ ] Add defective-query reduced live log.
[ ] Add local context bundle or session digest format.
```

---

### Phase 3 — Seed and test operational memory

Goal:

```text
Make memory retrieval useful before feature work starts.
```

Checklist:

```text
[ ] Create seed chunks from current confirmed facts and current verified repo facts.
[ ] Do not bulk-import archive material.
[ ] Implement keyword/tag retrieval over seed chunks.
[ ] Enforce hard-rule ranking.
[ ] Run retrieval acceptance tests.
[ ] Fix missing seed coverage.
[ ] Verify preflight artifact is produced before task execution.
[ ] Verify no manual end-session archive ritual is required.
```

---

### Phase 4 — Add optional semantic/vector retrieval only after stub success

Goal:

```text
Improve recall without weakening hard-rule enforcement.
```

Checklist:

```text
[ ] Add semantic/vector backend only after keyword/tag stub works.
[ ] Preserve hard-rule ranking above similarity.
[ ] Compare semantic results against known acceptance tests.
[ ] Reject or quarantine stale, soft, or near-match garbage.
[ ] Keep keyword/tag fallback available.
```

---

### Phase 5 — Add LLM integration last

Goal:

```text
Add LLM integration only after clean base and operational memory are stable.
```

Checklist:

```text
[ ] Confirm first five deliverables are complete.
[ ] Confirm hard-rule retrieval works.
[ ] Confirm task wrapper or preflight artifact works.
[ ] Confirm role-boundary prompt exists.
[ ] Confirm safe shell/Git policy exists.
[ ] Implement LLM integration in small verified steps.
[ ] Verify build after each increment.
[ ] Save only durable implementation lessons.
```

---

## 22. Initial Acceptance Tests

The framework is judged by behavior, not document completeness.

Required tests:

```text
[ ] Request about advisor panel stock lookup retrieves cached diagnostic data rule.
[ ] Request about GUI/render logic retrieves no-live-world-query rule.
[ ] Request about Git use retrieves Git maturity policy.
[ ] Request about Claude.ai reviewing Claude Code output retrieves evidence-classification template.
[ ] Request about old repo files requires current-repo validation before use.
[ ] Request about disposable pricing/version data does not create permanent memory by default.
[ ] Request about defective query behavior retrieves reduced logging and serious-failure criteria.
[ ] Request requiring Windows shell commands retrieves PowerShell 5.1 compatibility rule.
[ ] Request requiring Claude Code implementation retrieves No Human-as-File-Transport rule.
[ ] If MCP sandbox bridge is tested, prompt/document artifact class rules are retrieved.
[ ] If MCP sandbox bridge is tested, sandbox deposit is classified as transport-only and not execution approval.
```

### Lightweight metrics

Track only if useful:

```text
memory queries per task
successful recall rate
missed known-rule count
bad-query count
manual paste requests
external escalation count
save-candidate count
violations per week
token cost per task
```

Metrics must not become ritual.

---

## 23. Current Plan Summary

```text
1. Retire the archive as active workflow.
2. Start from a clean base.
3. Build the minimum project-ops framework first.
4. Define the retrieval interface before choosing advanced backend behavior.
5. Use keyword/tag retrieval first.
6. Seed memory from current confirmed rules and verified current facts, not archive bulk import.
7. Enforce memory-first retrieval with a wrapper or artifact.
8. Rank hard rules above soft similarity.
9. Separate rules, facts, preferences, decisions, commands, failures, and temporary context.
10. Use durability scoring before saving new information.
11. Keep the inbox temporary and explicitly classified.
12. Convert serious query failures into retrieval tests.
13. Maintain strict Claude AI / Claude Code role boundaries.
14. Treat the Claude.ai MCP sandbox bridge as optional proof-of-concept artifact handoff until verified.
15. Use local context authority only.
16. Add LLM integration last.
```

---

## 24. Guiding Principles

```text
The archive is not the memory system.

Memory must be retrieval-first and workflow-enforced.

The retrieval interface comes before the retrieval backend.

A simple working stub beats an ambitious unproven backend.

Hard rules outrank soft similarity.

Seed chunks come from current confirmed rules and verified facts, not bulk archive import.

External reasoning should be classified before saving.

Disposable queries should not pollute permanent memory.

The inbox is staging, not a second archive.

Bad queries should improve future retrieval.

Claude AI must not do Claude Code’s job.

Dragon must not be used as file or shell transport.

Claude AI should output self-contained Claude Code execution blocks.

Claude.ai sandbox deposits are transport artifacts only, not authority, memory, or approval.

Git operations are maturity-gated and task-authorized.

Current local evidence outranks old memory.

The framework must be portable, but the first implementation must validate it concretely.

The first five deliverables must ship before feature work begins.
```

---

## 25. Confidence

Confidence in this revised direction: **0.90**

Main remaining uncertainties:

```text
The exact clean repo / clean branch / clean worktree choice still needs execution selection.
The first project-specific seed chunks require current repo inspection.
The enforcement layer may need to be lighter or heavier after real Claude Code behavior is observed.
Semantic/vector retrieval may or may not be worth adding after the keyword/tag stub works.
```
