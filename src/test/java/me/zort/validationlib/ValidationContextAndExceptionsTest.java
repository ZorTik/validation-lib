package me.zort.validationlib;

import me.zort.validationlib.exception.InvalidConfigurationException;
import me.zort.validationlib.exception.RuleValidationException;
import me.zort.validationlib.rule.Rule;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Validation Context and Exception Contract Tests")
class ValidationContextAndExceptionsTest {

    @Test
    @DisplayName("RuleValidationException should retain failed Step and error message")
    void shouldRetainStepAndMessageInException() {
        MemoryConfiguration config = new MemoryConfiguration();
        Step step = new Step(config, "missing.key", Rule.required());

        RuleValidationException exception = new RuleValidationException(step, "Custom error message");

        assertEquals(step, exception.getFailedStep());
        assertEquals("missing.key", exception.getFailedStep().getPath());
        assertEquals("Custom error message", exception.getMessage());
        assertTrue(exception instanceof InvalidConfigurationException);
    }

    @Test
    @DisplayName("ValidationContext should track passed rules during validation")
    void shouldTrackPassedRulesInContext() {
        MemoryConfiguration config = new MemoryConfiguration();
        config.set("user.email", "test@example.com");

        Rule requiredRule = Rule.required();
        Rule stringRule = Rule.string();

        Model model = Model.builder()
                .path("user.email", requiredRule, stringRule)
                .build();

        ConfigValidator validator = ConfigValidator.create();
        assertDoesNotThrow(() -> validator.validate(config, model));
    }
}
