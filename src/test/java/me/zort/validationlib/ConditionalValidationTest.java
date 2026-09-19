package me.zort.validationlib;

import me.zort.validationlib.exception.RuleValidationException;
import me.zort.validationlib.rule.Rule;
import org.bukkit.configuration.MemoryConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Conditional and Target Validation Tests")
class ConditionalValidationTest {

    private ConfigValidator validator;
    private MemoryConfiguration config;

    @BeforeEach
    void setUp() {
        validator = ConfigValidator.create();
        config = new MemoryConfiguration();
    }

    private Rule dependingOn(String targetPath, Object expectedValue, Rule... rules) {
        return Rule.onlyIf(Rule.target(targetPath, Rule.isEqual(expectedValue))).then(rules);
    }

    @Nested
    @DisplayName("Conditional Validation Behavior (onlyIf)")
    class OnlyIfTests {

        @Test
        @DisplayName("Should enforce rules when condition matches")
        void shouldEnforceRulesWhenConditionMatches() {
            config.set("redis.enabled", true);
            config.set("redis.host", "127.0.0.1");
            config.set("redis.port", 6379);

            Model model = Model.builder()
                    .path("redis.enabled", Rule.required(), Rule.bool())
                    .path("redis.host", dependingOn("redis.enabled", true, Rule.required(), Rule.string()))
                    .path("redis.port", dependingOn("redis.enabled", true, Rule.integer(), Rule.min(1), Rule.max(65535)))
                    .build();

            assertDoesNotThrow(() -> validator.validate(config, model));
        }

        @Test
        @DisplayName("Should fail dependent field when enabled condition matches but field is invalid")
        void shouldFailDependentFieldWhenConditionMatchesAndFieldInvalid() {
            config.set("redis.enabled", true);
            config.set("redis.port", -5); // Invalid port!

            Model model = Model.builder()
                    .path("redis.enabled", Rule.required(), Rule.bool())
                    .path("redis.port", dependingOn("redis.enabled", true, Rule.integer(), Rule.min(1)))
                    .build();

            RuleValidationException ex = assertThrows(
                    RuleValidationException.class,
                    () -> validator.validate(config, model)
            );

            assertEquals("redis.port", ex.getFailedStep().getPath());
            assertTrue(ex.getMessage().contains("Value must be at least 1"));
        }

        @Test
        @DisplayName("Should ignore dependent rules when condition is false")
        void shouldIgnoreDependentRulesWhenConditionIsFalse() {
            config.set("redis.enabled", false);
            // redis.host and redis.port are missing or invalid, but redis is disabled

            Model model = Model.builder()
                    .path("redis.enabled", Rule.required(), Rule.bool())
                    .path("redis.host", dependingOn("redis.enabled", true, Rule.required(), Rule.string()))
                    .path("redis.port", dependingOn("redis.enabled", true, Rule.required(), Rule.integer()))
                    .build();

            assertDoesNotThrow(() -> validator.validate(config, model));
        }

        @Test
        @DisplayName("Should ignore dependent rules when condition path does not exist")
        void shouldIgnoreDependentRulesWhenConditionPathIsMissing() {
            // redis.enabled is missing, so dependent rule condition fails
            Model model = Model.builder()
                    .path("redis.host", dependingOn("redis.enabled", true, Rule.required(), Rule.string()))
                    .build();

            assertDoesNotThrow(() -> validator.validate(config, model));
        }
    }

    @Nested
    @DisplayName("Target Rule Behavior")
    class TargetRuleTests {

        @Test
        @DisplayName("Should pass when target path satisfies rule")
        void shouldPassWhenTargetSatisfiesRule() {
            config.set("database.driver", "POSTGRES");

            Model model = Model.builder()
                    .path("database.pool", Rule.target("database.driver", Rule.required(), Rule.string()))
                    .build();

            assertDoesNotThrow(() -> validator.validate(config, model));
        }

        @Test
        @DisplayName("Should fail when target path does not satisfy rule")
        void shouldFailWhenTargetFailsRule() {
            config.set("database.driver", 12345); // Not a string

            Model model = Model.builder()
                    .path("database.pool", Rule.target("database.driver", Rule.string()))
                    .build();

            RuleValidationException ex = assertThrows(
                    RuleValidationException.class,
                    () -> validator.validate(config, model)
            );

            assertEquals("database.pool", ex.getFailedStep().getPath());
            assertTrue(ex.getMessage().contains("Target database.driver failed rule"));
        }
    }
}
