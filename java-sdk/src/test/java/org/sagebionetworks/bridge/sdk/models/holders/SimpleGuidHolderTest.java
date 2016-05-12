package org.sagebionetworks.bridge.sdk.models.holders;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

public class SimpleGuidHolderTest {
    
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(SimpleGuidHolder.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
}
