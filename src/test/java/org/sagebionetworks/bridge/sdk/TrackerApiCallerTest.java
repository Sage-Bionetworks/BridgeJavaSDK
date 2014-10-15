package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.models.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.Tracker;

public class TrackerApiCallerTest {

    ClientProvider provider;

    @Before
    public void before() {
        if (provider == null) {
            provider = ClientProvider.valueOf();

            Config conf = provider.getConfig();
            SignInCredentials signIn = SignInCredentials.valueOf()
                    .setUsername(conf.get("admin.email"))
                    .setPassword(conf.get("admin.password"));
            provider.signIn(signIn);
        }
    }

    @Test
    public void cannotGetIfUnauthorized() {
        provider.signOut();
        try {
            TrackerApiCaller trackerApi = TrackerApiCaller.valueOf(ClientProvider.valueOf());
            trackerApi.getAllTrackers();
            fail("Should not get to this point.");
        } catch (Throwable t) {}
        provider = null; // make sure it gets created again in another test.
    }

    @Test
    public void successfullyGetTrackerList() {
        TrackerApiCaller trackerApi = TrackerApiCaller.valueOf(provider);
        List<Tracker> trackers = trackerApi.getAllTrackers();
        assertNotNull("Trackers should not be null.", trackers);
        assertTrue("Trackers should have a non-zero size.", trackers.size() > 0);
    }

    @Test
    public void successfullyGetSchema() {
        TrackerApiCaller trackerApi = TrackerApiCaller.valueOf(provider);
        List<Tracker> trackers = trackerApi.getAllTrackers();
        String schema = trackerApi.getSchema(trackers.get(0));
        assertNotNull("Schema from tracker should not be null.", schema);
    }
}
