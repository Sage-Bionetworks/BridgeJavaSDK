package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.sagebionetworks.bridge.IntegrationSmokeTest;
import org.sagebionetworks.bridge.sdk.DeveloperClient;
import org.sagebionetworks.bridge.sdk.Roles;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.models.studies.Study;
import org.sagebionetworks.bridge.sdk.models.users.DataGroups;
import org.sagebionetworks.bridge.sdk.models.users.ExternalIdentifier;
import org.sagebionetworks.bridge.sdk.models.users.UserProfile;

import com.google.common.collect.Sets;

@Category(IntegrationSmokeTest.class)
public class UserProfileTest {

    private static final Set<String> DATA_GROUPS = Sets.newHashSet("sdk-int-1","sdk-int-2");
    private TestUser developer;

    @Before
    public void before() {
        developer = TestUserHelper.createAndSignInUser(UserProfileTest.class, true, Roles.DEVELOPER);
        
        DeveloperClient devClient = developer.getSession().getDeveloperClient();
        Study study = devClient.getStudy();
        Set<String> dataGroups = study.getDataGroups();
        if (!dataGroups.containsAll(DATA_GROUPS)) {
            dataGroups.addAll(DATA_GROUPS);
            devClient.updateStudy(study);
        }
    }

    @After
    public void after() {
        developer.signOutAndDeleteUser();
    }

    @Test
    public void canUpdateProfile() throws Exception {
        final UserClient client = developer.getSession().getUserClient();

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
        final UserClient client = developer.getSession().getUserClient();
        
        client.addExternalUserIdentifier(new ExternalIdentifier("ABC-123-XYZ"));
    }
    
    @Test
    public void canUpdateDataGroups() throws Exception {
        final UserClient client = developer.getSession().getUserClient();
        
        DataGroups dataGroups = new DataGroups();
        dataGroups.setDataGroups(Sets.newHashSet("sdk-int-1", "sdk-int-2"));
        
        client.updateDataGroups(dataGroups);
        
        DataGroups newGroup = client.getDataGroups();
        assertEquals(Sets.newHashSet("sdk-int-1","sdk-int-2"), newGroup.getDataGroups());
        
        client.updateDataGroups(new DataGroups());
        newGroup = client.getDataGroups();
        assertEquals(0, newGroup.getDataGroups().size());
    }

}
