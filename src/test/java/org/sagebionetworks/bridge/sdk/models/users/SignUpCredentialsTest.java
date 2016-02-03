package org.sagebionetworks.bridge.sdk.models.users;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;

import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Sets;

public class SignUpCredentialsTest {
    
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(SignUpCredentials.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void canSerialize() throws Exception {
        Set<String> userSets = Sets.newHashSet("sdk-int-1", "sdk-int-2");
        DataGroups dataGroups = new DataGroups();
        dataGroups.setDataGroups(userSets);
        SignUpCredentials signUp = new SignUpCredentials("studyIdentifier", "email", "password", dataGroups);
        
        String json = Utilities.getMapper().writeValueAsString(signUp);
        JsonNode node = Utilities.getMapper().readTree(json);
        
        assertEquals("email", node.get("email").asText());
        assertEquals("email", node.get("username").asText());
        assertEquals("password", node.get("password").asText());
        assertEquals("studyIdentifier", node.get("study").asText());
        
        ArrayNode array = (ArrayNode)node.get("dataGroups");
        assertEquals(2, array.size());
        Set<String> sets = Sets.newHashSet(array.get(0).asText(), array.get(1).asText());
        assertEquals(userSets, sets);
        
        SignUpCredentials signUp2 = Utilities.getMapper().readValue(json, SignUpCredentials.class);
        assertEquals(signUp, signUp2);
    }
    
}
