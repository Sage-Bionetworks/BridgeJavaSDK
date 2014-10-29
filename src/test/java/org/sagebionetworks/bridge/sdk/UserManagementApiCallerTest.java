package org.sagebionetworks.bridge.sdk;

import org.junit.After;
import org.junit.Before;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;

public class UserManagementApiCallerTest {

    private TestUser testUser;
    private UserManagementApiCaller userManagementApi;

    @Before
    public void before() {
        testUser = TestUserHelper.createAndSignInUser(UserManagementApiCallerTest.class, true, "admin");
        userManagementApi = UserManagementApiCaller.valueOf(testUser.getSession());
    }

    @After
    public void after() {
        testUser.signOutAndDeleteUser();
    }
}
