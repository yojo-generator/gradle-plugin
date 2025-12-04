package ru.yojo.codegen.meta;

import groovy.lang.Closure;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.Input;
import ru.yojo.codegen.YojoConfig;
import ru.yojo.codegen.domain.lombok.Accessors;
import ru.yojo.codegen.domain.lombok.EqualsAndHashCode;
import ru.yojo.codegen.domain.lombok.LombokProperties;

import javax.inject.Inject;

public class Configuration {
    private ProjectLayout layout;

    @Input
    private String springBootVersion;
    @Input
    private Lombok lombok = new Lombok();

    // ✅ Multi-spec support
    private NamedDomainObjectContainer<SpecificationProperties> specificationProperties;

    @Inject
    public Configuration(ProjectLayout layout, ObjectFactory objects) {
        this.layout = layout;
        this.specificationProperties = objects.domainObjectContainer(
                SpecificationProperties.class,
                name -> objects.newInstance(SpecificationProperties.class, name)
        );
    }

    // -- DSL: lombok { ... }
    @SuppressWarnings("unused")
    public void lombok(Closure<?> closure) {
        YojoConfig.applyClosureToDelegate(closure, lombok);
    }

    // -- DSL: specificationProperties { api { ... } }
    @SuppressWarnings("unused")
    public void specificationProperties(Closure<?> closure) {
        YojoConfig.applyClosureToDelegate(closure, specificationProperties);
    }

    // -- Getters
    public String getSpringBootVersion() {
        return springBootVersion;
    }

    public void setSpringBootVersion(String springBootVersion) {
        this.springBootVersion = springBootVersion;
    }

    public Lombok getLombok() {
        return lombok;
    }

    public void setLombok(Lombok lombok) {
        this.lombok = lombok;
    }

    public NamedDomainObjectContainer<SpecificationProperties> getSpecificationProperties() {
        return specificationProperties;
    }

    // -- Вспомогательные методы для преобразования в domain-модель Yojo
    public ru.yojo.codegen.domain.lombok.LombokProperties toLombokProperties() {
        LombokProperties lombokProperties = new LombokProperties(
                lombok.isEnable(),
                lombok.isAllArgsConstructor(),
                lombok.getAccessors() != null
                        ? new Accessors(
                        lombok.getAccessors().isEnable(),
                        lombok.getAccessors().isFluent(),
                        lombok.getAccessors().isChain())
                        : null
        );

        if (lombok.getEqualsAndHashCode() != null) {
            EqualsAndHashCode equalsAndHashCode = new EqualsAndHashCode();
            equalsAndHashCode.setEnable(lombok.getEqualsAndHashCode().isEnable());
            equalsAndHashCode.setCallSuper(lombok.getEqualsAndHashCode().getCallSuper());
            lombokProperties.setEqualsAndHashCode(equalsAndHashCode);
        }
        lombokProperties.setNoArgsConstructor(lombok.isNoArgsConstructor());
        return lombokProperties;
    }

    public java.util.List<ru.yojo.codegen.context.SpecificationProperties> toSpecList() {
        return specificationProperties.stream().map(sp -> {
            ru.yojo.codegen.context.SpecificationProperties domain = new ru.yojo.codegen.context.SpecificationProperties();
            domain.setSpecName(sp.getSpecName());
            domain.setInputDirectory(sp.getInputDirectory());
            domain.setOutputDirectory(sp.getOutputDirectory());
            domain.setPackageLocation(sp.getPackageLocation());
            return domain;
        }).collect(java.util.stream.Collectors.toList());
    }
}