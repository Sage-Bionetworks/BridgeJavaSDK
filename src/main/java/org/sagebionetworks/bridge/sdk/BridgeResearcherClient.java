package org.sagebionetworks.bridge.sdk;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.joda.time.DateTime;
import org.sagebionetworks.bridge.sdk.models.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.surveys.Survey;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BridgeResearcherClient extends BaseApiCaller {

    private static final ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.setSerializationInclusion(Include.NON_NULL);
    }

    private BridgeResearcherClient(ClientProvider provider) {
        super(provider);
    }

    static BridgeResearcherClient valueOf(ClientProvider provider) {
        return new BridgeResearcherClient(provider);
    }

    public Survey getSurvey(String guid, DateTime timestamp) {
        Survey survey = null;
        try {
            String url = String.format("/researchers/v1/surveys/%s/%s", guid, timestamp.toString());
            HttpResponse response = authorizedGet(url);

            StatusLine status = response.getStatusLine();
            HttpEntity entity = response.getEntity();

            // There should be error handling before this, probably in the post method
            if (status.getStatusCode() != 200) {
                throw new RuntimeException(status.getStatusCode() + ": " + status.getReasonPhrase());
            }
            survey = mapper.readValue(entity.getContent(), Survey.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return survey;
    }

    public GuidVersionHolder createSurvey(Survey survey) {
        GuidVersionHolder holder = null;
        try {
            String json = mapper.writeValueAsString(survey);
            // Version is not in the place he's expecting at this point, it'll break.
            HttpResponse response = post("/researchers/v1/surveys", json);

            StatusLine status = response.getStatusLine();
            HttpEntity entity = response.getEntity();

            // There should be error handling before this, probably in the post method
            if (status.getStatusCode() != 200) {
                throw new RuntimeException(status.getStatusCode() + ": " + status.getReasonPhrase());
            }
            JsonNode node = mapper.readTree(entity.getContent());
            String guid = node.get("guid").asText();
            String versionedOn = node.get("versionedOn").asText();
            holder = new GuidVersionHolder(guid, DateTime.parse(versionedOn));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return holder;
    }

    public void publishSurvey(String guid, DateTime timestamp) {
        String url = String.format("/researchers/v1/surveys/%s/%s/publish", guid, timestamp.toString());
        HttpResponse response = post(url);

        StatusLine status = response.getStatusLine();
        // There should be error handling before this, probably in the post method
        if (status.getStatusCode() != 200) {
            throw new RuntimeException(status.getStatusCode() + ": " + status.getReasonPhrase());
        }
    }

}
