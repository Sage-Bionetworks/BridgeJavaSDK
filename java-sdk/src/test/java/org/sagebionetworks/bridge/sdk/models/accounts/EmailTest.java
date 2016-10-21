package org.sagebionetworks.bridge.sdk.models.accounts;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class EmailTest {
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(EmailCredentials.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }

}
