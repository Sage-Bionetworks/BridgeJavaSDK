package org.sagebionetworks.bridge.sdk.models.accounts;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class ConsentSignatureTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ConsentSignature.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
}
