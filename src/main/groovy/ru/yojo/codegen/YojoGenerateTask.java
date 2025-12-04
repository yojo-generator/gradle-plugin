package ru.yojo.codegen;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.TaskAction;
import ru.yojo.codegen.context.YojoContext;
import ru.yojo.codegen.generator.YojoGenerator;
import ru.yojo.codegen.meta.Configuration;

import javax.inject.Inject;
import java.io.IOException;

@CacheableTask
public abstract class YojoGenerateTask extends DefaultTask {

    private final Configuration config;
    private final ProjectLayout layout;

    @Inject
    public YojoGenerateTask(YojoConfig yojoConfig, ProjectLayout layout) {
        this.config = yojoConfig.getConfiguration();
        this.layout = layout;
    }

    @TaskAction
    public void generateClasses() {
        System.out.println("▶ Starting Yojo code generation...");

        if (config.getSpecificationProperties().isEmpty()) {
            throw new IllegalArgumentException("At least one specification must be defined in 'specificationProperties { ... }'");
        }

        YojoContext context = new YojoContext();
        context.setSpringBootVersion(config.getSpringBootVersion());
        context.setLombokProperties(config.toLombokProperties());
        context.setSpecificationProperties(config.toSpecList());

        try {
            new YojoGenerator().generateAll(context);
            System.out.println("✅ Yojo generation completed successfully.");
        } catch (IOException e) {
            throw new RuntimeException("Yojo generation failed", e);
        }
    }
}