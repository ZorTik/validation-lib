package me.zort.validationlib.condition;

import me.zort.validationlib.Step;
import me.zort.validationlib.ValidationContext;

public class ExistsCondition implements Condition {
    private static final ExistsCondition INSTANCE = new ExistsCondition();

    public static ExistsCondition getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean passes(Step step, ValidationContext context) {
        return step.getConfig().contains(step.getPath());
    }
}
