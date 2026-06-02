#!/usr/bin/env python3
"""Deterministic candidate validation and promotion for project memory."""

from __future__ import annotations

import argparse
import hashlib
import json
import os
import re
import shutil
from datetime import datetime, timezone
from pathlib import Path

VALIDATOR_VERSION = "memory-pipeline-v1"
AUTO_APPROVE_THRESHOLD = 0.85
SEMANTIC_DUPLICATE_THRESHOLD = 0.80
CONTROL_FIELDS = {
    "validator_version", "validator_stage", "validator_hash", "validated_at",
    "approval_route", "user_approved",
}

SOURCE_WIKILINK_RX = re.compile(r'\*\*Source:\*\*\s+\[\[.+\]\]', re.MULTILINE)


def utc_now() -> str:
    return datetime.now(timezone.utc).isoformat()


def project_dir() -> Path:
    return Path(os.environ.get("MEMORY_PIPELINE_ROOT")
                or os.environ.get("CLAUDE_PROJECT_DIR")
                or Path(__file__).parent.parent.parent).resolve()


def has_yaml_frontmatter(text: str) -> bool:
    return bool(re.match(r"^---\r?\n", text))


def has_source_wikilink(text: str) -> bool:
    return bool(SOURCE_WIKILINK_RX.search(text))


def parse_frontmatter(text: str) -> tuple[dict[str, str], str]:
    match = re.match(r"^---\r?\n(.*?)\r?\n---\r?\n?", text, re.DOTALL)
    if not match:
        return {}, text.strip()
    fields: dict[str, str] = {}
    for line in match.group(1).splitlines():
        if ":" in line:
            key, value = line.split(":", 1)
            fields[key.strip().lower()] = value.strip()
    return fields, text[match.end():].strip()


def render_candidate(fields: dict[str, str], body: str) -> str:
    ordered = [
        "title", "domain", "fact", "confidence", "usefulness", "suspicious",
        "supersedes", "authority", "validator_version", "validator_stage",
        "validator_hash", "validated_at", "approval_route", "user_approved",
    ]
    lines = []
    for key in ordered:
        if fields.get(key):
            lines.append(f"{key}: {fields[key]}")
    for key in sorted(set(fields) - set(ordered)):
        if fields.get(key):
            lines.append(f"{key}: {fields[key]}")
    return "---\n" + "\n".join(lines) + "\n---\n\n" + body.strip() + "\n"


def canonical_payload(fields: dict[str, str], body: str) -> str:
    stable = {k: v for k, v in fields.items() if k not in CONTROL_FIELDS}
    return json.dumps(stable, sort_keys=True, separators=(",", ":")) + "\n" + body.strip()


def candidate_hash(fields: dict[str, str], body: str) -> str:
    return hashlib.sha256(canonical_payload(fields, body).encode("utf-8")).hexdigest()


def normalize_fact(fields: dict[str, str], body: str) -> str:
    fact = fields.get("fact") or body
    return re.sub(r"\s+", " ", fact).strip().lower()


def fact_tokens(fact: str) -> set[str]:
    return set(re.findall(r"[a-z0-9]+", fact.lower()))


def similarity(left: str, right: str) -> float:
    a, b = fact_tokens(left), fact_tokens(right)
    if not a or not b:
        return 0.0
    return len(a & b) / len(a | b)


def truthy(value: str) -> bool:
    return str(value).strip().lower() in {"1", "true", "yes", "approved"}


def confidence(fields: dict[str, str]) -> float:
    try:
        return float(fields.get("confidence", "0"))
    except ValueError:
        return 0.0


def domain_root(root: Path, candidate: Path) -> Path | None:
    mem = root / ".memsearch" / "memory"
    parts = candidate.resolve().parts
    if "framework" in parts:
        return mem / "framework"
    if "neoforge" in parts:
        return mem / "domains" / "neoforge"
    if "minecolonies" in parts:
        return mem / "domains" / "minecolonies"
    if "dragontweaksv2" in parts:
        return mem / "projects" / "dragontweaksv2"
    return None


def candidate_dirs(root: Path, stage: str) -> list[Path]:
    mem = root / ".memsearch" / "memory"
    dirs = [
        mem / "framework" / "candidates" / stage,
        mem / "domains" / "neoforge" / "candidates" / stage,
        mem / "domains" / "minecolonies" / "candidates" / stage,
        mem / "projects" / "dragontweaksv2" / "candidates" / stage,
    ]
    return dirs


def ensure_dirs(root: Path) -> None:
    for stage in ("extracted", "review", "tentative-approved", "failed-validation", "rejected"):
        for folder in candidate_dirs(root, stage):
            folder.mkdir(parents=True, exist_ok=True)
    (root / ".memsearch" / "pipeline-state").mkdir(parents=True, exist_ok=True)


def manifest_path(root: Path) -> Path:
    return root / ".memsearch" / "pipeline-state" / "first-validation.json"


