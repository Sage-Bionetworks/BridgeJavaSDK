package org.sagebionetworks.bridge.sdk;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

final class Config {

    Utilities utils = Utilities.getInstance();

    private static final String DEFAULT_CONFIG = "bridge-sdk.properties";

    private static final List<String> properties = new ArrayList<String>();

    private static final String PARTICIPANT_EMAIL = "PARTICIPANT_EMAIL";
    private static final String PARTICIPANT_PASSWORD = "PARTICIPANT_PASSWORD";

    private static final String ADMIN_EMAIL = "ADMIN_EMAIL";
    private static final String ADMIN_PASSWORD = "ADMIN_PASSWORD";

    private static final String HOST = "HOST";

    private static final String AUTH_API = "AUTH_API";
    private static final String PROFILE_API = "PROFILE_API";
    private static final String CONSENT_API = "CONSENT_API";
    private static final String STUDY_CONSENT_API = "STUDY_CONSENT_API";
    private static final String SCHEDULE_PLANNING_API = "SCHEDULE_PLANNING_API";
    private static final String SCHEDULES_API = "SCHEDULES_API";
    private static final String ACTIVITIES_API = "ACTIVITIES_API";
    private static final String SURVEYS_API = "SURVEYS_API";
    private static final String SURVEY_RESPONSE_API = "SURVEY_RESPONSE_API";
    private static final String TRACKER_API = "TRACKER_API";
    private static final String HEALTH_DATA_API = "HEALTH_DATA_API";
    private static final String UPLOAD_API = "UPLOAD_API";
    private static final String USER_MANAGEMENT_API = "USER_MANAGEMENT_API";

    private Configuration config;

    private Config(String configPath) {
        assert configPath != null;
        assert configPath.endsWith(".properties");
        assert properties.size() > 0;

        // Set with config file.
        try {
            config = new PropertiesConfiguration(configPath);
        } catch (ConfigurationException e) {
            throw new BridgeSDKException(
                    "Something went wrong while reading the config file. Is the path or file correct? path="
                            + configPath, e);
        }

        // Override any options with environment variables.
        overrideWithEnvironmentVariables(config);

        assertAllPropertiesPresent();
        assertHostAndEmailValid();
    }

    static Config valueOf(String configPath) {
        if (properties.size() == 0) {
            constructPropertiesList();
        }
        return new Config(configPath);
    }

    static Config valueOfDefault() {
        if (properties.size() == 0) {
            constructPropertiesList();
        }
        return new Config(DEFAULT_CONFIG);
    }

    private static void constructPropertiesList() {
        assert properties.size() == 0;
        properties.add(PARTICIPANT_EMAIL);
        properties.add(PARTICIPANT_PASSWORD);
        properties.add(ADMIN_EMAIL);
        properties.add(ADMIN_PASSWORD);
        properties.add(HOST);
        properties.add(AUTH_API);
        properties.add(PROFILE_API);
        properties.add(CONSENT_API);
        properties.add(STUDY_CONSENT_API);
        properties.add(SCHEDULE_PLANNING_API);
        properties.add(SCHEDULES_API);
        properties.add(ACTIVITIES_API);
        properties.add(SURVEYS_API);
        properties.add(SURVEY_RESPONSE_API);
        properties.add(TRACKER_API);
        properties.add(HEALTH_DATA_API);
        properties.add(UPLOAD_API);
        properties.add(USER_MANAGEMENT_API);
    }

    String getParticipantEmail() { return config.getString(PARTICIPANT_EMAIL); }
    String getParticipantPassword() { return config.getString(PARTICIPANT_PASSWORD); }
    String getAdminEmail() { return config.getString(ADMIN_EMAIL); }
    String getAdminPassword() { return config.getString(ADMIN_PASSWORD); }
    String getHost() { return config.getString(HOST); }
    String getAuthApi() { return config.getString(AUTH_API); }
    String getProfileApi() { return config.getString(PROFILE_API); }
    String getConsentApi() { return config.getString(CONSENT_API); }
    String getStudyConsentApi() { return config.getString(STUDY_CONSENT_API); }
    String getSchedulePlanningApi() { return config.getString(SCHEDULE_PLANNING_API); }
    String getSchedulesApi() { return config.getString(SCHEDULES_API); }
    String getActivitiesApi() { return config.getString(ACTIVITIES_API); }
    String getSurveyApi() { return config.getString(SURVEYS_API); }
    String getSurveyResponsesApi() { return config.getString(SURVEY_RESPONSE_API); }
    String getTrackerApi() { return config.getString(TRACKER_API); }
    String getHealthDataApi() { return config.getString(HEALTH_DATA_API); }
    String getUploadApi() { return config.getString(UPLOAD_API); }
    String getUserManagementApi() { return config.getString(USER_MANAGEMENT_API); }

    private void overrideWithEnvironmentVariables(Configuration config) {
        String value;
        for (String property : properties) {
            value = System.getenv(property);
            if (value != null) {
                config.setProperty(property, value);
            }
        }
    }

    private void assertAllPropertiesPresent() {
        for (String property : properties) {
            assert config.getString(property) != null : property + " property is not present.";
        }
    }

    private void assertHostAndEmailValid() {
        if (!utils.isValidEmail(getParticipantEmail())) {
            throw new IllegalArgumentException(getParticipantEmail() + " is not a valid email address.");
        } else if (!utils.isValidEmail(getAdminEmail())) {
            throw new IllegalArgumentException(getAdminEmail() + " is not a valid email address.");
        } else if (!utils.isValidUrl(getHost()) || !getHost().endsWith("/")) {
            throw new IllegalArgumentException(getHost()
                    + " is not a valid URL. Needs to be of the form http://www.sagebase.org/");
        }
        // BridgePF is not currently pingable, so commenting out for now.
        // else if (!isConnectableUrl(getHost(), 1000)) {
        // throw new IllegalArgumentException("Could not connect to the URL: " + getHost());
        // }
    }

    @Override
    public String toString() {
        assert config != null;
        assertAllPropertiesPresent();

        StringBuilder builder = new StringBuilder();
        builder.append("Config[");
        for (String property : properties) {
            builder.append(property + "=" + config.getString(property) + ", ");
        }
        builder.delete(builder.length() - 2, builder.length()); // remove last ", "
        builder.append("]");
        return builder.toString();
    }
}
