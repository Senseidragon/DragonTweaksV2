# DragonTweaksV2

NeoForge mod for Minecraft 1.21.1 by SenseiDragon. Adds an immersive in-game AI advisor system powered by OpenRouter.

---

## Session Status — 2026-06-10

> **Superseded.** This entire session log describes an abandoned design (Indiana Jones persona, `#a`/`#f` command-prefix routing, static woodland-mansion lore with no dynamic injection). The advisor was rebuilt around dynamic `LoreIndex` lookup and a persona-driven system prompt — see `docs/superpowers/specs/2026-06-20-advisor-persona-grounding-design.md`. Kept below for historical reference.

### What We're Building

An in-game NPC advisor that responds to player chat commands (`#a`, `#f`) with immersive, in-world responses backed by lore data and an LLM. The advisor role adopts an Indiana Jones persona — knowledgeable scholar/adventurer, dry wit, visceral spider fear. Lore is injected per-query from `docs/minecraft-lore/`.

### Current State

- `OpenRouterService.java` handles async HTTP to OpenRouter. No system prompt is wired yet — raw player input is sent as the only message.
- `ChatCommandHandler.java` routes `#a` → advisory model, `#f` → flavor model. Model IDs come from `model_config.json`.
- `docs/deprecated/assistant_roles.md` — role definitions including persona and prompt rules (just written this session). Superseded as of 2026-06-20; see `docs/superpowers/specs/2026-06-20-advisor-persona-grounding-design.md`.
- `tmp/advisor_test.py` — standalone test script for firing queries at OpenRouter outside of Minecraft.

### What We Defined This Session

#### `docs/deprecated/assistant_roles.md` — Role 1: Advisor (`#a`)

- Model: cheapest advisory (reasoning) model — currently `openai/gpt-oss-120b`
- Persona: Indiana Jones — archaeologist, scholar/adventurer, direct, dry, occasionally self-deprecating
- Names enemies by what they do, not their game names
- Fear of spiders: real, visceral, barely controlled — must show explicitly if spiders/webs appear
- Describes 3–4 rooms as scenes personally witnessed, 1–2 secret rooms
- No lists, no filler, every word earns its place
- Exactly 4 sentences, never exceed 5
- In-world language only — no game mechanics, rules, or technical terms

#### System Prompt (current working version)

```
You are Indiana Jones — professor of archaeology, field explorer, and reluctant authority on dangerous places.
You speak with the weight of someone who has studied these locations and then walked into them anyway.
You observe, interpret, and warn from personal experience. You describe what a room felt like and what it told you, not what was in it.
You do not list. You narrate. You name enemies by what they do, not what they are called.
Your fear of spiders is real, visceral, and barely controlled. If spiders or webs appear in any room you describe, you must show that fear explicitly — not as a joke, not as bravado.
Never mention game mechanics, rules, or technical terms.
Open with one sentence: name the place and one dark rumor about its purpose.
Choose 2 or 3 rooms from ROOMS. Choose 1 or 2 from SECRET ROOMS. Describe only the rooms you chose, as rooms you personally discovered on your foray into the mansion. Vary your room selection — do not default to the same rooms.
Each room: 1 sentence. 2 only for rooms with multiple threats or hidden dangers. Rooms with loot only and no enemies: 1 sentence, no exceptions.
Enemies: describe what they do on first mention. One noun, no modifiers, on any repeat mention.
Close with one sentence warning about the most powerful enemies the player is likely to encounter.
```

#### Lore Block Format (injected per-query into system prompt)

Structure lore is pulled from `docs/minecraft-lore/structures/<name>.md`. The lore block fed to the model includes:
- A brief description of the structure's purpose/lore
- A bulleted list of regular rooms (model picks 3–4)
- A bulleted list of secret rooms (model picks 1–2)

Example lore block used for woodland mansion testing is in `tmp/advisor_test.py`.

### Difficulties Encountered

1. **Model burns tokens on reasoning before responding** — `openai/gpt-oss-120b` is a reasoning model. With `max_tokens: 500`, all tokens went to chain-of-thought and content came back null. Fixed by setting `max_tokens: 2000`.

2. **Response truncation** — without `max_tokens`, the model hit a provider-side limit mid-sentence. Set to 2000 to give reasoning + content enough room.

3. **Spider room always selected** — the web-choked secret room was first in the list and the spider persona fear biased the model toward always choosing it. Fixed by shuffling the secret rooms list order so the spider room is no longer first.

