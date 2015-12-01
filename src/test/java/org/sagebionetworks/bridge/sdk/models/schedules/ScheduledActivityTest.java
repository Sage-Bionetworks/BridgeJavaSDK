package org.sagebionetworks.bridge.sdk.models.schedules;

import static org.joda.time.DateTimeZone.UTC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.joda.time.DateTime;
import org.junit.Test;

import org.sagebionetworks.bridge.sdk.utils.Utilities;

public class ScheduledActivityTest {
    
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ScheduledActivity.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }

    @Test
    public void canSerialize() throws Exception {
        Activity activity = new Activity("Activity Label 1", null, new TaskReference("task1"));
        ScheduledActivity schActivity = new ScheduledActivity("AAA-BBB-CCC", activity, 
            DateTime.parse("2014-06-01T10:00:00.000-07:00").withZone(UTC), 
            DateTime.parse("2014-06-14T10:00:00.000-07:00").withZone(UTC), 
            DateTime.parse("2014-06-08T17:32:12.970-07:00").withZone(UTC),
            null, 2, 8, true);
        
        String json = Utilities.getMapper().writeValueAsString(schActivity);
        assertEquals("{\"guid\":\"AAA-BBB-CCC\",\"activity\":{\"label\":\"Activity Label 1\",\"task\":{\"identifier\":\"task1\"},\"activityType\":\"task\"},\"scheduledOn\":\"2014-06-01T17:00:00.000Z\",\"expiresOn\":\"2014-06-14T17:00:00.000Z\",\"startedOn\":\"2014-06-09T00:32:12.970Z\",\"minAppVersion\":2,\"maxAppVersion\":8,\"persistent\":true,\"status\":\"started\"}", json);
        
        ScheduledActivity newSchActivity = Utilities.getMapper().readValue(json, ScheduledActivity.class);
        assertEquals(schActivity.getGuid(), newSchActivity.getGuid());
        assertEquals(schActivity.getActivity(), newSchActivity.getActivity());
        assertEquals(schActivity.getScheduledOn(), newSchActivity.getScheduledOn());
        assertEquals(schActivity.getExpiresOn(), newSchActivity.getExpiresOn());
        assertEquals(schActivity.getStartedOn(), newSchActivity.getStartedOn());
        assertEquals(schActivity.getPersistent(), newSchActivity.getPersistent());
        assertEquals(2, schActivity.getMinAppVersion().intValue());
        assertEquals(8, schActivity.getMaxAppVersion().intValue());
        assertNull(newSchActivity.getFinishedOn());
    }
}
