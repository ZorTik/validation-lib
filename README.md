# validation-lib

So, there was nothing simple enough for validation of Bukkit configuration.

Therefore, enjoy :)

```java
public class Examples {

    public static void loadConfig(ConfigurationSection config) {
        Model model = Model.builder()
                .path("redis.enabled", Rule.required(), Rule.bool())
                .path("redis.host", ifRedisEnabled(Rule.required(), Rule.string()))
                .path("redis.port", ifRedisEnabled(Rule.integer(), Rule.min(1), Rule.max(65535)))
                .path("redis.user", ifRedisEnabled(Rule.string()))
                .path("redis.password", ifRedisEnabled(Rule.string()))
                .build();

        ConfigValidator validator = ConfigValidator.create();
        try {
            validator.validate(config, model);
        } catch (RuleValidationException e) {
            String failedPath = e.getFailedStep().getPath();
            String errorMessage = e.getMessage();
            // TODO: Handle the validation error
        }
        
        // We are safe to load
    }

    private static Rule ifRedisEnabled(Rule... rules) {
        return Rule
                .onlyIf(
                        Rule.target("redis.enabled", Rule.isEqual(true))
                )
                .then(rules);
    }
}
```