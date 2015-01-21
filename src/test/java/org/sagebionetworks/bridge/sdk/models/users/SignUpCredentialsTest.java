package org.sagebionetworks.bridge.sdk.models.users;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SignUpCredentialsTest {
    @Test
    public void hashCodeEquals() {
        SignUpCredentials c1 = createSignUpCredentials("test");
        SignUpCredentials c2 = createSignUpCredentials("test");
        
        assertEquals(c1.hashCode(), c2.hashCode());
        assertTrue(c1.equals(c2));
    }
    
    @Test
    public void hashCodeEqualsUnequal() {
        SignUpCredentials c1 = createSignUpCredentials("test");
        SignUpCredentials c2 = createSignUpCredentials("test2");
        
        assertNotEquals(c1.hashCode(), c2.hashCode());
        assertFalse(c1.equals(c2));
    }
    
    private SignUpCredentials createSignUpCredentials(String name) {
        return new SignUpCredentials(name, name + "@email.com", "password");
    }
}
