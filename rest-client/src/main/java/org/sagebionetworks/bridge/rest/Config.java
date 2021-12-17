package org.sagebionetworks.bridge.rest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.sagebionetworks.bridge.rest.model.Environment;
import org.sagebionetworks.bridge.rest.model.SignIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

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
    private static final Logger LOG = LoggerFactory.getLogger(Config.class);

    private static final String CONFIG_FILE = "/bridge-sdk.properties";
    private static final String USER_CONFIG_FILE = System.getProperty("user.home") + "/bridge-sdk.properties";
    /**
     * We chose two property names that are identical to System properties that are set in a Java environment. 
     * For backwards compatibility reasons, we do not want to pick up these values from the environment (e.g.
     * if this is being used in a Java environment, the setting of these properties in bridge-sdk.properties
     * would just be ignored).
     */
    private static final Set<String> OVERLOADED_SYSTEM_PROPERTIES = ImmutableSet.of("os.name", "os.version");
    
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
        SDK_VERSION,
        
        // test properties... not documented for public use
        DEV_NAME,
        ADMIN_EMAIL,
        ADMIN_PASSWORD,
        SYNAPSE_TEST_USER,
        SYNAPSE_TEST_USER_ID,
        SYNAPSE_TEST_USER_PASSWORD,
        SYNAPSE_TEST_USER_API_KEY;

        public String getPropertyName() {
            return this.name().replace("_", ".").toLowerCase();
        }
    }
    
    private static interface ConfigReader {
        String read(String name);
    }
    
    private final ConfigReader envReader = new ConfigReader() {
        @Override
        public String read(String name) {
            try {
                // Change to a valid environment variable name
                name = name.toUpperCase().replace('.', '_');
                return System.getenv(name);
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            }
        }
    };

    private final ConfigReader sysReader = new ConfigReader() {
        @Override
        public String read(String name) {
            try {
                return System.getProperty(name);
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            }
        }
    };

    private Properties config;
    private Environment environment = Environment.LOCAL;

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

        // Finally, overwrite from environment variables and system properties. property values 
        // are overridden by environment variables, which are overridden by system properties.
        String envName = envReader.read("env");
        if (envName == null) {
            envName = sysReader.read("env");
        }
        if (envName == null) {
            envName = config.getProperty("env");    
        }
        if (envName != null) {
            environment = Environment.valueOf(envName.toUpperCase());
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("SDK Config environment: " + environment);    
        }
        this.config = new Properties(collapse(config, environment.name().toLowerCase()));
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
     * Collapses the properties into new properties relevant to the current
     * environment. 1) Default properties usually bundled in the code base as
     * resources. 2) Overwrite with properties read from the user's home
     * directory 3) Merge the properties to the current environment 4) Overwrite
     * with properties read from the environment variables. 5) Overwrite with
     * properties read from the command-line arguments.
     * 
     * 
     */
    private Properties collapse(final Properties properties, final String envName) {
        Properties collapsed = new Properties();
        // Read the default properties
        for (final String key : properties.stringPropertyNames()) {
            if (isDefaultProperty(key)) {
                collapsed.setProperty(key, properties.getProperty(key));
            }
        }
        // Overwrite with properties for the current environment
        for (final String key : properties.stringPropertyNames()) {
            if (key.startsWith(envName + ".")) {
                String strippedName = key.substring(envName.length() + 1);
                collapsed.setProperty(strippedName, properties.getProperty(key));
            }
        }
        
        // Finally, overwrite from environment variables and system properties. property values 
        // are overridden by environment variables, which are overridden by system properties.

        // Overwrite with environment variables and system properties
        for (final String key : properties.stringPropertyNames()) {
            String value = envReader.read(key);
            if (value == null && !OVERLOADED_SYSTEM_PROPERTIES.contains(key)) {
                value = sysReader.read(key);
            }
            if (value != null) {
                if (key.startsWith(envName + ".")) {
                    String strippedName = key.substring(envName.length() + 1);
                    collapsed.setProperty(strippedName, value);
                } else {
                    collapsed.setProperty(key, value);
                }
            }
        }
        return collapsed;
    }
    
    /**
     * When the property is not bound to a particular environment.
     */
    private boolean isDefaultProperty(final String propName) {
        for (Environment env : Environment.values()) {
            String envPrefix = env.name().toLowerCase() + ".";
            if (propName.startsWith(envPrefix)) {
                return false;
            }
        }
        return true;
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
