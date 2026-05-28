#!/usr/bin/env python3
"""
SessionStart hook: auto-promote tentative-approved candidates.

Validates by title dedup against existing approved memory.
Promotes passing candidates, silently discards duplicates.
Outputs a JSON summary only when files were processed.

Handles two file formats:
  1. YAML frontmatter files  (---\ntitle: ...\ndomain: ...\n---)
  2. Batch files             (## Candidate N sections with inline Domain:/Title: fields)
"""

import json
import os
import re
import shutil
from pathlib import Path

PROJECT_DIR = Path(os.environ.get("CLAUDE_PROJECT_DIR", Path(__file__).parent.parent.parent))
MEM_BASE = PROJECT_DIR / ".memsearch" / "memory"

# Folders with an implicit approved destination (no domain frontmatter needed)
FIXED_DIRS = {
    MEM_BASE / "framework" / "candidates" / "tentative-approved":
        MEM_BASE / "framework" / "approved",
    MEM_BASE / "domains" / "neoforge" / "candidates" / "tentative-approved":
        MEM_BASE / "domains" / "neoforge" / "approved",
    MEM_BASE / "domains" / "minecolonies" / "candidates" / "tentative-approved":
        MEM_BASE / "domains" / "minecolonies" / "approved",
}

# Root tentative-approved: destination determined by domain field
ROOT_TENTATIVE = PROJECT_DIR / ".memsearch" / "candidates" / "tentative-approved"

DOMAIN_MAP = {
    "framework":                MEM_BASE / "framework" / "approved",
    "projects/dragontweaksv2":  MEM_BASE / "projects" / "dragontweaksv2" / "approved",
    "domains/neoforge":         MEM_BASE / "domains" / "neoforge" / "approved",
    "domains/minecolonies":     MEM_BASE / "domains" / "minecolonies" / "approved",
}

# Natural-language domain strings found in batch files -> canonical DOMAIN_MAP key
_NATURAL_DOMAIN_MAP = {
    "dragontweaksv2": "projects/dragontweaksv2",
    "project memory": "projects/dragontweaksv2",
    "framework memory": "framework",
    "framework": "framework",
    "neoforge": "domains/neoforge",
    "minecolonies": "domains/minecolonies",
}


def _resolve_natural_domain(raw: str) -> str | None:
    raw_lower = raw.lower()
    for token, canonical in _NATURAL_DOMAIN_MAP.items():
        if token in raw_lower:
            return canonical
    return None


def parse_frontmatter(text: str) -> dict:
    m = re.match(r"^---\n(.*?)\n---", text, re.DOTALL)
    if not m:
        return {}
    fields = {}
    for line in m.group(1).splitlines():
        if ":" in line:
            k, _, v = line.partition(":")
            fields[k.strip()] = v.strip()
    return fields


def parse_batch_candidates(text: str) -> list[dict]:
    """Extract per-candidate {title, domain, body} dicts from a batch file."""
    results = []
    sections = re.split(r"\n##\s+Candidate\s+\d+\s*\n", "\n" + text)
    for section in sections[1:]:
        entry: dict = {}
        body_lines = []
        for line in section.splitlines():
            matched = False
            for key in ("Title", "Domain"):
                if line.startswith(f"{key}:"):
                    entry[key.lower()] = line.split(":", 1)[1].strip()
                    matched = True
                    break
            if not matched:
                body_lines.append(line)
        entry["body"] = "\n".join(body_lines).strip()
        if entry.get("title"):
            results.append(entry)
    return results


def _title_to_slug(title: str) -> str:
    slug = re.sub(r"[^a-z0-9]+", "-", title.lower()).strip("-")
    return slug[:80]


def _unique_path(target_dir: Path, slug: str) -> Path:
    dest = target_dir / f"{slug}.md"
    counter = 1
    while dest.exists():
        dest = target_dir / f"{slug}-{counter}.md"
        counter += 1
    return dest


def approved_titles(approved_dir: Path) -> set:
    titles = set()
    if not approved_dir.exists():
        return titles
    for f in approved_dir.glob("*.md"):
        fm = parse_frontmatter(f.read_text(encoding="utf-8", errors="ignore"))
        t = fm.get("title", "").lower().strip()
        if t:
            titles.add(t)
    return titles


