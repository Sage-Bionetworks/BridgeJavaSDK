package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class UserSessionTest {

    private static ObjectMapper mapper = Utilities.getMapper();

    @Test
    public void canConstructUserSessionFromJson() {
        ObjectNode node = mapper.createObjectNode();
        node.put("sessionToken", "se55i0n-t0k3n");
        node.put("authenticated", true);
        node.put("consented", false);
        node.put("dataSharing", true);
        node.put("sharingScope", "no_sharing");

        String json = null;
        try {
            json = mapper.writeValueAsString(node);
        } catch (IOException e) {
            fail("Something went wrong with converting JSON node into a string.");
        }

        UserSession session = null;
        try {
            session = mapper.readValue(json, UserSession.class);
        } catch (IOException e) {
            fail("Something went wrong with converting the JSON string into a UserSession.");
        }
        assertNotNull(session);
        assertEquals(session.getSessionToken(), node.get("sessionToken").asText());
        assertEquals(session.isAuthenticated(), node.get("authenticated").asBoolean());
        assertEquals(session.isConsented(), node.get("consented").asBoolean());
        assertEquals(session.getSharingScope().name().toLowerCase(), node.get("sharingScope").asText());
        // We don't pick this up. It's not really necessary, it's there for 100% backwards compatability.
        assertEquals(true, node.get("dataSharing").asBoolean());
    }
}
