package org.sagebionetworks.bridge.sdk.models.users;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.fasterxml.jackson.databind.JsonNode;

public class SignInCredentialsTest {
    
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(SignInCredentials.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void canSerializer() throws Exception {
        SignInCredentials signIn = new SignInCredentials("study-key", "email@email.com", "password");
        
        JsonNode node = Utilities.getMapper().valueToTree(signIn);
        assertEquals("study-key", node.get("study").asText());
        assertEquals("email@email.com", node.get("username").asText());
        assertEquals("email@email.com", node.get("email").asText());
        assertEquals("password", node.get("password").asText());
    }
    
}
