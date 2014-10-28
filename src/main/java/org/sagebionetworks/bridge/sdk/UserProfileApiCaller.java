package org.sagebionetworks.bridge.sdk;

import org.apache.http.HttpResponse;
import org.sagebionetworks.bridge.sdk.models.UserProfile;

import com.fasterxml.jackson.core.JsonProcessingException;

class UserProfileApiCaller extends BaseApiCaller {

    UserProfileApiCaller(Session session) {
        super(session);
    }

    static UserProfileApiCaller valueOf(Session session) {
        return new UserProfileApiCaller(session);
    }

    UserProfile getProfile() {
        String url = config.getProfileApi();
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
        String url = config.getProfileApi();
        post(url, profileJson);
    }

}
