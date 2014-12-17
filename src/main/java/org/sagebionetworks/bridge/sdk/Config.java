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
        ADMIN_STUDIES_API,
        ADMIN_STUDY_API,
        HOST,
        LOG_LEVEL,
        AUTH_SIGNUP_API,
        AUTH_SIGNIN_API,
        AUTH_SIGNOUT_API,
        AUTH_VERIFYEMAIL_API,
        AUTH_REQUESTRESET_API,
        AUTH_RESET_API,
        CONSENT_API,
        CONSENT_SUSPEND_API,
        CONSENT_RESUME_API,
        DEV_NAME,
        HEALTH_DATA_TRACKER_API,
        HEALTH_DATA_TRACKER_RECORD_API,
        PROFILE_API,
        RESEARCHER_STUDY_API,
        SCHEDULES_API,
        SCHEDULEPLAN_API,
        SCHEDULEPLANS_API,
        SDK_VERSION,
        STUDY_CONSENTS_API,
        STUDY_CONSENT_API,
        STUDY_CONSENT_GET_ACTIVE_API,
        STUDY_CONSENT_SET_ACTIVE_API,
        SURVEY_API,
        SURVEY_RECENT_API,
        SURVEY_RECENTLY_PUBLISHED_API,
        SURVEY_RECENTLY_PUBLISHED_USER_API,
        SURVEY_USER_API,
        SURVEY_WITH_IDENTIFIER_USER_API,
        SURVEY_PUBLISH_API,
        SURVEY_CLOSE_API,
        SURVEY_VERSIONS_API,
        SURVEY_VERSIONS_NEW_API,
        SURVEYS_API,
        SURVEYS_RECENT_API,
        SURVEYS_PUBLISHED_API,
        SURVEY_RESPONSE_API,
        TRACKER_API,
        UPLOAD_API,
        UPLOAD_COMPLETE_API,
        USER_MANAGEMENT_API,
        USER_MANAGEMENT_CONSENT_API,
        USER_MANAGEMENT_ALLTESTUSERS_API;

        public String getPropertyName() {
            return this.name().replace("_", ".").toLowerCase();
        }
    }

    private Properties config;

    private Config() {
        config = new Properties();
        
        try(InputStream in = this.getClass().getResourceAsStream(CONFIG_FILE)) {
            config.load(in);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        // loadProperties(CONFIG_FILE, config);
        loadProperties(USER_CONFIG_FILE, config);

        for (Props key : Props.values()) {
            String value = System.getenv(key.name());
            if (value == null) {
                value = System.getProperty(key.name());
            }
            if (value != null) {
                config.setProperty(key.getPropertyName(), value);
            }
        }
    }

    static Config valueOf() {
        return new Config();
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
    public SignInCredentials getAccountCredentials() {
        return new SignInCredentials(getAccountEmail(), getAccountPassword());
    }
    public SignInCredentials getAdminCredentials() {
        return new SignInCredentials(getAdminEmail(), getAdminPassword());
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
    public String getDevName() {
        return val(Props.DEV_NAME);
    }
    public String getHost() {
        return val(Props.HOST);
    }
    public String getLogLevel() {
        return val(Props.LOG_LEVEL);
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
        return val(Props.AUTH_VERIFYEMAIL_API);
    }
    public String getAuthRequestResetApi() {
        return val(Props.AUTH_REQUESTRESET_API);
    }
    public String getAuthResetApi() {
        return val(Props.AUTH_RESET_API);
    }
    public String getProfileApi() {
        return val(Props.PROFILE_API);
    }
    public String getConsentApi() {
        return val(Props.CONSENT_API);
    }
    public String getConsentSuspendApi() {
        return val(Props.CONSENT_SUSPEND_API);
    }
    public String getConsentResumeApi() {
        return val(Props.CONSENT_RESUME_API);
    }
    public String getStudyConsentsApi() {
        return val(Props.STUDY_CONSENTS_API);
    }
    public String getStudyConsentApi(DateTime timestamp) {
        checkNotNull(timestamp);
        return String.format(val(Props.STUDY_CONSENT_API), timestamp.toString(ISODateTimeFormat.dateTime()));
    }
    public String getActiveStudyConsentApi() {
        return val(Props.STUDY_CONSENT_GET_ACTIVE_API);
    }
    public String getVersionStudyConsentApi(DateTime timestamp) {
        checkNotNull(timestamp);
        return String.format(val(Props.STUDY_CONSENT_SET_ACTIVE_API), timestamp.toString(ISODateTimeFormat.dateTime()));
    }
    public String getTrackerApi() {
        return val(Props.TRACKER_API);
    }
    public String getHealthDataTrackerApi(String trackerId) {
        checkArgument(isNotBlank(trackerId));
        return String.format(val(Props.HEALTH_DATA_TRACKER_API), trackerId);
    }
    public String getHealthDataRecordApi(String trackerId, String guid) {
        checkArgument(isNotBlank(trackerId));
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.HEALTH_DATA_TRACKER_RECORD_API), trackerId, guid);
    }
    public String getUploadApi() {
        return val(Props.UPLOAD_API);
    }
    public String getUploadCompleteApi(String uploadId) {
        return String.format(val(Props.UPLOAD_COMPLETE_API), uploadId);
    }
    public String getUserManagementApi() {
        return val(Props.USER_MANAGEMENT_API);
    }
    public String getUserManagementAllTestUsersApi() {
        return val(Props.USER_MANAGEMENT_ALLTESTUSERS_API);
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
    public String getSurveyMostRecentlyPublishedVersionApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.SURVEY_RECENTLY_PUBLISHED_API), guid);
    }
    public String getSurveyMostRecentVersionApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.SURVEY_RECENT_API), guid);
    }
    public String getSurveyVersionsApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.SURVEY_VERSIONS_API), guid);
    }
    public String getSurveyNewVersionApi(String guid, DateTime createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(val(Props.SURVEY_VERSIONS_NEW_API), guid, createdOn.toString(ISODateTimeFormat.dateTime()));
    }
    public String getPublishSurveyApi(String guid, DateTime createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(val(Props.SURVEY_PUBLISH_API), guid, createdOn.toString(ISODateTimeFormat.dateTime()));
    }
    public String getCloseSurveyApi(String guid, DateTime createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(val(Props.SURVEY_CLOSE_API), guid, createdOn.toString(ISODateTimeFormat.dateTime()));
    }
    public String getSurveyUserApi(String guid, DateTime createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(val(Props.SURVEY_USER_API), guid, createdOn.toString(ISODateTimeFormat.dateTime()));
    }
    public String getRecentlyPublishedSurveyUserApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.SURVEY_RECENTLY_PUBLISHED_USER_API), guid);
    }
    public String getSurveyWithIdentifierUserApi(String guid, DateTime createdOn, String identifier) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        checkArgument(isNotBlank(identifier));
        return String.format(val(Props.SURVEY_WITH_IDENTIFIER_USER_API), guid, createdOn.toString(ISODateTimeFormat.dateTime()), identifier);
    }
    public String getSurveyResponseApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.SURVEY_RESPONSE_API), guid);
    }
    public String getSchedulePlansApi() {
        return val(Props.SCHEDULEPLANS_API);
    }
    public String getSchedulePlanApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.SCHEDULEPLAN_API), guid);
    }
    public String getResearcherStudyApi() {
        return val(Props.RESEARCHER_STUDY_API);
    }
    public String getAdminStudiesApi() {
        return val(Props.ADMIN_STUDIES_API);
    }
    public String getAdminStudyApi(String identifier) {
        checkArgument(isNotBlank(identifier));
        return String.format(val(Props.ADMIN_STUDY_API), identifier);
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
