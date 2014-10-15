package org.sagebionetworks.bridge.sdk.models.schedules;

import org.joda.time.DateTime;
import org.joda.time.Period;

public class Schedule {

    private String label;
    private ActivityType activityType;
    private String activityRef;
    private ScheduleType scheduleType = ScheduleType.once;
    private String cronTrigger;
    private DateTime startsOn;
    private DateTime endsOn;
    private Period expires;
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public String getCronTrigger() {
        return cronTrigger;
    }

    public void setCronTrigger(String cronTrigger) {
        this.cronTrigger = cronTrigger;
        this.scheduleType = (cronTrigger == null) ? ScheduleType.once : ScheduleType.recurring;
    }

    public DateTime getStartsOn() {
        return startsOn;
    }

    public void setStartsOn(DateTime startsOn) {
        this.startsOn = startsOn;
    }

    public DateTime getEndsOn() {
        return endsOn;
    }

    public void setEndsOn(DateTime endsOn) {
        this.endsOn = endsOn;
    }

    public Period getExpires() {
        return expires;
    }

    public void setExpires(Period expires) {
        this.expires = expires;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public String getActivityRef() {
        return activityRef;
    }

    public void setActivityRef(String activityRef) {
        this.activityRef = activityRef;
    }
    
}
