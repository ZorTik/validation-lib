package me.zort.validationlib;

import me.zort.validationlib.exception.RuleValidationException;
import me.zort.validationlib.rule.Rule;
import org.bukkit.configuration.MemoryConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Value Bounds and Comparison Validation Tests")
class ValueBoundsValidationTest {

    private ConfigValidator validator;
    private MemoryConfiguration config;

    @BeforeEach
    void setUp() {
        validator = ConfigValidator.create();
        config = new MemoryConfiguration();
    }

    @Nested
    @DisplayName("Number Rule Behavior")
    class NumberRuleTests {

        @Test
        @DisplayName("Should pass for int, double, or long types")
        void shouldPassForAnyNumericType() {
            config.set("num.int", 42);
            config.set("num.double", 3.14);
            config.set("num.long", 100_000_000_000L);

            Model model = Model.builder()
                    .path("num.int", Rule.number())
                    .path("num.double", Rule.number())
                    .path("num.long", Rule.number())
                    .build();

            assertDoesNotThrow(() -> validator.validate(config, model));
        }

        @Test
        @DisplayName("Should fail when value is not a number")
        void shouldFailForNonNumericType() {
            config.set("num.val", "forty-two");
            Model model = Model.builder()
                    .path("num.val", Rule.number())
                    .build();

            RuleValidationException ex = assertThrows(
                    RuleValidationException.class,
                    () -> validator.validate(config, model)
            );

            assertEquals("num.val", ex.getFailedStep().getPath());
            assertTrue(ex.getMessage().contains("Expected a number value"));
        }
    }

    @Nested
    @DisplayName("Min and Max Range Rule Behavior")
    class RangeRuleTests {

        @ParameterizedTest(name = "Port {0} should be valid between 1024 and 65535")
        @ValueSource(ints = {1024, 8080, 65535})
        void shouldPassWhenValueIsWithinRange(int port) {
            config.set("server.port", port);
            Model model = Model.builder()
                    .path("server.port", Rule.required(), Rule.integer(), Rule.min(1024), Rule.max(65535))
                    .build();

            assertDoesNotThrow(() -> validator.validate(config, model));
        }

        @Test
        @DisplayName("Should fail when value is smaller than minimum")
        void shouldFailWhenValueIsBelowMin() {
            config.set("server.port", 80);
            Model model = Model.builder()
                    .path("server.port", Rule.min(1024))
                    .build();

            RuleValidationException ex = assertThrows(
                    RuleValidationException.class,
                    () -> validator.validate(config, model)
            );

            assertEquals("server.port", ex.getFailedStep().getPath());
            assertTrue(ex.getMessage().contains("Value must be at least 1024"));
        }

        @Test
        @DisplayName("Should fail when value is larger than maximum")
        void shouldFailWhenValueIsAboveMax() {
            config.set("server.port", 70000);
            Model model = Model.builder()
                    .path("server.port", Rule.max(65535))
                    .build();

            RuleValidationException ex = assertThrows(
                    RuleValidationException.class,
                    () -> validator.validate(config, model)
            );

            assertEquals("server.port", ex.getFailedStep().getPath());
            assertTrue(ex.getMessage().contains("Value must be at most 65535"));
        }

        @Test
        @DisplayName("Should support double precision in min/max rules")
        void shouldSupportDoubleRange() {
            config.set("tax.rate", 0.05);
            Model model = Model.builder()
                    .path("tax.rate", Rule.min(0.01), Rule.max(0.20))
                    .build();

            assertDoesNotThrow(() -> validator.validate(config, model));

            config.set("tax.rate", 0.25);
            RuleValidationException ex = assertThrows(
                    RuleValidationException.class,
                    () -> validator.validate(config, model)
            );
            assertTrue(ex.getMessage().contains("Value must be at most 0.2"));
        }
    }

    @Nested
    @DisplayName("Equals Rule Behavior")
    class EqualsRuleTests {

        @Test
        @DisplayName("Should pass when config value matches expected object")
        void shouldPassWhenValueMatchesExpected() {
            config.set("version", "1.0.0");
            config.set("cluster.size", 3);

            Model model = Model.builder()
                    .path("version", Rule.isEqual("1.0.0"))
                    .path("cluster.size", Rule.isEqual(3))
                    .build();

            assertDoesNotThrow(() -> validator.validate(config, model));
        }

        @Test
        @DisplayName("Should fail when config value differs from expected")
        void shouldFailWhenValueDiffersFromExpected() {
            config.set("version", "2.0.0");
            Model model = Model.builder()
                    .path("version", Rule.isEqual("1.0.0"))
                    .build();

            RuleValidationException ex = assertThrows(
                    RuleValidationException.class,
                    () -> validator.validate(config, model)
            );

            assertEquals("version", ex.getFailedStep().getPath());
            assertTrue(ex.getMessage().contains("Expected value: 1.0.0, but found: 2.0.0"));
        }
    }
}
