# Dragon Tweaks — Build and Test Commands

## Build commands

| Command | Purpose |
|---|---|
| `./gradlew build` | Compile and package the mod JAR |
| `./gradlew clean` | Clean build artifacts |
| `./gradlew --refresh-dependencies` | Force re-download dependencies |

## Run commands

| Command | Purpose |
|---|---|
| `./gradlew runClient` | Launch Minecraft client with the mod loaded |
| `./gradlew runServer` | Launch dedicated server with the mod loaded |
| `./gradlew runData` | Run data generators (resources land in `src/generated/resources/`) |
| `./gradlew runGameTestServer` | Run game tests |

## Test state

No test framework is currently set up. `./gradlew test` finds no tests. `testJunit` and `compileTestJava` report `NO-SOURCE`.

## Build output

- JAR lands in `build/libs/`
- Archive base name: `dragontweaksv2` (from `mod_id`)
- Generated resources: `src/generated/resources/` (included in source set automatically)

## Verified build

- `./gradlew build` passed 2026-05-25. See `docs/versions.md` for log.

## Gradle configuration

| Setting | Value |
|---|---|
| JVM args | `-Xmx1G` |
| Daemon | enabled |
| Parallel | enabled |
| Build cache | enabled |
| Configuration cache | enabled |
| Gradle version | 9.5.0 |

## CI

`.github/workflows/build.yml` runs `./gradlew build` on every push and pull request using Ubuntu + JDK 21 (Temurin).

## Rules

- Run `./gradlew build` and confirm it passes before treating any change as complete.
- Do not commit build artifacts (`build/`, `run/`).
- Do not skip the build step as a preflight shortcut.
