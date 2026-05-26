# Rule Pack Structure

## Principle

The memory system is divided into layers. The base framework is portable across projects.
Domain memory packs are self-contained knowledge libraries. Project memory is local to the
active project. Domain packs must not hard-depend on each other.

## Memory System Layers

```text
Base framework memory
    Defines agent behavior, approval rules, candidate capture, validation gates,
    anti-pollution rules, shell/git restrictions, retrieval and indexing rules.
    Portable across projects. Must not contain domain facts.

Domain memory packs
    Self-contained reusable knowledge libraries for a specific domain.
    Examples: MineColonies, NeoForge, Minecraft, Java, development workflows.
    Portable. Must not hard-depend on other domain packs.
    May include optional "See Also" cross-references (advisory only).

Project memory
    Facts specific to the active project: local paths, verified commands, decisions,
    architecture facts, corrected mistakes, current repo constraints.
    Must not pollute reusable domain packs.

Raw intake quarantine
    Unprocessed source material: forum posts, Discord dumps, wiki pages, articles, notes.
    Not indexed. Not trusted memory. Must survive extraction, user approval, and
    durability validation before becoming approved memory.

Candidate workflow
    Extracted candidates awaiting user review and durability validation.
    Stages: extracted -> tentative-approved -> validated -> approved.
    Not indexed until approved.

Deprecated / tombstoned memory
    Superseded or incorrect memory that must not be deleted but must not be indexed.
    Preserved for audit history. All deprecated entries carry Index: false metadata.

Optional see-also cross-references
    Domain packs may reference related packs as advisory context.
    References are advisory only. A domain pack must function without the referenced
    pack present.
    Allowed: "See also: NeoForge domain pack"
    Forbidden: "Requires: NeoForge domain pack"
```

## Directory Structure

```text
framework/
    approved/
    validation/
    changelog.md

domains/
    <domain-name>/
        manifest.md
        raw/
        candidates/
            extracted/
            tentative-approved/
            rejected/
            failed-validation/
        approved/
        deprecated/
        validation/
        changelog.md

projects/
    <project-name>/
        project/
        candidates/
        validation/
        changelog.md
```

## Portable Framework Contains

- Role boundaries
- Memory-first retrieval protocol
- Candidate-after-query protocol
- Defective query log format
- Query quality rules
- Safe shell policy
- Git maturity model
- Rule pack structure (this file)
- Fact deduplication procedure

## Domain Pack Contains

- Durable domain-specific facts, rules, and patterns
- Domain manifest (name, scope, hard dependencies: none, optional see-also, version, last validated)
- Raw intake folder for unprocessed source material
- Candidate workflow folders
- Deprecated folder for tombstoned entries
- Changelog

## Project Memory Contains

- Project-specific architecture invariants
- Project-specific threading rules
- Project-specific dependency constraints
- Project-specific build and test commands
- Project-specific docs and mappings
- Decisions made and mistakes corrected

## Domain Pack Portability Rules

- A domain pack must remain usable without any other domain pack.
- Optional "See Also" references are advisory only.
- Do not copy domain packs into multiple project directories and let them diverge.
- Prefer a canonical shared domain pack linked by symlink or shared path.
- Projects may submit contribution candidates back into the canonical domain pack queue.

## Rule

Portable framework docs must never contain project-specific rules. Project-specific
rules must never be applied to other projects without explicit review and adaptation.
Domain packs must remain self-contained and must not hard-depend on other domain packs.
