package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.models.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.SignUpCredentials;

public class ConsentApiCallerTest {

    private ConsentApiCaller consentApi;
    private UserManagementApiCaller userManagementApi;

    private ClientProvider provider;
    private SignInCredentials adminSignIn;
    private String testEmail = "testtesting@sagebase.org";

    @Before
    public void before() {
        provider = ClientProvider.valueOf();

        // Sign in admin
        String adminEmail = provider.getConfig().getAdminEmail();
        String adminPassword = provider.getConfig().getAdminPassword();
        adminSignIn = SignInCredentials.valueOf().setUsername(adminEmail).setPassword(adminPassword);
        provider.signIn(adminSignIn);

        userManagementApi = UserManagementApiCaller.valueOf(provider);

        // Construct test user, which can only be done as admin
        String testUsername = "testUser";
        String testPassword = "p4ssw0rD";
        boolean consent = true;
        userManagementApi.createUser(SignUpCredentials.valueOf().setEmail(testEmail).setUsername(testUsername).setPassword(testPassword), consent);

        provider.signOut();

        // Sign in test user to test consent.
        SignInCredentials testSignIn = SignInCredentials.valueOf().setUsername(testEmail).setPassword(testPassword);
        provider.signIn(testSignIn);

        consentApi = ConsentApiCaller.valueOf(provider);
    }

    @After
    public void after() {
        provider.signOut();
        provider.signIn(adminSignIn);
        userManagementApi.deleteUser(testEmail);
    }

    @Test
    public void canToggleDataSharing() {
        try {
            consentApi.resumeDataSharing();
            consentApi.suspendDataSharing();
        } catch (Exception e) {
            fail("Should not throw an exception => test failed.");
        }

    }
}
