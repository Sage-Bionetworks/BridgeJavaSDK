package org.sagebionetworks.bridge.sdk.models.accounts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.sagebionetworks.bridge.sdk.utils.BridgeUtils;

import com.fasterxml.jackson.databind.JsonNode;

import nl.jqno.equalsverifier.EqualsVerifier;

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
        
        JsonNode node = BridgeUtils.getMapper().valueToTree(identifier);
        assertEquals("ABC", node.get("identifier").asText());
        assertTrue(node.get("assigned").asBoolean());
        
        ExternalIdentifier deser = BridgeUtils.getMapper().readValue(node.toString(), ExternalIdentifier.class);
        assertEquals(identifier, deser);
    }
    
}
