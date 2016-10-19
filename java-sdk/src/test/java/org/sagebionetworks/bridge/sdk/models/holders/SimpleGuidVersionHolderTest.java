package org.sagebionetworks.bridge.sdk.models.holders;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class SimpleGuidVersionHolderTest {
    
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(SimpleGuidVersionHolder.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
}
