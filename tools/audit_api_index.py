#!/usr/bin/env python3
"""
audit_api_index.py — Deterministic audit of docs/STUB_INDEX.md.

Reads the index, inspects every Java file labeled "Stub file", measures
concrete signals from the source, assigns an evidence-based label, and
writes docs/API_INDEX_PROPOSED.md.  docs/STUB_INDEX.md is never modified.

Usage:
    python tools/audit_api_index.py [--repo-root PATH]

Defaults to the parent of the script's directory as the repo root.
"""

import argparse
import re
import sys
from collections import Counter
from dataclasses import dataclass, field
from pathlib import Path
from typing import Optional


# ---------------------------------------------------------------------------
# Label constants
# ---------------------------------------------------------------------------
LABEL_RICH = "Rich API surface"
LABEL_MEANINGFUL = "Meaningful API surface"
LABEL_THIN = "Thin declaration"
LABEL_PKG_INFO = "Package/info-only"
LABEL_MISSING = "Missing source file"
LABEL_UNCERTAIN = "Uncertain"


# ---------------------------------------------------------------------------
# Data structures
# ---------------------------------------------------------------------------
@dataclass
class IndexEntry:
    filename: str           # e.g. "ModConfigSpec.java"
    original_desc: str      # raw description from index, e.g. "Stub file"
    section: str            # e.g. "neoforge/common"
    base_dir: str           # e.g. "docs/stubs/net/neoforged"
    full_path: Optional[Path] = None

    @property
    def is_stub_label(self) -> bool:
        return self.original_desc.strip() == "Stub file"


@dataclass
class FileSignals:
    path: Path
    exists: bool
    line_count: int = 0
    public_type_count: int = 0
    public_protected_method_count: int = 0
    constructor_count: int = 0
    public_static_final_field_count: int = 0
    has_javadoc: bool = False
    first_javadoc_sentence: str = ""
    notable_methods: list = field(default_factory=list)
    is_package_info: bool = False


# ---------------------------------------------------------------------------
# Parsing the index
# ---------------------------------------------------------------------------
def parse_stub_index(index_path: Path) -> list:
    """
    Parse STUB_INDEX.md and return all IndexEntry objects.

    Format:
      ## net/neoforged — `docs/stubs/net/neoforged/`
      ### neoforge/attachment
      - `AttachmentHolder.java` — Description text
    """
    entries = []
    current_base_dir = ""
    current_section = ""

    re_base = re.compile(r"^##\s+\S+\s+[—–-]+\s+`([^`]+)`")
    re_section = re.compile(r"^###\s+(.+)")
    re_entry = re.compile(r"^-\s+`([^`]+\.java)`\s+[—–-]+\s+(.+)")

    with index_path.open(encoding="utf-8") as fh:
        for line in fh:
            line = line.rstrip("\n")

            m = re_base.match(line)
            if m:
                current_base_dir = m.group(1).rstrip("/")
                current_section = ""
                continue

            m = re_section.match(line)
            if m:
                current_section = m.group(1).strip()
                continue

            m = re_entry.match(line)
            if m and current_base_dir:
                entries.append(IndexEntry(
                    filename=m.group(1),
                    original_desc=m.group(2).strip(),
                    section=current_section,
                    base_dir=current_base_dir,
                ))

    return entries


def resolve_paths(entries, repo_root: Path) -> None:
    """Attach absolute Path objects to each entry."""
    for e in entries:
        rel = Path(e.base_dir) / e.section / e.filename
        e.full_path = repo_root / rel


# ---------------------------------------------------------------------------
# Java source analysis
# ---------------------------------------------------------------------------
_RE_PUBLIC_TYPE = re.compile(
    r"^\s*public\s+(?:(?:abstract|final|sealed|non-sealed|static|strictfp)\s+)*"
    r"(?:class|interface|enum|record|@interface)\s+\w+"
)

_RE_METHOD = re.compile(
    r"^\s*(?:public|protected)\s+"
    r"(?:(?:static|final|abstract|synchronized|native|default|strictfp)\s+)*"
    r"(?!(?:class|interface|enum|record)\b)"
    r"[\w<>\[\],\s?@.]+\s+"
    r"(\w+)\s*\("
)

_RE_CONSTRUCTOR = re.compile(
    r"^\s*(?:public|protected)\s+([A-Z]\w*)\s*\("
)

_RE_PUBLIC_STATIC_FINAL = re.compile(
    r"^\s*public\s+static\s+final\s+"
)

_KEYWORDS = {"class", "interface", "enum", "record", "if", "for", "while", "switch", "return"}


