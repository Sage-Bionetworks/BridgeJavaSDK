package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.models.UserSession;

public class AuthenticationApiCallerTest {

    private static ClientProvider provider;
    private static AuthenticationApiCaller authApi;

    @BeforeClass
    public static void initialSetup() {
        provider = ClientProvider.valueOf();
        authApi = AuthenticationApiCaller.valueOf(provider);
    }

    @Test
    public void signInNoCredentialsFailsWith404() {
        try {
            authApi.signIn("", "");
            fail("Sign In should throw an exception when given junk credentials.");
        } catch (BridgeServerException e) {
            assertTrue("HttpResponse indicates user not found.", e.getStatusCode() == 404);
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

    @Test
    public void canSignInAndOut() {
        Config config = provider.getConfig();
        String username = config.getAdminEmail();
        String password = config.getAdminPassword();

        UserSession session = authApi.signIn(username, password);
        assertNotNull("After signing in, session should not be  null.", session);

        session = authApi.signOut(session);
        assertNull("After signing out, session token should be null.", session.getSessionToken());
    }
}
