"""
Sync docs/minecolonies-lore/ into obsidian-docs/minecolonies/lore/ so that
every [[wikilink]] in the Obsidian category pages resolves to a real page
with content.

Source of truth: docs/minecolonies-lore/  (read by LoreIndex at runtime)
Obsidian mirror:  obsidian-docs/minecolonies/lore/  (navigable in graph view)

Run after any lore file change to keep the vault in sync.
"""
import os
import re

PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
LORE_ROOT = os.path.join(PROJECT_ROOT, "docs", "minecolonies-lore")
OBS_LORE_ROOT = os.path.join(PROJECT_ROOT, "obsidian-docs", "minecolonies", "lore")

CATEGORY_BACK_LINKS = {
    "buildings": "[[Lore-Buildings]]",
    "systems":   "[[Lore-Systems]]",
    "needs":     "[[Lore-Needs]]",
    "items":     "[[Lore-Items]]",
}

FRONTMATTER_RE = re.compile(r"^---\s*\n.*?\n---\s*\n", re.DOTALL)
TOPIC_RE = re.compile(r"^topic:\s*(.+)$", re.MULTILINE)


def strip_frontmatter(text):
    m = FRONTMATTER_RE.match(text)
    return text[m.end():].lstrip("\n") if m else text


def get_topic(text):
    m = TOPIC_RE.search(text)
    return m.group(1).strip().strip('"') if m else None


def sync():
    os.makedirs(OBS_LORE_ROOT, exist_ok=True)
    written = []

    for category in ("buildings", "systems", "needs", "items"):
        cat_path = os.path.join(LORE_ROOT, category)
        if not os.path.exists(cat_path):
            continue
        back_link = CATEGORY_BACK_LINKS[category]

        for fname in sorted(os.listdir(cat_path)):
            if not fname.endswith(".md"):
                continue
            src = os.path.join(cat_path, fname)
            with open(src, encoding="utf-8") as f:
                raw = f.read()

            topic = get_topic(raw)
            if not topic:
                print(f"WARN: no topic in {src}, skipping")
                continue

            body = strip_frontmatter(raw).rstrip("\n")
            obs_name = f"{topic}.md"
            obs_path = os.path.join(OBS_LORE_ROOT, obs_name)

            content = f"{body}\n\n← {back_link}\n"
            with open(obs_path, "w", encoding="utf-8", newline="\n") as f:
                f.write(content)
            written.append(obs_name)

    print(f"Synced {len(written)} pages to obsidian-docs/minecolonies/lore/")


if __name__ == "__main__":
    sync()
