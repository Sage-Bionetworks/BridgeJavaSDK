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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((schedule == null) ? 0 : schedule.hashCode());
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
        SimpleScheduleStrategy other = (SimpleScheduleStrategy) obj;
        if (schedule == null) {
            if (other.schedule != null)
                return false;
        } else if (!schedule.equals(other.schedule))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SimpleScheduleStrategy [schedule=" + schedule + "]";
    }
    
}
