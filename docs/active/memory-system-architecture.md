# Memory System Architecture

Date: 2026-05-26
Status: Active authority
Sources merged:
- memory_system_target_architecture_2026-05-26.md
- memory_system_supersession_addendum_2026-05-26.md
- memory_system_ai_advisory_addendum_2026-05-26.md

---

## Part 1: Target Architecture — Conversation Capture

### 1. Core Problem Identified

The current memory system drifted away from the user's actual intent.

What was built was primarily a self-governing compliance framework: rules about Claude/Claude Code behavior, shell restrictions, git restrictions, query gates, save protocols, and validation constraints.

What was wanted was broader:

1. A reusable governance framework to keep AI agents controlled and on task.
2. A useful knowledge base containing durable domain knowledge.
3. Portable domain memory packs that can be reused across projects.
4. A strict approval workflow preventing AI tools from deciding what becomes memory.
5. A raw intake workflow for messy sources such as Reddit, Discord, forums, wikis, and webpages.

The main failure mode was that the framework for managing memory crowded out the actual act of building useful memory.

---

### 2. Core Architectural Separation

The memory system should be divided into three major layers.

#### 2.1 Base Framework Memory

Purpose:
- Defines how AI agents operate.
- Defines approval rules.
- Defines candidate capture.
- Defines validation gates.
- Defines anti-pollution rules.
- Defines shell/git/process restrictions.
- Defines retrieval and indexing workflow rules.

This layer is portable across projects.

It must not contain domain facts except examples explicitly marked as non-ingestable.

#### 2.2 Domain Memory Packs

Purpose:
- Store reusable durable knowledge about a specific domain.
- Examples:
  - MineColonies
  - NeoForge
  - Minecraft
  - Java
  - Minecraft registries
  - Development workflows

Each domain pack is self-contained and portable.

A domain pack may contain optional "See Also" references to related packs, but must not require those packs to function.

Example:
- A MineColonies pack may say: "See also: NeoForge domain pack for loader lifecycle, event bus, registry, and config mechanics."
- It must not say: "Requires NeoForge domain pack."

#### 2.3 Project Memory

Purpose:
- Store facts specific to the active project.
- For DragonTweaksV2, this includes:
  - local paths
  - verified commands
  - project decisions
  - architecture facts
  - mistakes corrected
  - current repo-specific constraints

Project memory must not pollute reusable domain packs.

---

### 3. Claude Code Approval Boundary

Claude Code must never approve, promote, ingest, or index memory by itself.

Claude Code may only:
1. Identify candidate memory items.
2. Write them into a candidate batch file.
3. Preserve enough context/provenance for later validation.
4. Wait for user action.

Claude Code may not:
- Move candidate information into approved memory.
- Index candidate information.
- Treat test artifacts as production memory.
- Decide that a candidate is durable enough to keep.
- Promote raw source content directly into memory.

This boundary exists because a previous failure allowed a test chunk to enter production memory and pollute retrieval results.

---

### 4. Candidate Capture Workflow

At session end, Claude Code may write candidate items into a batch file.

Candidate format should be simple and manually editable.

Example:

```markdown
## Candidate 1

Domain:
Change type:
Title:
Source/evidence:
Proposed fact/rule:
Reason flagged:
Durability concern:
Recommended action:
```

There should be blank space between candidates so the user can easily delete unwanted entries.

The user reviews the candidate file manually:
- Delete junk candidates.
- Leave candidates worth keeping.
- Edit candidates that need cleanup.
- Move the edited file into a tentative approval folder.

Only after this user action may a validator examine the remaining candidates.

---

### 5. Approval Pipeline

The intended pipeline is:

```text
candidate capture
    ->
user edits/deletes candidates
    ->
tentative-approved folder
    ->
durability validation
    ->
approved domain/project/framework memory
    ->
index/reindex
```

Nothing skips this pipeline.

No item moves directly from candidate to indexed memory.

No item moves directly from raw source to indexed memory.

---

### 6. Durability Validation

After the user moves a candidate batch into tentative approval, a separate validation step checks durability.

The validator decides whether each retained item is:
- durable enough to promote
- too weak
- outdated
- underspecified
- lacking source evidence
- version-scoped incorrectly
- domain-misclassified
- project-specific instead of domain-general
- domain-general instead of project-specific

If validation fails, the item should not be silently discarded or indexed.

Instead, the system should provide feedback:
- what failed
- why it failed
- what evidence is missing
- whether the user should delete, modify, or reconsider it

---

### 7. Raw Folder Role

The `raw/` folder is a junk drawer / quarantine intake zone.

