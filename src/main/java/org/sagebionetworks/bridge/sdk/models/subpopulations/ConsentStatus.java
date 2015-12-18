package org.sagebionetworks.bridge.sdk.models.subpopulations;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.sagebionetworks.bridge.sdk.utils.Utilities.TO_STRING_STYLE;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class ConsentStatus {

    public static boolean isUserConsented(Map<SubpopulationGuid,ConsentStatus> statuses) {
        checkNotNull(statuses);
        if (statuses.isEmpty()) {
            return false;
        }
        for (ConsentStatus status : statuses.values()) {
            if (status.isRequired() && !status.isConsented()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Are all the required consents up-to-date?
     * @return
     */
    public static boolean isConsentCurrent(Map<SubpopulationGuid,ConsentStatus> statuses) {
        checkNotNull(statuses);
        if (statuses.isEmpty()) {
            return false;
        }
        for (ConsentStatus status : statuses.values()) {
            if (status.isRequired() && !status.isMostRecentConsent()) {
                return false;
            }
        }
        return true;
    }

    private final String name;
    private final String subpopulationGuid;
    private final boolean required;
    private final boolean consented;
    private final boolean mostRecentConsent;
    
    @JsonCreator
    public ConsentStatus(@JsonProperty("name") String name, @JsonProperty("subpopulationGuid") String subpopulationGuid,
            @JsonProperty("required") boolean isRequired, @JsonProperty("consented") boolean isConsented, 
            @JsonProperty("mostRecentConsent") boolean isMostRecentConsent) {
        this.name = checkNotNull(name);
        this.subpopulationGuid = checkNotNull(subpopulationGuid);
        this.required = isRequired;
        this.consented = isConsented;
        this.mostRecentConsent = isMostRecentConsent;
    }
    
    public String getName() {
        return name;
    }

    public String getSubpopulationGuid() {
        return subpopulationGuid;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isConsented() {
        return consented;
    }
    
    public boolean isMostRecentConsent() {
        return mostRecentConsent;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, subpopulationGuid, required, consented, mostRecentConsent);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ConsentStatus other = (ConsentStatus) obj;
        return Objects.equals(name, other.name) && Objects.equals(subpopulationGuid, other.subpopulationGuid)
                && Objects.equals(required, other.required) && Objects.equals(consented, other.consented)
                && Objects.equals(mostRecentConsent, other.mostRecentConsent);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE)
                .append("name", name)
                .append("subpopulationGuid", subpopulationGuid)
                .append("isRequired", required)
                .append("isConsented", consented)
                .append("isMostRecentConsent", mostRecentConsent).toString();
    }

}
