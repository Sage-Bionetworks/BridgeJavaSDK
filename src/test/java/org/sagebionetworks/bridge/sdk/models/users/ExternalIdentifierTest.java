package org.sagebionetworks.bridge.sdk.models.users;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.fasterxml.jackson.databind.JsonNode;

public class ExternalIdentifierTest {
    
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ExternalIdentifier.class).allFieldsShouldBeUsed().verify();
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void cannotConstructNullIdentifier() {
        new ExternalIdentifier(null, false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void cannotConstructEmptyIdentifier() {
        new ExternalIdentifier("", false);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void cannotConstructBlankIdentifier() {
        new ExternalIdentifier("   ", false);
    }
    
    @Test
    public void canSerialize() throws Exception {
        ExternalIdentifier identifier = new ExternalIdentifier("ABC", true);
        
        JsonNode node = Utilities.getMapper().valueToTree(identifier);
        assertEquals("ABC", node.get("identifier").asText());
        assertTrue(node.get("assigned").asBoolean());
        
        ExternalIdentifier deser = Utilities.getMapper().readValue(node.toString(), ExternalIdentifier.class);
        assertEquals(identifier, deser);
    }
    
}
