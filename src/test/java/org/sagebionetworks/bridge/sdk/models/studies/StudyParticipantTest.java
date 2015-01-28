package org.sagebionetworks.bridge.sdk.models.studies;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

public class StudyParticipantTest {
    
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(StudyParticipant.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }

}
