package org.sagebionetworks.bridge.sdk.models.studies;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

public class StudyTest {
    
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Study.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
}
