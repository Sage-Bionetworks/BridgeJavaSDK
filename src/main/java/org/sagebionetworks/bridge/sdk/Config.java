package org.sagebionetworks.bridge.sdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

final class Config {

    private static final String CONFIG_FILE = "bridge-sdk.properties";
    private static final String USER_CONFIG_FILE = System.getProperty("user.home") + "/bridge-sdk.properties";

    public static enum Props {
        ACCOUNT_EMAIL,
        ACCOUNT_PASSWORD,
        ADMIN_EMAIL,
        ADMIN_PASSWORD,
        HOST,
        AUTH_API,
        CONSENT_API,
        HEALTH_DATA_API,
        PROFILE_API,
        SCHEDULES_API,
        SCHEDULEPLAN_API,
        SCHEDULEPLANS_API,
        STUDY_CONSENT_API,
        SURVEY_API,
        SURVEY_USER_API,
        SURVEY_PUBLISH_API,
        SURVEY_VERSIONS_API,
        SURVEYS_API,
        SURVEY_RESPONSE_API,
        TRACKER_API,
        UPLOAD_API,
        USER_MANAGEMENT_API;

        public String getPropertyName() {
            return this.name().replace("_", ".").toLowerCase();
        }
    }

    private Properties config;

    private Config() {
        try {
            config = new Properties();
            loadProperties(new FileInputStream(new File(CONFIG_FILE)), config);

            File userConfig = new File(USER_CONFIG_FILE);
            if (userConfig.exists()) {
                loadProperties(new FileInputStream(userConfig), config);
            }

            for (Props key : Props.values()) {
                String value = System.getenv(key.name());
                if (value == null) {
                    value = System.getProperty(key.name());
                }
                if (value != null) {
                    config.setProperty(key.getPropertyName(), value);
                }
            }
        } catch(FileNotFoundException e) {
            throw new BridgeSDKException(e.getMessage(), e);
        }
    }

    private Config(String host) {
        this();
        Preconditions.checkNotEmpty(host, "Host is null or empty");
        config.setProperty(Props.HOST.getPropertyName(), host);
    }

    static Config valueOf() {
        return new Config();
    }

    static Config valueOf(String host) {
        return new Config(host);
    }

    private void loadProperties(final InputStream inputStream, final Properties properties) {
        try {
            properties.load(inputStream);
        } catch(IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }
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
    String getHost() {
        return val(Props.HOST);
    }
    String getAuthApi() {
        return val(Props.AUTH_API);
    }
    String getProfileApi() {
        return val(Props.PROFILE_API);
    }
    String getConsentApi() {
        return val(Props.CONSENT_API);
    }
    String getStudyConsentApi() {
        return val(Props.STUDY_CONSENT_API);
    }
    String getTrackerApi() {
        return val(Props.TRACKER_API);
    }
    String getHealthDataApi() {
        return val(Props.HEALTH_DATA_API);
    }
    String getUploadApi() {
        return val(Props.UPLOAD_API);
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
    String getSurveyApi(String guid, DateTime timestamp) {
        return String.format(val(Props.SURVEY_API), guid, timestamp.toString(ISODateTimeFormat.dateTime()));
    }
    String getPublishSurveyApi(String guid, DateTime timestamp) {
        return String.format(val(Props.SURVEY_PUBLISH_API), guid, timestamp.toString(ISODateTimeFormat.dateTime()));
    }
    String getSurveyVersionsApi(String guid) {
        return String.format(val(Props.SURVEY_VERSIONS_API), guid);
    }
    String getSurveyUserApi(String guid, DateTime timestamp) {
        return String.format(val(Props.SURVEY_USER_API), guid, timestamp.toString(ISODateTimeFormat.dateTime()));
    }
    String getSurveyResponseApi(String guid) {
        return String.format(val(Props.SURVEY_RESPONSE_API), guid);
    }
    String getSchedulePlansApi() {
        return val(Props.SCHEDULEPLANS_API);
    }
    String getSchedulePlanApi(String guid) {
        return String.format(val(Props.SCHEDULEPLAN_API), guid);
    }

    private String val(Props prop) {
        return config.getProperty(prop.getPropertyName());
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
