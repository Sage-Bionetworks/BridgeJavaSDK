package org.sagebionetworks.bridge.sdk.models.schedules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.joda.time.DateTime;
import org.junit.Test;

public class SurveyReferenceTest {

    private static final String CREATED_ON_STRING = "2015-04-29T23:41:56.231Z";
    
    private static final DateTime CREATED_ON = DateTime.parse(CREATED_ON_STRING);
    
    @Test
    public void equalsHashCode() {
        EqualsVerifier.forClass(SurveyReference.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void correctlyParsesSurveyURL() {
        SurveyReference ref = new SurveyReference("identifier", "AAA-BBB-CCC", CREATED_ON);
        
        assertEquals("identifier", ref.getIdentifier());
        assertEquals("AAA-BBB-CCC", ref.getGuid());
        assertEquals(CREATED_ON, ref.getCreatedOn());
    }
    
    @Test
    public void correctlyParsesPublishedSurveyURL() {
        SurveyReference ref = new SurveyReference("identifier", "AAA-BBB-CCC");
        
        assertEquals("identifier", ref.getIdentifier());
        assertEquals("AAA-BBB-CCC", ref.getGuid());
        assertNull(ref.getCreatedOn());
    }

}
