package org.sagebionetworks.bridge.sdk.models.healthData;

import java.util.Objects;

/**
 * Represents a health data record in db
 * Does NOT includes all other values right now except exporter status for simplicity, will add those values if needed in the future
 */
public class HealthDataRecord {
    private String id;
    private String uploadId;
    private RecordExportStatusRequest.ExporterStatus synapseExporterStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public RecordExportStatusRequest.ExporterStatus getSynapseExporterStatus() {
        return synapseExporterStatus;
    }

    public void setSynapseExporterStatus(RecordExportStatusRequest.ExporterStatus synapseExporterStatus) {
        this.synapseExporterStatus = synapseExporterStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uploadId, synapseExporterStatus);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        HealthDataRecord other = (HealthDataRecord) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(uploadId, other.uploadId)
                && Objects.equals(synapseExporterStatus, other.synapseExporterStatus);
    }
}
