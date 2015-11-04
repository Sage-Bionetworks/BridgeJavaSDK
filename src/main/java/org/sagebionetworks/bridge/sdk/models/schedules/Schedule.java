package org.sagebionetworks.bridge.sdk.models.schedules;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Objects;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.Period;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

/**
 * A schedule for a set of activities in a study.
 */
public final class Schedule {

    private String label;
    private ScheduleType scheduleType = ScheduleType.ONCE;
    private String cronTrigger;
    private DateTime startsOn;
    private DateTime endsOn;
    private Period expires;
    private String eventId;
    private Period delay;
    private Period interval;
    private List<Activity> activities = Lists.newArrayList();
    private List<LocalTime> times = Lists.newArrayList();

    public List<LocalTime> getTimes() {
        return times;
    }
    public void setTimes(List<LocalTime> times) {
        this.times = times;
    }
    public void addTimes(LocalTime... times) {
        for (LocalTime time : times) {
            checkNotNull(time);
            this.times.add(time);
        }
    }
    public void addTimes(String... times) {
        for (String time : times) {
            checkNotNull(time);
            this.times.add(LocalTime.parse(time));
        }
    }
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
     * The identifier for an event that should trigger this schedule.
     * @return
     */
    public String getEventId() {
        return eventId;
    }
    public void setEventId(String eventId) {
        this.eventId = eventId;
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
        this.scheduleType = (cronTrigger == null) ? ScheduleType.ONCE : ScheduleType.RECURRING;
    }
    /**
     * This is the earliest date and time at which the activity should be shown to users in study
     * applications. Within the <code>startsOn</code> and <code>endsOn</code> timestamps, the 
     * activity should be undertaken according to the schedule defined by the <code>cronTrigger</code>.
     */
    public DateTime getStartsOn() {
        return startsOn;
    }
    @JsonProperty("startsOn")
    public void setStartsOn(DateTime startsOn) {
        this.startsOn = startsOn;
    }
    public void setStartsOn(String startsOn) {
        this.startsOn = DateTime.parse(startsOn);
    }
    /**
     * This is the latest date and time at which the activity should be show to users in the study 
     * applications.
     */
    public DateTime getEndsOn() {
        return endsOn;
    }
    @JsonProperty("endsOn")
    public void setEndsOn(DateTime endsOn) {
        this.endsOn = endsOn;
    }
    public void setEndsOn(String endsOn) {
        this.endsOn = DateTime.parse(endsOn);
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
    @JsonProperty("expires")
    public void setExpires(Period expires) {
        this.expires = expires;
    }
    public void setExpires(String expires) {
        this.expires = Period.parse(expires);
    }
    /**
     * A delay period between when a schedule event occurs, and when the schedule
     * creates the first scheduled activity.
     * @return
     */
    public Period getDelay() {
        return delay;
    }
    @JsonProperty("delay")
    public void setDelay(Period delay) {
        this.delay = delay;
    }
    public void setDelay(String delay) {
        this.delay = Period.parse(delay);
    }
    /**
     * The interval between activities produced by this scheduler.
     * @return
     */
    public Period getInterval() {
        return interval;
    }
    @JsonProperty("interval")
    public void setInterval(Period interval) {
        this.interval = interval;
        this.scheduleType = (interval == null) ? ScheduleType.ONCE : ScheduleType.RECURRING;
    }
    public void setInterval(String interval) {
        this.interval = Period.parse(interval);
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
    /**
     * A persistent schedule is one that keeps an activity alive in the list of scheduled 
     * activities, recreating it every time it is completed. Persistent schedules are 
     * scheduled to occur one time, but have an event ID that immediately triggers 
     * re-scheduling when one of the activities assigned by the schedule is completed.
     * @return
     */
    @JsonIgnore
    public boolean getPersistent() {
        if (activities != null) {
            for (Activity activity : activities) {
                if (activity.isPersistentlyRescheduledBy(this)) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean schedulesImmediatelyAfterEvent() {
        return getEventId() != null && 
               getScheduleType() == ScheduleType.ONCE &&        
               (getDelay() == null || getDelay().toDurationFrom(DateTime.now()).getMillis() <= 0L);
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
        result = prime * result + Objects.hashCode(eventId);
        result = prime * result + Objects.hashCode(delay);
        result = prime * result + Objects.hashCode(interval);
        result = prime * result + Objects.hashCode(times);
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
                && Objects.equals(label, other.label) && Objects.equals(scheduleType, other.scheduleType) 
                && Objects.equals(startsOn, other.startsOn) && Objects.equals(eventId, other.eventId)
                && Objects.equals(delay, other.delay) && Objects.equals(interval, other.interval) 
                && Objects.equals(times, other.times));
    }
    
    @Override
    public String toString() {
        return String.format("Schedule [label=%s, scheduleType=%s, cronTrigger=%s, startsOn=%s, endsOn=%s, expires=%s, eventId=%s, delay=%s, interval=%s, times=%s, persistent=%s, activities=%s]", 
                label, scheduleType, cronTrigger, startsOn, endsOn, expires, eventId, delay, interval, times, getPersistent(), activities);
    }
    
}
