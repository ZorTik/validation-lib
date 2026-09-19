package me.zort.validationlib.rule;

import lombok.AllArgsConstructor;
import me.zort.validationlib.Step;
import me.zort.validationlib.ValidationContext;
import me.zort.validationlib.condition.Condition;
import me.zort.validationlib.condition.PassesRuleCondition;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AllArgsConstructor
public class IfRule implements Rule {
    private final List<Rule> conditionRules;
    private final List<Rule> thenRules;

    public static final class IfRuleBuilder {
        private final List<Rule> conditionRules;

        public IfRuleBuilder(Rule... conditionRules) {
            this.conditionRules = List.of(conditionRules);
        }

        public IfRule then(Rule... thenRules) {
            return new IfRule(conditionRules, List.of(thenRules));
        }
    }

    @Override
    public @Nullable String validate(Step step, ValidationContext context) {
        // no validation since all rules validation is handled using getRules
        return null;
    }

    @Override
    public Iterable<Rule> getRules() {
        return thenRules;
    }

    @Override
    public Iterable<? extends Condition> getConditions() {
        return conditionRules
                .stream()
                .map(PassesRuleCondition::of)
                .toList();
    }
}
