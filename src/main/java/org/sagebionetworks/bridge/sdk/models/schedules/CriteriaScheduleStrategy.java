package org.sagebionetworks.bridge.sdk.models.schedules;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;

public class CriteriaScheduleStrategy implements ScheduleStrategy {

    private final List<ScheduleCriteria> scheduleCriteria = Lists.newArrayList();

    public void addCriteria(ScheduleCriteria criteria) {
        this.scheduleCriteria.add(criteria);
    }
    
    public List<ScheduleCriteria> getScheduleCriteria() {
        return scheduleCriteria;
    }
    
    public void setScheduleCriteria(List<ScheduleCriteria> criteria) {
        this.scheduleCriteria.clear();
        if (criteria != null) {
            this.scheduleCriteria.addAll(criteria);    
        }
    }    
    
    @Override
    public int hashCode() {
        return Objects.hash(scheduleCriteria);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        CriteriaScheduleStrategy other = (CriteriaScheduleStrategy) obj;
        return Objects.equals(scheduleCriteria, other.scheduleCriteria);
    }

    @Override
    public String toString() {
        return "CriteriaScheduleStrategy [scheduleCriteria=" + scheduleCriteria + "]";
    }

}
