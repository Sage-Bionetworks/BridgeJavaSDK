package org.sagebionetworks.bridge.sdk.models.upload;

import static org.sagebionetworks.bridge.sdk.utils.Utilities.TO_STRING_STYLE;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

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
    
    @JsonCreator
    public Upload(@JsonProperty("contentLength") long contentLength, @JsonProperty("status") UploadStatus status, 
            @JsonProperty("requestedOn") DateTime requestedOn, @JsonProperty("completedOn") DateTime completedOn,
            @JsonProperty("completedBy") UploadCompletionClient completedBy, 
            @JsonProperty("uploadDate") LocalDate uploadDate, @JsonProperty("uploadId") String uploadId, 
            @JsonProperty("validationMessageList") List<String> validationMessageList) {
        this.contentLength = contentLength;
        this.status = status;
        this.requestedOn = requestedOn;
        this.completedOn = completedOn;
        this.completedBy = completedBy;
        this.uploadDate = uploadDate;
        this.uploadId = uploadId;
        this.validationMessageList = validationMessageList;
    }

    public long getContentLength() {
        return contentLength;
    }
    public UploadStatus getStatus() {
        return status;
    }
    public DateTime getRequestedOn() {
        return requestedOn;
    }
    public DateTime getCompletedOn() {
        return completedOn;
    }
    public UploadCompletionClient getCompletedBy() {
        return completedBy;
    }
    public LocalDate getUploadDate() {
        return uploadDate;
    }
    public String getUploadId() {
        return uploadId;
    }
    public List<String> getValidationMessageList() {
        return validationMessageList;
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentLength, status, requestedOn, completedOn, completedBy, uploadDate, uploadId,
                validationMessageList);
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
                && Objects.equals(validationMessageList, other.validationMessageList);
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE).append("contentLength", contentLength)
                .append("status", status).append("requestedOn", requestedOn)
                .append("completedOn", completedOn).append("completedBy", completedBy)
                .append("uploadDate", uploadDate).append("uploadId", uploadId)
                .append("validationMessageList", validationMessageList).build();
    }
}
