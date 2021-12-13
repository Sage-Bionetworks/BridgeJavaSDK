package org.sagebionetworks.bridge.rest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.sagebionetworks.bridge.rest.model.Environment;
import org.sagebionetworks.bridge.rest.model.SignIn;

/**
 * <p>The Config class provides the implementation for loading configuration for the ClientManager. The 
 * properties included in Config.Props are loaded from the following locations:</p>
 * <ul>
 *  <li>A bridge-sdk.properties file in the user's home directory;</li>
 *  <li>environment variables;</li>
 *  <li>Java system properties.</li>
 * </ul>
 * <p>Each location will overwrite the prior location, and values can be set programmatically, for example:</p>
 * <pre>
 * config.setProperty(Config.Props.ACCOUNT_EMAIL, "email@email.com");
 * </pre>
 */
public final class Config {

    private static final String CONFIG_FILE = "/bridge-sdk.properties";
    private static final String USER_CONFIG_FILE = System.getProperty("user.home") + "/bridge-sdk.properties";

    public enum Props {
        // Base properties
        ENV, 
        HOST,
        LANGUAGES,
        LOG_LEVEL, 

        // Convenience properties to set an account to execute SDK with
        ACCOUNT_APP_ID,
        ACCOUNT_EMAIL, 
        ACCOUNT_PASSWORD, 
        
        // Properties for the User-Agent header
        APP_NAME,
        APP_VERSION,
        DEVICE_NAME,
        OS_NAME,
        OS_VERSION,
        SDK_VERSION;

        public String getPropertyName() {
            return this.name().replace("_", ".").toLowerCase();
        }
    }

    private Properties config;
    private Environment environment;

    public Config() {
        this(CONFIG_FILE, USER_CONFIG_FILE);
    }
    
    public Config(String... paths) {
        config = new Properties();
        
        for (String onePath : paths) {
            try (InputStream in = this.getClass().getResourceAsStream(onePath)) {
                config.load(in);
            } catch (NullPointerException | IOException e) {
                loadProperties(onePath, config);
            }
        }

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
    
    /**
     * Arbitrary properties can be added to the bridge-sdk.properties file. These are not part of the 
     * required properties to use the Bridge REST SDK. 
     * @param propertyName the name of the configuration property
     * @return the value of the property if it exists.
     */
    public String get(String propertyName) {
        String value = config.getProperty(propertyName);
        return (value == null) ? value : value.trim();
    }

    public SignIn getAccountSignIn() {
        return new SignIn().appId(getAccountAppId()).email(getAccountEmail()).password(getAccountPassword());
    }

    public String getAccountAppId() {
        return fromProperty(Props.ACCOUNT_APP_ID);
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

    public String getLanguages() {
        return fromProperty(Props.LANGUAGES);
    }
    
    public String getAppName() {
        return fromProperty(Props.APP_NAME);
    }
    
    public String getAppVersion() {
        return fromProperty(Props.APP_VERSION);
    }
    
    public String getDeviceName() {
        return fromProperty(Props.DEVICE_NAME);
    }
    
    public String getOsName() {
        return fromProperty(Props.OS_NAME);
    }
    
    public String getOsVersion() {
        return fromProperty(Props.OS_VERSION);
    }

    public Environment getEnvironment() {
        return environment;
    }
    
    public String getLogLevel() {
        return fromProperty(Props.LOG_LEVEL);
    }
    
    public String getHost() {
        return fromProperty(Props.HOST);
    }

    /**
     * Method to get any of the values that are defined in the bridge-sdk.properties configuration file.
     *
     * @param property
     *         The property you are getting
     * @return The value for the property
     */
    public String fromProperty(Props property) {
        String value = config.getProperty(property.getPropertyName());
        return (value == null || value.trim().length() == 0) ? null : value.trim();
    }
}
