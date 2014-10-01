package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.sagebionetworks.bridge.sdk.models.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.Tracker;

public class TrackerApiCallerTest {

    @Test
    public void cannotGetIfUnauthorized() {
        try {
            TrackerApiCaller trackerApi = TrackerApiCaller.valueOf(ClientProvider.valueOf());
            trackerApi.getAllTrackers();
            fail("Should not get to this point.");
        } catch (Throwable t) {}
    }

    @Test
    public void successfullyGetTrackerList() {
        ClientProvider provider = ClientProvider.valueOf();

        String adminEmail = provider.getConfig().getAdminEmail();
        String adminPassword = provider.getConfig().getAdminPassword();
        provider.signIn(SignInCredentials.valueOf(adminEmail, adminPassword));

        TrackerApiCaller trackerApi = TrackerApiCaller.valueOf(provider);
        List<Tracker> trackers = trackerApi.getAllTrackers();
        assertNotNull("Trackers should not be null.", trackers);
        assertTrue("Trackers should have a non-zero size.", trackers.size() > 0);
    }

    @Test
    public void successfullyGetSchema() {
        ClientProvider provider = ClientProvider.valueOf();

        String adminEmail = provider.getConfig().getAdminEmail();
        String adminPassword = provider.getConfig().getAdminPassword();
        provider.signIn(SignInCredentials.valueOf(adminEmail, adminPassword));

        TrackerApiCaller trackerApi = TrackerApiCaller.valueOf(provider);
        List<Tracker> trackers = trackerApi.getAllTrackers();
        String schema = trackerApi.getSchema(trackers.get(0));
        assertNotNull("Schema from tracker should not be null.", schema);
    }
}
