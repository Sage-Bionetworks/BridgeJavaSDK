package org.sagebionetworks.bridge.sdk.clients;

import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class AuthenticationClient extends BaseClient {

    private static final Pattern rfc2822 = Pattern.compile(
            "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
            );

    private static final String AUTH = "auth/";
    private static final String SIGN_UP = AUTH + "signUp";
    private static final String SIGN_IN = AUTH + "signIn";
    private static final String SIGN_OUT = AUTH + "signOut";
    private static final String REQUEST_RESET = AUTH + "requestResetPassword";
    
    private static String sessionToken;

    public static final void signUp(String email, String username, String password) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Email is not valid: " + email);
        } else if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username is not valid: " + username);
        } else if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password is not valid.");
        }
        
        ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put("email", email);
        json.put("username", username);
        json.put("password", password);
        
        HttpResponse response = post(SIGN_UP, json);
        sessionToken = getSessionToken(response);
    }

    public static final void signIn(String username, String password) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username is not valid: " + username);
        } else if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password is not valid.");
        }
        ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put("username", username);
        json.put("password", password);
        
        HttpResponse response = post(SIGN_IN, json);
        sessionToken = getSessionToken(response);
    }

    public static final void signOut() {
        Header header = new BasicHeader("Cookie", sessionToken);
        get(SIGN_OUT, header);
    }

    public static final void requestResetPassword(String email) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Email is not valid.");
        }
        ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put("email", email);
        post(REQUEST_RESET, json);
    }

    private static final boolean isValidEmail(String email) {
        if (rfc2822.matcher(email).matches()) {
            return true;
        } else {
            return false;
        }
    }
    
}