package org.sagebionetworks.bridge.sdk;

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

        String url = STUDY_CONSENT;
        HttpResponse response = get(url);

        JsonNode items = getPropertyFromResponse(response, "items");
        List<StudyConsent> consents = mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, StudyConsent.class));

        return consents;
    }

    StudyConsent getStudyConsent(DateTime timestamp) {
        assert provider.isSignedIn();
        assert timestamp != null;

        String url = STUDY_CONSENT + timestamp(timestamp);
        HttpResponse response = get(url);

        return getResponseBodyAsType(response, StudyConsent.class);
    }

    StudyConsent getActiveStudyConsent() {
        assert provider.isSignedIn();

        HttpResponse response = get(ACTIVE);
        return getResponseBodyAsType(response, StudyConsent.class);
    }

    void setActiveStudyConsent(DateTime timestamp) {
        assert provider.isSignedIn();

        post(ACTIVE + timestamp(timestamp));
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
        HttpResponse response = post(STUDY_CONSENT, json);
        return getResponseBodyAsType(response, StudyConsent.class);
    }

    private String timestamp(DateTime date) {
        return "/" + date.toString(ISODateTimeFormat.dateTime());
    }
}
