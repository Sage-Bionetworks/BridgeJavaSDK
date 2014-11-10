package org.sagebionetworks.bridge.sdk;

import org.apache.http.HttpResponse;
import org.sagebionetworks.bridge.sdk.models.users.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.users.SignUpCredentials;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;

final class AuthenticationApiCaller extends BaseApiCaller {

    private AuthenticationApiCaller(Session session) {
        super(session);
    };

    static AuthenticationApiCaller valueOf(Session session) {
        return new AuthenticationApiCaller(session);
    }
    
    static AuthenticationApiCaller valueOf() {
        return new AuthenticationApiCaller(null);
    }

    void signUp(SignUpCredentials signUp) {
        try {
            String url = config.getAuthSignUpApi();
            post(url, mapper.writeValueAsString(signUp));
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException(e.getMessage(), e);
        }
    }

    UserSession signIn(String username, String password) {
        String signIn = null;
        try {
            SignInCredentials cred = new SignInCredentials(username, password);
            signIn = mapper.writeValueAsString(cred);
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException(
                    "Error occurred while processing SignInCredentials. Is there something wrong with the username and password fields? "
                            + "username=" + username + ", password=" + password, e);
        }
        String url = config.getAuthSignInApi();
        HttpResponse response = post(url, signIn);

        return getResponseBodyAsType(response, UserSession.class);
    }

    void signOut() {
        String url = config.getAuthSignOutApi();
        get(url);
    }

    void requestResetPassword(String email) {
        ObjectNode json = mapper.createObjectNode();
        json.put("email", email);

        String emailJson;
        try {
            emailJson = mapper.writeValueAsString(json);
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException("Error occurred while processing email: email=" + email, e);
        }
        String url = config.getAuthRequestResetApi();
        post(url, emailJson);
    }
}