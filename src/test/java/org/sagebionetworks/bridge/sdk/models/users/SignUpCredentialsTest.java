package org.sagebionetworks.bridge.sdk.models.users;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

public class SignUpCredentialsTest {
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(SignUpCredentials.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
}
