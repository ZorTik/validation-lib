package me.zort.validationlib.condition;

import lombok.AllArgsConstructor;
import me.zort.validationlib.Step;
import me.zort.validationlib.ValidationContext;
import me.zort.validationlib.exception.RuleValidationException;
import me.zort.validationlib.rule.Rule;

@AllArgsConstructor
public class PassesRuleCondition implements Condition {
    private final Rule rule;

    public static PassesRuleCondition of(Rule rule) {
        return new PassesRuleCondition(rule);
    }

    @Override
    public boolean passes(Step step, ValidationContext context) {
        try {
            return context.validate(step.withDifferentRule(rule));
        } catch (RuleValidationException e) {
            return false;
        }
    }
}
