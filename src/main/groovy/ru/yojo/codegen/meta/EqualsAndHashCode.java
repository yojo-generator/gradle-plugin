package ru.yojo.codegen.meta;

import org.gradle.api.tasks.Input;

public class EqualsAndHashCode {
    @Input private Boolean enable = false;
    @Input private Boolean callSuper;

    public Boolean isEnable() { return enable; }
    public void enable(Boolean enable) { this.enable = enable; }
    public Boolean getCallSuper() { return callSuper; }
    public void callSuper(Boolean callSuper) { this.callSuper = callSuper; }
}