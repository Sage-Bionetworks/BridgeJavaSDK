package org.sagebionetworks.bridge.sdk.models.schedules;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ABTestGroup {
    private final int percentage;
    private final Schedule schedule;
    
    @JsonCreator
    public ABTestGroup(@JsonProperty("percentage") int percentage, @JsonProperty("schedule") Schedule schedule) {
        this.percentage = percentage;
        this.schedule = schedule;
    }
    public int getPercentage() {
        return percentage;
    }
    public Schedule getSchedule() {
        return schedule;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + percentage;
        result = prime * result + Objects.hashCode(schedule);
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ABTestGroup other = (ABTestGroup) obj;
        return (percentage == other.percentage && Objects.equals(schedule, other.schedule));
    }
    @Override
    public String toString() {
        return String.format("ABTestGroup [percentage=%s, schedule=%s]", percentage, schedule);
    }
}

