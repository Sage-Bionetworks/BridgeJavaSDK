package org.sagebionetworks.bridge.sdk.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.Config;
import org.sagebionetworks.bridge.sdk.Session;
import org.sagebionetworks.bridge.sdk.UserClient;

public class ConsentTest {

    private Session session;
    private UserClient user;

    @Before
    public void before() {
        Config config = ClientProvider.getConfig();
        session = ClientProvider.signIn(config.getAdminCredentials());
        user = session.getUserClient();
    }

    @After
    public void after() {
        session.signOut();
    }

    @Test
    public void canToggleDataSharing() {
        user.resumeDataSharing();
        user.suspendDataSharing();
    }
}
