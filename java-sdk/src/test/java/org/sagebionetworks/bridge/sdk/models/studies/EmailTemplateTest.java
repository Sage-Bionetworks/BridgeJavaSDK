package org.sagebionetworks.bridge.sdk.models.studies;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.sagebionetworks.bridge.sdk.models.studies.EmailTemplate.MimeType;
import org.sagebionetworks.bridge.sdk.utils.BridgeUtils;

import com.fasterxml.jackson.databind.JsonNode;

import nl.jqno.equalsverifier.EqualsVerifier;

public class EmailTemplateTest {

    @Test
    public void equalsHashCode() {
        EqualsVerifier.forClass(EmailTemplate.class).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void canSerialize() throws Exception {
        EmailTemplate template = new EmailTemplate("Subject", "Body", MimeType.TEXT);
        
        String json = BridgeUtils.getMapper().writeValueAsString(template);
        JsonNode node = BridgeUtils.getMapper().readTree(json);
        
        assertEquals("Subject", node.get("subject").asText());
        assertEquals("Body", node.get("body").asText());
        
        EmailTemplate template2 = BridgeUtils.getMapper().readValue(json, EmailTemplate.class);
        assertEquals(template, template2);
    }

}
