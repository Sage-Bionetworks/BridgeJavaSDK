package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.sagebionetworks.bridge.sdk.models.UserSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserSessionTest {

    private static ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Test
    public void canConstructUserSessionFromJson() {
        String username = "test1";
        String sessionToken = "se55i0n-t0k3n";
        boolean authenticated = true;
        boolean consented = false;
        boolean dataSharing = true;
        String json = "{\"username\":\"" + username + "\",\"sessionToken\":\"" + sessionToken + "\",\"authenticated\":"
                + authenticated + ",\"consented\":" + consented + ",\"dataSharing\":" + dataSharing + "}";

        UserSession session = null;
        try {
            session = mapper.readValue(json, UserSession.class);
        } catch (IOException e) {
            fail("Something went wrong with converting the JSON string into a UserSession.");
        }
        assertNotNull(session);
        assertEquals(session.getSessionToken(), sessionToken);
        assertEquals(session.getUsername(), username);
        assertEquals(session.isAuthenticated(), authenticated);
        assertEquals(session.isConsented(), consented);
        assertEquals(session.isDataSharing(), dataSharing);

        try {
            assertEquals(json, mapper.writeValueAsString(session));
        } catch (JsonProcessingException e) {
            fail("Couldn't write session into a string.");
        }
    }
}
