package org.sagebionetworks.bridge.sdk.models.schedules;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.sagebionetworks.bridge.sdk.utils.Utilities;

public class ActivityTest {
    
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Activity.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
    /**
     * Many of these cases should go away. The only thing we'll be interested in is the completion of an activity.
     * But it all works during the transition. 
     * @throws Exception
     */
    @Test
    public void activityKnowsWhenItIsPersistentlyScheduled() throws Exception {
        // This is persistently scheduled due to an activity
        Schedule schedule = Utilities.getMapper().readValue("{\"scheduleType\":\"once\",\"eventId\":\"activity:HHH:finished\",\"activities\":[{\"label\":\"Label\",\"labelDetail\":\"Label Detail\",\"guid\":\"HHH\",\"task\":{\"identifier\":\"foo\"},\"activityType\":\"task\"}]}", Schedule.class);
        assertTrue(schedule.getActivities().get(0).isPersistentlyRescheduledBy(schedule));
        
        // This is persistently schedule due to a task completion. We actually never generate this event, and it will go away.
        schedule = Utilities.getMapper().readValue("{\"scheduleType\":\"once\",\"eventId\":\"task:foo:finished\",\"activities\":[{\"label\":\"Label\",\"labelDetail\":\"Label Detail\",\"guid\":\"HHH\",\"task\":{\"identifier\":\"foo\"},\"activityType\":\"task\"}]}", Schedule.class);
        assertTrue(schedule.getActivities().get(0).isPersistentlyRescheduledBy(schedule));
        
        // This is persistently schedule due to a survey completion. This should not match (it's not a survey)
        schedule = Utilities.getMapper().readValue("{\"scheduleType\":\"once\",\"eventId\":\"survey:HHH:finished\",\"activities\":[{\"label\":\"Label\",\"labelDetail\":\"Label Detail\",\"guid\":\"HHH\",\"task\":{\"identifier\":\"foo\"},\"activityType\":\"task\"}]}", Schedule.class);
        assertFalse(schedule.getActivities().get(0).isPersistentlyRescheduledBy(schedule));
        
        // Wrong activity, not persistent
        schedule = Utilities.getMapper().readValue("{\"scheduleType\":\"once\",\"eventId\":\"survey:HHH:finished\",\"activities\":[{\"label\":\"Label\",\"labelDetail\":\"Label Detail\",\"guid\":\"III\",\"task\":{\"identifier\":\"foo\"},\"activityType\":\"task\"}]}", Schedule.class);
        assertFalse(schedule.getActivities().get(0).isPersistentlyRescheduledBy(schedule));
    }
    
}
