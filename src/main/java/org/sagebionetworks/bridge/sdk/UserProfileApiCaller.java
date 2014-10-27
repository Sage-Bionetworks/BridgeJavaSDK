package org.sagebionetworks.bridge.sdk;

import org.apache.http.HttpResponse;
import org.sagebionetworks.bridge.sdk.models.UserProfile;

import com.fasterxml.jackson.core.JsonProcessingException;

class UserProfileApiCaller extends BaseApiCaller {

    UserProfileApiCaller(ClientProvider provider) {
        super(provider);
    }

    static UserProfileApiCaller valueOf(ClientProvider provider) {
        return new UserProfileApiCaller(provider);
    }

    UserProfile getProfile() {
        String url = provider.getConfig().getProfileApi();
        HttpResponse response = get(url);

        return getResponseBodyAsType(response, UserProfile.class);
    }

    void updateProfile(UserProfile profile) {
        String profileJson = null;
        try {
            profileJson = mapper.writeValueAsString(profile);
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException("Could not process the profile. Is it correct? " + profile.toString(), e);
        }
        String url = provider.getConfig().getProfileApi();
        post(url, profileJson);
    }

}
