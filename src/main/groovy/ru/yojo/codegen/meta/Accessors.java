package ru.yojo.codegen.meta;

import org.gradle.api.tasks.Input;

public class Accessors {

    @Input
    protected boolean enable;
    @Input
    protected boolean fluent;
    @Input
    protected boolean chain;

    public boolean isEnable() {
        return enable;
    }

    public void enable(boolean enable) {
        this.enable = enable;
    }

    public boolean isFluent() {
        return fluent;
    }

    public void fluent(boolean fluent) {
        this.fluent = fluent;
    }

    public boolean isChain() {
        return chain;
    }

    public void chain(boolean chain) {
        this.chain = chain;
    }
}