package org.sagebionetworks.bridge.sdk.models.accounts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import org.sagebionetworks.bridge.sdk.Roles;
import org.sagebionetworks.bridge.sdk.models.users.SharingScope;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import nl.jqno.equalsverifier.EqualsVerifier;

public class StudyParticipantTest {
    
    private static final Set<String> DATA_GROUPS = Sets.newHashSet("group1","group2");
    
    @Test
    public void hashCodeEquals() {
        EqualsVerifier.forClass(StudyParticipant.class).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void canSerialize() throws Exception {
        LinkedHashSet<String> languages = new LinkedHashSet<>();
        languages.add("en");
        languages.add("fr");
        
        Map<String,List<UserConsentHistory>> consentHistories = new HashMap<>();
        
        StudyParticipant participant = new StudyParticipant(
                "firstName", 
                "lastName", 
                "email@email.com", 
                "externalId", 
                SharingScope.ALL_QUALIFIED_RESEARCHERS,
                true, 
                DATA_GROUPS, 
                "healthCode", 
                new ImmutableMap.Builder<String,String>().put("a","b").build(),
                consentHistories, 
                Sets.newHashSet(Roles.DEVELOPER), 
                languages);
        
        JsonNode node = Utilities.getMapper().valueToTree(participant);
        assertEquals("firstName", node.get("firstName").asText());
        assertEquals("lastName", node.get("lastName").asText());
        assertEquals("externalId", node.get("externalId").asText());
        assertEquals("healthCode", node.get("healthCode").asText());
        assertEquals(SharingScope.ALL_QUALIFIED_RESEARCHERS, SharingScope.valueOf(node.get("sharingScope").asText().toUpperCase()));
        assertTrue(node.get("notifyByEmail").asBoolean());
        assertEquals("email@email.com", node.get("email").asText());
        
        ArrayNode dataGroups = (ArrayNode)node.get("dataGroups");
        assertEquals("group1", dataGroups.get(0).asText());
        assertEquals("group2", dataGroups.get(1).asText());
        
        ObjectNode attributes = (ObjectNode)node.get("attributes");
        assertEquals("b", attributes.get("a").asText());
        
        ArrayNode roles = (ArrayNode)node.get("roles");
        assertEquals("developer", roles.get(0).asText());
        
        ArrayNode langs = (ArrayNode)node.get("languages");
        assertEquals("en", langs.get(0).asText());
        assertEquals("fr", langs.get(1).asText());
        
        /*
        private final Map<String,List<UserConsentHistory>> consentHistories;
         */
    }
}
