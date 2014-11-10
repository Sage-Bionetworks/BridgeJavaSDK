package org.sagebionetworks.bridge.sdk.exceptions;

public class BridgeSDKException extends RuntimeException {

    private static final long serialVersionUID = -8415388154731660243L;

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
