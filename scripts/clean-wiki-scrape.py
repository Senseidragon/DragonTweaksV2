"""
clean-wiki-scrape.py

Cleans a raw firecrawl wiki scrape file for use as domain knowledge.

Input:  a file containing a JSON envelope {"markdown": "...", "metadata": {...}}
        as returned by firecrawl and written verbatim to docs/

Output: a clean markdown file with:
        - YAML frontmatter reconstructed from firecrawl metadata
        - All image links removed (both ![alt](url) and [](url) forms)
        - All markdown links de-linked: [text](url) -> text
        - Embedded JSON blobs stripped
        - Bedrock Edition paragraphs, sections, and inline qualifiers removed
        - Java Edition inline tags removed (noise once BE is stripped)
        - Spawn egg references removed
        - NBSP and invisible unicode removed
        - XP table lines removed
        - "Jump up to" footnote links removed
        - Drop tables collapsed to simple item lists
        - Entire noise sections stripped: History, Achievements, Advancements,
          Data values, Gallery, Trivia, References, External links, Navigation
        - Wiki navigation artifacts removed (language list, jump links, categories)
        - [hide]/[show] toggle markers removed
        - Wiki maintenance notices removed
        - _upcoming: [...] and [verify] inline markers removed
        - Excessive blank lines collapsed

Usage:
    PYTHONUTF8=1 python3 scripts/clean-wiki-scrape.py docs/<mob>-raw.md docs/<mob>-clean.md
"""

import sys
import json
import re


# Section headers that are pure noise — strip from header through next same-level header or EOF
NOISE_SECTIONS = {
    'History', 'Achievements', 'Advancements', 'Data values', 'Gallery',
    'Trivia', 'References', 'External links', 'Navigation', 'Issues',
    'Sounds', 'Notes',
}


def extract_frontmatter(metadata: dict) -> str:
    lines = ["---"]
    if metadata.get("sourceURL"):
        lines.append(f'source_url: {metadata["sourceURL"]}')
    if metadata.get("title"):
        lines.append(f'title: "{metadata["title"]}"')
    lines.append("source_version: \"1.21.1\"")
    lines.append("source_type: official_wiki")
    lines.append("cleaned: true")
    lines.append("---")
    return "\n".join(lines) + "\n\n"


def strip_noise_sections(text: str) -> str:
    """Remove entire top-level sections that are pure noise.

    Handles both ATX-style (## Header) and Setext-style (Header\n-------)
    headers, which the Minecraft wiki produces for top-level sections.
    """
    for section in NOISE_SECTIONS:
        escaped = re.escape(section)
        # ATX style: ## Section ... until next ## or EOF
        text = re.sub(
            r'\n## ' + escaped + r'\b.*?(?=\n## |\Z)',
            '', text, flags=re.DOTALL | re.IGNORECASE
        )
        # Setext style: Section\n---... ... until next Setext header or ## or EOF
        # Setext underline is 2+ dashes or equals signs
        text = re.sub(
            r'\n' + escaped + r'\n[-=]{2,}.*?(?=\n\S[^\n]*\n[-=]{2,}|\n## |\Z)',
            '', text, flags=re.DOTALL | re.IGNORECASE
        )
    return text


def strip_wiki_navigation(text: str) -> str:
    """Remove wiki chrome: language list, jump links, 'From Minecraft Wiki', categories."""
    # Language list block: "N languages\n\n* Deutsch\n* Español\n..." up to first real content
    text = re.sub(
        r'^\d+ languages\n[\s\S]*?(?=\n[A-Z#])',
        '',
        text,
        flags=re.MULTILINE
    )

    # "From Minecraft Wiki" line
    text = re.sub(r'^From Minecraft Wiki[^\n]*\n?', '', text, flags=re.MULTILINE)

    # "Jump to navigation / Jump to search" lines
    text = re.sub(r'^Jump to navigation[^\n]*\n?', '', text, flags=re.MULTILINE)
    text = re.sub(r'^ Jump to search[^\n]*\n?', '', text, flags=re.MULTILINE)
    text = re.sub(r'^Jump to search[^\n]*\n?', '', text, flags=re.MULTILINE)

    # "For other uses, see Foo (disambiguation)" lines
    text = re.sub(r'^For other uses,[^\n]*\n?', '', text, flags=re.MULTILINE)

    # "Retrieved from ..." line
    text = re.sub(r'^Retrieved from[^\n]*\n?', '', text, flags=re.MULTILINE)

    # "Categories:" block — everything from "Categories\n:" to end of list
    text = re.sub(r'\nCategories\n:[\s\S]*?(?=\nHidden categories:|\n[A-Z]|\Z)', '', text)
    text = re.sub(r'\nHidden categories:[\s\S]*?(?=\n[A-Z]|\Z)', '', text)

    # "Navigation menu" block
    text = re.sub(r'\nNavigation menu\n[\s\S]*$', '', text)

    return text


