# Rule Pack Structure

## Principle

The framework is portable. Project-specific rules are isolated in rule packs and never mixed into portable framework docs.

## Directory Structure

```text
docs/framework/          ← portable, project-agnostic rules
docs/rule-packs/         ← project-specific rule packs
  └── <project-name>/
      ├── architecture.md
      ├── threading.md
      ├── dependencies.md
      └── build.md
```

## Portable Framework Contains

- Role boundaries
- Memory-first retrieval protocol
- Save-after-query protocol
- Defective query log format
- Query quality rules
- Safe shell policy
- Git maturity model
- Rule pack structure (this file)

## Rule Pack Contains

- Project-specific architecture invariants
- Project-specific threading rules
- Project-specific dependency constraints
- Project-specific build and test commands
- Project-specific docs and mappings

## Example Rule Pack: Dragon Tweaks

```text
docs/rule-packs/dragon-tweaks/
  ├── architecture.md     ← Minecraft/NeoForge architecture rules
  ├── threading.md        ← main thread / server thread constraints
  ├── dependencies.md     ← NeoForge, MineColonies version constraints
  └── build.md            ← Gradle build and test commands
```

## Rule

Portable framework docs must never contain project-specific rules. Project-specific rules must never be applied to other projects without explicit review and adaptation.
