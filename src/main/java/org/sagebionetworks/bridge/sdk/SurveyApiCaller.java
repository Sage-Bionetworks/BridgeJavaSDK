package org.sagebionetworks.bridge.sdk;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.SimpleGuidCreatedOnVersionHolder;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

class SurveyApiCaller extends BaseApiCaller {

    private SurveyApiCaller(Session session) {
        super(session);
    }

    static SurveyApiCaller valueOf(Session session) {
        return new SurveyApiCaller(session);
    }

    List<Survey> getAllVersionsOfAllSurveys() {
        String url = config.getSurveysApi();
        HttpResponse response = get(url);

        JsonNode items = getPropertyFromResponse(response, "items");
        List<Survey> surveys = mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, Survey.class));

        return surveys;
    }

    List<Survey> getPublishedVersionsOfAllSurveys() {
        String url = config.getSurveysPublishedApi();
        HttpResponse response = get(url);

        JsonNode items = getPropertyFromResponse(response, "items");
        List<Survey> surveys = mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, Survey.class));

        return surveys;
    }

    List<Survey> getRecentVersionsOfAllSurveys() {
        String url = config.getRecentSurveysApi();
        HttpResponse response = get(url);

        JsonNode items = getPropertyFromResponse(response, "items");
        List<Survey> surveys = mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, Survey.class));

        return surveys;
    }

    List<Survey> getAllVersionsOfASurvey(String guid) {
        String url = config.getSurveyVersionsApi(guid);
        HttpResponse response = get(url);

        JsonNode items = getPropertyFromResponse(response, "items");
        List<Survey> surveys = mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, Survey.class));

        return surveys;
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

    SimpleGuidCreatedOnVersionHolder createSurvey(Survey survey) {
        String json;
        try {
            json = mapper.writeValueAsString(survey);
        } catch (IOException e) {
            throw new BridgeSDKException("Could not process Survey. Are you sure it is correct? survey=" + survey, e);
        }
        HttpResponse response = post(config.getSurveysApi(), json);

        return getResponseBodyAsType(response, SimpleGuidCreatedOnVersionHolder.class);
    }

    SimpleGuidCreatedOnVersionHolder versionSurvey(String guid, DateTime createdOn) {
        String url = config.getSurveyNewVersionApi(guid, createdOn);
        HttpResponse response = post(url);

        return getResponseBodyAsType(response, SimpleGuidCreatedOnVersionHolder.class);
    }

    SimpleGuidCreatedOnVersionHolder updateSurvey(Survey survey) {
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
