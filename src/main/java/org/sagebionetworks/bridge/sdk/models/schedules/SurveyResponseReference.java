package org.sagebionetworks.bridge.sdk.models.schedules;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SurveyResponseReference {
    
    private final String guid;
    private final String href;
    
    @JsonCreator
    private SurveyResponseReference(@JsonProperty("guid") String guid, @JsonProperty("href") String href) {
        checkArgument(isNotBlank(guid));
        this.guid = guid;
        this.href = href;
    }
    
    public String getGuid() {
        return guid;
    }
    
    public String getHref() {
        return href;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(guid);
        result = prime * result + Objects.hashCode(href);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        SurveyResponseReference other = (SurveyResponseReference) obj;
        return (Objects.equals(guid, other.guid) && Objects.equals(href, other.href));
    }

    @Override
    public String toString() {
        return "SurveyResponseReference [guid=" + guid + ", href=" + getHref() + "]";
    }
}
