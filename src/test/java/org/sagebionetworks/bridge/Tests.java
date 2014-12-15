package org.sagebionetworks.bridge;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tests {

    private static Logger logger = LoggerFactory.getLogger(Tests.class);
    
    public static final String TEST_KEY = "api";
    
    public static final String ADMIN_ROLE = "admin";

    public static final String RESEARCHER_ROLE = TEST_KEY + "_researcher";

    public static final void untilConsistent(Callable<Boolean> callable) throws Exception {
        // Have been seeing some significant delays locally, hence the really high wait/try numbers.
        
        callable.call();
        // Don't poll more than once, no code executes off the request thread right now.
        /*
        int delay = 500;
        int loopLimit = 150;
        int successesLimit = 1; 
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
        }*/
    }

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
