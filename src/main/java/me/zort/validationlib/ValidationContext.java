package me.zort.validationlib;

import me.zort.validationlib.exception.InvalidConfigurationException;

/**
 * Represents the context of a validation process.
 *
 * @author ZorTik
 */
public final class ValidationContext {
    private final ValidatorReference validatorReference;

    public interface ValidatorReference {
        boolean validateStep(Step step, ValidationContext context);
    }

    ValidationContext(ValidatorReference validatorReference) {
        this.validatorReference = validatorReference;
    }

    /**
     * Validates the specified step.
     *
     * @param step The step to validate against.
     * @return true if the step passed both validation and conditions to test. false otherwise.
     * @throws InvalidConfigurationException if the step validation fails.
     */
    public boolean validate(Step step) {
        return validatorReference.validateStep(step, this);
    }
}
