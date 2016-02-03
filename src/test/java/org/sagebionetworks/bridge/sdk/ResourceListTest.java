package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Ignore;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.users.UserProfile;

import com.google.common.collect.Lists;

public class ResourceListTest {
    
    @Test
    @Ignore // This actually throws an internal error in EqualsVerifier, we hit a bug
    public void equalsContract() {
        EqualsVerifier.forClass(ResourceList.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void hashCodeEquals() {
        ResourceList<UserProfile> c1 = createProfileList("test");
        ResourceList<UserProfile> c2 = createProfileList("test");
        
        assertEquals(c1.hashCode(), c2.hashCode());
        assertTrue(c1.equals(c2));
    }
    
    @Test
    public void hashCodeEqualsUnequal() {
        ResourceList<UserProfile> c1 = createProfileList("test");
        ResourceList<UserProfile> c2 = createProfileList("test2");
        
        
        assertNotEquals(c1.hashCode(), c2.hashCode());
        assertFalse(c1.equals(c2));
    }
    
    private ResourceList<UserProfile> createProfileList(String email) {
        UserProfile profile = new UserProfile();
        profile.setEmail(email);
        List<UserProfile> list = Lists.newArrayList(profile);
        
        return new ResourceListImpl<UserProfile>(list, list.size());
    }

}
