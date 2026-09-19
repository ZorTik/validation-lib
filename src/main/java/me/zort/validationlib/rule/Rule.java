package me.zort.validationlib.rule;

import me.zort.validationlib.Step;
import me.zort.validationlib.ValidationContext;
import me.zort.validationlib.condition.Condition;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Represents a validation rule.
 *
 * @author ZorTik
 */
public interface Rule {

    /**
     * Validates the value at the given step in the configuration.
     *
     * @param step the current step in the validation process
     * @param context the validation context
     * @return a string describing the validation error, or null if the value is valid
     */
    @Nullable
    String validate(Step step, ValidationContext context);

    /**
     * Returns the rules that should be applied to the value being validated.
     * These rules will be applied BEFORE the current rule is applied.
     *
     * @return an iterable of rules
     */
    default Iterable<? extends Rule> getRules() {
        return Collections.emptyList();
    }

    /**
     * Returns the conditions that must be satisfied for this rule to be applied.
     * If any of the conditions are not satisfied, the rule will not be applied.
     *
     * @return an iterable of conditions
     */
    default Iterable<? extends Condition> getConditions() {
        return Collections.emptyList();
    }

    static Rule required() {
        return RequiredRule.getInstance();
    }

    static Rule string() {
        return StringRule.getInstance();
    }

    static Rule integer() {
        return IntegerRule.getInstance();
    }

    static Rule bool() {
        return BooleanRule.getInstance();
    }

    static Rule doubleValue() {
        return DoubleRule.getInstance();
    }

    static Rule longValue() {
        return LongRule.getInstance();
    }

    static Rule list() {
        return ListRule.getInstance();
    }

    static Rule section() {
        return SectionRule.getInstance();
    }

    static Rule number() {
        return NumberRule.getInstance();
    }

    static Rule max(double max) {
        return new MaxRule(max);
    }

    static Rule min(double min) {
        return new MinRule(min);
    }

    static Rule isEqual(Object value) {
        return new EqualsRule(value);
    }

    static Rule target(String targetPath, Rule... rules) {
        return new TargetRule(targetPath, List.of(rules));
    }

    static IfRule.IfRuleBuilder onlyIf(Rule... conditionRules) {
        return new IfRule.IfRuleBuilder(conditionRules);
    }
}
