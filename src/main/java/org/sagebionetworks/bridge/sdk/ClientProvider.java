package org.sagebionetworks.bridge.sdk;

import static org.sagebionetworks.bridge.sdk.Preconditions.checkNotEmpty;
import static org.sagebionetworks.bridge.sdk.Preconditions.checkNotNull;

import org.sagebionetworks.bridge.sdk.models.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.SignUpCredentials;

public class ClientProvider {

    private static Config config; 
    
    public static synchronized Config getConfig() {
        if (config == null) {
            config = Config.valueOf();
        }
        return config;
    }
    
    public static Session signIn(SignInCredentials signIn) {
        checkNotNull(signIn, "SignInCredentials required.");
        
        AuthenticationApiCaller authApi = AuthenticationApiCaller.valueOf();
        UserSession session = authApi.signIn(signIn.getUsername(), signIn.getPassword());
        return BridgeSession.valueOf(session);
    }
    public static void signUp(SignUpCredentials signUp) {
        checkNotNull(signUp, "SignUpCredentials required.");
        checkNotEmpty(signUp.getEmail(), "Email cannot be blank/null");
        checkNotEmpty(signUp.getUsername(), "Username cannot be blank/null");
        checkNotEmpty(signUp.getPassword(), "Password cannot be blank/null");

        AuthenticationApiCaller authApi = AuthenticationApiCaller.valueOf();
        authApi.signUp(signUp);
    }
    public static void requestResetPassword(String email) {
        checkNotEmpty(email, "Email cannot be blank/null");

        AuthenticationApiCaller authApi = AuthenticationApiCaller.valueOf();
        authApi.requestResetPassword(email);
    }
}