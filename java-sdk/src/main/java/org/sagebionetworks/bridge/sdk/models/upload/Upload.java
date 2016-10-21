package org.sagebionetworks.bridge.sdk.models.upload;

import static org.sagebionetworks.bridge.sdk.utils.BridgeUtils.TO_STRING_STYLE;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.sagebionetworks.bridge.sdk.models.healthData.RecordExportStatusRequest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A read only information object that describes the current status of an upload. This contains more
 * information than the UploadValidationStatus and may eventually replace it.
 */
public final class Upload {
    
    private final long contentLength;
    private final UploadStatus status;
    private final DateTime requestedOn;
    private final DateTime completedOn;
    private final UploadCompletionClient completedBy;
    private final LocalDate uploadDate;
    private final String uploadId;
    private final List<String> validationMessageList;

    private final RecordExportStatusRequest.ExporterStatus healthRecordExporterStatus;
    
    @JsonCreator
    public Upload(@JsonProperty("contentLength") long contentLength, @JsonProperty("status") UploadStatus status, 
            @JsonProperty("requestedOn") DateTime requestedOn, @JsonProperty("completedOn") DateTime completedOn,
            @JsonProperty("completedBy") UploadCompletionClient completedBy, 
            @JsonProperty("uploadDate") LocalDate uploadDate, @JsonProperty("uploadId") String uploadId, 
            @JsonProperty("validationMessageList") List<String> validationMessageList,
            @JsonProperty("healthRecordExporterStatus") RecordExportStatusRequest.ExporterStatus healthRecordExporterStatus) {
        this.contentLength = contentLength;
        this.status = status;
        this.requestedOn = requestedOn;
        this.completedOn = completedOn;
        this.completedBy = completedBy;
        this.uploadDate = uploadDate;
        this.uploadId = uploadId;
        this.validationMessageList = validationMessageList;
        this.healthRecordExporterStatus = healthRecordExporterStatus;
    }

    /**
     * The length of the content being uploaded. This is sent by the client. 
     */
    public long getContentLength() {
        return contentLength;
    }
    /**
     * The status of the upload. 
     */
    public UploadStatus getStatus() {
        return status;
    }
    /**
     * The timestamp of the initial request for an URL to upload data to the server. This timestamp will 
     * always be present, even if the upload never completes or fails validation.
     */
    public DateTime getRequestedOn() {
        return requestedOn;
    }
    /**
     * The timestamp when the upload is successfully uploaded and validated. Can be null.
     */
    public DateTime getCompletedOn() {
        return completedOn;
    }
    /**
     * The process or client that completed the upload. This can be done from the client but there is 
     * also now a process that responds when a file is successfully uploaded given the URL that is 
     * handed out by the Bridge API.
     */
    public UploadCompletionClient getCompletedBy() {
        return completedBy;
    }
    /**
     * The day on which the upload succeeded. Currently most study data is exported to Synapse on a daily 
     * schedule; this is the day this data will be exported to Synapse.
     */
    public LocalDate getUploadDate() {
        return uploadDate;
    }
    /**
     * The ID of the upload.
     */
    public String getUploadId() {
        return uploadId;
    }
    /**
     * If there upload was successful but the upload file failed validation, validation messages can be 
     * found in this list.
     */
    public List<String> getValidationMessageList() {
        return validationMessageList;
    }

    public RecordExportStatusRequest.ExporterStatus getHealthRecordExporterStatus() {
        return healthRecordExporterStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentLength, status, requestedOn, completedOn, completedBy, uploadDate, uploadId,
                validationMessageList, healthRecordExporterStatus);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Upload other = (Upload) obj;
        return Objects.equals(contentLength, other.contentLength)
                && Objects.equals(status, other.status)
                && Objects.equals(requestedOn, other.requestedOn)
                && Objects.equals(completedOn, other.completedOn)
                && Objects.equals(completedBy, other.completedBy) 
                && Objects.equals(uploadDate, other.uploadDate)
                && Objects.equals(uploadId, other.uploadId)
                && Objects.equals(validationMessageList, other.validationMessageList)
                && Objects.equals(healthRecordExporterStatus, other.healthRecordExporterStatus);
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE).append("contentLength", contentLength)
                .append("status", status).append("requestedOn", requestedOn)
                .append("completedOn", completedOn).append("completedBy", completedBy)
                .append("uploadDate", uploadDate).append("uploadId", uploadId)
                .append("validationMessageList", validationMessageList)
                .append("healthRecordExporterStatus", healthRecordExporterStatus).build();
    }
}
