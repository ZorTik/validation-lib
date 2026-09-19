package me.zort.validationlib.rule;

import lombok.Getter;
import me.zort.validationlib.Model;
import me.zort.validationlib.Step;
import me.zort.validationlib.ValidationContext;
import me.zort.validationlib.condition.Condition;
import me.zort.validationlib.condition.ExistsCondition;
import me.zort.validationlib.condition.PassesRuleCondition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
public class ContentModelRule implements Rule {
    private final Model model;
    private final List<Rule> subRules;

    public ContentModelRule(@Nullable Model model) {
        this(model, Collections.emptyList());
    }

    public ContentModelRule(List<Rule> subRules) {
        this(null, subRules);
    }

    public ContentModelRule(@Nullable Model model, List<Rule> subRules) {
        this.model = model;
        this.subRules = subRules != null ? subRules : Collections.emptyList();
    }

    public static ContentModelRule of(Model model) {
        return new ContentModelRule(model);
    }

    public static ContentModelRule of(Rule... subRules) {
        return new ContentModelRule(List.of(subRules));
    }

    @Override
    public @Nullable String validate(Step step, ValidationContext context) {
        ConfigurationSection config = step.getConfig();
        if (config.isConfigurationSection(step.getPath())) {
            validateSection(step, context, config);
            return null;
        } else if (config.isList(step.getPath())) {
            validateList(step, context, config);
            return null;
        }

        return "Used on invalid path type. Expected a section or a list.";
    }

    private void validateSection(Step step, ValidationContext context, ConfigurationSection config) {
        ConfigurationSection section = config.getConfigurationSection(step.getPath());
        assert section != null;

        for (String key : section.getKeys(false)) {
            String keyPath = step.getPath() + "." + key;
            validateElement(keyPath, config, context);
        }
    }

    private void validateList(Step step, ValidationContext context, ConfigurationSection config) {
        List<?> list = config.getList(step.getPath());
        assert list != null;

        for (int i = 0; i < list.size(); i++) {
            Object element = list.get(i);
            String elementPath = step.getPath() + "[" + i + "]";

            ConfigurationSection tempConfig = new MemoryConfiguration();
            if (element instanceof ConfigurationSection section) {
                tempConfig.createSection(elementPath, section.getValues(true));
            } else if (element instanceof Map<?, ?> map) {
                tempConfig.createSection(elementPath, map);
            } else if (element != null) {
                tempConfig.set(elementPath, element);
            }

            validateElement(elementPath, tempConfig, context);
        }
    }

    private void validateElement(String elementPath, ConfigurationSection config, ValidationContext context) {
        if (model != null) {
            model.getRules().forEach((subPath, rulesList) -> {
                String fullPath = elementPath + "." + subPath;
                for (Rule rule : rulesList) {
                    context.validate(new Step(config, fullPath, rule));
                }
            });
        }

        for (Rule rule : subRules) {
            context.validate(new Step(config, elementPath, rule));
        }
    }

    @Override
    public Iterable<Condition> getConditions() {
        return List.of(
                ExistsCondition.getInstance(),
                Condition.any(
                        PassesRuleCondition.of(Rule.section()),
                        PassesRuleCondition.of(Rule.list())
                )
        );
    }
}
