package org.sagebionetworks.bridge.sdk;

import org.junit.After;
import org.junit.Before;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;

public class TrackerApiCallerTest {

    TestUser testUser;
    //Session session;

    @Before
    public void before() {
        testUser = TestUserHelper.createAndSignInUser(TrackerApiCallerTest.class, true);
    }

    @After
    public void after() {
        testUser.signOutAndDeleteUser();
    }
}
