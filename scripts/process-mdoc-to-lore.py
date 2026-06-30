"""
process-mdoc-to-lore.py

Converts MineColonies wiki source .mdoc files (docs/wiki-ref/) into
advisor-artifact lore files (docs/minecolonies-lore/) with [[wikilinks]].

Handles:
  - Template resolution: {% building /%}, {% worker /%}, {% item /%}, etc.
  - Building name map built from all wiki-ref/buildings/*.mdoc frontmatters
  - Strips ## Interface section and all GUI content block templates
  - Strips image links
  - Strips MDX template syntax not resolvable from source
  - Writes YAML frontmatter with advisor-artifact schema
  - Preserves bullet lists (prose refinement is done in a separate pass if needed)

Usage:
    PYTHONUTF8=1 python3 scripts/process-mdoc-to-lore.py

Processes all .mdoc files under docs/wiki-ref/ and writes outputs to
docs/minecolonies-lore/<category>/<topic>.md

Skips files that already exist in docs/minecolonies-lore/ (use --overwrite to override).
"""

import sys
import os
import re
import yaml

WIKI_REF_ROOT = os.path.join(os.path.dirname(__file__), '..', 'docs', 'wiki-ref')
LORE_ROOT = os.path.join(os.path.dirname(__file__), '..', 'docs', 'minecolonies-lore')
WIKI_BASE_URL = 'https://wiki.minecolonies.ldtteam.com/source'

OVERWRITE = '--overwrite' in sys.argv


def load_frontmatter(text):
    if not text.startswith('---'):
        return {}, text
    end = text.find('\n---', 4)
    if end == -1:
        return {}, text
    fm_text = text[4:end]
    body = text[end + 4:].lstrip('\n')
    try:
        fm = yaml.safe_load(fm_text) or {}
    except Exception:
        fm = {}
    return fm, body


def build_building_name_map():
    """Return dict: building_id -> display_name from all buildings/*.mdoc frontmatters."""
    name_map = {}
    buildings_dir = os.path.join(WIKI_REF_ROOT, 'buildings')
    if not os.path.isdir(buildings_dir):
        return name_map
    for fname in os.listdir(buildings_dir):
        if not fname.endswith('.mdoc'):
            continue
        bid = fname[:-5]
        path = os.path.join(buildings_dir, fname)
        with open(path, encoding='utf-8') as f:
            text = f.read()
        fm, _ = load_frontmatter(text)
        name = fm.get('name') or bid.replace('-', ' ').title()
        name_map[bid] = name
    return name_map


def infer_wiki_link(name):
    """Wrap a display name as a wikilink."""
    return f'[[{name}]]'


def resolve_templates(text, fm, building_name_map):
    """Replace {% ... /%} and {% ... %} ... {% /... %} template calls."""

    self_name = fm.get('name', '')
    workers = fm.get('workers', [])
    self_worker = (workers[0].title() if workers else '').strip()

    # {% building /%} -> self building name as wikilink
    text = re.sub(r'\{%\s*building\s*/%\}', infer_wiki_link(self_name) if self_name else 'the building', text)

    # {% building name="X" /%} -> looked up name as wikilink
    def replace_building_named(m):
        bid = m.group(1)
        name = building_name_map.get(bid, bid.replace('-', ' ').title())
        return infer_wiki_link(name)
    text = re.sub(r'\{%\s*building\s+name="([^"]+)"\s*/%\}', replace_building_named, text)

    # {% worker /%} -> self worker title-cased
    text = re.sub(r'\{%\s*worker\s*/%\}', self_worker or 'the worker', text)

    # {% worker name="X" /%} and {% worker name="X" plural=true /%}
    def replace_worker_named(m):
        wname = m.group(1).title()
        plural = bool(re.search(r'plural\s*=\s*true', m.group(2), re.IGNORECASE))
        return (wname + 's') if plural else wname
    text = re.sub(r'\{%\s*worker\s+name="([^"]+)"([^%]*?)/%\}', replace_worker_named, text)

    # {% item name="minecolonies/X" /%} -> human-readable name
    ITEM_NAMES = {
        'ancienttome': 'Ancient Tome',
        'blockpostbox': 'Postbox',
        'postbox': 'Postbox',
        'resourcescroll': 'Resource Scroll',
        'buildtool': 'Build Tool',
    }
    def replace_item(m):
        item_id = m.group(1).split('/')[-1].lower()
        if item_id in ITEM_NAMES:
            return ITEM_NAMES[item_id]
        # Strip common prefixes and split camelCase
        item_id = re.sub(r'^block', '', item_id)
        item_id = re.sub(r'([a-z])([A-Z])', r'\1 \2', item_id)
        return item_id.replace('_', ' ').strip().title() or m.group(1)
    text = re.sub(r'\{%\s*item\s+name="([^"]+)"\s*/%\}', replace_item, text)

    # {% research_link name="path/to/research" /%} -> last path segment, title-case
    def replace_research(m):
        path = m.group(1)
        name = path.split('/')[-1].replace('-', ' ').replace('_', ' ').title()
        return name
    text = re.sub(r'\{%\s*research_link\s+name="([^"]+)"\s*/%\}', replace_research, text)

    # {% food_list /%} -> placeholder
    text = re.sub(r'\{%\s*food_list\s*/%\}', '[crop list varies by biome]', text)

    # Strip all remaining simple self-closing templates: {% anything /%}
    text = re.sub(r'\{%[^%]*?/%\}', '', text)

    # Strip block templates with content: {% X %} ... {% /X %}
    text = re.sub(r'\{%[^%]*?%\}[\s\S]*?\{%\s*/\w[^%]*?%\}', '', text)

    # Strip any leftover {% ... %} or {% /... %} tags
    text = re.sub(r'\{%[^%]*?%\}', '', text)

    return text


