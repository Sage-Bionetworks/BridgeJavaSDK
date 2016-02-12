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
import org.sagebionetworks.bridge.sdk.models.users.SignUpByAdmin;

public class UserManagementTest {

    private Session session;
    private AdminClient admin;

    @Before
    public void before() {
        Config config = ClientProvider.getConfig();
        session = ClientProvider.signIn(config.getAdminCredentials());
        admin = session.getAdminClient();
    }

    @After
    public void after() {
        session.signOut();
    }

    @Test
    public void canCreateAndSignOutAndDeleteUser() {
        String email = TestUserHelper.makeEmail(UserManagementTest.class);
        String password = "P4ssword";
        boolean consent = true;

        SignUpByAdmin signUp = new SignUpByAdmin(email, password, null, consent);

        boolean result = admin.createUser(signUp);
        assertTrue(result);

        result = admin.signOutUser(email);
        assertTrue(result);

        result = admin.deleteUser(email);
        assertTrue(result);
    }

}
