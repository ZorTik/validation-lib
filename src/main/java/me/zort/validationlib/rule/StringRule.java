package me.zort.validationlib.rule;

import me.zort.validationlib.Step;
import me.zort.validationlib.ValidationContext;
import me.zort.validationlib.condition.Condition;
import me.zort.validationlib.condition.ExistsCondition;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StringRule implements Rule {
    private static final StringRule INSTANCE = new StringRule();

    public static StringRule getInstance() {
        return INSTANCE;
    }

    @Override
    public @Nullable String validate(Step step, ValidationContext context) {
        if (!step.getConfig().isString(step.getPath())) {
            return "Expected a string value.";
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
