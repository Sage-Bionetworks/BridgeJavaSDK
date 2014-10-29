package org.sagebionetworks.bridge;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tests {
    
    private static Logger logger = LoggerFactory.getLogger(Tests.class);
    
    public static void untilConsistently(Callable<Boolean> callable) throws Exception {
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
            String msg = String.format("untilConsistently sleeping %sms (%s/%s successes after loop %s/%s)", delay, successes,
                    successesLimit, loops, loopLimit);
            logger.info(msg);
            System.out.println(msg);
            Thread.sleep(delay);
        }
    }
}
