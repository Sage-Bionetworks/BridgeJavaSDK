package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class AuthenticationApiCallerTest {

    private AuthenticationApiCaller authApi;

    @Before
    public void before() {
        authApi = AuthenticationApiCaller.valueOf();
    }
    
    @Test
    public void signInNoCredentialsFailsWith404() {
        try {
            authApi.signIn("", "");
            fail("Sign In should throw an exception when given junk credentials.");
        } catch (InvalidEntityException e) {
            assertTrue("HttpResponse indicates user name and password are required.", e.getStatusCode() == 400);
        }
    }

    @Test
    public void signInGarbageCredentialsFailsWith404() {
        try {
            authApi.signIn("lalallalalalallalalalTESTTESTTEST", "password");
            fail("Sign In should throw an exception when given junk credentials.");
        } catch (BridgeServerException e) {
            assertTrue("HttpResponse indicates user not found.", e.getStatusCode() == 404);
        }
    }

}
