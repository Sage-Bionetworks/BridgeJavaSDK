package org.sagebionetworks.bridge.rest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mockito.Mockito;
import org.sagebionetworks.bridge.rest.model.Environment;

public class ConfigTest extends Mockito {

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
    public void read_fromEnv() {
        Config config = spy(Config.class);
        when(config.readEnv("FOO_BAR")).thenReturn("baz");
        
        String retValue = config.read("foo.bar", null);
        assertEquals("baz", retValue);
    }
    
    @Test
    public void read_fromEnvWithPrefix() {
        Config config = spy(Config.class);
        config.set(Environment.LOCAL);
        
        when(config.readEnv("FOO_BAR")).thenReturn("baz");
        when(config.readEnv("LOCAL_FOO_BAR")).thenReturn("biff");
        
        String retValue = config.read("FOO_BAR", null);
        assertEquals("biff", retValue);
    }
    
    @Test
    public void read_fromSystemProperty() {
        Config config = spy(Config.class);
        when(config.readSystemProp("foo.bar")).thenReturn("baz");
        
        String retValue = config.read("FOO_BAR", null);
        assertEquals("baz", retValue);
    }
    
    @Test
    public void read_fromSystemPropertyWithPrefix() {
        Config config = spy(Config.class);
        config.set(Environment.LOCAL);
        
        when(config.readSystemProp("foo.bar")).thenReturn("baz");
        when(config.readSystemProp("local.foo.bar")).thenReturn("biff");
        
        String retValue = config.read("FOO_BAR", null);
        assertEquals("biff", retValue);
    }
    
    @Test
    public void read_envOverridesSystemProperty() {
        // We can't spy on the read methods before we construct config and at that time,
        // all the properties are read in. We *can* test environmental overrides in the 
        // properties file and we can test overrides of the environment by system properties.
        Config config = spy(Config.class);
        when(config.readEnv("FOO")).thenReturn("env testName");
        when(config.readSystemProp("foo")).thenReturn("system prop testName");
        
        String retValue = config.read("foo", null);
        assertEquals("env testName", retValue);
    }
    
    @Test
    public void read_envPropTriesSystemCasing() {
        // We can't spy on the read methods before we construct config and at that time,
        // all the properties are read in. We *can* test environmental overrides in the 
        // properties file and we can test overrides of the environment by system properties.
        Config config = spy(Config.class);
        when(config.readSystemProp("foo.bar")).thenReturn("baz");
        
        String retValue = config.read("FOO_BAR", null);
        assertEquals("baz", retValue);
    }
    
    @Test
    public void read_systemPropTriesEnvCasing() {
        // We can't spy on the read methods before we construct config and at that time,
        // all the properties are read in. We *can* test environmental overrides in the 
        // properties file and we can test overrides of the environment by system properties.
        Config config = spy(Config.class);
        when(config.readSystemProp("FOO_BAR")).thenReturn("baz");
        
        String retValue = config.read("foo.bar", null);
        assertEquals("baz", retValue);
    }
    
    @Test
    public void read_default() {
        Config config = spy(Config.class);
        
        String retValue = config.read("foo.bar", "A");
        assertEquals("A", retValue);
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
