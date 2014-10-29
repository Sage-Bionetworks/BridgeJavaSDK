package org.sagebionetworks.bridge;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tests {
    
    private static Logger logger = LoggerFactory.getLogger(Tests.class);
    
    public static final String TEST_KEY = "teststudy";
    
    public static final String ADMIN_ROLE = "admin";
    
    public static final String RESEARCHER_ROLE = TEST_KEY + "_researcher";
    
    public static final void untilConsistent(Callable<Boolean> callable) throws Exception {
        int delay = 200;
        int loopLimit = 40;
        int successesLimit = 3;
        int loops = 0;
        int successes = 0;
        while (successes < successesLimit && loops < loopLimit) {
            if (callable.call()) {
                successes++;
            } else {
                successes = 0;
            }
            loops++;
            String msg = String.format("untilConsistent sleeping %sms (%s/%s successes after loop %s/%s)", delay, successes,
                    successesLimit, loops, loopLimit);
            logger.debug(msg);
            Thread.sleep(delay);
        }
    }
    
    public static final Properties getApplicationProperties() {
        Properties properties = new Properties();
        File file = new File("bridge-sdk.properties");
        try (InputStream in = new FileInputStream(file)) {
            properties.load(in);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
    
}
