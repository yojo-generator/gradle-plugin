package ru.yojo.codegen.meta;

import groovy.lang.Closure;
import org.gradle.api.tasks.Input;
import ru.yojo.codegen.YojoConfig;

public class Lombok {
    @Input private Boolean enable;
    @Input private Boolean allArgsConstructor;
    @Input private Boolean noArgsConstructor = true;
    @Input private Accessors accessors;
    @Input private EqualsAndHashCode equalsAndHashCode;

    // -- DSL: accessors { ... }
    @SuppressWarnings("unused")
    public void accessors(Closure<?> closure) {
        if (accessors == null) accessors = new Accessors();
        YojoConfig.applyClosureToDelegate(closure, accessors);
    }

    // -- DSL: equalsAndHashCode { ... }
    @SuppressWarnings("unused")
    public void equalsAndHashCode(Closure<?> closure) {
        if (equalsAndHashCode == null) equalsAndHashCode = new EqualsAndHashCode();
        YojoConfig.applyClosureToDelegate(closure, equalsAndHashCode);
    }

    // -- Getters/setters
    public Boolean isEnable() { return enable != null ? enable : true; }
    public void enable(Boolean enable) { this.enable = enable; }
    public Boolean isAllArgsConstructor() { return allArgsConstructor != null && allArgsConstructor; }
    public void allArgsConstructor(Boolean allArgsConstructor) { this.allArgsConstructor = allArgsConstructor; }
    public Boolean isNoArgsConstructor() { return noArgsConstructor != null && noArgsConstructor; }
    public void noArgsConstructor(Boolean noArgsConstructor) { this.noArgsConstructor = noArgsConstructor; }
    public Accessors getAccessors() { return accessors; }
    public void accessors(Accessors accessors) { this.accessors = accessors; }
    public EqualsAndHashCode getEqualsAndHashCode() { return equalsAndHashCode; }
    public void equalsAndHashCode(EqualsAndHashCode equalsAndHashCode) { this.equalsAndHashCode = equalsAndHashCode; }
}