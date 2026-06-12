---
topic: Trader Llama
type: advisor-artifact
source: "[[https://minecraft.wiki/w/Trader_Llama]]"
scraped: 2026-06-08
version: 1.21.1
pipeline_stage: advisor-artifact
---

# Trader Llama

Trader llamas are a variant of llamas that spawn leashed to wandering traders. They are functionally identical to regular llamas but defend their trader and despawn alongside them unless the lead is broken.

## Spawning

Two trader llamas attempt to spawn within 4 blocks of every wandering trader (each with 10 attempts). They spawn leashed to the wandering trader.

## Key Differences from Regular Llamas

- **Untamable while leashed to trader.** Once the lead to the wandering trader is removed (or the trader is killed), they become tamable -- or are immediately considered tamed.
- **Despawning:** Trader llamas despawn ~1 tick before their wandering trader. The despawn timer resets if still leashed. Tamed trader llamas do NOT despawn.
- **Default carpet:** Trader llamas always wear a unique blue rug design. It can be replaced with a different carpet but cannot be removed.
- **Defender:** Trader llamas spit at players or mobs that attack the wandering trader they are leashed to.

## Behavior (shared with Llama)

Spits when attacked (1HP Easy/Normal, 1.5HP Hard). Not flammable. Tamed trader llamas do not spit at mobs attacking their owner (but still retaliate if attacked themselves).

**Caravans:** Leashing a trader llama causes up to 9 nearby llamas to follow, forming a caravan of up to 10.

## Storage

After taming, a chest can be equipped (press use while holding chest). Inventory slots depend on strength (1-5):

| Strength | Slots | Spawn Probability |
|----------|-------|-------------------|
| 1 | 3 | 32.8% |
| 2 | 6 | 32.8% |
| 3 | 9 | 32.8% |
| 4 | 12 | 0.8% |
| 5 | 15 | 0.8% |

## Taming

Same mechanics as regular llama: mount repeatedly until hearts appear. Each attempt checks a random number 0-29 against `Temper` value (starts 0, max 30). Failure increases Temper by 5. Feeding wheat (+3) or hay bale (+6) speeds taming.

## Feeding

| Food | Heals | Growth Speed | Temper |
|------|-------|-------------|--------|
| Wheat | 2HP | +10s | +3 |
| Hay Bale | 10HP | +1:30 min | +6, triggers love mode |

## Breeding

Tamed trader llamas breed with hay bales. Offspring of two trader llamas is always a trader llama (and wears the default blue rug). Offspring of a trader llama x normal llama is always a normal llama.

## Drops

- Leather on death
- 1-7 XP upon breeding
