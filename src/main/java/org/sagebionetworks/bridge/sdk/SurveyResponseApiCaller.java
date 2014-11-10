package org.sagebionetworks.bridge.sdk;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.exceptions.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.models.holders.GuidHolder;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyAnswer;
import org.sagebionetworks.bridge.sdk.models.surveys.SurveyResponse;

class SurveyResponseApiCaller extends BaseApiCaller {

    private SurveyResponseApiCaller(Session session) {
        super(session);
    }

    static SurveyResponseApiCaller valueOf(Session session) {
        return new SurveyResponseApiCaller(session);
    }

    Survey getSurvey(String guid, DateTime timestamp) {
        String url = config.getSurveyUserApi(guid, timestamp);
        HttpResponse response = get(url);

        return getResponseBodyAsType(response, Survey.class);
    }

    SurveyResponse getSurveyResponse(String guid) {
        String url = config.getSurveyResponseApi(guid);
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
        String url = config.getSurveyUserApi(guid, timestamp);
        HttpResponse response = post(url, json);

        return getResponseBodyAsType(response, SimpleGuidHolder.class);
    }

    void addAnswersToSurveyResponse(List<SurveyAnswer> answers, String guid) {
        String json;
        try {
            json = mapper.writeValueAsString(answers);
        } catch (IOException e) {
            throw new BridgeSDKException("Could not process List<SurveyAnswer>. Are you sure it is correct? answers="
                    + answers, e);
        }

        String url = config.getSurveyResponseApi(guid);
        post(url, json);
    }

    void  deleteSurveyResponse(String guid) {
        String url = config.getSurveyResponseApi(guid);
        delete(url);
    }

}