It may contain:
- Reddit posts
- forum posts
- Discord posts
- wiki pages
- webpages
- copied articles
- notes
- miscellaneous source dumps

Raw material is not trusted memory.

Rules:
1. Nothing in `raw/` is indexed directly.
2. Nothing in `raw/` is considered approved.
3. Nothing in `raw/` becomes memory until it survives extraction, user approval, and durability validation.

Raw pipeline:

```text
raw source dump
    ->
candidate extraction
    ->
candidate batch file
    ->
user deletes junk / keeps useful candidates
    ->
tentative-approved
    ->
durability validation
    ->
approved chunks
    ->
index/reindex
```

The validator/extractor may read raw files, but may only output candidate chunks.

---

### 8. Domain Pack Layout

Recommended canonical layout:

```text
MemoryLibrary/
  framework/
    approved/
    validation/
    changelog.md

  domains/
    minecolonies/
      manifest.md
      raw/
        reddit/
        discord/
        forum/
        wiki/
        webpage/
        misc/
      candidates/
        extracted/
        tentative-approved/
        rejected/
        failed-validation/
      approved/
      deprecated/
      validation/
      changelog.md

    neoforge/
      manifest.md
      raw/
      candidates/
      approved/
      deprecated/
      validation/
      changelog.md

  projects/
    DragonTweaksV2/
      project/
      candidates/
      validation/
      changelog.md
```

Project repositories should reference shared domain packs rather than copying them into divergent local versions.

---

### 9. Single Source of Truth for Domains

Domain packs should be canonical shared assets.

Do not create multiple independent editable copies of a MineColonies domain pack across different projects.

Preferred model:
- Maintain one canonical domain pack.
- Link projects to it using symlinks, mounts, or a configured shared path.
- Let projects submit candidate improvements back into the canonical domain contribution queue.

Example:

```text
C:\Users\sense\MemoryLibrary\domains\minecolonies\
C:\Users\sense\Desktop\DragonTweaksV2\memory\domains\minecolonies -> symlink to canonical pack
```

Claude Code may read symlinked domain packs.

Claude Code should not directly modify approved domain files through symlinks.

Claude Code may only write proposed changes into contribution/candidate folders.

---

### 10. Domain Improvements

Domain packs should support self-improvement.

A project may discover:
- a new durable fact
- a replacement for an outdated fact
- a removal candidate for invalid information
- a better version-scoped rule
- a clarified source-backed rule

These should be captured as domain contributions.

Change types:
- ADD
- REPLACE
- REMOVE
- CLARIFY
- DEPRECATE

Example:

```markdown
## Candidate 1

Domain: MineColonies
Change type: REPLACE
Supersedes: approved/old-builder-routing-rule.md
Source/evidence:
Reason:
Proposed replacement:
Durability concern:
```

A domain manifest should indicate pending improvements.

Example:

```markdown
# MineColonies Domain Pack

Domain: MineColonies
Scope: MineColonies mechanics, API behavior, wiki-derived facts, integration behavior
Hard dependencies: None
Optional see also:
- NeoForge domain pack

Status: active
Version: 0.1.0
Last validated: 2026-05-26
Pending improvements: yes
Pending count: 4
```

---

### 11. Soft Cross-References Between Domains

Domain packs may include optional cross-references.

Allowed:
```text
See also: NeoForge domain pack
```

Not allowed:
```text
Requires: NeoForge domain pack
```

Rule:

A domain pack may include "See Also" references to related domain packs when useful for context or navigation. These references must be advisory only. A domain pack must remain usable when the referenced domain pack is absent.

---

### 12. Indexing Model

The durable source of truth should be Markdown files.

The vector index should be treated as generated cache.

Expected model:
1. Approved Markdown chunks are read.
2. Chunks are embedded.
3. Embeddings and metadata are stored in the vector backend.
4. Searches retrieve matching chunks.
5. The index can be deleted and regenerated from the Markdown source.

The domain pack should not depend on an opaque Docker/vector database as its only durable form.

Portable artifact:
```text
approved Markdown domain pack
```

Disposable/rebuildable artifact:
```text
vector index
```

Runtime requirements likely include:
- Docker running
- vector backend running, such as Milvus if using MemSearch
- memory client configured to the correct collection
- approved Markdown available locally
- indexing command run
- flush step if required by the current local setup

---

### 13. Possible Index Organization Models

Three possible models exist.

#### Model A: One Collection Per Project

One collection contains all active memory for a project.

Metadata distinguishes:
- framework
- project
- domain
- domain name

Pros:
- likely simplest with existing project-oriented tools

Cons:
- domain reuse requires reindexing into each project collection

