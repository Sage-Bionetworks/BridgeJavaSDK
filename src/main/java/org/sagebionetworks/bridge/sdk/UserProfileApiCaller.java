package org.sagebionetworks.bridge.sdk;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.sagebionetworks.bridge.sdk.models.UserProfile;

import com.fasterxml.jackson.core.JsonProcessingException;

class UserProfileApiCaller extends BaseApiCaller {

    private final String PROFILE = provider.getConfig().getProfileApi();

    UserProfileApiCaller(ClientProvider provider) {
        super(provider);
    }

    static UserProfileApiCaller valueOf(ClientProvider provider) {
        return new UserProfileApiCaller(provider);
    }

    UserProfile getProfile() {
        assert provider.isSignedIn();

        HttpResponse response = get(PROFILE);
        String responseBody = getResponseBody(response);

        UserProfile profile;
        try {
            profile = mapper.readValue(responseBody, UserProfile.class);
        } catch (IOException e) {
            throw new BridgeSDKException(
                    "Something went wrong while converting Response Body into UserProfile: responseBody="
                            + responseBody, e);
        }
        return profile;
    }

    void updateProfile(UserProfile profile) {
        assert provider.isSignedIn();
        assert provider.getSession().getUsername().equals(profile.getUsername());

        String profileJson = null;
        try {
            profileJson = mapper.writeValueAsString(profile);
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException("Could not process the profile. Is it correct? " + profile.toString(), e);
        }
        post(PROFILE, profileJson);
    }

}
