package ru.yojo.codegen;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskProvider;

/**
 * Gradle plugin that generates Java DTOs from AsyncAPI specifications using Yojo.
 * <p>
 * Each named configuration registers its own task {@code generateClasses<Name>}
 * (e.g. {@code generateClassesMain}, {@code generateClassesEvents}).
 * A lifecycle task {@code generateClasses} aggregates all of them, so a single
 * {@code dependsOn("generateClasses")} always works regardless of config count.
 * <p>
 * Usage:
 * <pre>
 * yojo {
 *     configurations {
 *         create("main") {
 *             specificationProperties {
 *                 register("api") { ... }
 *             }
 *         }
 *     }
 * }
 * </pre>
 */
@SuppressWarnings("unused")
public class YojoPlugin implements Plugin<Project> {

    static final String LIFECYCLE_TASK_NAME = "generateClasses";
    static final String TASK_GROUP = "YOJO";
    static final String EXTENSION_NAME = "yojo";

    @Override
    public void apply(Project project) {
        YojoExtension yojoExtension = project.getExtensions().create(EXTENSION_NAME, YojoExtension.class);

        // Register lifecycle task that aggregates all config-specific tasks.
        // Users wire dependsOn("generateClasses") once regardless of config count.
        TaskProvider<Task> lifecycleTask = project.getTasks().register(LIFECYCLE_TASK_NAME, task -> {
            task.setDescription("Generates Yojo sources from all configured specifications.");
            task.setGroup(TASK_GROUP);
        });

        yojoExtension.getConfigurations().configureEach(config -> {
            String taskName = deriveTaskName(config.getName());
            TaskProvider<YojoGenerateTask> configTask = project.getTasks().register(
                    taskName,
                    YojoGenerateTask.class,
                    config,
                    project.getLayout()
            );
            configTask.configure(task -> {
                task.setDescription("Generates Yojo sources from the '%s' configuration."
                        .formatted(config.getName()));
                task.setGroup(TASK_GROUP);
            });

            // Wire each config task into the lifecycle
            lifecycleTask.configure(task -> task.dependsOn(configTask));
        });
    }

    /**
     * Derives a deterministic task name from the configuration name.
     * <ul>
     *   <li>{@code "main"} → {@code "generateClassesMain"}</li>
     *   <li>{@code "events"} → {@code "generateClassesEvents"}</li>
     * </ul>
     */
    static String deriveTaskName(String configName) {
        return LIFECYCLE_TASK_NAME + capitalize(configName);
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
