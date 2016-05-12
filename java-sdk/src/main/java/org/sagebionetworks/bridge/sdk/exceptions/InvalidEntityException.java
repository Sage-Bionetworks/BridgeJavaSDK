package org.sagebionetworks.bridge.sdk.exceptions;

import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class InvalidEntityException extends BridgeSDKException {

    private final Map<String,List<String>> errors;

    public InvalidEntityException(String message) {
        super(message, 400, null);
        this.errors = null;
    }

    public InvalidEntityException(String message, Map<String,List<String>> errors, String endpoint) {
        super(message, 400, endpoint);
        this.errors = errors;
    }

    public Map<String,List<String>> getErrors() {
        return errors;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((errors == null) ? 0 : errors.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        InvalidEntityException other = (InvalidEntityException) obj;
        if (errors == null) {
            if (other.errors != null)
                return false;
        } else if (!errors.equals(other.errors))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "InvalidEntityException [errors=" + getErrors() + ", statusCode=" + getStatusCode()
                + ", endpoint=" + getRestEndpoint() + ", message=" + getMessage() + "]";
    }

}
