package org.sagebionetworks.bridge.sdk.models.studies;

import static org.junit.Assert.assertEquals;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;
import org.sagebionetworks.bridge.sdk.Utilities;

public class StudyTest {
    
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Study.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void testRoundtripSerialization() throws Exception {
        Study study = new Study();
        study.setName("Test Name");
        study.getUserProfileAttributes().add("test");
        
        String json = Utilities.getMapper().writeValueAsString(study);
        assertEquals("{\"name\":\"Test Name\",\"minAgeOfConsent\":0,\"maxNumOfParticipants\":0,\"userProfileAttributes\":[\"test\"]}", json);
        
        Study newStudy = Utilities.getMapper().readValue(json, Study.class);
        
        assertEquals(study.getUserProfileAttributes(), newStudy.getUserProfileAttributes());
        assertEquals(study.getUserProfileAttributes().iterator().next(), newStudy.getUserProfileAttributes().iterator().next());
        assertEquals("Test Name", newStudy.getName());
    }
}
