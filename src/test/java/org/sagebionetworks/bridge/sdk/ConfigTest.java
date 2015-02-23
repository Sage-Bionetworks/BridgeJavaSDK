package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.sagebionetworks.bridge.sdk.Config;

public class ConfigTest {

    @Test
    public void createConfig() {
        Config conf = new Config();
        assertNotNull(conf);
        assertEquals("conf returns values", "/researchers/v1/surveys/asdf/published",
                conf.getSurveyMostRecentlyPublishedVersionApi("asdf"));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void configChecksArguments() {
        Config conf = new Config();
        conf.getSurveyMostRecentlyPublishedVersionApi(null);
    }
    
    // Doesn't work on Travis, does work locally.
    @Test
    @Ignore
    public void configPicksUpSystemProperty() {
        System.setProperty("ENV", "staging");
        Config conf = new Config();
        
        assertTrue(Environment.STAGING == conf.getEnvironment());
    }
}
