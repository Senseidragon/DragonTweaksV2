---
title: Test Artifact Quarantine — False-Positive Attractor Pattern
type: framework-rule
domain: framework
status: approved
source: .memsearch/candidates/tentative-approved/2026-05-26-salvage-candidates.md
date: 2026-05-26
intent_triggers:
  - test chunk
  - false positive attractor
  - test artifact
  - retrieval pollution
  - chunk scoping
  - broad chunk title
  - memsearch chunk quality
  - chunk replacement
  - retrieval quality
  - acceptance test
  - test-artifact quarantine
  - production memory
---

# Test Artifact Quarantine — False-Positive Attractor Pattern

A chunk with a vague or test-oriented title acts as a false-positive attractor.
It surfaces in unrelated searches and pollutes retrieval results even when its
content is not relevant to the query.

## Rules

- Test fixtures, retrieval probes, and verification chunks must never be written
  into production memory. They must live in a quarantined test folder, a disposable
  test collection, or be deleted immediately after verification.

- Retrieval acceptance tests must not merely check that expected keywords appear
  in top-N results. A test chunk with broad keywords can pass such a test while being
  operationally useless or actively harmful to real recall.

## Correct fix pattern

Replace the vague/test chunk with a properly scoped operational chunk that has:
- a specific descriptive title matching only the relevant domain
- tight intent triggers covering only the relevant use case
- concrete enforcement content with real operational value

## Example

The chunk "Test Chunk — Keyword Retrieval Verification" was replaced with the
NeoForge event bus routing rule chunk (2026-05-25). The acceptance test (test #11)
passed both before and after, but the replacement chunk is both test-correct and
operationally useful.
