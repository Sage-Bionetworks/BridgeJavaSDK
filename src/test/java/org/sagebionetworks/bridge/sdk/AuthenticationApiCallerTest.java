package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.models.UserSession;

public class AuthenticationApiCallerTest {

    @Test
    @Ignore
    public void garbageSignInFails() {
        String username = "d;fjasdl;kfjads";
        String password = "lalalalalala";
        try {
            AuthenticationApiCaller auth = AuthenticationApiCaller.valueOf(ClientProvider.valueOf());
            auth.signIn(username, password);
            fail("If we did not throw an error or exception, then something has gone wrong.");
        } catch (Exception e) {}
    }

    @Test
    public void canSignInAndOut() {
        ClientProvider provider = ClientProvider.valueOf();
        Config config = provider.getConfig();

        String username = config.get("admin.email");
        String password = config.get("admin.password");

        AuthenticationApiCaller auth = AuthenticationApiCaller.valueOf(provider);
        UserSession session = auth.signIn(username, password);
        assertNotNull("After signing in, session should not be  null.", session);

        session = auth.signOut(session);
        assertNull("After signing out, session token should be null.", session.getSessionToken());
    }
}
