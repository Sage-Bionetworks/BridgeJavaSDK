package org.sagebionetworks.bridge.sdk.exceptions;

@SuppressWarnings("serial")
public class BridgeSDKException extends RuntimeException {

    public BridgeSDKException(String message, Throwable cause) {
        super(message, cause);
    }

    public BridgeSDKException(String message) {
        super(message);
    }

    public BridgeSDKException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[Error message: " + getMessage() + "]";
    }

}
