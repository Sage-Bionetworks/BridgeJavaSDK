package org.sagebionetworks.bridge.sdk;

import org.apache.http.HttpResponse;
import org.sagebionetworks.bridge.sdk.exceptions.BridgeSDKException;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.VersionHolder;
import org.sagebionetworks.bridge.sdk.models.studies.Study;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

public class StudyApiCaller extends BaseApiCaller {

    private StudyApiCaller(Session session) {
        super(session);
    }

    static StudyApiCaller valueOf(Session session) {
        return new StudyApiCaller(session);
    }
    
    Study getStudyForResearcher() {
        String url = config.getResearcherStudyApi();
        HttpResponse response = get(url);

        return getResponseBodyAsType(response, Study.class);
    }
    VersionHolder updateStudyForResearcher(Study study) {
        String json = null;
        try {
            json = mapper.writeValueAsString(study);
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException("Could not process Study: " + study.toString(), e);
        }
        String url = config.getResearcherStudyApi();
        HttpResponse response = post(url, json);

        return getResponseBodyAsType(response, SimpleVersionHolder.class);
    }
    VersionHolder updateStudyForAdmin(Study study) {
        String json = null;
        try {
            json = mapper.writeValueAsString(study);
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException("Could not process Study: " + study.toString(), e);
        }
        String url = config.getAdminStudyApi(study.getIdentifier());
        HttpResponse response = post(url, json);

        return getResponseBodyAsType(response, SimpleVersionHolder.class);
    }
    Study getStudyForAdmin(String identifier) {
        String url = config.getAdminStudyApi(identifier);
        HttpResponse response = get(url);

        return getResponseBodyAsType(response, Study.class);
    }
    
    ResourceList<Study> getAllStudies() {
        HttpResponse response = get(config.getAdminStudiesApi());

        JsonNode node = getJsonNode(response);
        return mapper.convertValue(node, new TypeReference<ResourceListImpl<Study>>() {});
    }
    
    VersionHolder createStudy(Study study) {
        String json = null;
        try {
            json = mapper.writeValueAsString(study);
        } catch (JsonProcessingException e) {
            throw new BridgeSDKException("Could not process Study: " + study.toString(), e);
        }
        String url = config.getAdminStudiesApi();
        HttpResponse response = post(url, json);

        return getResponseBodyAsType(response, SimpleVersionHolder.class);
    }
    
    void deleteStudy(String identifier) {
        String url = config.getAdminStudyApi(identifier);
        delete(url);
    }
    
}
