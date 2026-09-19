package me.zort.simplevalidationlib.rule;

import me.zort.simplevalidationlib.Step;
import me.zort.simplevalidationlib.ValidationContext;
import me.zort.simplevalidationlib.condition.Condition;
import me.zort.simplevalidationlib.condition.ExistsCondition;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SectionRule implements Rule {
    private static final SectionRule INSTANCE = new SectionRule();

    public static SectionRule getInstance() {
        return INSTANCE;
    }

    @Override
    public @Nullable String validate(Step step, ValidationContext context) {
        if (!step.getConfig().isConfigurationSection(step.getPath())) {
            return "Expected a configuration section.";
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
