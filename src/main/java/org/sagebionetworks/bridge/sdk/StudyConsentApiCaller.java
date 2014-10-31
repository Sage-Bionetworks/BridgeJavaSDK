package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.apache.http.HttpResponse;
import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.ConsentDocument;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

class StudyConsentApiCaller extends BaseApiCaller {

    private StudyConsentApiCaller(Session session) {
        super(session);
    }

    static StudyConsentApiCaller valueOf(Session session) {
        return new StudyConsentApiCaller(session);
    }

    List<ConsentDocument> getAllStudyConsents() {
        String url = config.getStudyConsentsApi();
        HttpResponse response = get(url);

        JsonNode items = getPropertyFromResponse(response, "items");
        List<ConsentDocument> consents = mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, ConsentDocument.class));

        return consents;
    }

    ConsentDocument getStudyConsent(DateTime timestamp) {
        String url = config.getStudyConsentApi(timestamp);
        HttpResponse response = get(url);

        return getResponseBodyAsType(response, ConsentDocument.class);
    }

    ConsentDocument getActiveStudyConsent() {
        String url = config.getActiveStudyConsentApi();
        HttpResponse response = get(url);
        return getResponseBodyAsType(response, ConsentDocument.class);
    }

    void setActiveStudyConsent(DateTime timestamp) {
        String url = config.getVersionStudyConsentApi(timestamp);
        post(url);
    }

    ConsentDocument createConsentDocument(ConsentDocument studyConsent) {
        String json = null;
        try {
            json = mapper.writeValueAsString(studyConsent);
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException("Could not process StudyConsent. Are you sure it is correct? "
                    + studyConsent.toString(), e);
        }
        String url = config.getStudyConsentsApi();
        HttpResponse response = post(url, json);

        return getResponseBodyAsType(response, ConsentDocument.class);
    }
}
