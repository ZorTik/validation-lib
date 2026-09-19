package me.zort.validationlib.rule;

import lombok.Getter;
import me.zort.validationlib.Step;
import me.zort.validationlib.ValidationContext;
import me.zort.validationlib.condition.Condition;
import me.zort.validationlib.condition.ExistsCondition;
import me.zort.validationlib.condition.PassesRuleCondition;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public class MaxRule implements Rule {
    private final double max;

    public MaxRule(double max) {
        this.max = max;
    }

    public static MaxRule of(double max) {
        return new MaxRule(max);
    }

    @Override
    public @Nullable String validate(Step step, ValidationContext context) {
        Object val = step.getConfig().get(step.getPath());
        if (val instanceof Number) {
            double doubleVal = ((Number) val).doubleValue();
            if (doubleVal > max) {
                String formattedMax = max % 1 == 0 ? String.valueOf((long) max) : String.valueOf(max);

                return "Value must be at most " + formattedMax + ".";
            }
        }

        return null;
    }

    @Override
    public Iterable<Condition> getConditions() {
        return List.of(
                ExistsCondition.getInstance(),
                PassesRuleCondition.of(Rule.number())
        );
    }
}
