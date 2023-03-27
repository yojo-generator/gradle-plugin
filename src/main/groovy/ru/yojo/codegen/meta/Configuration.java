package ru.yojo.codegen.meta;

import groovy.lang.Closure;
import org.gradle.api.file.ProjectLayout;
import ru.yojo.codegen.YojoConfig;

import javax.inject.Inject;

public class Configuration {

    ProjectLayout layout;
    protected Boolean lombokEnabled;
    protected Boolean allArgsConstructor;
    protected Boolean accessors;
    protected Directories directories;

    @Inject
    public Configuration(ProjectLayout layout) {
        this.layout = layout;
    }

    @SuppressWarnings("unused")
    public void directories(Closure<?> closure) {
        // apply the given closure to the configuration bridge, i.e. its contained JAXB Configuration object
        YojoConfig.applyClosureToDelegate(closure, directories);
    }

    public Boolean getLombokEnabled() {
        return lombokEnabled;
    }

    public void setLombokEnabled(Boolean lombokEnabled) {
        this.lombokEnabled = lombokEnabled;
    }

    public Boolean getAllArgsConstructor() {
        return allArgsConstructor;
    }

    public void setAllArgsConstructor(Boolean allArgsConstructor) {
        this.allArgsConstructor = allArgsConstructor;
    }

    public Boolean getAccessors() {
        return accessors;
    }

    public void setAccessors(Boolean accessors) {
        this.accessors = accessors;
    }

    public Directories getDirectories() {
        return directories;
    }

    public void setDirectories(Directories directoriesProperty) {
        this.directories = directoriesProperty;
    }

    public Configuration withDirectories() {
        this.directories = new Directories(layout);
        return this;
    }

}