package org.sagebionetworks.bridge.sdk;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.GuidHolder;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyAnswer;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyResponse;

class SurveyResponseApiCaller extends BaseApiCaller {

    private SurveyResponseApiCaller(ClientProvider provider) {
        super(provider);
    }

    static SurveyResponseApiCaller valueOf(ClientProvider provider) {
        return new SurveyResponseApiCaller(provider);
    }

    Survey getSurvey(String guid, DateTime timestamp) {
        String url = provider.getConfig().getSurveyUserApi(guid, timestamp);
        HttpResponse response = get(url);

        return getResponseBodyAsType(response, Survey.class);
    }

    SurveyResponse getSurveyResponse(String guid) {
        String url = provider.getConfig().getSurveyResponseApi(guid);
        HttpResponse response = get(url);

        return getResponseBodyAsType(response, SurveyResponse.class);
    }

    GuidHolder submitAnswers(List<SurveyAnswer> answers, String guid, DateTime timestamp) {
        String json;
        try {
            json = mapper.writeValueAsString(answers);
        } catch (IOException e) {
            throw new BridgeSDKException("Could not process List<SurveyAnswer>. Are you sure it is correct? answers="
                    + answers, e);
        }

        String url = provider.getConfig().getSurveyApi(guid, timestamp);
        HttpResponse response = post(url, json);

        return getResponseBodyAsType(response, GuidHolder.class);
    }

    void addAnswersToSurveyResponse(List<SurveyAnswer> answers, String guid) {
        String json;
        try {
            json = mapper.writeValueAsString(answers);
        } catch (IOException e) {
            throw new BridgeSDKException("Could not process List<SurveyAnswer>. Are you sure it is correct? answers="
                    + answers, e);
        }

        String url = provider.getConfig().getSurveyResponseApi(guid);
        post(url, json);
    }

    void  deleteSurveyResponse(String guid) {
        String url = provider.getConfig().getSurveyResponseApi(guid);
        delete(url);
    }

}
