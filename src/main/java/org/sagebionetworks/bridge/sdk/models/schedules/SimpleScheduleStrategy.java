package org.sagebionetworks.bridge.sdk.models.schedules;

public class SimpleScheduleStrategy implements ScheduleStrategy {

    private Schedule schedule;

    public SimpleScheduleStrategy() {
    }
    
    public SimpleScheduleStrategy(Schedule schedule) {
        this.schedule = schedule;
    }
    
    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    @Override
    public String toString() {
        return "SimpleScheduleStrategy [schedule=" + schedule + "]";
    }
    
}
