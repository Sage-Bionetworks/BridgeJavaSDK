package org.sagebionetworks.bridge.sdk.models.surveys;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.fasterxml.jackson.databind.JsonNode;

import nl.jqno.equalsverifier.EqualsVerifier;

public class SurveyRuleTest {
    
    @Test
    public void hashCodeEquals() {
        EqualsVerifier.forClass(SurveyRule.class).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void canSerializeSkipToRule() throws Exception {
        String json = Tests.unescapeJson(
                "{'operator':'ne','value':'foo','skipTo':'targetId','type':'SurveyRule'}");
        SurveyRule rule = Utilities.getMapper().readValue(json, SurveyRule.class);
        SurveyRule deser = new SurveyRule.Builder().withOperator(SurveyRule.Operator.NE).withValue("foo")
                .withSkipToTarget("targetId").build();

        assertEquals(deser, rule);
        JsonNode node = Utilities.getMapper().valueToTree(rule);
        assertEquals("ne", node.get("operator").asText());
        assertEquals("foo", node.get("value").asText());
        assertEquals("targetId", node.get("skipTo").asText());
    }
    
    @Test
    public void canSerializeEndSurveyRule() throws Exception {
        String json = Tests.unescapeJson(
                "{'operator':'ne','value':'foo','endSurvey':true,'type':'SurveyRule'}");
        SurveyRule rule = Utilities.getMapper().readValue(json, SurveyRule.class);
        SurveyRule deser = new SurveyRule.Builder().withOperator(SurveyRule.Operator.NE).withValue("foo")
                .withEndSurvey(Boolean.TRUE).build();

        assertEquals(deser, rule);
        JsonNode node = Utilities.getMapper().valueToTree(rule);
        assertEquals("ne", node.get("operator").asText());
        assertEquals("foo", node.get("value").asText());
        assertTrue(node.get("endSurvey").asBoolean());
    }
    
    @Test
    public void cannotSetEndSurveyToFalse() {
        SurveyRule rule = new SurveyRule.Builder().withOperator(SurveyRule.Operator.NE).withValue("foo")
                .withEndSurvey(Boolean.FALSE).build();
        
        assertNull(rule.getEndSurvey());
    }
}
