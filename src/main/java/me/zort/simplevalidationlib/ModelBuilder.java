package me.zort.simplevalidationlib;

import me.zort.simplevalidationlib.rule.Rule;

import java.util.*;

/**
 * A builder class for creating a {@link Model}.
 *
 * @author ZorTik
 */
public final class ModelBuilder {
    private final List<ModelPathBuilder> pathBuilders;

    public class ModelPathBuilder {
        private final String path;
        private final List<Rule> rules;

        private ModelPathBuilder(String path) {
            this.path = path;
            this.rules = new ArrayList<>();
        }

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

    public ModelPathBuilder path(String path) {
        Objects.requireNonNull(path, "Path cannot be null");

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

    public ModelBuilder path(String path, Rule... rules) {
        ModelPathBuilder pathBuilder = path(path);
        for (Rule rule : rules) {
            pathBuilder.rule(rule);
        }
        return this;
    }

    public Model build() {
        Map<String, Iterable<Rule>> rulesMap = new LinkedHashMap<>();
        pathBuilders.forEach(pb ->
                rulesMap.put(pb.path, new ArrayList<>(pb.rules)));

        return new Model(rulesMap);
    }
}