def _extract_first_javadoc_sentence(lines: list) -> str:
    """Find the first /** ... */ block and extract the first complete sentence."""
    in_doc = False
    fragments = []

    for raw in lines:
        line = raw.strip()
        if not in_doc:
            if "/**" in line:
                in_doc = True
                after = line[line.index("/**") + 3:].strip(" *")
                if after and not after.startswith("/"):
                    fragments.append(after)
        else:
            if "*/" in line:
                break
            clean = re.sub(r"^\s*\*\s?", "", line)
            if clean:
                fragments.append(clean)

        if in_doc and fragments:
            combined = " ".join(fragments)
            m = re.search(r"(.+?\.)\s", combined + " ")
            if m:
                return m.group(1).strip()[:200]

    if fragments:
        combined = " ".join(fragments)
        m = re.search(r"(.+?\.)\s?", combined + " ")
        if m:
            return m.group(1)[:200].strip()
        return combined[:200].strip()
    return ""


def analyze_java_file(path: Path) -> FileSignals:
    sig = FileSignals(path=path, exists=path.exists())
    if not sig.exists:
        return sig

    try:
        content = path.read_text(encoding="utf-8", errors="replace")
    except OSError:
        return sig

    lines = content.splitlines()
    sig.line_count = len(lines)
    sig.is_package_info = path.name == "package-info.java"

    if sig.is_package_info:
        sig.has_javadoc = "/**" in content
        if sig.has_javadoc:
            sig.first_javadoc_sentence = _extract_first_javadoc_sentence(lines)
        return sig

    sig.has_javadoc = "/**" in content
    if sig.has_javadoc:
        sig.first_javadoc_sentence = _extract_first_javadoc_sentence(lines)

    method_names = []
    in_block_comment = False

    for raw in lines:
        line = raw.strip()

        if "/*" in line and "*/" not in line:
            in_block_comment = True
        if "*/" in line:
            in_block_comment = False
            continue
        if in_block_comment or line.startswith("//"):
            continue

        if _RE_PUBLIC_TYPE.match(raw):
            sig.public_type_count += 1

        if _RE_PUBLIC_STATIC_FINAL.match(raw):
            sig.public_static_final_field_count += 1

        # Constructor check (must precede method check to avoid double-count)
        m_ctor = _RE_CONSTRUCTOR.match(raw)
        if m_ctor:
            candidate = m_ctor.group(1)
            # Reject lines that look like method return types
            if not re.search(r"\b(?:void|int|long|double|float|boolean|String|List|Map|Set|Object)\b", raw):
                sig.constructor_count += 1
                continue

        m_method = _RE_METHOD.match(raw)
        if m_method:
            mname = m_method.group(1)
            if mname not in _KEYWORDS:
                sig.public_protected_method_count += 1
                if len(method_names) < 8:
                    method_names.append(mname)

    sig.notable_methods = method_names[:5]
    return sig


# ---------------------------------------------------------------------------
# Labelling logic
# ---------------------------------------------------------------------------
def assign_label(sig: FileSignals) -> tuple:
    """Return (label, description) based on measured signals."""
    if not sig.exists:
        return LABEL_MISSING, "Missing source file — not found on disk"

    if sig.is_package_info:
        if sig.has_javadoc and sig.first_javadoc_sentence:
            return LABEL_PKG_INFO, f"Package/info-only — {sig.first_javadoc_sentence}"
        return LABEL_PKG_INFO, "Package/info-only"

    total_members = (
        sig.public_protected_method_count
        + sig.constructor_count
        + sig.public_static_final_field_count
    )

    if sig.public_type_count >= 2 or sig.public_protected_method_count >= 8:
        label = LABEL_RICH
    elif total_members >= 3 or sig.public_protected_method_count >= 2:
        label = LABEL_MEANINGFUL
    elif total_members >= 1 or sig.public_type_count >= 1:
        label = LABEL_THIN
    elif sig.line_count > 10:
        label = LABEL_UNCERTAIN
    else:
        label = LABEL_THIN

    # Build description
    lead = sig.first_javadoc_sentence or ""

    evidence = []
    if sig.public_type_count:
        evidence.append(f"{sig.public_type_count} public type(s)")
    if sig.public_protected_method_count:
        evidence.append(f"{sig.public_protected_method_count} method(s)")
    if sig.constructor_count:
        evidence.append(f"{sig.constructor_count} constructor(s)")
    if sig.public_static_final_field_count:
        evidence.append(f"{sig.public_static_final_field_count} constant(s)")
    evidence.append(f"{sig.line_count} lines")
    if sig.notable_methods:
        evidence.append("incl. " + ", ".join(sig.notable_methods))

    evidence_str = "; ".join(evidence)

    if lead:
        desc = f"{lead} [{evidence_str}]"
    else:
        desc = f"{label} — {evidence_str}"

    if len(desc) > 280:
        desc = desc[:277] + "..."

    return label, desc


