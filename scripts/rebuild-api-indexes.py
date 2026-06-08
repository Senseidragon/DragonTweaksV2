#!/usr/bin/env python3
"""
Deterministic API index rebuild script.
Generates *_INDEX.md files for docs/api/ domain directories.

Usage:
    python scripts/rebuild-api-indexes.py --check
    python scripts/rebuild-api-indexes.py --apply
"""

import argparse
import os
import re
import shutil
import sys
import tempfile
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent
DOCS_API = REPO_ROOT / "docs" / "api"
SCRIPT_REL = "scripts/rebuild-api-indexes.py"
REQUIRED_DOMAINS = ["minecolonies", "neoforge"]


def rel(path: Path) -> str:
    return path.relative_to(REPO_ROOT).as_posix()


def index_name(dirname: str) -> str:
    return f"{dirname.upper()}_INDEX.md"


def dir_needs_index(dirpath: Path) -> bool:
    """True if dir has at least one immediate .java file or immediate subdirectory."""
    for entry in dirpath.iterdir():
        if entry.is_file() and entry.suffix == ".java":
            return True
        if entry.is_dir():
            return True
    return False


def generate_content(dirpath: Path) -> str:
    source = rel(dirpath)
    lines = [
        "<!-- GENERATED FILE — do not edit by hand.",
        f"     Generator: {SCRIPT_REL}",
        f"     Source:    {source}",
        "-->",
        "",
        f"# {dirpath.name.upper()} API Index",
        "",
        f"**Source directory:** `{source}`",
        "",
    ]

    java_files = sorted(
        e.name for e in dirpath.iterdir()
        if e.is_file() and e.suffix == ".java"
    )
    subdirs = sorted(
        (e for e in dirpath.iterdir() if e.is_dir()),
        key=lambda e: e.name,
    )

    if java_files:
        lines += ["## Files", ""]
        for f in java_files:
            lines.append(f"- `{f}`")
        lines.append("")

    if subdirs:
        lines += ["## Subdirectories", ""]
        for sd in subdirs:
            if dir_needs_index(sd):
                child_idx = index_name(sd.name)
                lines.append(f"- [{sd.name}/]({sd.name}/{child_idx})")
            else:
                lines.append(f"- `{sd.name}/` _(empty — no index)_")
        lines.append("")

    return "\n".join(lines) + "\n"


def collect_desired(docs_api: Path) -> dict:
    """Return {index_path: content} for all indexes that should exist."""
    desired = {}

    domains = sorted(d for d in docs_api.iterdir() if d.is_dir())

    for domain in domains:
        # Domain root always gets an index
        desired[domain / index_name(domain.name)] = generate_content(domain)

        # Walk all subdirs within the domain
        for dirpath_str, dirnames, _filenames in os.walk(str(domain)):
            dirnames.sort()  # deterministic traversal order
            dirpath = Path(dirpath_str)
            if dirpath == domain:
                continue  # already handled above
            if dir_needs_index(dirpath):
                desired[dirpath / index_name(dirpath.name)] = generate_content(dirpath)

    return desired


def collect_existing_indexes(docs_api: Path) -> list:
    """Return all existing *_INDEX.md files under docs/api/."""
    existing = []
    for dirpath_str, _dirnames, filenames in os.walk(str(docs_api)):
        for f in filenames:
            if f.endswith("_INDEX.md"):
                existing.append(Path(dirpath_str) / f)
    return existing


LINK_PATTERN = re.compile(r'\[.*?\]\(([^)]+)\)')


def verify(desired: dict, docs_api: Path) -> list:
    """Run all verification checks. Returns list of error messages."""
    errors = []

    # Check required domain root indexes
    for domain_name in REQUIRED_DOMAINS:
        domain_dir = docs_api / domain_name
        if domain_dir.exists():
            expected = domain_dir / index_name(domain_name)
            if expected not in desired:
                errors.append(f"MISSING required domain root index: {rel(expected)}")

    # Check every domain root has an index
    for domain in sorted(d for d in docs_api.iterdir() if d.is_dir()):
        idx = domain / index_name(domain.name)
        if idx not in desired:
            errors.append(f"MISSING domain root index: {rel(idx)}")

        # Check every dir that needs_index has one
        for dirpath_str, dirnames, _filenames in os.walk(str(domain)):
            dirnames.sort()
            dirpath = Path(dirpath_str)
            if dirpath == domain:
                continue
            if dir_needs_index(dirpath):
                idx2 = dirpath / index_name(dirpath.name)
                if idx2 not in desired:
                    errors.append(f"MISSING index for directory: {rel(dirpath)}")

    # Check links in generated content
    for idx_path, content in desired.items():
        idx_dir = idx_path.parent
        for match in LINK_PATTERN.finditer(content):
            link = match.group(1)
            target = (idx_dir / link).resolve()
            # Must be under docs/api/
            try:
                target.relative_to(docs_api)
            except ValueError:
                errors.append(f"LINK escapes docs/api/ in {rel(idx_path)}: {link!r}")
                continue
            # Target must be in desired (will be created) or already exists on disk
            if target not in desired and not target.exists():
                errors.append(
                    f"BROKEN link in {rel(idx_path)}: {link!r} -> {rel(target)}"
                )

    # Check for duplicate index names within the same directory
    by_dir: dict = {}
    for idx_path in desired:
        parent = idx_path.parent
        by_dir.setdefault(parent, []).append(idx_path.name)
    for parent, names in by_dir.items():
        if len(names) != len(set(names)):
            errors.append(f"DUPLICATE index names in {rel(parent)}: {names}")

    return errors


