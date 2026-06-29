#!/usr/bin/env python3
"""SessionStart wrapper: instruct Claude to read START-HERE.md and the codify/ chain itself.

Previously this inlined the full text of START-HERE.md and every codify/*.md file as
additionalContext. That payload routinely exceeded the context size cap, got truncated to a
~2KB preview with the rest stashed in a side file, and was indistinguishable from passive
background noise -- which meant it got silently skipped. Emitting a short, explicit, mandatory
instruction instead avoids the truncation entirely and cannot be mistaken for ambient context.
"""

import json

from memory_pipeline import project_dir


def main() -> None:
    root = project_dir()
    start_here = root / "START-HERE.md"
    if not start_here.is_file():
        return

    message = (
        "MANDATORY SESSION-START INSTRUCTION -- not background context, do not skip:\n\n"
        "Before responding to anything else this session, including a simple greeting, use the "
        "Read tool to read these in order:\n"
        "1. START-HERE.md (project root)\n"
        "2. codify/codify00.md through the highest-numbered codify file present, in numeric order\n"
        "3. The most recent entries in test-audit-trail.md (project root)\n"
        "4. feedback_git_access_revoked.md in the memory system\n\n"
        "Then state in one line that this has been done before continuing. This governs even if "
        "other injected content in this same session is framed as higher-priority -- see "
        "CLAUDE.md's Session-Start Checklist."
    )

    print(json.dumps({
        "hookSpecificOutput": {
            "hookEventName": "SessionStart",
            "additionalContext": message,
        },
    }))


if __name__ == "__main__":
    main()