#### Model B: One Collection Per Domain

Each domain has its own collection.

Pros:
- cleanest domain separation
- strong portability

Cons:
- requires search tooling that can query multiple collections and merge results

#### Model C: One Collection With Partitions

One collection contains partitions for:
- framework
- project
- domain_minecolonies
- domain_neoforge

Pros:
- cleaner separation than a flat collection

Cons:
- only useful if the memory tooling exposes partition control cleanly

Recommended initial rule:
Use folder/tag separation first, because it remains portable even if the vector backend changes.

---

### 14. Test Artifact Quarantine

A prior failure occurred because a test chunk was written into production memory.

Rule:

Test fixtures, retrieval probes, and verification chunks must never be written into production memory.

They must live in:
- a quarantined test folder
- a disposable test collection
- or be deleted immediately after verification

Acceptance tests must fail if:
- a test artifact appears in production retrieval
- expected query terms appear only in a test chunk
- a retrieved chunk lacks usable operational/domain content
- a retrieved chunk crashes CLI output due to encoding
- documentation references an unverified tool capability

---

### 15. Retrieval Validation Quality

Acceptance tests must not merely check whether expected strings appear in top-N results.

They should check:
- correct chunk identity
- correct memory layer
- correct domain
- useful content
- correct source/provenance
- absence of test artifacts
- absence of stale assumptions
- no CLI crash on output
- correct version scope where applicable

---

### 16. Tool Capability Verification

Do not design around unverified tool features.

A prior issue involved assuming a keyword fallback flag existed when it did not.

Rule:

Any tool capability used in architecture or validation must be verified against the actual local installed version before being documented as available.

If a command or flag is uncertain, inspect local help output first.

---

### 17. Encoding Rule

Because the working environment includes Windows PowerShell 5.1, indexed memory should be ASCII-only unless UTF-8 output has been explicitly validated.

Avoid:
- arrows (use `->` instead)
- smart quotes
- em dashes
- box drawing
- fancy bullets
- non-ASCII symbols

Use plain ASCII equivalents.

---

### 18. Recovery Direction From Current State

The current system should be repaired, not endlessly polished.

Immediate recovery priorities:
1. Remove or quarantine test chunks from production memory.
2. Remove unverified keyword fallback claims.
3. Replace non-ASCII characters in indexed/seed memory.
4. Reduce duplicate/meta-governance sludge.
5. Enforce the candidate-to-validation-to-index pipeline.
6. Add domain pack structure.
7. Begin seeding actual domain knowledge.

Do not keep expanding governance until useful domain knowledge exists.

---

### 19. Strategic Summary

The target system is:

```text
portable memory operating system
    +
plug-in domain knowledge packs
    +
project-local memory
    +
manual approval gates
    +
durability validation
    +
rebuildable vector index
```

The base framework controls behavior.

The domain packs contain reusable knowledge.

The project memory contains local facts.

The raw folder is an evidence intake quarantine.

The vector database is not the permanent memory source; Markdown is.

---

### 20. Next Planning Task

A future implementation plan should define how to move from the current corrupted/misaligned state to this target design.

That plan should cover:
1. inventory of existing memory files
2. quarantine/removal of test artifacts
3. cleanup of Unicode and invalid tool claims
4. reduction of excessive governance memory
5. introduction of canonical domain library folders
6. symlink or shared-path strategy
7. candidate batch workflow
8. durability validator rules
9. domain manifest format
10. indexing/reindexing commands
11. acceptance test redesign
12. migration strategy for any existing useful facts
13. explicit "Claude Code must not approve memory" enforcement

---

## Part 2: Supersession, Confidence, and Human Adjudication

### 21. Problem Addressed

A durable memory item may enter an approved domain pack and later be discovered to be incorrect.

Example:

```text
Old approved memory:
MineColonists cannot climb ladders.

New evidence:
A MineColonist is observed climbing a ladder, or an official MineColonies Discord clarification states that MineColonists can climb ladders under certain conditions.
```

The system must not simply add the new fact beside the old one.

That would leave contradictory active memory in the system.

Instead, the validator must detect the conflict and determine whether the new fact supersedes, narrows, replaces, or merely qualifies the old fact.

---

### 22. Supersession Rule

When a candidate contradicts an approved domain memory item, the validator must treat it as a supersession case.

The validator must identify:

1. The existing approved fact.
2. The new conflicting candidate fact.
3. The source authority of both.
4. The version scope of both.
5. Whether the new fact fully replaces, partially replaces, narrows, or merely creates an exception to the old fact.
6. Whether confidence is high enough to apply the replacement automatically.

The validator may classify the candidate as:

```text
FULL REPLACE
PARTIAL REPLACE
ADD EXCEPTION
CLARIFY / NARROW
NEEDS TEST
REJECT
HUMAN REVIEW REQUIRED
```

---

### 23. Confidence Threshold Requirement

The validator must maintain a confidence score for supersession decisions.

Example:

```text
Validator confidence: 0.92
Required threshold: 0.85
Decision: apply supersession
```

If confidence meets or exceeds the configured threshold, the validator may prepare a supersession update.

If confidence is below the threshold, the validator must not choose a winner.

Instead, it must create a human review patch.

Rule:

```text
If confidence >= threshold:
  Prepare supersession update.

If confidence < threshold:
  Create human review patch containing both facts.
```

The exact threshold should be configurable.

A reasonable initial default is:

```text
Supersession confidence threshold: 0.85
```

---

### 24. Human Review Patch

When the validator cannot determine with sufficient confidence which fact is correct, it must create a review patch.

The review patch must contain both conflicting facts.

The user resolves the conflict by deleting the incorrect fact from the patch.

The remaining fact is treated as the superseding truth.

Important:

Deleting the incorrect fact from the review patch does not mean directly deleting it from the database.

It means:

```text
This fact loses the conflict.
The remaining fact supersedes it.
The losing active memory should be tombstoned/deprecated.
```

---

### 25. Human Review Patch Format

Recommended format:

```markdown
# Supersession Review Patch

Domain: MineColonies
Conflict type: Direct contradiction
Validator confidence: 0.68
Required threshold: 0.85

Instruction:
Delete the fact that should NOT remain true.
Leave the fact that should supersede the other.

Important:
Deleting a fact from this patch does not directly delete it from memory.
It tells the validator which fact loses the conflict.
The losing approved memory item will be tombstoned/deprecated and removed from active indexing.

---

## Fact A

Current approved memory:
MineColonists cannot climb ladders.

Source:
[existing memory source/provenance]

Status:
Currently active

Path:
approved/pathfinding/colonists-cannot-climb-ladders.md

---

## Fact B

New candidate:
MineColonists can climb ladders under [specific version/condition].

Source:
Official MineColonies Discord clarification, [date], [author/role if known]

Status:
Candidate replacement

Candidate source:
candidates/tentative-approved/[file].md
```

After user review, if only Fact B remains, Fact B supersedes Fact A.

If only Fact A remains, the new candidate is rejected or archived as non-superseding evidence.

---

### 26. Tombstoning / Deprecation Behavior

Incorrect or superseded approved memory should not disappear without trace.

It should be moved to `deprecated/` or marked as deprecated with an explicit tombstone.

Example tombstone:

```markdown
# Deprecated: MineColonists Cannot Climb Ladders

Status: Deprecated
Deprecated date: 2026-05-26
Deprecated by: approved/pathfinding/colonists-ladder-pathfinding.md
Reason:
Superseded by user-adjudicated review patch after new evidence showed the old fact was overbroad or incorrect.

Old claim:
MineColonists cannot climb ladders.

Replacement claim:
MineColonists can climb ladders under [specific verified conditions].

Index: false
Do not use: true
```

The tombstone preserves audit history while preventing the incorrect fact from contaminating active retrieval.

---

### 27. Active Index Rule

Deprecated memory must not remain in the active index.

Rules:

```text
Approved memory: indexed
Candidate memory: not indexed
Raw memory: not indexed
Deprecated memory: not indexed
Rejected memory: not indexed
Review patches: not indexed
```

If the indexing tool cannot selectively exclude folders, deprecated files must include explicit metadata:

```text
Status: Deprecated
Index: false
```

The preferred behavior is to exclude `deprecated/`, `raw/`, `candidates/`, `rejected/`, and `review/` folders from active indexing entirely.

---

### 28. Authority and Scope Comparison

The validator should compare source authority before superseding an old fact.

Suggested authority order for MineColonies-style domain facts:

```text
1. Current official documentation / wiki
2. Current official Discord clarification from recognized team/dev source
3. Current source code or issue tracker evidence
4. Reproducible in-game test in the target version
5. High-quality current guide with clear evidence
6. Reddit/forum/community anecdote
7. Old unsourced memory
```

Authority alone is not enough.

The validator must also compare scope:

```text
Minecraft version
MineColonies version
Loader
Source date
Observed conditions
Whether the claim is absolute or conditional
```

Many supersession cases should replace an absolute claim with a scoped claim rather than another absolute claim.

Bad replacement:

```text
MineColonists can climb ladders.
```

Better replacement:

```text
MineColonists can climb ladders in the verified version/context when pathfinding recognizes the ladder route as valid.
```

---

### 29. Conflict Search Requirement

Before approving a new domain fact, the validator must search the existing approved domain pack for related or contradictory claims.

If a contradiction is found, the validator must not treat the new candidate as a simple ADD.

It must route the candidate through the supersession workflow.

---

### 30. Self-Healing Pipeline

Full self-healing flow:

```text
new candidate fact
    ->
validator searches approved memory for conflicts
    ->
conflict found
    ->
validator compares source authority and scope
    ->
validator assigns supersession confidence
    ->
if confidence >= threshold:
       prepare supersession update
       tombstone old fact
       approve scoped replacement
       reindex active memory
    ->
if confidence < threshold:
       create human review patch
       user deletes losing fact
       validator interprets remaining fact as winner
       tombstone losing approved fact
       approve scoped replacement if valid
       reindex active memory
```

---

### 31. Design Principle (Supersession)

The validator may assist with self-healing, but it must not overrule uncertainty.

When evidence is strong, the validator can recommend or prepare replacement.

When evidence is ambiguous, the validator must produce a human review patch.

The user's edit to the patch is the final adjudication signal.

---

## Part 3: Optional AI Advisory Model Selection (Future Upgrade)

Status: Optional future upgrade. Not an active rebuild requirement.

---

### 32. Scope Control

This feature is explicitly a possible future upgrade.

It is not required for the initial memory-system recovery or rebuild.

The current priority remains:

1. Clean polluted memory.
2. Enforce candidate approval boundaries.
3. Establish domain packs.
4. Establish raw intake quarantine.
5. Establish durability validation.
6. Establish supersession review and tombstoning.

AI advisory review may be added later if the added complexity is justified.

---

### 33. Core Principle (Advisory)

AI advisory models may help evaluate memory candidates, but they must not approve memory.

Allowed:

```text
AI may critique.
AI may compare.
AI may estimate confidence.
AI may identify missing evidence.
AI may recommend ADD / REPLACE / REJECT / HUMAN REVIEW.
AI may suggest better wording or scope.
```

Forbidden:

```text
AI may not approve memory.
AI may not promote memory.
AI may not index memory.
AI may not tombstone memory.
AI may not delete memory.
AI may not bypass user approval.
AI may not bypass validator thresholds.
```

Summary rule:

```text
AI suggests.
Validator checks.
User adjudicates.
System indexes.
```

---

### 34. Model Eligibility Filters

A model should only be eligible if it satisfies the configured task constraints.

Known user preferences:

1. The model name should expose its designed parameter count, such as `27B`, `70B`, `235B`, etc.
2. Generic router models or models without clear parameter-size indication may be excluded.
3. `IT` in a model name may be treated as an instruction-tuned / instruction-following indicator.
4. Context window is not capped, but there is a floor.
5. A likely initial context-window floor is 128k tokens.
6. Models below the context floor should be excluded from the candidate index.
7. Pricing must be available.
8. Task category must match the advisory job.

---

### 35. Cost Policy

The default goal is:

```text
Use the cheapest viable model that clears the task floor.
```

However, large models are allowed as advisory candidates when the cost-benefit ratio is favorable for high-impact, durable-memory decisions.

---

### 36. Advisory Model Rule (Compact)

```text
The system normally uses the lowest-cost viable advisory model with an acceptable reliability score. If that model returns low confidence, poor provenance handling, internal inconsistency, vague reasoning, or a high-impact uncertain decision, the same prompt may be submitted to a second model in the same task class. If the models materially disagree, the validator must create a human review patch rather than promoting memory automatically.
```

---

### 37. Model Disagreement Rule

If two advisory models materially disagree, the validator must not automatically promote memory.

Result:

```text
Human review required.
```

Model disagreement should create or strengthen the case for a human review patch.

---

### 38. Advisory Pipeline (Optional Future)

```text
candidate or conflict detected
    ->
select cheapest viable advisory model with acceptable reliability
    ->
run advisory prompt
    ->
if advisory output is clear and adequate:
       feed advisory result into validator confidence assessment
    ->
if advisory output is uncertain or weak:
       optionally run same prompt against second model
    ->
if models agree and confidence threshold is met:
       prepare validator recommendation
    ->
if models disagree or confidence remains low:
       create human review patch
```

No part of this pipeline grants approval authority to the model.

---

### 39. Design Status (Advisory)

This is a useful and appropriate future upgrade, but it is not part of the initial recovery scope.

The memory system should first become correct, inspectable, and safe without advisory AI.

After the core system works, advisory model selection can be added as a controlled enhancement.
