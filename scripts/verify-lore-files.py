"""
verify-lore-files.py

Scans markdown files in a directory for known wiki scrape artifacts.
Dry-run by default -- pass --apply to fix in-place.

Usage:
    python3 scripts/verify-lore-files.py [directory]           # dry-run
    python3 scripts/verify-lore-files.py --apply [directory]   # fix in-place

Default directory: docs/minecraft-lore

Exit codes:
    0  -- all files clean (or all issues fixed with --apply)
    1  -- issues found (nothing written without --apply)
"""

import re
import sys
from pathlib import Path

# ---------------------------------------------------------------------------
# Pattern registry
# Each entry: (name, match_fn, strip_fn)
#   match_fn(content) -> True if the pattern is present
#   strip_fn(content) -> cleaned content
# ---------------------------------------------------------------------------

def _blob_match(content):
    return bool(re.search(r'^\s*\{[^}]*"chestNames"', content, re.MULTILINE))

def _blob_strip(content):
    # Remove any line that is a wiki loot calculator JSON blob.
    # These lines start with { and contain "chestNames" somewhere on the same line.
    # They are always a single very long line.
    return re.sub(r'^[^\n]*"chestNames"[^\n]*\n?', '', content, flags=re.MULTILINE)


def _share_feedback_match(content):
    return 'Share article feedback' in content

def _share_feedback_strip(content):
    return re.sub(r'Share article feedback\s*\n=+\s*\n?', '', content)


def _discuss_banner_match(content):
    return bool(re.search(r'\[discuss\\', content))

def _discuss_banner_strip(content):
    # Removes \[discuss\ \] blocks and surrounding context
    content = re.sub(r'\s*\\\[discuss\\\\\s*\n\\\]\s*\n?', '\n', content)
    return content


def _maintenance_notice_match(content):
    patterns = [
        r'It has been suggested that this page be split',
        r'This page is currently in the process of being split',
        r'See the talk page for more information about the split',
        r'\*\*Reason:\*\* _MCW:P/',
    ]
    return any(re.search(p, content) for p in patterns)

def _maintenance_notice_strip(content):
    patterns = [
        r'It has been suggested that this page be split.*?MCW:P/\w+_\s*\n',
        r'This page is currently in the process of being split.*?MCW:P/\w+_\s*\n',
        r'See the talk page for more information about the split\.\s*\n',
        r'\*\*Reason:\*\* _MCW:P/\w+_\s*\n',
    ]
    for p in patterns:
        content = re.sub(p, '', content, flags=re.DOTALL)
    return content


def _redirected_notice_match(content):
    return bool(re.search(r'^\(Redirected from', content, re.MULTILINE))

def _redirected_notice_strip(content):
    return re.sub(r'^\(Redirected from[^\n]*\)\s*\n?', '', content, flags=re.MULTILINE)


def _excess_blank_match(content):
    return bool(re.search(r'\n{3,}', content))

def _excess_blank_strip(content):
    return re.sub(r'\n{3,}', '\n\n', content)


PATTERNS = [
    ('wiki-loot-blob',          _blob_match,               _blob_strip),
    ('share-feedback-header',   _share_feedback_match,     _share_feedback_strip),
    ('discuss-banner',          _discuss_banner_match,     _discuss_banner_strip),
    ('maintenance-notice',      _maintenance_notice_match, _maintenance_notice_strip),
    ('redirected-notice',       _redirected_notice_match,  _redirected_notice_strip),
    ('excess-blank-lines',      _excess_blank_match,       _excess_blank_strip),
]

# ---------------------------------------------------------------------------

def scan_file(path: Path, dry_run: bool) -> list[str]:
    """Returns list of pattern names that were found (and fixed unless dry_run)."""
    try:
        content = path.read_text(encoding='utf-8')
    except Exception as e:
        print(f"  ERROR reading {path}: {e}")
        return []

    original = content
    found = []

    for name, match_fn, strip_fn in PATTERNS:
        if match_fn(content):
            found.append(name)
            if not dry_run:
                content = strip_fn(content)

    if found and not dry_run and content != original:
        path.write_text(content, encoding='utf-8')

    return found


def main():
    args = sys.argv[1:]
    apply = '--apply' in args
    dry_run = not apply
    args = [a for a in args if not a.startswith('--')]

    target = Path(args[0]) if args else Path('docs/minecraft-lore')

    if not target.exists():
        print(f"Directory not found: {target}")
        sys.exit(1)

    md_files = sorted(target.rglob('*.md'))
    if not md_files:
        print(f"No .md files found in {target}")
        sys.exit(0)

    mode = 'DRY RUN' if dry_run else 'APPLY'
    print(f"[{mode}] Scanning {len(md_files)} file(s) in {target}\n")

    total_issues = 0
    for path in md_files:
        found = scan_file(path, dry_run)
        if found:
            total_issues += len(found)
            status = 'FOUND' if dry_run else 'FIXED'
            print(f"  {status}  {path.relative_to(target.parent)}")
            for name in found:
                print(f"           - {name}")
        else:
            print(f"  clean    {path.relative_to(target.parent)}")

    suffix = '(run with --apply to fix)' if dry_run and total_issues > 0 else ''
    print(f"\n{'Issues found' if dry_run else 'Issues fixed'}: {total_issues} across {len(md_files)} file(s) {suffix}".rstrip())

    if dry_run and total_issues > 0:
        sys.exit(1)


if __name__ == '__main__':
    main()