def run_check(docs_api: Path) -> int:
    print(f"Scanning: {rel(docs_api)}\n")
    desired = collect_desired(docs_api)
    existing = collect_existing_indexes(docs_api)

    desired_set = set(desired.keys())
    existing_set = set(existing)

    to_create = sorted(desired_set - existing_set, key=rel)
    to_delete = sorted(existing_set - desired_set, key=rel)
    unchanged = []
    to_update = []
    for p in sorted(desired_set & existing_set, key=rel):
        current = p.read_text(encoding="utf-8")
        if current.rstrip("\n") == desired[p].rstrip("\n"):
            unchanged.append(p)
        else:
            to_update.append(p)

    print("=" * 60)
    print("PLANNED CHANGES")
    print("=" * 60)
    print(f"  To create : {len(to_create)}")
    print(f"  To update : {len(to_update)}")
    print(f"  Unchanged : {len(unchanged)}")
    print(f"  To delete : {len(to_delete)}")

    if to_create:
        print(f"\nCREATE ({len(to_create)}):")
        for p in to_create:
            print(f"  + {rel(p)}")

    if to_update:
        print(f"\nUPDATE ({len(to_update)}):")
        for p in to_update:
            print(f"  ~ {rel(p)}")

    if to_delete:
        print(f"\nDELETE ({len(to_delete)}):")
        for p in to_delete:
            print(f"  - {rel(p)}")

    errors = verify(desired, docs_api)
    print()
    if errors:
        print(f"VERIFICATION ERRORS ({len(errors)}):")
        for e in errors:
            print(f"  ERROR: {e}")
        return 1

    print(f"Verification: PASS ({len(desired)} indexes in desired set)")
    return 0


def run_apply(docs_api: Path) -> int:
    print(f"Applying to: {rel(docs_api)}\n")
    desired = collect_desired(docs_api)
    existing = collect_existing_indexes(docs_api)
    existing_set = set(existing)

    # Step 1: Compute full desired index set (done above).

    # Step 2: Write new content to temporary files.
    tmp_dir = Path(tempfile.mkdtemp(prefix="api-index-rebuild-"))
    try:
        temp_map = {}
        for i, (idx_path, content) in enumerate(desired.items()):
            tmp_file = tmp_dir / f"{idx_path.name}.{i}.tmp"
            tmp_file.write_text(content, encoding="utf-8")
            temp_map[idx_path] = tmp_file

        # Step 3: Verify desired set before touching any real file.
        errors = verify(desired, docs_api)
        if errors:
            print("Verification failed — aborting, no files modified.")
            for e in errors:
                print(f"  ERROR: {e}")
            return 1

        # Step 4: Remove stale *_INDEX.md files.
        stale = sorted(existing_set - set(desired.keys()), key=rel)
        for p in stale:
            p.unlink()
            print(f"  deleted : {rel(p)}")

        # Step 5: Move temp files into place.
        for idx_path, tmp_file in sorted(temp_map.items(), key=lambda kv: rel(kv[0])):
            idx_path.parent.mkdir(parents=True, exist_ok=True)
            action = "updated" if idx_path in existing_set else "created"
            shutil.move(str(tmp_file), str(idx_path))
            print(f"  {action} : {rel(idx_path)}")

        # Post-apply verification.
        post_existing = set(collect_existing_indexes(docs_api))
        post_errors = []
        for p in sorted(desired, key=rel):
            if p not in post_existing:
                post_errors.append(f"MISSING after apply: {rel(p)}")
        for p in sorted(post_existing - set(desired.keys()), key=rel):
            post_errors.append(f"STALE after apply: {rel(p)}")

        if post_errors:
            print("\nPost-apply verification FAILED:")
            for e in post_errors:
                print(f"  ERROR: {e}")
            return 1

        print(f"\nApply complete. {len(desired)} indexes in place.")
        return 0

    finally:
        shutil.rmtree(str(tmp_dir), ignore_errors=True)


def main() -> int:
    if not DOCS_API.exists():
        print(f"ERROR: {rel(DOCS_API)} does not exist", file=sys.stderr)
        return 2

    parser = argparse.ArgumentParser(
        description="Rebuild docs/api/ index files deterministically."
    )
    group = parser.add_mutually_exclusive_group(required=True)
    group.add_argument(
        "--check", action="store_true",
        help="Report planned changes without modifying any files."
    )
    group.add_argument(
        "--apply", action="store_true",
        help="Atomically rebuild all index files."
    )
    args = parser.parse_args()

    if args.check:
        return run_check(DOCS_API)
    return run_apply(DOCS_API)


if __name__ == "__main__":
    sys.exit(main())