package org.sagebionetworks.bridge.sdk;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.GuidVersionedOnHolder;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

import com.fasterxml.jackson.databind.JsonNode;

class SurveyApiCaller extends BaseApiCaller {
    
    private SurveyApiCaller(ClientProvider provider) {
        super(provider);
    }

    static SurveyApiCaller valueOf(ClientProvider provider) {
        return new SurveyApiCaller(provider);
    }
    
    Survey getSurvey(String guid, DateTime timestamp) {
        Survey survey = null;
        try {
            String url = provider.getConfig().getSurveyApi(guid, timestamp);
            HttpResponse response = authorizedGet(url);

            StatusLine status = response.getStatusLine();
            HttpEntity entity = response.getEntity();

            // There should be error handling before this, probably in the post method
            if (status.getStatusCode() != 200) {
                throw new RuntimeException(status.getStatusCode() + ": " + status.getReasonPhrase());
            }
            survey = mapper.readValue(entity.getContent(), Survey.class);
        } catch (IOException e) {
            throw new BridgeSDKException(e.getMessage(), e);
        }
        return survey;
    }

    GuidVersionedOnHolder createSurvey(Survey survey) {
        GuidVersionedOnHolder holder = null;
        try {
            String json = mapper.writeValueAsString(survey);
            HttpResponse response = post(provider.getConfig().getSurveysApi(), json);

            StatusLine status = response.getStatusLine();
            HttpEntity entity = response.getEntity();

            // There should be error handling before this, probably in the post method
            if (status.getStatusCode() != 200) {
                throw new RuntimeException(status.getStatusCode() + ": " + status.getReasonPhrase());
            }
            JsonNode node = mapper.readTree(entity.getContent());
            String guid = node.get("guid").asText();
            String versionedOn = node.get("versionedOn").asText();
            holder = new GuidVersionedOnHolder(guid, DateTime.parse(versionedOn));
        } catch (IOException e) {
            throw new BridgeSDKException(e.getMessage(), e);
        }
        return holder;
    }

    void publishSurvey(String guid, DateTime timestamp) {
        String url = provider.getConfig().getPublishSurveyApi(guid, timestamp);
        HttpResponse response = post(url);

        StatusLine status = response.getStatusLine();
        // There should be error handling before this, probably in the post method
        if (status.getStatusCode() != 200) {
            throw new BridgeSDKException(status.getStatusCode() + ": " + status.getReasonPhrase());
        }
    }
}
