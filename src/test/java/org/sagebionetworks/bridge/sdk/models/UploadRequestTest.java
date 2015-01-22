package org.sagebionetworks.bridge.sdk.models;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

public class UploadRequestTest {
    
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(UploadRequest.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
}
