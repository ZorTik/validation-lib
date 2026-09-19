package me.zort.validationlib.rule;

import me.zort.validationlib.Step;
import me.zort.validationlib.ValidationContext;
import me.zort.validationlib.condition.Condition;
import me.zort.validationlib.condition.ExistsCondition;
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
