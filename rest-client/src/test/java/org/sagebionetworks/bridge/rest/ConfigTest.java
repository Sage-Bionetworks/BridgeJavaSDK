package org.sagebionetworks.bridge.rest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConfigTest {

    @Test
    public void test() {
        // The default constructor assumes that home directory properties are set a specific way, which we
        // don't want for this test. So we force examination only of the test properties file.
        Config config = new Config("/bridge-sdk.properties");
        assertEquals("5", config.getSdkVersion());
        assertEquals("testName", config.getAppName());
        assertEquals("testName", config.fromProperty(Config.Props.APP_NAME));
        assertEquals("warn", config.getLogLevel());
        assertEquals("33", config.getAppVersion());
        assertEquals("Swankie Device", config.getDeviceName());
        assertEquals("en,fr", config.getLanguages());
        assertEquals("webOS", config.getOsName());
        assertEquals("ultimate", config.getOsVersion());
    }
    
    @Test
    public void testEnvironmentSubstitutionWithProperty() {
        Config config = new Config("/bridge-sdk-staging.properties");
        assertEquals("staging testName", config.getAppName());
        assertEquals("33", config.getAppVersion());
    }

    @Test
    public void testEnvironmentSubstitutionWithSystemProp() {
        try {
            System.setProperty("env", "production");
            Config config = new Config("/bridge-sdk.properties");
            assertEquals("production testName", config.getAppName());
            assertEquals("33", config.getAppVersion());
        } finally {
            System.setProperty("env", "local");
        }
    }
}
