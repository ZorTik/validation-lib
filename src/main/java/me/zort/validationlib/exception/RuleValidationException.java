package me.zort.validationlib.exception;

import lombok.Getter;
import me.zort.validationlib.Step;

@Getter
public class RuleValidationException extends InvalidConfigurationException {
    private final Step failedStep;

    public RuleValidationException(Step failedStep, String message) {
        super(message);
        this.failedStep = failedStep;
    }

    public RuleValidationException(Step failedStep, String message, Throwable cause) {
        super(message, cause);
        this.failedStep = failedStep;
    }
}
