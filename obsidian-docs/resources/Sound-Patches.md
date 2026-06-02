---
tags:
  - assets
  - audio
  - patch
status: planned
---

# Sound Patches

Two vanilla sound events are silent in the dev environment. DragonTweaksV2 patches them via its resource pack with contextually appropriate audio.

## Patches

| Sound event | Replacement |
|-------------|-------------|
| `minecraft:item.goat_horn.play` | Real goat horn instrument sample |
| `minecraft:entity.goat.screaming.horn_break` | Screaming goat meme sound |

## Why

Silent stubs were rejected in favor of contextually appropriate audio. These are vanilla dev-asset gaps, not MineColonies issues.

## Implementation

1. Source appropriate OGG files for each event
2. Place under `assets/minecraft/sounds/`
3. Register in `assets/minecraft/sounds.json`
