package ru.yojo.codegen.meta

import org.junit.jupiter.api.Test
import ru.yojo.codegen.domain.lombok.Accessors
import ru.yojo.codegen.domain.lombok.BuilderProperties
import ru.yojo.codegen.domain.lombok.EqualsAndHashCode
import ru.yojo.codegen.domain.lombok.LombokProperties

import static org.junit.jupiter.api.Assertions.*

/**
 * Unit tests for {@link Configuration} Lombok conversion methods.
 * Tests the static {@link Configuration#toLombokProperties(Lombok)} method
 * which is the core conversion from plugin meta classes to generator domain model.
 */
class ConfigurationTest {

    @Test
    void 'should convert Lombok with builder DSL to LombokProperties'() {
        given:
        def lombok = new Lombok()
        lombok.enable(true)
        lombok.allArgsConstructor(true)
        lombok.builder {
            enable = true
            singular = true
            builderDefault = true
        }

        when:
        def props = Configuration.toLombokProperties(lombok)

        then:
        assertTrue(props.enableLombok())
        assertTrue(props.allArgsConstructor())
        assertTrue(props.noArgsConstructor()) // default is true
        assertNotNull(props.getBuilder())
        assertTrue(props.getBuilder().isEnable())
        assertTrue(props.getBuilder().isSingular())
        assertTrue(props.getBuilder().isBuilderDefault())
    }

    @Test
    void 'should convert Lombok with builder disabled'() {
        given:
        def lombok = new Lombok()
        lombok.enable(true)
        lombok.builder {
            enable = false
            singular = false
            builderDefault = false
        }

        when:
        def props = Configuration.toLombokProperties(lombok)

        then:
        assertNotNull(props.getBuilder())
        assertFalse(props.getBuilder().isEnable())
        assertFalse(props.getBuilder().isSingular())
        assertFalse(props.getBuilder().isBuilderDefault())
    }

    @Test
    void 'should return null builder when not configured'() {
        given:
        def lombok = new Lombok()
        lombok.enable(true)

        when:
        def props = Configuration.toLombokProperties(lombok)

        then:
        assertNull(props.getBuilder())
    }

    @Test
    void 'should convert accessors DSL to Accessors'() {
        given:
        def lombok = new Lombok()
        lombok.enable(true)
        lombok.accessors {
            fluent = true
            chain = false
        }

        when:
        def props = Configuration.toLombokProperties(lombok)

        then:
        assertNotNull(props.getAccessors())
        assertTrue(props.getAccessors().isFluent())
        assertFalse(props.getAccessors().isChain())
    }

    @Test
    void 'should convert equalsAndHashCode DSL to EqualsAndHashCode'() {
        given:
        def lombok = new Lombok()
        lombok.enable(true)
        lombok.equalsAndHashCode {
            callSuper = true
        }

        when:
        def props = Configuration.toLombokProperties(lombok)

        then:
        assertNotNull(props.getEqualsAndHashCode())
        assertEquals(Boolean.TRUE, props.getEqualsAndHashCode().getCallSuper())
        assertTrue(props.getEqualsAndHashCode().isEnable())
    }

    @Test
    void 'should convert all Lombok features together'() {
        given:
        def lombok = new Lombok()
        lombok.enable(true)
        lombok.allArgsConstructor(true)
        lombok.noArgsConstructor(false)
        lombok.accessors {
            enable = true
            fluent = true
            chain = true
        }
        lombok.equalsAndHashCode {
            enable = true
            callSuper = false
        }
        lombok.builder {
            enable = true
            singular = true
            builderDefault = false
        }

        when:
        def props = Configuration.toLombokProperties(lombok)

        then:
        assertTrue(props.enableLombok())
        assertTrue(props.allArgsConstructor())
        assertFalse(props.noArgsConstructor())

        assertNotNull(props.getAccessors())
        assertTrue(props.getAccessors().isEnable())
        assertTrue(props.getAccessors().isFluent())
        assertTrue(props.getAccessors().isChain())

        assertNotNull(props.getEqualsAndHashCode())
        assertTrue(props.getEqualsAndHashCode().isEnable())
        assertEquals(Boolean.FALSE, props.getEqualsAndHashCode().getCallSuper())

        assertNotNull(props.getBuilder())
        assertTrue(props.getBuilder().isEnable())
        assertTrue(props.getBuilder().isSingular())
        assertFalse(props.getBuilder().isBuilderDefault())
    }

    @Test
    void 'should convert new Lombok annotations v4_5_0 to LombokProperties'() {
        given:
        def lombok = new Lombok()
        lombok.enable(true)
        lombok.allArgsConstructor(true)
        lombok.value(true)
        lombok.with(true)
        lombok.getter(true)
        lombok.setter(true)
        lombok.toString(true)
        lombok.requiredArgsConstructor(true)
        lombok.slf4j(true)

        when:
        def props = Configuration.toLombokProperties(lombok)

        then:
        assertTrue(props.enableLombok())
        assertTrue(props.allArgsConstructor())
        assertTrue(props.isValue())
        assertTrue(props.isWith())
        assertTrue(props.isGetter())
        assertTrue(props.isSetter())
        assertTrue(props.isToString())
        assertTrue(props.isRequiredArgsConstructor())
        assertTrue(props.isSlf4j())
    }

    @Test
    void 'should default new Lombok annotations to false when not set'() {
        given:
        def lombok = new Lombok()
        lombok.enable(true)

        when:
        def props = Configuration.toLombokProperties(lombok)

        then:
        assertFalse(props.isValue())
        assertFalse(props.isWith())
        assertFalse(props.isGetter())
        assertFalse(props.isSetter())
        assertFalse(props.isToString())
        assertFalse(props.isRequiredArgsConstructor())
        assertFalse(props.isSlf4j())
    }
}
