package org.sagebionetworks.bridge.sdk.models.accounts;

import static org.sagebionetworks.bridge.sdk.utils.Utilities.TO_STRING_STYLE;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class UserConsentHistory {
    
    private final SubpopulationGuid subpopulationGuid;
    private final DateTime consentCreatedOn;
    private final String name;
    private final LocalDate birthdate;
    private final String imageData;
    private final String imageMimeType;
    private final DateTime signedOn;
    private final DateTime withdrewOn;
    private final boolean hasSignedActiveConsent;
    
    // Users will never create this object, so using constructor with final fields.
    @JsonCreator
    UserConsentHistory(@JsonProperty("subpopulationGuid") SubpopulationGuid subpopGuid, 
            @JsonProperty("consentCreatedOn") DateTime consentCreatedOn, 
            @JsonProperty("name") String name,
            @JsonProperty("birthdate") LocalDate birthdate, 
            @JsonProperty("imageData") String imageData, 
            @JsonProperty("imageMimeType") String imageMimeType, 
            @JsonProperty("signedOn") DateTime signedOn, 
            @JsonProperty("withdrewOn") DateTime withdrewOn,
            @JsonProperty("hasSignedActiveConsent") boolean hasSignedActiveConsent) {
        this.subpopulationGuid = subpopGuid;
        this.consentCreatedOn = consentCreatedOn;
        this.name = name;
        this.birthdate = birthdate;
        this.imageData = imageData;
        this.imageMimeType = imageMimeType;
        this.signedOn = signedOn;
        this.withdrewOn = withdrewOn;
        this.hasSignedActiveConsent = hasSignedActiveConsent;
    }

    public String getSubpopulationGuid() {
        return subpopulationGuid.getGuid();
    }

    public DateTime getConsentCreatedOn() {
        return consentCreatedOn;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public String getImageData() {
        return imageData;
    }

    public String getImageMimeType() {
        return imageMimeType;
    }

    public DateTime getSignedOn() {
        return signedOn;
    }

    public DateTime getWithdrewOn() {
        return withdrewOn;
    }

    public boolean isHasSignedActiveConsent() {
        return hasSignedActiveConsent;
    }

    @Override
    public int hashCode() {
        return Objects.hash(birthdate, consentCreatedOn, hasSignedActiveConsent, imageData, 
                imageMimeType, name, signedOn, subpopulationGuid, withdrewOn);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        UserConsentHistory other = (UserConsentHistory) obj;
        return Objects.equals(birthdate, other.birthdate) && Objects.equals(consentCreatedOn, other.consentCreatedOn)
                && Objects.equals(hasSignedActiveConsent, other.hasSignedActiveConsent) 
                && Objects.equals(imageData, other.imageData) && Objects.equals(imageMimeType, other.imageMimeType)
                && Objects.equals(name, other.name) && Objects.equals(signedOn, other.signedOn) 
                && Objects.equals(subpopulationGuid, other.subpopulationGuid)
                && Objects.equals(withdrewOn, other.withdrewOn);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE).append("subpopulationGuid", subpopulationGuid)
                .append("consentCreatedOn", consentCreatedOn).append("name", name).append("birthdate", birthdate)
                .append("imageData", imageData).append("imageMimeType", imageMimeType).append("signedOn", signedOn)
                .append("withdrewOn", withdrewOn).append("hasSignedActiveConsent", hasSignedActiveConsent).toString();
    }

}
