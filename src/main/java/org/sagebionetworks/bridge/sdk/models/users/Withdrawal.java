package org.sagebionetworks.bridge.sdk.models.users;

import java.util.Objects;

public final class Withdrawal {

    private final String reason;
    
    public Withdrawal(String reason) {
        this.reason = reason;
    }
    
    public String getReason() {
        return reason;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hash(reason);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Withdrawal other = (Withdrawal) obj;
        return Objects.equals(reason, other.reason);
    }

    @Override
    public String toString() {
        return "Withdrawal [reason=" + reason + "]";
    }
    
}
