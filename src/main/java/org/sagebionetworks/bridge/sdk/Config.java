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
        DEV_NAME,
        ENV,
        LOG_LEVEL,
        SDK_VERSION,
        STUDY_IDENTIFIER,
        V3_AUTH_SIGNIN,
        V3_AUTH_SIGNOUT,
        V3_AUTH_REQUESTRESETPASSWORD,
        V3_AUTH_RESETPASSWORD,
        V3_AUTH_SIGNUP,
        V3_AUTH_VERIFYEMAIL,
        V3_AUTH_RESENDEMAILVERIFICATION,
        V3_CONSENTS_SIGNATURE,
        V3_CONSENTS_SIGNATURE_EMAIL,
        V3_CONSENTS,
        V3_CONSENTS_RECENT,
        V3_CONSENTS_PUBLISHED,
        V3_CONSENTS_TIMESTAMP,
        V3_CONSENTS_TIMESTAMP_PUBLISH,
        V3_USERS,
        V3_USERS_EMAILPARTICIPANTROSTER,
        V3_USERS_SELF,
        V3_USERS_SELF_EXTERNALID,
        V3_USERS_SELF_EMAILDATA,
        V3_USERS_SELF_UNSUBSCRIBEEMAIL,
        V3_USERS_SELF_DATASHARING,
        V3_SURVEYS,
        V3_SURVEYS_RECENT,
        V3_SURVEYS_PUBLISHED,
        V3_SURVEYS_SURVEYGUID_REVISIONS,
        V3_SURVEYS_SURVEYGUID_REVISIONS_RECENT,
        V3_SURVEYS_SURVEYGUID_REVISIONS_PUBLISHED,
        V3_SURVEYS_SURVEYGUID_REVISIONS_CREATEDON_VERSION,
        V3_SURVEYS_SURVEYGUID_REVISIONS_CREATEDON_PUBLISH,
        V3_SURVEYS_SURVEYGUID_REVISIONS_CREATEDON,
        V3_SURVEYS_SURVEYGUID_REVISIONS_CREATEDON_PHYSICAL_TRUE,
        V3_SURVEYRESPONSES,
        V3_SURVEYRESPONSES_IDENTIFIER,
        V3_SCHEDULES,
        V3_TASKS,
        V3_UPLOADS,
        V3_UPLOADS_UPLOADID_COMPLETE,
        V3_UPLOADSTATUSES_UPLOADID,
        V3_UPLOADSCHEMAS,
        V3_UPLOADSCHEMAS_SCHEMAID,
        V3_UPLOADSCHEMAS_SCHEMAID_RECENT,
        V3_UPLOADSCHEMAS_SCHEMAID_REVISIONS_REV,
        V3_SCHEDULEPLANS,
        V3_SCHEDULEPLANS_GUID,
        V3_STUDIES,
        V3_STUDIES_SELF,
        V3_STUDIES_IDENTIFIER,
        V3_BACKFILL_NAME,
        V3_BACKFILL_NAME_START,
        V3_CACHE,
        V3_CACHE_CACHEKEY;
        
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
        return val(Props.V3_CACHE);
    }
    public String getCacheKeyApi(String key) {
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
    public String getAuthResendEmailVerificationApi() {
        return val(Props.V3_AUTH_RESENDEMAILVERIFICATION);
    }
    public String getAuthSignUpApi() {
        return val(Props.V3_AUTH_SIGNUP);
    }
    public String getAuthSignInApi() {
        return val(Props.V3_AUTH_SIGNIN);
    }
    public String getAuthSignOutApi() {
        return val(Props.V3_AUTH_SIGNOUT);
    }
    public String getAuthVerifyEmailApi() {
        return val(Props.V3_AUTH_VERIFYEMAIL);
    }
    public String getAuthRequestResetApi() {
        return val(Props.V3_AUTH_REQUESTRESETPASSWORD);
    }
    public String getAuthResetApi() {
        return val(Props.V3_AUTH_RESETPASSWORD);
    }
    public String getExternalIdentifierApi() {
        return val(Props.V3_USERS_SELF_EXTERNALID);
    }
    public String getProfileApi() {
        return val(Props.V3_USERS_SELF);
    }
    public String getConsentApi() {
        return val(Props.V3_CONSENTS_TIMESTAMP);
    }
    public String getConsentChangeApi() {
        return val(Props.V3_USERS_SELF_DATASHARING);
    }
    public String getConsentsApi() {
        return val(Props.V3_CONSENTS);
    }
    public String getConsentApi(DateTime timestamp) {
        checkNotNull(timestamp);
        return String.format(val(Props.V3_CONSENTS_TIMESTAMP), timestamp.toString(ISODateTimeFormat.dateTime()));
    }
    public String getConsentSignatureApi() {
        return val(Props.V3_CONSENTS_SIGNATURE);
    }
    public String getEmailConsentSignatureApi() {
        return val(Props.V3_CONSENTS_SIGNATURE_EMAIL);
    }
    public String getStudyIdentifier() {
        return val(Props.STUDY_IDENTIFIER);
    }
    public String getPublishedStudyConsentApi() {
        return val(Props.V3_CONSENTS_PUBLISHED);
    }
    public String getMostRecentStudyConsentApi() {
        return val(Props.V3_CONSENTS_RECENT);
    }
    public String getPublishStudyConsentApi(DateTime timestamp) {
        checkNotNull(timestamp);
        return String.format(val(Props.V3_CONSENTS_TIMESTAMP_PUBLISH), timestamp.toString(ISODateTimeFormat.dateTime()));
    }
    public String getUploadApi() {
        return val(Props.V3_UPLOADS);
    }
    public String getUploadCompleteApi(String uploadId) {
        return String.format(val(Props.V3_UPLOADS_UPLOADID_COMPLETE), uploadId);
    }
    public String getUploadStatusApi(String uploadId) {
        return String.format(val(Props.V3_UPLOADSTATUSES_UPLOADID), uploadId);
    }
    public String getUsersApi() {
        return val(Props.V3_USERS);
    }
    public String getSchedulesApi() {
        return val(Props.V3_SCHEDULES);
    }
    public String getSurveysApi() {
        return val(Props.V3_SURVEYS);
    }
    public String getSurveysRecentApi() {
        return val(Props.V3_SURVEYS_RECENT);
    }
    public String getSurveysPublishedApi() {
        return val(Props.V3_SURVEYS_PUBLISHED);
    }
    public String getSurveyApi(String guid, DateTime createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(val(Props.V3_SURVEYS), guid, createdOn.toString(ISODateTimeFormat.dateTime()));
    }
    public String getDeleteSurveyPermanentlyApi(String guid, DateTime createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(val(Props.V3_SURVEYS_SURVEYGUID_REVISIONS_CREATEDON_PHYSICAL_TRUE), guid, createdOn.toString(ISODateTimeFormat.dateTime()));
    }
    public String getSurveyApi(String guid, String createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(val(Props.V3_SURVEYS_SURVEYGUID_REVISIONS_CREATEDON), guid, createdOn);
    }
    public String getSurveyMostRecentlyPublishedRevisionApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.V3_SURVEYS_SURVEYGUID_REVISIONS_PUBLISHED), guid);
    }
    public String getSurveyMostRecentRevisionApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.V3_SURVEYS_SURVEYGUID_REVISIONS_RECENT), guid);
    }
    public String getSurveyRevisionsApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.V3_SURVEYS_SURVEYGUID_REVISIONS), guid);
    }
    public String getSurveyNewRevisionApi(String guid, DateTime createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(val(Props.V3_SURVEYS_SURVEYGUID_REVISIONS_CREATEDON_VERSION), guid, createdOn.toString(ISODateTimeFormat.dateTime()));
    }
    public String getPublishSurveyApi(String guid, DateTime createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(val(Props.V3_SURVEYS_SURVEYGUID_REVISIONS_CREATEDON_PUBLISH), guid, createdOn.toString(ISODateTimeFormat.dateTime()));
    }
    public String getRecentlyPublishedSurveyUserApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.V3_SURVEYS_SURVEYGUID_REVISIONS_PUBLISHED), guid);
    }
    public String getSurveyResponseApi() {
        return val(Props.V3_SURVEYRESPONSES);
    }
    public String getSurveyResponseApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.V3_SURVEYRESPONSES), guid);
    }
    public String getSchedulePlansApi() {
        return val(Props.V3_SCHEDULEPLANS);
    }
    public String getSchedulePlanApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.V3_SCHEDULEPLANS_GUID), guid);
    }
    public String getTasksApi() {
        return val(Props.V3_TASKS);
    }
    public String getUploadSchemasApi() {
        return val(Props.V3_UPLOADSCHEMAS);
    }
    public String getUploadSchemaByIdApi(String schemaId) {
        return String.format(val(Props.V3_UPLOADSCHEMAS_SCHEMAID), schemaId);
    }
    public String getUploadSchemaByIdAndRevisionApi(String schemaId, int revision) {
        return String.format(val(Props.V3_UPLOADSCHEMAS_SCHEMAID_REVISIONS_REV), schemaId, revision);
    }
    public String getStudySelfApi() {
        return val(Props.V3_STUDIES_SELF);
    }
    public String getEmailParticipantRosterApi() {
        return val(Props.V3_USERS_EMAILPARTICIPANTROSTER);
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
