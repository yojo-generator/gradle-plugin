package ru.yojo.codegen;

import groovy.lang.Closure;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import ru.yojo.codegen.meta.Configuration;

import javax.inject.Inject;

public class YojoConfig {
    private final String name;
    private final Configuration configuration;

    @Inject
    public YojoConfig(String name, ObjectFactory objects, ProjectLayout layout) {
        this.name = name;
        this.configuration = objects.newInstance(Configuration.class, layout);
    }

    // DSL: specificationProperties { api { ... } }
    @SuppressWarnings("unused")
    public void specificationProperties(Closure<?> closure) {
        YojoConfig.applyClosureToDelegate(closure, configuration.getSpecificationProperties());
    }

    // DSL: validationApi("JAKARTA") — preferred way to select javax vs jakarta
    @SuppressWarnings("unused")
    public void validationApi(String api) {
        configuration.setValidationApi(api);
    }

    // DSL: springBootVersion("3.2.0") — legacy fallback (used when validationApi is not set)
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