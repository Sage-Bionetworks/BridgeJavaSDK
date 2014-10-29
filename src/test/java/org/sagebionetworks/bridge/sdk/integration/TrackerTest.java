package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.ClientProvider;
import org.sagebionetworks.bridge.sdk.Config;
import org.sagebionetworks.bridge.sdk.Session;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.models.Tracker;

public class TrackerTest {

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

    @Test(expected = Exception.class)
    public void cannotGetIfUnauthorized() {
        session.signOut();
        user.getAllTrackers();
    }

    @Test
    public void successfullyGetTrackerList() {
        List<Tracker> trackers = user.getAllTrackers();
        assertNotNull("Trackers should not be null.", trackers);
        assertTrue("Trackers should have a non-zero size.", trackers.size() > 0);
    }

    @Test
    public void successfullyGetSchema() {
        List<Tracker> trackers = user.getAllTrackers();
        String schema = user.getSchema(trackers.get(0));
        assertNotNull("Schema from tracker should not be null.", schema);
    }
}
