package me.zort.simplevalidationlib.rule;

import lombok.Getter;
import me.zort.simplevalidationlib.Step;
import me.zort.simplevalidationlib.ValidationContext;
import me.zort.simplevalidationlib.condition.Condition;
import me.zort.simplevalidationlib.condition.ExistsCondition;
import me.zort.simplevalidationlib.condition.PassesRuleCondition;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public class MinRule implements Rule {
    private final double min;

    public MinRule(double min) {
        this.min = min;
    }

    public static MinRule of(double min) {
        return new MinRule(min);
    }

    @Override
    public @Nullable String validate(Step step, ValidationContext context) {
        Object val = step.getConfig().get(step.getPath());
        if (val instanceof Number) {
            double doubleVal = ((Number) val).doubleValue();
            if (doubleVal < min) {
                String formattedMin = min % 1 == 0 ? String.valueOf((long) min) : String.valueOf(min);

                return "Value must be at least " + formattedMin + ".";
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
