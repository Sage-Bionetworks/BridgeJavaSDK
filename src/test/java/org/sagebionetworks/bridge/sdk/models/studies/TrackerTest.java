package org.sagebionetworks.bridge.sdk.models.studies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TrackerTest {
    @Test
    public void hashCodeEquals() throws Exception {
        Tracker tracker1 = createTracker("Name");
        Tracker tracker2 = createTracker("Name");
        assertEquals(tracker1.hashCode(), tracker2.hashCode());
        assertTrue(tracker1.equals(tracker2));
    }
    
    @Test
    public void hashCodeEqualsUnequal() throws Exception {
        Tracker tracker1 = createTracker("Name");
        Tracker tracker2 = createTracker("Name 2");
        
        assertNotEquals(tracker1.hashCode(), tracker2.hashCode());
        assertFalse(tracker1.equals(tracker2));
    }
    
    private Tracker createTracker(String name) throws Exception {
        String json = "{\"name\":\""+name+"\",\"type\":\"Tracker\",\"schemaUrl\":\"http://localhost:9000/\",\"identifier\":\"identity\"}";
        return new ObjectMapper().readValue(json, Tracker.class);
    }

}
