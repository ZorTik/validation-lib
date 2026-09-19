package me.zort.simplevalidationlib.rule;

import me.zort.simplevalidationlib.Step;
import me.zort.simplevalidationlib.ValidationContext;
import org.jetbrains.annotations.Nullable;

public final class RequiredRule implements Rule {
    private static final RequiredRule INSTANCE = new RequiredRule();

    private RequiredRule() {
    }

    public static RequiredRule getInstance() {
        return INSTANCE;
    }

    @Override
    public @Nullable String validate(Step step, ValidationContext context) {
        if (!step.getConfig().contains(step.getPath())) {
            return "Required value is missing.";
        }

        return null;
    }
}
