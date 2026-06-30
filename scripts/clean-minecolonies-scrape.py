"""
clean-minecolonies-scrape.py

Cleans a raw Firecrawl scrape of a MineColonies wiki page into an
intermediate clean markdown file, ready for synthesis into a
docs/minecolonies-lore/ advisor-artifact prose file.

Input:  a file containing a JSON envelope {"markdown": "...", "metadata": {...}}
        as returned by Firecrawl and written verbatim to docs/minecolonies-pipeline/

Output: a clean markdown file with:
        - YAML frontmatter reconstructed from Firecrawl metadata
        - All image links removed
        - All markdown links de-linked: [text](url) -> text
        - Embedded JSON blobs stripped
        - Wiki navigation chrome removed (nav menu, breadcrumbs, footer)
        - Template syntax artifacts removed ({% ... %} if any survive rendering)
        - [hide]/[show] toggle markers removed
        - Maintenance notices removed
        - Changelog / Version History sections stripped
        - Citation footnotes stripped
        - Excessive blank lines collapsed

Usage:
    PYTHONUTF8=1 python3 scripts/clean-minecolonies-scrape.py <raw.md> <clean.md>

After running this script, read the clean file and synthesize it into a
docs/minecolonies-lore/<category>/<topic>.md advisor-artifact using the
minecolonies-lore prose conventions:
    - Plain flowing prose, no section headers, no bullet lists
    - No game-mechanic framing ("tier", "saturation bar", "satisfaction penalty")
    - [[wikilinks]] to related buildings, workers, and systems
    - Write from the inside of the colony world, not as a game manual

Always prefix with PYTHONUTF8=1 -- wiki content may contain non-ASCII characters.
"""

import sys
import json
import re


NOISE_SECTIONS = {
    'Changelog', 'Version History', 'History', 'Gallery', 'Trivia',
    'References', 'External links', 'Navigation', 'Issues', 'Notes',
    'Permissions', 'Config', 'Recipe', 'Recipes',
}


def extract_frontmatter(metadata: dict, topic: str) -> str:
    lines = ["---"]
    lines.append(f'topic: {topic}')
    lines.append('type: advisor-artifact')
    if metadata.get("sourceURL"):
        lines.append(f'source: "[[{metadata["sourceURL"]}]]"')
    else:
        lines.append('source: unknown')
    lines.append('scraped: unknown')
    lines.append('pipeline_stage: advisor-artifact-raw')
    lines.append("---")
    return "\n".join(lines) + "\n\n"


def strip_noise_sections(text: str) -> str:
    for section in NOISE_SECTIONS:
        escaped = re.escape(section)
        text = re.sub(
            r'\n## ' + escaped + r'\b.*?(?=\n## |\Z)',
            '', text, flags=re.DOTALL | re.IGNORECASE
        )
        text = re.sub(
            r'\n### ' + escaped + r'\b.*?(?=\n## |\n### |\Z)',
            '', text, flags=re.DOTALL | re.IGNORECASE
        )
    return text


def strip_wiki_navigation(text: str) -> str:
    # "Jump to navigation / Jump to search" lines
    text = re.sub(r'^Jump to navigation[^\n]*\n?', '', text, flags=re.MULTILINE)
    text = re.sub(r'^Jump to search[^\n]*\n?', '', text, flags=re.MULTILINE)

    # "Retrieved from ..." line
    text = re.sub(r'^Retrieved from[^\n]*\n?', '', text, flags=re.MULTILINE)

    # "Categories:" blocks
    text = re.sub(r'\nCategories[:\n][\s\S]*?(?=\n[A-Z]|\Z)', '', text)

    # Navigation menu block at end
    text = re.sub(r'\nNavigation menu\n[\s\S]*$', '', text)

    # Breadcrumb lines (e.g. "Home > Buildings > Baker")
    text = re.sub(r'^[A-Z][^\n]*(?:>|»)[^\n]*\n?', '', text, flags=re.MULTILINE)

    # "From the MineColonies Wiki" header lines
    text = re.sub(r'^From (?:the )?[Mm]ine[Cc]olonies[^\n]*\n?', '', text, flags=re.MULTILINE)

    return text


def collapse_duplicate_runs(text: str) -> str:
    """Collapse carousel DOM artifacts where the same phrase repeats N times.

    MineColonies wiki uses a carousel component whose tooltip/popover nodes
    each re-render the same linked text, so Firecrawl captures it ~17 times
    per occurrence. After de-linking, this leaves "Town Hall Town Hall Town Hall..."
    Collapse any run of 3+ consecutive identical tokens (word or multi-word phrase)
    down to a single instance.
    """
    # Collapse runs of 3+ identical single words
    text = re.sub(r'\b(\w+)(?:\s+\1){2,}\b', r'\1', text)
    # Collapse runs of 3+ identical two-word phrases
    text = re.sub(r'\b(\w+ \w+)(?:\s+\1){2,}\b', r'\1', text)
    # Collapse runs of 3+ identical three-word phrases
    text = re.sub(r'\b(\w+ \w+ \w+)(?:\s+\1){2,}\b', r'\1', text)
    return text


