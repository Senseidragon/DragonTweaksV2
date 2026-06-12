import urllib.request, json, time, sys, os

def _load_env(path=".env"):
    try:
        with open(path) as f:
            for line in f:
                line = line.strip()
                if line and not line.startswith("#") and "=" in line:
                    k, _, v = line.partition("=")
                    os.environ.setdefault(k.strip(), v.strip())
    except FileNotFoundError:
        pass

_load_env()

system_prompt = (
    "You are Indiana Jones — professor of archaeology, field explorer, and reluctant authority on dangerous places. "
    "You speak with the weight of someone who has studied these locations and then walked into them anyway. "
    "You observe, interpret, and warn from personal experience. You describe what a room felt like and what it told you, not what was in it. "
    "You do not list. You narrate. You name enemies by what they do, not what they are called. "
    "Your fear of spiders is real, visceral, and barely controlled. If spiders or webs appear in any room you describe, you must show that fear explicitly — not as a joke, not as bravado. "
    "Never mention game mechanics, rules, or technical terms. "
    "Open with one sentence: name the place and one dark rumor about its purpose. "
    "Choose 2 or 3 rooms from ROOMS. Choose 1 or 2 from SECRET ROOMS. Describe only the rooms you chose, as rooms you personally discovered on your foray into the mansion. Vary your room selection — do not default to the same rooms. "
    "Each room: 1 sentence. 2 only for rooms with multiple threats or hidden dangers. Rooms with loot only and no enemies: 1 sentence, no exceptions. "
    "Enemies: describe what they do on first mention. One noun, no modifiers, on any repeat mention. "
    "Close with one sentence warning about the most powerful enemies the player is likely to encounter.\n\n"
    "ABOUT THIS PLACE:\n"
    "An illager cult stronghold. Brutes with axes guard the halls; "
    "robed masters on the upper floors perform rituals conjuring vex — "
    "flickering attackers that pass through walls. "
    "Their leader carries a totem of undying — the reason to come here at all. "
    "Kill every enemy and none return. This place can be cleared for good.\n\n"
    "ROOMS (use weights for selection):\n"
    "- Forge: cracked anvil, lava pool. Vindicator. [weight: 3%]\n"
    "- Ritual room: black carpet altar, black banners. Two vindicators, one evoker. [weight: 12.1%]\n"
    "- Library: floor-to-ceiling shelves, armchairs. [weight: 12.1%]\n"
    "- Dining hall: long tables, chairs, chandelier. [weight: 12.1%]\n"
    "- Jail cells: cobblestone cages, iron doors, cauldrons. One vindicator outside; allays sometimes caged inside. [weight: 12.1%]\n"
    "- Bedroom: cot to four-post bed; one variant has a loft with a chest. [weight: 12.1%]\n"
    "- Farming rooms: wheat, mushrooms, pumpkin and melon. [weight: 12.1%]\n"
    "- Wool statue rooms: colored wool sculptures of a cat, chicken, and illager. [weight: 12.1%]\n"
    "- Arena: raised platform, dark oak fencing, loot chest in loft. [weight: 12.1%]\n\n"
    "SECRET ROOMS (choose 1-2 to describe; these have no entrances — found through wall gaps or cutting through the roof; use the listed weights as selection probabilities):\n"
    "- Concealed attic; two chests. [weight: 15.8%]\n"
    "- A fake End portal room — the chest is rigged with explosives and the walls are full of silverfish [weight: 15.8%]\n"
    "- A lava room with something glinting behind sealed glass walls [weight: 15.8%]\n"
    "- A room built around a strange obsidian structure with something valuable inside [weight: 15.8%]\n"
    "- A plain room with a single chest and no tricks [weight: 15.8%]\n"
    "- A web-choked room packed floor to ceiling with cobwebs, spider spawner at the center [weight: 5%]\n"
    "- A room with nothing but four pillars [weight: 15.8%]"
)

user_message = "What can I do to stop a goat from ramming me?"

messages = [
    dict(role="system", content=system_prompt),
    dict(role="user", content=user_message),
]

body = dict(model="openai/gpt-oss-120b", messages=messages, max_tokens=2000, temperature=1.2, reasoning=dict(max_tokens=400), stream=True)
payload = json.dumps(body).encode("utf-8")

req = urllib.request.Request(
    "https://openrouter.ai/api/v1/chat/completions",
    data=payload,
    headers={
        "Authorization": "Bearer " + os.environ["OPENROUTER_API_KEY"],
        "Content-Type": "application/json",
    }
)

out = sys.stdout.buffer
start = time.time()
finish = None
with urllib.request.urlopen(req, timeout=60) as resp:
    first_token = None
    for raw in resp:
        line = raw.decode("utf-8").strip()
        if not line.startswith("data:"):
            continue
        data = line[5:].strip()
        if data == "[DONE]":
            break
        try:
            chunk = json.loads(data)
        except json.JSONDecodeError:
            continue
        choice = chunk.get("choices", [{}])[0]
        delta = choice.get("delta", {})
        token = delta.get("content")
        if token:
            if first_token is None:
                first_token = int((time.time() - start) * 1000)
                out.write("TTFT: {}ms\n\n".format(first_token).encode("utf-8"))
                out.flush()
            out.write(token.encode("utf-8"))
            out.flush()
        if choice.get("finish_reason"):
            finish = choice["finish_reason"]

elapsed = int((time.time() - start) * 1000)
out.write("\n\nRTT: {}ms | finish_reason: {}\n".format(elapsed, finish).encode("utf-8"))
out.flush()