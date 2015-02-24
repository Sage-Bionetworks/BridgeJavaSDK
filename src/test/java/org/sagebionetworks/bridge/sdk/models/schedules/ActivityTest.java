package org.sagebionetworks.bridge.sdk.models.schedules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;

public class ActivityTest {
    
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Activity.class).suppress(Warning.NONFINAL_FIELDS).allFieldsShouldBeUsed().verify();
    }
    
    @Test
    public void surveyLinksAreMadeAbsolute() {
        Activity activity = new Activity("Test", "/api/v1/surveys/abc/dateString");

        assertEquals("Test", activity.getLabel());
        assertTrue(activity.getRef().startsWith("http"));
        assertEquals(ActivityType.survey, activity.getActivityType());
    }
    
    @Test
    public void surveyTasksAreCorrect() {
        
        Activity activity = new Activity("Test", "task:doSurveys"); // tricky, eh?
        assertEquals("Test", activity.getLabel());
        assertTrue(activity.getRef().startsWith("task:"));
        assertEquals(ActivityType.task, activity.getActivityType());
    }
    
    @Test
    public void activityCreatedFromGuidCreatedOnVersionHolder() {
        final DateTime dateTime = DateTime.now();
        GuidCreatedOnVersionHolder keys = new GuidCreatedOnVersionHolder() {
            @Override
            public String getGuid() {
                return "abc";
            }
            @Override
            public DateTime getCreatedOn() {
                return dateTime;
            }
            @Override
            public Long getVersion() {
                return 1L;
            }
        };
        Activity activity = new Activity("Test", keys);
        assertEquals("Test", activity.getLabel());
        assertTrue(activity.getRef().startsWith("http"));
        assertEquals(ActivityType.survey, activity.getActivityType());
    }
    @Test
    public void activityCreatedFromSurveyReference() {
        final DateTime dateTime = DateTime.now();
        
        SurveyReference ref = new SurveyReference("abc", dateTime);
        Activity activity = new Activity("Test", ref);
        assertEquals("Test", activity.getLabel());
        assertTrue(activity.getRef().startsWith("http"));
        assertTrue(activity.getRef().endsWith(dateTime.toString(ISODateTimeFormat.dateTime())));
        assertEquals(ActivityType.survey, activity.getActivityType());
    }
    
    @Test
    public void activityCreatedFromPublishedSurveyReference() {
        SurveyReference ref = new SurveyReference("abc", null);
        Activity activity = new Activity("Test", ref);
        assertEquals("Test", activity.getLabel());
        assertTrue(activity.getRef().startsWith("http"));
        assertTrue(activity.getRef().endsWith("published"));
        assertEquals(ActivityType.survey, activity.getActivityType());
    }
    
}
