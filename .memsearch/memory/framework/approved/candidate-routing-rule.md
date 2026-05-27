**Title:** Candidate routing rule — extracted vs tentative-approved placement
**Type:** rule
**Intent triggers:** candidate placement, extracted, tentative-approved, routing, candidate pipeline, where to put candidate
**Source/evidence:** User-identified gap 2026-05-27. Current docs do not specify which candidates go to extracted vs tentative-approved.
**Rule or fact:** Only two types of candidates belong in extracted/: Claude Code external reasoning results, and first-pass extractions from the raw/ folder. These have not yet been human-reviewed. Candidates drafted directly from a user-identified issue, or explicitly reviewed and approved by the user during drafting, go directly to tentative-approved/ — the user's involvement in drafting constitutes the review step.
