package org.sagebionetworks.bridge.sdk.models.holders;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

public class SimpleVersionHolderTest {
    
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(SimpleVersionHolder.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
}
