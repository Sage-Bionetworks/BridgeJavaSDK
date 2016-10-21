package org.sagebionetworks.bridge.sdk.models.accounts;

import static org.sagebionetworks.bridge.sdk.utils.BridgeUtils.TO_STRING_STYLE;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class AccountSummary {

    private final String firstName;
    private final String lastName;
    private final String email;
    private final DateTime createdOn;
    private final AccountStatus status;
    private final String id;
    
    @JsonCreator
    AccountSummary(@JsonProperty("firstName") String firstName, @JsonProperty("lastName") String lastName,
            @JsonProperty("email") String email, @JsonProperty("createdOn") DateTime createdOn,
            @JsonProperty("id") String id, @JsonProperty("status") AccountStatus status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.createdOn = createdOn;
        this.status = status;
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
    
    public DateTime getCreatedOn() {
        return createdOn;
    }

    public AccountStatus getStatus() {
        return status;
    }
    
    public String getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, email, createdOn, status, id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        AccountSummary other = (AccountSummary) obj;
        return Objects.equals(firstName, other.firstName) && Objects.equals(lastName, other.lastName)
                && Objects.equals(email, other.email) && Objects.equals(status, other.status)
                && Objects.equals(createdOn, other.createdOn) && Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE).append("firstName", firstName).append("lastName", lastName)
                .append("email", email).append("createdOn",createdOn).append("status", status)
                .append("id", id).toString();
    }
}
