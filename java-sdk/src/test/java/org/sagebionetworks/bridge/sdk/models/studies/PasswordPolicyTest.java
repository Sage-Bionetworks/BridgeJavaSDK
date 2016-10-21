package org.sagebionetworks.bridge.sdk.models.studies;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.sagebionetworks.bridge.sdk.utils.BridgeUtils;

import com.fasterxml.jackson.databind.JsonNode;

import nl.jqno.equalsverifier.EqualsVerifier;

public class PasswordPolicyTest {
    @Test
    public void equalsHashCode() {
        EqualsVerifier.forClass(PasswordPolicy.class).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void canSerialize() throws Exception {
        PasswordPolicy policy = new PasswordPolicy(8, true, false, true, true);
        
        String json = BridgeUtils.getMapper().writeValueAsString(policy);
        JsonNode node = BridgeUtils.getMapper().readTree(json);
        
        assertEquals(8, node.get("minLength").asInt());
        assertEquals(true, node.get("numericRequired").asBoolean());
        assertEquals(false, node.get("symbolRequired").asBoolean());
        assertEquals(true, node.get("lowerCaseRequired").asBoolean());
        assertEquals(true, node.get("upperCaseRequired").asBoolean());
        
        PasswordPolicy policy2 = BridgeUtils.getMapper().readValue(json, PasswordPolicy.class);
        assertEquals(policy, policy2);
    }
}
