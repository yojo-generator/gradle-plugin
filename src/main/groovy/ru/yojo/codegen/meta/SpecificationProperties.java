package ru.yojo.codegen.meta;

import groovy.lang.Closure;
import org.gradle.api.tasks.Input;
import ru.yojo.codegen.YojoConfig;

import javax.inject.Inject;

public class SpecificationProperties {
    private final String name;

    @Input private String specName;
    @Input private String inputDirectory;
    @Input private String outputDirectory;
    @Input private String packageLocation;
    @Input private Lombok lombok;

    @Inject
    public SpecificationProperties(String name) {
        this.name = name;
    }

    // -- DSL: lombok { ... } (per-spec overrides)
    @SuppressWarnings("unused")
    public void lombok(Closure<?> closure) {
        if (lombok == null) lombok = new Lombok();
        YojoConfig.applyClosureToDelegate(closure, lombok);
    }

    public String getName() { return name; }
    public String getSpecName() { return specName; }

    public String getInputDirectory() { return inputDirectory; }
    public void setInputDirectory(String inputDirectory) { this.inputDirectory = inputDirectory; }

    public String getOutputDirectory() { return outputDirectory; }
    public void setOutputDirectory(String outputDirectory) { this.outputDirectory = outputDirectory; }

    public String getPackageLocation() { return packageLocation; }
    public void setPackageLocation(String packageLocation) { this.packageLocation = packageLocation; }

    public void setSpecName(String specName) { this.specName = specName; }

    public Lombok getLombok() { return lombok; }
    public void setLombok(Lombok lombok) { this.lombok = lombok; }
}