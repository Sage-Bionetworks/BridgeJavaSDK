package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.AdminClient;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.Config;
import org.sagebionetworks.bridge.sdk.Session;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.models.users.SignUpCredentials;

public class UserManagementTest {
    private static final ClientProvider CLIENT_PROVIDER = new ClientProvider();

    private Session session;
    private AdminClient admin;

    @Before
    public void before() {
        Config config = CLIENT_PROVIDER.getConfig();
        session = CLIENT_PROVIDER.signIn(config.getAdminCredentials());
        admin = session.getAdminClient();
    }

    @After
    public void after() {
        session.signOut();
    }

    @Test
    public void canCreateAndDeleteUser() {
        String username = TestUserHelper.makeUserName(UserManagementTest.class);
        String email = username + "@sagebase.org";
        String password = "P4ssword";
        boolean consent = true;

        SignUpCredentials signUp = new SignUpCredentials(username, email, password);

        boolean result = admin.createUser(signUp, null, consent);
        assertTrue(result);

        // This is already done as part of deletion, and doesn't make sense separately because we're not
        // supporting someone removing themself from a study.
        // result = userManagementApi.revokeAllConsentRecords(email);
        // assertTrue(result);

        result = admin.deleteUser(email);
        assertTrue(result);
    }

}
