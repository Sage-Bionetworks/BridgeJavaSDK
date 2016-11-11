package org.sagebionetworks.bridge.rest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConfigTest {

    @Test
    public void test() {
        Config config = new Config();
        assertEquals("5", config.getSdkVersion());
        assertEquals("testName", config.getAppName());
        assertEquals("warn", config.getLogLevel());
    }
    
}
