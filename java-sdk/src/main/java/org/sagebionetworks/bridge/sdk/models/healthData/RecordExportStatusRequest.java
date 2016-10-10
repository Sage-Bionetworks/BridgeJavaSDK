package org.sagebionetworks.bridge.sdk.models.healthData;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.sagebionetworks.bridge.sdk.Config;
import org.sagebionetworks.bridge.sdk.models.upload.UploadCompletionClient;
import org.sagebionetworks.bridge.sdk.models.upload.UploadStatus;

import java.util.List;

/** Represents a record export status request to the Bridge Server. */
public class RecordExportStatusRequest {

    private List<String> recordIds;
    private Config.ExporterStatus synapseExporterStatus;

    public List<String> getRecordIds() { return this.recordIds; }

    public Config.ExporterStatus getSynapseExporterStatus() { return this.synapseExporterStatus; }

    @JsonCreator
    public RecordExportStatusRequest(@JsonProperty("recordIds") List<String> recordIds, @JsonProperty("synapseExporterStatus") Config.ExporterStatus synapseExporterStatus) {
        this.recordIds = recordIds;
        this.synapseExporterStatus = synapseExporterStatus;
    }
    public void setRecordIds(List<String> recordIds) { this.recordIds = recordIds; }

    public void setSynapseExporterStatus(Config.ExporterStatus synapseExporterStatus) { this.synapseExporterStatus = synapseExporterStatus; }
}
