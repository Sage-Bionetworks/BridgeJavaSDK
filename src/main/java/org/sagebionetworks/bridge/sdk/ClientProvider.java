package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkNotNull;

import org.sagebionetworks.bridge.sdk.exceptions.ConsentRequiredException;
import org.sagebionetworks.bridge.sdk.models.users.EmailCredentials;
import org.sagebionetworks.bridge.sdk.models.users.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.users.SignUpCredentials;

public class ClientProvider {

    private static final Config config = new Config();
    
    private static ClientInfo info = new ClientInfo.Builder().build();
    
    /**
     * Retrieve the Config object for the system.
     *
     * @return Config
     */
    public static Config getConfig() {
        return config;
    }
    
    public static ClientInfo getClientInfo() {
        return info;
    }
    
    public static synchronized void setClientInfo(ClientInfo clientInfo) {
        info = checkNotNull(clientInfo); 
    }

    /**
     * Sign In to Bridge with the given credentials. Returns a session object to manipulate. Method is idempotent.
     *
     * @param signIn
     *            The credentials you wish to sign in with.
     * @return Session
     */
    public static Session signIn(SignInCredentials signIn) throws ConsentRequiredException {
        checkNotNull(signIn, "SignInCredentials required.");

        UserSession session = new BaseApiCaller(null).post(config.getSignInApi(), signIn, UserSession.class);
        return new BridgeSession(session);
    }

    /**
     * Sign Up an account with Bridge using the given credentials.
     *
     * @param signUp
     *            The credentials to create an account with.
     */
    public static void signUp(SignUpCredentials signUp) {
        checkNotNull(signUp, "SignUpCredentials required.");

        new BaseApiCaller(null).post(config.getSignUpApi(), signUp);
    }
    
    /**
     * Resend an email verification request to the supplied email address.
     * 
     * @param email
     *      Email credentials associated with a Bridge account.
     */
    public static void resendEmailVerification(EmailCredentials email) {
        checkNotNull(email, "EmailCredentials required");
        
        new BaseApiCaller(null).post(config.getResendEmailVerificationApi(), email);
    }

    /**
     * Request your password be reset. A link to change the password will be sent to the provided email.
     *
     * @param email
     *            Email credentials associated with a Bridge account.
     */
    public static void requestResetPassword(EmailCredentials email) {
        checkNotNull(email, "EmailCredentials required");

        new BaseApiCaller(null).post(config.getRequestResetPasswordApi(), email);
    }
}
