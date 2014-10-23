package org.sagebionetworks.bridge.sdk;

import org.apache.http.HttpResponse;
import org.sagebionetworks.bridge.sdk.models.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.SignUpCredentials;
import org.sagebionetworks.bridge.sdk.models.UserSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;

final class AuthenticationApiCaller extends BaseApiCaller {

    private AuthenticationApiCaller(ClientProvider provider) {
        super(provider);
    };

    static AuthenticationApiCaller valueOf(ClientProvider provider) {
        return new AuthenticationApiCaller(provider);
    }

    String signUp(String email, String username, String password) {
        String signUp = null;
        try {
            SignUpCredentials cred = SignUpCredentials.valueOf().setEmail(email).setUsername(username).setPassword(password);
            signUp = mapper.writeValueAsString(cred);
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException(
                    "Could not process email, username, and password. Are they incorrect or malformed? "
                            + "email=" + email + ", username=" + username + ", password=" + password, e);
        }
        String url = provider.getConfig().getAuthSignUpApi();
        HttpResponse response = post(url, signUp);

        return getSessionToken(response, url);
    }

    UserSession signIn(String username, String password) {
        String signIn = null;
        try {
            SignInCredentials cred = SignInCredentials.valueOf().setUsername(username).setPassword(password);
            signIn = mapper.writeValueAsString(cred);
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException(
                    "Error occurred while processing SignInCredentials. Is there something wrong with the username and password fields? "
                            + "username=" + username + ", password=" + password, e);
        }
        String url = provider.getConfig().getAuthSignInApi();
        HttpResponse response = post(url, signIn);

        return getResponseBodyAsType(response, UserSession.class);
    }

    UserSession signOut(UserSession session) {
        String url = provider.getConfig().getAuthSignOutApi();
        get(url);
        return session.signOut();
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
        String url = provider.getConfig().getAuthRequestResetApi();
        post(url, emailJson);
    }
}