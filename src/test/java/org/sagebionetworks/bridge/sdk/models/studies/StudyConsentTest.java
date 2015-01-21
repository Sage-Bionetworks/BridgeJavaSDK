package org.sagebionetworks.bridge.sdk.models.studies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StudyConsentTest {
    @Test
    public void hashCodeEquals() {
        StudyConsent consent1 = createStudyConsent();
        StudyConsent consent2 = createStudyConsent();
        assertEquals(consent1.hashCode(), consent2.hashCode());
        assertTrue(consent1.equals(consent2));
    }
    
    @Test
    public void hashCodeEqualsUnequal() {
        StudyConsent consent1 = createStudyConsent();
        StudyConsent consent2 = createStudyConsent();
        consent2.setPath("/path/to/null");
        
        assertNotEquals(consent1.hashCode(), consent2.hashCode());
        assertFalse(consent1.equals(consent2));
    }
    
    private StudyConsent createStudyConsent() {
        StudyConsent consent = new StudyConsent();
        consent.setMinAge(18);
        consent.setPath("/path/null");
        consent.setVersion(3L);
        return consent;
    }
}
