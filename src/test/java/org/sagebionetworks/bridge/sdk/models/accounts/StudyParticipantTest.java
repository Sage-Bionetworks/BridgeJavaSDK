package org.sagebionetworks.bridge.sdk.models.accounts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.junit.Test;

import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.Roles;
import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;
import org.sagebionetworks.bridge.sdk.models.users.SharingScope;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import nl.jqno.equalsverifier.EqualsVerifier;

public class StudyParticipantTest {
    
    private static final DateTime CREATED_ON = DateTime.now().withZone(DateTimeZone.UTC);
    private static final LinkedHashSet<String> LANGUAGES = Tests.newLinkedHashSet("en","fr");
    private static final Set<String> DATA_GROUPS = Sets.newHashSet("group1","group2");
    private static final Map<String,String> ATTRIBUTES = new ImmutableMap.Builder<String, String>()
            .put("a", "b").build();
    
    @Test
    public void hashCodeEquals() {
        EqualsVerifier.forClass(StudyParticipant.class).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void canSerialize() throws Exception {
        UserConsentHistory history = new UserConsentHistory(new SubpopulationGuid("subpopGuid"),
                DateTime.parse("2010-10-10T10:10:10.000Z"), "Test User", LocalDate.parse("2010-10-12"), "imageData",
                "image/png", DateTime.parse("2011-10-10T10:10:10.000Z"), DateTime.parse("2012-10-10T10:10:10.000Z"),
                true);
        Map<String,List<UserConsentHistory>> consentHistories = new HashMap<>();
        consentHistories.put("subpopGuid", Lists.newArrayList(history));
        
        StudyParticipant participant = new StudyParticipant("firstName", "lastName", "email@email.com", "externalId",
                "password", SharingScope.ALL_QUALIFIED_RESEARCHERS, true, DATA_GROUPS, "healthCode", ATTRIBUTES, consentHistories,
                Sets.newHashSet(Roles.DEVELOPER), CREATED_ON, AccountStatus.ENABLED, LANGUAGES);
        
        JsonNode node = Utilities.getMapper().valueToTree(participant);
        assertEquals("firstName", node.get("firstName").asText());
        assertEquals("lastName", node.get("lastName").asText());
        assertEquals("externalId", node.get("externalId").asText());
        assertEquals("password", node.get("password").asText());
        assertEquals("healthCode", node.get("healthCode").asText());
        assertEquals(CREATED_ON.toString(), node.get("createdOn").asText());
        assertEquals("enabled", node.get("status").asText());
        assertEquals(SharingScope.ALL_QUALIFIED_RESEARCHERS, SharingScope.valueOf(node.get("sharingScope").asText().toUpperCase()));
        assertTrue(node.get("notifyByEmail").asBoolean());
        assertEquals("email@email.com", node.get("email").asText());
        
        ArrayNode dataGroups = (ArrayNode)node.get("dataGroups");
        assertTrue(DATA_GROUPS.contains(dataGroups.get(0).asText()));
        assertTrue(DATA_GROUPS.contains(dataGroups.get(1).asText()));
        
        JsonNode attrs = node.get("attributes");
        assertEquals("b", attrs.get("a").asText());
        
        ArrayNode roles = (ArrayNode)node.get("roles");
        assertEquals("developer", roles.get(0).asText());
        
        ArrayNode langs = (ArrayNode)node.get("languages");
        assertEquals("en", langs.get(0).asText());
        assertEquals("fr", langs.get(1).asText());

        ObjectNode allConsents = (ObjectNode)node.get("consentHistories");
        ArrayNode consents = (ArrayNode)allConsents.get("subpopGuid");
        ObjectNode consent = (ObjectNode)consents.get(0);
        
        assertEquals("subpopGuid",consent.get("subpopulationGuid").asText()); 
        assertEquals("2010-10-10T10:10:10.000Z",consent.get("consentCreatedOn").asText()); 
        assertEquals("Test User",consent.get("name").asText());
        assertEquals("2010-10-12",consent.get("birthdate").asText()); 
        assertEquals("imageData",consent.get("imageData").asText()); 
        assertEquals("image/png",consent.get("imageMimeType").asText()); 
        assertEquals("2011-10-10T10:10:10.000Z",consent.get("signedOn").asText()); 
        assertEquals("2012-10-10T10:10:10.000Z",consent.get("withdrewOn").asText());
        assertTrue(consent.get("hasSignedActiveConsent").asBoolean());
        
        StudyParticipant deserParticipant = Utilities.getMapper().treeToValue(node, StudyParticipant.class);
        assertEquals(participant, deserParticipant);
    }
    
    @Test
    public void canBuild() {
        StudyParticipant.Builder builder = new StudyParticipant.Builder();
        builder.withFirstName("firstName");
        builder.withLastName("lastName");
        builder.withExternalId("externalId");
        builder.withEmail("email@email.com");
        builder.withPassword("password");
        builder.withSharingScope(SharingScope.ALL_QUALIFIED_RESEARCHERS);
        builder.withNotifyByEmail(true);
        builder.withDataGroups(DATA_GROUPS);
        builder.withLanguages(LANGUAGES);
        builder.withAttributes(ATTRIBUTES);
        builder.withStatus(AccountStatus.DISABLED);
        
        StudyParticipant participant = builder.build();

        assertEquals("firstName", participant.getFirstName());
        assertEquals("lastName", participant.getLastName());
        assertEquals("email@email.com", participant.getEmail());
        assertEquals("externalId", participant.getExternalId());
        assertEquals("password", participant.getPassword());
        assertEquals(SharingScope.ALL_QUALIFIED_RESEARCHERS, participant.getSharingScope());
        assertEquals(true, participant.isNotifyByEmail());
        assertEquals(DATA_GROUPS, participant.getDataGroups());
        assertEquals(LANGUAGES, participant.getLanguages());
        assertEquals(ATTRIBUTES, participant.getAttributes());
        assertEquals(AccountStatus.DISABLED, participant.getStatus());
    }
}
