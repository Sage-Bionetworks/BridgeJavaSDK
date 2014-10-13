package org.sagebionetworks.bridge.sdk;

import org.apache.http.StatusLine;

public class BridgeServerException extends RuntimeException {

    private static final long serialVersionUID = 1730838346722663310L;

    private final int statusCode;
    private final String reasonPhrase;
    private final String serverResponse;

    BridgeServerException(String message, StatusLine statusLine, String serverResponseMessage) {
        super(message);
        this.statusCode = statusLine == null ? -1 : statusLine.getStatusCode();
        this.reasonPhrase = statusLine == null ? "No reason phrase." : statusLine.getReasonPhrase();
        this.serverResponse = serverResponseMessage == null ? "No server response." : serverResponseMessage;
    }

    BridgeServerException(String message, Throwable cause, StatusLine statusLine, String serverResponseMessage) {
        super(message, cause);
        this.statusCode = statusLine == null ? -1 : statusLine.getStatusCode();
        this.reasonPhrase = statusLine == null ? "No reason phrase." : statusLine.getReasonPhrase();
        this.serverResponse = serverResponseMessage == null ? "No server response." : serverResponseMessage;
    }

    BridgeServerException(Throwable cause, StatusLine statusLine, String serverResponseMessage) {
        this(cause.getMessage(), cause, statusLine, serverResponseMessage);
    }

    BridgeServerException(String message, Throwable cause) {
        this(message, cause, null, null);
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    public String getServerResponseMessage() {
        return this.serverResponse;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[message=" + getMessage() +
                ", statusCode=" + getStatusCode() +
                ", reasonPhrase=" + getReasonPhrase() +
                ", serverResponseMessage=" + getServerResponseMessage() + "]";
    }
}
