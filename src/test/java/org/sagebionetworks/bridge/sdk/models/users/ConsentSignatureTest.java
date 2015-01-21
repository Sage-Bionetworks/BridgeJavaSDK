package org.sagebionetworks.bridge.sdk.models.users;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.joda.time.LocalDate;
import org.junit.Test;

public class ConsentSignatureTest {

    @Test
    public void hashCodeEquals() {
        ConsentSignature sig1 = createConsentSignature("Test User");
        ConsentSignature sig2 = createConsentSignature("Test User");
        assertEquals(sig1.hashCode(), sig2.hashCode());
        assertTrue(sig1.equals(sig2));
    }
    
    @Test
    public void hashCodeEqualsUnequal() {
        ConsentSignature sig1 = createConsentSignature("Test User");
        ConsentSignature sig2 = createConsentSignature("Test User 2");
        
        assertNotEquals(sig1.hashCode(), sig2.hashCode());
        assertFalse(sig1.equals(sig2));
    }
    
    private ConsentSignature createConsentSignature(String name) {
        return new ConsentSignature(name, LocalDate.parse("1900-02-02"), "absc", "image/png");
    }

}
