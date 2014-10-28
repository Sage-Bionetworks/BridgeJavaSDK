package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.models.Tracker;

public class TrackerApiCallerTest {

    Session session;

    @Before
    public void before() {
        Config config = ClientProvider.getConfig();
        session = ClientProvider.signIn(config.getAccountCredentials());
    }

    @Test(expected = Exception.class)
    public void cannotGetIfUnauthorized() {
        session.signOut();
        TrackerApiCaller trackerApi = TrackerApiCaller.valueOf(session);
        trackerApi.getAllTrackers();
    }

    @Test
    public void successfullyGetTrackerList() {
        TrackerApiCaller trackerApi = TrackerApiCaller.valueOf(session);
        List<Tracker> trackers = trackerApi.getAllTrackers();
        assertNotNull("Trackers should not be null.", trackers);
        assertTrue("Trackers should have a non-zero size.", trackers.size() > 0);
    }

    @Test
    public void successfullyGetSchema() {
        TrackerApiCaller trackerApi = TrackerApiCaller.valueOf(session);
        List<Tracker> trackers = trackerApi.getAllTrackers();
        String schema = trackerApi.getSchema(trackers.get(0));
        assertNotNull("Schema from tracker should not be null.", schema);
    }
}
