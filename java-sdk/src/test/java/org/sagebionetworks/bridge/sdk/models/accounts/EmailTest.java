package org.sagebionetworks.bridge.sdk.models.accounts;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

public class EmailTest {
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(EmailCredentials.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }

}