# ---------------------------------------------------------------------------
# Index rebuilding
# ---------------------------------------------------------------------------
def rebuild_index(index_path: Path, entries, labels_map: dict) -> str:
    """
    Re-emit the full index text with "Stub file" labels replaced.
    All other lines pass through verbatim.
    """
    replacement = {}
    for i, e in enumerate(entries):
        if e.is_stub_label and i in labels_map:
            _label, desc = labels_map[i]
            replacement[(e.section, e.filename)] = desc

    re_entry = re.compile(r"^(-\s+`([^`]+\.java)`\s+[—–-]+\s+)(.+)")
    re_section = re.compile(r"^###\s+(.+)")
    current_section = ""
    output_lines = []

    with index_path.open(encoding="utf-8") as fh:
        for raw_line in fh:
            line = raw_line.rstrip("\n")

            m_sec = re_section.match(line)
            if m_sec:
                current_section = m_sec.group(1).strip()

            m_entry = re_entry.match(line)
            if m_entry:
                prefix = m_entry.group(1)
                fname = m_entry.group(2)
                key = (current_section, fname)
                if key in replacement:
                    line = prefix + replacement[key]

            output_lines.append(line)

    return "\n".join(output_lines)


# ---------------------------------------------------------------------------
# Report
# ---------------------------------------------------------------------------
def print_report(entries, labels_map: dict, changed_entries: list) -> None:
    total = len(entries)
    original_stub_count = sum(1 for e in entries if e.is_stub_label)

    new_label_counts: Counter = Counter()
    missing_count = 0
    uncertain_count = 0

    for i, e in enumerate(entries):
        if e.is_stub_label and i in labels_map:
            lbl, _ = labels_map[i]
            new_label_counts[lbl] += 1
            if lbl == LABEL_MISSING:
                missing_count += 1
            if lbl == LABEL_UNCERTAIN:
                uncertain_count += 1

    print("=" * 72)
    print("STUB INDEX AUDIT REPORT")
    print("=" * 72)
    print(f"Total entries scanned          : {total}")
    print(f"Original 'Stub file' count     : {original_stub_count}")
    print()
    print("Proposed label distribution:")
    for lbl in [LABEL_RICH, LABEL_MEANINGFUL, LABEL_THIN,
                LABEL_PKG_INFO, LABEL_MISSING, LABEL_UNCERTAIN]:
        count = new_label_counts.get(lbl, 0)
        if count:
            print(f"  {lbl:<32}: {count}")
    print()
    print(f"Missing source files           : {missing_count}")
    print(f"Uncertain entries              : {uncertain_count}")
    print()
    print("Sample changed entries (up to 10):")
    print("-" * 72)
    for e, old_desc, new_desc in changed_entries[:10]:
        print(f"  {e.section}/{e.filename}")
        print(f"    WAS : {old_desc}")
        print(f"    NOW : {new_desc}")
        print()
    print("=" * 72)


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------
def main() -> None:
    parser = argparse.ArgumentParser(description="Audit docs/STUB_INDEX.md")
    parser.add_argument(
        "--repo-root",
        default=None,
        help="Repo root path (default: parent of tools/ directory)",
    )
    args = parser.parse_args()

    script_dir = Path(__file__).resolve().parent
    repo_root = Path(args.repo_root).resolve() if args.repo_root else script_dir.parent

    index_path = repo_root / "docs" / "STUB_INDEX.md"
    output_path = repo_root / "docs" / "API_INDEX_PROPOSED.md"

    if not index_path.exists():
        print(f"ERROR: Index not found at {index_path}", file=sys.stderr)
        sys.exit(1)

    print(f"Reading index : {index_path}")
    entries = parse_stub_index(index_path)
    resolve_paths(entries, repo_root)
    print(f"Parsed {len(entries)} entries total.")

    stub_pairs = [(i, e) for i, e in enumerate(entries) if e.is_stub_label]
    print(f"Found {len(stub_pairs)} 'Stub file' entries. Analyzing...")

    signals_map: dict = {}
    labels_map: dict = {}
    changed_entries = []

    for i, e in stub_pairs:
        assert e.full_path is not None
        sig = analyze_java_file(e.full_path)
        signals_map[i] = sig
        label, desc = assign_label(sig)
        labels_map[i] = (label, desc)
        display = desc if desc.startswith(label) else f"{label} — {desc}"
        changed_entries.append((e, e.original_desc, display))

    print(f"Analysis complete. Writing: {output_path}")

    proposed_content = rebuild_index(index_path, entries, labels_map)

    header = (
        "<!-- API_INDEX_PROPOSED.md — Generated by tools/audit_api_index.py -->\n"
        "<!-- DO NOT edit by hand. Re-run the script to regenerate. -->\n"
        "<!-- Replaces 'Stub file' labels with evidence-based descriptions. -->\n"
        "<!-- docs/STUB_INDEX.md is unchanged. -->\n\n"
    )
    output_path.write_text(header + proposed_content, encoding="utf-8")

    print_report(entries, labels_map, changed_entries)
    print(f"\nProposed index written to : {output_path}")


if __name__ == "__main__":
    main()
