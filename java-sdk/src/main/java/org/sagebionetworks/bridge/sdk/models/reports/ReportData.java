package org.sagebionetworks.bridge.sdk.models.reports;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Record representing the data for a report entry (for a specific day). The SDK expects this data 
 * to be in JSON format so it is parsed here into an object model, though the REST API does not 
 * require this.
 */
public class ReportData {
    private final LocalDate date;
    private final JsonNode reportData;
    
    @JsonCreator
    public ReportData(@JsonProperty("date") LocalDate date, @JsonProperty("data") JsonNode reportData) {
        this.date = date;
        this.reportData = reportData;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    @JsonProperty("data")
    public JsonNode getReportData() {
        return reportData;
    }
}
