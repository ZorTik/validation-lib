package me.zort.validationlib.rule;

import me.zort.validationlib.Step;
import me.zort.validationlib.ValidationContext;
import me.zort.validationlib.condition.Condition;
import me.zort.validationlib.condition.ExistsCondition;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DoubleRule implements Rule {
    private static final DoubleRule INSTANCE = new DoubleRule();

    public static DoubleRule getInstance() {
        return INSTANCE;
    }

    @Override
    public @Nullable String validate(Step step, ValidationContext context) {
        if (!step.getConfig().isDouble(step.getPath())) {
            return "Expected a double value.";
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
