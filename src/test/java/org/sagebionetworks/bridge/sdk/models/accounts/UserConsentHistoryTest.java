package org.sagebionetworks.bridge.sdk.models.accounts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        DateTime consentCreatedOn = new DateTime(123L).withZone(DateTimeZone.UTC);
        
        DateTime signedOn = new DateTime(246L).withZone(DateTimeZone.UTC);
        
        DateTime withdrewOn = new DateTime(444L).withZone(DateTimeZone.UTC);
        
        UserConsentHistory history = new UserConsentHistory(new SubpopulationGuid("guid"), consentCreatedOn, "name",
                LocalDate.parse("2000-01-01"), "imageData", "image/png", signedOn, withdrewOn, true);
        
        JsonNode node = Utilities.getMapper().valueToTree(history);
        assertEquals("guid", node.get("subpopulationGuid").asText());
        assertEquals(consentCreatedOn.toString(), node.get("consentCreatedOn").asText());
        assertEquals("name", node.get("name").asText());
        assertEquals("2000-01-01", node.get("birthdate").asText());
        assertEquals("imageData", node.get("imageData").asText());
        assertEquals("image/png", node.get("imageMimeType").asText());
        assertEquals(signedOn.toString(), node.get("signedOn").asText());
        assertEquals(withdrewOn.toString(), node.get("withdrewOn").asText());
        assertTrue(node.get("hasSignedActiveConsent").asBoolean());
        
        UserConsentHistory deserHistory = Utilities.getMapper().treeToValue(node, UserConsentHistory.class);
        assertEquals(history, deserHistory);
    }
}
