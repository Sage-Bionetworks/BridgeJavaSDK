package org.sagebionetworks.bridge.sdk.rest;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

import org.sagebionetworks.bridge.sdk.rest.api.AuthenticationApi;
import org.sagebionetworks.bridge.sdk.rest.model.SignIn;
import org.sagebionetworks.bridge.sdk.rest.model.UserSessionInfo;

/**
 * Retrieves session information for a Bridge user.
 */
class UserSessionInfoProvider {
    private static final Logger LOG = LoggerFactory.getLogger(UserSessionInfoProvider.class);

    private final Retrofit retrofit;
    private final AuthenticationApi authenticationApi;

    public UserSessionInfoProvider(Retrofit retrofit) {
        this.retrofit = retrofit;
        this.authenticationApi = retrofit.create(AuthenticationApi.class);
    }

    /**
     * Retrieves a session from a service.
     *
     * @param signIn
     *         sign in credentials for a user
     * @return session info for the user, or null if the return value was not a 200
     * @throws IOException
     */
    public UserSessionInfo retrieveSession(SignIn signIn) throws IOException {
        LOG.info("Retrieving session for study: " + signIn.getStudy() + ", email:" +
                signIn.getEmail());
        Response<UserSessionInfo> signInResponse = authenticationApi.signIn(signIn)
                .execute();

        if (signInResponse.isSuccessful()) {
            return signInResponse.body();
        } else if (signInResponse.code() == 412) {

            // Look up a converter for the UserSessionInfo type on the Retrofit instance.
            Converter<ResponseBody, UserSessionInfo> errorConverter =
                    retrofit.responseBodyConverter(UserSessionInfo.class, new Annotation[0]);
            return errorConverter.convert(signInResponse.errorBody());
        }

        LOG.warn("Login failed");
        return null;
    }
}
