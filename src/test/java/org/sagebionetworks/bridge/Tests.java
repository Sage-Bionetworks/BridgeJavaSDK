package org.sagebionetworks.bridge;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;

public class Tests {

    public static final String TEST_KEY = "api";
    
    public static final String ADMIN_ROLE = "admin";

    public static final String RESEARCHER_ROLE = TEST_KEY + "_researcher";

    public static final Properties getApplicationProperties() {
        Properties properties = new Properties();
        File file = new File("src/main/resources/bridge-sdk.properties");
        try (InputStream in = new FileInputStream(file)) {
            properties.load(in);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
    
    public static final String randomIdentifier() {
        return ("sdk-" + RandomStringUtils.randomAlphabetic(5)).toLowerCase();
    }
}
