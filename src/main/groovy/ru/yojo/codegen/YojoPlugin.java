package ru.yojo.codegen;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

@SuppressWarnings("unused")
public class YojoPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        YojoExtension yojoExtension = project.getExtensions().create("yojo", YojoExtension.class);

        yojoExtension.getConfigurations().configureEach(config -> {
            String taskName = "generateClasses";
            TaskProvider<YojoGenerateTask> yojo = project.getTasks().register(taskName, YojoGenerateTask.class, config, project.getLayout());
            yojo.configure(task -> {
                task.setDescription(String.format("Generates the Yojo sources from the %s Yojo configuration.", config.getName()));
                task.setGroup("YOJO");
            });
        });
    }

    public static String capitalize(String s) {
        return s == null || s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
