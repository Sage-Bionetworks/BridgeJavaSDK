package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.models.SignUpCredentials;

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
    
    @Test
    public void canCreateAndDeleteUser() {
        String username = TestUserHelper.makeUserName(UserManagementApiCallerTest.class);
        String email = username + "@sagebase.org";
        String password = "P4ssword";
        boolean consent = true;

        SignUpCredentials signUp = SignUpCredentials.valueOf().setUsername(username).setEmail(email).setPassword(password); 
        
        boolean result = userManagementApi.createUser(signUp, consent);
        assertTrue(result);

        // This is already done as part of deletion, and doesn't make sense separately because we're not 
        // supporting someone removing themself from a study.
        // result = userManagementApi.revokeAllConsentRecords(email);
        // assertTrue(result);

        result = userManagementApi.deleteUser(email);
        assertTrue(result);
    }
}
