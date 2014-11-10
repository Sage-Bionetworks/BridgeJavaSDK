package org.sagebionetworks.bridge.sdk;

import org.apache.http.HttpResponse;
import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.studies.StudyConsent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

class StudyConsentApiCaller extends BaseApiCaller {

    private StudyConsentApiCaller(Session session) {
        super(session);
    }

    static StudyConsentApiCaller valueOf(Session session) {
        return new StudyConsentApiCaller(session);
    }

    ResourceListImpl<StudyConsent> getAllStudyConsents() {
        String url = config.getStudyConsentsApi();
        HttpResponse response = get(url);

        JsonNode node = getJsonNode(response);
        return mapper.convertValue(node, new TypeReference<ResourceListImpl<StudyConsent>>() {});
    }

    StudyConsent getStudyConsent(DateTime timestamp) {
        String url = config.getStudyConsentApi(timestamp);
        HttpResponse response = get(url);

        return getResponseBodyAsType(response, StudyConsent.class);
    }

    StudyConsent getActiveStudyConsent() {
        String url = config.getActiveStudyConsentApi();
        HttpResponse response = get(url);
        return getResponseBodyAsType(response, StudyConsent.class);
    }

    void setActiveStudyConsent(DateTime timestamp) {
        String url = config.getVersionStudyConsentApi(timestamp);
        post(url);
    }

    StudyConsent createStudyConsent(StudyConsent studyConsent) {
        String json = null;
        try {
            json = mapper.writeValueAsString(studyConsent);
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException("Could not process StudyConsent. Are you sure it is correct? "
                    + studyConsent.toString(), e);
        }
        String url = config.getStudyConsentsApi();
        HttpResponse response = post(url, json);

        return getResponseBodyAsType(response, StudyConsent.class);
    }
}
