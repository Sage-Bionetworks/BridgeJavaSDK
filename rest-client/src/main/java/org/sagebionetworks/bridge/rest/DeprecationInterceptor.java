package org.sagebionetworks.bridge.rest;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Log deprecation of HTTP endpoints on the Bridge server to warn developers the API 
 * has changed and is subject to removal.
 */
class DeprecationInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(ErrorResponseInterceptor.class);
    
    private static final String BRIDGE_API_STATUS_HEADER = "Bridge-Api-Status";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        logDeprecationWarning(response);
        return response;
    }
    
    /**
     * TODO: This is really a separate interceptor, it's orthogonal to exception handling.
     * @param response
     */
    private void logDeprecationWarning(Response response) {
        List<String> statusHeaders = response.headers().toMultimap().get(BRIDGE_API_STATUS_HEADER);
        if (statusHeaders != null && statusHeaders.size() > 0 && "deprecated".equals(statusHeaders.get(0))) {
            logger.warn(response.request().url().toString()
                    + " is a deprecated API. This API may return 410 (Gone) at a future date. "
                    + "Please consult the API documentation for an alternative.");
        }
    }
}