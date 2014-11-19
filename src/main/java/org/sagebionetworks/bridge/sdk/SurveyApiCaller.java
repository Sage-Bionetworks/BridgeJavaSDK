package org.sagebionetworks.bridge.sdk;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.exceptions.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

class SurveyApiCaller extends BaseApiCaller {

    private SurveyApiCaller(Session session) {
        super(session);
    }

    static SurveyApiCaller valueOf(Session session) {
        return new SurveyApiCaller(session);
    }

    ResourceList<Survey> getAllVersionsOfAllSurveys() {
        String url = config.getSurveysApi();
        HttpResponse response = get(url);

        JsonNode node = getJsonNode(response);
        return mapper.convertValue(node, new TypeReference<ResourceListImpl<Survey>>() {});
    }

    ResourceList<Survey> getPublishedVersionsOfAllSurveys() {
        String url = config.getSurveysPublishedApi();
        HttpResponse response = get(url);

        JsonNode node = getJsonNode(response);
        return mapper.convertValue(node, new TypeReference<ResourceListImpl<Survey>>() {});
    }

    ResourceList<Survey> getRecentVersionsOfAllSurveys() {
        String url = config.getRecentSurveysApi();
        HttpResponse response = get(url);

        JsonNode node = getJsonNode(response);
        return mapper.convertValue(node, new TypeReference<ResourceListImpl<Survey>>() {});
    }

    ResourceList<Survey> getAllVersionsOfASurvey(String guid) {
        String url = config.getSurveyVersionsApi(guid);
        HttpResponse response = get(url);

        JsonNode node = getJsonNode(response);
        return mapper.convertValue(node, new TypeReference<ResourceListImpl<Survey>>() {});
    }

    Survey getSurveyForResearcher(String guid, DateTime createdOn) {
        String url = config.getSurveyApi(guid, createdOn);
        HttpResponse response = get(url);

        return getResponseBodyAsType(response, Survey.class);
    }

    Survey getSurveyForUser(String guid, DateTime createdOn) {
        String url = config.getSurveyUserApi(guid, createdOn);
        HttpResponse response = get(url);

        return getResponseBodyAsType(response, Survey.class);
    }

    GuidCreatedOnVersionHolder createSurvey(Survey survey) {
        String json;
        try {
            json = mapper.writeValueAsString(survey);
        } catch (IOException e) {
            throw new BridgeSDKException("Could not process Survey. Are you sure it is correct? survey=" + survey, e);
        }
        HttpResponse response = post(config.getSurveysApi(), json);

        return getResponseBodyAsType(response, SimpleGuidCreatedOnVersionHolder.class);
    }

    GuidCreatedOnVersionHolder versionSurvey(String guid, DateTime createdOn) {
        String url = config.getSurveyNewVersionApi(guid, createdOn);
        HttpResponse response = post(url);

        return getResponseBodyAsType(response, SimpleGuidCreatedOnVersionHolder.class);
    }

    GuidCreatedOnVersionHolder updateSurvey(Survey survey) {
        try {
            String url = config.getSurveyApi(survey.getGuid(), new DateTime(survey.getCreatedOn()));
            HttpResponse response = post(url, mapper.writeValueAsString(survey));

            return getResponseBodyAsType(response, SimpleGuidCreatedOnVersionHolder.class);
        } catch(JsonProcessingException e) {
            throw new BridgeSDKException(e.getMessage(), e);
        }
    }
    
    void publishSurvey(String guid, DateTime createdOn) {
        String url = config.getPublishSurveyApi(guid, createdOn);
        post(url);
    }
    
    void deleteSurvey(String guid, DateTime createdOn) {
        String url = config.getSurveyApi(guid, createdOn);
        delete(url);
    }

    void closeSurvey(String guid, DateTime createdOn) {
        String url = config.getCloseSurveyApi(guid, createdOn);
        post(url);
    }
}
