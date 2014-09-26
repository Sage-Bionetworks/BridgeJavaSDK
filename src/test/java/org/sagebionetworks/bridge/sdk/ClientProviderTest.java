package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.sagebionetworks.bridge.sdk.models.SignInCredentials;

public class ClientProviderTest {

    @Test
    public void canAuthenticateAndCreateClientAndSignOut() {
        SignInCredentials signIn = SignInCredentials.valueOf("cje", "1");

        ClientProvider provider = ClientProvider.valueOf(HostName.getDev());
        assertNotNull(provider);

        provider.signIn(signIn);
        assertTrue(provider.isSignedIn());

        BridgeUserClient client = provider.getClient();
        assertNotNull(client);

        provider.signOut();
        assertFalse(provider.isSignedIn());
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
