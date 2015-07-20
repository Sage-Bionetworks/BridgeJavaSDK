package org.sagebionetworks.bridge.sdk.models.schedules;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class TaskReference {
    
    private final String identifier;
    
    @JsonCreator
    public TaskReference(@JsonProperty("identifier") String identifier) {
        checkArgument(isNotBlank(identifier));
        this.identifier = identifier;
    }
    
    public String getIdentifier() {
        return this.identifier;
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
        TaskReference other = (TaskReference) obj;
        return (Objects.equals(identifier, other.identifier));
    }

    @Override
    public String toString() {
        return "TaskReference [identifier=" + identifier + "]";
    }

}
