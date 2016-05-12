package org.sagebionetworks.bridge.sdk.models.schedules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.junit.Test;

import org.sagebionetworks.bridge.sdk.utils.Utilities;

import com.google.common.collect.Lists;

public class ScheduleTest {
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Schedule.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void canSerializeDeserializeSchedule() throws Exception {
        Activity activity = new Activity("label", "labelDetail", new SurveyReference("AAA"));
        
        Schedule schedule = new Schedule();
        schedule.getActivities().add(activity);
        schedule.setCronTrigger("0 0 8 ? * TUE *");
        schedule.setDelay(Period.parse("P1D"));
        schedule.setExpires(Period.parse("P2D"));
        schedule.setStartsOn(DateTime.parse("2015-02-02T10:10:10.000Z"));
        schedule.setEndsOn(DateTime.parse("2015-01-01T10:10:10.000Z"));
        schedule.setEventId("eventId");
        schedule.setInterval(Period.parse("P3D"));
        schedule.setLabel("label");
        schedule.setScheduleType(ScheduleType.RECURRING);
        schedule.setTimes(Lists.newArrayList(LocalTime.parse("10:10"), LocalTime.parse("14:00")));
        
        String string = Utilities.getMapper().writeValueAsString(schedule);
        schedule = Utilities.getMapper().readValue(string, Schedule.class);
        
        assertEquals("0 0 8 ? * TUE *", schedule.getCronTrigger());
        assertEquals("P1D", schedule.getDelay().toString());
        assertEquals("P2D", schedule.getExpires().toString());
        assertEquals("eventId", schedule.getEventId());
        assertEquals("label", schedule.getLabel());
        assertEquals("P3D", schedule.getInterval().toString());
        assertEquals(ScheduleType.RECURRING, schedule.getScheduleType());
        assertEquals("2015-02-02T10:10:10.000Z", schedule.getStartsOn().toString());
        assertEquals("2015-01-01T10:10:10.000Z", schedule.getEndsOn().toString());
        assertEquals("10:10:00.000", schedule.getTimes().get(0).toString());
        assertEquals("14:00:00.000", schedule.getTimes().get(1).toString());
        activity = schedule.getActivities().get(0);
        assertNull(activity.getGuid()); // Created on server
        assertEquals("label", activity.getLabel());
        assertEquals("AAA", activity.getSurvey().getGuid());
    }
    
    @Test
    public void activitiesDeserializerHrefs() throws Exception {
        Schedule schedule = Utilities.getMapper().readValue("{\"label\":\"label\",\"scheduleType\":\"recurring\",\"cronTrigger\":\"0 0 8 ? * TUE *\",\"eventId\":\"eventId\",\"activities\":[{\"label\":\"label\",\"labelDetail\":\"labelDetail\",\"survey\":{\"identifier\":\"identifier\",\"guid\":\"AAA\",\"href\":\"https://webservices.sagebase.org/s3/surveys/AAA/revisions/published\"},\"activityType\":\"survey\"}],\"times\":[\"10:10:00.000\",\"14:00:00.000\"],\"startsOn\":\"2015-02-02T10:10:10.000Z\",\"endsOn\":\"2015-01-01T10:10:10.000Z\",\"expires\":\"P2D\",\"delay\":\"P1D\",\"interval\":\"P3D\"}", Schedule.class);
        assertEquals("https://webservices.sagebase.org/s3/surveys/AAA/revisions/published", schedule.getActivities().get(0).getSurvey().getHref());
    }
    
}
