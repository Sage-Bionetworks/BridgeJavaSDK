package org.sagebionetworks.bridge.sdk;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.fluent.Response;
import org.apache.http.util.EntityUtils;
import org.sagebionetworks.bridge.sdk.models.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.SignUpCredentials;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
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
            throw new BridgeSDKException("Could not process the following JSON: " + signUp, e);
        }
        Response response = post(SIGN_UP, signUp);
        return getSessionToken(response, SIGN_UP);
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
        StatusLine statusLine = null;
        try {
            HttpResponse hr = response.returnResponse();
            statusLine = hr.getStatusLine();
            sessionJsonString = EntityUtils.toString(hr.getEntity());
        } catch (IOException e) {
            throw new BridgeServerException(e, statusLine, getFullUrl(SIGN_IN));
        }
        return UserSession.valueOf(sessionJsonString);
    }

    UserSession signOut(UserSession session) {
        assert session != null;
        assert session.getSessionToken() != null;

        Response response = authorizedGet(SIGN_OUT);
        StatusLine statusLine = null;
        try {
            statusLine = response.returnResponse().getStatusLine();
        } catch (IOException e) {
            throw new BridgeServerException(e, statusLine, getFullUrl(SIGN_OUT));
        }
        return session.signOut();
    }

    void requestResetPassword(String email) {
        assert email != null;

        ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put("email", email);
        StatusLine statusLine = null;
        try {
            email = mapper.writeValueAsString(json);
            statusLine = post(REQUEST_RESET, email).returnResponse().getStatusLine();
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException("Could not process the following JSON: " + email, e);
        } catch (IOException e) {
            throw new BridgeServerException(e, statusLine, getFullUrl(REQUEST_RESET));
        }
    }

}