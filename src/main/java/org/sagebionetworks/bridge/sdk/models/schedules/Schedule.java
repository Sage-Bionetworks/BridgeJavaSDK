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
    public void setScheduleType(ScheduleType type) {
        this.scheduleType = type;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((activityRef == null) ? 0 : activityRef.hashCode());
        result = prime * result + ((activityType == null) ? 0 : activityType.hashCode());
        result = prime * result + ((cronTrigger == null) ? 0 : cronTrigger.hashCode());
        result = prime * result + ((endsOn == null) ? 0 : endsOn.hashCode());
        result = prime * result + ((expires == null) ? 0 : expires.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((scheduleType == null) ? 0 : scheduleType.hashCode());
        result = prime * result + ((startsOn == null) ? 0 : startsOn.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Schedule other = (Schedule) obj;
        if (activityRef == null) {
            if (other.activityRef != null)
                return false;
        } else if (!activityRef.equals(other.activityRef))
            return false;
        if (activityType != other.activityType)
            return false;
        if (cronTrigger == null) {
            if (other.cronTrigger != null)
                return false;
        } else if (!cronTrigger.equals(other.cronTrigger))
            return false;
        if (endsOn == null) {
            if (other.endsOn != null)
                return false;
        } else if (!endsOn.equals(other.endsOn))
            return false;
        if (expires == null) {
            if (other.expires != null)
                return false;
        } else if (!expires.equals(other.expires))
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (scheduleType != other.scheduleType)
            return false;
        if (startsOn == null) {
            if (other.startsOn != null)
                return false;
        } else if (!startsOn.equals(other.startsOn))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Schedule [label=" + label + ", activityType=" + activityType + ", activityRef=" + activityRef
                + ", scheduleType=" + scheduleType + ", cronTrigger=" + cronTrigger + ", startsOn=" + startsOn
                + ", endsOn=" + endsOn + ", expires=" + expires + "]";
    }
    
}
