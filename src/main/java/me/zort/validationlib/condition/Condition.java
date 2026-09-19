package me.zort.validationlib.condition;

import me.zort.validationlib.Step;
import me.zort.validationlib.ValidationContext;

public interface Condition {

    /**
     * Evaluates the condition against the provided step and validation context.
     *
     * @param step    The current step in the validation process.
     * @param context The validation context containing relevant information.
     * @return true if the condition passes, false otherwise.
     */
    boolean passes(Step step, ValidationContext context);

    /**
     * Aggregates multiple conditions into a single condition that passes only if all provided conditions pass.
     *
     * @param conditions The conditions to aggregate.
     * @return A new Condition that represents the aggregation of the provided conditions.
     */
    static Condition all(Condition... conditions) {
        return (step, context) -> {
            for (Condition condition : conditions) {
                if (!condition.passes(step, context)) {
                    return false;
                }
            }

            return true;
        };
    }

    /**
     * Aggregates multiple conditions into a single condition that passes if at least one of the provided conditions passes.
     *
     * @param conditions The conditions to aggregate.
     * @return A new Condition that represents the aggregation of the provided conditions.
     */
    static Condition any(Condition... conditions) {
        return (step, context) -> {
            for (Condition condition : conditions) {
                if (condition.passes(step, context)) {
                    return true;
                }
            }

            return false;
        };
    }
}
