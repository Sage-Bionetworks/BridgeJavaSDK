package org.sagebionetworks.bridge.sdk;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.client.fluent.Response;
import org.apache.http.message.BasicHeader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

final class AuthenticationApiCaller extends BaseApiCaller {

    private static ObjectMapper mapper = new ObjectMapper().configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final String AUTH = "auth/";
    private static final String SIGN_UP = AUTH + "signUp";
    private static final String SIGN_IN = AUTH + "signIn";
    private static final String SIGN_OUT = AUTH + "signOut";
    private static final String REQUEST_RESET = AUTH + "requestResetPassword";

    private AuthenticationApiCaller() {};

    static AuthenticationApiCaller valueOf() {
        return new AuthenticationApiCaller();
    }

    static AuthenticationApiCaller valueOf(String hostname) {
        AuthenticationApiCaller authClient = new AuthenticationApiCaller();
        authClient.setHost(hostname);
        return authClient;
    }

    String signUp(String email, String username, String password) {
        assert email != null && username != null && password != null;

        String signUp = null;
        try {
            signUp = mapper.writeValueAsString(SignUpCredentials.valueOf(email, username, password));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Response response = post(SIGN_UP, signUp);
        return getSessionToken(response);
    }

    UserSession signIn(String username, String password) {
        assert username != null && password != null;

        String signIn = null;
        try {
            signIn = mapper.writeValueAsString(SignInCredentials.valueOf(username, password));
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
        }
        Response response = post(SIGN_IN, signIn);
        String sessionJsonString = null;
        try {
            sessionJsonString = response.returnContent().asString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return UserSession.valueOf(sessionJsonString);
    }

    UserSession signOut(UserSession session) {
        assert session != null;
        assert session.getSessionToken() != null;

        Header header = new BasicHeader("Bridge-Session", session.getSessionToken());
        get(SIGN_OUT, header);
        return session.signOut();
    }

    void requestResetPassword(String email) {
        assert email != null;

        ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put("email", email);
        try {
            email = mapper.writeValueAsString(json);
            post(REQUEST_RESET, email); // will error if email is invalid.
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}