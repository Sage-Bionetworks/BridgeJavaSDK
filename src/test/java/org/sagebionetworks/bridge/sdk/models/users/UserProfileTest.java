package org.sagebionetworks.bridge.sdk.models.users;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;
import org.sagebionetworks.bridge.sdk.Utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public class UserProfileTest {
    
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(UserProfile.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void roundtripSerializationWorks() throws Exception {
        UserProfile profile = new UserProfile();
        profile.setFirstName("first name");
        profile.setLastName("last name");
        profile.setEmail("email@email.com");
        profile.setUsername("username");
        profile.setAttribute("can_be_recontacted", "true");
        
        JsonNode node = marshalUnmarshal(profile);
        
        assertTrue(node.has("firstName"));
        assertTrue(node.has("lastName"));
        assertTrue(node.has("username"));
        assertTrue(node.has("email"));
        assertTrue(node.has("can_be_recontacted"));

        assertEquals("first name", node.get("firstName").asText());
        assertEquals("last name", node.get("lastName").asText());
        assertEquals("username", node.get("username").asText());
        assertEquals("email@email.com", node.get("email").asText());
        assertEquals("true", node.get("can_be_recontacted").asText());
    }
    
    @Test
    public void attributesSetterGetterWorks() {
        UserProfile profile = new UserProfile();
        profile.setAttribute(null, null);
        assertTrue(profile.getAttributes().isEmpty());
        
        profile.setAttribute("test", null);
        assertTrue(profile.getAttributes().isEmpty());
        
        profile.setAttribute("test", "bar");
        assertEquals("bar", profile.getAttribute("test"));
        
        profile.setAttribute("test", "baz");
        assertEquals("baz", profile.getAttribute("test"));
        
        profile.removeAttribute(null);
        assertEquals("baz", profile.getAttribute("test"));
        
        profile.removeAttribute("test");
        assertTrue(profile.getAttributes().isEmpty());
    }
    
    @Test
    public void attributesDontConflictWithProperties() throws Exception {
        // You can't rewrite a JSON property from the Java fields throught the attributes map
        try {
            UserProfile profile = new UserProfile();
            profile.setUsername("username1");
            profile.setAttribute("username", "username2");
            fail("Should have thrown exception");
        } catch(IllegalArgumentException e) {
            assertEquals("Attribute 'username' conflicts with existing Java field name.", e.getMessage());
        }
    }

    private JsonNode marshalUnmarshal(UserProfile profile) throws JsonProcessingException, IOException {
        String json = Utilities.getMapper().writeValueAsString(profile);
        return Utilities.getMapper().readTree(json);
    }
}
