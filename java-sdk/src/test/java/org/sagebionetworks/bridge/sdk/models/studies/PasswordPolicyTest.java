package org.sagebionetworks.bridge.sdk.models.studies;

import static org.junit.Assert.assertEquals;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.fasterxml.jackson.databind.JsonNode;

public class PasswordPolicyTest {
    @Test
    public void equalsHashCode() {
        EqualsVerifier.forClass(PasswordPolicy.class).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void canSerialize() throws Exception {
        PasswordPolicy policy = new PasswordPolicy(8, true, false, true, true);
        
        String json = Utilities.getMapper().writeValueAsString(policy);
        JsonNode node = Utilities.getMapper().readTree(json);
        
        assertEquals(8, node.get("minLength").asInt());
        assertEquals(true, node.get("numericRequired").asBoolean());
        assertEquals(false, node.get("symbolRequired").asBoolean());
        assertEquals(true, node.get("lowerCaseRequired").asBoolean());
        assertEquals(true, node.get("upperCaseRequired").asBoolean());
        
        PasswordPolicy policy2 = Utilities.getMapper().readValue(json, PasswordPolicy.class);
        assertEquals(policy, policy2);
    }
}
