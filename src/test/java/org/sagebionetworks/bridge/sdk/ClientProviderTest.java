package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.exceptions.EntityNotFoundException;
import org.sagebionetworks.bridge.sdk.models.users.EmailCredentials;
import org.sagebionetworks.bridge.sdk.models.users.SignInCredentials;

public class ClientProviderTest {

    @Test
    public void canAuthenticateAndCreateClientAndSignOut() {
        TestUser testUser = TestUserHelper.createAndSignInUser(ClientProviderTest.class, true);
        try {
            testUser.getSession().signOut();
            
            SignInCredentials credentials = new SignInCredentials(Tests.TEST_KEY, testUser.getUsername(), testUser.getPassword());
            Session session = ClientProvider.signIn(credentials);
            assertTrue(session.isSignedIn());

            UserClient client = session.getUserClient();
            assertNotNull(client);

            session.signOut();
            assertFalse(session.isSignedIn());
        } finally {
            testUser.signOutAndDeleteUser();
        }
    }
    
    @Test(expected = EntityNotFoundException.class)
    public void badStudyReturns404() {
        ClientProvider.requestResetPassword(new EmailCredentials("junk", "bridge-testing@sagebase.org"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void badEmailCredentialsReturnsException() {
        ClientProvider.requestResetPassword(new EmailCredentials(null, "bridge-testing@sagebase.org"));
    }
}