def clean_body(text: str) -> str:
    # Remove image links: ![alt](url)
    text = re.sub(r'!\[.*?\]\(.*?\)', '', text, flags=re.DOTALL)

    # Remove empty links: [](url)
    text = re.sub(r'\[\]\([^\)]*\)', '', text)

    # De-link: [text](url) -> text (non-greedy, handles multiline link text)
    text = re.sub(r'\[([^\]]+)\]\([^\)]*\)', r'\1', text)

    # Strip embedded JSON blobs — detect by lookahead: a lone { followed within
    # 3 lines by a line containing "title": or "rows": or \\"field\\"
    json_blob_pattern = re.compile(
        r'\n\{[\s\S]*?"(?:title|rows|field)"[\s\S]*?\n\}',
        re.MULTILINE
    )
    text = json_blob_pattern.sub('', text)

    # Also strip any remaining lines that look like escaped JSON (contain \\" patterns)
    lines = text.splitlines()
    cleaned_lines = [l for l in lines if '\\"' not in l]
    text = "\n".join(cleaned_lines)

    # Strip entire noise sections before other cleaning (reduces work on dead content)
    text = strip_noise_sections(text)

    # Strip wiki navigation chrome
    text = strip_wiki_navigation(text)

    # Remove [hide] and [show] table toggle markers (plain text artifacts)
    text = re.sub(r'\\\[hide\\\]', '', text)
    text = re.sub(r'\\\[show\\\]', '', text)
    text = re.sub(r'\[hide\]', '', text)
    text = re.sub(r'\[show\]', '', text)

    # Remove wiki maintenance notices
    text = re.sub(
        r'This section (?:is missing information about|would benefit from)[^\n]*\n?',
        '', text, flags=re.IGNORECASE
    )
    text = re.sub(
        r'Please (?:expand|remove) (?:the section|this notice)[^\n]*\n?',
        '', text, flags=re.IGNORECASE
    )
    text = re.sub(
        r'The specific instructions are:[^\n]*\n?',
        '', text, flags=re.IGNORECASE
    )
    text = re.sub(
        r'Further details may exist on the talk page\.[^\n]*\n?',
        '', text, flags=re.IGNORECASE
    )

    # Remove _upcoming: [...] inline markers
    text = re.sub(r'\_upcoming:[^\]]*\]\_?', '', text)
    text = re.sub(r'_upcoming:[^\]]*\]', '', text)

    # Remove [verify] and \[verify\_\] inline markers
    text = re.sub(r'\\?\[verify[^\]]*\]\\?\_?', '', text)

    # Fix split italic markers: _text\n_ -> _text_
    text = re.sub(r'_([^\n_]+)\n_', r'_\1_', text)

    # Join orphaned comma continuations: word\n, next -> word, next
    text = re.sub(r'(\w)\n(,)', r'\1\2', text)

    # Join orphaned period at end: word\n. -> word.
    text = re.sub(r'(\w)\n(\.)', r'\1\2', text)

    # Join space-led continuation lines: word\n [a-z] -> word [a-z]
    text = re.sub(r'(\w)\n +([a-z])', r'\1 \2', text)

    # Remove Bedrock Edition paragraphs: "In _Bedrock Edition_, ..." sentences
    text = re.sub(r'In _Bedrock Edition_,[^\n]+\n?', '', text)

    # Remove Bedrock Edition subsections (#### _Bedrock Edition_ ... until next header)
    text = re.sub(r'####\s*_Bedrock Edition_.*?(?=\n#{1,4}\s|\Z)', '', text, flags=re.DOTALL)

    # Remove Bedrock Edition inside table cells (e.g. **In _Bedrock Edition_<br>:** ...)
    text = re.sub(r'\*?\*?In _Bedrock Edition_.*?(?=\||\n)', '', text)

    # Remove Bedrock Edition standalone label lines
    text = re.sub(r'_Bedrock Edition_\n?', '', text)

    # Remove Bedrock Edition inline qualifiers: [BE only], ‌[BE only]
    text = re.sub(r'‌?\\_?\[_?BE[^\]]*\]_?\\?', '', text)
    text = re.sub(r'\[BE[^\]]*\]', '', text)

    # Remove Java Edition inline qualifiers: [JE only]
    text = re.sub(r'‌?\\_?\[_?JE[^\]]*\]_?\\?', '', text)
    text = re.sub(r'\[JE[^\]]*\]', '', text)

    # Strip "In _Java Edition_," prefix from sentences — keep the rest (it's the only edition now)
    text = re.sub(r'In _Java Edition_,\s*', '', text)

    # Remove standalone _Java Edition_ label lines
    text = re.sub(r'_Java Edition_\n?', '', text)

    # Remove spawn egg references
    text = re.sub(r'[^\n]*spawn egg[^\n]*\n?', '', text, flags=re.IGNORECASE)

    # Remove NBSP
    text = text.replace('\u00a0', ' ')

    # Remove "Jump up to" footnote lines
    text = re.sub(r'[^\n]*Jump up to[^\n]*\n?', '', text, flags=re.IGNORECASE)
    text = re.sub(r'\s*↑[^\n]*\n?', '', text)

    # Remove XP / experience lines
    text = re.sub(r'[^\n]*\bXP\b[^\n]*\n?', '', text)
    text = re.sub(r'[^\n]*experience point[^\n]*\n?', '', text, flags=re.IGNORECASE)

    # Collapse drop tables to a simple item list
    TABLE_NOISE = {
        'Item', 'Default', 'Average', 'Amount', 'Probability', 'Killed',
        'Expected Drops', 'Quantity', 'Chance', 'Looting I', 'Looting II',
        'Looting III', 'Decimal', 'Fraction', 'Distribution', 'Expectation',
        'Category', 'Spawn area', 'Spawn weight', 'Spawn chance', 'Group size',
        'JE', 'BE',
    }

    def collapse_drop_table(m):
        block = m.group(0)
        items = re.findall(r'\|\s+([A-Z][A-Za-z\s\']+?)\s+\|', block)
        items = [i.strip() for i in items if i.strip() and i.strip() not in TABLE_NOISE]
        items = list(dict.fromkeys(items))  # deduplicate preserving order
        if items:
            return '\n**Drops on death:** ' + ', '.join(items) + '\n'
        return ''

    text = re.sub(
        r'### On death.*?(?=\n##|\n###|\Z)',
        collapse_drop_table,
        text,
        flags=re.DOTALL
    )

    # Remove empty section headers (header followed immediately by another header or end)
    text = re.sub(r'\n(#{1,4}[^\n]+)\n+(?=#{1,4}|\Z)', '\n', text)

    # Fix orphaned "in ." and "in  or N blocks in ." artifacts left after edition stripping
    text = re.sub(r'\s+in\s+or\s+\d+\s+blocks\s+in\s+\.', '.', text)
    text = re.sub(r'\s+in\s+\.', '.', text)
    text = re.sub(r'\s+in\s+\n', '\n', text)

    # Fix orphaned "In <br>:" inside table cells
    text = re.sub(r'\*\*In\s*<br>\s*:\*\*\s*', '', text)

    # Remove Melee attack entries from infobox (BE only — no melee in JE)
    text = re.sub(r'\*\s*\*\s*\*\s*\n\s*\*\*Melee:\*\*.*?(?=\||\n\|)', '', text, flags=re.DOTALL)

    # Fix sentences that lost their leading capital after "In _Java Edition_," strip
    text = re.sub(r'(?<=\n)([a-z])', lambda m: m.group(1).upper(), text)

    # Remove wave bonus / increasing wave data lines
    text = re.sub(r'[^\n]*(bonus wave|wave increases|decrease in number as the waves)[^\n]*\n?', '', text, flags=re.IGNORECASE)

    # Remove wiki edit section links left as plain text artifacts
    text = re.sub(r'\\\[edit.*?\\\]', '', text)

    # Remove citation footnotes like [1], [2]
    text = re.sub(r'\[\d+\]', '', text)

    # Remove zero-width non-joiner and other invisible unicode
    text = re.sub(r'[\u200b\u200c\u200d\ufeff]', '', text)

    # Collapse runs of 3+ blank lines to 2
    text = re.sub(r'\n{3,}', '\n\n', text)

    return text.strip() + "\n"


def main():
    if len(sys.argv) < 3:
        print("Usage: python3 clean-wiki-scrape.py <input> <output>")
        sys.exit(1)

    infile = sys.argv[1]
    outfile = sys.argv[2]

    with open(infile, encoding='utf-8') as f:
        raw = f.read()

    # Unwrap JSON envelope
    try:
        data = json.loads(raw)
        markdown = data.get("markdown", "")
        metadata = data.get("metadata", {})
    except json.JSONDecodeError:
        # Not JSON — treat as plain markdown (already partially processed)
        markdown = raw
        metadata = {}

    frontmatter = extract_frontmatter(metadata)
    body = clean_body(markdown)

    with open(outfile, 'w', encoding='utf-8') as f:
        f.write(frontmatter + body)

    print(f"Written: {outfile} ({len(frontmatter + body)} chars)")


if __name__ == "__main__":
    main()
