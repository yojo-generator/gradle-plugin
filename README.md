# YOJO Gradle Plugin

**Gradle plugin for generating Java DTOs from AsyncAPI YAML specs** — code generation at build time. Parse AsyncAPI v2.0/v2.6/v3.0 contracts and produce Java POJOs with Lombok, Jackson, Jakarta Bean Validation, and Builder pattern support.

✅ Multi-spec · ✅ AsyncAPI v2.6/v3.0 · ✅ Lombok (@Data, @Builder, @Value) · ✅ Jackson polymorphism (@JsonTypeInfo) · ✅ Bean Validation (@NotBlank, @Email) · ✅ Enums with descriptions · ✅ Manual Builder class

![Yojo Banner](./yojo.png)

[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v?label=Gradle%20Plugin&metadataUrl=https%3A%2F%2Fplugins.gradle.org%2Fm2%2Fio%2Fgithub%2Fyojo-generator%2Fgradle-plugin%2Fio.github.yojo-generator.gradle-plugin.gradle.plugin%2Fmaven-metadata.xml)](https://plugins.gradle.org/plugin/io.github.yojo-generator.gradle-plugin)
[![JDK 17+](https://img.shields.io/badge/JDK-17%2B-green.svg)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE.md)
[![AsyncAPI 2.0+](https://img.shields.io/badge/AsyncAPI-2.0%2F2.6%2F3.0-blue)](https://www.asyncapi.com)
[![Status: Active](https://img.shields.io/badge/status-active-success)](https://github.com/yojo-generator/gradle-plugin)

---

- [Quick Start](#quick-start)
- [How It Works](#how-it-works)
- [Plugin DSL Reference](#plugin-dsl-reference)
- [Generator Features (YAML)](#generator-features-yaml)
- [Type Reference](#type-reference)
- [Roadmap](#roadmap)

---

## Quick Start

### 1. Apply the plugin

```groovy
// build.gradle
plugins {
    id 'io.github.yojo-generator.gradle-plugin' version '1.4.0'
```

```kotlin
// build.gradle.kts
plugins {
    id("io.github.yojo-generator.gradle-plugin") version "1.4.0"
}
```

### 2. Configure

```groovy
// build.gradle
yojo {
    configurations {
        create("main") {
            specificationProperties {
                register("api") {
                    specName = "order-service.yaml"
                    inputDirectory = layout.projectDirectory.dir("contract").asFile.absolutePath
                    outputDirectory = layout.buildDirectory.dir("generated/sources/yojo/com/example/api").get().asFile.absolutePath
                    packageLocation = "com.example.api"
                }
            }
        }
    }
}

sourceSets {
    main.java.srcDir(layout.buildDirectory.dir("generated/sources/yojo"))
}

tasks.compileJava {
    dependsOn("generateClasses")
}
```

### 3. Add an AsyncAPI contract

```yaml
# contract/order-service.yaml
asyncapi: 2.6.0
info:
  title: Order Service
  version: 1.0.0
channels:
  OrderCreated:
    subscribe:
      message:
        payload:
          $ref: '#/components/schemas/Order'
components:
  schemas:
    Order:
      type: object
      properties:
        id:
          type: string
        quantity:
          type: integer
          minimum: 1
        email:
          type: string
          format: email
```

### 4. Generate

```bash
./gradlew generateClasses
```

Output:

```
build/generated/sources/yojo/com/example/api/
├── common/
│   └── Order.java
└── messages/
    └── OrderCreatedMessage.java
```

Generated `Order.java`:

```java
@Generated("Yojo")
public class Order {

    private String id;

    @Min(1)
    private Integer quantity;

    @Email
    private String email;
}
```

---

## How It Works

The plugin is a thin Gradle wrapper around the [Yojo generator](https://github.com/yojo-generator/generator) (core library).

```
 ┌─────────────────────────────────────────────────────┐
 │                    build.gradle                      │
 │  yojo { configurations { ... } }                     │
 └──────────────────────┬──────────────────────────────┘
                        │
                        ▼
 ┌─────────────────────────────────────────────────────┐
 │              Yojo Gradle Plugin                      │
 │  - Reads DSL configuration                           │
 │  - Creates named tasks (generateClasses, ...)        │
 │  - Passes config to the generator                    │
 └──────────────────────┬──────────────────────────────┘
                        │
                        ▼
 ┌─────────────────────────────────────────────────────┐
 │              Yojo Generator (core)                   │
 │  - Parses AsyncAPI YAML                              │
 │  - Resolves $refs, discriminator, polymorphism       │
 │  - Resolves Java types, adds validation annotations  │
 │  - Generates .java files                             │
 └──────────────────────┬──────────────────────────────┘
                        │
                        ▼
 ┌─────────────────────────────────────────────────────┐
 │        Generated Java DTOs                           │
 │  Order.java, Status.java, OrderCreatedMessage.java   │
 └─────────────────────────────────────────────────────┘
```

**Key principle:** The plugin handles _where_ to read specs and _where_ to write output. The generator handles _what_ Java code to produce, driven entirely by your AsyncAPI YAML contract.

---

## Plugin DSL Reference

Everything below is configured in `build.gradle` / `build.gradle.kts` inside the `yojo { }` block.

### Task naming

Each configuration registers a named task `generateClasses<Name>`:

| Config name | Config task | Lifecycle task |
|---|---|---|
| `"main"` | `generateClassesMain` | `generateClasses` |
| `"events"` | `generateClassesEvents` | `generateClasses` |
| `"internal"` | `generateClassesInternal` | `generateClasses` |

The **lifecycle task** `generateClasses` depends on all config tasks automatically.
You only need `dependsOn("generateClasses")` — it always works, regardless of how
many configurations you have:

### `yojo { configurations { ... } }`

```groovy
yojo {
    configurations {
        create("main") {          // ← named configuration, becomes task name
            specificationProperties { ... }   // required: at least one spec
            validationApi("JAKARTA")          // optional: "JAKARTA" or "JAVAX"
            // springBootVersion("3.2.0")    // legacy fallback
            lombok { ... }                    // optional: Lombok settings
        }
    }
}
```

### `specificationProperties { register("id") { ... } }`

Each spec registration maps one YAML file to one output tree. Register multiple specs to generate from several contracts.

| Field | Required | Type | Description | Example |
|---|---|---|---|---|
| `specName` | ✅ | `String` | YAML filename | `"order-service.yaml"` |
| `inputDirectory` | ✅ | `String` | Absolute path to spec directory | `layout.projectDirectory.dir("contract").asFile.absolutePath` |
| `outputDirectory` | ✅ | `String` | Absolute output root | `layout.buildDirectory.dir("...").get().asFile.absolutePath` |
| `packageLocation` | ✅ | `String` | Base Java package | `"com.example.api"` |

Generated package structure:

```
<outputDirectory>/com/example/api/
├── common/      ← DTOs, enums, interfaces from components.schemas
└── messages/    ← message payloads from channels
```

### `validationApi`

Preferred way to select the validation annotation namespace:

| Value | API | Example annotation |
|---|---|---|
| `"JAKARTA"` | `jakarta.validation.*` | `jakarta.validation.constraints.NotNull` |
| `"JAVAX"` | `javax.validation.*` | `javax.validation.constraints.NotNull` |
| omitted | `jakarta.validation.*` | (defaults to Jakarta) |

### `springBootVersion` (legacy)

Fallback when `validationApi` is not set. Uses version-based heuristic:

| Value | API | Example annotation |
|---|---|---|
| `"3.0.0"` or higher | `jakarta.validation.*` | `jakarta.validation.constraints.NotNull` |
| `"2.7.x"` or lower | `javax.validation.*` | `javax.validation.constraints.NotNull` |
| omitted | `jakarta.validation.*` | (defaults to Jakarta) |

> `validationApi` takes precedence. Only use `springBootVersion` if you need the version-based heuristic.

### `lombok { ... }`

| Option | Type | Default | Description |
|---|---|---|---|
| `enable` | `boolean` | `true` | Adds `@Data` to generated classes |
| `allArgsConstructor` | `boolean` | `false` | Adds `@AllArgsConstructor` |
| `noArgsConstructor` | `boolean` | `true` | Adds `@NoArgsConstructor` |
| `accessors { ... }` | closure | — | `@Accessors` configuration |
| `equalsAndHashCode { ... }` | closure | — | `@EqualsAndHashCode` configuration |
| `builder { ... }` | closure | — | `@Builder` / manual Builder configuration |

#### `accessors { ... }`

| Option | Type | Default | Effect |
|---|---|---|---|
| `enable` | `boolean` | `false` | Generate `@Accessors` |
| `fluent` | `boolean` | `false` | `obj.field(val)` instead of `obj.setField(val)` |
| `chain` | `boolean` | `false` | Setters return `this` |

#### `equalsAndHashCode { ... }`

| Option | Type | Default | Effect |
|---|---|---|---|
| `enable` | `boolean` | `false` | Generate `@EqualsAndHashCode` |
| `callSuper` | `boolean` | — | `callSuper = true` if set |

#### `builder { ... }`

| Option | Type | Default | Effect |
|---|---|---|---|
| `enable` | `boolean` | `false` | Generate `@Builder` (Lombok) or manual Builder class (no Lombok) |
| `singular` | `boolean` | `true` | Add `@Singular` / singular adder methods for `List`/`Set` fields |
| `builderDefault` | `boolean` | `true` | Apply `@Builder.Default` / propagate default values to Builder |

> The singular name is derived automatically by removing the trailing `s` from the field name
> (e.g., `names` → `"name"`). When Lombok is disabled, a full static inner `Builder` class
> with fluent setters, singular adders, and `build()` method is generated instead. |

### Complete DSL example

```groovy
yojo {
    configurations {
        create("main") {
            specificationProperties {
                register("api") {
                    specName = "order-service.yaml"
                    inputDirectory = layout.projectDirectory.dir("contract").asFile.absolutePath
                    outputDirectory = layout.buildDirectory
                        .dir("generated/sources/yojo/com/example/api").get().asFile.absolutePath
                    packageLocation = "com.example.api"
                }
                register("events") {
                    specName = "events.yaml"
                    inputDirectory = layout.projectDirectory.dir("contract").asFile.absolutePath
                    outputDirectory = layout.buildDirectory
                        .dir("generated/sources/yojo/io/example/events").get().asFile.absolutePath
                    packageLocation = "io.example.events"
                }
            }
            validationApi("JAKARTA")
            lombok {
                enable = true
                allArgsConstructor = false
                noArgsConstructor = true
                accessors {
                    enable = true
                    fluent = false
                    chain = true
                }
                equalsAndHashCode {
                    enable = true
                    callSuper = false
                }
                builder {
                    enable = true
                    singular = true
                    builderDefault = true
                }
            }
        }
    }
}

sourceSets {
    main.java.srcDir(layout.buildDirectory.dir("generated/sources/yojo"))
}

tasks.compileJava {
    dependsOn("generateClasses")
}
```

```kotlin
// build.gradle.kts
yojo {
    configurations {
        create("main") {
            specificationProperties {
                register("api") {
                    specName.set("order-service.yaml")
                    inputDirectory.set(layout.projectDirectory.dir("contract").asFile.absolutePath)
                    outputDirectory.set(layout.buildDirectory
                        .dir("generated/sources/yojo/com/example/api").get().asFile.absolutePath)
                    packageLocation.set("com.example.api")
                }
            }
            validationApi.set("JAKARTA")
            lombok {
                enable.set(true)
                allArgsConstructor.set(false)
                noArgsConstructor.set(true)
                accessors {
                    enable.set(true)
                    fluent.set(false)
                    chain.set(true)
                }
                equalsAndHashCode {
                    enable.set(true)
                    callSuper.set(false)
                }
                builder {
                    enable.set(true)
                    singular.set(true)
                    builderDefault.set(true)
                }
            }
        }
    }
}

sourceSets {
    main {
        java {
            srcDir(layout.buildDirectory.dir("generated/sources/yojo"))
        }
    }
}

tasks.compileJava {
    dependsOn("generateClasses")
}
```

---

## Generator Features (YAML)

All features below are controlled through your **AsyncAPI YAML contract**, not through the Gradle DSL. The plugin passes the YAML to the generator, which reads these attributes and produces the corresponding Java code.

### 1. Fields and Types

```yaml
properties:
  simpleString:
    type: string                                         # → String
  uuidField:
    type: string
    format: uuid                                         # → UUID
  dateField:
    type: string
    format: date                                         # → LocalDate
  dateTimeField:
    type: string
    format: date-time                                    # → OffsetDateTime
  count:
    type: integer                                        # → Integer
  bigPrime:
    type: integer
    format: int64                                        # → Long
  ratio:
    type: number
    format: big-decimal                                  # → BigDecimal
  isActive:
    type: boolean                                        # → Boolean
  primitiveInt:
    type: integer
    primitive: true                                      # → int (not Integer)
```

### 2. Required fields and validation

```yaml
properties:
  email:
    type: string
    format: email                                        # → @Email
    required: [email]                                    # → @NotBlank on strings
  age:
    type: integer
    minimum: 0
    maximum: 150                                         # → @Min(0) @Max(150)
  description:
    type: string
    minLength: 10
    maxLength: 1000                                      # → @Size(min=10, max=1000)
  code:
    type: string
    pattern: "^[A-Z]{3}-\\d{4}$"                         # → @Pattern(regexp="...")
  price:
    type: number
    format: big-decimal
    multipleOf: 0.01                                     # → @Digits(integer=1, fraction=2)
```

### 3. Collections

```yaml
properties:
  tags:
    type: array
    items:
      type: string                                       # → List<String>
      x-realization: ArrayList                           # → = new ArrayList<>()
  uniqueIds:
    type: array
    format: set
    items:
      type: integer                                      # → Set<Integer>
      x-realization: HashSet                             # → = new HashSet<>()
  matrix:
    type: array
    items:
      type: array
      items:
        type: number                                     # → List<List<BigDecimal>>
```

### 4. Maps

```yaml
properties:
  metadata:
    type: object
    additionalProperties:
      type: string                                       # → Map<String, String>
  config:
    type: object
    format: uuid
    additionalProperties:
      type: integer                                      # → Map<UUID, Integer>
  nested:
    type: object
    additionalProperties:
      type: array
      format: set
      items:
        type: string                                     # → Map<String, Set<String>>
```

### 5. Enums

Enums can be defined in two ways:

**A. Standalone schema enum** — generates a top-level Java enum class:

```yaml
components:
  schemas:
    Status:
      type: object
      enum: [PENDING, CONFIRMED, SHIPPED]
```
```java
public enum Status { PENDING, CONFIRMED, SHIPPED }
```

**B. Property-level enum** — generates an inner enum class (named `ParentProperty`):

```yaml
properties:
  status:
    type: string
    enum: [PENDING, CONFIRMED, SHIPPED]    # → OrderStatus enum class
```

---

**Enum with descriptions** (`x-enumNames`):

```yaml
Result:
  type: object
  enum: [SUCCESS, FAILURE]
  x-enumNames:
    SUCCESS: "Operation completed successfully"
    FAILURE: "Operation failed"
```
```java
public enum Result {
    SUCCESS("Operation completed successfully"),
    FAILURE("Operation failed");
    // + getValue()
}
```

**Enum with wire values** (`x-enumValues` + optional `x-enumDefault`):

```yaml
OrderStatus:
  type: object
  enum: [PENDING, CONFIRMED, CANCELLED]
  x-enumValues:
    PENDING: "P"
    CONFIRMED: "C"
    CANCELLED: "X"
  x-enumDefault: true          # adds UNKNOWN_DEFAULT_YOJO fallback
```
```java
public enum OrderStatus {
    PENDING("P"),
    CONFIRMED("C"),
    CANCELLED("X"),
    UNKNOWN_DEFAULT_YOJO("UNKNOWN");

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static OrderStatus fromValue(String value) {
        for (OrderStatus v : values()) {
            if (v.value.equals(value)) return v;
        }
        return UNKNOWN_DEFAULT_YOJO;  // instead of throwing
    }
}
```

### 6. Polymorphism (`oneOf` / `allOf`)

```yaml
# Property-level: all schemas merged into one class
polymorph:
  oneOf:
    - $ref: '#/components/schemas/StatusOnly'
    - $ref: '#/components/schemas/StatusWithCode'         # → PolymorphStatusOnlyStatusWithCode

# Schema-level: merged with root properties taking priority
MySchema:
  allOf:
    - $ref: '#/components/schemas/Base'
    - type: object
      properties:
        extraField:
          type: string
```

### 7. Discriminator (Jackson `@JsonTypeInfo` / `@JsonSubTypes`)

```yaml
Pet:
  type: object
  discriminator: petType
  properties:
    name:
      type: string
    petType:
      type: string

Cat:
  allOf:
    - $ref: '#/components/schemas/Pet'
  properties:
    huntingSkill:
      type: string

# Custom discriminator value via const
StickInsect:
  allOf:
    - $ref: '#/components/schemas/Pet'
    - type: object
      properties:
        petType:
          const: StickBug    # → @JsonSubTypes.Type(value = StickInsect.class, name = "StickBug")
        color:
          type: string
```

Generated Java:

```java
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
              property = "petType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Cat.class, name = "Cat"),
    @JsonSubTypes.Type(value = StickInsect.class, name = "StickBug")
})
public class Pet { ... }
public class Cat extends Pet { ... }
public class StickInsect extends Pet { ... }
```

### 8. Final fields (`x-final`)

```yaml
ImmutableDto:
  type: object
  properties:
    createdAt:
      type: string
      format: date-time
      x-final: true              # → private final OffsetDateTime createdAt;
    name:
      type: string               # → private String name; (regular mutable field)
```

- Fields with `x-final: true` are declared `final` and have **no setter**
- A constructor is generated for all final fields
- `@NoArgsConstructor` is automatically skipped when uninitialized finals exist
- If `default` is also set: `private final Type field = defaultValue;`

### 9. Builder pattern (`@Builder` / manual Builder)

The builder pattern is controlled via Gradle DSL `lombok { builder { ... } }` or per-schema via `x-lombok`:

```yaml
MyDto:
  type: object
  x-lombok:
    builder:
      enable: true
      singular: true
      builderDefault: true
  properties:
    names:
      type: array
      items:
        type: string                                      # @Singular("name")
    scores:
      type: array
      format: set
      items:
        type: integer                                     # @Singular("score")
    message:
      type: string
      default: Hello                                      # @Builder.Default
    count:
      type: integer                                       # regular field
```

**With Lombok:** generates `@Builder`, `@Singular`, `@Builder.Default` annotations.

**Without Lombok:** generates a full static inner `Builder` class with fluent setters, singular adders (e.g., `name()`, `score()`), and `build()` method.

### 10. Inheritance and interfaces

```yaml
# Extend a superclass
UserDto:
  type: object
  x-extends:
    x-from-class: BaseEntity
    x-from-package: com.example.base
  properties:
    name:
      type: string               # → public class UserDto extends BaseEntity { ... }

# Implement interfaces
Validatable:
  type: object
  x-implements:
    x-from-interface:
      - com.example.api.Validatable
      - java.io.Serializable     # → public class X implements Validatable, Serializable { ... }

# Define an interface (no implementation)
MyService:
  type: object
  format: interface
  x-methods:
    doWork:
      description: "Performs work"
      x-definition: "void doWork();"
  x-imports:
    - com.example.Domain        # → import com.example.Domain;
```

### 11. Custom annotations

```yaml
# Class-level annotations
MySchema:
  type: object
  x-class-annotation:
    - com.example.MyAnnotation
    - com.example.WithParam("value")
  properties: ...

# Field-level annotations
MySchema:
  type: object
  properties:
    myField:
      type: string
      x-field-annotation:
        - com.example.MyFieldAnnotation
```

### 12. Existing types (reference external classes)

```yaml
properties:
  author:
    type: object
    format: existing
    name: User
    package: com.example.domain    # → private User author; + import com.example.domain.User;
```

### 13. Validation groups

```yaml
MySchema:
  type: object
  x-validation-groups: [Create.class, Update.class]
  x-validation-groups-imports:
    - com.example.validation.Create
    - com.example.validation.Update
  x-validate-by-groups: [email, name]
  properties:
    email:
      type: string                 # → @NotBlank(groups = {Create.class, Update.class})
    name:
      type: string                 # → @NotBlank(groups = {Create.class, Update.class})
    age:
      type: integer                # → no group annotation (not in validate-by list)
```

### 14. Messages

```yaml
channels:
  OrderCreated:
    subscribe:
      message:
        payload:
          $ref: '#/components/schemas/Order'

  # Inline payload
  UserRegistered:
    subscribe:
      message:
        payload:
          properties:
            userId:
              type: string

  # Polymorphic message
  Notification:
    subscribe:
      message:
        payload:
          oneOf:
            - $ref: '#/components/schemas/EmailNotification'
            - $ref: '#/components/schemas/SmsNotification'

  # Custom message package
  CustomEvent:
    subscribe:
      message:
        payload:
          pathForGenerateMessage: "io.github.events"
          $ref: '#/components/schemas/EventPayload'
```

### 15. Attribute deprecation notice

Legacy attribute names (without `x-` prefix) still work but log deprecation warnings:

| Legacy (deprecated) | New (preferred) |
|---|---|
| `realization` | `x-realization` |
| `digits` | `x-digits` |
| `additionalFormat` | `x-additional-format` |
| `validationGroups` | `x-validation-groups` |
| `validationGroupsImports` | `x-validation-groups-imports` |
| `validateByGroups` | `x-validate-by-groups` |
| `extends` / `fromClass` / `fromPackage` | `x-extends` / `x-from-class` / `x-from-package` |
| `implements` / `fromInterface` | `x-implements` / `x-from-interface` |
| `lombok` | `x-lombok` (per-schema override) |
| `methods` / `definition` | `x-methods` / `x-definition` |
| `imports` | `x-imports` |
| `removeSchema` | `x-remove-schema` |
| `pathForGenerateMessage` | `x-path-for-generate-message` |

---

## Type Reference

### YAML → Java type mapping

| YAML | Java | Import |
|---|---|---|
| `type: string` | `String` | — |
| `type: string, format: uuid` / `type: object, format: uuid` | `UUID` | `java.util.UUID` |
| `type: string, format: date` / `type: object, format: date` | `LocalDate` | `java.time.LocalDate` |
| `type: string, format: date-time` / `type: object, format: date-time` | `OffsetDateTime` | `java.time.OffsetDateTime` |
| `type: string, format: local-date-time` | `LocalDateTime` | `java.time.LocalDateTime` |
| `type: string, format: simple-date` | `Date` | `java.util.Date` |
| `type: string, format: email` | `String` + `@Email` | `jakarta.validation.constraints.Email` |
| `type: string, format: uri` | `URI` | `java.net.URI` |
| `type: integer` / `format: int32` | `Integer` | — |
| `type: integer, format: int64` | `Long` | — |
| `type: number, format: float` | `Float` | — |
| `type: number, format: double` | `Double` | — |
| `type: number` (bare) | `BigDecimal` | `java.math.BigDecimal` |
| `type: number, format: big-decimal` | `BigDecimal` | `java.math.BigDecimal` |
| `type: number, format: big-integer` | `BigInteger` | `java.math.BigInteger` |
| `type: boolean` | `Boolean` | — |
| `type: array` | `List<T>` | `java.util.List` |
| `type: array, format: set` | `Set<T>` | `java.util.Set` |
| `type: object, additionalProperties` | `Map<K, V>` | `java.util.Map` |
| `type: object, properties: {...}` | Custom DTO class | generated |

### Validation annotations

| YAML condition | Annotation |
|---|---|
| `required` + string | `@NotBlank` |
| `required` + collection | `@NotEmpty` |
| `required` + other | `@NotNull` |
| `pattern: "..."` | `@Pattern(regexp = "...")` |
| `minLength` / `maxLength` | `@Size(min = X, max = Y)` |
| `minimum` / `maximum` | `@Min(X)` / `@Max(Y)` |
| `multipleOf: 0.01` | `@Digits(integer = I, fraction = F)` |
| `format: email` | `@Email` |
| nested object (non-collection) | `@Valid` |
| not required, no validation | `@Nullable` (configurable type) |

### AsyncAPI version support

| Version | Status | Notes |
|---|---|---|
| AsyncAPI 2.0 / 2.6 | ✅ Full | All features, `channels` → `publish`/`subscribe` → `message` → `payload` |
| AsyncAPI 3.0 (RC) | ✅ Experimental | `operations` → `channel` → `messages` → `payload { schema { ... } }` |
| OpenAPI 3.x | ❌ Not supported | Planned no earlier than 2026 |

---

## Roadmap

| Feature | Status |
|---|---|
| `x-` prefixed attributes (deprecation of legacy names) | ✅ Done (core 4.3.0) |
| `x-final` immutable fields | ✅ Done (core 4.3.0) |
| `@JsonTypeId` on discriminator fields | ✅ Done (core 4.3.0) |
| Jackson annotations (`@JsonProperty`, `@JsonFormat`) | 🔄 In development |
| AsyncAPI spec validation (pre-generation) | 🔄 In development |
| Lombok extensions (`@Builder`, `@Singular`, `@Builder.Default`) | ✅ Done (generator 4.4.0) |
| Gradle configuration cache support | 📋 Planned |
| OpenAPI 3.1 support | 📋 Planned Q2 2026 |

---

## Contact

- **Author**: Vladimir Morozkin
- **Email**: `jvmorozkin@gmail.com`
- **Telegram**: [@vmorozkin](https://t.me/vmorozkin)
- **GitHub**: [yojo-generator/gradle-plugin](https://github.com/yojo-generator/gradle-plugin)
- **Core library**: [yojo-generator/generator](https://github.com/yojo-generator/generator)

> PRs, issues, and feedback are welcome. Send your AsyncAPI spec and I will help you integrate.

---

## License

Distributed under the **Apache License 2.0**. See [LICENSE.md](LICENSE.md).
