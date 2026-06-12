# Web Memory Ingestion Pipeline

Web/wiki-derived data must never enter approved memory directly.

## Required pipeline

1. Raw deep scrape
2. Cleaned reference
3. Short advisor artifact
4. Optional flavor derived only from advisor
5. Metadata
6. Candidate submission
7. Validation
8. Promotion
9. Reindex/flush

## Role rules

- Raw scrape: evidence only, not default retrieval.
- Cleaned reference: normalized reference, not default player advice.
- Advisor artifact: default retrieval material for practical gameplay questions.
- Flavor artifact: derived from advisor only, never directly from raw/cleaned material.

## Validation requirements

Before promotion, verify:

- crawl depth/tool invocation
- source provenance
- extraction coverage
- ASCII-safe or explicitly allowed encoding
- no parser-hostile markup/table debris
- semantic adequacy for intended query type
- retrieval-quality test against the promoted candidate

Clean-looking scrape output is not proof of valid memory.
