package org.sagebionetworks.bridge.sdk.models.schedules;

import static org.joda.time.DateTimeZone.UTC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.joda.time.DateTime;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.Utilities;

public class TaskTest {
    
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Task.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }

    @Test
    public void canSerialize() throws Exception {
        Activity activity = new Activity("Activity Label 1", null, new TaskReference("task1"));
        Task task = new Task("AAA-BBB-CCC", activity, 
            DateTime.parse("2014-06-01T10:00:00.000-07:00").withZone(UTC), 
            DateTime.parse("2014-06-14T10:00:00.000-07:00").withZone(UTC), 
            DateTime.parse("2014-06-08T17:32:12.970-07:00").withZone(UTC),
            null);
        
        String json = Utilities.getMapper().writeValueAsString(task);
        assertEquals("{\"guid\":\"AAA-BBB-CCC\",\"activity\":{\"label\":\"Activity Label 1\",\"task\":{\"identifier\":\"task1\"},\"activityType\":\"task\"},\"scheduledOn\":\"2014-06-01T17:00:00.000Z\",\"expiresOn\":\"2014-06-14T17:00:00.000Z\",\"startedOn\":\"2014-06-09T00:32:12.970Z\",\"status\":\"started\"}", json);
        
        Task newTask = Utilities.getMapper().readValue(json, Task.class);
        assertEquals(task.getGuid(), newTask.getGuid());
        assertEquals(task.getActivity(), newTask.getActivity());
        assertEquals(task.getScheduledOn(), newTask.getScheduledOn());
        assertEquals(task.getExpiresOn(), newTask.getExpiresOn());
        assertEquals(task.getStartedOn(), newTask.getStartedOn());
        assertNull(newTask.getFinishedOn());
    }
}
