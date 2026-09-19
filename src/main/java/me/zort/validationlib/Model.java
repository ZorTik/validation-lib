package me.zort.validationlib;

import me.zort.validationlib.rule.Rule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.Map;

/**
 * Represents a model for config validation.
 *
 * @author ZorTik
 */
public final class Model {
    private final Map<String, Iterable<Rule>> rules;

    public Model(Map<String, Iterable<Rule>> rules) {
        this.rules = Collections.unmodifiableMap(rules);
    }

    public static @NotNull ModelBuilder builder() {
        return new ModelBuilder();
    }

    @Unmodifiable
    public Map<String, Iterable<Rule>> getRules() {
        return rules;
    }
}
