package me.zort.validationlib.rule;

import me.zort.validationlib.Step;
import me.zort.validationlib.ValidationContext;
import me.zort.validationlib.condition.Condition;
import me.zort.validationlib.condition.ExistsCondition;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LongRule implements Rule {
    private static final LongRule INSTANCE = new LongRule();

    public static LongRule getInstance() {
        return INSTANCE;
    }

    @Override
    public @Nullable String validate(Step step, ValidationContext context) {
        if (!step.getConfig().isLong(step.getPath())) {
            return "Expected a long value.";
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
