package org.sagebionetworks.bridge.sdk.models.studies;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as=Study.class)
public final class Study implements VersionHolder {
    
    private String name;
    private String sponsorName;
    private String identifier;
    private Long version;
    private String supportEmail;
    private String consentNotificationEmail;
    private String technicalEmail;
    private int minAgeOfConsent;
    private int maxNumOfParticipants;
    private Set<String> userProfileAttributes;
    private PasswordPolicy passwordPolicy;
    private EmailTemplate verifyEmailTemplate;
    private EmailTemplate resetPasswordTemplate;

    public Study() {
        userProfileAttributes = new HashSet<String>();
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getSponsorName() {
        return sponsorName;
    }
    
    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
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
    
    public String getTechnicalEmail() {
        return technicalEmail;
    }
    
    public void setTechnicalEmail(String technicalEmail) {
        this.technicalEmail = technicalEmail;
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

    public PasswordPolicy getPasswordPolicy() {
        return passwordPolicy;
    }

    public void setPasswordPolicy(PasswordPolicy passwordPolicy) {
        this.passwordPolicy = passwordPolicy;
    }

    public EmailTemplate getVerifyEmailTemplate() {
        return verifyEmailTemplate;
    }

    public void setVerifyEmailTemplate(EmailTemplate verifyEmailTemplate) {
        this.verifyEmailTemplate = verifyEmailTemplate;
    }

    public EmailTemplate getResetPasswordTemplate() {
        return resetPasswordTemplate;
    }

    public void setResetPasswordTemplate(EmailTemplate resetPasswordTemplate) {
        this.resetPasswordTemplate = resetPasswordTemplate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(name);
        result = prime * result + Objects.hashCode(sponsorName);
        result = prime * result + Objects.hashCode(identifier);
        result = prime * result + Objects.hashCode(supportEmail);
        result = prime * result + Objects.hashCode(consentNotificationEmail);
        result = prime * result + Objects.hashCode(technicalEmail);
        result = prime * result + Objects.hashCode(version);
        result = prime * result + Objects.hashCode(userProfileAttributes);
        result = prime * result + Objects.hashCode(passwordPolicy);
        result = prime * result + Objects.hashCode(verifyEmailTemplate);
        result = prime * result + Objects.hashCode(resetPasswordTemplate);
        result = prime * result + Objects.hashCode(maxNumOfParticipants);
        result = prime * result + Objects.hashCode(minAgeOfConsent);
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
                && Objects.equals(version, other.version) && Objects.equals(userProfileAttributes, other.userProfileAttributes)
                && Objects.equals(sponsorName, other.sponsorName) && Objects.equals(technicalEmail, other.technicalEmail) 
                && Objects.equals(verifyEmailTemplate, other.verifyEmailTemplate) && Objects.equals(passwordPolicy, other.passwordPolicy)
                && Objects.equals(resetPasswordTemplate, other.resetPasswordTemplate));
    }

    @Override
    public String toString() {
        return String.format("Study [name=%s, sponsorName=%s, identifier=%s, version=%s, supportEmail=%s, consentNotificationEmail=%s, technicalEmail=%s, minAgeOfConsent=%s, maxNumOfParticipants=%s, userProfileAttributes=%s, passwordPolicy=%s, verifyEmailTemplate=%s, resetPasswordTemplate=%s]",
            name, sponsorName, identifier, version, supportEmail, consentNotificationEmail, technicalEmail, minAgeOfConsent, maxNumOfParticipants, userProfileAttributes, passwordPolicy, verifyEmailTemplate, resetPasswordTemplate);
    }
}
