package org.sagebionetworks.bridge.sdk.models.subpopulations;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

import org.sagebionetworks.bridge.sdk.models.subpopulations.StudyConsent;

public class StudyConsentTest {
    
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(StudyConsent.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
}
