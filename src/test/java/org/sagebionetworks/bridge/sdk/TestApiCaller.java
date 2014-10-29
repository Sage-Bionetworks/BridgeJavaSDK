package org.sagebionetworks.bridge.sdk;

import java.util.Map;

import org.apache.http.HttpResponse;

/**
 * Allows us to call the base HTTP methods directly in cases where we want to 
 * test direct access to the server APIs, without the enforcement of the SDK.
 */
public class TestApiCaller extends BaseApiCaller {

    public TestApiCaller(Session session) {
        super(session);
    }

    public HttpResponse publicGet(String url) {
        return super.publicGet(url);
    }

    public HttpResponse get(String url) {
        return super.get(url);
    }

    public HttpResponse get(String url, Map<String,String> queryParameters) {
        return super.get(url, queryParameters);
    }

    public HttpResponse post(String url) {
        return super.post(url);
    }

    public HttpResponse post(String url, String json) {
        return super.post(url, json);
    }

    public HttpResponse delete(String url) {
        return super.delete(url);
    }

    public HttpResponse delete(String url, Map<String,String> queryParameters) {
        return super.delete(url, queryParameters);
    }
    
}
