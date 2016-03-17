package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Properties;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;
import org.sagebionetworks.bridge.sdk.models.users.SignInCredentials;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

public final class Config {

    private static final String CONFIG_FILE = "/bridge-sdk.properties";
    private static final String USER_CONFIG_FILE = System.getProperty("user.home") + "/bridge-sdk.properties";

    public static enum Props {
        ACCOUNT_EMAIL, 
        ACCOUNT_PASSWORD, 
        ADMIN_EMAIL, 
        ADMIN_PASSWORD, 
        DEV_NAME, 
        ENV, 
        LOG_LEVEL, 
        SDK_VERSION, 
        STUDY_IDENTIFIER, 
        V3_ACTIVITIES, 
        V3_AUTH_REQUESTRESETPASSWORD, 
        V3_AUTH_RESENDEMAILVERIFICATION, 
        V3_AUTH_RESETPASSWORD, 
        V3_AUTH_SIGNIN, 
        V3_AUTH_SIGNOUT, 
        V3_AUTH_SIGNUP, 
        V3_AUTH_VERIFYEMAIL, 
        V3_BACKFILL_NAME, 
        V3_BACKFILL_NAME_START, 
        V3_CACHE, 
        V3_CACHE_CACHEKEY,
        V3_PARTICIPANT,
        V3_PARTICIPANTS,
        V3_SCHEDULEPLANS, 
        V3_SCHEDULEPLANS_GUID, 
        V3_STUDIES, 
        V3_STUDIES_IDENTIFIER,
        V3_STUDIES_SELF, 
        V3_SUBPOPULATION,
        V3_SUBPOPULATIONS,
        V3_SUBPOPULATIONS_CONSENTS,
        V3_SUBPOPULATIONS_CONSENTS_PUBLISHED,
        V3_SUBPOPULATIONS_CONSENTS_RECENT,
        V3_SUBPOPULATIONS_CONSENTS_SIGNATURE,
        V3_SUBPOPULATIONS_CONSENTS_SIGNATURE_EMAIL,
        V3_SUBPOPULATIONS_CONSENTS_SIGNATURE_WITHDRAW,
        V3_SUBPOPULATIONS_CONSENTS_TIMESTAMP,
        V3_SUBPOPULATIONS_CONSENTS_TIMESTAMP_PUBLISH,        
        V3_SURVEYRESPONSES, 
        V3_SURVEYRESPONSES_IDENTIFIER, 
        V3_SURVEYS, 
        V3_SURVEYS_PUBLISHED, 
        V3_SURVEYS_RECENT, 
        V3_SURVEYS_SURVEYGUID_REVISIONS, 
        V3_SURVEYS_SURVEYGUID_REVISIONS_CREATEDON, 
        V3_SURVEYS_SURVEYGUID_REVISIONS_CREATEDON_PHYSICAL_TRUE, 
        V3_SURVEYS_SURVEYGUID_REVISIONS_CREATEDON_PUBLISH, 
        V3_SURVEYS_SURVEYGUID_REVISIONS_CREATEDON_VERSION, 
        V3_SURVEYS_SURVEYGUID_REVISIONS_PUBLISHED, 
        V3_SURVEYS_SURVEYGUID_REVISIONS_RECENT, 
        V3_UPLOADS, 
        V3_UPLOADSCHEMAS, 
        V3_UPLOADSCHEMAS_SCHEMAID, 
        V3_UPLOADSCHEMAS_SCHEMAID_RECENT, 
        V3_UPLOADSCHEMAS_SCHEMAID_REVISIONS_REV, 
        V3_UPLOADSTATUSES_UPLOADID, 
        V3_UPLOADS_UPLOADID_COMPLETE, 
        V3_USERS,
        V3_USERS_SIGNOUT,
        V3_USERS_EMAILPARTICIPANTROSTER, 
        V3_USERS_SELF,
        V3_USERS_SELF_DATAGROUPS,
        V3_USERS_SELF_DATASHARING, 
        V3_USERS_SELF_EMAILDATA, 
        V3_USERS_SELF_EXTERNALID, 
        V3_USERS_SELF_UNSUBSCRIBEEMAIL, 
        V4_SCHEDULES;