def strip_interface_section(text):
    """Remove ## Interface and everything after it (GUI content blocks only)."""
    match = re.search(r'\n## Interface\b', text, re.IGNORECASE)
    if match:
        text = text[:match.start()]
    return text


def clean_body(text):
    # Strip image links: ![alt](url) and relative image paths
    text = re.sub(r'!\[.*?\]\(.*?\)', '', text, flags=re.DOTALL)
    text = re.sub(r'\[.*?\]\(\.\.\/.*?\.(png|jpg|gif|svg|webp)\)', '', text, flags=re.IGNORECASE)

    # De-link: [text](url) -> text
    text = re.sub(r'\[([^\]]+)\]\([^\)]*\)', r'\1', text)

    # Strip raw image references on their own line
    text = re.sub(r'^!\[.*\n?', '', text, flags=re.MULTILINE)

    # Strip config file references
    text = re.sub(r'\[config file\][^\n]*\n?', '', text, flags=re.IGNORECASE)

    # Collapse 3+ blank lines to 2
    text = re.sub(r'\n{3,}', '\n\n', text)

    return text.strip()


def build_frontmatter(fm, category, topic_id, wiki_url):
    topic = fm.get('name') or fm.get('title') or topic_id.replace('-', ' ').title()
    source = wiki_url
    lines = ['---']
    lines.append(f'topic: {topic}')
    lines.append('type: advisor-artifact')
    lines.append(f'source: "[[{source}]]"')
    lines.append('pipeline_stage: advisor-artifact')
    lines.append('version: latest')
    lines.append('---')
    return '\n'.join(lines) + '\n\n'


def process_file(src_path, dst_path, category, topic_id, building_name_map):
    with open(src_path, encoding='utf-8') as f:
        text = f.read()

    fm, body = load_frontmatter(text)

    wiki_url = f'{WIKI_BASE_URL}/{category}/{topic_id}'

    body = strip_interface_section(body)
    body = resolve_templates(body, fm, building_name_map)
    body = clean_body(body)

    if not body.strip():
        print(f'  SKIP (empty after processing): {src_path}')
        return False

    out_frontmatter = build_frontmatter(fm, category, topic_id, wiki_url)
    output = out_frontmatter + body + '\n'

    os.makedirs(os.path.dirname(dst_path), exist_ok=True)
    with open(dst_path, 'w', encoding='utf-8') as f:
        f.write(output)

    print(f'  OK: {dst_path} ({len(output)} chars)')
    return True


def main():
    building_name_map = build_building_name_map()
    print(f'Building name map: {len(building_name_map)} buildings loaded')

    processed = 0
    skipped = 0
    errors = 0

    for category in os.listdir(WIKI_REF_ROOT):
        category_path = os.path.join(WIKI_REF_ROOT, category)
        if not os.path.isdir(category_path):
            continue

        for fname in sorted(os.listdir(category_path)):
            if not fname.endswith('.mdoc'):
                continue

            topic_id = fname[:-5]
            src_path = os.path.join(category_path, fname)
            dst_path = os.path.join(LORE_ROOT, category, topic_id + '.md')

            if os.path.exists(dst_path) and not OVERWRITE:
                print(f'  EXISTS (skip): {dst_path}')
                skipped += 1
                continue

            try:
                ok = process_file(src_path, dst_path, category, topic_id, building_name_map)
                if ok:
                    processed += 1
                else:
                    skipped += 1
            except Exception as e:
                print(f'  ERROR: {src_path}: {e}')
                errors += 1

    print(f'\nDone: {processed} processed, {skipped} skipped, {errors} errors')


if __name__ == '__main__':
    main()
