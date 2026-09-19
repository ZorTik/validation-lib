# SimpleValidationLib

In other words, if there is nothing simple enough.

Enjoy :)

```java
public class Examples {

    public static void loadConfig(ConfigurationSection config) {
        Model model = Model.builder()
                .path("redis.enabled", Rule.required(), Rule.bool())
                .path("redis.host", onlyIfRedisEnabled(Rule.required(), Rule.string()))
                .path("redis.port", onlyIfRedisEnabled(Rule.integer(), Rule.min(1), Rule.max(65535)))
                .path("redis.user", onlyIfRedisEnabled(Rule.string()))
                .path("redis.password", onlyIfRedisEnabled(Rule.string()))
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
        if (config.getBoolean("redis.enabled")) {
            String host = config.getString("redis.host");
            int port = config.getInt("redis.port", 6379);
            String user = config.getString("redis.user", "default");
            String password = config.getString("redis.password", null);
        }
    }

    private static Rule onlyIfRedisEnabled(Rule... rules) {
        return Rule
                .onlyIf(
                        Rule.target("redis.enabled", Rule.isEqual(true))
                )
                .then(rules);
    }
}
```