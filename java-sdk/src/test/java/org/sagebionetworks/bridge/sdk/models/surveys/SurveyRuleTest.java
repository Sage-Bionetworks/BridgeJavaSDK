package org.sagebionetworks.bridge.sdk.models.surveys;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

import nl.jqno.equalsverifier.EqualsVerifier;

public class SurveyRuleTest {
    
    @Test
    public void hashCodeEquals() {
        EqualsVerifier.forClass(SurveyRule.class).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void canSerialize() throws Exception {
        String json = Tests.unescapeJson("{'operator':'ne','value':'foo',"+
                "'skipTo':'targetId','type':'SurveyRule'}");
        SurveyRule rule = Utilities.getMapper().readValue(json, SurveyRule.class);
        SurveyRule deser = new SurveyRule(SurveyRule.Operator.NE, "foo", "targetId");
        assertEquals(deser, rule);
        
        json = Tests.unescapeJson("{'operator':'ne','value':'foo',"+
                "'endSurvey':true,'type':'SurveyRule'}");
        rule = Utilities.getMapper().readValue(json, SurveyRule.class);
        deser = new SurveyRule(SurveyRule.Operator.NE, "foo");
        assertEquals(deser, rule);
    }

}
