package me.zort.validationlib;

import me.zort.validationlib.rule.Rule;
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

    @Test
    @DisplayName("Should correctly prefix sub-model paths when embedding model")
    void shouldEmbedSubModelPathsWithPrefix() {
        Model subModel = Model.builder()
                .path("host", Rule.required(), Rule.string())
                .path("port", Rule.required(), Rule.integer())
                .build();

        Model parentModel = Model.builder()
                .path("database", subModel)
                .build();

        Map<String, Iterable<Rule>> rules = parentModel.getRules();
        assertEquals(2, rules.size());
        assertTrue(rules.containsKey("database.host"));
        assertTrue(rules.containsKey("database.port"));

        List<Rule> hostRules = (List<Rule>) rules.get("database.host");
        assertEquals(2, hostRules.size());

        List<Rule> portRules = (List<Rule>) rules.get("database.port");
        assertEquals(2, portRules.size());
    }

    @Test
    @DisplayName("Should merge rules when embedding sub-model into path with existing rules")
    void shouldMergeRulesWhenEmbeddingSubModelOnExistingPath() {
        Model subModel = Model.builder()
                .path("host", Rule.required())
                .build();

        Model parentModel = Model.builder()
                .path("database.host", Rule.string())
                .path("database", subModel)
                .build();

        Map<String, Iterable<Rule>> rules = parentModel.getRules();
        assertEquals(1, rules.size());
        assertTrue(rules.containsKey("database.host"));

        List<Rule> hostRules = (List<Rule>) rules.get("database.host");
        assertEquals(2, hostRules.size());
    }

    @Test
    @DisplayName("Should handle multi-level nested models")
    void shouldHandleMultiLevelNestedModels() {
        Model poolModel = Model.builder()
                .path("size", Rule.integer())
                .build();

        Model dbModel = Model.builder()
                .path("pool", poolModel)
                .build();

        Model appModel = Model.builder()
                .path("app.database", dbModel)
                .build();

        Map<String, Iterable<Rule>> rules = appModel.getRules();
        assertEquals(1, rules.size());
        assertTrue(rules.containsKey("app.database.pool.size"));
    }

    @Test
    @DisplayName("Should throw NullPointerException when embedded model is null")
    void shouldThrowNpeWhenSubModelIsNull() {
        ModelBuilder builder = Model.builder();
        assertThrows(NullPointerException.class, () -> builder.path("database", (Model) null));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when embedding sub-model with invalid parent path")
    void shouldThrowIllegalArgumentExceptionForInvalidParentPath() {
        Model subModel = Model.builder()
                .path("host", Rule.required())
                .build();
        ModelBuilder builder = Model.builder();

        assertThrows(IllegalArgumentException.class, () -> builder.path("", subModel));
        assertThrows(IllegalArgumentException.class, () -> builder.path("db.", subModel));
        assertThrows(IllegalArgumentException.class, () -> builder.path(".db", subModel));
        assertThrows(IllegalArgumentException.class, () -> builder.path("db..host", subModel));
    }

    @Test
    @DisplayName("Should convert Model to ModelBuilder preserving all paths and rules")
    void shouldConvertToModelBuilderPreservingPathsAndRules() {
        Model original = Model.builder()
                .path("app.name", Rule.required(), Rule.string())
                .path("app.version", Rule.required(), Rule.integer())
                .build();

        Model copy = original.toModelBuilder().build();

        Map<String, Iterable<Rule>> originalRules = original.getRules();
        Map<String, Iterable<Rule>> copyRules = copy.getRules();

        assertEquals(originalRules.keySet(), copyRules.keySet());
        assertEquals(2, ((List<Rule>) copyRules.get("app.name")).size());
        assertEquals(2, ((List<Rule>) copyRules.get("app.version")).size());
    }

    @Test
    @DisplayName("Should allow modifying builder created from toModelBuilder without affecting original Model")
    void shouldAllowModifyingConvertedModelBuilderIndependently() {
        Model original = Model.builder()
                .path("database.host", Rule.required())
                .build();

        Model modified = original.toModelBuilder()
                .path("database.host", Rule.string())
                .path("database.port", Rule.integer())
                .build();

        assertEquals(1, original.getRules().size());
        assertEquals(1, ((List<Rule>) original.getRules().get("database.host")).size());

        assertEquals(2, modified.getRules().size());
        assertEquals(2, ((List<Rule>) modified.getRules().get("database.host")).size());
        assertTrue(modified.getRules().containsKey("database.port"));
    }

    @Test
    @DisplayName("Should extend existing model with another model containing distinct paths")
    void shouldExtendModelWithDistinctPaths() {
        Model baseModel = Model.builder()
                .path("app.name", Rule.required())
                .build();

        Model extensionModel = Model.builder()
                .path("app.version", Rule.integer())
                .build();

        Model combined = baseModel.extend(extensionModel);

        assertEquals(2, combined.getRules().size());
        assertTrue(combined.getRules().containsKey("app.name"));
        assertTrue(combined.getRules().containsKey("app.version"));

        assertEquals(1, baseModel.getRules().size());
        assertEquals(1, extensionModel.getRules().size());
    }

    @Test
    @DisplayName("Should merge rules for overlapping paths when extending model")
    void shouldMergeRulesWhenExtendingModelWithOverlappingPaths() {
        Model baseModel = Model.builder()
                .path("server.port", Rule.required())
                .build();

        Model extensionModel = Model.builder()
                .path("server.port", Rule.integer())
                .path("server.host", Rule.string())
                .build();

        Model combined = baseModel.extend(extensionModel);

        assertEquals(2, combined.getRules().size());
        List<Rule> portRules = (List<Rule>) combined.getRules().get("server.port");
        assertEquals(2, portRules.size());
    }

    @Test
    @DisplayName("Should handle extending with empty model")
    void shouldHandleExtendingWithEmptyModel() {
        Model baseModel = Model.builder()
                .path("app.name", Rule.required())
                .build();

        Model emptyModel = Model.builder().build();

        Model result = baseModel.extend(emptyModel);
        assertEquals(1, result.getRules().size());
        assertTrue(result.getRules().containsKey("app.name"));
    }

    @Test
    @DisplayName("Should throw NullPointerException when extending with null model")
    void shouldThrowNpeWhenExtendingWithNullModel() {
        Model baseModel = Model.builder()
                .path("app.name", Rule.required())
                .build();

        assertThrows(NullPointerException.class, () -> baseModel.extend(null));
    }
}