def load_manifest(root: Path) -> dict:
    path = manifest_path(root)
    if not path.exists():
        return {"validator_version": VALIDATOR_VERSION, "entries": {}}
    try:
        data = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError):
        return {"validator_version": VALIDATOR_VERSION, "entries": {}}
    if not isinstance(data.get("entries"), dict):
        data["entries"] = {}
    return data


def save_manifest(root: Path, manifest: dict) -> None:
    manifest["validator_version"] = VALIDATOR_VERSION
    path = manifest_path(root)
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(manifest, indent=2, sort_keys=True) + "\n", encoding="utf-8")


def manifest_key(root: Path, path: Path) -> str:
    return path.resolve().relative_to(root.resolve()).as_posix()


def move_unique(source: Path, destination_dir: Path) -> Path:
    destination_dir.mkdir(parents=True, exist_ok=True)
    destination = destination_dir / source.name
    counter = 1
    while destination.exists():
        destination = destination_dir / f"{source.stem}-{counter}{source.suffix}"
        counter += 1
    shutil.move(str(source), str(destination))
    return destination


def approved_records(domain: Path) -> list[tuple[Path, dict[str, str], str, str]]:
    records = []
    approved = domain / "approved"
    if not approved.exists():
        return records
    for path in approved.glob("*.md"):
        fields, body = parse_frontmatter(path.read_text(encoding="utf-8", errors="ignore"))
        records.append((path, fields, body, normalize_fact(fields, body)))
    return records


def relationship(domain: Path, fields: dict[str, str], body: str) -> tuple[str, Path | None]:
    fact = normalize_fact(fields, body)
    supersedes = fields.get("supersedes", "").strip()
    for path, _, _, approved_fact in approved_records(domain):
        if supersedes and path.name.lower() == supersedes.lower():
            return "supersedes", path
        if fact and fact == approved_fact:
            return "duplicate", path
        if similarity(fact, approved_fact) >= SEMANTIC_DUPLICATE_THRESHOLD:
            return "semantic-duplicate", path
    return "novel", None


def mark_validated(fields: dict[str, str], body: str, route: str) -> dict[str, str]:
    updated = dict(fields)
    updated["validator_version"] = VALIDATOR_VERSION
    updated["validator_stage"] = "first"
    updated["validator_hash"] = candidate_hash(updated, body)
    updated["validated_at"] = utc_now()
    updated["approval_route"] = route
    return updated


def register_manifest(root: Path, manifest: dict, path: Path,
                      fields: dict[str, str], body: str) -> None:
    manifest["entries"][manifest_key(root, path)] = {
        "hash": candidate_hash(fields, body),
        "validator_version": VALIDATOR_VERSION,
        "approval_route": fields.get("approval_route", ""),
        "validated_at": fields.get("validated_at", ""),
    }


def first_validate(root: Path) -> dict[str, list[str]]:
    ensure_dirs(root)
    manifest = load_manifest(root)
    result = {"tentative-approved": [], "review": [], "rejected": []}
    for extracted in candidate_dirs(root, "extracted"):
        domain = domain_root(root, extracted)
        if domain is None:
            continue
        for source in sorted(extracted.glob("*.md")):
            raw_text = source.read_text(encoding="utf-8", errors="ignore")
            fields, body = parse_frontmatter(raw_text)

            if has_yaml_frontmatter(raw_text):
                # Java-source rules: full frontmatter validation applies
                useless = (
                    not fields.get("title")
                    or not normalize_fact(fields, body)
                    or fields.get("usefulness", "").lower() == "trash"
                )
                suspicious = truthy(fields.get("suspicious", ""))
                cand_confidence = confidence(fields)
            else:
                # Non-YAML: only a **Source:** [[wikilink]] is required for format validity.
                # Always route to review; never assign auto-approve confidence.
                useless = not has_source_wikilink(raw_text)
                suspicious = False
                cand_confidence = 0.0

            rel, _ = relationship(domain, fields, body)
            if useless or rel in {"duplicate", "semantic-duplicate"}:
                dest = move_unique(source, domain / "candidates" / "rejected")
                result["rejected"].append(str(dest))
                continue
            if suspicious or cand_confidence < AUTO_APPROVE_THRESHOLD:
                dest = move_unique(source, domain / "candidates" / "review")
                result["review"].append(str(dest))
                continue
            updated = mark_validated(fields, body, "auto")
            source.write_text(render_candidate(updated, body), encoding="utf-8")
            dest = move_unique(source, domain / "candidates" / "tentative-approved")
            register_manifest(root, manifest, dest, updated, body)
            result["tentative-approved"].append(str(dest))
    save_manifest(root, manifest)
    return result


def approve_review(root: Path, paths: list[str]) -> dict[str, list[str]]:
    ensure_dirs(root)
    manifest = load_manifest(root)
    result = {"tentative-approved": []}
    for raw in paths:
        source = Path(raw).resolve()
        domain = domain_root(root, source)
        if domain is None or source.parent != (domain / "candidates" / "review").resolve():
            raise ValueError(f"review candidate is outside a recognized review folder: {source}")
        fields, body = parse_frontmatter(source.read_text(encoding="utf-8", errors="ignore"))
        updated = mark_validated(fields, body, "user-review")
        updated["user_approved"] = "true"
        source.write_text(render_candidate(updated, body), encoding="utf-8")
        dest = move_unique(source, domain / "candidates" / "tentative-approved")
        register_manifest(root, manifest, dest, updated, body)
        result["tentative-approved"].append(str(dest))
    save_manifest(root, manifest)
    return result


