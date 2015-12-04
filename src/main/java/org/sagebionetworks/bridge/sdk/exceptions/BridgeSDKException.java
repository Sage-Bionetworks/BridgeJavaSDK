package org.sagebionetworks.bridge.sdk.exceptions;

import static org.sagebionetworks.bridge.sdk.utils.Utilities.TO_STRING_STYLE;

import org.apache.commons.lang3.builder.ToStringBuilder;

@SuppressWarnings("serial")
public class BridgeSDKException extends RuntimeException {

    private final int statusCode;
    private final String endpoint;
    
    public BridgeSDKException(String message, Throwable throwable, String endpoint) {
        super(message, throwable);
        this.statusCode = 500;
        this.endpoint = endpoint;
    }

    public BridgeSDKException(String message, int statusCode, String endpoint) {
        super(message);
        this.statusCode = statusCode;
        this.endpoint = endpoint;
    }

    public BridgeSDKException(Throwable t, int statusCode, String endpoint) {
        super(t.getMessage(), t);
        this.statusCode = statusCode;
        this.endpoint = endpoint;
    }

    public BridgeSDKException(String message, Throwable throwable) {
        this(message, throwable, null);
    }

    public BridgeSDKException(String message, int statusCode) {
        this(message, statusCode, null);
    }
    
    public final int getStatusCode() {
        return this.statusCode;
    }

    public final String getRestEndpoint() {
        return endpoint;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((endpoint == null) ? 0 : endpoint.hashCode());
        result = prime * result + statusCode;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BridgeSDKException other = (BridgeSDKException) obj;
        if (endpoint == null) {
            if (other.endpoint != null)
                return false;
        } else if (!endpoint.equals(other.endpoint))
            return false;
        if (statusCode != other.statusCode)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE)
                .append("message", getMessage())
                .append("statusCode", getStatusCode())
                .append("endpoint",getRestEndpoint()).toString();
    }
}
