package org.sagebionetworks.bridge.sdk.models.accounts;

import static org.sagebionetworks.bridge.sdk.utils.Utilities.TO_STRING_STYLE;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;

import org.sagebionetworks.bridge.sdk.Roles;
import org.sagebionetworks.bridge.sdk.models.users.SharingScope;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public final class StudyParticipant {

    private final String firstName;
    private final String lastName;
    private final String externalId;
    private final String password;
    private final SharingScope sharingScope;
    private final boolean notifyByEmail;
    private final String email;
    private final Set<String> dataGroups;
    private final String healthCode;
    private final Map<String,List<UserConsentHistory>> consentHistories;
    private final Set<Roles> roles;
    private final LinkedHashSet<String> languages;
    private final DateTime createdOn;
    private final AccountStatus status;
    // This has to be initialized to a value because the serialization flattens attributes 
    // as part of the object, so they are set via the @JsonAnySetter method
    private final Map<String,String> attributes = Maps.newHashMap();
    
    @JsonCreator
    StudyParticipant(@JsonProperty("firstName") String firstName, 
            @JsonProperty("lastName") String lastName, 
            @JsonProperty("email") String email, 
            @JsonProperty("externalId") String externalId,
            @JsonProperty("password") String password,
            @JsonProperty("sharingScope") SharingScope sharingScope,
            @JsonProperty("notifyByEmail") boolean notifyByEmail, 
            @JsonProperty("dataGroups") Set<String> dataGroups, 
            @JsonProperty("healthCode") String healthCode, 
            @JsonProperty("attributes") Map<String,String> attributes,
            @JsonProperty("consentHistories") Map<String,List<UserConsentHistory>> consentHistories, 
            @JsonProperty("roles") Set<Roles> roles, 
            @JsonProperty("createdOn") DateTime createdOn,
            @JsonProperty("status") AccountStatus status,
            @JsonProperty("languages") LinkedHashSet<String> languages) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.externalId = externalId;
        this.password = password;
        this.sharingScope = sharingScope;
        this.notifyByEmail = notifyByEmail;
        this.email = email;
        this.dataGroups = dataGroups;
        this.healthCode = healthCode;
        this.consentHistories = consentHistories;
        this.roles = roles;
        this.createdOn = createdOn;
        this.status = status;
        this.languages = languages;
        if (attributes != null) {
            this.attributes.putAll(attributes);
        }
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
    public String getPassword() {
        return password;
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
    public DateTime getCreatedOn() {
        return createdOn;
    }
    public AccountStatus getStatus() {
        return status;
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
        return Objects.hash(attributes, consentHistories, dataGroups, email, externalId, password, firstName, 
                lastName, healthCode, languages, notifyByEmail, roles, createdOn, status, sharingScope);
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
                && Objects.equals(externalId, other.externalId) && Objects.equals(password, other.password)
                && Objects.equals(firstName, other.firstName) && Objects.equals(healthCode, other.healthCode) 
                && Objects.equals(languages, other.languages) && Objects.equals(lastName, other.lastName) 
                && Objects.equals(notifyByEmail, other.notifyByEmail) && Objects.equals(roles, other.roles) 
                && Objects.equals(sharingScope, other.sharingScope) && Objects.equals(status, other.status)
                && Objects.equals(createdOn, other.createdOn);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE).append("firstName", firstName).append("lastName", lastName)
                .append("externalId", externalId).append("password", "[REDACTED]").append("sharingScope", sharingScope)
                .append("notifyByEmail", notifyByEmail).append("email", email).append("healthCode", "[REDACTED]")
                .append("dataGroups", dataGroups).append("attributes", attributes).append("roles", roles)
                .append("status", status).append("createdOn", createdOn).append("languages", languages)
                .append("consentHistories", consentHistories).toString();
    }
    
    public static class Builder {
        private String firstName;
        private String lastName;
        private String email;
        private String externalId;
        private String password;
        private SharingScope sharingScope;
        private boolean notifyByEmail;
        private Set<String> dataGroups = Sets.newHashSet();
        private Map<String,String> attributes = Maps.newHashMap();
        private LinkedHashSet<String> languages = new LinkedHashSet<>();
        private AccountStatus status;
        
        public Builder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }
        public Builder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        };
        public Builder withEmail(String email) {
            this.email = email;
            return this;
        };
        public Builder withExternalId(String externalId) {
            this.externalId = externalId;
            return this;
        }
        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }
        public Builder withSharingScope(SharingScope sharingScope) {
            this.sharingScope = sharingScope;
            return this;
        }
        public Builder withNotifyByEmail(boolean notifyByEmail) {
            this.notifyByEmail = notifyByEmail;
            return this;
        }
        public Builder withDataGroups(Set<String> dataGroups) {
            if (dataGroups != null) {
                this.dataGroups = dataGroups;
            }
            return this;
        }
        public Builder withLanguages(LinkedHashSet<String> languages) {
            if (languages != null) {
                this.languages = languages;
            }
            return this;
        }
        public Builder withAttributes(Map<String,String> attributes) {
            if (attributes != null) {
                this.attributes = attributes;
            }
            return this;
        }
        public Builder withStatus(AccountStatus status) {
            this.status = status;
            return this;
        }

        public StudyParticipant build() {
            return new StudyParticipant(firstName, lastName, email, externalId, password, sharingScope, notifyByEmail,
                    dataGroups, null, attributes, null, null, null, status, languages);
        }
    }    
}
