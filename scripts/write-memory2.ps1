$dir = "C:\Users\sense\.claude\projects\C--Users-sense-Desktop-DragonTweaksV2\memory"

Set-Content "$dir\feedback_context7_link_conversion.md" -Encoding UTF8 @'
---
name: Context7 raw output requires wikilink conversion before use
description: Context7 returns relative markdown links; must be converted to wikilink format before a candidate is valid
type: feedback
---

Context7 returns documentation with standard relative markdown links (e.g. `../concepts/registries.md`). Our memory entries require wikilink format (`[[...]]`). Raw Context7 output pasted directly into a candidate is not valid as-is.

**Why:** Consistent wikilink format makes sources machine-parseable and Obsidian-clickable. Relative links from Context7 are meaningless outside the upstream repo context.

**How to apply:** When capturing Context7 data as a candidate, author the content -- convert relative links to wikilinks, or omit them if they don't resolve to local files. Never paste raw Context7 output verbatim if it contains relative markdown links.
'@

Write-Host "Memory file written."
