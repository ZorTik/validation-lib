package me.zort.simplevalidationlib;

import me.zort.simplevalidationlib.rule.Rule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Model and ModelBuilder Contract Tests")
class ModelBuilderTest {

    @Test
    @DisplayName("Should correctly map paths and rules using varargs path method")
    void shouldBuildModelUsingVarargsPath() {
        Model model = Model.builder()
                .path("app.name", Rule.required(), Rule.string())
                .path("app.version", Rule.required(), Rule.integer())
                .build();

        Map<String, Iterable<Rule>> rules = model.getRules();
        assertEquals(2, rules.size());
        assertTrue(rules.containsKey("app.name"));
        assertTrue(rules.containsKey("app.version"));
    }

    @Test
    @DisplayName("Should correctly map paths and rules using fluent chain builder")
    void shouldBuildModelUsingFluentChain() {
        Model model = Model.builder()
                .path("database.host")
                    .rule(Rule.required())
                    .rule(Rule.string())
                    .and()
                .path("database.port")
                    .rule(Rule.required())
                    .rule(Rule.integer())
                    .and()
                .build();

        Map<String, Iterable<Rule>> rules = model.getRules();
        assertEquals(2, rules.size());
        assertTrue(rules.containsKey("database.host"));
        assertTrue(rules.containsKey("database.port"));
    }

    @Test
    @DisplayName("Should merge rules when same path is declared multiple times")
    void shouldMergeRulesForDuplicatePath() {
        Model model = Model.builder()
                .path("server.port", Rule.required())
                .path("server.port", Rule.integer())
                .build();

        Map<String, Iterable<Rule>> rules = model.getRules();
        assertEquals(1, rules.size());
        List<Rule> portRules = (List<Rule>) rules.get("server.port");
        assertEquals(2, portRules.size());
    }

    @Test
    @DisplayName("Should throw NullPointerException when path is null")
    void shouldThrowNpeForNullPath() {
        ModelBuilder builder = Model.builder();
        assertThrows(NullPointerException.class, () -> builder.path(null));
    }

    @Test
    @DisplayName("Model rules map should be unmodifiable")
    void shouldReturnUnmodifiableMap() {
        Model model = Model.builder()
                .path("test.path", Rule.required())
                .build();

        Map<String, Iterable<Rule>> rules = model.getRules();
        assertThrows(UnsupportedOperationException.class, () -> rules.put("new.path", List.of()));
    }
}
