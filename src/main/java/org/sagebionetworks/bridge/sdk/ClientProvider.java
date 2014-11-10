package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.sagebionetworks.bridge.sdk.models.users.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.users.SignUpCredentials;

public class ClientProvider {

    private static Config config;

    /**
     * Retrieve the Config object for the system.
     *
     * @return Config
     */
    public static synchronized Config getConfig() {
        if (config == null) {
            config = Config.valueOf();
        }
        return config;
    }

    /**
     * Sign In to Bridge with the given credentials. Returns a session object to manipulate. Method is idempotent.
     *
     * @param signIn
     *            The credentials you wish to sign in with.
     * @return Session
     */
    public static Session signIn(SignInCredentials signIn) {
        checkNotNull(signIn, "SignInCredentials required.");

        AuthenticationApiCaller authApi = AuthenticationApiCaller.valueOf();
        UserSession session = authApi.signIn(signIn.getUsername(), signIn.getPassword());
        return BridgeSession.valueOf(session);
    }

    /**
     * Sign Up an account with Bridge using the given credentials.
     *
     * @param signUp
     *            The credentials to create an account with.
     */
    public static void signUp(SignUpCredentials signUp) {
        checkNotNull(signUp, "SignUpCredentials required.");
        checkArgument(isNotBlank(signUp.getEmail()), "Email cannot be blank/null");
        checkArgument(isNotBlank(signUp.getUsername()), "Username cannot be blank/null");
        checkArgument(isNotBlank(signUp.getPassword()), "Password cannot be blank/null");

        AuthenticationApiCaller authApi = AuthenticationApiCaller.valueOf();
        authApi.signUp(signUp);
    }

    /**
     * Request your password be reset. A link to change the password will be sent to the provided email.
     *
     * @param email
     *            Email associated with a Bridge account.
     */
    public static void requestResetPassword(String email) {
        checkArgument(isNotBlank(email), "Email cannot be blank/null");

        AuthenticationApiCaller authApi = AuthenticationApiCaller.valueOf();
        authApi.requestResetPassword(email);
    }
}
