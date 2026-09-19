package me.zort.validationlib;

import me.zort.validationlib.rule.Rule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.Map;
import java.util.function.BiConsumer;

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

    /**
     * Iterates over all paths that are valid.
     *
     * @param consumer the consumer to apply to each valid path and its associated rules
     */
    public void forEachValidPaths(BiConsumer<String, Iterable<Rule>> consumer) {
        for (Map.Entry<String, Iterable<Rule>> entry : rules.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }

            consumer.accept(entry.getKey(), entry.getValue());
        }
    }

    @Unmodifiable
    public Map<String, Iterable<Rule>> getRules() {
        return rules;
    }

    @NotNull
    public Model extend(Model other) {
        ModelBuilder builder = toModelBuilder();
        builder.importPaths(other);
        return builder.build();
    }

    @NotNull
    public ModelBuilder toModelBuilder() {
        ModelBuilder builder = new ModelBuilder();
        builder.importPaths(this);
        return builder;
    }
}
