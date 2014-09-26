package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

public class AuthenticationApiCallerTest {

    @Test
    public void garbageSignInFails() {
        String username = "d;fjasdl;kfjads";
        String password = "lalalalalala";
        try {
            AuthenticationApiCaller auth = AuthenticationApiCaller.valueOf(ClientProvider.valueOf());
            auth.signIn(username, password);
            fail("If we did not throw an error or exception, then something has gone wrong.");
        } catch (Throwable t) {}
    }

    @Test
    public void canSignInAndOut() {
        ClientProvider provider = ClientProvider.valueOf(HostName.getDev());
        Config config = provider.getConfig();

        String username = config.getAdminEmail();
        String password = config.getAdminPassword();

        AuthenticationApiCaller auth = AuthenticationApiCaller.valueOf(provider);
        UserSession session = auth.signIn(username, password);
        assertNotNull(session);

        session = auth.signOut(session);
        assertNull(session.getSessionToken());
    }
}
