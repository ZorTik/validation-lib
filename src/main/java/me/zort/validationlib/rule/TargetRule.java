package me.zort.validationlib.rule;

import lombok.AllArgsConstructor;
import me.zort.validationlib.Step;
import me.zort.validationlib.ValidationContext;
import me.zort.validationlib.exception.RuleValidationException;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AllArgsConstructor
public class TargetRule implements Rule {
    private final String path;
    private final List<Rule> rules;

    @Override
    public @Nullable String validate(Step step, ValidationContext context) {
        try {
            for (Rule rule : rules) {
                if (!context.validate(step.withDifferentPathRule(path, rule))) {
                    return String.format("Target %s condition failed", path);
                }
            }
        } catch (RuleValidationException e) {
            return String.format("Target %s failed rule: %s", path, e.getMessage());
        }

        return null;
    }
}
