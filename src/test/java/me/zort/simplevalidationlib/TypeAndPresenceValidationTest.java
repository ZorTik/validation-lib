package me.zort.simplevalidationlib;

import me.zort.simplevalidationlib.exception.RuleValidationException;
import me.zort.simplevalidationlib.rule.Rule;
import org.bukkit.configuration.MemoryConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Type and Presence Validation Tests")
class TypeAndPresenceValidationTest {

    private ConfigValidator validator;
    private MemoryConfiguration config;

    @BeforeEach
    void setUp() {
        validator = ConfigValidator.create();
        config = new MemoryConfiguration();
    }

    @Nested
    @DisplayName("Required Rule Behavior")
    class RequiredRuleTests {

        @Test
        @DisplayName("Should pass when required path exists in configuration")
        void shouldPassWhenRequiredPathExists() {
            config.set("server.name", "MyServer");
            Model model = Model.builder()
                    .path("server.name", Rule.required())
                    .build();

            assertDoesNotThrow(() -> validator.validate(config, model));
        }

        @Test
        @DisplayName("Should throw RuleValidationException when required path is missing")
        void shouldFailWhenRequiredPathIsMissing() {
            Model model = Model.builder()
                    .path("server.name", Rule.required())
                    .build();

            RuleValidationException exception = assertThrows(
                    RuleValidationException.class,
                    () -> validator.validate(config, model)
            );

            assertEquals("server.name", exception.getFailedStep().getPath());
            assertTrue(exception.getMessage().contains("Required value is missing"));
        }
    }

    @Nested
    @DisplayName("String Rule Behavior")
    class StringRuleTests {

        @Test
        @DisplayName("Should pass when value is a String")
        void shouldPassForStringValue() {
            config.set("app.title", "SimpleApp");
            Model model = Model.builder()
                    .path("app.title", Rule.string())
                    .build();

            assertDoesNotThrow(() -> validator.validate(config, model));
        }

        @Test
        @DisplayName("Should skip validation if optional string path does not exist")
        void shouldPassForMissingOptionalString() {
            Model model = Model.builder()
                    .path("app.title", Rule.string())
                    .build();

            assertDoesNotThrow(() -> validator.validate(config, model));
        }

        @Test
        @DisplayName("Should throw exception when value is not a String")
        void shouldFailForNonStringValue() {
            config.set("app.title", 12345);
            Model model = Model.builder()
                    .path("app.title", Rule.string())
                    .build();

            RuleValidationException ex = assertThrows(
                    RuleValidationException.class,
                    () -> validator.validate(config, model)
            );

            assertEquals("app.title", ex.getFailedStep().getPath());
            assertTrue(ex.getMessage().contains("Expected a string value"));
        }
    }

    @Nested
    @DisplayName("Integer and Int Rule Behavior")
    class IntegerRuleTests {

        @Test
        @DisplayName("Should pass when value is an integer")
        void shouldPassForIntValue() {
            config.set("server.port", 8080);
            Model model = Model.builder()
                    .path("server.port", Rule.integer())
                    .build();

            assertDoesNotThrow(() -> validator.validate(config, model));
        }

        @Test
        @DisplayName("Should throw exception when string is passed for integer path")
        void shouldFailForStringValueInIntPath() {
            config.set("server.port", "not-an-int");
            Model model = Model.builder()
                    .path("server.port", Rule.integer())
                    .build();

            RuleValidationException ex = assertThrows(
                    RuleValidationException.class,
                    () -> validator.validate(config, model)
            );

            assertEquals("server.port", ex.getFailedStep().getPath());
            assertTrue(ex.getMessage().contains("Expected an integer value"));
        }
    }

    @Nested
    @DisplayName("Double and Long Rule Behavior")
    class FloatingAndLongRuleTests {

        @Test
        @DisplayName("Should pass when value is double")
        void shouldPassForDoubleValue() {
            config.set("rates.multiplier", 1.5);
            Model model = Model.builder()
                    .path("rates.multiplier", Rule.doubleValue())
                    .build();

            assertDoesNotThrow(() -> validator.validate(config, model));
        }

        @Test
        @DisplayName("Should pass when value is long")
        void shouldPassForLongValue() {
            config.set("limits.max_bytes", 10_000_000_000L);
            Model model = Model.builder()
                    .path("limits.max_bytes", Rule.longValue())
                    .build();

            assertDoesNotThrow(() -> validator.validate(config, model));
        }
    }

    @Nested
    @DisplayName("Boolean Rule Behavior")
    class BooleanRuleTests {

        @Test
        @DisplayName("Should pass when value is boolean")
        void shouldPassForBoolean() {
            config.set("features.enabled", true);
            Model model = Model.builder()
                    .path("features.enabled", Rule.bool())
                    .build();

            assertDoesNotThrow(() -> validator.validate(config, model));
        }

        @Test
        @DisplayName("Should throw exception when value is non-boolean")
        void shouldFailForNonBoolean() {
            config.set("features.enabled", "yes");
            Model model = Model.builder()
                    .path("features.enabled", Rule.bool())
                    .build();

            RuleValidationException ex = assertThrows(
                    RuleValidationException.class,
                    () -> validator.validate(config, model)
            );

            assertEquals("features.enabled", ex.getFailedStep().getPath());
            assertTrue(ex.getMessage().contains("Expected a boolean value"));
        }
    }

    @Nested
    @DisplayName("List and Section Rule Behavior")
    class StructureRuleTests {

        @Test
        @DisplayName("Should pass for valid list of items")
        void shouldPassForList() {
            config.set("allowed-ips", List.of("127.0.0.1", "10.0.0.1"));
            Model model = Model.builder()
                    .path("allowed-ips", Rule.list())
                    .build();

            assertDoesNotThrow(() -> validator.validate(config, model));
        }

        @Test
        @DisplayName("Should pass when target path is a configuration section")
        void shouldPassForSection() {
            config.createSection("database");
            config.set("database.host", "localhost");

            Model model = Model.builder()
                    .path("database", Rule.section())
                    .build();

            assertDoesNotThrow(() -> validator.validate(config, model));
        }

        @Test
        @DisplayName("Should fail when non-section is validated as section")
        void shouldFailForNonSection() {
            config.set("database", "just-a-string");

            Model model = Model.builder()
                    .path("database", Rule.section())
                    .build();

            RuleValidationException ex = assertThrows(
                    RuleValidationException.class,
                    () -> validator.validate(config, model)
            );

            assertEquals("database", ex.getFailedStep().getPath());
            assertTrue(ex.getMessage().contains("Expected a configuration section"));
        }
    }
}
