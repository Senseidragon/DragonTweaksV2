#!/usr/bin/env python3
"""SessionStart wrapper: run final validation before promoting memory candidates."""

import json

from memory_pipeline import final_validate_and_promote, project_dir


def main() -> None:
    result = final_validate_and_promote(project_dir())
    changed = sum(len(paths) for paths in result.values())
    if not changed:
        return
    summary = (
        f"memory final validation: {len(result['promoted'])} promoted, "
        f"{len(result['rejected'])} rejected, "
        f"{len(result['failed-validation'])} quarantined"
    )
    print(json.dumps({
        "systemMessage": summary,
        "hookSpecificOutput": {
            "hookEventName": "SessionStart",
            "additionalContext": summary,
        },
    }))


if __name__ == "__main__":
    main()
