package org.sagebionetworks.bridge.sdk;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

final class Config {

    private static final String DEFAULT_CONFIG = "config.properties";

    private static final String[] fields = new String[5];

    private static final String PARTICIPANT_EMAIL = fields[0] = "PARTICIPANT_EMAIL";
    private static final String PARTICIPANT_PASSWORD = fields[1] = "PARTICIPANT_PASSWORD";

    private static final String ADMIN_EMAIL = fields[2] = "ADMIN_EMAIL";
    private static final String ADMIN_PASSWORD = fields[3] = "ADMIN_PASSWORD";

    private static final String URL = fields[4] = "URL";

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
        System.out.println("config created.");
        // Override any options with environment variables.
        overrideWithEnvironmentVariables(config);
        System.out.println("environment variables added");
        assertAllPropertiesPresent(config);
        System.out.println("all properties present.");
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
    String getUrl() { return config.getString(URL); }

    private void assertAllPropertiesPresent(Configuration config) {
        for (String field : fields) {
            assert config.getString(field) != null;
        }
    }

    private void overrideWithEnvironmentVariables(Configuration config) {
        String value;
        for (String field : fields) {
            value = System.getenv(field);
            if (value != null) {
                config.setProperty(field, value);
            }
        }
    }

    @Override
    public String toString() {
        assert config != null;
        assertAllPropertiesPresent(config);

        StringBuilder builder = new StringBuilder();
        builder.append("Config[");
        for (String field : fields) {
            builder.append(field + "=" + config.getString(field) + ", ");
        }
        builder.delete(builder.length()-2, builder.length()); // remove last ", "
        builder.append("]");
        return builder.toString();
    }
}
