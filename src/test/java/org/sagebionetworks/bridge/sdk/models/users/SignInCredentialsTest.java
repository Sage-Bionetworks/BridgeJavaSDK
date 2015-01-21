package org.sagebionetworks.bridge.sdk.models.users;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SignInCredentialsTest {
    @Test
    public void hashCodeEquals() {
        SignInCredentials c1 = createSignInCredentials("test");
        SignInCredentials c2 = createSignInCredentials("test");
        
        assertEquals(c1.hashCode(), c2.hashCode());
        assertTrue(c1.equals(c2));
    }
    
    @Test
    public void hashCodeEqualsUnequal() {
        SignInCredentials c1 = createSignInCredentials("test");
        SignInCredentials c2 = createSignInCredentials("test2");
        
        assertNotEquals(c1.hashCode(), c2.hashCode());
        assertFalse(c1.equals(c2));
    }
    
    private SignInCredentials createSignInCredentials(String name) {
        return new SignInCredentials(name, "password");
    }

}
