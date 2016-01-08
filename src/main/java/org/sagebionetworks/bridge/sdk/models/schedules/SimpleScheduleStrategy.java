package org.sagebionetworks.bridge.sdk.models.schedules;

import java.util.Objects;

/**
 * The simplest (and default) schedule selection strategy for a schedule plan: all participants 
 * get the same schedule.
 */
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(schedule);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        SimpleScheduleStrategy other = (SimpleScheduleStrategy) obj;
        return Objects.equals(schedule, other.schedule);
    }

    @Override
    public String toString() {
        return String.format("SimpleScheduleStrategy [schedule=%s]", schedule);
    }
    
}
