package me.zort.simplevalidationlib.condition;

import lombok.AllArgsConstructor;
import me.zort.simplevalidationlib.Step;
import me.zort.simplevalidationlib.ValidationContext;
import me.zort.simplevalidationlib.exception.RuleValidationException;
import me.zort.simplevalidationlib.rule.Rule;

@AllArgsConstructor
public class PassesRuleCondition implements Condition {
    private final Rule rule;

    public static PassesRuleCondition of(Rule rule) {
        return new PassesRuleCondition(rule);
    }

    @Override
    public boolean passes(Step step, ValidationContext context) {
        try {
            return context.validate(rule, step, context);
        } catch (RuleValidationException e) {
            return false;
        }
    }
}
