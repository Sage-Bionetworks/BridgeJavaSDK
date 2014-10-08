package org.sagebionetworks.bridge.sdk;

import org.apache.http.HttpResponse;
import org.sagebionetworks.bridge.sdk.models.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.SignUpCredentials;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;

final class AuthenticationApiCaller extends BaseApiCaller {

    private final String AUTH = provider.getConfig().getAuthApi();
    private final String SIGN_UP = AUTH + "/signUp";
    private final String SIGN_IN = AUTH + "/signIn";
    private final String SIGN_OUT = AUTH + "/signOut";
    private final String REQUEST_RESET = AUTH + "/requestResetPassword";

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
            throw new BridgeSDKException(
                    "Could not process email, username, and password. Are they incorrect or malformed? "
                            + "email=" + email + ", username=" + username + ", password=" + password, e);
        }
        HttpResponse response = post(getFullUrl(SIGN_UP), signUp);
        return getSessionToken(response, SIGN_UP);
    }

    UserSession signIn(String username, String password) {
        assert username != null && password != null;

        String signIn = null;
        try {
            signIn = mapper.writeValueAsString(SignInCredentials.valueOf(username, password));
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException(
                    "Error occurred while processing SignInCredentials. Is there something wrong with the username and password fields? "
                            + "username=" + username + ", password=" + password, e);
        }
        HttpResponse response = post(getFullUrl(SIGN_IN), signIn);
        String sessionJsonString = getResponseBody(response);

        return UserSession.valueOf(sessionJsonString);
    }

    UserSession signOut(UserSession session) {
        assert session != null;
        assert session.getSessionToken() != null;

        authorizedGet(getFullUrl(SIGN_OUT));
        return session.signOut();
    }

    void requestResetPassword(String email) {
        assert email != null;

        ObjectNode json = mapper.createObjectNode();
        json.put("email", email);
        try {
            String emailJson = mapper.writeValueAsString(json);
            post(getFullUrl(REQUEST_RESET), emailJson);
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException("Error occurred while processing email: email=" + email, e);
        }
    }

}