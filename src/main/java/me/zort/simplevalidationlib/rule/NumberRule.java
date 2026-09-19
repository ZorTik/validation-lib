package me.zort.simplevalidationlib.rule;

import me.zort.simplevalidationlib.Step;
import me.zort.simplevalidationlib.ValidationContext;
import me.zort.simplevalidationlib.condition.Condition;
import me.zort.simplevalidationlib.condition.ExistsCondition;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NumberRule implements Rule {
    private static final NumberRule INSTANCE = new NumberRule();

    public static NumberRule getInstance() {
        return INSTANCE;
    }

    @Override
    public @Nullable String validate(Step step, ValidationContext context) {
        if (!step.getConfig().isInt(step.getPath())
                && !step.getConfig().isDouble(step.getPath())
                && !step.getConfig().isLong(step.getPath())) {
            return "Expected a number value.";
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