4. **Spider fear not showing in response** — even when the model chose the spider room, it described it without any fear reaction. Fixed by strengthening the prompt: "If spiders or webs appear in any room you describe, you must show that fear explicitly — not as a joke, not as bravado."

5. **Windows console encoding error** — model responses contain non-ASCII characters (e.g. `‑` non-breaking hyphen) that crash `print()` on cp1252 terminals. Fixed by writing to `sys.stdout.buffer` with explicit UTF-8 encoding.

6. **Response too sparse / filler-heavy (early attempts)** — first few responses were action-hero summaries, not scholarly scene descriptions. Root cause: persona prompt said "in the vein of Indiana Jones" which wasn't specific enough. Fixed by reframing him as professor/scholar first, and requiring each sentence to carry specific information or vivid description.

7. **Room descriptions were inventory lists in sentence form** — model kept cramming all rooms into one sentence as a list. Fixed by adding "You do not list. You narrate." and restructuring the lore block to present rooms as discrete scene options rather than a flat inventory.

### Test Script

`tmp/advisor_test.py` — fires a single query at OpenRouter with the current system prompt and woodland mansion lore block. Prints TTFT, RTT, and finish_reason.

Current test question: *"What can I do to stop a goat from ramming me?"*

### Next Steps

- Wire system prompt + lore injection into `OpenRouterService.query()` in Java
- **Implement dynamic lore injection** — parse the player's question to identify the structure or entity, load the matching lore file from `docs/minecraft-lore/`, inject it into the system prompt at query time. This is the critical unresolved gap (see item 19 below).
- Define Role 2 (flavor `#f`)

### Prompt Tuning Session — 2026-06-10 Mistakes and Corrections

8. **Unsolicited source scanning** — During a status report, Java source files were read without permission. Rule: do not read any source file without explicit authorization. Use README.md and docs/ for status.

9. **Model swap proposed for speed** — When reasoning model latency was raised, the first suggestion was to swap models. Rejected as lazy. Root fix: add `reasoning: {max_tokens: 400}` to cap CoT spend, and `temperature: 1.2` for randomness.

10. **Response not shown in full** — Multiple times the model response was summarized or truncated instead of printed verbatim. Rule: always show the full response.

11. **Script edited between runs** — The test script was modified during runs when only execution was needed. Rule: run as-is; only edit when a specific change is requested.

12. **Verify skill scope creep** — When asked to "test it," the verify skill was invoked, adding unnecessary procedure. Rule: use what the project already has.

13. **Conflicting room count instructions** — Instruction section said "2 to 3 rooms" but the ROOMS header still said "choose 3-4." Fixed by aligning both.

14. **Persona section too long** — 20 lines at 40 chars. Compressed to 12 by removing framing the model infers from the Indiana Jones name.

15. **10-word room limit crushed the voice** — Too terse; responses sounded like telegrams. Replaced with a 2-sentence limit at 40 chars per sentence.

16. **Room descriptions were editorial** — Phrases like "most dangerous room" and "no permanent guards" are judgments, not facts. Room entries now contain only: name, physical description, mobs present. The model does the rest.

17. **Model blamed for prompt failure** — Model produced bloated, unconstrained output. Initial diagnosis blamed `temperature: 1.2` exceeding o-series ceiling. Rejected by Dragon — model had performed fine previously. Root cause was the prompt: terse notation (`rooms: 2[75%]`) read as metadata not commands; `"End with a warning to be careful"` generated multi-paragraph closing advice; `"40 characters per sentence"` is a character-counting task LLMs cannot do. Fixed by rewriting the structural block with plain imperative commands, explicit prohibitions, and dropping the character limit.

18. **Response still not shown in full** — Recurring violation. After runs, responses were summarized or followed immediately with commentary instead of printed verbatim first. Rule stands: full response first, every time, no exceptions.

19. **Lore injection is not dynamic — goat question failed** — Asked the model "What can I do to stop a goat from ramming me?" The model ignored the question entirely and responded with a woodland mansion walkthrough. Root cause: the system prompt lore block is hardcoded to the woodland mansion. Any user question is overridden by the static lore context. Did not fix this session. Dynamic lore injection (parse player question → identify entity/structure → load matching lore file → inject into system prompt) is the next required step before the advisor is usable for arbitrary player queries.

---

## Build

```bash
./gradlew build
./gradlew runClient
./gradlew runServer
```

Requires `.env` with `OPENROUTER_API_KEY` and `model_config.json` in the project root.
