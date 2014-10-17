package org.sagebionetworks.bridge.sdk;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.GuidVersionedOnHolder;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

import com.fasterxml.jackson.databind.JsonNode;

class SurveyApiCaller extends BaseApiCaller {

    // TODO change api callers so that all api urls come from the config file.

    private SurveyApiCaller(ClientProvider provider) {
        super(provider);
    }

    static SurveyApiCaller valueOf(ClientProvider provider) {
        return new SurveyApiCaller(provider);
    }

    List<Survey> getAllVersionsAllSurveys() {
        assert provider.isSignedIn();

        String url = provider.getConfig().getSurveysApi();
        HttpResponse response = get(url);

        JsonNode items = getPropertyFromResponse(response, "items");
        List<Survey> surveys = mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, Survey.class));

        return surveys;
    }

    List<Survey> getPublishedVersionsAllSurveys() {
        assert provider.isSignedIn();

        String url = provider.getConfig().getSurveysPublishedApi();
        HttpResponse response = get(url);

        JsonNode items = getPropertyFromResponse(response, "items");
        List<Survey> surveys = mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, Survey.class));

        return surveys;
    }

    List<Survey> getRecentVersionsAllSurveys() {
        assert provider.isSignedIn();

        String url = provider.getConfig().getRecentSurveysApi();
        HttpResponse response = get(url);

        JsonNode items = getPropertyFromResponse(response, "items");
        List<Survey> surveys = mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, Survey.class));

        return surveys;
    }

    List<Survey> getAllVersionsForSurvey(String guid) {
        String url = provider.getConfig().getSurveyVersionsApi(guid);
        HttpResponse response = get(url);

        JsonNode items = getPropertyFromResponse(response, "items");
        List<Survey> surveys = mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, Survey.class));

        return surveys;
    }

    Survey getSurvey(String guid, DateTime timestamp) {
        String url = provider.getConfig().getSurveyApi(guid, timestamp);
        HttpResponse response = get(url);

        return getResponseBodyAsType(response, Survey.class);
    }

    GuidVersionedOnHolder createNewSurvey(Survey survey) {
        String json;
        try {
            json = mapper.writeValueAsString(survey);
        } catch (IOException e) {
            throw new BridgeSDKException("Could not process Survey. Are you sure it is correct? survey=" + survey, e);
        }
        HttpResponse response = post(provider.getConfig().getSurveysApi(), json);

        return getResponseBodyAsType(response, GuidVersionedOnHolder.class);
    }

    GuidVersionedOnHolder createNewVersionForSurvey(String guid, DateTime timestamp) {
        String url = provider.getConfig().getSurveyNewVersionApi(guid, timestamp);
        HttpResponse response = post(url);

        return getResponseBodyAsType(response, GuidVersionedOnHolder.class);
    }

    GuidVersionedOnHolder updateSurvey(String guid, DateTime timestamp) {
        String url = provider.getConfig().getSurveyApi(guid, timestamp);
        HttpResponse response = post(url);

        return getResponseBodyAsType(response, GuidVersionedOnHolder.class);
    }

    void publishSurvey(String guid, DateTime timestamp) {
        String url = provider.getConfig().getPublishSurveyApi(guid, timestamp);
        post(url);
    }

    void closeSurvey(String guid, DateTime timestamp) {
        String url = provider.getConfig().getCloseSurveyApi(guid, timestamp);
        post(url);
    }
}
