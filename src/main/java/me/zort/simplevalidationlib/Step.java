package me.zort.simplevalidationlib;

import lombok.Getter;
import me.zort.simplevalidationlib.rule.Rule;
import org.bukkit.configuration.ConfigurationSection;

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
}
