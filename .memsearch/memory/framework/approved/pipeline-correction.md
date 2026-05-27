**Title:** Candidate approval pipeline — automated validation before human review
**Type:** correction
**Intent triggers:** candidate pipeline, approval pipeline, human review, automated validation, confidence threshold, durability validation
**Source/evidence:** User-identified gap in memory-system-architecture.md Section 5. Current pipeline front-loads human review on every candidate regardless of confidence. Supersession and deduplication workflows already use confidence-threshold-gated human review correctly — the general pipeline never got updated to match.
**Rule or fact:** The correct candidate approval pipeline is: candidate capture -> automated durability validation -> if confidence >= threshold, promote to approved automatically -> if confidence < threshold, route to human review -> human reviews only ambiguous or conflicting cases -> approved. Human review is a fallback for low-confidence cases, not a required step for every candidate.
