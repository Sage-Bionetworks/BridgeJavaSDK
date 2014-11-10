package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.exceptions.ConsentRequiredException;
import org.sagebionetworks.bridge.sdk.models.users.ConsentSignature;

public class ConsentTest {

    private TestUser testUser;

    @Before
    public void before() {
        testUser = TestUserHelper.createAndSignInUser(ConsentTest.class, true);
    }

    @After
    public void after() {
        testUser.signOutAndDeleteUser();
    }

    @Test
    public void canToggleDataSharing() {
        UserClient client = testUser.getSession().getUserClient();

        assertTrue("Initially sharing data", testUser.getSession().isDataSharing());
        client.suspendDataSharing();
        assertFalse("Not sharing data", testUser.getSession().isDataSharing());
        client.resumeDataSharing();
        assertTrue("Sharing data", testUser.getSession().isDataSharing());
    }

    @Test
    public void signedInUserMustGiveConsent() {
        TestUser user = TestUserHelper.createAndSignInUser(AuthenticationTest.class, false);
        try {
            UserClient client = user.getSession().getUserClient();
            assertFalse("User has not consented", user.getSession().isConsented());
            try {
                client.getSchedules();
                fail("Should have required consent.");
            } catch(ConsentRequiredException e) {
                assertEquals("Exception is a 412 Precondition Failed", 412, e.getStatusCode());
            }
            client.consentToResearch(ConsentSignature.valueOf(user.getUsername(), DateTime.now()));
            assertTrue("User has not consented", user.getSession().isConsented());
            client.getSchedules();
        } finally {
            user.signOutAndDeleteUser();
        }
    }
}
