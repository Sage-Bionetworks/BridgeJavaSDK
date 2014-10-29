package org.sagebionetworks.bridge.sdk;

import org.junit.After;
import org.junit.Before;

public class UserProfileApiCallerTest {

    private Session session;

    @Before
    public void before() {
        Config config = ClientProvider.getConfig();
        session = ClientProvider.signIn(config.getAdminCredentials());
    }

    @After
    public void after() {
        session.signOut();
    }
}
