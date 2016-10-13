package org.sagebionetworks.bridge.sdk.rest;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.sagebionetworks.bridge.sdk.rest.api.AuthenticationApi;
import org.sagebionetworks.bridge.sdk.rest.model.SignIn;
import org.sagebionetworks.bridge.sdk.rest.model.UserSessionInfo;

import retrofit2.Response;

/**
 * Created by liujoshua on 10/11/16.
 */
class UserSessionInfoProvider {
    private static final Logger LOG = LoggerFactory.getLogger(UserSessionInfoProvider.class);
    private final AuthenticationApi authenticationApi;

    public UserSessionInfoProvider(AuthenticationApi authenticationApi) {
        this.authenticationApi = authenticationApi;
    }

    public UserSessionInfo retrieveSession(SignIn signIn) throws IOException {
        LOG.info("Retrieving session for study: " +signIn.getStudy() + ", email:" + signIn.getEmail());
        Response<UserSessionInfo> signInResponse = authenticationApi.v3AuthSignInPost(signIn).execute();

        if (signInResponse.isSuccessful()) {
            return signInResponse.body();
        }

        LOG.warn("Login failed");

        return null;
    }
}
