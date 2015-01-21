package org.sagebionetworks.bridge.sdk.models.users;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ResetPasswordCredentialsTest {
    @Test
    public void hashCodeEquals() {
        ResetPasswordCredentials c1 = createResetPasswordCredentials("testuser@test.com");
        ResetPasswordCredentials c2 = createResetPasswordCredentials("testuser@test.com");
        
        assertEquals(c1.hashCode(), c2.hashCode());
        assertTrue(c1.equals(c2));
    }
    
    @Test
    public void hashCodeEqualsUnequal() {
        ResetPasswordCredentials c1 = createResetPasswordCredentials("testuser@test.com");
        ResetPasswordCredentials c2 = createResetPasswordCredentials("testuser2@test.com");
        
        assertNotEquals(c1.hashCode(), c2.hashCode());
        assertFalse(c1.equals(c2));
    }
    
    private ResetPasswordCredentials createResetPasswordCredentials(String name) {
        return new ResetPasswordCredentials(name);
    }
}
