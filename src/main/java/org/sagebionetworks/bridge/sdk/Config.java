package org.sagebionetworks.bridge.sdk;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

final class Config {

    private final String CONFIG;
    private static final String DEFAULT_CONFIG = "config.properties";

    private static final String PARTICIPANT_EMAIL = "participant.email";
    private static final String PARTICIPANT_PASSWORD = "participant.password";

    private static final String ADMIN_EMAIL = "admin.email";
    private static final String ADMIN_PASSWORD = "admin.password";

    private static final String URL = "url";

    private Configuration config;

    private Config(String configPath) {
        if (configPath == null) {
            CONFIG = DEFAULT_CONFIG;
        } else {
            assert configPath.endsWith(".properties");
            CONFIG = configPath;
        }
        try {
            config = new PropertiesConfiguration(CONFIG);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        assertAllPropertiesPresent(config);
    }

    static Config valueOf(String configPath) {
        return new Config(configPath);
    }

    static Config valueOfDefault() {
        return new Config(null);
    }

    String getParticipantEmail() { return config.getString(PARTICIPANT_EMAIL); }
    String getParticipantPassword() { return config.getString(PARTICIPANT_PASSWORD); }
    String getAdminEmail() { return config.getString(ADMIN_EMAIL); }
    String getAdminPassword() { return config.getString(ADMIN_PASSWORD); }
    String getUrl() { return config.getString(URL); }

    private void assertAllPropertiesPresent(Configuration config) {
        assert config.getString(PARTICIPANT_EMAIL) != null;
        assert config.getString(PARTICIPANT_PASSWORD) != null;
        assert config.getString(ADMIN_EMAIL) != null;
        assert config.getString(ADMIN_PASSWORD) != null;
        assert config.getString(URL) != null;
    }
}
