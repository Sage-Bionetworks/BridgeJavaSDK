package org.sagebionetworks.bridge.sdk.models.subpopulations;

import java.util.Objects;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class StudyConsent {

    private DateTime createdOn;
    private boolean active;
    private String documentContent;

    @JsonCreator
    private StudyConsent(@JsonProperty("timestamp") String createdOn, @JsonProperty("active") boolean active,
        @JsonProperty("documentContent") String documentContent) {
        this.createdOn = createdOn == null ? null : DateTime.parse(createdOn, ISODateTimeFormat.dateTime());
        this.active = active;
        this.documentContent = documentContent;
    }
    
    public StudyConsent() {
    }

    public DateTime getCreatedOn() {
        return createdOn;
    }

    public boolean isActive() {
        return active;
    }
    
    public String getDocumentContent() {
        return documentContent;
    }
    
    public void setDocumentContent(String documentContent) {
        this.documentContent = documentContent;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hash(createdOn);
        result = prime * result + Objects.hash(active);
        result = prime * result + Objects.hash(documentContent);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        StudyConsent other = (StudyConsent) obj;
        return (Objects.equals(createdOn, other.createdOn) && 
                Objects.equals(documentContent, other.documentContent) && 
                Objects.equals(active, other.active));
    }

    @Override
    public String toString() {
        return String.format("StudyConsent[createdOn=%s, active=%s, documentContent=%s]", 
                (createdOn == null) ? null : createdOn.toString(ISODateTimeFormat.dateTime()), active, documentContent);
    }
}
