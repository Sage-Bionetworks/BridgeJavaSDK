package org.sagebionetworks.bridge.sdk;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.sagebionetworks.bridge.sdk.models.StudyConsent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

class StudyConsentApiCaller extends BaseApiCaller {

    private final String STUDY_CONSENT = provider.getConfig().getStudyConsentApi();
    private final String ACTIVE = STUDY_CONSENT + "/active";

    private StudyConsentApiCaller(ClientProvider provider) {
        super(provider);
    }

    static StudyConsentApiCaller valueOf(ClientProvider provider) {
        return new StudyConsentApiCaller(provider);
    }

    List<StudyConsent> getAllStudyConsents() {
        assert provider.isSignedIn();

        String url = getFullUrl(STUDY_CONSENT);
        HttpResponse response = authorizedGet(url);

        JsonNode items = getPropertyFromResponse(response, "items");
        List<StudyConsent> consents = mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, StudyConsent.class));

        return consents;
    }

    StudyConsent getStudyConsent(DateTime timestamp) {
        assert provider.isSignedIn();
        assert timestamp != null;

        String url = getFullUrl(STUDY_CONSENT) + timestamp(timestamp);
        HttpResponse response = authorizedGet(url);
        String responseBody = getResponseBody(response);

        StudyConsent consent;
        try {
            consent = mapper.readValue(responseBody, StudyConsent.class);
        } catch (IOException e) {
            throw new BridgeSDKException(
                    "Something went wrong while converting Response Body JSON into StudyConsent: responseBody="
                            + responseBody, e);
        }
        return consent;
    }

    StudyConsent getActiveStudyConsent() {
        assert provider.isSignedIn();

        String url = getFullUrl(ACTIVE);
        HttpResponse response = authorizedGet(url);
        String responseBody = getResponseBody(response);

        StudyConsent consent;
        try {
            consent = mapper.readValue(responseBody, StudyConsent.class);
        } catch (IOException e) {
            throw new BridgeSDKException(
                    "Something went wrong while converting ResponseBody JSON into StudyConsent: responseBody="
                            + responseBody, e);
        }
        return consent;
    }

    StudyConsent setActiveStudyConsent(DateTime timestamp) {
        assert provider.isSignedIn();

        String url = getFullUrl(ACTIVE) + timestamp(timestamp);
        HttpResponse response = post(url);
        String responseBody = getResponseBody(response);

        StudyConsent consent;
        try {
            consent = mapper.readValue(responseBody, StudyConsent.class);
        } catch (IOException e) {
            throw new BridgeSDKException(
                    "Something when wrong while converting Response Body JSON into StudyConsent: responseBody="
                            + responseBody, e);
        }
        return consent;
    }

    StudyConsent addStudyConsent(StudyConsent studyConsent) {
        assert provider.isSignedIn();

        String url = getFullUrl(STUDY_CONSENT);
        String json = null;
        try {
            json = mapper.writeValueAsString(studyConsent);
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException("Could not process StudyConsent. Are you sure it is correct? "
                    + studyConsent.toString(), e);
        }


        return null;
    }

    private String timestamp(DateTime date) {
        return "/" + date.toString(ISODateTimeFormat.dateTime());
    }
}
