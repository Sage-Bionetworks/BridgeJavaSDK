package org.sagebionetworks.bridge.sdk.models.users;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UserProfileTest {
    @Test
    public void hashCodeEquals() {
        UserProfile profile1 = createUserProfile("test");
        UserProfile profile2 = createUserProfile("test");
        
        assertEquals(profile1.hashCode(), profile2.hashCode());
        assertTrue(profile1.equals(profile2));
    }
    
    @Test
    public void hashCodeEqualsUnequal() {
        UserProfile profile1 = createUserProfile("test");
        UserProfile profile2 = createUserProfile("test2");
        
        assertNotEquals(profile1.hashCode(), profile2.hashCode());
        assertFalse(profile1.equals(profile2));
    }
    
    private UserProfile createUserProfile(String name) {
        UserProfile profile = new UserProfile();
        profile.setEmail("test@test.com");
        profile.setPhone("123-456-7890");
        profile.setUsername("testname");
        profile.setFirstName(name);
        profile.setLastName("Tester");
        return profile;
    }

}
