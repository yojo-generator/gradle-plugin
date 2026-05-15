package ru.yojo.codegen.meta;

import org.gradle.api.tasks.Input;

/**
 * Groovy DSL meta class for the {@code builder { ... }} block inside {@code lombok}.
 * <p>
 * Corresponds to {@link ru.yojo.codegen.domain.lombok.BuilderProperties} in the generator domain model.
 *
 * <pre>{@code
 * lombok {
 *     builder {
 *         enable = true
 *         singular = true
 *         builderDefault = true
 *     }
 * }
 * }</pre>
 */
public class Builder {

    @Input
    private boolean enable;

    @Input
    private boolean singular = true;

    @Input
    private boolean builderDefault = true;

    public boolean isEnable() {
        return enable;
    }

    public void enable(boolean enable) {
        this.enable = enable;
    }

    public boolean isSingular() {
        return singular;
    }

    public void singular(boolean singular) {
        this.singular = singular;
    }

    public boolean isBuilderDefault() {
        return builderDefault;
    }

    public void builderDefault(boolean builderDefault) {
        this.builderDefault = builderDefault;
    }
}
