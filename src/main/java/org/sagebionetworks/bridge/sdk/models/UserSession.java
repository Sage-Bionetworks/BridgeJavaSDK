package org.sagebionetworks.bridge.sdk.models;

import com.fasterxml.jackson.databind.JsonNode;

public final class UserSession {

    private final String username;
    private final String sessionToken;
    private final boolean consented;
    private final boolean authenticated;

    private UserSession(String username, String sessionToken, boolean authenticated, boolean consented) {
        this.username = username;
        this.sessionToken = sessionToken;
        this.consented = consented;
        this.authenticated = authenticated;
    }
    
    public final String getUsername() { return this.username; }
    public final String getSessionToken() { return this.sessionToken; }
    public final boolean isAuthenticated() { return this.authenticated; }
    public final boolean isConsented() { return this.consented; }

    public static final UserSession valueOf(String username, String sessionToken, boolean authenticated,
            boolean consented) {
        if (username == null) {
            throw new IllegalArgumentException("username is null.");
        } else if (sessionToken == null) {
            throw new IllegalArgumentException("sessionToken is null.");
        }
        return new UserSession(username, sessionToken, authenticated, consented);
    }

    public static final UserSession valueOf(JsonNode json) {
        assertAllFieldsExist(json);
        return new UserSession(json.get("username").asText(), json.get("sessionToken").asText(), json.get(
                "authenticated").asBoolean(), json.get("consented").asBoolean());
    }

    private static final void assertAllFieldsExist(JsonNode json) {
        if (json == null) {
            throw new IllegalArgumentException("JSON is null.");
        } else if (!json.has("username")) {
            throw new AssertionError("JSON does not have username field.");
        } else if (!json.has("sessionToken")) {
            throw new AssertionError("JSON does not have sessionToken field.");
        } else if (!json.has("authenticated")) {
            throw new AssertionError("JSON does not have authenticated field.");
        } else if (!json.has("consented")) {
            throw new AssertionError("JSON does not have consented field.");
        }
    }

}
