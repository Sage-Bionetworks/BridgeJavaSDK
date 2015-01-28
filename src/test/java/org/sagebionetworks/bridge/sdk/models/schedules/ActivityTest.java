package org.sagebionetworks.bridge.sdk.models.schedules;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

public class ActivityTest {
    
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Activity.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void equalsForActivity() {
        EqualsVerifier.forClass(SurveyReference.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();   
    }
    
}
