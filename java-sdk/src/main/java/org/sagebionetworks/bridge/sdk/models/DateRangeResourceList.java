package org.sagebionetworks.bridge.sdk.models;

import java.util.List;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DateRangeResourceList<T> {
    
    private final List<T> items;
    private final int total;
    private LocalDate startDate;
    private LocalDate endDate;

    @JsonCreator
    DateRangeResourceList(@JsonProperty("items") List<T> items, @JsonProperty("total") int total,
            @JsonProperty("startDate") LocalDate startDate, @JsonProperty("endDate") LocalDate endDate) {
        this.items = items;
        this.startDate = startDate;
        this.endDate = endDate;
        this.total = total;
    }
    public List<T> getItems() {
        return items;
    }
    public int getTotal() {
        return total;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public LocalDate getEndDate() {
        return endDate;

    }
    @Override
    public String toString() {
        return "DateRangeResourceList [items=" + items + ", total=" + total + ", startDate=" + startDate + ", endDate="
                + endDate + "]";
    }
}
