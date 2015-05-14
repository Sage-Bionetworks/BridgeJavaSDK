package org.sagebionetworks.bridge.sdk.models.studies;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as=Study.class)
public final class Study implements VersionHolder {
    
    private String name;
    private String identifier;
    private Long version;
    private String supportEmail;
    private String consentNotificationEmail;
    private int minAgeOfConsent;
    private int maxNumOfParticipants;
    private Set<String> userProfileAttributes;

    public Study() {
        userProfileAttributes = new HashSet<String>();
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
    
    public String getSupportEmail() {
        return supportEmail;
    }

    public void setSupportEmail(String supportEmail) {
        this.supportEmail = supportEmail;
    }

    public String getConsentNotificationEmail() {
        return consentNotificationEmail;
    }

    public void setConsentNotificationEmail(String consentNotificationEmail) {
        this.consentNotificationEmail = consentNotificationEmail;
    }

    public int getMinAgeOfConsent() {
        return minAgeOfConsent;
    }

    public void setMinAgeOfConsent(int minAgeOfConsent) {
        this.minAgeOfConsent = minAgeOfConsent;
    }

    public int getMaxNumOfParticipants() {
        return maxNumOfParticipants;
    }

    public void setMaxNumOfParticipants(int maxParticipants) {
        this.maxNumOfParticipants = maxParticipants;
    }

    public Set<String> getUserProfileAttributes() {
        return userProfileAttributes;
    }
    
    public void setUserProfileAttributes(Set<String> attributes) {
        this.userProfileAttributes = attributes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(consentNotificationEmail);
        result = prime * result + Objects.hashCode(identifier);
        result = prime * result + Objects.hashCode(name);
        result = prime * result + Objects.hashCode(supportEmail);
        result = prime * result + Objects.hashCode(version);
        result = prime * result + Objects.hashCode(userProfileAttributes);
        result = prime * result + maxNumOfParticipants;
        result = prime * result + minAgeOfConsent;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Study other = (Study) obj;
        return (Objects.equals(consentNotificationEmail, other.consentNotificationEmail)
                && Objects.equals(identifier, other.identifier)
                && (maxNumOfParticipants == other.maxNumOfParticipants) && (minAgeOfConsent == other.minAgeOfConsent)
                && Objects.equals(name, other.name) && Objects.equals(supportEmail, other.supportEmail) 
                && Objects.equals(version, other.version) && Objects.equals(userProfileAttributes, other.userProfileAttributes));
    }

    @Override
    public String toString() {
        return String.format("Study [name=%s, identifier=%s, version=%s, supportEmail=%s, consentNotificationEmail=%s, minAgeOfConsent=%s, maxNumOfParticipants=%s, userProfileAttributes=%s]", 
                    name, identifier, version, supportEmail, consentNotificationEmail, minAgeOfConsent, maxNumOfParticipants, userProfileAttributes);
    }

}
