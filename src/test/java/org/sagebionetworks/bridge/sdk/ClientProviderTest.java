package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.models.SignInCredentials;

public class ClientProviderTest {

    @Test
    public void canAuthenticateAndCreateClientAndSignOut() {
        TestUser testUser = TestUserHelper.createAndSignInUser(ClientProviderTest.class, true);
        testUser.getSession().signOut();
        
        SignInCredentials credentials = SignInCredentials.valueOf().setUsername(testUser.getUsername())
                .setPassword(testUser.getPassword());
        Session session = ClientProvider.signIn(credentials);
        assertTrue(session.isSignedIn());

        UserClient client = session.getUserClient();
        assertNotNull(client);

        session.signOut();
        assertFalse(session.isSignedIn());
    }

    /* It's okay for this to silently "pass" even though there's nothing to do.
    @Test
    public void cannotSignOutWhenNotAuthenticated() {
        ClientProvider provider = ClientProvider.valueOf(HostName.getDev());
        assertNotNull(provider);
        try {
            provider.signOut();
            fail("Signing out when not authenticated didn't throw an exception.");
        } catch (Exception e) {
            assertTrue(e instanceof IllegalStateException);
        }
    }*/
}
