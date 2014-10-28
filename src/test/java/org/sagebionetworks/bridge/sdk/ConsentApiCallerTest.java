package org.sagebionetworks.bridge.sdk;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;

public class ConsentApiCallerTest {

    private ConsentApiCaller consentApi;

    private TestUser testUser;

    @Before
    public void before() {
        testUser = TestUserHelper.createAndSignInUser(ConsentApiCallerTest.class, true);
        consentApi = ConsentApiCaller.valueOf(testUser.getSession());
    }

    @After
    public void after() {
        testUser.signOutAndDeleteUser();
    }

    @Test
    public void canToggleDataSharing() {
        consentApi.resumeDataSharing();
        consentApi.suspendDataSharing();
    }
}
