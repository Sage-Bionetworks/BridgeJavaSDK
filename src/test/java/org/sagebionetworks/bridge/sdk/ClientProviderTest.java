package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ClientProviderTest {

    @Test
    public void canAuthenticateAndCreateClientAndSignOut() {
        Config config = ClientProvider.getConfig();
        Session session = ClientProvider.signIn(config.getAccountCredentials());
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
