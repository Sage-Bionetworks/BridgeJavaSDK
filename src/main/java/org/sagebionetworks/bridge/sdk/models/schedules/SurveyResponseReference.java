package org.sagebionetworks.bridge.sdk.models.schedules;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class SurveyResponseReference {
    
    private final String identifier;
    private final String href;
    
    @JsonCreator
    private SurveyResponseReference(@JsonProperty("identifier") String identifier, @JsonProperty("href") String href) {
        checkArgument(isNotBlank(identifier));
        this.identifier = identifier;
        this.href = href;
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    public String getHref() {
        return href;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(identifier);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        SurveyResponseReference other = (SurveyResponseReference) obj;
        return (Objects.equals(identifier, other.identifier));
    }

    @Override
    public String toString() {
        return "SurveyResponseReference [identifier=" + identifier + ", href=" + getHref() + "]";
    }
}
