package org.sagebionetworks.bridge.sdk;

public enum Environment {

    LOCAL("http://localhost:9000"),
    DEV("https://webservices-develop.sagebridge.org"),
    STAGING("https://webservices-staging.sagebridge.org"),
    PRODUCTION("https://webservices.sagebridge.org");
    
    private final String url;
    
    private Environment(String url) {
        this.url = url;
    }
    
    public String getUrl() {
        return url;
    }
    
}
