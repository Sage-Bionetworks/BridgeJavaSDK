package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.sagebionetworks.bridge.sdk.models.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.UserProfile;

public class UserProfileApiCallerTest {

    @Test
    public void cannotGetProfileWhenUnauthorized() {
        UserProfileApiCaller profileApi = UserProfileApiCaller.valueOf(ClientProvider.valueOf());
        try {
            profileApi.getProfile();
            fail("Should throw exception before you get here.");
        } catch (Throwable t) {
            assertTrue("The exception thrown was a BridgeServerException.",
                    t.getClass().equals(BridgeServerException.class));
        }
    }

    @Test
    public void cannotUpdateProfileWhenUnauthorized() {
        UserProfile profile = UserProfile.valueOf();
        profile.setFirstName("first name")
               .setLastName("last name")
               .setUsername("fake-username")
               .setEmail("fake-email@sagebase.org");

        UserProfileApiCaller profileApi = UserProfileApiCaller.valueOf(ClientProvider.valueOf());
        try {
            profileApi.updateProfile(profile);
            fail("Should throw exception before you get here.");
        } catch (Throwable t) {
            assertTrue("The exception thrown was a BridgeServerException.",
                    t.getClass().equals(BridgeServerException.class));
        }
    }

    @Test
    public void canRetrieveAndUpdateProfile() {
        ClientProvider provider = ClientProvider.valueOf();

        Config conf = provider.getConfig();
        SignInCredentials signIn = SignInCredentials.valueOf()
                .setUsername(conf.getAdminEmail())
                .setPassword(conf.getAdminPassword());
        provider.signIn(signIn);

        UserProfileApiCaller profileApi = UserProfileApiCaller.valueOf(provider);
        UserProfile profile = profileApi.getProfile();
        assertNotNull("Profile should not be null.", profile);

        String oldFirstName = profile.getFirstName();
        String oldLastName = profile.getLastName();
        String newFirstName = "testFirst";
        String newLastName = "testLast";

        // Ensure profile changes were saved on server.
        profile.setFirstName(newFirstName).setLastName(newLastName);
        profileApi.updateProfile(profile);
        profile = profileApi.getProfile();
        assertEquals("Updated profile first name should match one sent.", profile.getFirstName(), newFirstName);
        assertEquals("Updated profile last name should match one sent.", profile.getLastName(), newLastName);

        // Change back to original names.
        profile.setFirstName(oldFirstName).setLastName(oldLastName);
        profileApi.updateProfile(profile);
    }
}
