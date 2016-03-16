package org.sagebionetworks.bridge.sdk.models.accounts;

import static org.sagebionetworks.bridge.sdk.utils.Utilities.TO_STRING_STYLE;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.sagebionetworks.bridge.sdk.Roles;
import org.sagebionetworks.bridge.sdk.models.users.SharingScope;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class StudyParticipant {

    private final String firstName;
    private final String lastName;
    private final String externalId;
    private final SharingScope sharingScope;
    private final boolean notifyByEmail;
    private final String email;
    private final Set<String> dataGroups;
    private final String healthCode;
    private final Map<String,String> attributes;
    private final Map<String,List<UserConsentHistory>> consentHistories;
    private final Set<Roles> roles;
    private final LinkedHashSet<String> languages;
    
    // Currently no plan that users will create this object, so using constructor with final fields.
    @JsonCreator
    StudyParticipant(@JsonProperty("firstName") String firstName, 
            @JsonProperty("lastName") String lastName, 
            @JsonProperty("email") String email, 
            @JsonProperty("externalId") String externalId, 
            @JsonProperty("sharingScope") SharingScope sharingScope,
            @JsonProperty("notifyByEmail") boolean notifyByEmail, 
            @JsonProperty("dataGroups") Set<String> dataGroups, 
            @JsonProperty("healthCode") String healthCode, 
            @JsonProperty("attributes") Map<String,String> attributes,
            @JsonProperty("consentHistories") Map<String,List<UserConsentHistory>> consentHistories, 
            @JsonProperty("roles") Set<Roles> roles, 
            @JsonProperty("languages") LinkedHashSet<String> languages) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.externalId = externalId;
        this.sharingScope = sharingScope;
        this.notifyByEmail = notifyByEmail;
        
        this.email = email;
        this.dataGroups = dataGroups;
        this.healthCode = healthCode;
        this.attributes = attributes;
        this.consentHistories = consentHistories;
        this.roles = roles;
        this.languages = languages;
    }
    
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getEmail() {
        return email;
    }
    public String getExternalId() {
        return externalId;
    }
    public SharingScope getSharingScope() {
        return sharingScope;
    }
    public boolean isNotifyByEmail() {
        return notifyByEmail;
    }
    public Set<String> getDataGroups() {
        return dataGroups;
    }
    public String getHealthCode() {
        return healthCode;
    }
    public Map<String,String> getAttributes() {
        return attributes;
    }
    public Map<String, List<UserConsentHistory>> getConsentHistories() {
        return consentHistories;
    }
    public Set<Roles> getRoles() {
        return roles;
    }
    public LinkedHashSet<String> getLanguages() {
        return languages;
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributes, consentHistories, dataGroups, email, externalId, firstName, 
                lastName, healthCode, languages, notifyByEmail, roles, sharingScope);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        StudyParticipant other = (StudyParticipant) obj;
        return Objects.equals(attributes, other.attributes) && Objects.equals(consentHistories, other.consentHistories)
                && Objects.equals(dataGroups, other.dataGroups) && Objects.equals(email, other.email)
                && Objects.equals(externalId, other.externalId) && Objects.equals(firstName, other.firstName)
                && Objects.equals(healthCode, other.healthCode) && Objects.equals(languages, other.languages)
                && Objects.equals(lastName, other.lastName) && Objects.equals(notifyByEmail, other.notifyByEmail)
                && Objects.equals(roles, other.roles) && Objects.equals(sharingScope, other.sharingScope);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE).append("firstName", firstName).append("lastName", lastName)
                .append("externalId", externalId).append("sharingScope", sharingScope)
                .append("notifyByEmail", notifyByEmail).append("email", email).append("healthCode", "[REDACTED]")
                .append("dataGroups", dataGroups).append("attributes", attributes).append("roles", roles)
                .append("languages", languages).append("consentHistories", consentHistories).toString();
    }
}
