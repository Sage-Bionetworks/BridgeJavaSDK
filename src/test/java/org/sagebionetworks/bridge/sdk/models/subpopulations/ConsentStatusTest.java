package org.sagebionetworks.bridge.sdk.models.subpopulations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ConsentStatusTest {
    
    private static final ConsentStatus REQUIRED_SIGNED_CURRENT = new ConsentStatus("Name1", "foo1", true, true, true);
    private static final ConsentStatus REQUIRED_SIGNED_OBSOLETE = new ConsentStatus("Name1", "foo2", true, true, false);
    private static final ConsentStatus OPTIONAL_SIGNED_CURRENT = new ConsentStatus("Name1", "foo3", false, true, true);
    private static final ConsentStatus REQUIRED_UNSIGNED = new ConsentStatus("Name1", "foo4", true, false, false);
    private static final ConsentStatus OPTIONAL_UNSIGNED = new ConsentStatus("Name1", "foo5", false, false, false);
    
    private Map<SubpopulationGuid,ConsentStatus> statuses;
    
    @Before
    public void before() {
        statuses = Maps.newHashMap();
    }
    
    @Test
    public void hashCodeEquals() {
        EqualsVerifier.forClass(ConsentStatus.class).allFieldsShouldBeUsed().verify(); 
    }
    
    // Will be stored as JSON in the the session, via the User object, so it must serialize.
    @Test
    public void canSerialize() throws Exception {
        ConsentStatus status = new ConsentStatus("Name", "GUID", true, true, true);

        String json = Utilities.getMapper().writeValueAsString(status);
        JsonNode node = Utilities.getMapper().readTree(json);
        
        assertEquals("Name", node.get("name").asText());
        assertEquals("GUID", node.get("subpopulationGuid").asText());
        assertTrue(node.get("required").asBoolean());
        assertTrue(node.get("consented").asBoolean());
        assertTrue(node.get("mostRecentConsent").asBoolean());
        
        ConsentStatus status2 = Utilities.getMapper().readValue(json, ConsentStatus.class);
        assertEquals(status, status2);
    }

    @Test
    public void isUserConsentedEmpy() {
        assertFalse(ConsentStatus.isUserConsented(statuses));
    }
    
    @Test
    public void isUserConsentedUnsigedRequiredWithSignedOptional() {
        add(REQUIRED_UNSIGNED);
        add(REQUIRED_SIGNED_CURRENT);
        add(OPTIONAL_SIGNED_CURRENT);
        assertFalse(ConsentStatus.isUserConsented(statuses));
    }
    
    @Test
    public void isUserConsentedUnsigedRequiredWithUnsignedOptional() {
        add(REQUIRED_SIGNED_CURRENT);
        add(REQUIRED_UNSIGNED);
        add(OPTIONAL_UNSIGNED);
        assertFalse(ConsentStatus.isUserConsented(statuses));
    }
    
    @Test
    public void isUserConsentedAllSigned() {
        add(REQUIRED_SIGNED_CURRENT);
        add(REQUIRED_SIGNED_OBSOLETE);
        add(OPTIONAL_UNSIGNED);
        assertTrue(ConsentStatus.isUserConsented(statuses));
    }

    @Test
    public void isConsentCurrentEmpty() {
        assertFalse(ConsentStatus.isConsentCurrent(statuses));
    }
    
    @Test
    public void isConsentCurrentOneRequiredObsolete() {
        add(REQUIRED_SIGNED_CURRENT);
        add(REQUIRED_SIGNED_OBSOLETE);
        add(OPTIONAL_UNSIGNED);
        assertFalse(ConsentStatus.isConsentCurrent(statuses));
    }
    
    @Test
    public void isConsentCurrentRequiredSigned() {
        add(REQUIRED_SIGNED_CURRENT);
        add(OPTIONAL_UNSIGNED);
        assertTrue(ConsentStatus.isConsentCurrent(statuses));
    }

    private void add(ConsentStatus status) {
        statuses.put(new SubpopulationGuid(status.getSubpopulationGuid()), status);
    }
}
