---
title: MineColonies — Happiness API (ICitizenHappinessHandler, IHappinessModifier, HappinessConstants)
domain: minecolonies
fact: Per-citizen happiness is accessed via citizenData.getCitizenHappinessHandler(). Each modifier has an ID (string), a weight, and a factor (0–1 = negative impact, >1 = positive). Overall happiness via getHappiness(colony, citizenData) is a weighted aggregate on a 0–10 scale (MAX_HAPPINESS=10). There are 15 canonical modifier IDs. The key "homelessness" (not "housing") maps to the housing factor. Advisor diagnostic thresholds must be calibrated in-game — cannot be derived from source.
confidence: 0.95
usefulness: high
authority: authoritative
supersedes: wiki-needs-happiness-2026-06-02.md
---

## API Chain

```java
ICitizenHappinessHandler h = citizenData.getCitizenHappinessHandler();

// Overall weighted happiness — 0–10 scale (MAX_HAPPINESS = 10)
double overall = h.getHappiness(colony, citizenData);

// Per-modifier access
IHappinessModifier mod = h.getModifier("food");   // use HappinessConstants key
double factor = mod.getFactor(citizenData);         // 0–1 = negative, >1 = positive
double weight  = mod.getWeight();

// All modifier IDs registered on this citizen
List<String> names = h.getModifiers();
```

## Canonical Modifier IDs (from HappinessConstants)

| Constant | String ID |
|---|---|
| HOMELESSNESS | `homelessness` |
| UNEMPLOYMENT | `unemployment` |
| HEALTH | `health` |
| IDLEATJOB | `idleatjob` |
| SCHOOL | `school` |
| MYSTICAL_SITE | `mysticalsite` |
| SECURITY | `security` |
| SOCIAL | `social` |
| DAMAGE | `damage` |
| DEATH | `death` |
| RAIDWITHOUTDEATH | `raidwithoutdeath` |
| SLEPTTONIGHT | `slepttonight` |
| QUEST | `quest` |
| FOOD | `food` |
| HADGREATFOOD | `greatfood` |

**Count: 15.** V1 assumed 10 — missing: `damage`, `death`, `raidwithoutdeath`, `quest`, `greatfood`.

## Key Facts

- **Overall happiness scale:** 0–10 (`MAX_HAPPINESS = 10`). `getOverallHappiness()` on `IColony` returns the same scale.
- **Per-factor scale:** `getFactor()` returns 0–1 when the modifier is a negative impact; >1 when it is a positive boost. Neutral = 1.0.
- **V1 key error:** V1 code used `"housing"` — correct key is `"homelessness"`.
- **Threshold calibration:** V1 used RED < 0.5, YELLOW < 0.9 against per-factor values. These thresholds are plausible given the 0–1 scale but were never validated in-game. Must be calibrated by observation.
- **Starting happiness:** Overall happiness at colony start is reported to begin near -1 (Dragon, 2026-06-06). This likely reflects compound negatives from simultaneous unemployment + homelessness + food dependency on day 1. The `getHappiness()` impl is not in the API files — exact starting value and formula require in-game verification.
- **Modifier types:** `StaticHappinessModifier` (fixed), `TimeBasedHappinessModifier` (degrades over days while condition persists), `ExpirationBasedHappinessModifier` (active for a limited period then expires to 1.0).

## What Is NOT Here

- The weighted aggregation formula in `getHappiness()` — impl only, not in API files.
- The precise starting value per modifier at citizen spawn.
- Whether all 15 modifiers are registered by default on every citizen or only when triggered.

**Source:** [[docs/api/minecolonies/entity/citizen/citizenhandlers/ICitizenHappinessHandler.java]], [[docs/api/minecolonies/entity/citizen/happiness/IHappinessModifier.java]], [[docs/api/minecolonies/util/constant/HappinessConstants.java]]
