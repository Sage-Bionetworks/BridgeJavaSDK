package org.sagebionetworks.bridge.sdk.models.schedules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Test;

import com.google.common.collect.Lists;

public class SchedulePlanTest {

    @Test
    public void hashCodeEquals() {
        DateTime date = DateTime.now();
        SchedulePlan plan1 = createSchedulePlan(date);
        SchedulePlan plan2 = createSchedulePlan(date);
        
        assertEquals(plan1.hashCode(), plan2.hashCode());
        assertTrue(plan1.equals(plan2));
    }
    
    @Test
    public void hashCodeEqualsUnequal() {
        DateTime date = DateTime.now();
        SchedulePlan plan1 = createSchedulePlan(date);
        SchedulePlan plan2 = createSchedulePlan(date);
        plan2.setVersion(3L);
        
        assertNotEquals(plan1.hashCode(), plan2.hashCode());
        assertFalse(plan1.equals(plan2));
    }
    
    private SchedulePlan createSchedulePlan(DateTime date) {
        Schedule schedule = new Schedule();
        schedule.setActivities(Lists.newArrayList(new Activity("Label", "ref://asdf")));
        schedule.setCronTrigger("* * *");
        schedule.setEndsOn(date);
        schedule.setStartsOn(date);
        schedule.setExpires(Period.parse("PT2H"));
        schedule.setScheduleType(ScheduleType.recurring);
        
        SchedulePlan plan = new SchedulePlan();
        plan.setGuid("AAA");
        plan.setModifiedOn(date);
        plan.setSchedule(schedule);
        plan.setVersion(2L);
        return plan;
    }
}
