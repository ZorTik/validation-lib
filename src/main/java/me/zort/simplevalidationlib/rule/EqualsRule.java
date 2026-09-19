package me.zort.simplevalidationlib.rule;

import lombok.AllArgsConstructor;
import me.zort.simplevalidationlib.Step;
import me.zort.simplevalidationlib.ValidationContext;
import me.zort.simplevalidationlib.condition.Condition;
import me.zort.simplevalidationlib.condition.ExistsCondition;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AllArgsConstructor
public class EqualsRule implements Rule {
    private final Object expectedValue;

    public static EqualsRule of(Object expectedValue) {
        return new EqualsRule(expectedValue);
    }

    @Override
    public @Nullable String validate(Step step, ValidationContext context) {
        Object actualValue = step.getConfig().get(step.getPath());
        if (!expectedValue.equals(actualValue)) {
            return "Expected value: " + expectedValue + ", but found: " + actualValue;
        }

        return null;
    }

    @Override
    public Iterable<Condition> getConditions() {
        return List.of(
                ExistsCondition.getInstance()
        );
    }
}
