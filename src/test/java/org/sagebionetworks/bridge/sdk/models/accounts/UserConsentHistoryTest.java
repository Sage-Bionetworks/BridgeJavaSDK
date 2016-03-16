package org.sagebionetworks.bridge.sdk.models.accounts;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.junit.Test;

import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.fasterxml.jackson.databind.JsonNode;

import nl.jqno.equalsverifier.EqualsVerifier;

public class UserConsentHistoryTest {
    
    @Test
    public void hashCodeEquals() {
        EqualsVerifier.forClass(UserConsentHistory.class).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void canSerialize() throws Exception {
        UserConsentHistory history = new UserConsentHistory(new SubpopulationGuid("guid"), 123L, "name",
                LocalDate.parse("2000-01-01"), "imageData", "image/png", 246L, new Long(444), true);        
        
        JsonNode node = Utilities.getMapper().valueToTree(history);
        assertEquals("guid", node.get("subpopulationGuid").asText());
        assertEquals(new DateTime(123L).withZone(DateTimeZone.UTC).toString(), 
                node.get("consentCreatedOn").asText());
        assertEquals("name", node.get("name").asText());
        assertEquals("2000-01-01", node.get("birthdate").asText());
        assertEquals("imageData", node.get("imageData").asText());
        assertEquals("imageMimeType", node.get("imageMimeType").asText());
        assertEquals(new DateTime(246L).withZone(DateTimeZone.UTC).toString(), 
                node.get("signedOn").asText());
        /*
        private final DateTime signedOn;
        private final DateTime withdrewOn;
        private final boolean hasSignedActiveConsent;
        */
        
        
        System.out.println(history);
    }
}
