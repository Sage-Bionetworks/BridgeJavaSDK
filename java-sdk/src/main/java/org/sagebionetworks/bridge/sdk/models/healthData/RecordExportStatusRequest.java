package org.sagebionetworks.bridge.sdk.models.healthData;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Represents a record export status request to the Bridge Server. */
public class RecordExportStatusRequest {
    public enum ExporterStatus { NOT_EXPORTED, SUCCEEDED }

    private List<String> recordIds;
    private ExporterStatus synapseExporterStatus;

    public List<String> getRecordIds() { return this.recordIds; }

    public ExporterStatus getSynapseExporterStatus() { return this.synapseExporterStatus; }

    @JsonCreator
    public RecordExportStatusRequest(@JsonProperty("recordIds") List<String> recordIds, @JsonProperty("synapseExporterStatus") ExporterStatus synapseExporterStatus) {
        this.recordIds = recordIds;
        this.synapseExporterStatus = synapseExporterStatus;
    }
    public void setRecordIds(List<String> recordIds) { this.recordIds = recordIds; }

    public void setSynapseExporterStatus(ExporterStatus synapseExporterStatus) { this.synapseExporterStatus = synapseExporterStatus; }
}
