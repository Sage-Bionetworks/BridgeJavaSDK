package org.sagebionetworks.bridge.sdk.models.schedules;

import java.util.List;

import java.util.Objects;

import org.joda.time.DateTime;
import org.joda.time.Period;

import com.google.common.collect.Lists;

/**
 * A schedule for a set of activities in a study.
 */
public final class Schedule {

    private String label;
    private ScheduleType scheduleType = ScheduleType.once;
    private String cronTrigger;
    private DateTime startsOn;
    private DateTime endsOn;
    private Period expires;
    private List<Activity> activities = Lists.newArrayList();
   
    /**
     * A label for a schedule (most likely shown to researchers).
     */
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    /**
     * Whether this schedule describes one or more activities that should be done one time
     * by the user, or more than once according to a shedule set forth in the <code>cronTrigger</code> 
     * of the schedule.
     */
    public ScheduleType getScheduleType() {
        return scheduleType;
    }
    public void setScheduleType(ScheduleType type) {
        this.scheduleType = type;
    }
    /**
     * A cron trigger string describing the frequency at which the study would like the participant
     * to undertake the activities for this schedule.
     */
    public String getCronTrigger() {
        return cronTrigger;
    }
    public void setCronTrigger(String cronTrigger) {
        this.cronTrigger = cronTrigger;
        this.scheduleType = (cronTrigger == null) ? ScheduleType.once : ScheduleType.recurring;
    }
    /**
     * This is the earliest date and time at which the activity should be shown to users in study
     * applications. Within the <code>startsOn</code> and <code>endsOn</code> timestamps, the 
     * activity should be undertaken according to the schedule defined by the <code>cronTrigger</code>.
     */
    public DateTime getStartsOn() {
        return startsOn;
    }
    public void setStartsOn(DateTime startsOn) {
        this.startsOn = startsOn;
    }
    /**
     * This is the latest date and time at which the activity should be show to users in the study 
     * applications.
     */
    public DateTime getEndsOn() {
        return endsOn;
    }
    public void setEndsOn(DateTime endsOn) {
        this.endsOn = endsOn;
    }
    /**
     * This is the period during which time the activities defined by this schedule should be shown to 
     * the user. After this period expires, the activities should be taken out of the user interface. 
     * For example, if the schedule indicates that the activities should be shown on Friday at 6AM, and 
     * the <code>expires</code> value indicates six hours, then the activities should be hidden at 
     * 12PM (all in the user's local time).
     */
    public Period getExpires() {
        return expires;
    }
    public void setExpires(Period expires) {
        this.expires = expires;
    }
    /**
     * Add an activity to this schedule.
     */
    public void addActivity(Activity activity) {
        this.activities.add(activity);
    }
    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }
    /**
     * The list of all activities being scheduled.
     */
    public List<Activity> getActivities() {
        return activities;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(activities);
        result = prime * result + Objects.hashCode(cronTrigger);
        result = prime * result + Objects.hashCode(endsOn);
        result = prime * result + Objects.hashCode(expires);
        result = prime * result + Objects.hashCode(label);
        result = prime * result + Objects.hashCode(scheduleType);
        result = prime * result + Objects.hashCode(startsOn);
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Schedule other = (Schedule) obj;
        return (Objects.equals(activities, other.activities) && Objects.equals(cronTrigger, other.cronTrigger)
                && Objects.equals(endsOn, other.endsOn) && Objects.equals(expires, other.expires)
                && Objects.equals(label, other.label) && Objects.equals(scheduleType, other.scheduleType) && Objects
                    .equals(startsOn, other.startsOn));
    }
    
    @Override
    public String toString() {
        return String.format("Schedule [label=%s, scheduleType=%s, cronTrigger=%s, startsOn=%s, endsOn=%s, expires=%s, activities=%s]", 
                label, scheduleType, cronTrigger, startsOn, endsOn, expires, activities);
    }
    
}
