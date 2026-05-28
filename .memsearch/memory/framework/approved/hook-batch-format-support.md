---
title: promote-tentative-approved hook handles batch-format candidate files
type: framework-rule
domain: framework
status: tentative-approved
---

**Title:** promote-tentative-approved hook handles batch-format candidate files
**Type:** Framework rule
**Intent triggers:** promote-tentative-approved, SessionStart hook, batch candidate, batch format, tentative-approved, candidate promotion, autofire, YAML frontmatter, inline Domain, inline Title
**Rule or fact:** The SessionStart hook `scripts/hooks/promote-tentative-approved.py` handles two candidate file formats:

  1. YAML frontmatter files — `---\ntitle: ...\ndomain: ...\n---` at the top of the file.
     Destination is determined by the `domain` field.

  2. Batch files — `## Candidate N` sections with inline `Title:` and `Domain:` fields.
     Used when a human-reviewed batch is moved to tentative-approved as a single file.
     The hook parses each section, resolves the domain via natural-language matching,
     checks for title duplicates against the target approved directory, and deletes the
     batch file once all entries are handled.

  Files without either format are left in place and logged as UNREADABLE.

  Natural-language domain aliases recognized: dragontweaksv2, project memory,
  framework, framework memory, neoforge, minecolonies.
