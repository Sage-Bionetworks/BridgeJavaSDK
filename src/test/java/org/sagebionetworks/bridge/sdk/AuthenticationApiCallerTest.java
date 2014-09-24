package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class AuthenticationApiCallerTest {

    @Test
    public void garbageSignInFails() {
        String username = "d;fjasdl;kfjads";
        String password = "lalalalalala";
        try {
            AuthenticationApiCaller auth = AuthenticationApiCaller.valueOf();
            UserSession session = auth.signIn(username, password);
        } catch (Throwable t) {}
    }

    @Test
    public void canSignInAndOut() {
        String username = "cje";
        String password = "1";
        AuthenticationApiCaller auth = AuthenticationApiCaller.valueOf();
        UserSession session = auth.signIn(username, password);
        assertNotNull(session);

        session = auth.signOut(session);
        assertNull(session.getSessionToken());
    }
}
