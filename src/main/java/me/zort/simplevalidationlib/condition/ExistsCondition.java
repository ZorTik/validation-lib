package me.zort.simplevalidationlib.condition;

import me.zort.simplevalidationlib.Step;
import me.zort.simplevalidationlib.ValidationContext;

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
