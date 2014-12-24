package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.sagebionetworks.bridge.sdk.models.users.ResetPasswordCredentials;
import org.sagebionetworks.bridge.sdk.models.users.SignInCredentials;
import org.sagebionetworks.bridge.sdk.models.users.SignUpCredentials;

/**
 * Main entry point for Bridge Java SDK.
 */
public class ClientProvider {

    private final @Nonnull Config config;
    private final @Nonnull ClientInfo info;
    private final BaseApiCaller baseApiCaller = new BaseApiCaller(null, this);

    /**
     * Default constructor. Constructs a ClientProvider with the default values.
     */
    public ClientProvider() {
        this(null, null);
    }

    /**
     * Constructs a ClientProvider with the specified config and client info. If the config or the client info are
     * specified as null, this constructor uses the default values instead.
     *
     * @param config
     *         client config, uses default configs if null
     * @param info
     *         client info, instantiates client info with default values if null
     */
    public ClientProvider(@Nullable Config config, @Nullable ClientInfo info) {
        if (config != null) {
            this.config = config;
        } else {
            this.config = new Config();
        }

        if (info != null) {
            this.info = info;
        } else {
            this.info = new ClientInfo().withOsName(System.getProperty("os.name"))
                    .withOsVersion(System.getProperty("os.version")).withSdkVersion(this.config.getSdkVersion());
        }
    }

    /**
     * Retrieve the Config object for the system.
     *
     * @return Config
     */
    @Nonnull
    public Config getConfig() {
        return config;
    }

    @Nonnull
    public ClientInfo getClientInfo() {
        return info;
    }

    /**
     * Sign In to Bridge with the given credentials. Returns a session object to manipulate. Method is idempotent.
     *
     * @param signIn
     *            The credentials you wish to sign in with.
     * @return Session
     */
    public Session signIn(SignInCredentials signIn) {
        checkNotNull(signIn, "SignInCredentials required.");

        UserSession session = baseApiCaller.post(config.getAuthSignInApi(), signIn, UserSession.class);
        return new BridgeSession(session, this);
    }

    /**
     * Sign Up an account with Bridge using the given credentials.
     *
     * @param signUp
     *            The credentials to create an account with.
     */
    public void signUp(SignUpCredentials signUp) {
        checkNotNull(signUp, "SignUpCredentials required.");
        checkArgument(isNotBlank(signUp.getEmail()), "Email cannot be blank/null");
        checkArgument(isNotBlank(signUp.getUsername()), "Username cannot be blank/null");
        checkArgument(isNotBlank(signUp.getPassword()), "Password cannot be blank/null");

        baseApiCaller.post(config.getAuthSignUpApi(), signUp);
    }

    /**
     * Request your password be reset. A link to change the password will be sent to the provided email.
     *
     * @param email
     *            Email associated with a Bridge account.
     */
    public void requestResetPassword(String email) {
        checkArgument(isNotBlank(email), "Email cannot be blank/null");

        baseApiCaller.post(config.getAuthRequestResetApi(), new ResetPasswordCredentials(email));
    }
}
