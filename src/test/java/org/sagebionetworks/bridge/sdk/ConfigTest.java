package org.sagebionetworks.bridge.sdk;

import org.junit.Test;

public class ConfigTest {

    @Test
    public void createConfig() {
        Config conf = Config.valueOfDefault();
        System.out.println(conf);
    }
}
