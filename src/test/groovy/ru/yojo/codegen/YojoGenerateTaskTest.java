package ru.yojo.codegen;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class YojoGenerateTaskTest {

    @Test
    void testMultiSpecConfiguration() {
        Project project = ProjectBuilder.builder().build();
        YojoExtension ext = project.getExtensions().create("yojo", YojoExtension.class);

        ext.getConfigurations().create("main", config -> {
            config.getConfiguration().getSpecificationProperties().create("test.yaml", sp -> {
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
}