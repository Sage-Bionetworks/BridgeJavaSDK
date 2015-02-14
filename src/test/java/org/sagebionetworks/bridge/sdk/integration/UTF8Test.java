package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.Session;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.models.users.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.users.UserProfile;

public class UTF8Test {
    
    private TestUser testUser;
    
    @Before
    public void before() {
        testUser = TestUserHelper.createAndSignInUser(UTF8Test.class, true);
    }
    
    @After
    public void after() {
        testUser.signOutAndDeleteUser();
    }

    @Test
    public void canSaveAndRetrieveDataStoredInRedis() {
        UserClient client = testUser.getSession().getUserClient();
        
        UserProfile profile = client.getProfile();
        profile.setFirstName("☃");
        // I understand from the source of this text that it is actualy UTF-16.
        // It should still work.
        profile.setLastName("지구상의　３대　극지라　불리는");
        client.saveProfile(profile); // should be update
        
        // Force a refresh of the Redis session cache.
        testUser.getSession().signOut();
        Session session = ClientProvider.signIn(new SignInCredentials(testUser.getEmail(), testUser.getPassword()));
        
        profile = session.getUserClient().getProfile();
        assertEquals("☃", profile.getFirstName());
        assertEquals("지구상의　３대　극지라　불리는", profile.getLastName());
    }
    
}
