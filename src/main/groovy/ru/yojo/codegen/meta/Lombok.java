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
    @Input private Builder builder;

    // -- New Lombok annotations (4.5.0)
    @Input private Boolean value;
    @Input private Boolean with;
    @Input private Boolean getter;
    @Input private Boolean setter;
    @Input private Boolean toString;
    @Input private Boolean requiredArgsConstructor;
    @Input private Boolean slf4j;

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

    // -- DSL: builder { ... }
    @SuppressWarnings("unused")
    public void builder(Closure<?> closure) {
        if (builder == null) builder = new Builder();
        YojoConfig.applyClosureToDelegate(closure, builder);
    }

    // -- Getters/setters (existing)
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
    public Builder getBuilder() { return builder; }
    public void builder(Builder builder) { this.builder = builder; }

    // -- Getters/setters (4.5.0)
    public Boolean isValue() { return value != null && value; }
    public void value(Boolean value) { this.value = value; }
    public Boolean isWith() { return with != null && with; }
    public void with(Boolean with) { this.with = with; }
    public Boolean isGetter() { return getter != null && getter; }
    public void getter(Boolean getter) { this.getter = getter; }
    public Boolean isSetter() { return setter != null && setter; }
    public void setter(Boolean setter) { this.setter = setter; }
    public Boolean isToString() { return toString != null && toString; }
    public void toString(Boolean toString) { this.toString = toString; }
    public Boolean isRequiredArgsConstructor() { return requiredArgsConstructor != null && requiredArgsConstructor; }
    public void requiredArgsConstructor(Boolean requiredArgsConstructor) { this.requiredArgsConstructor = requiredArgsConstructor; }
    public Boolean isSlf4j() { return slf4j != null && slf4j; }
    public void slf4j(Boolean slf4j) { this.slf4j = slf4j; }
}