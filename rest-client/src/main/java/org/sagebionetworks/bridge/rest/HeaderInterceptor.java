package org.sagebionetworks.bridge.rest;

import java.io.IOException;

import okhttp3.Interceptor;

/**
 * Attaches headers that are common to all Bridge HTTP requests.
 */
class HeaderInterceptor implements Interceptor {
    private final String userAgent;

    public HeaderInterceptor(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        return chain.proceed(chain.request().newBuilder().header("User-Agent", userAgent).build());
    }
}