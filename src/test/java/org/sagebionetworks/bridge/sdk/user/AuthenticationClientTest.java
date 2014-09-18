package org.sagebionetworks.bridge.sdk.user;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class AuthenticationClientTest {

    @Test
    public void garbageSignInFails() {
        String username = "d;fjasdl;kfjads";
        String password = "lalalalalala";
        try {
            UserSession session = AuthenticationClient.signIn(username, password);
            assertNull(session);
        } catch (Exception e) {}
    }
    
    @Test
    public void canSignInAndOut() {
        String username = "cje";
        String password = "1";
        UserSession session = AuthenticationClient.signIn(username, password);
        assertNotNull(session);

        session = AuthenticationClient.signOut(session);
        assertNull(session.getSessionToken());
    }
}
