package org.sagebionetworks.bridge.sdk;

import org.apache.http.HttpResponse;
import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.studies.Tracker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

class TrackerApiCaller extends BaseApiCaller {

    private TrackerApiCaller(Session session) {
        super(session);
    }

    static TrackerApiCaller valueOf(Session session) {
        return new TrackerApiCaller(session);
    }

    ResourceList<Tracker> getAllTrackers() {
        String url = config.getTrackerApi();
        HttpResponse response = get(url);
        
        JsonNode node = getJsonNode(response);
        return mapper.convertValue(node, new TypeReference<ResourceListImpl<Tracker>>() {});
    }

    String getTrackerSchema(Tracker tracker) {
        String url = tracker.getSchemaUrl();
        HttpResponse response = get(url);
        String schema = getResponseBody(response);

        return schema;
    }

}
