# Yojo Gradle Plugin

**Gradle wrapper for Yojo Generator**
https://github.com/yojo-generator/gradle-plugin

## What

Gradle plugin that generates Java DTOs from AsyncAPI specs at build time. Thin configuration layer — all code generation logic is in the [generator core](https://github.com/yojo-generator/generator).

## DSL reference

```groovy
yojo {
    configurations {
        create("main") {
            specificationProperties {
                register("api") {
                    specName = "order-service.yaml"
                    inputDirectory = file("contract").absolutePath
                    outputDirectory = layout.buildDirectory.dir("generated").get().asFile.absolutePath
                    packageLocation = "com.example.api"
                }
            }
            validationApi("JAKARTA")  // or "JAVAX"
            lombok {
                enable = true
                allArgsConstructor = false
                noArgsConstructor = true
                accessors { fluent = false; chain = true }
                equalsAndHashCode { enable = true; callSuper = false }
                builder { enable = true; singular = true; builderDefault = true }
                // 4.5.0+ : value, with, getter, setter, toString, requiredArgsConstructor, slf4j
            }
        }
    }
}
```

## Published
- **Gradle Plugin Portal:** `io.github.yojo-generator.gradle-plugin`
