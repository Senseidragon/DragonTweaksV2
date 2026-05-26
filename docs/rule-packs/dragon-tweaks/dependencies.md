# Dragon Tweaks — Dependency Constraints

## Pinned versions

All versions are pinned in `gradle.properties` and referenced via template expansion in `neoforge.mods.toml`.

| Component | Pinned version | Source |
|---|---|---|
| Minecraft | 1.21.1 | `gradle.properties` → `minecraft_version` |
| NeoForge | 21.1.230 | `gradle.properties` → `neo_version` |
| NeoGradle Userdev Plugin | 7.1.36 | `build.gradle` plugins block |
| Parchment Mappings (MC) | 1.21.1 | `gradle.properties` → `neogradle.subsystems.parchment.minecraftVersion` |
| Parchment Mappings | 2024.11.17 | `gradle.properties` → `neogradle.subsystems.parchment.mappingsVersion` |
| Foojay Resolver Convention | 1.0.0 | `settings.gradle` |
| Java toolchain | 21 | `build.gradle` → `java.toolchain.languageVersion` |

## Runtime dependencies declared in neoforge.mods.toml

| Dependency | Type | Version range |
|---|---|---|
| neoforge | required | `[21.1.230,)` |
| minecraft | required | `[1.21.1]` |

## Optional dependencies

None active. All optional dependency examples in `build.gradle` (JEI, local libs, sister projects) are commented out.

## Deferred

- MineColonies — not yet installed; deferred pending framework decisions.

## Rules

- Do not bump any pinned version without updating `docs/versions.md`.
- Do not add an optional dependency without verifying it does not introduce a server-side `Dist.CLIENT` reference.
- The Minecraft version and NeoForge version must remain compatible. Check the NeoForge project page before bumping either.