def strip_duplicate_recipe_blocks(text: str) -> str:
    """Keep the first Recipe: block; remove all subsequent ones.

    The wiki carousel renders one recipe block per building level; Firecrawl
    captures all of them. Only the first is needed (they are identical).
    """
    pattern = re.compile(r'(\*\*Recipe:\*\*.*?)(?=\*\*Recipe:\*\*|\Z)', re.DOTALL)
    blocks = pattern.findall(text)
    if len(blocks) <= 1:
        return text
    # Find the position of the first block and everything after the last one
    first_start = text.find('**Recipe:**')
    last_match = list(pattern.finditer(text))[-1]
    after_last = text[last_match.end():]
    return text[:first_start] + blocks[0] + after_last


def clean_body(text: str) -> str:
    # Remove image links: ![alt](url)
    text = re.sub(r'!\[.*?\]\(.*?\)', '', text, flags=re.DOTALL)

    # Remove empty links: [](url)
    text = re.sub(r'\[\]\([^\)]*\)', '', text)

    # De-link: [text](url) -> text
    text = re.sub(r'\[([^\]]+)\]\([^\)]*\)', r'\1', text)

    # Collapse duplicate carousel runs (must happen immediately after de-linking)
    text = collapse_duplicate_runs(text)

    # Strip carousel prev/next arrow characters
    text = re.sub(r'^[‹›<>]\s*$', '', text, flags=re.MULTILINE)
    text = re.sub(r'[‹›]\s*', '', text)

    # Deduplicate recipe blocks (carousel renders one per building level)
    text = strip_duplicate_recipe_blocks(text)

    # Strip embedded JSON blobs
    json_blob_pattern = re.compile(
        r'\n\{[\s\S]*?"(?:title|rows|field)"[\s\S]*?\n\}',
        re.MULTILINE
    )
    text = json_blob_pattern.sub('', text)

    # Strip lines with escaped JSON patterns
    lines = text.splitlines()
    lines = [l for l in lines if '\\"' not in l]
    text = "\n".join(lines)

    # Strip noise sections
    text = strip_noise_sections(text)

    # Strip wiki navigation chrome
    text = strip_wiki_navigation(text)

    # Remove MDX/template syntax that may survive rendering ({% ... %})
    text = re.sub(r'\{%[^%]*%\}', '', text)
    text = re.sub(r'\{/[^}]*\}', '', text)

    # Remove [hide] and [show] markers
    text = re.sub(r'\\\[hide\\\]', '', text)
    text = re.sub(r'\\\[show\\\]', '', text)
    text = re.sub(r'\[hide\]', '', text)
    text = re.sub(r'\[show\]', '', text)

    # Remove wiki maintenance notices
    text = re.sub(
        r'This (?:section|page|article) (?:is missing|needs|would benefit from|is a stub)[^\n]*\n?',
        '', text, flags=re.IGNORECASE
    )
    text = re.sub(r'Please (?:expand|remove|help)[^\n]*\n?', '', text, flags=re.IGNORECASE)

    # Remove citation footnotes like [1], [2]
    text = re.sub(r'\[\d+\]', '', text)

    # Remove zero-width and invisible unicode
    text = re.sub(r'[​‌‍﻿ ]', ' ', text)

    # Remove empty section headers
    text = re.sub(r'\n(#{1,4}[^\n]+)\n+(?=#{1,4}|\Z)', '\n', text)

    # Collapse 3+ blank lines to 2
    text = re.sub(r'\n{3,}', '\n\n', text)

    return text.strip() + "\n"


def infer_topic(infile: str) -> str:
    import os
    base = os.path.basename(infile)
    # Strip category prefix and -raw.md suffix: "buildings-baker-raw.md" -> "Baker"
    base = re.sub(r'^[a-z]+-', '', base)        # strip "buildings-"
    base = re.sub(r'-raw\.md$', '', base)        # strip "-raw.md"
    base = re.sub(r'\.md$', '', base)
    return base.replace('-', ' ').title()


def main():
    if len(sys.argv) < 3:
        print("Usage: PYTHONUTF8=1 python3 scripts/clean-minecolonies-scrape.py <raw.md> <clean.md>")
        sys.exit(1)

    infile = sys.argv[1]
    outfile = sys.argv[2]

    with open(infile, encoding='utf-8') as f:
        raw = f.read()

    try:
        data = json.loads(raw)
        markdown = data.get("markdown", "")
        metadata = data.get("metadata", {})
    except json.JSONDecodeError:
        markdown = raw
        metadata = {}

    topic = infer_topic(infile)
    frontmatter = extract_frontmatter(metadata, topic)
    body = clean_body(markdown)

    with open(outfile, 'w', encoding='utf-8') as f:
        f.write(frontmatter + body)

    print(f"Written: {outfile} ({len(frontmatter + body)} chars)")


if __name__ == "__main__":
    main()
