package org.sagebionetworks.bridge.sdk.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.TestUserHelper;
import org.sagebionetworks.bridge.sdk.TestUserHelper.TestUser;
import org.sagebionetworks.bridge.sdk.UserClient;
import org.sagebionetworks.bridge.sdk.models.Tracker;

public class TrackerTest {

    private TestUser user;
    
    @Before
    public void before() {
        user = TestUserHelper.createAndSignInUser(TrackerTest.class, true);
    }
    
    @After
    public void after() {
        user.signOutAndDeleteUser();
    }
    
    @Test
    public void returnsCorrectTrackerType() {
        UserClient client = user.getSession().getUserClient();
        
        List<Tracker> trackers = client.getAllTrackers();
        
        assertEquals("Type is BloodPressure", "BloodPressure", trackers.get(0).getType());
    }
    
    @Test
    public void canRetrieveASchemaForTracker() {
        UserClient client = user.getSession().getUserClient();
        
        List<Tracker> trackers = client.getAllTrackers();
        String schema = client.getTrackerSchema(trackers.get(0));
        
        assertTrue("Schema has been returned", schema.contains("systolic") && schema.contains("diastolic"));
    }

}
