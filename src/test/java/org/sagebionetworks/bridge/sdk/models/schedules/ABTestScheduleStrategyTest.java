package org.sagebionetworks.bridge.sdk.models.schedules;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

public class ABTestScheduleStrategyTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ABTestScheduleStrategy.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
}
