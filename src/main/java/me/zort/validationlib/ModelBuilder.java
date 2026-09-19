package me.zort.validationlib;

import me.zort.validationlib.rule.Rule;

import java.util.*;

/**
 * A builder class for creating a {@link Model}.
 *
 * @author ZorTik
 */
public final class ModelBuilder {
    private final List<ModelPathBuilder> pathBuilders;

    public final class ModelPathBuilder {
        private final String path;
        private final List<Rule> rules;

        private ModelPathBuilder(String path) {
            this.path = path;
            this.rules = new ArrayList<>();
        }

        /**
         * Adds a rule to the current path.
         *
         * @param rule the rule to add
         * @return this {@link ModelPathBuilder} instance
         * @throws NullPointerException if the rule is null
         */
        public ModelPathBuilder rule(Rule rule) {
            this.rules.add(rule);
            return this;
        }

        public ModelBuilder and() {
            return ModelBuilder.this;
        }
    }

    public ModelBuilder() {
        this.pathBuilders = new ArrayList<>();
    }

    /**
     * Adds a new path to the model builder.
     *
     * @param path the path to add
     * @return a {@link ModelPathBuilder} for adding rules to the specified path
     * @throws NullPointerException if the path is null
     * @throws IllegalArgumentException if the path is empty, starts or ends with a dot, or contains consecutive dots
     */
    public ModelPathBuilder path(String path) {
        validatePath(path);

        ModelPathBuilder pathBuilder = pathBuilders.stream()
                .filter(pb -> pb.path.equals(path))
                .findFirst()
                .orElse(null);
        if (pathBuilder != null) {
            // path already exists, return the existing path builder
            return pathBuilder;
        }

        pathBuilder = new ModelPathBuilder(path);
        pathBuilders.add(pathBuilder);
        return pathBuilder;
    }

    /**
     * Adds a new path to the model builder with the specified rules.
     *
     * @param path the path to add
     * @param rules the rules to apply to the path
     * @return this {@link ModelBuilder} instance
     * @throws NullPointerException if the path is null
     * @throws IllegalArgumentException if the path is empty, starts or ends with a dot, or contains consecutive dots
     */
    public ModelBuilder path(String path, Rule... rules) {
        validatePath(path);

        ModelPathBuilder pathBuilder = path(path);
        for (Rule rule : rules) {
            pathBuilder.rule(rule);
        }
        return this;
    }

    /**
     * Adds a new path to the model builder with the rules from the specified model.
     *
     * @param path the path to add
     * @param model the model containing the rules to apply to the path
     * @return this {@link ModelBuilder} instance
     * @throws NullPointerException if the path or model is null
     * @throws IllegalArgumentException if the path is empty, starts or ends with a dot, or contains consecutive dots
     */
    public ModelBuilder path(String path, Model model) {
        validatePath(path);
        Objects.requireNonNull(model, "Model cannot be null");

        model.getRules().forEach((subPath, rules) -> {
            String combinedPath = path + "." + subPath;

            ModelPathBuilder pathBuilder = path(combinedPath);
            for (Rule rule : rules) {
                pathBuilder.rule(rule);
            }
        });
        return this;
    }

    /**
     * Builds the {@link Model}.
     *
     * @return a new {@link Model}.
     */
    public Model build() {
        Map<String, Iterable<Rule>> rulesMap = new LinkedHashMap<>();
        pathBuilders.forEach(pb ->
                rulesMap.put(pb.path, new ArrayList<>(pb.rules)));

        return new Model(rulesMap);
    }

    private static void validatePath(String path) {
        Objects.requireNonNull(path, "Path cannot be null");

        if (path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be empty");
        }
        if (path.startsWith(".") || path.endsWith(".")) {
            throw new IllegalArgumentException("Path cannot start or end with a dot");
        }
        if (path.contains("..")) {
            throw new IllegalArgumentException("Path cannot contain consecutive dots");
        }
    }
}