def deprecate_existing(domain: Path, existing: Path, replacement: Path) -> Path:
    deprecated = domain / "deprecated"
    deprecated.mkdir(parents=True, exist_ok=True)
    destination = deprecated / existing.name
    counter = 1
    while destination.exists():
        destination = deprecated / f"{existing.stem}-{counter}{existing.suffix}"
        counter += 1
    fields, body = parse_frontmatter(existing.read_text(encoding="utf-8", errors="ignore"))
    fields["status"] = "deprecated"
    fields["superseded_by"] = replacement.name
    existing.write_text(render_candidate(fields, body), encoding="utf-8")
    shutil.move(str(existing), str(destination))
    return destination


def final_validate_and_promote(root: Path) -> dict[str, list[str]]:
    ensure_dirs(root)
    manifest = load_manifest(root)
    result = {"promoted": [], "rejected": [], "failed-validation": [], "deprecated": []}
    for tentative in candidate_dirs(root, "tentative-approved"):
        domain = domain_root(root, tentative)
        if domain is None:
            continue
        for source in sorted(tentative.glob("*.md")):
            raw_text = source.read_text(encoding="utf-8", errors="ignore")
            fields, body = parse_frontmatter(raw_text)
            key = manifest_key(root, source)
            provenance = manifest["entries"].get(key)

            if has_yaml_frontmatter(raw_text):
                valid_marker = (
                    provenance
                    and fields.get("validator_version") == VALIDATOR_VERSION
                    and fields.get("validator_stage") == "first"
                    and fields.get("validator_hash") == candidate_hash(fields, body)
                    and provenance.get("hash") == candidate_hash(fields, body)
                    and provenance.get("validator_version") == VALIDATOR_VERSION
                )
                if not valid_marker:
                    dest = move_unique(source, domain / "candidates" / "failed-validation")
                    result["failed-validation"].append(str(dest))
                    manifest["entries"].pop(key, None)
                    continue
                if confidence(fields) < AUTO_APPROVE_THRESHOLD and not truthy(fields.get("user_approved", "")):
                    dest = move_unique(source, domain / "candidates" / "failed-validation")
                    result["failed-validation"].append(str(dest))
                    manifest["entries"].pop(key, None)
                    continue
            else:
                # Non-YAML: verify manifest entry, source wikilink, and manifest-recorded review approval.
                # Manifest presence alone is not approval; approval_route must be user-review.
                if not provenance or not has_source_wikilink(raw_text):
                    dest = move_unique(source, domain / "candidates" / "failed-validation")
                    result["failed-validation"].append(str(dest))
                    manifest["entries"].pop(key, None)
                    continue
                if provenance.get("approval_route") != "user-review":
                    dest = move_unique(source, domain / "candidates" / "failed-validation")
                    result["failed-validation"].append(str(dest))
                    manifest["entries"].pop(key, None)
                    continue

            rel, existing = relationship(domain, fields, body)
            if rel in {"duplicate", "semantic-duplicate"}:
                dest = move_unique(source, domain / "candidates" / "rejected")
                result["rejected"].append(str(dest))
                manifest["entries"].pop(key, None)
                continue
            approved = domain / "approved"
            approved.mkdir(parents=True, exist_ok=True)
            destination = approved / source.name
            if rel == "supersedes":
                if not existing or fields.get("authority", "").lower() != "authoritative":
                    dest = move_unique(source, domain / "candidates" / "failed-validation")
                    result["failed-validation"].append(str(dest))
                    manifest["entries"].pop(key, None)
                    continue
                result["deprecated"].append(str(deprecate_existing(domain, existing, source)))
            elif destination.exists():
                dest = move_unique(source, domain / "candidates" / "failed-validation")
                result["failed-validation"].append(str(dest))
                manifest["entries"].pop(key, None)
                continue
            shutil.move(str(source), str(destination))
            result["promoted"].append(str(destination))
            manifest["entries"].pop(key, None)
    if result["promoted"]:
        queue = root / ".memsearch" / "candidates" / "pending-reindex.txt"
        queue.parent.mkdir(parents=True, exist_ok=True)
        with queue.open("a", encoding="utf-8") as handle:
            for folder in sorted({str(Path(path).parent) for path in result["promoted"]}):
                handle.write(folder + "\n")
    save_manifest(root, manifest)
    return result


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("command", choices=("first-validate", "approve-review", "final-promote"))
    parser.add_argument("paths", nargs="*")
    args = parser.parse_args()
    root = project_dir()
    if args.command == "first-validate":
        result = first_validate(root)
    elif args.command == "approve-review":
        result = approve_review(root, args.paths)
    else:
        result = final_validate_and_promote(root)
    print(json.dumps(result, indent=2, sort_keys=True))


if __name__ == "__main__":
    main()
