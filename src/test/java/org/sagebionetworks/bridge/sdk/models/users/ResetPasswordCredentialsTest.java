package org.sagebionetworks.bridge.sdk.models.users;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

public class ResetPasswordCredentialsTest {
    
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ResetPasswordCredentials.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
}
