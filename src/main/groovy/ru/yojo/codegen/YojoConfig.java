package ru.yojo.codegen;

import groovy.lang.Closure;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import ru.yojo.codegen.meta.Configuration;

import javax.inject.Inject;
import java.util.ArrayList;

public class YojoConfig {
    private final String name;
    private final ProjectLayout layout;
    private final Configuration configuration;

    @Inject
    public YojoConfig(String name, ProviderFactory providers, ObjectFactory objects, ProjectLayout layout) {
        this.name = name;
        this.layout = layout;
        this.configuration = objects.newInstance(Configuration.class, layout);
        // Инициализация defaults
        this.configuration.getSpecificationProperties().addAll(new ArrayList<>());
    }

    // DSL: specificationProperties { api { ... } }
    @SuppressWarnings("unused")
    public void specificationProperties(Closure<?> closure) {
        YojoConfig.applyClosureToDelegate(closure, configuration.getSpecificationProperties());
    }

    // DSL: springBootVersion = "3.2.0", lombok { ... }, etc.
    @SuppressWarnings("unused")
    public void springBootVersion(String version) {
        configuration.setSpringBootVersion(version);
    }

    @SuppressWarnings("unused")
    public void lombok(Closure<?> closure) {
        YojoConfig.applyClosureToDelegate(closure, configuration.getLombok());
    }

    // Геттеры
    public String getName() {
        return name;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public static void applyClosureToDelegate(Closure<?> closure, Object delegate) {
        Closure<?> copy = (Closure<?>) closure.clone();
        copy.setResolveStrategy(Closure.DELEGATE_FIRST);
        copy.setDelegate(delegate);
        if (copy.getMaximumNumberOfParameters() == 0) {
            copy.call();
        } else {
            copy.call(delegate);
        }
    }
}