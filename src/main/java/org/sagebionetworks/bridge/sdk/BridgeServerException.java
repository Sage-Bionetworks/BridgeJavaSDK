package org.sagebionetworks.bridge.sdk;

public class BridgeServerException extends RuntimeException {

    private static final long serialVersionUID = 1730838346722663310L;

    private final int statusCode;
    private final String endpoint;

    BridgeServerException(String message, Throwable throwable, String endpoint) {
        super(message, throwable);
        this.statusCode = 500;
        this.endpoint = endpoint;
    }
    
    BridgeServerException(String message, int statusCode, String endpoint) {
        super(message);
        this.statusCode = statusCode;
        this.endpoint = endpoint;
    }
    
    public int getStatusCode() {
        return this.statusCode;
    }
    
    public String getRestEndpoint() {
        return endpoint;
    }

    @Override
    public String toString() {
        return "BridgeServerException[message=" + getMessage() + ", statusCode=" + getStatusCode() + ", endpoint="
                + getRestEndpoint() + "]";
    }
}
