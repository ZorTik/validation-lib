package me.zort.simplevalidationlib;

import me.zort.simplevalidationlib.condition.Condition;
import me.zort.simplevalidationlib.rule.Rule;
import org.bukkit.configuration.MemoryConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Condition Composition Tests")
class ConditionTest {

    private Step step;
    private ValidationContext context;

    @BeforeEach
    void setUp() {
        MemoryConfiguration config = new MemoryConfiguration();
        config.set("foo", "bar");
        step = new Step(config, "foo", Rule.string());
        context = new ValidationContext((rule, s, ctx) -> true);
    }

    @Test
    @DisplayName("Condition.all should pass only when all sub-conditions pass")
    void shouldPassAllOnlyWhenAllSubConditionsPass() {
        Condition pass1 = (s, c) -> true;
        Condition pass2 = (s, c) -> true;
        Condition fail = (s, c) -> false;

        assertTrue(Condition.all(pass1, pass2).passes(step, context));
        assertFalse(Condition.all(pass1, fail).passes(step, context));
    }

    @Test
    @DisplayName("Condition.any should pass when at least one sub-condition passes")
    void shouldPassAnyWhenAtLeastOneSubConditionPasses() {
        Condition pass = (s, c) -> true;
        Condition fail1 = (s, c) -> false;
        Condition fail2 = (s, c) -> false;

        assertTrue(Condition.any(fail1, pass, fail2).passes(step, context));
        assertFalse(Condition.any(fail1, fail2).passes(step, context));
    }
}
