package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.apache.http.HttpResponse;
import org.sagebionetworks.bridge.sdk.models.Tracker;

import com.fasterxml.jackson.databind.JsonNode;

class TrackerApiCaller extends BaseApiCaller {

    private TrackerApiCaller(Session session) {
        super(session);
    }

    static TrackerApiCaller valueOf(Session session) {
        return new TrackerApiCaller(session);
    }

    List<Tracker> getAllTrackers() {
        String url = config.getTrackerApi();
        HttpResponse response = get(url);
        JsonNode items = getPropertyFromResponse(response, "items");
        List<Tracker> trackers = mapper.convertValue(items,
                mapper.getTypeFactory().constructCollectionType(List.class, Tracker.class));

        return trackers;
    }

    String getTrackerSchema(Tracker tracker) {
        String url = tracker.getSchemaUrl();
        HttpResponse response = get(url);
        String schema = getResponseBody(response);

        return schema;
    }

}