        public String getPropertyName() {
            return this.name().replace("_", ".").toLowerCase();
        }
    }

    private Properties config;
    private Environment environment;

    Config() {
        config = new Properties();

        // Load from default configuration file
        try (InputStream in = this.getClass().getResourceAsStream(CONFIG_FILE)) {
            config.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Overwrite from user's local file
        loadProperties(USER_CONFIG_FILE, config);

        // Finally, overwrite from environment variables and system properties
        for (Props key : Props.values()) {
            String value = System.getenv(key.name());
            if (value == null) {
                value = System.getProperty(key.name());
            }
            if (value != null) {
                config.setProperty(key.getPropertyName(), value);
            }
        }
        String envName = config.getProperty("env");
        if (envName != null) {
            environment = Environment.valueOf(envName.toUpperCase());
        }
        checkNotNull(environment);
    }

    private void loadProperties(final String fileName, final Properties properties) {
        File file = new File(fileName);
        if (file.exists()) {
            try (InputStream in = new FileInputStream(file)) {
                properties.load(in);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Method to reset any of the default values that are defined in the bridge-sdk.properties configuration file.
     * 
     * @param property
     * @param value
     */
    public void set(Props property, String value) {
        checkNotNull(property, "Must specify a property");
        checkNotNull(value, "Must specify a value");
        config.setProperty(property.getPropertyName(), value);
    }

    /**
     * Method to set the environment of the SDK.
     * 
     * @param env
     */
    public void set(Environment env) {
        this.environment = env;
    }

    public SignInCredentials getAccountCredentials() {
        return new SignInCredentials(getStudyIdentifier(), getAccountEmail(), getAccountPassword());
    }

    public SignInCredentials getAdminCredentials() {
        return new SignInCredentials(getStudyIdentifier(), getAdminEmail(), getAdminPassword());
    }

    public String getAccountEmail() {
        return val(Props.ACCOUNT_EMAIL);
    }

    public String getAccountPassword() {
        return val(Props.ACCOUNT_PASSWORD);
    }

    public String getAdminEmail() {
        return val(Props.ADMIN_EMAIL);
    }

    public String getAdminPassword() {
        return val(Props.ADMIN_PASSWORD);
    }

    public String getCacheApi() {
        return val(Props.V3_CACHE);
    }

    public String getCacheApi(String key) {
        checkNotNull(key);
        return String.format(val(Props.V3_CACHE_CACHEKEY), key);
    }

    public String getDevName() {
        return val(Props.DEV_NAME);
    }

    public Environment getEnvironment() {
        return environment;
    }

    public String getLogLevel() {
        return val(Props.LOG_LEVEL);
    }

    public String getResendEmailVerificationApi() {
        return val(Props.V3_AUTH_RESENDEMAILVERIFICATION);
    }

    public String getSignUpApi() {
        return val(Props.V3_AUTH_SIGNUP);
    }

    public String getSignInApi() {
        return val(Props.V3_AUTH_SIGNIN);
    }

    public String getSignOutApi() {
        return val(Props.V3_AUTH_SIGNOUT);
    }

    public String getVerifyEmailApi() {
        return val(Props.V3_AUTH_VERIFYEMAIL);
    }

    public String getRequestResetPasswordApi() {
        return val(Props.V3_AUTH_REQUESTRESETPASSWORD);
    }

    public String getResetPasswordApi() {
        return val(Props.V3_AUTH_RESETPASSWORD);
    }

    public String getSetExternalIdApi() {
        return val(Props.V3_USERS_SELF_EXTERNALID);
    }

    public String getProfileApi() {
        return val(Props.V3_USERS_SELF);
    }

    public String getDataGroupsApi() {
        return val(Props.V3_USERS_SELF_DATAGROUPS);
    }
    
    public String getSetDataSharingApi() {
        return val(Props.V3_USERS_SELF_DATASHARING);
    }
    
    public String getConsentsApi(SubpopulationGuid subpopGuid) {
        checkNotNull(subpopGuid);
        return String.format(val(Props.V3_SUBPOPULATIONS_CONSENTS), subpopGuid.getGuid());
    }

    public String getConsentApi(SubpopulationGuid subpopGuid, DateTime timestamp) {
        checkNotNull(subpopGuid);
        checkNotNull(timestamp);
        return String.format(val(Props.V3_SUBPOPULATIONS_CONSENTS_TIMESTAMP), subpopGuid.getGuid(),
                timestamp.toString(ISODateTimeFormat.dateTime()));
    }

    public String getConsentSignatureApi(SubpopulationGuid subpopGuid) {
        checkNotNull(subpopGuid);
        return String.format(val(Props.V3_SUBPOPULATIONS_CONSENTS_SIGNATURE), subpopGuid.getGuid());
    }

    public String getEmailConsentSignatureApi(SubpopulationGuid subpopGuid) {
        checkNotNull(subpopGuid);
        return String.format(val(Props.V3_SUBPOPULATIONS_CONSENTS_SIGNATURE_EMAIL), subpopGuid.getGuid());
    }
    
    public String getWithdrawConsentSignatureApi(SubpopulationGuid subpopGuid) {
        checkNotNull(subpopGuid);
        return String.format(val(Props.V3_SUBPOPULATIONS_CONSENTS_SIGNATURE_WITHDRAW), subpopGuid.getGuid());
    }

    public String getStudyIdentifier() {
        return val(Props.STUDY_IDENTIFIER);
    }

    public String getPublishedStudyConsentApi(SubpopulationGuid subpopGuid) {
        checkNotNull(subpopGuid);
        return String.format(val(Props.V3_SUBPOPULATIONS_CONSENTS_PUBLISHED), subpopGuid.getGuid());
    }

    public String getMostRecentStudyConsentApi(SubpopulationGuid subpopGuid) {
        checkNotNull(subpopGuid);
        return String.format(val(Props.V3_SUBPOPULATIONS_CONSENTS_RECENT), subpopGuid.getGuid());
    }

    public String getPublishStudyConsentApi(SubpopulationGuid subpopGuid, DateTime timestamp) {
        checkNotNull(subpopGuid);
        checkNotNull(timestamp);
        return String.format(val(Props.V3_SUBPOPULATIONS_CONSENTS_TIMESTAMP_PUBLISH),
                subpopGuid.getGuid(), timestamp.toString(ISODateTimeFormat.dateTime()));
    }

    public String getUploadsApi() {
        return val(Props.V3_UPLOADS);
    }

    public String getCompleteUploadApi(String uploadId) {
        return String.format(val(Props.V3_UPLOADS_UPLOADID_COMPLETE), uploadId);
    }

    public String getUploadStatusApi(String uploadId) {
        return String.format(val(Props.V3_UPLOADSTATUSES_UPLOADID), uploadId);
    }

    public String getUsersApi() {
        return val(Props.V3_USERS);
    }

    public String getUsersSignOutApi() {
        return val(Props.V3_USERS_SIGNOUT);
    }

    public String getSchedulesApi() {
        return val(Props.V4_SCHEDULES);
    }

    public String getSurveysApi() {
        return val(Props.V3_SURVEYS);
    }

    public String getRecentSurveysApi() {
        return val(Props.V3_SURVEYS_RECENT);
    }

    public String getPublishedSurveysApi() {
        return val(Props.V3_SURVEYS_PUBLISHED);
    }

    public String getSurveyApi(String guid, DateTime createdOn) {
        checkNotNull(createdOn);
        return getSurveyApi(guid, createdOn.toString(ISODateTimeFormat.dateTime()));
    }

    public String getSurveyApi(String guid, String createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(val(Props.V3_SURVEYS_SURVEYGUID_REVISIONS_CREATEDON), guid, createdOn);
    }

    public String getDeleteSurveyPermanentlyApi(String guid, DateTime createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(val(Props.V3_SURVEYS_SURVEYGUID_REVISIONS_CREATEDON_PHYSICAL_TRUE), guid,
                createdOn.toString(ISODateTimeFormat.dateTime()));
    }

    public String getMostRecentlyPublishedSurveyRevisionApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.V3_SURVEYS_SURVEYGUID_REVISIONS_PUBLISHED), guid);
    }

    public String getMostRecentSurveyRevisionApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.V3_SURVEYS_SURVEYGUID_REVISIONS_RECENT), guid);
    }

    public String getSurveyAllRevisionsApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.V3_SURVEYS_SURVEYGUID_REVISIONS), guid);
    }

    public String getVersionSurveyApi(String guid, DateTime createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(val(Props.V3_SURVEYS_SURVEYGUID_REVISIONS_CREATEDON_VERSION), guid,
                createdOn.toString(ISODateTimeFormat.dateTime()));
    }

    public String getPublishSurveyApi(String guid, DateTime createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(val(Props.V3_SURVEYS_SURVEYGUID_REVISIONS_CREATEDON_PUBLISH), guid,
                createdOn.toString(ISODateTimeFormat.dateTime()));
    }

    public String getRecentlyPublishedSurveyForUserApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.V3_SURVEYS_SURVEYGUID_REVISIONS_PUBLISHED), guid);
    }

    public String getSurveyResponsesApi() {
        return val(Props.V3_SURVEYRESPONSES);
    }

    public String getSurveyResponseApi(String identifier) {
        checkArgument(isNotBlank(identifier));
        return String.format(val(Props.V3_SURVEYRESPONSES_IDENTIFIER), identifier);
    }

    public String getSchedulePlansApi() {
        return val(Props.V3_SCHEDULEPLANS);
    }

    public String getSchedulePlanApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.V3_SCHEDULEPLANS_GUID), guid);
    }

    public String getScheduledActivitiesApi() {
        return val(Props.V3_ACTIVITIES);
    }

    public String getUploadSchemasApi() {
        return val(Props.V3_UPLOADSCHEMAS);
    }

    public String getUploadSchemaAllRevisionsApi(String schemaId) {
        checkArgument(isNotBlank(schemaId));
        return String.format(val(Props.V3_UPLOADSCHEMAS_SCHEMAID), schemaId);
    }

    public String getMostRecentUploadSchemaApi(String schemaId) {
        checkArgument(isNotBlank(schemaId));
        return String.format(val(Props.V3_UPLOADSCHEMAS_SCHEMAID_RECENT), schemaId);
    }

    public String getUploadSchemaApi(String schemaId, int revision) {
        checkArgument(isNotBlank(schemaId));
        checkArgument(revision != 0);
        return String.format(val(Props.V3_UPLOADSCHEMAS_SCHEMAID_REVISIONS_REV), schemaId, revision);
    }

    public String getCurrentStudyApi() {
        return val(Props.V3_STUDIES_SELF);
    }

    public String getStudiesApi() {
        return val(Props.V3_STUDIES);
    }

    public String getStudyApi(String identifier) {
        checkArgument(isNotBlank(identifier));
        return String.format(val(Props.V3_STUDIES_IDENTIFIER), identifier);
    }

    public String getSdkVersion() {
        return val(Props.SDK_VERSION);
    }

    public String getEmailParticipantRosterApi() {
        return val(Props.V3_USERS_EMAILPARTICIPANTROSTER);
    }
    
    public String getSubpopulations() {
        return val(Props.V3_SUBPOPULATIONS);
    }
    
    public String getSubpopulation(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.V3_SUBPOPULATION), guid);
    }

    public String getParticipantsApi(int offsetBy, int pageSize, String emailFilter) {
        checkArgument(offsetBy >= 0);
        checkArgument(pageSize >= 5);

        return String.format(val(Props.V3_PARTICIPANTS), offsetBy, pageSize, MoreObjects.firstNonNull(emailFilter, ""));
    }
    
    public String getParticipant(String email) {
        checkArgument(isNotBlank(email));
        try {
            String encodedEmail = URLEncoder.encode(email, "UTF-8");
            return String.format(val(Props.V3_PARTICIPANT), encodedEmail);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    private String val(Props prop) {
        String value = config.getProperty(prop.getPropertyName());
        checkNotNull(value, "The property '" + prop.getPropertyName() + "' has not been set.");
        return value;
    }

    @Override
    public String toString() {
        List<String> list = Lists.newArrayList();
        for (int i = 0; i < Props.values().length; i++) {
            String property = Props.values()[i].getPropertyName();
            String value = property.contains("password") ? "******" : config.getProperty(property);
            list.add(property + "=" + value);
        }
        return "Config[" + Joiner.on(", ").join(list) + "]";
    }
}
