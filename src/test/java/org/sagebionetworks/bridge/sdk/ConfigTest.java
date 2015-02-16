package org.sagebionetworks.bridge.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

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
    
    
    @Test
    public void configPicksUpSystemProperty() {
        System.setProperty("ENV", "staging");
        Config conf = new Config();
        
        assertEquals(Environment.STAGING, conf.getEnvironment());
    }
}
