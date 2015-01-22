package org.sagebionetworks.bridge.sdk.models.studies;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

public class TrackerTest {
    
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Tracker.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
}
