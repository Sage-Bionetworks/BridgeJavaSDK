package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.BridgeServerException;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.Config;
import org.sagebionetworks.bridge.sdk.Session;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.models.UserProfile;

public class UserProfileTest {

    private Session session;
    private UserClient user;

    @Before
    public void before() {
        Config config = ClientProvider.getConfig();
        session = ClientProvider.signIn(config.getAdminCredentials());
        user = session.getUserClient();
    }

    @After
    public void after() {
        session.signOut();
    }

    @Test
    public void cannotGetProfileWhenUnauthorized() {
        session.signOut();
        try {
            user.getProfile();
            fail("Should throw exception before you get here.");
        } catch (Throwable t) {
            assertTrue("The exception thrown was a BridgeServerException.",
                    t.getClass().equals(BridgeServerException.class));
        }
    }

    @Test
    public void cannotUpdateProfileWhenUnauthorized() {
        session.signOut();

        UserProfile profile = UserProfile.valueOf();
        profile.setFirstName("first name")
               .setLastName("last name")
               .setUsername("fake-username")
               .setEmail("fake-email@sagebase.org");

        try {
            user.saveProfile(profile);
            fail("Should throw exception before you get here.");
        } catch (Throwable t) {
            assertTrue("The exception thrown was a BridgeServerException.",
                    t.getClass().equals(BridgeServerException.class));
        }
    }

    @Test
    public void canRetrieveAndUpdateProfile() {
        UserProfile profile = user.getProfile();
        assertNotNull("Profile should not be null.", profile);

        String oldFirstName = profile.getFirstName();
        String oldLastName = profile.getLastName();
        String newFirstName = "testFirst";
        String newLastName = "testLast";

        // Ensure profile changes were saved on server.
        profile.setFirstName(newFirstName).setLastName(newLastName);
        user.saveProfile(profile);
        profile = user.getProfile();
        assertEquals("Updated profile first name should match one sent.", profile.getFirstName(), newFirstName);
        assertEquals("Updated profile last name should match one sent.", profile.getLastName(), newLastName);

        // Change back to original names.
        profile.setFirstName(oldFirstName).setLastName(oldLastName);
        user.saveProfile(profile);
    }
}
