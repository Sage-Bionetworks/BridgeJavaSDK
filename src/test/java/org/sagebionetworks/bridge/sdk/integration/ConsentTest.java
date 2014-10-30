package org.sagebionetworks.bridge.sdk.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.UserClient;

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

        client.suspendDataSharing();
        client.resumeDataSharing();
    }
}
