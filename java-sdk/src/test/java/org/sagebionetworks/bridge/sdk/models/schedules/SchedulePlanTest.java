package org.sagebionetworks.bridge.sdk.models.schedules;

import static org.junit.Assert.assertEquals;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;
import org.sagebionetworks.bridge.Tests;
import org.sagebionetworks.bridge.sdk.utils.Utilities;

public class SchedulePlanTest {
    
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(SchedulePlan.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void canRoundtripSerialization() throws Exception {
        SchedulePlan plan1 = Tests.getABTestSchedulePlan();
        SchedulePlan plan2 = Tests.getABTestSchedulePlan();
        
        String json = Utilities.getMapper().writeValueAsString(plan1);
        plan1 = Utilities.getMapper().readValue(json, SchedulePlan.class);
        
        assertEquals(plan2, plan1);
    }

}
