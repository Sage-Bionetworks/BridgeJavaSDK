package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.sagebionetworks.bridge.sdk.models.users.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.users.SignUpCredentials;

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
        checkArgument(isNotBlank(signUp.getEmail()), "Email cannot be blank/null");
        checkArgument(isNotBlank(signUp.getUsername()), "Username cannot be blank/null");
        checkArgument(isNotBlank(signUp.getPassword()), "Password cannot be blank/null");

        AuthenticationApiCaller authApi = AuthenticationApiCaller.valueOf();
        authApi.signUp(signUp);
    }
    public static void requestResetPassword(String email) {
        checkArgument(isNotBlank(email), "Email cannot be blank/null");

        AuthenticationApiCaller authApi = AuthenticationApiCaller.valueOf();
        authApi.requestResetPassword(email);
    }
}