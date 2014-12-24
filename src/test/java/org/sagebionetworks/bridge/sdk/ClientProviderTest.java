package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.models.users.SignInCredentials;

public class ClientProviderTest {
    private static final ClientProvider CLIENT_PROVIDER = new ClientProvider();

    private TestUser testUser;
    
    @Before
    public void before() {
        testUser = TestUserHelper.createAndSignInUser(ClientProviderTest.class, true);
    }
    
    @After
    public void after() {
        testUser.signOutAndDeleteUser();
    }
    
    @Test
    public void canAuthenticateAndCreateClientAndSignOut() {
        testUser.getSession().signOut();
        
        SignInCredentials credentials = new SignInCredentials(testUser.getUsername(), testUser.getPassword());
        Session session = CLIENT_PROVIDER.signIn(credentials);
        assertTrue(session.isSignedIn());

        UserClient client = session.getUserClient();
        assertNotNull(client);

        session.signOut();
        assertFalse(session.isSignedIn());
    }
    
}
