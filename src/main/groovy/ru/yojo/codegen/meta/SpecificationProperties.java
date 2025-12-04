package ru.yojo.codegen.meta;

import org.gradle.api.tasks.Input;
import javax.inject.Inject;

public class SpecificationProperties {
    private final String name;

    @Input private String specName;
    @Input private String inputDirectory;
    @Input private String outputDirectory;
    @Input private String packageLocation;

    @Inject
    public SpecificationProperties(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public String getSpecName() { return specName != null ? specName : name; }

    // геттеры/сеттеры для остального
    public String getInputDirectory() { return inputDirectory; }
    public void setInputDirectory(String inputDirectory) { this.inputDirectory = inputDirectory; }

    public String getOutputDirectory() { return outputDirectory; }
    public void setOutputDirectory(String outputDirectory) { this.outputDirectory = outputDirectory; }

    public String getPackageLocation() { return packageLocation; }
    public void setPackageLocation(String packageLocation) { this.packageLocation = packageLocation; }

    public void setSpecName(String specName) { this.specName = specName; }
}