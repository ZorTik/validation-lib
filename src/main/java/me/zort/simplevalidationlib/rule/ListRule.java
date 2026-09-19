package me.zort.simplevalidationlib.rule;

import me.zort.simplevalidationlib.Step;
import me.zort.simplevalidationlib.ValidationContext;
import me.zort.simplevalidationlib.condition.Condition;
import me.zort.simplevalidationlib.condition.ExistsCondition;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ListRule implements Rule {
    private static final ListRule INSTANCE = new ListRule();

    public static ListRule getInstance() {
        return INSTANCE;
    }

    @Override
    public @Nullable String validate(Step step, ValidationContext context) {
        if (!step.getConfig().isList(step.getPath())) {
            return "Expected a list value.";
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
