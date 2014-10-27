package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.models.SignUpCredentials;

public class UserManagementApiCallerTest {

    private ClientProvider provider;
    private TestUser testUser;
    private UserManagementApiCaller userManagementApi;

    @Before
    public void before() {
        provider = ClientProvider.valueOf();
        userManagementApi = UserManagementApiCaller.valueOf(provider);
        testUser = TestUserHelper.valueOf(provider).createAndSignInUser(UserManagementApiCallerTest.class, true, "admin");
    }
    
    @After
    public void after() {
        testUser.signOutAndDeleteUser();
    }
    
    @Test
    public void canCreateAndDeleteUser() {
        String username = TestUserHelper.valueOf(provider).makeUserName(UserManagementApiCallerTest.class);
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
