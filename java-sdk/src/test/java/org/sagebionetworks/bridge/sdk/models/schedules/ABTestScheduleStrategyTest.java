package org.sagebionetworks.bridge.sdk.models.schedules;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class ABTestScheduleStrategyTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ABTestScheduleStrategy.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
}
