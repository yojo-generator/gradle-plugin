package ru.yojo.codegen

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

import static org.junit.jupiter.api.Assertions.*

class YojoIntegrationTest {

    private Path outputDir
    private File projectDir

    @BeforeEach
    void setup() {
        outputDir = Files.createTempDirectory("yojo-test-")
        projectDir = new File("src/test/resources/example")
    }

    @AfterEach
    void cleanup() {
        Files.walk(outputDir)
                .sorted { p1, p2 -> p2.compareTo(p1) }
                .forEach { it.toFile().delete() }
    }

    @Test
    void shouldGenerateJavaClassesFromTestYaml() throws Exception {
        // 1. Создаём Gradle проект и применяем плагин
        def project = ProjectBuilder.builder()
                .withProjectDir(projectDir)
                .withGradleUserHomeDir(outputDir.resolve("gradle-user-home").toFile())  // ← изолируем в tmp
                .build()

        project.pluginManager.apply(YojoPlugin)

        // 2. Настраиваем конфигурацию
        def yojoExt = project.extensions.getByType(YojoExtension)

        yojoExt.configurations.create("main") { config ->
            config.configuration.specificationProperties.create("test") { sp ->
                sp.specName = "test.yaml"
                sp.inputDirectory = projectDir.toPath().resolve("contract").toString()
                sp.outputDirectory = outputDir.toString()
                sp.packageLocation = "example.testGenerate"
            }

            config.configuration.springBootVersion = "3.2.0"
            config.configuration.lombok.enable = false
        }

        // 3. Получаем и **корректно выполняем** задачу
        def task = project.tasks.findByName("generateClasses")
        assertNotNull(task, "Task 'generateClasses' should be registered by YojoPlugin")

        // ✅ ПРАВИЛЬНО: вызов через actions
        task.actions.each { action ->
            action.execute(task)
        }

        // 4. Проверяем результат
        def messagesDir = outputDir.resolve("messages").toFile()
        def commonDir = outputDir.resolve("common").toFile()

        assertTrue(messagesDir.exists(), "messages/ dir must exist")
        assertTrue(commonDir.exists(), "common/ dir must exist")

        assertTrue(new File(messagesDir, "RequestDtoWithProperties.java").exists())
        assertTrue(new File(commonDir, "SomeObject.java").exists())
        assertTrue(new File(commonDir, "InnerEnumWithoutDescriptionSmall.java").exists())
        assertTrue(new File(commonDir, "CollectionTypes.java").exists())
    }
}