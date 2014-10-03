package org.sagebionetworks.bridge.sdk;

import java.io.IOException;

import org.apache.http.client.fluent.Response;
import org.sagebionetworks.bridge.sdk.models.UserProfile;

import com.fasterxml.jackson.core.JsonProcessingException;

class UserProfileApiCaller extends BaseApiCaller {

    private final Utilities utils = Utilities.getInstance();

    private final String PROFILE = "/api/v1/profile";

    UserProfileApiCaller(ClientProvider provider) {
        super(provider);
    }

    static UserProfileApiCaller valueOf(ClientProvider provider) {
        return new UserProfileApiCaller(provider);
    }

    UserProfile getProfile() {
        assert provider.isSignedIn();

        Response response = authorizedGet(PROFILE);
        String profileJson = null;
        try {
            profileJson = response.returnContent().asString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return UserProfile.valueOf(profileJson);
    }

    void updateProfile(UserProfile profile) {
        assert provider.isSignedIn();
        assert provider.getSession().getUsername().equals(profile.getUsername());
        assert profile.getFirstName() != null;
        assert profile.getLastName() != null;
        assert profile.getUsername() != null;
        assert utils.isValidEmail(profile.getEmail());

        String profileJson = null;
        try {
            profileJson = mapper.writeValueAsString(profile);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        post(PROFILE, profileJson);
    }

}
