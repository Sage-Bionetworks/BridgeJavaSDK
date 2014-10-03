package org.sagebionetworks.bridge.sdk;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.fluent.Response;
import org.apache.http.util.EntityUtils;
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

        Response response = authorizedGet(PROFILE);
        StatusLine statusLine = null;
        String profileJson = null;
        try {
            HttpResponse hr = response.returnResponse();
            statusLine = hr.getStatusLine();
            profileJson = EntityUtils.toString(hr.getEntity());
        } catch (IOException e) {
            throw new BridgeServerException(e, statusLine, getFullUrl(PROFILE));
        }

        return UserProfile.valueOf(profileJson);
    }

    void updateProfile(UserProfile profile) {
        assert provider.isSignedIn();
        assert provider.getSession().getUsername().equals(profile.getUsername());

        String profileJson = null;
        try {
            profileJson = mapper.writeValueAsString(profile);
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException("Could not process the following JSON: " + profileJson, e);
        }
        Response response = post(PROFILE, profileJson);
        StatusLine statusLine = null;
        try {
            HttpResponse hr = response.returnResponse();
            statusLine = hr.getStatusLine();
        } catch (IOException e) {
            throw new BridgeServerException(e, statusLine, getFullUrl(PROFILE));
        }
    }

}
