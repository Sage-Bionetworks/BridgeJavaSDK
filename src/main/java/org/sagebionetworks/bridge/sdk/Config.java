package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.sdk.models.users.SignInCredentials;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public final class Config {

    private static final String CONFIG_FILE = "/bridge-sdk.properties";
    private static final String USER_CONFIG_FILE = System.getProperty("user.home") + "/bridge-sdk.properties";

    public static enum Props {
        ACCOUNT_EMAIL,
        ACCOUNT_PASSWORD,
        ADMIN_EMAIL,
        ADMIN_PASSWORD,
        AUTH_REQUEST_RESET_PASSWORD_API,
        AUTH_RESEND_EMAIL_VERIFICATION_API,
        AUTH_RESET_PASSWORD_API,
        AUTH_SIGNIN_API,
        AUTH_SIGNOUT_API,
        AUTH_SIGNUP_API,
        AUTH_VERIFY_EMAIL_API,
        BACKFILL_API,
        BACKFILL_START_API,
        CACHE_API,
        CACHE_KEY_API,
        CONSENTS_API,
        CONSENT_API,
        CONSENT_PUBLISH_API,
        CONSENT_PUBLISHED_API,
        CONSENT_RECENT_API,
        CONSENT_SIGNATURE_API,
        CONSENT_SIGNATURE_EMAIL_API,
        DEV_NAME,
        ENV,
        LOG_LEVEL,
        SCHEDULEPLANS_API,
        SCHEDULEPLAN_API,
        SCHEDULES_API,
        SDK_VERSION,
        STUDIES_API,
        STUDIES_SUMMARY_API,
        STUDY_API,
        STUDY_IDENTIFIER,
        STUDY_SELF_API,
        SURVEYRESPONSES_API,
        SURVEYRESPONSE_API,
        SURVEYS_API,
        SURVEYS_PUBLISHED_API,
        SURVEYS_RECENT_API,
        SURVEY_API,
        SURVEY_PUBLISHED_REVISION_API,
        SURVEY_PUBLISH_API,
        SURVEY_RECENT_REVISION_API,
        SURVEY_REVISIONS_API,
        SURVEY_VERSION_API,
        TASKS_API,
        UPLOADSCHEMAS_API,
        UPLOADSCHEMA_API,
        UPLOADSCHEMA_REVISION_API,
        UPLOADSTATUS_API,
        UPLOADS_API,
        UPLOAD_COMPLETE_API,
        USERS_API,
        USERS_EMAIL_API,
        USER_SELF_API,
        USER_SELF_DATASHARING_API,
        USER_SELF_EXTERNALID_API,
        USER_SELF_UNSUBSCRIBE_API;
        
        public String getPropertyName() {
            return this.name().replace("_", ".").toLowerCase();
        }
    }

    private Properties config;
    private Environment environment;

    Config() {
        config = new Properties();
        
        // Load from default configuration file
        try(InputStream in = this.getClass().getResourceAsStream(CONFIG_FILE)) {
            config.load(in);
        } catch(IOException e) {
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
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Accessor for the public API that allows consumer to change any value that
    // is in the configuration files, programmatically, if that's something they
    // want to do.
    public void set(Props property, String value) {
        checkNotNull(property, "Must specify a property");
        checkNotNull(value, "Must specify a value");
        config.setProperty(property.getPropertyName(), value);
    }
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
        return val(Props.CACHE_API);
    }
    public String getCacheKeyApi(String key) {
        checkNotNull(key);
        return String.format(val(Props.CACHE_KEY_API), key);
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
    public String getAuthResendEmailVerificationApi() {
        return val(Props.AUTH_RESEND_EMAIL_VERIFICATION_API);
    }
    public String getAuthSignUpApi() {
        return val(Props.AUTH_SIGNUP_API);
    }
    public String getAuthSignInApi() {
        return val(Props.AUTH_SIGNIN_API);
    }
    public String getAuthSignOutApi() {
        return val(Props.AUTH_SIGNOUT_API);
    }
    public String getAuthVerifyEmailApi() {
        return val(Props.AUTH_VERIFY_EMAIL_API);
    }
    public String getAuthRequestResetApi() {
        return val(Props.AUTH_REQUEST_RESET_PASSWORD_API);
    }
    public String getAuthResetApi() {
        return val(Props.AUTH_RESET_PASSWORD_API);
    }
    public String getExternalIdentifierApi() {
        return val(Props.USER_SELF_EXTERNALID_API);
    }
    public String getProfileApi() {
        return val(Props.USER_SELF_API);
    }
    public String getConsentApi() {
        return val(Props.CONSENT_API);
    }
    public String getConsentChangeApi() {
        return val(Props.USER_SELF_DATASHARING_API);
    }
    public String getConsentsApi() {
        return val(Props.CONSENTS_API);
    }
    public String getConsentApi(DateTime timestamp) {
        checkNotNull(timestamp);
        return String.format(val(Props.CONSENT_API), timestamp.toString(ISODateTimeFormat.dateTime()));
    }
    public String getConsentSignatureApi() {
        return val(Props.CONSENT_SIGNATURE_API);
    }
    public String getEmailConsentSignatureApi() {
        return val(Props.CONSENT_SIGNATURE_EMAIL_API);
    }
    public String getStudyIdentifier() {
        return val(Props.STUDY_IDENTIFIER);
    }
    public String getPublishedStudyConsentApi() {
        return val(Props.CONSENT_PUBLISHED_API);
    }
    public String getMostRecentStudyConsentApi() {
        return val(Props.CONSENT_RECENT_API);
    }
    public String getPublishStudyConsentApi(DateTime timestamp) {
        checkNotNull(timestamp);
        return String.format(val(Props.CONSENT_PUBLISH_API), timestamp.toString(ISODateTimeFormat.dateTime()));
    }
    public String getUploadApi() {
        return val(Props.UPLOADS_API);
    }
    public String getUploadCompleteApi(String uploadId) {
        return String.format(val(Props.UPLOAD_COMPLETE_API), uploadId);
    }
    public String getUploadStatusApi(String uploadId) {
        return String.format(val(Props.UPLOADSTATUS_API), uploadId);
    }
    public String getUsersApi() {
        return val(Props.USERS_API);
    }
    public String getSchedulesApi() {
        return val(Props.SCHEDULES_API);
    }
    public String getSurveysApi() {
        return val(Props.SURVEYS_API);
    }
    public String getSurveysRecentApi() {
        return val(Props.SURVEYS_RECENT_API);
    }
    public String getSurveysPublishedApi() {
        return val(Props.SURVEYS_PUBLISHED_API);
    }
    public String getSurveyApi(String guid, DateTime createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(val(Props.SURVEY_API), guid, createdOn.toString(ISODateTimeFormat.dateTime()));
    }
    public String getSurveyApi(String guid, String createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(val(Props.SURVEY_API), guid, createdOn);
    }
    public String getSurveyMostRecentlyPublishedRevisionApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.SURVEY_PUBLISHED_REVISION_API), guid);
    }
    public String getSurveyMostRecentRevisionApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.SURVEY_RECENT_REVISION_API), guid);
    }
    public String getSurveyRevisionsApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.SURVEY_REVISIONS_API), guid);
    }
    public String getSurveyNewRevisionApi(String guid, DateTime createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(val(Props.SURVEY_VERSION_API), guid, createdOn.toString(ISODateTimeFormat.dateTime()));
    }
    public String getPublishSurveyApi(String guid, DateTime createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(val(Props.SURVEY_PUBLISH_API), guid, createdOn.toString(ISODateTimeFormat.dateTime()));
    }
    public String getRecentlyPublishedSurveyUserApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.SURVEY_PUBLISHED_REVISION_API), guid);
    }
    public String getSurveyResponseApi() {
        return val(Props.SURVEYRESPONSES_API);
    }
    public String getSurveyResponseApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.SURVEYRESPONSE_API), guid);
    }
    public String getSchedulePlansApi() {
        return val(Props.SCHEDULEPLANS_API);
    }
    public String getSchedulePlanApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.SCHEDULEPLAN_API), guid);
    }
    public String getTasksApi() {
        return val(Props.TASKS_API);
    }
    public String getUploadSchemasApi() {
        return val(Props.UPLOADSCHEMAS_API);
    }
    public String getUploadSchemaByIdApi(String schemaId) {
        return String.format(val(Props.UPLOADSCHEMA_API), schemaId);
    }
    public String getUploadSchemaByIdAndRevisionApi(String schemaId, int revision) {
        return String.format(val(Props.UPLOADSCHEMA_REVISION_API), schemaId, revision);
    }
    public String getStudySelfApi() {
        return val(Props.STUDY_SELF_API);
    }
    public String getEmailParticipantRosterApi() {
        return val(Props.USERS_EMAIL_API);
    }
    public String getStudiesApi() {
        return val(Props.STUDIES_API);
    }
    public String getStudyApi(String identifier) {
        checkArgument(isNotBlank(identifier));
        return String.format(val(Props.STUDY_API), identifier);
    }
    public String getSdkVersion() {
        return val(Props.SDK_VERSION);
    }

    private String val(Props prop) {
        String value = config.getProperty(prop.getPropertyName());
        checkNotNull(value, "The property '" + prop.getPropertyName() + "' has not been set.");
        return value;
    }

    @Override
    public String toString() {
        List<String> list = Lists.newArrayList();
        for (int i=0; i < Props.values().length; i++) {
            String property = Props.values()[i].getPropertyName();
            String value = property.contains("password") ? "******" : config.getProperty(property);
            list.add(property + "=" + value);
        }
        return "Config[" + Joiner.on(", ").join(list) + "]";
    }
}
