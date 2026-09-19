package me.zort.simplevalidationlib;

import me.zort.simplevalidationlib.exception.InvalidConfigurationException;
import me.zort.simplevalidationlib.rule.Rule;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the context of a validation process.
 *
 * @author ZorTik
 */
public final class ValidationContext {
    private final ValidatorReference validatorReference;
    private final List<Rule> passedRules;

    public interface ValidatorReference {
        boolean validateRule(Rule rule, Step step, ValidationContext context);
    }

    ValidationContext(ValidatorReference validatorReference) {
        this.validatorReference = validatorReference;
        this.passedRules = new ArrayList<>();
    }

    /**
     * Validates the specified rule against the given step and context.
     *
     * @param rule The rule to validate.
     * @param step The step to validate against.
     * @param context The validation context.
     * @return true if the rule passed both validation and conditions to test. false otherwise.
     * @throws InvalidConfigurationException if the rule validation fails.
     */
    public boolean validate(Rule rule, Step step, ValidationContext context) {
        return validatorReference.validateRule(rule, step, context);
    }

    void addPassedRule(Rule rule) {
        passedRules.add(rule);
    }

    /**
     * Checks if a rule of the specified class has been passed.
     *
     * @param ruleClass The class of the rule to check.
     * @return true if a rule of the specified class has been passed, false otherwise.
     */
    public boolean hasPassedRule(Class<? extends Rule> ruleClass) {
        return getRuleIfPassed(ruleClass).isPresent();
    }

    /**
     * Retrieves a rule of the specified class if it has been passed.
     *
     * @param ruleClass The class of the rule to retrieve.
     * @param <R> The type of the rule.
     * @return An Optional containing the rule if it has been passed, or an empty Optional if not.
     */
    public <R extends Rule> Optional<R> getRuleIfPassed(Class<R> ruleClass) {
        return passedRules.stream()
                .filter(rule -> ruleClass.isAssignableFrom(rule.getClass()))
                .map(ruleClass::cast)
                .findFirst();
    }

    @Unmodifiable
    public List<Rule> getPassedRules() {
        return List.copyOf(passedRules);
    }
}