def _process_single(candidate: Path, fm: dict,
                    approved_dir: Path | None,
                    promoted: list, promoted_paths: list, discarded: list) -> bool:
    """Handle a single-candidate frontmatter file. Returns True if consumed."""
    target = approved_dir
    if target is None:
        domain = fm.get("domain", "").strip()
        target = DOMAIN_MAP.get(domain) or DOMAIN_MAP.get(_resolve_natural_domain(domain) or "")
        if target is None:
            return False

    title = fm.get("title", "").lower().strip()
    if title and title in approved_titles(target):
        candidate.unlink()
        discarded.append(candidate.name)
    else:
        target.mkdir(parents=True, exist_ok=True)
        dest = target / candidate.name
        shutil.copy2(candidate, dest)
        candidate.unlink()
        promoted.append(candidate.name)
        promoted_paths.append(str(dest))
    return True


def _process_batch(candidate: Path, text: str,
                   default_approved_dir: Path | None,
                   promoted: list, promoted_paths: list, discarded: list) -> bool:
    """Handle a multi-candidate batch file. Returns True if fully consumed."""
    batch = parse_batch_candidates(text)
    if not batch:
        return False

    all_handled = True
    for entry in batch:
        raw_domain = entry.get("domain", "")
        title = entry.get("title", "")
        title_lower = title.lower().strip()

        target = default_approved_dir
        if target is None:
            canonical = _resolve_natural_domain(raw_domain)
            target = DOMAIN_MAP.get(canonical or "")
        if target is None:
            all_handled = False
            continue

        label = f"{candidate.name}[{title or '?'}]"
        if title_lower and title_lower in approved_titles(target):
            discarded.append(label)
        else:
            target.mkdir(parents=True, exist_ok=True)
            slug = _title_to_slug(title) if title else "untitled"
            dest = _unique_path(target, slug)
            content = f"---\ntitle: {title}\ndomain: {raw_domain}\n---\n\n{entry.get('body', '')}"
            dest.write_text(content, encoding="utf-8")
            promoted.append(label)
            promoted_paths.append(str(dest))

    if all_handled:
        candidate.unlink()
    return all_handled


def process(tentative_dir: Path, approved_dir: Path | None,
            promoted: list, promoted_paths: list, discarded: list):
    if not tentative_dir.exists():
        return
    for candidate in sorted(tentative_dir.glob("*.md")):
        text = candidate.read_text(encoding="utf-8", errors="ignore")
        fm = parse_frontmatter(text)

        if fm:
            _process_single(candidate, fm, approved_dir, promoted, promoted_paths, discarded)
        else:
            consumed = _process_batch(candidate, text, approved_dir, promoted, promoted_paths, discarded)
            if not consumed:
                discarded.append(f"{candidate.name}[UNREADABLE — no frontmatter or batch headers]")


REINDEX_QUEUE = PROJECT_DIR / ".memsearch" / "candidates" / "pending-reindex.txt"
PROMOTE_RESULTS = PROJECT_DIR / ".memsearch" / "candidates" / "promote-results.json"


def main():
    promoted, promoted_paths, discarded = [], [], []

    process(ROOT_TENTATIVE, None, promoted, promoted_paths, discarded)
    for tentative_dir, approved_dir in FIXED_DIRS.items():
        process(tentative_dir, approved_dir, promoted, promoted_paths, discarded)

    if not promoted and not discarded:
        return

    REINDEX_QUEUE.parent.mkdir(parents=True, exist_ok=True)

    # Write reindex breadcrumb — Claude reads this at startup and runs memsearch index.
    if promoted:
        affected_dirs = set(FIXED_DIRS.values()) | set(DOMAIN_MAP.values())
        with REINDEX_QUEUE.open("a", encoding="utf-8") as fh:
            for d in sorted(str(p) for p in affected_dirs):
                fh.write(d + "\n")

    # Self-verify: check every promoted path actually exists on disk.
    missing = [p for p in promoted_paths if not Path(p).exists()]

    lines = []
    if promoted:
        lines.append(f"Promoted: {', '.join(promoted)}")
    if discarded:
        lines.append(f"Discarded (duplicate): {', '.join(discarded)}")
    if missing:
        lines.append(f"VERIFICATION FAILED — paths missing on disk: {', '.join(missing)}")
    elif promoted:
        lines.append(f"Verified: all {len(promoted_paths)} promoted path(s) confirmed on disk.")

    summary = f"promote-tentative-approved: {len(promoted)} promoted, {len(discarded)} discarded"

    result = {
        "systemMessage": summary,
        "hookSpecificOutput": {
            "hookEventName": "SessionStart",
            "additionalContext": "\n".join(lines),
        },
    }
    print(json.dumps(result))


if __name__ == "__main__":
    main()
