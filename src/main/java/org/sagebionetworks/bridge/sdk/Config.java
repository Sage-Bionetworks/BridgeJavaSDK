package org.sagebionetworks.bridge.sdk;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

final class Config {

    private static final String DEFAULT_CONFIG = "config.properties";

    private static final String[] properties = new String[4];

    private static final String PARTICIPANT_EMAIL = properties[0] = "PARTICIPANT_EMAIL";
    private static final String PARTICIPANT_PASSWORD = properties[1] = "PARTICIPANT_PASSWORD";

    private static final String ADMIN_EMAIL = properties[2] = "ADMIN_EMAIL";
    private static final String ADMIN_PASSWORD = properties[3] = "ADMIN_PASSWORD";

    private Configuration config;

    private Config(String configPath) {
        assert configPath != null;
        assert configPath.endsWith(".properties");

        // Set with config file.
        try {
            config = new PropertiesConfiguration(configPath);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

        // Override any options with environment variables.
        overrideWithEnvironmentVariables(config);

        assertAllPropertiesPresent(config);
    }

    static Config valueOf(String configPath) {
        return new Config(configPath);
    }

    static Config valueOfDefault() {
        return new Config(DEFAULT_CONFIG);
    }

    String getParticipantEmail() { return config.getString(PARTICIPANT_EMAIL); }
    String getParticipantPassword() { return config.getString(PARTICIPANT_PASSWORD); }
    String getAdminEmail() { return config.getString(ADMIN_EMAIL); }
    String getAdminPassword() { return config.getString(ADMIN_PASSWORD); }

    private void assertAllPropertiesPresent(Configuration config) {
        for (String property : properties) {
            assert config.getString(property) != null : property + " property is not present.";
        }
    }

    private void overrideWithEnvironmentVariables(Configuration config) {
        String value;
        for (String property : properties) {
            value = System.getenv(property);
            if (value != null) {
                config.setProperty(property, value);
            }
        }
    }

    @Override
    public String toString() {
        assert config != null;
        assertAllPropertiesPresent(config);

        StringBuilder builder = new StringBuilder();
        builder.append("Config[");
        for (String property : properties) {
            builder.append(property + "=" + config.getString(property) + ", ");
        }
        builder.delete(builder.length()-2, builder.length()); // remove last ", "
        builder.append("]");
        return builder.toString();
    }
}
