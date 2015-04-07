package org.sagebionetworks.bridge.sdk.models.schedules;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

public class ScheduleTest {
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Schedule.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
}
