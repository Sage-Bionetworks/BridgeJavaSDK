package org.sagebionetworks.bridge.sdk;

import org.apache.http.StatusLine;

public class BridgeServerException extends RuntimeException {

    private static final long serialVersionUID = 1730838346722663310L;

    private final StatusLine statusLine;
    private final String url;

    BridgeServerException(String message, StatusLine statusLine, String url) {
        super(message);
        this.statusLine = statusLine;
        this.url = url;
    }

    BridgeServerException(Throwable cause, StatusLine statusLine, String url) {
        this(cause.getMessage(), cause, statusLine, url);
    }

    BridgeServerException(String message, Throwable cause, StatusLine statusLine, String url) {
        super(message, cause);
        this.statusLine = statusLine;
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    public int getStatusCode() {
        return statusLine.getStatusCode();
    }

    public String getReasonPhrase() {
        return statusLine.getReasonPhrase();
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[message=" + getMessage() +
                ", url=" + getUrl() +
                ", statusCode=" + getStatusCode() +
                ", reasonPhrase=" + getReasonPhrase() + "]";
    }
}
