#!/usr/bin/env bash
# SessionStart hook: passively report candidate folders that need attention.

exec </dev/null

PROJECT_DIR="${CLAUDE_PROJECT_DIR:-$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)}"
MEM_BASE="$PROJECT_DIR/.memsearch/memory"

CANDIDATE_DIRS=(
  "$PROJECT_DIR/.memsearch/candidates/inbox"
  "$PROJECT_DIR/.memsearch/candidates/tentative-approved"
  "$MEM_BASE/framework/candidates/extracted"
  "$MEM_BASE/framework/candidates/review"
  "$MEM_BASE/framework/candidates/tentative-approved"
  "$MEM_BASE/domains/neoforge/candidates/extracted"
  "$MEM_BASE/domains/neoforge/candidates/review"
  "$MEM_BASE/domains/neoforge/candidates/tentative-approved"
  "$MEM_BASE/domains/minecolonies/candidates/extracted"
  "$MEM_BASE/domains/minecolonies/candidates/review"
  "$MEM_BASE/domains/minecolonies/candidates/tentative-approved"
  "$MEM_BASE/projects/dragontweaksv2/candidates/extracted"
  "$MEM_BASE/projects/dragontweaksv2/candidates/review"
  "$MEM_BASE/projects/dragontweaksv2/candidates/tentative-approved"
)

pending=()
for dir in "${CANDIDATE_DIRS[@]}"; do
  if [ -d "$dir" ]; then
    while IFS= read -r f; do
      [ -n "$f" ] && pending+=("${f#$PROJECT_DIR/}")
    done < <(find "$dir" -maxdepth 1 -type f \( -name "*.md" -o -name "*.json" -o -name "*.yaml" \) 2>/dev/null)
  fi
done

if [ ${#pending[@]} -eq 0 ]; then
  exit 0
fi

# Build warning message
msg="Candidate queue notice — pending memory candidates exist:\n"
for f in "${pending[@]}"; do
  msg+="  $f\n"
done
msg+="\nThis is informational only. Continue the user's requested work unless the user explicitly asks to process memory candidates."

# JSON-encode: escape double quotes; \n in msg is already the JSON escape sequence
escaped="${msg//\"/\\\"}"
json_msg="\"$escaped\""

echo "{\"systemMessage\": \"candidate-queue-check: ${#pending[@]} file(s) pending\", \"hookSpecificOutput\": {\"hookEventName\": \"SessionStart\", \"additionalContext\": $json_msg}}"
