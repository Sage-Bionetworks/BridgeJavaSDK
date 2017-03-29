package org.sagebionetworks.bridge.rest.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.sagebionetworks.bridge.rest.RestUtils;

public class ScheduledActivityTest {

    public static class ClientData {
        final String name;
        final boolean enabled;
        final int count;
        public ClientData(String name, boolean enabled, int count) {
            this.name = name;
            this.enabled = enabled;
            this.count = count;
        }
        public String getName() {
            return name;
        }
        public boolean getEnabled() {
            return enabled;
        }
        public int getCount() {
            return count;
        }
    }
    
    @Test
    public void canSerializeClientData() {
        ClientData clientData = new ClientData("Test user", true, 10);
        
        ScheduledActivity activity = new ScheduledActivity().clientData(clientData);
        String json = RestUtils.GSON.toJson(activity);
        ScheduledActivity deser = RestUtils.GSON.fromJson(json, ScheduledActivity.class);

        ClientData deserClientData = RestUtils.toType(deser.getClientData(), ClientData.class);
        
        assertEquals(clientData.getName(), deserClientData.getName());
        assertEquals(clientData.getCount(), deserClientData.getCount());
        assertTrue(deserClientData.getEnabled());
    }
    
    
}
