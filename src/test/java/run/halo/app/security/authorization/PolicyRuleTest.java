package run.halo.app.security.authorization;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import run.halo.app.infra.utils.JsonUtils;

/**
 * Tests for {@link PolicyRule}.
 *
 * @author guqing
 * @since 2.0.0
 */
class PolicyRuleTest {
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = JsonUtils.DEFAULT_JSON_MAPPER;
    }

    @Test
    public void constructPolicyRule() throws JsonProcessingException, JSONException {
        PolicyRule policyRule = new PolicyRule(null, null, null, null, null, null);
        assertThat(policyRule).isNotNull();
        JSONAssert.assertEquals("""
            {
                "pluginName": "",
                "apiGroups": [],
                "resources": [],
                "resourceNames": [],
                "nonResourceURLs": [],
                "verbs": []
            }
            """,
            JsonUtils.objectToJson(policyRule),
            true);

        PolicyRule policyByBuilder = new PolicyRule.Builder().build();
        JSONAssert.assertEquals("""
            {
                "pluginName": "",
                "apiGroups": [],
                "resources": [],
                "resourceNames": [],
                "nonResourceURLs": [],
                "verbs": []
            }
            """,
            JsonUtils.objectToJson(policyByBuilder),
            true);

        PolicyRule policyNonNull = new PolicyRule.Builder()
            .pluginName("fakePluginName")
            .apiGroups("group")
            .resources("resource-1", "resource-2")
            .resourceNames("resourceName")
            .nonResourceURLs("non resource url")
            .verbs("verbs")
            .build();

        JsonNode expected = objectMapper.readTree("""
            {
                "pluginName": "fakePluginName",
                "apiGroups": [
                    "group"
                ],
                "resources": [
                    "resource-1",
                    "resource-2"
                ],
                "resourceNames": [
                    "resourceName"
                ],
                "nonResourceURLs": [
                    "non resource url"
                ],
                "verbs": [
                    "verbs"
                ]
            }
            """);
        JsonNode policyNonNullJson = objectMapper.valueToTree(policyNonNull);
        assertThat(policyNonNullJson).isEqualTo(expected);
    }
}