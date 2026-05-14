package ru.yojo.codegen.meta;

import groovy.lang.Closure;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.Input;
import ru.yojo.codegen.YojoConfig;
import ru.yojo.codegen.domain.lombok.Accessors;
import ru.yojo.codegen.domain.lombok.BuilderProperties;
import ru.yojo.codegen.domain.lombok.EqualsAndHashCode;
import ru.yojo.codegen.domain.lombok.LombokProperties;

import javax.inject.Inject;

public class Configuration {
    @Input
    private String validationApi;           // "JAKARTA" or "JAVAX" — preferred way
    @Input
    private String springBootVersion;       // legacy fallback, used when validationApi is null
    @Input
    private Lombok lombok = new Lombok();

    // ✅ Multi-spec support
    private NamedDomainObjectContainer<SpecificationProperties> specificationProperties;

    @Inject
    public Configuration(ProjectLayout layout, ObjectFactory objects) {
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
    public String getValidationApi() {
        return validationApi;
    }

    public void setValidationApi(String validationApi) {
        this.validationApi = validationApi;
    }

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
        return toLombokProperties(this.lombok);
    }

    /**
     * Converts a plugin {@link Lombok} meta instance into a generator domain {@link LombokProperties}.
     * <p>
     * This method applies a <strong>full override</strong> strategy: every field is populated with
     * either the user-configured value or the plugin's default, producing a complete
     * {@code LombokProperties} that can be used independently of any global configuration.
     *
     * @param lombok the plugin Lombok meta instance (must not be {@code null})
     * @return fully-populated {@link LombokProperties}
     */
    public static LombokProperties toLombokProperties(Lombok lombok) {
        LombokProperties lombokProperties = new LombokProperties(
                lombok.isEnable(),
                lombok.isAllArgsConstructor(),
                lombok.getAccessors() != null
                        ? new Accessors(
                        lombok.getAccessors().isEnable(),
                        lombok.getAccessors().isFluent(),
                        lombok.getAccessors().isChain())
                        : new Accessors(false, false, false)
        );

        if (lombok.getEqualsAndHashCode() != null) {
            EqualsAndHashCode equalsAndHashCode = new EqualsAndHashCode();
            equalsAndHashCode.setEnable(lombok.getEqualsAndHashCode().isEnable());
            equalsAndHashCode.setCallSuper(lombok.getEqualsAndHashCode().getCallSuper());
            lombokProperties.setEqualsAndHashCode(equalsAndHashCode);
        }
        lombokProperties.setNoArgsConstructor(lombok.isNoArgsConstructor());

        if (lombok.getBuilder() != null) {
            lombokProperties.setBuilder(new BuilderProperties(
                    lombok.getBuilder().isEnable(),
                    lombok.getBuilder().isSingular(),
                    lombok.getBuilder().isBuilderDefault()
            ));
        }

        return lombokProperties;
    }

    public java.util.List<ru.yojo.codegen.context.SpecificationProperties> toSpecList() {
        return specificationProperties.stream().map(sp -> {
            ru.yojo.codegen.context.SpecificationProperties domain = new ru.yojo.codegen.context.SpecificationProperties();
            domain.setSpecName(sp.getSpecName());
            domain.setInputDirectory(sp.getInputDirectory());
            domain.setOutputDirectory(sp.getOutputDirectory());
            domain.setPackageLocation(sp.getPackageLocation());
            if (sp.getLombok() != null) {
                domain.setLombokProperties(toLombokProperties(sp.getLombok()));
            }
            return domain;
        }).collect(java.util.stream.Collectors.toList());
    }
}