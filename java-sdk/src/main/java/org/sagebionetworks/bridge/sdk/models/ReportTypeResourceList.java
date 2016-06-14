package org.sagebionetworks.bridge.sdk.models;

import java.util.List;

import org.sagebionetworks.bridge.sdk.models.reports.ReportType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReportTypeResourceList<T> {
    
    private final List<T> items;
    private final ReportType reportType;

    @JsonCreator
    private ReportTypeResourceList(@JsonProperty("items") List<T> items,
            @JsonProperty("reportType") ReportType reportType) {
        this.items = items;
        this.reportType = reportType;
    }
    public List<T> getItems() {
        return items;
    }
    public int getTotal() {
        return items.size();
    }
    public ReportType getReportType() {
        return reportType;
    }
}
