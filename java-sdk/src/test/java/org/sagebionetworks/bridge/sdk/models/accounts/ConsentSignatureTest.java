package org.sagebionetworks.bridge.sdk.models.accounts;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

public class ConsentSignatureTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ConsentSignature.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
}
