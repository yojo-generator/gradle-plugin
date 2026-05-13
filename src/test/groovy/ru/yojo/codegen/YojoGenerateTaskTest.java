package ru.yojo.codegen;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class YojoGenerateTaskTest {

    @Test
    void testSingleMainConfiguration() {
        Project project = ProjectBuilder.builder().build();
        var ext = project.getExtensions().create("yojo", YojoExtension.class);

        ext.getConfigurations().create("main", config -> {
            config.getConfiguration().getSpecificationProperties().create("test.yaml", sp -> {
                sp.setSpecName("test.yaml");
                sp.setInputDirectory("./src/test/resources/example/contract");
                sp.setOutputDirectory("./build/generated");
                sp.setPackageLocation("test.api");
            });
            config.getConfiguration().setSpringBootVersion("3.2.0");
            config.getConfiguration().getLombok().enable(true);
        });

        Task task = project.getTasks().create("generateClasses", YojoGenerateTask.class,
                ext.getConfigurations().getByName("main"),
                project.getLayout()
        );
        assertNotNull(task);
    }

    @Test
    void testAllConfigurationTaskNames() {
        assertEquals("generateClassesMain", YojoPlugin.deriveTaskName("main"));
        assertEquals("generateClassesMain", YojoPlugin.deriveTaskName("Main"));
        assertEquals("generateClassesEvents", YojoPlugin.deriveTaskName("events"));
        assertEquals("generateClassesApi", YojoPlugin.deriveTaskName("api"));
    }

    @Test
    void testSpecNameDefaultsToNull() {
        Project project = ProjectBuilder.builder().build();
        var ext = project.getExtensions().create("yojo", YojoExtension.class);

        ext.getConfigurations().create("main", config -> {
            config.getConfiguration().getSpecificationProperties().create("mySpec", sp -> {
                // specName intentionally not set — should be null, not fallback to "mySpec"
            });
        });

        var spec = ext.getConfigurations().getByName("main")
                .getConfiguration().getSpecificationProperties().getByName("mySpec");
        assertNull(spec.getSpecName(), "specName should be null when not explicitly set");
    }
}