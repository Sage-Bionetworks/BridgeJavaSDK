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

    private static final String CONFIG_FILE = "src/main/resources/bridge-sdk.properties";
    private static final String USER_CONFIG_FILE = System.getProperty("user.home") + "/bridge-sdk.properties";

    public static enum Props {
        ACCOUNT_EMAIL,
        ACCOUNT_PASSWORD,
        ADMIN_EMAIL,
        ADMIN_PASSWORD,
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
        SCHEDULES_API,
        SCHEDULEPLAN_API,
        SCHEDULEPLANS_API,
        STUDY_CONSENTS_API,
        STUDY_CONSENT_API,
        STUDY_CONSENT_GET_ACTIVE_API,
        STUDY_CONSENT_SET_ACTIVE_API,
        SURVEY_API,
        SURVEY_USER_API,
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
        USER_MANAGEMENT_CONSENT_API;

        public String getPropertyName() {
            return this.name().replace("_", ".").toLowerCase();
        }
    }

    private Properties config;

    private Config() {
        config = new Properties();
        loadProperties(CONFIG_FILE, config);
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
    String getAccountEmail() {
        return val(Props.ACCOUNT_EMAIL);
    }
    String getAccountPassword() {
        return val(Props.ACCOUNT_PASSWORD);
    }
    String getAdminEmail() {
        return val(Props.ADMIN_EMAIL);
    }
    String getAdminPassword() {
        return val(Props.ADMIN_PASSWORD);
    }
    String getDevName() {
        return val(Props.DEV_NAME);
    }
    String getHost() {
        return val(Props.HOST);
    }
    String getLogLevel() {
        return val(Props.LOG_LEVEL);
    }
    String getAuthSignUpApi() {
        return val(Props.AUTH_SIGNUP_API);
    }
    String getAuthSignInApi() {
        return val(Props.AUTH_SIGNIN_API);
    }
    String getAuthSignOutApi() {
        return val(Props.AUTH_SIGNOUT_API);
    }
    String getAuthVerifyEmailApi() {
        return val(Props.AUTH_VERIFYEMAIL_API);
    }
    String getAuthRequestResetApi() {
        return val(Props.AUTH_REQUESTRESET_API);
    }
    String getAuthResetApi() {
        return val(Props.AUTH_RESET_API);
    }
    String getProfileApi() {
        return val(Props.PROFILE_API);
    }
    String getConsentApi() {
        return val(Props.CONSENT_API);
    }
    String getConsentSuspendApi() {
        return val(Props.CONSENT_SUSPEND_API);
    }
    String getConsentResumeApi() {
        return val(Props.CONSENT_RESUME_API);
    }
    String getStudyConsentsApi() {
        return val(Props.STUDY_CONSENTS_API);
    }
    String getStudyConsentApi(DateTime timestamp) {
        checkNotNull(timestamp);
        return String.format(val(Props.STUDY_CONSENT_API), timestamp.toString(ISODateTimeFormat.dateTime()));
    }
    String getActiveStudyConsentApi() {
        return val(Props.STUDY_CONSENT_GET_ACTIVE_API);
    }
    String getVersionStudyConsentApi(DateTime timestamp) {
        checkNotNull(timestamp);
        return String.format(val(Props.STUDY_CONSENT_SET_ACTIVE_API), timestamp.toString(ISODateTimeFormat.dateTime()));
    }
    String getTrackerApi() {
        return val(Props.TRACKER_API);
    }
    String getHealthDataTrackerApi(String trackerId) {
        checkArgument(isNotBlank(trackerId));
        return String.format(val(Props.HEALTH_DATA_TRACKER_API), trackerId);
    }
    String getHealthDataTrackerRecordApi(String trackerId, String guid) {
        checkArgument(isNotBlank(trackerId));
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.HEALTH_DATA_TRACKER_RECORD_API), trackerId, guid);
    }
    String getUploadApi() {
        return val(Props.UPLOAD_API);
    }
    String getUploadCompleteApi(String uploadId) {
        return String.format(val(Props.UPLOAD_COMPLETE_API), uploadId);
    }
    String getUserManagementApi() {
        return val(Props.USER_MANAGEMENT_API);
    }
    String getSchedulesApi() {
        return val(Props.SCHEDULES_API);
    }
    String getSurveysApi() {
        return val(Props.SURVEYS_API);
    }
    String getRecentSurveysApi() {
        return val(Props.SURVEYS_RECENT_API);
    }
    String getSurveysPublishedApi() {
        return val(Props.SURVEYS_PUBLISHED_API);
    }
    String getSurveyApi(String guid, DateTime createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(val(Props.SURVEY_API), guid, createdOn.toString(ISODateTimeFormat.dateTime()));
    }
    String getSurveyVersionsApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.SURVEY_VERSIONS_API), guid);
    }
    String getSurveyNewVersionApi(String guid, DateTime createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(val(Props.SURVEY_VERSIONS_NEW_API), guid, createdOn.toString(ISODateTimeFormat.dateTime()));
    }
    String getPublishSurveyApi(String guid, DateTime createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(val(Props.SURVEY_PUBLISH_API), guid, createdOn.toString(ISODateTimeFormat.dateTime()));
    }
    String getCloseSurveyApi(String guid, DateTime createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(val(Props.SURVEY_CLOSE_API), guid, createdOn.toString(ISODateTimeFormat.dateTime()));
    }
    String getSurveyUserApi(String guid, DateTime createdOn) {
        checkArgument(isNotBlank(guid));
        checkNotNull(createdOn);
        return String.format(val(Props.SURVEY_USER_API), guid, createdOn.toString(ISODateTimeFormat.dateTime()));
    }
    String getSurveyResponseApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.SURVEY_RESPONSE_API), guid);
    }
    String getSchedulePlansApi() {
        return val(Props.SCHEDULEPLANS_API);
    }
    String getSchedulePlanApi(String guid) {
        checkArgument(isNotBlank(guid));
        return String.format(val(Props.SCHEDULEPLAN_API), guid);
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
