package org.sagebionetworks.bridge.sdk.models.studies;

import static org.sagebionetworks.bridge.sdk.utils.Utilities.TO_STRING_STYLE;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as=Study.class)
public final class Study implements VersionHolder {
    
    private String name;
    private String sponsorName;
    private String identifier;
    private Long version;
    private String supportEmail;
    private Long synapseDataAccessTeamId;
    private String synapseProjectId;
    private String consentNotificationEmail;
    private String technicalEmail;
    private boolean usesCustomExportSchedule;
    private int minAgeOfConsent;
    private Set<String> userProfileAttributes;
    private Set<String> taskIdentifiers;
    private Set<String> dataGroups;
    private PasswordPolicy passwordPolicy;
    private EmailTemplate verifyEmailTemplate;
    private EmailTemplate resetPasswordTemplate;
    private boolean strictUploadValidationEnabled;
    private boolean healthCodeExportEnabled;
    private boolean emailVerificationEnabled;
    private boolean externalIdValidationEnabled;
    private EnumMap<OperatingSystem,Integer> minSupportedAppVersions;
    
    public Study() {
        userProfileAttributes = new HashSet<String>();
        taskIdentifiers = new HashSet<String>();
        dataGroups = new HashSet<String>();
        minSupportedAppVersions = new EnumMap<OperatingSystem,Integer>(OperatingSystem.class);
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

    /** Synapse team ID that is granted read access to exported health data records. */
    public Long getSynapseDataAccessTeamId() {
        return synapseDataAccessTeamId;
    }

    /** @see #getSynapseDataAccessTeamId */
    public void setSynapseDataAccessTeamId(Long synapseDataAccessTeamId) {
        this.synapseDataAccessTeamId = synapseDataAccessTeamId;
    }

    /** The Synapse project to export health data records to. */
    public String getSynapseProjectId() {
        return synapseProjectId;
    }

    /** @see #getSynapseProjectId */
    public void setSynapseProjectId(String synapseProjectId) {
        this.synapseProjectId = synapseProjectId;
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

    /**
     * By default, all studies are exported using the default nightly schedule. Some studies may need custom schedules
     * for hourly or on-demand exports. To prevent this study from being exported twice (once by the custom schedule,
     * once by the default schedule), you should set this attribute to true.
     */
    public boolean getUsesCustomExportSchedule() {
        return usesCustomExportSchedule;
    }

    /** @see #getUsesCustomExportSchedule */
    public void setUsesCustomExportSchedule(boolean usesCustomExportSchedule) {
        this.usesCustomExportSchedule = usesCustomExportSchedule;
    }

    public int getMinAgeOfConsent() {
        return minAgeOfConsent;
    }

    public void setMinAgeOfConsent(int minAgeOfConsent) {
        this.minAgeOfConsent = minAgeOfConsent;
    }

    public Set<String> getUserProfileAttributes() {
        return userProfileAttributes;
    }
    
    public void setUserProfileAttributes(Set<String> attributes) {
        this.userProfileAttributes = attributes;
    }

    public Set<String> getTaskIdentifiers() {
        return taskIdentifiers;
    }
    
    public void setDataGroups(Set<String> dataGroups) {
        this.dataGroups = dataGroups;
    }
    
    public Set<String> getDataGroups() {
        return dataGroups;
    }
    
    public void setTaskIdentifiers(Set<String> taskIdentifiers) {
        this.taskIdentifiers = taskIdentifiers;
    }
    
    public boolean isStrictUploadValidationEnabled() {
        return strictUploadValidationEnabled;
    }

    public void setStrictUploadValidationEnabled(boolean enabled) {
        this.strictUploadValidationEnabled = enabled;
    }
    
    public boolean isHealthCodeExportEnabled() {
        return healthCodeExportEnabled;
    }
    
    public void setHealthCodeExportEnabled(boolean enabled) {
        this.healthCodeExportEnabled = enabled;
    }
    
    public boolean isEmailVerificationEnabled() {
        return this.emailVerificationEnabled;
    }
    
    public void setEmailVerificationEnabled(boolean emailVerificationEnabled) {
        this.emailVerificationEnabled = emailVerificationEnabled;
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

    public EnumMap<OperatingSystem,Integer> getMinSupportedAppVersions() {
        return minSupportedAppVersions;
    }
    
    public void setMinSupportedAppVersions(EnumMap<OperatingSystem,Integer> minSupportedAppVersions) {
        this.minSupportedAppVersions = minSupportedAppVersions;
    }

    public boolean isExternalIdValidationEnabled() {
        return externalIdValidationEnabled;
    }
    
    public void setExternalIdValidationEnabled(boolean externalIdValidationEnabled) {
        this.externalIdValidationEnabled = externalIdValidationEnabled;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, sponsorName, identifier, supportEmail, synapseDataAccessTeamId, synapseProjectId,
                consentNotificationEmail, version, emailVerificationEnabled, technicalEmail, userProfileAttributes,
                taskIdentifiers, dataGroups, passwordPolicy, verifyEmailTemplate, resetPasswordTemplate,
                minAgeOfConsent, strictUploadValidationEnabled, healthCodeExportEnabled, minSupportedAppVersions, 
                externalIdValidationEnabled, usesCustomExportSchedule);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Study other = (Study) obj;
        return (Objects.equals(consentNotificationEmail, other.consentNotificationEmail)
                && Objects.equals(identifier, other.identifier) && Objects.equals(minAgeOfConsent, other.minAgeOfConsent) 
                && Objects.equals(name, other.name) && Objects.equals(supportEmail, other.supportEmail)
                && Objects.equals(synapseDataAccessTeamId, other.synapseDataAccessTeamId)
                && Objects.equals(synapseProjectId, other.synapseProjectId)
                && Objects.equals(version, other.version)
                && Objects.equals(userProfileAttributes, other.userProfileAttributes)
                && Objects.equals(taskIdentifiers, other.taskIdentifiers)
                && Objects.equals(dataGroups, other.dataGroups) && Objects.equals(sponsorName, other.sponsorName)
                && Objects.equals(technicalEmail, other.technicalEmail)
                && Objects.equals(usesCustomExportSchedule, other.usesCustomExportSchedule)
                && Objects.equals(verifyEmailTemplate, other.verifyEmailTemplate)
                && Objects.equals(passwordPolicy, other.passwordPolicy)
                && Objects.equals(resetPasswordTemplate, other.resetPasswordTemplate)
                && Objects.equals(strictUploadValidationEnabled, other.strictUploadValidationEnabled)
                && Objects.equals(healthCodeExportEnabled, other.healthCodeExportEnabled)
                && Objects.equals(emailVerificationEnabled, other.emailVerificationEnabled)
                && Objects.equals(minSupportedAppVersions, other.minSupportedAppVersions)
                && Objects.equals(externalIdValidationEnabled, other.externalIdValidationEnabled));
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE).append("name", name).append("sponsorName", sponsorName)
                .append("identifier", identifier).append("version", version).append("supportEmail", supportEmail)
                .append("synapseDataAccessTeamId", synapseDataAccessTeamId)
                .append("synapseProjectId", synapseProjectId)
                .append("consentNotificationEmail", consentNotificationEmail).append("technicalEmail", technicalEmail)
                .append("usesCustomExportSchedule", usesCustomExportSchedule)
                .append("minAgeOfConsent", minAgeOfConsent).append("userProfileAttributes", userProfileAttributes)
                .append("taskIdentifiers", taskIdentifiers).append("dataGroups", dataGroups)
                .append("strictUploadValidationEnabled", strictUploadValidationEnabled)
                .append("healthCodeExportEnabled", healthCodeExportEnabled).append("passwordPolicy", passwordPolicy)
                .append("minSupportedAppVersions", minSupportedAppVersions).append("verifyEmailTemplate", verifyEmailTemplate)
                .append("emailVerificationEnabled", emailVerificationEnabled)
                .append("resetPasswordTemplate", resetPasswordTemplate)
                .append("externalIdValidationEnabled", externalIdValidationEnabled).toString();
    }
}
