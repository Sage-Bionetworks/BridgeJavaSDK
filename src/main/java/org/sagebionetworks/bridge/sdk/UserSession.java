package org.sagebionetworks.bridge.sdk;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

final class UserSession {

    private static final ObjectMapper mapper = new ObjectMapper().configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final String username;
    private final String sessionToken;
    private final boolean authenticated;
    private final boolean consented;
    private final boolean dataSharing;

    @JsonCreator
    private UserSession(@JsonProperty("username") String username, @JsonProperty("sessionToken") String sessionToken,
            @JsonProperty("authenticated") boolean authenticated, @JsonProperty("consented") boolean consented,
            @JsonProperty("dataSharing") boolean dataSharing) {
        this.username = username;
        this.sessionToken = sessionToken;
        this.consented = consented;
        this.authenticated = authenticated;
        this.dataSharing = dataSharing;
    }

    static UserSession valueOf(JsonNode json) {
        return mapper.convertValue(json, UserSession.class);
    }

    static UserSession valueOf(String json) {
        UserSession session = null;
        try {
            session = mapper.readValue(json, UserSession.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return session;
    }

    UserSession signOut() {
        return new UserSession(this.username, null, false, this.consented, this.dataSharing);
    }

    public String getUsername() { return this.username; }
    public String getSessionToken() { return this.sessionToken; }
    public boolean isAuthenticated() { return this.authenticated; }
    public boolean isConsented() { return this.consented; }
    public boolean isDataSharing() { return this.dataSharing; }

    @Override
    public String toString() {
        return "UserSession[username=" + username +
                ", sessionToken=" + sessionToken +
                ", authenticated=" + Boolean.toString(authenticated) +
                ", consented=" + Boolean.toString(consented) +
                ", dataSharing=" + Boolean.toString(dataSharing) + "]";
    }

}
