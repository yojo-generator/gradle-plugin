# Yojo Gradle Plugin — AGENTS.md

**Generated:** 2026-05-15
**Stack:** Java 17, Gradle, Groovy, Spock/JUnit 5

## Overview

Gradle plugin wrapping [Yojo Generator](https://github.com/yojo-generator/generator) core. Reads AsyncAPI YAML specs and generates Java DTOs at build time. Thin layer translating Gradle DSL ↔ generator domain model.

## Project Structure

```
gradle-plugin/
├── src/main/groovy/ru/yojo/codegen/
│   ├── YojoPlugin.groovy            # Plugin entry point
│   ├── YojoGenerateTask.java         # Gradle task implementation
│   ├── meta/                         # DSL model classes
│   │   ├── Configuration.java        # DSL → generator model converter
│   │   ├── Lombok.java               # Lombok DSL model
│   │   └── EqualsAndHashCode.java    # @EqualsAndHashCode DSL
│   └── util/
├── src/test/groovy/                  # Spock/Groovy tests
├── build.gradle                      # Version: 1.5.0
├── settings.gradle
└── AGENTS.md                         # This file
```

## Key Classes

| Class | Path | Role |
|-------|------|------|
| `YojoPlugin` | `YojoPlugin.groovy` | Plugin registration, extension setup |
| `YojoGenerateTask` | `YojoGenerateTask.java` | Gradle task executing code generation |
| `Configuration` | `meta/Configuration.java` | DSL → generator `LombokProperties` conversion |

## Conventions

- **Code style:** Java 17 for main classes, Groovy for plugin registration
- **DSL:** Gradle Extension pattern with closures
- **Testing:** JUnit 5 (Java) + Spock (Groovy)
- **Versioning:** Semantic, in `build.gradle`

## Commands

```bash
./gradlew build          # Build plugin
./gradlew test           # Run tests
./gradlew publishToMavenLocal  # Local install for testing
```

## AI Notes

- Thin wrapper: ~95% of logic lives in generator core
- Plugin DSL mirrors generator's LombokProperties model
- Publishes to Gradle Plugin Portal
- JitPack.yml present for CI builds
