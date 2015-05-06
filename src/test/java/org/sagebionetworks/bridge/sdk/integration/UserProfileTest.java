package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.sagebionetworks.bridge.IntegrationSmokeTest;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.models.users.ExternalIdentifier;
import org.sagebionetworks.bridge.sdk.models.users.UserProfile;

@Category(IntegrationSmokeTest.class)
public class UserProfileTest {

    private TestUser user;

    @Before
    public void before() {
        user = TestUserHelper.createAndSignInUser(UserProfileTest.class, true);
    }

    @After
    public void after() {
        user.signOutAndDeleteUser();
    }

    @Test
    public void canUpdateProfile() throws Exception {
        final UserClient client = user.getSession().getUserClient();

        UserProfile profile = client.getProfile();
        profile.setFirstName("Davey");
        profile.setLastName("Crockett");
        profile.setAttribute("can_be_recontacted", "true");

        client.saveProfile(profile);

        profile = client.getProfile();

        assertEquals("First name updated", "Davey", profile.getFirstName());
        assertEquals("Last name updated", "Crockett", profile.getLastName());
        assertEquals("Attribute set", "true", profile.getAttribute("can_be_recontacted"));
    }
    
    @Test
    public void canAddExternalIdentifier() throws Exception {
        final UserClient client = user.getSession().getUserClient();
        
        client.addExternalUserIdentifier(new ExternalIdentifier("ABC-123-XYZ"));
    }

}
