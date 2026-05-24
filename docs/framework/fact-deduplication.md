# Fact Deduplication — Memory System Rule

## 1. Purpose

Before any fact is saved into operational or vector memory, it must be evaluated
against existing memory to prevent redundancy, contradiction, and noise accumulation.
This document defines that evaluation procedure.

---

## 2. What Deduplication Prevents

A new memory entry must not:

- **Duplicate** a fact already stored (same meaning, different wording)
- **Weaken** a stronger, verified fact by replacing it with a vaguer version
- **Contradict** an existing fact without explicitly marking the conflict
- **Fragment** one cohesive operating procedure into disconnected micro-facts
- **Preserve** stale repository-state facts as if they are still current
- **Save session noise** — temporary emotional reactions, one-off incidents, or
  in-progress state — as permanent memory
- **Create multiple canonical versions** of the same decision or rule

---

## 3. Classification Categories

When evaluating a proposed new fact, assign exactly one label:

| # | Category | Action |
|---|----------|--------|
| 1 | **New canonical fact** | Save as new entry |
| 2 | **Duplicate of existing fact** | Discard; do not save |
| 3 | **Refinement of existing fact** | Merge into existing entry; update it |
| 4 | **Superseding replacement** | Replace old entry; mark old as superseded |
| 5 | **Conflict requiring review** | Save with explicit conflict label; do not merge silently |
| 6 | **Related but distinct fact** | Save as separate entry; do not collapse |
| 7 | **Temporary observation not worth saving** | Discard |

---

## 4. Before-Save Procedure

Before saving any new fact:

1. Search existing memory for facts that are semantically equivalent, broader,
   narrower, superseded by, or in conflict with the proposed fact.
2. Assign one classification label from Section 3.
3. Apply the corresponding action from the table.
4. If the label is ambiguous between 3 and 6, prefer **keep separate** — do not
   collapse distinct constraints into one rule.

---

## 5. Merge / Replace / Keep-Separate Rules

**Merge (Refinement):** Update the existing entry in-place. Do not create a second
entry covering the same scope.

**Replace (Superseding):** Delete or archive the old entry. One canonical version
must remain. Do not let both versions coexist without a supersession note.

**Keep separate (Related but distinct):** Facts that appear together often are not
necessarily the same fact. Do not collapse distinct safety constraints, event-bus
rules, or registration guardrails into one vague rule just because they share a
context.

**Prefer durable over incident-specific:** One canonical operating rule outranks
multiple memories from individual incidents. After extracting the durable rule,
the incident details may be discarded.

---

## 6. Conflict Handling

- A fact that contradicts an existing fact must be labeled **Conflict requiring review**.
- Conflicting facts must not be silently merged or averaged.
- The conflict entry must identify: which facts conflict, and what the disagreement is.
- Current repo-verified facts (read from live files) outrank memory, prior
  conversation summaries, and unverified assumptions. When live state conflicts
  with memory, trust live state and update the stale memory entry.

---

## 7. What Not to Deduplicate

Do not treat as duplicates:

- Facts that share a topic but have meaningfully different scopes or conditions
- Guardrails that appear together by convention but enforce separate constraints
- Facts in different memory types (e.g., a `feedback` rule and a `project` state
  fact that both mention the same subsystem)

When in doubt, keep separate rather than collapsing.

---

## 8. Stop Conditions

Do not save a fact — stop the save operation — if any of the following apply:

- The proposed fact is already covered verbatim or semantically by an existing entry
- The proposed fact is weaker or vaguer than the entry it would replace
- The fact is session-scoped and will not apply to future conversations
- The fact reflects an emotional reaction or one-time incident with no durable rule
- The fact describes repo state (file paths, function names) that should be verified
  by reading current code rather than recalled from memory
- No classification from Section 3 can be assigned without ambiguity — resolve the
  ambiguity before saving
