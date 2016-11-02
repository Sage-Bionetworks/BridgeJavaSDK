package org.sagebionetworks.bridge.rest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.sagebionetworks.bridge.rest.model.Environment;
import org.sagebionetworks.bridge.rest.model.SignIn;

public final class Config {

    private static final String CONFIG_FILE = "/bridge-sdk.properties";
    private static final String USER_CONFIG_FILE = System.getProperty("user.home") + "/bridge-sdk.properties";

    public enum Props {
        // These all require an entry in bridge-sdk.properties (accounts are optional).
        ACCOUNT_EMAIL(null), 
        ACCOUNT_PASSWORD(null), 
        ADMIN_EMAIL(null), 
        ADMIN_PASSWORD(null), 
        DEV_NAME(null), 
        ENV(null), 
        LOG_LEVEL(null), 
        SDK_VERSION(null), 
        STUDY_IDENTIFIER(null);

        private String endpoint;
        private Props(String endpoint) {
            this.endpoint = endpoint;
        }
        public String getEndpoint() {
            return endpoint;
        }
        public String getPropertyName() {
            return this.name().replace("_", ".").toLowerCase();
        }
    }

    private Properties config;
    private Environment environment;

    public Config() {
        config = new Properties();

        // Load from default configuration file
        try (InputStream in = this.getClass().getResourceAsStream(CONFIG_FILE)) {
            config.load(in);
        } catch (IOException e) {
            // This is okay, we'll fall back to examining environmental variables.
            System.err.println(e.getMessage());
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
     *      The property you are setting
     * @param value
     *      The value to set for the property
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
     *      One of the environments supported by the Bridge server. If you are not an internal Sage
     *      developer, you should never have to set this value to anything but Environment.PRODUCTION.
     */
    public void set(Environment env) {
        this.environment = env;
    }

    public SignIn getAccountSignIn() {
        return new SignIn().study(getStudyIdentifier()).email(getAccountEmail()).password(getAccountPassword());
    }

    public SignIn getAdminSignIn() {
        return new SignIn().study(getStudyIdentifier()).email(getAdminEmail()).password(getAdminPassword());
    }
    
    public String getStudyIdentifier() {
        return fromProperty(Props.STUDY_IDENTIFIER);
    }
    
    public String getSdkVersion() {
        return fromProperty(Props.SDK_VERSION);
    }
    
    public String getAccountEmail() {
        return fromProperty(Props.ACCOUNT_EMAIL);
    }

    public String getAccountPassword() {
        return fromProperty(Props.ACCOUNT_PASSWORD);
    }

    public String getAdminEmail() {
        return fromProperty(Props.ADMIN_EMAIL);
    }

    public String getAdminPassword() {
        return fromProperty(Props.ADMIN_PASSWORD);
    }

    public String getDevName() {
        return fromProperty(Props.DEV_NAME);
    }

    public Environment getEnvironment() {
        return environment;
    }
    
    public String getLogLevel() {
        return fromProperty(Props.LOG_LEVEL);
    }

    private String fromProperty(Props prop) {
        String value = config.getProperty(prop.getPropertyName());
        checkNotNull(value, "The property '" + prop.getPropertyName() + "' has not been set.");
        return value.trim();
    }
}
