package me.zort.validationlib;

import me.zort.validationlib.exception.RuleValidationException;
import me.zort.validationlib.rule.Rule;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("List Element and Section Record Validation Tests")
class ListElementAndSectionRecordValidationTest {

    private ConfigValidator validator;
    private MemoryConfiguration config;

    @BeforeEach
    void setUp() {
        validator = ConfigValidator.create();
        config = new MemoryConfiguration();
    }

    @Nested
    @DisplayName("List Element Rules Behavior with Rule.element(...)")
    class ListElementRulesTests {

        @Test
        @DisplayName("Should pass when all list elements satisfy element rules")
        void shouldPassWhenAllListElementsAreValid() {
            config.set("tags", List.of("alpha", "beta", "gamma"));

            Model model = Model.builder()
                    .path("tags", Rule.required(), Rule.list(), Rule.element(Rule.string()))
                    .build();

            assertDoesNotThrow(() -> validator.validate(config, model));
        }

        @Test
        @DisplayName("Should fail when a list element violates element rules")
        void shouldFailWhenListElementIsInvalid() {
            config.set("tags", List.of("alpha", 12345, "gamma"));

            Model model = Model.builder()
                    .path("tags", Rule.required(), Rule.list(), Rule.element(Rule.string()))
                    .build();

            RuleValidationException ex = assertThrows(
                    RuleValidationException.class,
                    () -> validator.validate(config, model)
            );

            assertEquals("tags[1]", ex.getFailedStep().getPath());
            assertTrue(ex.getMessage().contains("Expected a string value"));
        }

        @Test
        @DisplayName("Should pass when list elements satisfy multiple rules like type and bounds")
        void shouldPassForMultipleElementRules() {
            config.set("scores", List.of(10, 20, 30));

            Model model = Model.builder()
                    .path("scores", Rule.required(), Rule.list(), Rule.element(Rule.integer(), Rule.min(10)))
                    .build();

            assertDoesNotThrow(() -> validator.validate(config, model));
        }

        @Test
        @DisplayName("Should fail when list element violates bounds rule")
        void shouldFailWhenListElementViolatesBounds() {
            config.set("scores", List.of(10, 5, 30));

            Model model = Model.builder()
                    .path("scores", Rule.required(), Rule.list(), Rule.element(Rule.integer(), Rule.min(10)))
                    .build();

            RuleValidationException ex = assertThrows(
                    RuleValidationException.class,
                    () -> validator.validate(config, model)
            );

            assertEquals("scores[1]", ex.getFailedStep().getPath());
            assertTrue(ex.getMessage().contains("Value must be at least 10"));
        }

        @Test
        @DisplayName("Should validate list elements against a complex Model using Rule.element(Model)")
        void shouldValidateListElementsWithModel() {
            config.set("users", List.of(
                    Map.of("name", "Alice", "age", 25),
                    Map.of("name", "Bob", "age", -5)
            ));

            Model userModel = Model.builder()
                    .path("name", Rule.required(), Rule.string())
                    .path("age", Rule.required(), Rule.integer(), Rule.min(0))
                    .build();

            Model model = Model.builder()
                    .path("users", Rule.required(), Rule.list(), Rule.element(userModel))
                    .build();

            RuleValidationException ex = assertThrows(
                    RuleValidationException.class,
                    () -> validator.validate(config, model)
            );

            assertEquals("users[1].age", ex.getFailedStep().getPath());
            assertTrue(ex.getMessage().contains("Value must be at least 0"));
        }
    }

    @Nested
    @DisplayName("Section Record Rules Behavior with Rule.element(...) and Rule.contentModel(...)")
    class SectionRecordRulesTests {

        @Test
        @DisplayName("Should pass when all section key values satisfy record rules")
        void shouldPassWhenAllRecordValuesAreValid() {
            config.set("ranks.vip", 10);
            config.set("ranks.admin", 100);

            Model model = Model.builder()
                    .path("ranks", Rule.required(), Rule.section(), Rule.element(Rule.integer(), Rule.min(1)))
                    .build();

            assertDoesNotThrow(() -> validator.validate(config, model));
        }

        @Test
        @DisplayName("Should fail when a record value in section violates type rule")
        void shouldFailWhenRecordValueIsInvalidType() {
            config.set("ranks.vip", 10);
            config.set("ranks.admin", "hundred");

            Model model = Model.builder()
                    .path("ranks", Rule.required(), Rule.section(), Rule.element(Rule.integer()))
                    .build();

            RuleValidationException ex = assertThrows(
                    RuleValidationException.class,
                    () -> validator.validate(config, model)
            );

            assertEquals("ranks.admin", ex.getFailedStep().getPath());
            assertTrue(ex.getMessage().contains("Expected an integer value"));
        }

        @Test
        @DisplayName("Should validate section records against a complex Model using Rule.contentModel(Model)")
        void shouldValidateSectionRecordsWithRecord() {
            config.set("players.player1.level", 10);
            config.set("players.player1.rank", "VIP");
            config.set("players.player2.level", "not-a-number");
            config.set("players.player2.rank", "Admin");

            Model playerModel = Model.builder()
                    .path("level", Rule.required(), Rule.integer())
                    .path("rank", Rule.required(), Rule.string())
                    .build();

            Model model = Model.builder()
                    .path("players", Rule.required(), Rule.section(), Rule.element(playerModel))
                    .build();

            RuleValidationException ex = assertThrows(
                    RuleValidationException.class,
                    () -> validator.validate(config, model)
            );

            assertEquals("players.player2.level", ex.getFailedStep().getPath());
            assertTrue(ex.getMessage().contains("Expected an integer value"));
        }
    }
}
