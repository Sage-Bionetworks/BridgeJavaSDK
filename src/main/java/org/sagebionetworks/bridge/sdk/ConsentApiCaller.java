package org.sagebionetworks.bridge.sdk;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.sagebionetworks.bridge.sdk.models.ResearchConsent;
import org.sagebionetworks.bridge.sdk.models.UserSession;

import com.fasterxml.jackson.core.JsonProcessingException;

public class ConsentApiCaller extends BaseApiCaller {

    private final String CONSENT = provider.getConfig().getConsentApi();
    private final String SUSPEND = CONSENT + "/dataSharing/suspend";
    private final String RESUME = CONSENT + "/dataSharing/resume";

    private ConsentApiCaller(ClientProvider provider) {
        super(provider);
    }

    static ConsentApiCaller valueOf(ClientProvider provider) {
        return new ConsentApiCaller(provider);
    }

    UserSession consentToResearch(ResearchConsent consent) {
        if (consent == null) {
            throw new IllegalArgumentException("Consent cannot be null.");
        }

        String researchConsent;
        try {
            researchConsent = mapper.writeValueAsString(consent);
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException("Could not process researchConsent. Is it incorrect or malformed? " + consent.toString(), e);
        }

        HttpResponse response = post(getFullUrl(CONSENT), researchConsent);
        String sessionJson = getResponseBody(response);

        UserSession session;
        try {
            session = mapper.readValue(sessionJson, UserSession.class);
        } catch (IOException e) {
            throw new BridgeSDKException(
                    "Something went wrong while converting Response Body into UserProfile. responseBody=" + sessionJson,
                    e);
        }
        return session;
    }

    UserSession suspendDataSharing() {
        HttpResponse response = post(getFullUrl(SUSPEND));
        String sessionJson = getResponseBody(response);

        UserSession session;
        try {
            session = mapper.readValue(sessionJson, UserSession.class);
        }  catch (IOException e) {
            throw new BridgeSDKException("Could not read response body. responseBody=" + sessionJson, e);
        }
        return session;
    }

    UserSession resumeDataSharing() {
        HttpResponse response = post(getFullUrl(RESUME));
        String sessionJson = getResponseBody(response);

        UserSession session;
        try {
            session = mapper.readValue(sessionJson, UserSession.class);
        }  catch (IOException e) {
            throw new BridgeSDKException("Could not read response body. responseBody=" + sessionJson, e);
        }
        return session;
    }

}
