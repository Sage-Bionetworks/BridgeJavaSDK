package org.sagebionetworks.bridge.sdk.user;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.client.fluent.Response;
import org.apache.http.message.BasicHeader;
import org.sagebionetworks.bridge.sdk.util.BaseClient;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

final class AuthenticationClient extends BaseClient {

    private static final String AUTH = "auth/";
    private static final String SIGN_UP = AUTH + "signUp";
    private static final String SIGN_IN = AUTH + "signIn";
    private static final String SIGN_OUT = AUTH + "signOut";
    private static final String REQUEST_RESET = AUTH + "requestResetPassword";

    public static String signUp(String email, String username, String password) {
        ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put("email", email);
        json.put("username", username);
        json.put("password", password);

        Response response = post(SIGN_UP, json);
        return getSessionToken(response);
    }

    public static UserSession signIn(String username, String password) {
        ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put("username", username);
        json.put("password", password);

        Response response = post(SIGN_IN, json);
        UserSession session = null;
        try {
            session = UserSession.valueOf(response.returnContent().asString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return session;
    }

    public static UserSession signOut(UserSession session) {
        Header header = new BasicHeader("Bridge-Session", session.getSessionToken());
        get(SIGN_OUT, header);
        return UserSession.signOut(session);
    }

    public static void requestResetPassword(String email) {
        ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put("email", email);
        post(REQUEST_RESET, json); // will error if email is invalid.
    }

}