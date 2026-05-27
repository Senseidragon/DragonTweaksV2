# Deprecated: Candidate Approval Pipeline — Human Review First

Status: Deprecated
Deprecated date: 2026-05-27
Deprecated by: framework/approved/pipeline-correction.md
Reason: Superseded by user adjudication. Automated validation now runs first; human review is a fallback for low-confidence cases only.

Old pipeline:
candidate capture -> user edits/deletes candidates -> tentative-approved -> durability validation -> approved -> index/reindex

Replacement:
candidate capture -> automated durability validation -> if confidence >= threshold, promote automatically -> if confidence < threshold, route to human review -> approved -> index/reindex

Index: false
Do not use: true
