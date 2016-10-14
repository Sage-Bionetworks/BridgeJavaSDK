package org.sagebionetworks.bridge.sdk.rest;

import com.google.gson.Gson;

import org.sagebionetworks.bridge.sdk.rest.api.AuthenticationApi;
import org.sagebionetworks.bridge.sdk.rest.model.SignIn;
import org.sagebionetworks.bridge.sdk.rest.model.UserSessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import retrofit2.Response;

/**
 * Retrieves session information for a Bridge user.
 */
class UserSessionInfoProvider {
  private static final Logger LOG = LoggerFactory.getLogger(UserSessionInfoProvider.class);

  private final AuthenticationApi authenticationApi;

  public UserSessionInfoProvider(AuthenticationApi authenticationApi) {
    this.authenticationApi = authenticationApi;
  }

  /**
   * Retrieves a session from a service.
   * @param signIn
   *     sign in credentials for a user
   * @return session info for the user, or null if the return value was not a 200
   * @throws IOException
   */
  public UserSessionInfo retrieveSession(SignIn signIn) throws IOException {
    LOG.info("Retrieving session for study: " + signIn.getStudy() + ", email:" +
             signIn.getEmail());
    Response<UserSessionInfo> signInResponse = authenticationApi.v3AuthSignInPost(signIn).execute();

    if (signInResponse.isSuccessful()) {
      return signInResponse.body();
    } else if (signInResponse.code() == 412) {
      return ApiClientProvider.GSON.fromJson(signInResponse.errorBody().string(), UserSessionInfo
              .class);
    }

    LOG.warn("Login failed");

    return null;
  }
}
