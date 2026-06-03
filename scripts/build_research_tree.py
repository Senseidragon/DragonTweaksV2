#!/usr/bin/env python3
"""
build_research_tree.py

Processes all MineColonies research branches under a researches/ folder and produces:
  1. research_<branch>.md   per branch — human/LLM-readable knowledgebase document
  2. research_all_flat.json — combined runtime lookup keyed by node ID

Usage:
    python build_research_tree.py <path_to_researches_folder>

Example:
    python build_research_tree.py src/datagen/generated/minecolonies/data/minecolonies/researches
"""

import argparse
import json
import sys
from pathlib import Path
from collections import defaultdict


def load_nodes(branch_folder: Path, branch_name: str) -> dict:
    nodes = {}
    for f in sorted(branch_folder.glob("*.json")):
        with open(f) as fh:
            data = json.load(fh)
        node_id = f"minecolonies:{branch_name}/{f.stem}"
        data["_id"] = node_id
        data["_filename"] = f.stem
        nodes[node_id] = data
    return nodes


def build_tree(nodes: dict):
    children = defaultdict(list)
    roots = []
    for node_id, node in nodes.items():
        parent = node.get("parentResearch")
        if parent:
            children[parent].append(node_id)
        else:
            roots.append(node_id)
    roots.sort(key=lambda nid: nodes[nid].get("sortOrder", 999))
    return roots, children


def friendly_name(raw: str) -> str:
    stem = raw.split("/")[-1].split(":")[-1]
    return stem.replace("_", " ").replace("-", " ").title()


def format_cost(cost: dict) -> str:
    qty = cost.get("quantity") or cost.get("count", "?")
    cost_type = cost.get("type", "")
    if cost_type == "minecolonies:item_tag" or (not cost_type and cost.get("tag")):
        tag = cost.get("tag", "?").split(":")[-1]
        return f"{qty}x {tag} (any)"
    item = cost.get("item")
    if item:
        return f"{qty}x {item.split(':')[-1]}"
    return str(cost)


def format_requirement(req: dict) -> str:
    if req.get("type") in ("minecolonies:building", "minecolonies:single-building"):
        building = req.get("building", "?").split(":")[-1]
        level = req.get("level", "?")
        return f"{friendly_name(building)} level {level}"
    elif req.get("type") == "minecolonies:alternate-building":
        buildings = req.get("alternate-buildings", [])
        level = req.get("level", "?")
        names = " or ".join(friendly_name(b) for b in buildings)
        return f"{names} level {level}"
    elif req.get("type") == "minecolonies:research":
        return f"Research: {friendly_name(req.get('research', '?'))}"
    return str(req)


def format_effect(effect: dict) -> str:
    eid = effect.get("id", "?")
    level = effect.get("level", 1)
    name = eid.split("/")[-1]
    name = name.replace("blockhut", "").replace("_", " ").title().strip() or eid
    suffix = f" level {level}" if level > 1 else ""
    return f"Unlocks {name}{suffix}"


def render_node_md(node: dict, depth: int) -> list:
    indent = "  " * depth
    name = friendly_name(node["_filename"])
    res_level = node.get("researchLevel", "?")

    lines = []
    if depth == 0:
        lines.append(f"### {name}  *(Research Level {res_level})*\n\n")
    else:
        lines.append(f"{indent}- **{name}** *(Research Level {res_level})*\n")

    costs = node.get("costs", [])
    if costs:
        cost_str = ", ".join(format_cost(c) for c in costs)
        lines.append(f"{indent}  - Cost: {cost_str}\n")

    reqs = node.get("requirements", [])
    if reqs:
        req_str = ", ".join(format_requirement(r) for r in reqs)
        lines.append(f"{indent}  - Requires: {req_str}\n")

    effects = node.get("effects", [])
    if effects:
        eff_str = ", ".join(format_effect(e) for e in effects)
        lines.append(f"{indent}  - Effect: {eff_str}\n")

    return lines


def write_branch_markdown(nodes: dict, roots: list, children: dict, branch_name: str, out_path: Path):
    lines = [
        f"# MineColonies Research Tree — {branch_name.title()} Branch\n\n",
        f"Research nodes available in the University for the **{branch_name}** branch. "
        "Each node lists its cost, building prerequisites, and what it unlocks. "
        "Child nodes are indented under their parent.\n\n",
        "---\n\n",
    ]

    def walk(node_id, depth):
        node = nodes[node_id]
        lines.extend(render_node_md(node, depth))
        for child_id in sorted(children.get(node_id, []),
                               key=lambda nid: nodes[nid].get("sortOrder", 999)):
            walk(child_id, depth + 1)
        if depth == 0:
            lines.append("\n")

    for root_id in roots:
        walk(root_id, 0)

    out_path.write_text("".join(lines), encoding="utf-8")
    print(f"  Wrote: {out_path}")


def flatten_nodes(nodes: dict, children: dict) -> dict:
    output = {}
    for node_id, node in nodes.items():
        output[node_id] = {
            "id": node_id,
            "name": friendly_name(node["_filename"]),
            "branch": node.get("branch", ""),
            "researchLevel": node.get("researchLevel"),
            "sortOrder": node.get("sortOrder"),
            "parentResearch": node.get("parentResearch"),
            "children": children.get(node_id, []),
            "costs": node.get("costs", []),
            "requirements": node.get("requirements", []),
            "effects": node.get("effects", []),
        }
    return output


def main():
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("researches_dir", help="Path to the researches/ folder")
    parser.add_argument("--exclude", metavar="BRANCH", nargs="+", default=[],
                        help="Branch folder names to skip (e.g. --exclude effects unlockable)")
    args = parser.parse_args()

    researches_dir = Path(args.researches_dir)
    if not researches_dir.is_dir():
        print(f"ERROR: '{researches_dir}' is not a directory.")
        sys.exit(1)

    excluded = set(args.exclude)
    branch_folders = sorted(p for p in researches_dir.iterdir()
                            if p.is_dir() and p.name not in excluded)
    if not branch_folders:
        print(f"ERROR: No branch subfolders found under '{researches_dir}'.")
        sys.exit(1)

    if excluded:
        print(f"Excluding: {sorted(excluded)}")
    print(f"Found {len(branch_folders)} branch(es): {[b.name for b in branch_folders]}\n")

    out_dir = Path(".")
    all_flat = {}

    for branch_folder in branch_folders:
        branch_name = branch_folder.name
        print(f"Processing branch: {branch_name}")

        nodes = load_nodes(branch_folder, branch_name)
        print(f"  Found {len(nodes)} research nodes")

        roots, children = build_tree(nodes)
        print(f"  Root nodes: {len(roots)}")

        write_branch_markdown(nodes, roots, children, branch_name,
                              out_dir / f"research_{branch_name}.md")

        all_flat.update(flatten_nodes(nodes, children))
        print()

    output = {
        "_meta": {
            "globalRequirements": [
                {
                    "type": "minecolonies:building",
                    "building": "minecolonies:university",
                    "level": 1,
                    "note": "Required to access the research UI — not encoded in individual node requirements"
                }
            ]
        }
    }
    output.update(all_flat)

    flat_out = out_dir / "research_all_flat.json"
    flat_out.write_text(json.dumps(output, indent=2), encoding="utf-8")
    print(f"Wrote combined flat JSON: {flat_out}  ({len(all_flat)} total nodes)")
    print("\nDone.")


if __name__ == "__main__":
    main()
