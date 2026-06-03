#!/usr/bin/env python3
"""One-shot helper: creates review-patches/ and writes the flat-candidate fix patch."""
import pathlib

root = pathlib.Path(__file__).parent.parent
out = root / "review-patches" / "fix-hooks-memory-pipeline-flat-candidates.patch"
out.parent.mkdir(parents=True, exist_ok=True)

content = """\
--- a/scripts/hooks/memory_pipeline.py
+++ b/scripts/hooks/memory_pipeline.py
@@ -218,19 +218,30 @@
         domain = domain_root(root, extracted)
         if domain is None:
             continue
-        for source in sorted(extracted.glob("*.md")):
-            fields, body = parse_frontmatter(source.read_text(encoding="utf-8", errors="ignore"))
-            rel, _ = relationship(domain, fields, body)
-            useless = not fields.get("title") or not normalize_fact(fields, body) or fields.get("usefulness", "").lower() == "trash"
-            suspicious = truthy(fields.get("suspicious", ""))
-            if useless or rel in {"duplicate", "semantic-duplicate"}:
-                dest = move_unique(source, domain / "candidates" / "rejected")
-                result["rejected"].append(str(dest))
-                continue
-            if suspicious or confidence(fields) < AUTO_APPROVE_THRESHOLD:
-                dest = move_unique(source, domain / "candidates" / "review")
-                result["review"].append(str(dest))
-                continue
+        for source in sorted(extracted.glob("*.md")):
+            raw_text = source.read_text(encoding="utf-8", errors="ignore")
+            fields, body = parse_frontmatter(raw_text)
+            rel, _ = relationship(domain, fields, body)
+            if re.match(r"^---\\r?\\n", raw_text):
+                useless = (
+                    not fields.get("title")
+                    or not normalize_fact(fields, body)
+                    or fields.get("usefulness", "").lower() == "trash"
+                )
+                suspicious = truthy(fields.get("suspicious", ""))
+                cand_confidence = confidence(fields)
+            else:
+                # Non-YAML: only a **Source:** [[wikilink]] is required for format validity.
+                # Always route to review; never assign auto-approve confidence.
+                useless = not re.search(r'\\*\\*Source:\\*\\*\\s+\\[\\[.+\\]\\]', raw_text, re.MULTILINE)
+                suspicious = False
+                cand_confidence = 0.0
+            if useless or rel in {"duplicate", "semantic-duplicate"}:
+                dest = move_unique(source, domain / "candidates" / "rejected")
+                result["rejected"].append(str(dest))
+                continue
+            if suspicious or cand_confidence < AUTO_APPROVE_THRESHOLD:
+                dest = move_unique(source, domain / "candidates" / "review")
+                result["review"].append(str(dest))
+                continue
             updated = mark_validated(fields, body, "auto")
             source.write_text(render_candidate(updated, body), encoding="utf-8")
             dest = move_unique(source, domain / "candidates" / "tentative-approved")
"""

out.write_text(content, encoding="utf-8")
print(f"wrote {out}")
