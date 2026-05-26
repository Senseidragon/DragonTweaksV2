# Memory System Document Index

## 1. Purpose

This file helps Claude Code decide which memory-system document(s) to read for a given task. It is a routing aid only -- not a rule source. Always read the linked documents for actual rules.

---

## 2. How to Use This Index

1. Match your task keyword(s) against the Routing Table below.
2. Read only the document(s) listed for that row.
3. If a task spans multiple rows, read the listed combination (see Section 4).
4. Stop if a Stop Condition applies (see Section 6).

Do not read every framework file. Do not derive rules from this index itself.

---

## 3. Routing Table

| Task keyword(s) | Read this file |
|---|---|
| Memory-first retrieval, consult memory before reasoning, retrieval flow | `docs/framework/memory-first-retrieval.md` |
| Query construction, search quality, query reframing, failed recall, bad search | `docs/framework/query-quality-rules.md` |
| Save validated finding, post-query save, memory chunk, durable value extraction | `docs/framework/save-after-query-protocol.md` |
| Log bad query, defective query, misleading query, failed query, query entry | `docs/framework/defective-query-log.md` |
| Claude AI vs Claude Code, role separation, who reads files, who pastes output | `docs/framework/role-boundaries.md` |
| Shell command, script execution, PowerShell, command safety, side effects | `docs/framework/safe-shell-policy.md` |
| Rule pack layout, framework vs project-specific, rule organization, rule structure | `docs/framework/rule-pack-structure.md` |
| Git discipline, git maturity level, repository commits, git constraints | `docs/framework/git-maturity-model.md` |
| MemSearch recovery, Docker/Milvus recovery, reindex, flush, refresh procedure | `docs/recovery/MemSearchRecovery.md` |
| Inspect or modify refresh script behavior | `scripts/memsearch-refresh.ps1` (only if script behavior must be inspected) |
| Compliance review, auditing memory-system adherence, checking whether memory-system rules were followed | `docs/framework/role-boundaries.md` + `docs/framework/memory-first-retrieval.md`. For subsystem audits, see Section 4. |
| Adding, renaming, reorganizing, or removing a framework document | `docs/framework/rule-pack-structure.md` |
| Fact deduplication, canonical facts, duplicate memory entries, superseded memory, conflicting memory facts, before-save memory quality control | `docs/framework/fact-deduplication.md` |
| Memory system architecture, approval pipeline, domain packs, supersession, candidate workflow, advisory models | `docs/active/memory-system-architecture.md` |

---

## 4. Required Read Combinations

- **Troubleshooting failed memory recall:**
  `memory-first-retrieval.md` + `query-quality-rules.md` + `defective-query-log.md`

- **Adding a validated memory entry:**
  `memory-first-retrieval.md` + `save-after-query-protocol.md`

- **Running or modifying recovery commands:**
  `MemSearchRecovery.md` + `safe-shell-policy.md`

- **Deciding whether to run a shell command:**
  `safe-shell-policy.md` only (add `git-maturity-model.md` if the command involves git)

- **Defining Claude AI / Claude Code responsibilities:**
  `role-boundaries.md` only (add `safe-shell-policy.md` if commands are involved)

- **Evaluating and logging a defective query:**
  `query-quality-rules.md` + `defective-query-log.md`

- **Designing or reorganizing rule packs:**
  `rule-pack-structure.md` only

- **Evaluating whether a proposed fact should be saved, merged, replaced, kept separate, or rejected:**
  `docs/framework/fact-deduplication.md`

- **Auditing general memory-system compliance:**
  `docs/framework/role-boundaries.md` + `docs/framework/memory-first-retrieval.md`

- **Auditing a specific subsystem's compliance:**
  Above two files, plus that subsystem's routed source file from the Routing Table.

---

## 5. Forbidden Broad-Read Behavior

- **Do not read all framework files by default.** Read only what the task requires.
- **Do not treat this index as the authoritative rule source.** Rules live in the documents it points to.
- **Do not infer missing rules from prior conversations, old repo structure, old file names, obsolete files, or unverified memory.** Verify file existence with Glob or Read before citing any path.
- **Do not run commands** unless the task explicitly requires it and `safe-shell-policy.md` has been read.
- **Do not update vector memory** unless explicitly authorized by the user for the current task.

---

## 6. Stop Conditions

Stop and report to the user (do not proceed) if:

- A referenced framework file does not exist at the expected path.
- The task requires running a shell command but the safe-shell policy has not been read.
- The task requires git operations but the git maturity level has not been confirmed for the session.
- The task requires updating vector memory but no explicit authorization has been given.

---

## 7. Source Documents

Source files are listed in the Routing Table above. Section 3 is the routing authority for this index. Do not open source files merely because they appear in this document; open them only when the current task routes to them or when explicitly authorized by the task prompt.

---

## 8. Deprecated Files (Not Active Authority)

The following files are preserved for history but must not be treated as active authority.
Do not route tasks to them. Do not derive rules from them.

| File | Reason deprecated |
|---|---|
| `docs/deprecated/old-plans/current_plan_and_checklist.md` | Superseded; plan-era document, no longer active |
| `docs/deprecated/old-plans/cleanstart_project_operations_plan_revised_with_mcp_handoff.md` | Superseded; old operations plan, no longer active |
| `docs/memory_system_target_architecture_2026-05-26.md` | Merged into `docs/active/memory-system-architecture.md` |
| `docs/memory_system_supersession_addendum_2026-05-26.md` | Merged into `docs/active/memory-system-architecture.md` |
| `docs/memory_system_ai_advisory_addendum_2026-05-26.md` | Merged into `docs/active/memory-system-architecture.md` |
| `docs/MemSearchRecovery.md` | Canonical copy moved to `docs/recovery/MemSearchRecovery.md` |

---

## 9. Maintenance Notes

- Update the Routing Table when a new framework file is added or renamed.
- Do not add rule content here -- keep this file as routing only.
- If a file in the Source Document List is removed, add a Stop Condition entry noting the removal.
- When files are deprecated, add them to Section 8 with a reason.
