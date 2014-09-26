package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class ConfigTest {

    @Test
    public void createConfig() {
        Config conf = Config.valueOfDefault();
        assertNotNull(conf);
    }
}
