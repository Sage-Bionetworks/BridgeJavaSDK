package org.sagebionetworks.bridge.rest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Log warning msg from bridge pf's http headers
 */
class WarningHeaderInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(WarningHeaderInterceptor.class);
    
    static final String BRIDGE_API_STATUS_HEADER = "Bridge-Api-Status";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        logWarning(response);
        return response;
    }
    
    /**
     * @param response
     */
    private void logWarning(Response response) {
        String statusHeaders = response.headers().get(BRIDGE_API_STATUS_HEADER);
        if (statusHeaders != null && statusHeaders.length() > 0 ) {
            List<String> warningsMsgs = Arrays.asList(statusHeaders.trim().split("; "));
            for (String warningsMsg : warningsMsgs) {
                logger.warn(response.request().url().toString() + " " + warningsMsg);
            }
        }
    }
}