package org.sagebionetworks.bridge.rest;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request.Builder;

/**
 * Attaches headers that are common to all Bridge HTTP requests.
 */
class HeaderInterceptor implements Interceptor {
    public static final String BRIDGE_SESSION = "Bridge-Session";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String USER_AGENT = "User-Agent";
    
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
            builder.header(USER_AGENT, userAgent);    
        } else {
            // Use a blank User-Agent. Otherwise, OkHttp will use the default User-Agent.
            builder.header(USER_AGENT, "");
        }
        if (acceptLanguage != null) {
            builder.header(ACCEPT_LANGUAGE, acceptLanguage);
        }
        return chain.proceed(builder.build());
    }
}