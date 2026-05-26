# Fact Deduplication -- Memory System Rule

## 1. Purpose

Before any fact is approved into operational or vector memory, it must be evaluated
against existing memory to prevent redundancy, contradiction, and noise accumulation.
This document defines that evaluation procedure.

This procedure applies to the validator, not to Claude Code. Claude Code identifies
candidates. The validator classifies and routes them. The user adjudicates ambiguous
cases.

---

## 2. What Deduplication Prevents

A new memory entry must not:

- **Duplicate** a fact already stored (same meaning, different wording)
- **Weaken** a stronger, verified fact by replacing it with a vaguer version
- **Contradict** an existing fact without explicitly marking the conflict
- **Fragment** one cohesive operating procedure into disconnected micro-facts
- **Preserve** stale repository-state facts as if they are still current
- **Save session noise** -- temporary emotional reactions, one-off incidents, or
  in-progress state -- as permanent memory
- **Create multiple canonical versions** of the same decision or rule

---

## 3. Classification Categories

When evaluating a proposed candidate fact, assign exactly one label:

| # | Category | Definition | Action |
|---|----------|-----------|--------|
| 1 | **New canonical fact** | No existing entry covers this | Approve as new entry |
| 2 | **Duplicate** | Same meaning and scope, different wording | Discard; do not approve |
| 3 | **Near-duplicate** | Substantially similar with minor scope or wording difference | Merge into existing entry or discard; do not create a second entry |
| 4 | **Clarification** | Adds precision or narrows an existing fact without conflicting | Merge into existing entry as a scoped refinement |
| 5 | **Conflict** | Directly contradicts an existing approved fact | Route to supersession workflow; do not merge silently |
| 6 | **Replacement** | Clearly replaces an outdated fact; source authority and scope are strong | Replace old entry; tombstone old entry in deprecated/ |
| 7 | **Supersession** | High-confidence replacement after validator comparison of authority, scope, and evidence | Apply supersession update; tombstone old fact; approve scoped replacement |
| 8 | **Related but distinct** | Same topic, meaningfully different scope or condition | Approve as separate entry; do not collapse |
| 9 | **Temporary observation** | Session-scoped, no durable reuse value | Discard |

---

## 4. Before-Approval Procedure

Before approving any candidate fact:

1. Search existing approved memory for facts that are semantically equivalent, broader,
   narrower, superseded by, or in conflict with the candidate.
2. Assign one classification label from Section 3.
3. Apply the corresponding action from the table.
4. If the label is ambiguous between 4 (Clarification) and 8 (Related but distinct),
   prefer **keep separate** -- do not collapse distinct constraints into one rule.
5. If confidence is below the configured threshold, do not choose automatically.
   Route to human review patch instead.

---

## 5. Confidence Threshold and Human Review

The validator must assign a confidence score when classifying candidate facts in
conflict or supersession cases.

```text
If confidence >= threshold (default 0.85):
  Apply the classification decision.

If confidence < threshold:
  Do not choose automatically.
  Create a human review patch containing both the existing fact and the candidate.
  The user resolves the conflict by deleting the losing fact from the patch.
  The validator interprets the remaining fact as the winner.
```

Confidence below threshold applies especially to:
- Category 5 (Conflict)
- Category 6 (Replacement)
- Category 7 (Supersession)
- Any case where source authority, version scope, or evidence is unclear

---

## 6. Merge / Replace / Keep-Separate Rules

**Merge (Clarification or Near-Duplicate):** Update the existing entry. Do not create
a second entry covering the same scope.

**Replace (Replacement or Supersession):** Tombstone the old entry in the deprecated/
folder. One canonical version must remain. Do not let both versions coexist in active
memory without a supersession note.

**Keep separate (Related but distinct):** Facts that appear together often are not
necessarily the same fact. Do not collapse distinct safety constraints, event-bus
rules, or registration guardrails into one vague rule just because they share a context.

**Prefer durable over incident-specific:** One canonical operating rule outranks
multiple memories from individual incidents. After extracting the durable rule,
the incident details may be discarded.

---

## 7. Conflict Handling

- A fact that contradicts an existing approved fact must be labeled **Conflict** (Category 5).
- Conflicting facts must not be silently merged or averaged.
- The conflict entry must identify: which facts conflict, and what the disagreement is.
- Route to the supersession workflow. See `docs/active/memory-system-architecture.md`
  Sections 22-30.
- If confidence is below threshold, create a human review patch rather than choosing
  automatically.
- Current repo-verified facts (read from live files) outrank memory, prior
  conversation summaries, and unverified assumptions. When live state conflicts with
  memory, trust live state and flag the stale memory entry for review.

---

## 8. Tombstoning / Deprecation

Superseded or replaced approved memory must not disappear without trace.

Move the old entry to deprecated/ with a tombstone note indicating:
- What fact it was
- What superseded it
- The date and reason
- Index: false (to exclude from active retrieval)

See `docs/active/memory-system-architecture.md` Section 26 for the tombstone format.

---

## 9. What Not to Deduplicate

Do not treat as duplicates:

- Facts that share a topic but have meaningfully different scopes or conditions
- Guardrails that appear together by convention but enforce separate constraints
- Facts in different memory types (e.g., a `feedback` rule and a `project` state
  fact that both mention the same subsystem)

When in doubt, keep separate rather than collapsing.

---

## 10. Stop Conditions

Do not approve a fact -- stop the approval -- if any of the following apply:

- The proposed fact is already covered verbatim or semantically by an existing entry
- The proposed fact is weaker or vaguer than the entry it would replace
- The fact is session-scoped and will not apply to future conversations
- The fact reflects an emotional reaction or one-time incident with no durable rule
- The fact describes repo state (file paths, function names) that should be verified
  by reading current code rather than recalled from memory
- No classification from Section 3 can be assigned without ambiguity -- resolve the
  ambiguity before approving
- Confidence is below threshold and a human review patch has not been created --
  create the patch before proceeding
