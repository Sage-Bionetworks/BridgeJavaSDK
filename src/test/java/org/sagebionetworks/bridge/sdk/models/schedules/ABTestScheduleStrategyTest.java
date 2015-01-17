package org.sagebionetworks.bridge.sdk.models.schedules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.joda.time.Period;
import org.junit.Test;

public class ABTestScheduleStrategyTest {

    @Test
    public void hashCodeEquals() {
        ABTestScheduleStrategy s1 = create();
        ABTestScheduleStrategy s2 = create();
        
        assertEquals(s1.hashCode(), s2.hashCode());
        assertTrue(s1.equals(s2));
    }
    
    @Test
    public void hashCodeEqualsUnequal() {
        ABTestScheduleStrategy s1 = create();
        s1.getGroups().get(0).getSchedule().setCronTrigger("* 1 *");
        ABTestScheduleStrategy s2 = create();
        
        assertNotEquals(s1.hashCode(), s2.hashCode());
        assertFalse(s1.equals(s2));
    }
    
    private ABTestScheduleStrategy create() {
        Schedule schedule = new Schedule();
        schedule.setCronTrigger("* * *");
        schedule.setExpires(Period.parse("PT2H"));
        schedule.setLabel("This is a label");
        
        ABTestScheduleStrategy s = new ABTestScheduleStrategy();
        s.addGroup(50, schedule);
        return s;
    }
}
