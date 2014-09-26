package org.sagebionetworks.bridge.sdk;

import java.io.IOException;

import org.apache.http.client.fluent.Response;
import org.sagebionetworks.bridge.sdk.models.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.SignUpCredentials;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

final class AuthenticationApiCaller extends BaseApiCaller {

    private ObjectMapper mapper = new ObjectMapper().configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final String AUTH = "/api/v1/auth/";
    private final String SIGN_UP = AUTH + "signUp";
    private final String SIGN_IN = AUTH + "signIn";
    private final String SIGN_OUT = AUTH + "signOut";
    private final String REQUEST_RESET = AUTH + "requestResetPassword";

    private AuthenticationApiCaller(ClientProvider provider) {
        super(provider);
    };

    static AuthenticationApiCaller valueOf(ClientProvider provider) {
        return new AuthenticationApiCaller(provider);
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

        authorizedGet(SIGN_OUT);
        return UserSession.valueOfRemoveAuth(session);
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