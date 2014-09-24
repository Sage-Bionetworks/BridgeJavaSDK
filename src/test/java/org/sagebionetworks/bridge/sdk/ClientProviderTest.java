package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class ClientProviderTest {

    @Test
    public void canAuthenticateAndCreateClientAndSignOut() {
        SignInCredentials signIn = SignInCredentials.valueOf();
        signIn.setUsername("cje").setPassword("1");

        ClientProvider provider = ClientProvider.valueOf(Version.V1);
        assertNotNull(provider);

        provider.authenticate(signIn);
        assertTrue(provider.isAuthenticated());

        BridgeUserClient client = provider.getClient();
        assertNotNull(client);

        provider.signOut();
        assertFalse(provider.isAuthenticated());
    }

    @Test
    public void cannotSignOutWhenNotAuthenticated() {
        ClientProvider provider = ClientProvider.valueOf(Version.V1);
        assertNotNull(provider);
        try {
            provider.signOut();
            fail("Signing out when not authenticated didn't throw an exception.");
        } catch (Exception e) {
            assertTrue(e instanceof IllegalStateException);
        }
    }
}
