package me.zort.validationlib;

import me.zort.validationlib.condition.Condition;
import me.zort.validationlib.exception.InvalidConfigurationException;
import me.zort.validationlib.exception.RuleValidationException;
import me.zort.validationlib.rule.Rule;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * A simple utility class for validating configuration sections against a set of defined rules.
 *
 * @author ZorTik
 */
public final class ConfigValidator {

    /**
     * Creates a new instance of the ConfigValidator.
     *
     * @return A new instance of ConfigValidator.
     */
    public static @NotNull ConfigValidator create() {
        return new ConfigValidator();
    }

    /**
     * Validates the given configuration section against the defined rules.
     *
     * @param config The configuration section to validate.
     * @throws InvalidConfigurationException if the configuration is invalid according to the rules.
     */
    public void validate(ConfigurationSection config, Model model) {
        ValidationContext context = new ValidationContext(this::validateRuleInternal);

        for (Map.Entry<String, Iterable<Rule>> pathEntry : model.getRules().entrySet()) {
            Iterable<Rule> rulesForPath = pathEntry.getValue();
            if (rulesForPath == null) {
                continue;
            }

            for (Rule rule : rulesForPath) {
                validateStepInternal(new Step(config, pathEntry.getKey(), rule), context);
            }
        }
    }

    private boolean validateStepInternal(Step step, ValidationContext context) {
        Rule rule = step.getRule();

        boolean passed = validateRuleInternal(rule, step, context);
        if (passed) {
            context.addPassedRule(rule);
        }
        return passed;
    }

    private boolean validateRuleInternal(Rule rule, Step step, ValidationContext context) {
        for (Condition condition : rule.getConditions()) {
            if (!condition.passes(step, context)) {
                return false;
            }
        }

        for (Rule innerRule : rule.getRules()) {
            if (!validateStepInternal(new Step(step.getConfig(), step.getPath(), innerRule), context)) {
                return false;
            }
        }

        String error = rule.validate(step, context);
        if (error != null) {
            throw new RuleValidationException(step, error);
        }
        return true;
    }
}
