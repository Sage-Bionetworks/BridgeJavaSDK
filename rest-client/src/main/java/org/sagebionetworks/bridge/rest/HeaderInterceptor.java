package org.sagebionetworks.bridge.rest;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request.Builder;

/**
 * Attaches headers that are common to all Bridge HTTP requests.
 */
class HeaderInterceptor implements Interceptor {
    private final String userAgent;
    private final String acceptLanguage;

    public HeaderInterceptor(String userAgent) {
        this(userAgent, null);
    }

    public HeaderInterceptor(String userAgent, String acceptLanguage) {
        this.userAgent = userAgent;
        this.acceptLanguage = acceptLanguage;
    }
    
    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Builder builder = chain.request().newBuilder();
        if (userAgent != null) {
            builder.header("User-Agent", userAgent);    
        }
        if (acceptLanguage != null) {
            builder.header("Accept-Language", acceptLanguage);
        }
        return chain.proceed(builder.build());
    }
}