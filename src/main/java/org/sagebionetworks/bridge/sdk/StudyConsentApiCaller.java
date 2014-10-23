package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.apache.http.HttpResponse;
import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.StudyConsent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

class StudyConsentApiCaller extends BaseApiCaller {

    private StudyConsentApiCaller(ClientProvider provider) {
        super(provider);
    }

    static StudyConsentApiCaller valueOf(ClientProvider provider) {
        return new StudyConsentApiCaller(provider);
    }

    List<StudyConsent> getAllStudyConsents() {
        String url = provider.getConfig().getStudyConsentApi();
        HttpResponse response = get(url);

        JsonNode items = getPropertyFromResponse(response, "items");
        List<StudyConsent> consents = mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, StudyConsent.class));

        return consents;
    }

    StudyConsent getStudyConsent(DateTime timestamp) {
        String url = provider.getConfig().getStudyConsentTimestamp(timestamp);
        HttpResponse response = get(url);

        return getResponseBodyAsType(response, StudyConsent.class);
    }

    StudyConsent getActiveStudyConsent() {
        String url = provider.getConfig().getStudyConsentActive();
        HttpResponse response = get(url);
        return getResponseBodyAsType(response, StudyConsent.class);
    }

    void setActiveStudyConsent(DateTime timestamp) {
        String url = provider.getConfig().getStudyConsentActiveTimestamp(timestamp);
        post(url);
    }

    StudyConsent addStudyConsent(StudyConsent studyConsent) {
        assert provider.isSignedIn();

        String json = null;
        try {
            json = mapper.writeValueAsString(studyConsent);
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException("Could not process StudyConsent. Are you sure it is correct? "
                    + studyConsent.toString(), e);
        }
        String url = provider.getConfig().getStudyConsentApi();
        HttpResponse response = post(url, json);

        return getResponseBodyAsType(response, StudyConsent.class);
    }
}
