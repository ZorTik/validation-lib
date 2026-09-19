package me.zort.validationlib;

import lombok.Getter;
import me.zort.validationlib.rule.Rule;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

@Getter
public final class Step {
    private final ConfigurationSection config;
    private final String path;
    private final Rule rule;

    public Step(ConfigurationSection config, String path, Rule rule) {
        this.config = config;
        this.path = path;
        this.rule = rule;
    }

    /**
     * Creates a new Step with the same config and path, but with a different rule.
     *
     * @param rule The new rule to use for the new Step.
     * @return A new Step with the same config and path, but with the specified rule.
     */
    @NotNull
    public Step withDifferentRule(Rule rule) {
        return new Step(config, path, rule);
    }

    @NotNull
    public Step withDifferentPathRule(String path, Rule rule) {
        return new Step(config, path, rule);
    }
}
