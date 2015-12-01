package org.sagebionetworks.bridge.sdk.models.studies;

import static org.junit.Assert.assertEquals;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

import org.sagebionetworks.bridge.sdk.models.studies.EmailTemplate.MimeType;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.fasterxml.jackson.databind.JsonNode;

public class EmailTemplateTest {

    @Test
    public void equalsHashCode() {
        EqualsVerifier.forClass(EmailTemplate.class).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void canSerialize() throws Exception {
        EmailTemplate template = new EmailTemplate("Subject", "Body", MimeType.TEXT);
        
        String json = Utilities.getMapper().writeValueAsString(template);
        JsonNode node = Utilities.getMapper().readTree(json);
        
        assertEquals("Subject", node.get("subject").asText());
        assertEquals("Body", node.get("body").asText());
        
        EmailTemplate template2 = Utilities.getMapper().readValue(json, EmailTemplate.class);
        assertEquals(template, template2);
    }

}
