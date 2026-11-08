package ru.yojo.codegen;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies that the {@code dateTimeType} option of a named configuration reaches the generator.
 */
class DateTimeTypeOptionTest {

    @TempDir
    Path tempDir;

    @Test
    void defaultDateTypeIsOffsetDateTime() throws IOException {
        String event = generate(tempDir.resolve("default"), null);
        assertTrue(event.contains("import java.time.OffsetDateTime;"), event);
        assertTrue(event.contains("private OffsetDateTime createdAt;"), event);
        assertTrue(event.contains("List<OffsetDateTime> history;"), event);
    }

    @Test
    void instantIsUsedWhenConfigured() throws IOException {
        String event = generate(tempDir.resolve("instant"), "INSTANT");
        assertTrue(event.contains("import java.time.Instant;"), event);
        assertTrue(event.contains("private Instant createdAt;"), event);
        assertTrue(event.contains("List<Instant> history;"), event);
        assertFalse(event.contains("OffsetDateTime"), event);
    }

    @Test
    void optionIsCaseInsensitive() throws IOException {
        String event = generate(tempDir.resolve("lower-case"), "instant");
        assertTrue(event.contains("private Instant createdAt;"), event);
    }

    @Test
    void invalidValueIsRejected() {
        Project project = newProject(tempDir.resolve("invalid"), "UNKNOWN");
        Throwable error = assertThrows(IllegalArgumentException.class, () -> runTask(project));
        assertTrue(error.getMessage().contains("UNKNOWN"), error.getMessage());
    }

    private String generate(Path output, String dateTimeType) throws IOException {
        Project project = newProject(output, dateTimeType);
        runTask(project);

        try (Stream<Path> files = Files.walk(output)) {
            List<Path> generated = files
                    .filter(path -> path.getFileName().toString().equals("Event.java"))
                    .collect(Collectors.toList());
            assertEquals(1, generated.size(), "Event.java was not generated under " + output);
            return new String(Files.readAllBytes(generated.get(0)), StandardCharsets.UTF_8);
        }
    }

    private Project newProject(Path output, String dateTimeType) {
        Project project = ProjectBuilder.builder().build();
        var ext = project.getExtensions().create("yojo", YojoExtension.class);

        ext.getConfigurations().create("main", config -> {
            config.getConfiguration().getSpecificationProperties().create("date-time.yaml", sp -> {
                sp.setSpecName("date-time.yaml");
                sp.setInputDirectory("./src/test/resources/example/contract");
                sp.setOutputDirectory(output.toString());
                sp.setPackageLocation("test.datetime");
            });
            config.dateTimeType(dateTimeType);
        });
        return project;
    }

    private void runTask(Project project) {
        var ext = project.getExtensions().getByType(YojoExtension.class);
        Task task = project.getTasks().create("generateClasses", YojoGenerateTask.class,
                ext.getConfigurations().getByName("main"),
                project.getLayout()
        );
        ((YojoGenerateTask) task).generateClasses();
    }
}
